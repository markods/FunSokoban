import scala.collection.mutable

sealed trait GridChange {
  def applyChange(grid: Grid): Boolean

  def undo(grid: Grid): Boolean
}

object GridChangeNone extends GridChange {
  def applyChange(grid: Grid): Boolean = false

  def undo(grid: Grid): Boolean = false
}

object GridChangeUnit extends GridChange {
  def applyChange(grid: Grid): Boolean = true

  def undo(grid: Grid): Boolean = true
}


final class GridChangeList extends GridChange {
  private val gridChange = mutable.ArrayBuffer[GridChange]()

  def isEmpty: Boolean = gridChange.isEmpty

  def foreach[U](f: GridChange => U): Unit = {
    gridChange.foreach(f)
  }

  def forall(p: GridChange => Boolean): Boolean = {
    gridChange.forall(p)
  }

  def addOne(change: GridChange): Boolean = {
    if (change == GridChangeNone) {
      return false
    }
    if (change == GridChangeUnit) {
      return true
    }
    gridChange.addOne(change)
    true
  }

  def addAll(changes: GridChange): Boolean = {
    val success = changes match {
      case changeList: GridChangeList =>
        var addAllSuccess = true
        changeList.foreach(change => {
          addAllSuccess = addAllSuccess && addOne(change)
        })
        addAllSuccess
      case change: GridChange =>
        addOne(change)
        true
    }
    success
  }

  def applyChange(grid: Grid): Boolean = {
    for (change <- gridChange) {
      change.applyChange(grid)
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

    // Area in original/destination grid in which changes will happen. All bounds inclusive.
    // Original if we're removing bands.  <- WE'RE HERE
    // Destination if we're adding bands.
    // Col: (0, idx) -> (m-1, idx + abs(count)-1)
    // Row: (idx, 0) -> (idx + abs(count)-1, n-1)
    val upBound = band.unitVectorY * idx
    val leftBound = band.unitVectorX * idx
    val downBound = if (band.isRow) idx + Math.abs(count) - 1 else grid.size.m - 1
    val rightBound = if (band.isColumn) idx + Math.abs(count) - 1 else grid.size.n - 1

    for (i <- upBound to downBound; j <- leftBound to rightBound) {
      val oldTile = grid.getTile(i, j)
      val tileChange = new TileChange(i, j, oldTile, Tile.Floor)
      removedTiles.addOne(tileChange)
    }
  }

  def applyChange(grid: Grid): Boolean = {
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
  def applyChange(grid: Grid): Boolean = {
    grid.setTile(i, j, newTile)
    true
  }

  def undo(grid: Grid): Boolean = {
    grid.setTile(i, j, oldTile)
    true
  }
}
