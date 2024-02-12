
sealed trait Cmd {
  def name: String

  // Empty option message if the semantic analysis succeeds.
  def sema(editor: Editor): Option[String]

  def apply(editor: Editor): GridChange
}

case object CmdNone extends Cmd {
  def name: String = "CmdNone"

  def sema(editor: Editor): Option[String] = Option("No command specified")

  def apply(editor: Editor): GridChange = GridChangeNone
}

sealed abstract class CmdDefBase(cmdName: String,
                                 params: List[CmdParam],
                                 commands: List[CmdCallBase],
                                 isTransaction: Boolean) extends Cmd {
  def name: String = cmdName

  def sema(editor: Editor): Option[String] = {
    // TODO:
    Option.empty
  }

  def apply(editor: Editor): GridChange = {
    editor.addUserCmdDef(this)
    GridChangeUnit
  }
}

sealed abstract class CmdCallBase(cmdName: String,
                                  literals: List[CmdLiteral]) extends Cmd {
  def name: String = cmdName

  def sema(editor: Editor): Option[String] = {
    // TODO:
    Option.empty
  }

  final def apply(editor: Editor): GridChange = {
    // TODO:
    editor.cmdLiteralStack.pushFrame(literals)
    val change = call(editor)
    editor.cmdLiteralStack.popFrame()
    change
  }

  def call(editor: Editor): GridChange = {
    // TODO:
    val cmdDef = editor.getCmdDef(name)
    GridChangeNone
  }
}


//________________________________________________________________________________________________
// Predefined command calls.

sealed case class CmdCall(override val name: String,
                          literals: List[CmdLiteral]) extends CmdCallBase(name, literals)

final case class CmdExtendRow(literals: List[CmdLiteral]) extends CmdCallBase("extendRow", literals) {
  override def call(editor: Editor): GridChange = {
    val iBetween: Int = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[NumLiteral].value
    val count: Int = editor.cmdLiteralStack.getLiteral(1).asInstanceOf[NumLiteral].value
    if (!editor.checkAddTileBand(TileBandKind.Row, iBetween, count)) {
      return GridChangeNone
    }
    val gridChange = editor.addTileBandUnchecked(TileBandKind.Row, iBetween, count)
    gridChange
  }
}

final case class CmdExtendCol(literals: List[CmdLiteral]) extends CmdCallBase("extendCol", literals) {
  override def call(editor: Editor): GridChange = {
    val iBetween: Int = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[NumLiteral].value
    val count: Int = editor.cmdLiteralStack.getLiteral(1).asInstanceOf[NumLiteral].value
    if (!editor.checkAddTileBand(TileBandKind.Column, iBetween, count)) {
      return GridChangeNone
    }
    val gridChange = editor.addTileBandUnchecked(TileBandKind.Column, iBetween, count)
    gridChange
  }
}

final case class CmdDeleteRow(literals: List[CmdLiteral]) extends CmdCallBase("deleteRow", literals) {
  override def call(editor: Editor): GridChange = {
    val iBetween: Int = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[NumLiteral].value
    val count: Int = editor.cmdLiteralStack.getLiteral(1).asInstanceOf[NumLiteral].value
    if (!editor.checkAddTileBand(TileBandKind.Row, iBetween, -count)) {
      return GridChangeNone
    }
    val gridChange = editor.addTileBandUnchecked(TileBandKind.Row, iBetween, -count)
    gridChange
  }
}

final case class CmdDeleteCol(literals: List[CmdLiteral]) extends CmdCallBase("deleteCol", literals) {
  override def call(editor: Editor): GridChange = {
    val iBetween: Int = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[NumLiteral].value
    val count: Int = editor.cmdLiteralStack.getLiteral(1).asInstanceOf[NumLiteral].value
    if (!editor.checkAddTileBand(TileBandKind.Column, iBetween, -count)) {
      return GridChangeNone
    }
    val gridChange = editor.addTileBandUnchecked(TileBandKind.Column, iBetween, -count)
    gridChange
  }
}

final case class CmdSetTile(literals: List[CmdLiteral]) extends CmdCallBase("setTile", literals) {
  override def call(editor: Editor): GridChange = {
    val pos: GridPosition = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[PosLiteral].value
    val tile: Tile = editor.cmdLiteralStack.getLiteral(1).asInstanceOf[TileLiteral].value
    if (!editor.checkSetTile(pos, tile)) {
      return GridChangeNone
    }
    val gridChange = editor.setTileUnchecked(pos, tile)
    gridChange
  }
}

