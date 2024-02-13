import scala.annotation.tailrec
import scala.collection.mutable

sealed trait Cmd {
  def name: String

  // Empty option message if the semantic analysis succeeds.
  def sema(editor: Editor): String

  def getGridChange(editor: Editor): GridChange
}

case object CmdNone extends Cmd {
  def name: String = "CmdNone"

  def sema(editor: Editor): String = "No command specified"

  def getGridChange(editor: Editor): GridChange = GridChangeNone
}

sealed abstract class CmdDefBase extends Cmd {
  def name: String

  def params: List[CmdParam]

  def commands: List[CmdCallBase]

  def isTransaction: Boolean

  def sema(editor: Editor): String = {
    // Check that another command with the same name doesn't already exist.
    val cmdDef = this
    if (editor.getCmdDef(cmdDef.name) != CmdNone) {
      return s"def.${cmdDef.name}: command ${cmdDef.name} already exists"
    }

    // Check that parameter names are not keywords and are all different.
    var error = ""
    var i = 0
    val cmdDefParamNameToIndex = mutable.HashMap[String, Int]()

    def ensureValidParameter(param: CmdParam): Boolean = {
      if (param == NoneParam) {
        error = s"def.${cmdDef.name}: invalid syntax at parameter.$i"
        return false
      }
      if (editor.isKeyword(param.name)) {
        error = s"def.${cmdDef.name}: parameter ${param.name}.$i is a keyword"
        return false
      }
      if (cmdDefParamNameToIndex.contains(param.name)) {
        error = s"def.${cmdDef.name}: parameter ${param.name}.$i has the same name as a previous parameter"
        return false
      }
      cmdDefParamNameToIndex.addOne((param.name, i))
      i += 1
      true
    }

    cmdDef.params.forall(ensureValidParameter)
    if (!error.isBlank) {
      return error
    }

    // Check that there is at least one command call in the definition.
    if (cmdDef.commands.isEmpty) {
      return s"def.${cmdDef.name}: ${cmdDef.name} must call at least one command"
    }

    // Check that all command calls are valid.
    i = 0
    var j = 0
    val cmdDefUsedParamNames = mutable.HashSet[String]()

    def ensureValidArgumentForCall(call: CmdCallBase, param: CmdParam, literal: CmdLiteral): Boolean = {
      if (literal == NoneLiteral) {
        error = s"call.${call.name}.$i: invalid syntax at position.$j"
        return false
      }
      if (literal.kind == ValueKind.Ident) {
        val identLiteral = literal.asInstanceOf[IdentLiteral]
        val idxInOuterFrame = cmdDefParamNameToIndex.getOrElse(identLiteral.value, -1)
        identLiteral.idxInOuterFrame = idxInOuterFrame
        // If we're using one of the command definition parameters as an argument.
        if (identLiteral.isOuterParameter) {
          val outerParam = cmdDef.params(idxInOuterFrame)
          if (outerParam.kind != param.kind) {
            error = s"call.${call.name}.$i: argument and parameter.$j kinds aren't the same (provided: ${outerParam.kind}, expected: ${param.kind})"
            return false
          }
          cmdDefUsedParamNames.add(outerParam.name)
          return true
        }
      }
      if (literal.kind != param.kind) {
        error = s"call.${call.name}.$i: literal and parameter.$j kinds aren't the same (provided: ${literal.kind}, expected: ${param.kind})"
        return false
      }
      j += 1
      true
    }

    def ensureValidCall(call: CmdCallBase): Boolean = {
      if (call.name == cmdDef.name) {
        error = s"call.${call.name}.$i: recursion isn't supported"
        return false
      }
      if (editor.isKeyword(call.name)) {
        error = s"call.${call.name}.$i: ${call.name} can not be called inside another command"
        return false
      }

      val callDefOrNone = editor.getCmdDef(call.name)
      if (callDefOrNone == CmdNone) {
        error = s"call.${call.name}.$i: no command exists with the name ${call.name}"
        return false
      }

      val callDef = callDefOrNone.asInstanceOf[CmdDefBase]
      callDef.params.zip(call.literals).forall((param, literal) => ensureValidArgumentForCall(call, param, literal))
      if (!error.isBlank) {
        return false
      }

      i += 1
      j = 0
      true
    }

    cmdDef.commands.forall(ensureValidCall)
    if (!error.isBlank) {
      return error
    }

    // Check that all parameters are used.
    i = 0

    def checkAllParamsUsed(param: CmdParam): Boolean = {
      if (!cmdDefUsedParamNames.contains(param.name)) {
        error = s"def.${cmdDef.name}: unused parameter.$i"
        return false
      }
      i += 1
      true
    }

    val allParametersUsed = cmdDef.params.forall(checkAllParamsUsed)
    if (!error.isBlank) {
      return error
    }

    ""
  }

