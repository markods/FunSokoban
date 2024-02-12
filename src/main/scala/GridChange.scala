import scala.collection.mutable

sealed trait GridChange {
  def apply(grid: Grid): Boolean

  def undo(grid: Grid): Boolean
}

object GridChangeNone extends GridChange {
  def apply(grid: Grid): Boolean = false

  def undo(grid: Grid): Boolean = false
}

object GridChangeUnit extends GridChange {
  def apply(grid: Grid): Boolean = true

  def undo(grid: Grid): Boolean = true
}


final class GridChangeList extends GridChange {
  private val gridChange = mutable.ArrayBuffer[GridChange]()

  def isEmpty: Boolean = gridChange.isEmpty

  def addOne(change: GridChange): Unit = {
    gridChange.addOne(change)
  }

  def addAll(changes: GridChangeList): Unit = {
    gridChange.addAll(changes.gridChange.filter(change => change != GridChangeNone && change != GridChangeUnit))
  }

  def apply(grid: Grid): Boolean = {
    for (change <- gridChange) {
      change.apply(grid)
    }
    true
  }

  def undo(grid: Grid): Boolean = {
    for (change <- gridChange) {
      change.undo(grid)
    }
    true
  }
}

final class BandChange(private val band: TileBandKind,
                       private val idx: Int,
                       private val count: Int) extends GridChange {
  private val removedTiles = new GridChangeList()

  private def saveToBeRemovedTiles(grid: Grid): Unit = {
    if (count >= 0) {
      // Save is necessary only when the 'apply' action is 'remove bands'.
      return
    }

    // All bounds inclusive.
    // Row: (idx, 0) -> (idx+count, n-1)
    // Col: (0, idx) -> (m-1, idx+count)
    val upBound = band.unitVectorY * idx
    val downBound = if (band == TileBandKind.Row) idx + count else grid.size.m - 1
    val leftBound = band.unitVectorX * idx
    val rightBound = if (band == TileBandKind.Column) idx + count else grid.size.n - 1

    for (i <- upBound to downBound; j <- leftBound to rightBound) {
      val oldTile = grid.getTile(i, j)
      val tileChange = new TileChange(i, j, oldTile, Tile.Floor)
      removedTiles.addOne(tileChange)
    }
  }

  def apply(grid: Grid): Boolean = {
    if (count < 0 && removedTiles.isEmpty) {
      // Only initialize once. The 'apply' action should be 'remove bands'.
      saveToBeRemovedTiles(grid)
    }
    grid.addOrRemoveTileBandUnchecked(band, idx, count)
    true
  }

  def undo(grid: Grid): Boolean = {
    grid.addOrRemoveTileBandUnchecked(band, idx, -count)

    if (count < 0) {
      // Restore removed tiles, if the 'apply' action is 'remove bands'.
      removedTiles.undo(grid)
    }
    true
  }
}

final class TileChange(private val i: Int,
                       private val j: Int,
                       private val oldTile: Tile,
                       private val newTile: Tile) extends GridChange {
  def apply(grid: Grid): Boolean = {
    grid.setTile(i, j, newTile)
    true
  }

  def undo(grid: Grid): Boolean = {
    grid.setTile(i, j, oldTile)
    true
  }
}
