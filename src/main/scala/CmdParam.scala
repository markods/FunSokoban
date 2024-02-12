sealed trait CmdParam(kind: ValueKind)

case object NoneParam extends CmdParam(ValueKind.None)

final case class NumParam(name: String) extends CmdParam(ValueKind.Num)

final case class PosParam(name: String) extends CmdParam(ValueKind.Pos)

final case class TileParam(name: String) extends CmdParam(ValueKind.Tile)

final case class IdentParam(name: String) extends CmdParam(ValueKind.Ident)