  def getGridChange(editor: Editor): GridChange = {
    editor.addUserCmdDef(this)
    GridChangeUnit
  }
}

sealed abstract class CmdCallBase extends Cmd {
  def name: String

  def literals: List[CmdLiteral]

  def sema(editor: Editor): String = {
    val cmdCall = this

    val cmdDefOrNone = editor.getCmdDef(cmdCall.name)
    if (cmdDefOrNone == CmdNone) {
      return s"call.${cmdCall.name}: no command exists with the name ${cmdCall.name}"
    }

    val cmdDef = cmdDefOrNone.asInstanceOf[CmdDefBase]
    if (cmdDef.params.size != cmdCall.literals.size) {
      return s"call.${cmdCall.name}: the number of parameters (${cmdDef.params.size}) and provided arguments (${cmdCall.literals.size}) don't match"
    }

    var error = ""
    var i = 0

    def ensureValidLiteral(param: CmdParam, literal: CmdLiteral): Boolean = {
      if (literal == NoneLiteral) {
        error = s"call.${cmdCall.name}: invalid syntax at argument.$i"
        return false
      }
      // Command call should only deal with literal values.
      if (literal.kind == ValueKind.Ident && !literal.asInstanceOf[IdentLiteral].isStringLiteral) {
        error = s"call.${cmdCall.name}: unknown argument.$i"
        return false
      }
      if (literal.kind != param.kind) {
        error = s"call.${cmdCall.name}: literal and parameter.$i kinds aren't the same (provided: ${literal.kind}, expected: ${param.kind})"
        return false
      }
      i += 1
      true
    }

    cmdDef.params.zip(literals).forall(ensureValidLiteral)
    if (!error.isBlank) {
      return error
    }

    ""
  }

  final def getGridChange(editor: Editor): GridChange = {
    editor.cmdLiteralStack.pushFrame(literals)
    val change = callCmdGetGridChange(editor)
    editor.cmdLiteralStack.popFrame()
    change
  }

  protected def callCmdGetGridChange(editor: Editor): GridChange = {
    val cmdCall = this

    // Check that the command exists, because we can undefine commands.
    val cmdDefOrNone = editor.getCmdDef(cmdCall.name)
    if (cmdDefOrNone == CmdNone) {
      return GridChangeNone
    }

    val cmdDef = cmdDefOrNone.asInstanceOf[CmdDefBase]
    val isTransaction = cmdDef.isTransaction
    val gridChange = new GridChangeList()
    var nonUnitSuccessCount = 0
    var successCount = 0

    def callSubcommand(call: CmdCallBase): Boolean = {
      val change = call.getGridChange(editor)
      if (change == GridChangeNone) {
        return false
      }
      // Apply change to the grid here. We'll undo all changes and return a single change list from them.
      if (!editor.applyChange(change)) {
        return false
      }
      gridChange.addAll(change)
      if (change != GridChangeUnit) {
        nonUnitSuccessCount += 1
      }
      successCount += 1
      true
    }

    val isSuccess = cmdDef.commands.forall(callSubcommand)
    // Undo all non-unit changes here, so that we can return a single change that encompasses them all.
    editor.undoRedo(-nonUnitSuccessCount)

    // At least one command needs to succeed for a function to succeed.
    if (!isTransaction && successCount == 0) {
      return GridChangeNone
    }
    // All commands need to succeed for a transaction to succeed.
    if (isTransaction && !isSuccess) {
      return GridChangeNone
    }

    if (!gridChange.isEmpty) gridChange else GridChangeUnit
  }
}


