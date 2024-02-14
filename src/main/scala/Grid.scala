import TileBandKind.{Column, Row}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

final class Grid(private val gs: GridSize,
                 private val defaultTile: Tile) {
  private val tiles: ArrayBuffer[Tile] = ArrayBuffer.fill(gs.m * gs.n)(defaultTile)

  def this(gs: GridSize, tiles: IterableOnce[Tile], defaultTile: Tile) = {
    this(new GridSize(0, 0), defaultTile)
    this.tiles.addAll(tiles)
    this.gs.m = gs.m
    this.gs.n = gs.n
  }

  def size: GridSize = gs

  def validPosition(i: Int, j: Int): Boolean = i >= 0 && i < gs.m && j >= 0 && j < gs.n

  def validBandAddPosition(i: Int, j: Int): Boolean = i >= 0 && i <= gs.m && j >= 0 && j <= gs.n

  // Unchecked.
  def getTile(i: Int, j: Int): Tile = tiles(gs.n * i + j)

  def getTile(nij: Int): Tile = tiles(nij)

  // Unchecked.
  def setTile(i: Int, j: Int, tile: Tile): Unit = {
    tiles(gs.n * i + j) = tile
  }

  def setTile(nij: Int, tile: Tile): Unit = {
    tiles(nij) = tile
  }

  // 1 -> 2 ->
  def rotateTiles(i1: Int, j1: Int, i2: Int, j2: Int): Unit = {
    val tileA: Tile = getTile(i1, j1)
    val tileB: Tile = getTile(i2, j2)
    setTile(i1, j1, tileB)
    setTile(i2, j2, tileA)
  }

  def rotateTiles(nij1: Int, nij2: Int): Unit = {
    val tileA: Tile = getTile(nij1)
    val tileB: Tile = getTile(nij2)
    setTile(nij1, tileB)
    setTile(nij2, tileA)
  }

  // Right: 1 -> 2 -> 3 ->
  // Left:  1 <- 2 <- 3 <-
  def rotateTiles(i1: Int, j1: Int, i2: Int, j2: Int, i3: Int, j3: Int, linearRotation: LinearRotation): Unit = {
    val tileA: Tile = getTile(i1, j1)
    val tileB: Tile = getTile(i2, j2)
    val tileC: Tile = getTile(i3, j3)
    linearRotation match {
      // Default.
      case LinearRotation.Right =>
        setTile(i1, j1, tileC)
        setTile(i2, j2, tileA)
        setTile(i3, j3, tileB)
      // Inverse.
      case LinearRotation.Left =>
        setTile(i1, j1, tileB)
        setTile(i2, j2, tileC)
        setTile(i3, j3, tileA)
    }
  }

  def foreach[U](f: (i: Int, j: Int, tile: Tile) => U): Unit = {
    for (i <- 0 until gs.m; j <- 0 until gs.n) {
      f(i, j, getTile(i, j))
    }
  }

  def forall(p: (i: Int, j: Int, tile: Tile) => Boolean): Boolean = {
    var i = 0
    var j = 0
    tiles.forall(tile => {
      val res = p(i, j, getTile(i, j))
      j += 1
      if (j == gs.n) {
        i += 1
        j = 0
      }
      res
    })
  }

  def findFirst(p: (i: Int, j: Int, tile: Tile) => Boolean): Option[GridPosition] = {
    var result: Option[GridPosition] = Option.empty
    forall((i, j, tile) => {
      val found = p(i, j, tile)
      if (found) {
        result = Option(new GridPosition(i, j))
      }
      found
    })

    result
  }

  def findAll(p: (i: Int, j: Int, tile: Tile) => Boolean): ArrayBuffer[GridPosition] = {
    val result = ArrayBuffer[GridPosition]()

    foreach((i, j, tile) => {
      if (p(i, j, tile)) {
        result.addOne(GridPosition(i, j))
      }
    })

    result
  }

  def floodFillFind(pos: GridPosition,
                    onInside: (i: Int, j: Int, tile: Tile) => Boolean,
                    calculateSurfaceLayer: Boolean,
                    breakOnReachingOutsideGrid: Boolean): FloodFillResult = {
    val ff = new FloodFillResult()
    if (!validPosition(pos.i, pos.j)) {
      ff.reachedOutsideGrid = true
      return ff
    }
    val unvisited = mutable.ArrayDeque[GridPosition]()
    unvisited.append(pos)

    def appendUnvisited(position: GridPosition): Unit = {
      if (!validPosition(position.i, position.j)) {
        ff.reachedOutsideGrid = true
      } else if (!ff.area.contains(position)) {
        unvisited.append(position)
      }
    }

    def appendOuterLayer(position: GridPosition): Unit = {
      if (validPosition(position.i, position.j) && !ff.area.contains(position)) {
        ff.surfaceLayer.add(position)
      }
    }

    @tailrec
    def floodFillTailrec(): Unit = {
      if (unvisited.isEmpty) {
        return
      }
      val position = unvisited.removeHead(/*resizeInternalRepr*/ true)
      val tile = getTile(position.i, position.j)

      if (onInside(position.i, position.j, tile)) {
        ff.area.add(position)
        // Plus.
        appendUnvisited(new GridPosition(position.i - 1, position.j))
        appendUnvisited(new GridPosition(position.i + 1, position.j))
        appendUnvisited(new GridPosition(position.i, position.j - 1))
        appendUnvisited(new GridPosition(position.i, position.j + 1))

        // Diagonals. Not necessary if we're just finding the inside area.
        if (calculateSurfaceLayer) {
          appendOuterLayer(new GridPosition(position.i - 1, position.j - 1))
          appendOuterLayer(new GridPosition(position.i + 1, position.j + 1))
          appendOuterLayer(new GridPosition(position.i + 1, position.j - 1))
          appendOuterLayer(new GridPosition(position.i - 1, position.j + 1))
        }
      }
      else if (calculateSurfaceLayer) {
        appendOuterLayer(position)
      }

      if (ff.reachedOutsideGrid && breakOnReachingOutsideGrid) {
        return
      }

      floodFillTailrec()
    }

    floodFillTailrec()
    ff.surfaceLayer = ff.surfaceLayer.diff(ff.area)
    ff
  }

  def copy(): Grid = {
    val gridSize = gs
    val gridNew: Grid = new Grid(new GridSize(gs.m, gs.n), tiles.clone(), defaultTile)
    gridNew
  }

  // count > 0: idx in [0, axisLen]
  // count < 0: idx in [0, axisLen - 1]
  def checkAddOrRemoveTileBand(band: TileBandKind, idx: Int, count: Int): Boolean = {
    // Area in original/destination grid in which changes will happen. All bounds inclusive.
    // Original if we're removing bands.  <- WE'RE HERE, as adding is easier to check.
    // Destination if we're adding bands.
    // Col: (0, idx) -> (m-1, idx + abs(count)-1)
    // Row: (idx, 0) -> (idx + abs(count)-1, n-1)
    val upBound = band.unitVectorY * idx
    val leftBound = band.unitVectorX * idx
    val downBound = if (band.isRow) idx + Math.abs(count) - 1 else gs.m - 1
    val rightBound = if (band.isColumn) idx + Math.abs(count) - 1 else gs.n - 1

    val isAdd = count >= 0
    // Check row/column addition.
    // Note that the first outside row/column is a valid location to add more rows/columns.
    if (isAdd) {
      val isValidAddPosition = validBandAddPosition(upBound, leftBound)
      return isValidAddPosition
    }

    // Check row/column removal.
    if (!validPosition(upBound, leftBound) || !validPosition(downBound, rightBound)) {
      return false
    }
    // Should not remove all rows/columns.
    if ((band.isRow && downBound + 1 - upBound == gs.m) ||
      (band.isColumn && rightBound + 1 - leftBound == gs.n)
    ) {
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

    val gsSrc = new GridSize(gs.m, gs.n)
    val gsDst = new GridSize(gs.m + band.unitVectorY * count, gs.n + band.unitVectorX * count)
    val gsMax = new GridSize(Math.max(gsSrc.m, gsDst.m), Math.max(gsSrc.n, gsDst.n))
    val gsSrcMN = gsSrc.m * gsSrc.n
    val gsDstMN = gsDst.m * gsDst.n
    val tileDiff = if (band.isRow) count * gsSrc.n else gsSrc.m * count

    // Area in original/destination grid in which changes will happen. All bounds inclusive.
    // Original if we're removing bands.
    // Destination if we're adding bands.
    // Col: (0, idx) -> (m-1, idx + abs(count)-1)
    // Row: (idx, 0) -> (idx + abs(count)-1, n-1)
    val upBound = band.unitVectorY * idx
    val leftBound = band.unitVectorX * idx
    val downBound = if (band.isRow) idx + Math.abs(count) - 1 else gsMax.m - 1
    val rightBound = if (band.isColumn) idx + Math.abs(count) - 1 else gsMax.n - 1


    // Add tiles if needed.
    if (tileDiff > 0) {
      appendTiles(tileDiff)
    }

    if (tileDiff < 0) {
      // Remove: source grid. Left to right, up to down movement to avoid overwriting.
      var nijSrc = 0
      var nijDst = 0
      val inc = 1
      val skipInc = inc * (if (band.isRow) gsMax.n - 1 else Math.abs(count) - 1)

      var shouldContinue = true
      while (shouldContinue) {
        val iSrc = nijSrc / gsSrc.n
        val jSrc = nijSrc % gsSrc.n

        if (iSrc < upBound || iSrc > downBound || jSrc < leftBound || jSrc > rightBound) {
          // We're outside the no-swap area in the source grid.
          rotateTiles(nijSrc, nijDst)
          nijDst += inc
        } else {
          // Skip over the no-swap area.
          nijSrc += skipInc
        }

        nijSrc += inc
        shouldContinue = nijSrc >= 0 && nijSrc < gsSrcMN
      }
    }
    else {
      // Add: destination grid. Right to left, down to up movement to avoid overwriting.
      var nijSrc = gsSrc.m * gsSrc.n - 1
      var nijDst = gsDst.m * gsDst.n - 1
      val inc = -1
      val skipInc = inc * (if (band.isRow) gsMax.n - 1 else Math.abs(count) - 1)

      var shouldContinue = true
      while (shouldContinue) {
        val iDst = nijDst / gsDst.n
        val jDst = nijDst % gsDst.n

        if (iDst < upBound || iDst > downBound || jDst < leftBound || jDst > rightBound) {
          // We're outside the no-swap area in the destination grid.
          rotateTiles(nijSrc, nijDst)
          nijSrc += inc
        } else {
          // Skip over the no-swap area.
          nijDst += skipInc
        }

        nijDst += inc
        shouldContinue = nijDst >= 0 && nijDst < gsDstMN
      }
    }

    // Remove tiles if needed.
    if (tileDiff < 0) {
      dropRightInPlaceTiles(-tileDiff)
    }

    // Update the grid size.
    gs.m = gsDst.m
    gs.n = gsDst.n
  }

  private def appendTiles(count: Int): Unit = {
    for (i <- 0 until count) {
      tiles.addOne(defaultTile)
    }
  }

  private def dropRightInPlaceTiles(count: Int): Unit = {
    tiles.dropRightInPlace(count)
    tiles.trimToSize()
  }

}
