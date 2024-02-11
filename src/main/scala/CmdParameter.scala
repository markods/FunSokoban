sealed trait CmdParameter(kind: ValueKind)

final case class NumParameter(name: String) extends CmdParameter(ValueKind.Num)

final case class PosParameter(name: String) extends CmdParameter(ValueKind.Pos)

final case class TileParameter(name: String) extends CmdParameter(ValueKind.Tile)

final case class IdentParameter(name: String) extends CmdParameter(ValueKind.Ident)
