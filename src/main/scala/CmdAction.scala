sealed trait CmdAction {
  def valid(editor: Editor): Boolean

  def apply(editor: Editor): GridChange
}

case object NoAction extends CmdAction {
  def valid(editor: Editor): Boolean = false

  def apply(editor: Editor): GridChange = GridChangeNone
}

// TODO: editor action
sealed case class CmdCall(name: String, literals: List[CmdLiteral]) extends CmdAction {
  def valid(editor: Editor): Boolean = true

  def apply(editor: Editor): GridChange = GridChangeNone
}

sealed case class CmdDef(name: String, params: List[CmdParameter], commands: List[CmdCall], isTransaction: Boolean) extends CmdAction {
  def valid(editor: Editor): Boolean = true

  def apply(editor: Editor): GridChange = GridChangeNone
}

sealed abstract class CmdPredef(name: String, params: List[CmdParameter]) extends CmdAction

final class ExtendRow extends CmdPredef("extendRow", List(NumParameter("iBetween"), NumParameter("count"))) {
  def valid(editor: Editor): Boolean = {
    false
  }

  def apply(editor: Editor): GridChange = {
    GridChangeNone
  }
}

//final class ExtendCol extends CmdPredef("extendCol", List(NumParameter("iBetween"), NumParameter("count"))) {}
//
//final class DeleteRow extends CmdPredef("deleteRow", List(NumParameter("i"), NumParameter("n"))) {}
//
//final class DeleteCol extends CmdPredef("deleteCol", List(NumParameter("i"), NumParameter("n"))) {}
//
//final class SetTile extends CmdPredef("setTile", List(TileParameter("t"), PosParameter("pos"))) {}
//
//final class InvertBoxGoal extends CmdPredef("invertBoxGoal", List()) {}
//
//final class MinimizeWall extends CmdPredef("minimizeWall", List(PosParameter("pos"))) {}
//
//final class FilterWall extends CmdPredef("filterWall", List(PosParameter("pos"), NumParameter("n"))) {}
//
//final class FractalizeWall extends CmdPredef("fractalizeWall", List(PosParameter("pos"))) {}
//
//final class ValidateLevel extends CmdPredef("validateLevel", List()) {}
//
//final class Undef extends CmdPredef("undef", List(IdentParameter("name"))) {}
