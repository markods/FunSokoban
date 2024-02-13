enum TileBandKind:
  case Row, Column

  def isRow: Boolean = this == Row

  def isColumn: Boolean = this == Column

  def unitVectorY: Int = this match
    case Row => 1
    case _ => 0

  def unitVectorX: Int = this match
    case Column => 1
    case _ => 0
