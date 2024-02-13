sealed abstract class CmdParam(val kind: ValueKind) {
  def name: String
}

case object NoneParam extends CmdParam(ValueKind.None) {
  override def name: String = "noneParam"
}

final case class NumParam(override val name: String) extends CmdParam(ValueKind.Num)

final case class PosParam(override val name: String) extends CmdParam(ValueKind.Pos)

final case class TileParam(override val name: String) extends CmdParam(ValueKind.Tile)

final case class IdentParam(override val name: String) extends CmdParam(ValueKind.Ident)
