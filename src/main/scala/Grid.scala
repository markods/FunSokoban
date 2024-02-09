import TileBandKind.{Column, Row}

import scala.collection.mutable.ArrayBuffer

final class Grid(private val gs: GridSize,
                 private val defaultTile: Tile) extends Cloneable {
  private val tiles: ArrayBuffer[Tile] = ArrayBuffer.fill(gs.m * gs.n)(defaultTile)

  def size: GridSize = gs

  def validPosition(i: Int, j: Int): Boolean = i >= 0 && i < gs.m && j >= 0 && j < gs.n

  def validAddPosition(i: Int, j: Int): Boolean = i >= 0 && i <= gs.m && j >= 0 && j <= gs.n

  def getTile(i: Int, j: Int): Tile = tiles(gs.n * i + j)

  def setTile(i: Int, j: Int, tile: Tile): Unit = {
    tiles(gs.n * i + j) = tile
  }

  // 1 -> 2 ->
  def rotateTiles(i1: Int, j1: Int, i2: Int, j2: Int): Unit = {
    val tileA: Tile = getTile(i1, j1)
    val tileB: Tile = getTile(i2, j2)
    setTile(i1, j1, tileB)
    setTile(i2, j2, tileA)
  }

  // Right: 1 -> 2 -> 3 ->
  // Left:  1 <- 2 <- 3 <-
  def rotateTiles(i1: Int, j1: Int, i2: Int, j2: Int, i3: Int, j3: Int, linearRotation: LinearRotation): Unit = {
    val tileA: Tile = getTile(i1, j1)
    val tileB: Tile = getTile(i2, j2)
    val tileC: Tile = getTile(i3, j3)
    linearRotation match {
      // Default.
      case LinearRotation.Right => {
        setTile(i1, j1, tileC)
        setTile(i2, j2, tileA)
        setTile(i3, j3, tileB)
      }
      // Inverse.
      case LinearRotation.Left => {
        setTile(i1, j1, tileB)
        setTile(i2, j2, tileC)
        setTile(i3, j3, tileA)
      }
    }
  }

  def findTiles(onSuccess: Tile => Boolean): ArrayBuffer[GridPosition] = {
    val result = ArrayBuffer[GridPosition]()

    foreach((i, j, tile) => {
      if (onSuccess(tile)) {
        result.addOne(GridPosition(i, j))
      }
    })

    result
  }

  def foreach[U](f: (i: Int, j: Int, tile: Tile) => U): Unit = {
    for (i <- 0 until gs.m; j <- 0 until gs.n) {
      f(i, j, getTile(i, j))
    }
  }

  override def clone(): Grid = {
    val gridNew: Grid = new Grid(new GridSize(0, 0), defaultTile)
    gridNew.tiles.addAll(tiles.clone())

    gridNew.gs.m = gs.m
    gridNew.gs.n = gs.n

    gridNew
  }

  // count > 0: idx in [0, axisLen]
  // count < 0: idx in [0, axisLen - 1]
  def validAddOrRemoveTileBand(band: TileBandKind, idx: Int, count: Int): Boolean = {
    val isAdd = count >= 0

    // First outside row/column is a valid location to add more rows/columns.
    val iUp = band.unitVectorY * idx
    val jLeft = band.unitVectorX * idx
    if ((isAdd && !validAddPosition(iUp, jLeft)) || !validPosition(iUp, jLeft)) {
      return false
    }
    if (isAdd) return true

    // Can't remove more rows/colums than there are.
    if ((band == TileBandKind.Row && -count >= gs.m) ||
      (band == TileBandKind.Column && -count >= gs.n)) {
      return false
    }

    true
  }

  // count > 0: idx in [0, axisLen]
  // count < 0: idx in [0, axisLen - 1]
  def addOrRemoveTileBandUnchecked(band: TileBandKind, idx: Int, count: Int): Unit = {
    if (count == 0) {
      return
    }

    val m = gs.m
    val n = gs.n
    val deltaY = band.unitVectorY * count
    val deltaX = band.unitVectorX * count
    val tileDiff = if (band == TileBandKind.Row) count * n else m * count

    // Add tiles if requested.
    if (tileDiff > 0) {
      gs.m += deltaY
      gs.n += deltaX
      appendTiles(tileDiff)

      // No work if we want to insert below/right of the last row/column.
      if ((band == TileBandKind.Row && idx == m) ||
        (band == TileBandKind.Column && idx == n)) {
        return
      }
    }

    // Source moves on the original grid. All bounds are inclusive.
    val srcUpBound = band.unitVectorY * (idx + Math.max(0, -count))
    val srcDownBound = m - 1
    val srcLeftBound = band.unitVectorX * (idx + Math.max(0, -count))
    val srcRightBound = n - 1
    // Add: Right to left, down to up swap to avoid overwriting.
    // Remove: Left to Right, up to down swap to avoid overwriting.
    val iSrcRange = if (tileDiff > 0) srcDownBound to srcUpBound else srcUpBound to srcDownBound
    val jSrcRange = if (tileDiff > 0) srcRightBound to srcLeftBound else srcLeftBound to srcRightBound

    for (iSrc <- iSrcRange; jSrc <- jSrcRange) {
      val iDst = iSrc + deltaY
      val jDst = jSrc + deltaX
      rotateTiles(iDst, jDst, iSrc, jSrc)
    }

    // Remove tiles if requested. Count is negative here.
    if (tileDiff < 0) {
      gs.m += deltaY
      gs.n += deltaX
      dropRightInPlaceTiles(-tileDiff)
    }
  }

  private def appendTiles(count: Int): Unit = {
    for (i <- 0 until count) {
      tiles.addOne(defaultTile)
    }
  }

  private def dropRightInPlaceTiles(count: Int): Unit = {
    tiles.dropRightInPlace(count)
  }

}
