import scala.collection.mutable

final class FloodFillResult {
  var area: mutable.HashSet[GridPosition] = new mutable.HashSet[GridPosition]()
  var surfaceLayer: mutable.HashSet[GridPosition] = new mutable.HashSet[GridPosition]()
  var reachedOutsideGrid: Boolean = false
}
