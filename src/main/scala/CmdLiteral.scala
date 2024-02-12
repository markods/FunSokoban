sealed trait CmdLiteral(kind: ValueKind)

case object NoneLiteral extends CmdLiteral(ValueKind.None)

final case class NumLiteral(value: Int) extends CmdLiteral(ValueKind.Num)

final case class PosLiteral(value: GridPosition) extends CmdLiteral(ValueKind.Pos)

final case class TileLiteral(value: Tile) extends CmdLiteral(ValueKind.Tile)

final case class IdentLiteral(value: String) extends CmdLiteral(ValueKind.Ident) {
  var outerFrameIdx: Int = -1
}