//________________________________________________________________________________________________
// Predefined command calls.

sealed case class CmdCall(override val name: String,
                          override val literals: List[CmdLiteral]) extends CmdCallBase


sealed abstract class CmdCallPreDef(override val name: String) extends CmdCallBase {
}

final case class CmdAddRow(override val literals: List[CmdLiteral]) extends CmdCallPreDef("addRow") {
  protected override def callCmdGetGridChange(editor: Editor): GridChange = {
    val iBetween: Int = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[NumLiteral].value
    val count: Int = editor.cmdLiteralStack.getLiteral(1).asInstanceOf[NumLiteral].value
    if (!editor.checkAddTileBand(TileBandKind.Row, iBetween, count)) {
      return GridChangeNone
    }
    val gridChange = editor.addTileBandUnchecked(TileBandKind.Row, iBetween, count)
    gridChange
  }
}

final case class CmdAddCol(override val literals: List[CmdLiteral]) extends CmdCallPreDef("addCol") {
  protected override def callCmdGetGridChange(editor: Editor): GridChange = {
    val iBetween: Int = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[NumLiteral].value
    val count: Int = editor.cmdLiteralStack.getLiteral(1).asInstanceOf[NumLiteral].value
    if (!editor.checkAddTileBand(TileBandKind.Column, iBetween, count)) {
      return GridChangeNone
    }
    val gridChange = editor.addTileBandUnchecked(TileBandKind.Column, iBetween, count)
    gridChange
  }
}

final case class CmdRemoveRow(override val literals: List[CmdLiteral]) extends CmdCallPreDef("removeRow") {
  protected override def callCmdGetGridChange(editor: Editor): GridChange = {
    val iBetween: Int = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[NumLiteral].value
    val count: Int = editor.cmdLiteralStack.getLiteral(1).asInstanceOf[NumLiteral].value
    if (!editor.checkRemoveTileBand(TileBandKind.Row, iBetween, count)) {
      return GridChangeNone
    }
    val gridChange = editor.removeTileBandUnchecked(TileBandKind.Row, iBetween, count)
    gridChange
  }
}

final case class CmdRemoveCol(override val literals: List[CmdLiteral]) extends CmdCallPreDef("removeCol") {
  protected override def callCmdGetGridChange(editor: Editor): GridChange = {
    val iBetween: Int = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[NumLiteral].value
    val count: Int = editor.cmdLiteralStack.getLiteral(1).asInstanceOf[NumLiteral].value
    if (!editor.checkRemoveTileBand(TileBandKind.Column, iBetween, count)) {
      return GridChangeNone
    }
    val gridChange = editor.removeTileBandUnchecked(TileBandKind.Column, iBetween, count)
    gridChange
  }
}

final case class CmdSetTile(override val literals: List[CmdLiteral]) extends CmdCallPreDef("setTile") {
  protected override def callCmdGetGridChange(editor: Editor): GridChange = {
    val pos: GridPosition = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[PosLiteral].value
    val tile: Tile = editor.cmdLiteralStack.getLiteral(1).asInstanceOf[TileLiteral].value
    if (!editor.checkSetTile(pos, tile)) {
      return GridChangeNone
    }
    if (tile == editor.grid.getTile(pos.i, pos.j)) {
      return GridChangeUnit
    }
    val gridChange = editor.setTileUnchecked(pos, tile)
    gridChange
  }
}

final case class CmdInvertBoxGoal(override val literals: List[CmdLiteral]) extends CmdCallPreDef("invertBoxGoal") {
  protected override def callCmdGetGridChange(editor: Editor): GridChange = {
    val gridChange = editor.invertBoxesAndGoals()
    gridChange
  }
}

final case class CmdMinimizeWall(override val literals: List[CmdLiteral]) extends CmdCallPreDef("minimizeWall") {
  protected override def callCmdGetGridChange(editor: Editor): GridChange = {
    if (!editor.checkMinimizeWall()) {
      return GridChangeNone
    }
    val gridChange = editor.minimizeWallUnchecked()
    gridChange
  }
}

