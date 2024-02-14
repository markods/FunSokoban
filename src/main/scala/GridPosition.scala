final class GridPosition(var i: Int, var j: Int) {

  override def hashCode(): Int = 41 * i.hashCode() + j.hashCode()

  override def equals(obj: Any): Boolean = {
    obj match {
      case null => false
      case other: GridPosition =>
        this.i == other.i && this.j == other.j
      case _ => false
    }
  }

  override def toString: String = s"GridPosition($i,$j)"
}