final case class CmdInvertBoxGoal(literals: List[CmdLiteral]) extends CmdCallBase("invertBoxGoal", literals) {
  override def call(editor: Editor): GridChange = {
    val gridChange = editor.invertBoxesAndGoals()
    gridChange
  }
}

final case class CmdMinimizeWall(literals: List[CmdLiteral]) extends CmdCallBase("minimizeWall", literals) {
  override def call(editor: Editor): GridChange = {
    if (!editor.checkMinimizeWall()) {
      return GridChangeNone
    }
    val gridChange = editor.minimizeWallUnchecked()
    gridChange
  }
}

final case class CmdFilterWall(literals: List[CmdLiteral]) extends CmdCallBase("filterWall", literals) {
  override def call(editor: Editor): GridChange = {
    val pos: GridPosition = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[PosLiteral].value
    val radius: Int = editor.cmdLiteralStack.getLiteral(1).asInstanceOf[NumLiteral].value
    if (!editor.checkFilterWall(pos, radius)) {
      return GridChangeNone
    }
    val gridChange = editor.filterWallUnchecked(pos, radius)
    gridChange
  }
}

final case class CmdFractalizeWall(literals: List[CmdLiteral]) extends CmdCallBase("fractalizeWall", literals) {
  override def call(editor: Editor): GridChange = {
    val pos: GridPosition = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[PosLiteral].value
    val origin: GridPosition = editor.cmdLiteralStack.getLiteral(1).asInstanceOf[PosLiteral].value
    if (!editor.checkFractalizeWall(pos, origin)) {
      return GridChangeNone
    }
    val gridChange = editor.fractalizeWallUnchecked(pos, origin)
    gridChange
  }
}

final case class CmdValidateLevel(literals: List[CmdLiteral]) extends CmdCallBase("validateLevel", literals) {
  override def call(editor: Editor): GridChange = {
    if (!editor.validateLevel()) {
      return GridChangeNone
    }
    GridChangeUnit
  }
}

final case class CmdUndef(literals: List[CmdLiteral]) extends CmdCallBase("undef", literals) {
  override def call(editor: Editor): GridChange = {
    val ident: String = editor.cmdLiteralStack.getLiteral(0).asInstanceOf[IdentLiteral].value
    if (!editor.removeUserCmdDef(ident)) {
      return GridChangeNone
    }
    GridChangeUnit
  }
}

final case class CmdClear(literals: List[CmdLiteral]) extends CmdCallBase("clear", literals) {
  override def call(editor: Editor): GridChange = {
    editor.clearUserCmdDefs()
    GridChangeUnit
  }
}


//________________________________________________________________________________________________
// Predefined command definitions.

sealed case class CmdDef(override val name: String,
                         params: List[CmdParam],
                         commands: List[CmdCallBase],
                         isTransaction: Boolean) extends CmdDefBase(name, params, commands, isTransaction)

sealed abstract class CmdPreDef(name: String, params: List[CmdParam]) extends CmdDefBase(name, params, List(), true)

final case class CmdExtendRowDef() extends CmdPreDef("extendRow", List[CmdParam](NumParam("iBetween"), NumParam("count")))

final case class CmdExtendColDef() extends CmdPreDef("extendCol", List[CmdParam](NumParam("iBetween"), NumParam("count")))

final case class CmdDeleteRowDef() extends CmdPreDef("deleteRow", List[CmdParam](NumParam("i"), NumParam("n")))

final case class CmdDeleteColDef() extends CmdPreDef("deleteCol", List[CmdParam](NumParam("i"), NumParam("n")))

final case class CmdSetTileDef() extends CmdPreDef("setTile", List[CmdParam](TileParam("t"), PosParam("pos")))

final case class CmdInvertBoxGoalDef() extends CmdPreDef("invertBoxGoal", List[CmdParam]())

final case class CmdMinimizeWallDef() extends CmdPreDef("minimizeWall", List[CmdParam](PosParam("pos")))

final case class CmdFilterWallDef() extends CmdPreDef("filterWall", List[CmdParam](PosParam("pos"), NumParam("n")))

final case class CmdFractalizeWallDef() extends CmdPreDef("fractalizeWall", List[CmdParam](PosParam("pos")))

final case class CmdValidateLevelDef() extends CmdPreDef("validateLevel", List[CmdParam]())

final case class CmdUndefDef() extends CmdPreDef("undef", List[CmdParam](IdentParam("name")))

final case class CmdClearDef() extends CmdPreDef("clear", List[CmdParam]())
