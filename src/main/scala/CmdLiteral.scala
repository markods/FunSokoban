sealed abstract class CmdLiteral(val kind: ValueKind)

case object NoneLiteral extends CmdLiteral(ValueKind.None)

final case class NumLiteral(value: Int) extends CmdLiteral(ValueKind.Num)

final case class PosLiteral(value: GridPosition) extends CmdLiteral(ValueKind.Pos)

final case class TileLiteral(value: Tile) extends CmdLiteral(ValueKind.Tile)

final case class IdentLiteral(value: String) extends CmdLiteral(ValueKind.Ident) {
  // >= - this is a parameter from the outer method call
  // -1 - this is a string literal
  var idxInOuterFrame: Int = -1

  def isOuterParameter: Boolean = idxInOuterFrame >= 0

  def isStringLiteral: Boolean = idxInOuterFrame == -1
}

