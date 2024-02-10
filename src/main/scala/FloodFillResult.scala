import scala.collection.mutable

final class FloodFillResult {
  val area: mutable.HashSet[GridPosition] = new mutable.HashSet[GridPosition]()
  val surfaceLayer: mutable.HashSet[GridPosition] = new mutable.HashSet[GridPosition]()
  var reachedOutsideGrid: Boolean = false
}