final case class CmdFilterWall(override val literals: List[CmdLiteral]) extends CmdCallPreDef("filterWall") {
  protected override def callCmdGetGridChange(editor: Editor): GridChange = {
    val pos: GridPosition = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[PosLiteral].value
    val radius: Int = editor.cmdLiteralStack.getLiteral(1).asInstanceOf[NumLiteral].value
    if (!editor.checkFilterWall(pos, radius)) {
      return GridChangeNone
    }
    val gridChange = editor.filterWallUnchecked(pos, radius)
    gridChange
  }
}

final case class CmdFractalizeWall(override val literals: List[CmdLiteral]) extends CmdCallPreDef("fractalizeWall") {
  protected override def callCmdGetGridChange(editor: Editor): GridChange = {
    val pos: GridPosition = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[PosLiteral].value
    val origin: GridPosition = editor.cmdLiteralStack.getLiteral(1).asInstanceOf[PosLiteral].value
    if (!editor.checkFractalizeWall(pos, origin)) {
      return GridChangeNone
    }
    val gridChange = editor.fractalizeWallUnchecked(pos, origin)
    gridChange
  }
}

final case class CmdValidateLevel(override val literals: List[CmdLiteral]) extends CmdCallPreDef("validateLevel") {
  protected override def callCmdGetGridChange(editor: Editor): GridChange = {
    if (!editor.validateLevel()) {
      return GridChangeNone
    }
    GridChangeUnit
  }
}

final case class CmdUndef(override val literals: List[CmdLiteral]) extends CmdCallPreDef("undef") {
  protected override def callCmdGetGridChange(editor: Editor): GridChange = {
    val ident: String = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[IdentLiteral].value
    if (!editor.removeUserCmdDef(ident)) {
      return GridChangeNone
    }
    GridChangeUnit
  }
}

final case class CmdClear(override val literals: List[CmdLiteral]) extends CmdCallPreDef("clear") {
  protected override def callCmdGetGridChange(editor: Editor): GridChange = {
    editor.clearUserCmdDefs()
    GridChangeUnit
  }
}


//________________________________________________________________________________________________
// Predefined command definitions.

sealed case class CmdDef(override val name: String,
                         override val params: List[CmdParam],
                         override val commands: List[CmdCallBase],
                         override val isTransaction: Boolean) extends CmdDefBase

sealed abstract class CmdPreDef(override val name: String,
                                override val params: List[CmdParam]) extends CmdDefBase {
  override def commands: List[CmdCallBase] = List.empty

  override def isTransaction: Boolean = true
}

final case class CmdAddRowDef() extends CmdPreDef("addRow", List[CmdParam](NumParam("iBetween"), NumParam("count")))

final case class CmdAddColDef() extends CmdPreDef("addCol", List[CmdParam](NumParam("iBetween"), NumParam("count")))

final case class CmdRemoveRowDef() extends CmdPreDef("removeRow", List[CmdParam](NumParam("i"), NumParam("n")))

final case class CmdRemoveColDef() extends CmdPreDef("removeCol", List[CmdParam](NumParam("i"), NumParam("n")))

final case class CmdSetTileDef() extends CmdPreDef("setTile", List[CmdParam](PosParam("pos"), TileParam("t")))

final case class CmdInvertBoxGoalDef() extends CmdPreDef("invertBoxGoal", List[CmdParam]())

final case class CmdMinimizeWallDef() extends CmdPreDef("minimizeWall", List[CmdParam]())

final case class CmdFilterWallDef() extends CmdPreDef("filterWall", List[CmdParam](PosParam("pos"), NumParam("n")))

final case class CmdFractalizeWallDef() extends CmdPreDef("fractalizeWall", List[CmdParam](PosParam("pos")))

final case class CmdValidateLevelDef() extends CmdPreDef("validateLevel", List[CmdParam]())

final case class CmdUndefDef() extends CmdPreDef("undef", List[CmdParam](IdentParam("name")))

final case class CmdClearDef() extends CmdPreDef("clear", List[CmdParam]())
