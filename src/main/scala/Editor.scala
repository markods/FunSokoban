import scala.collection.mutable

final class Editor(private val undoStack: ActionStack[GridChange]) extends Actor {
  private var grid: Grid = _
  private val editorPos: GridPosition = new GridPosition(0, 0)

  def setGrid(grid: Grid): Boolean = {
    this.grid = grid
    editorPos.i = grid.size.m / 2
    editorPos.j = grid.size.n / 2
    undoStack.clear()
    true
  }

  def position: GridPosition = editorPos

  def moveNumber: Int = undoStack.size

  def setPositionUnchecked(i: Int, j: Int): Unit = {
    editorPos.i = i
    editorPos.j = j
  }

  def move(wanted: PlayerAction): Boolean = {
    if (!wanted.isBasicMovement) {
      return false
    }

    val iNext = editorPos.i + wanted.movementY
    val jNext = editorPos.j + wanted.movementX
    if (!grid.validPosition(iNext, jNext)) {
      return false
    }

    editorPos.i = iNext
    editorPos.j = jNext
    true
  }

  def undo(): Boolean = {
    val change = undoStack.undoAction()
    change.undo(grid)
  }

  def redo(): Boolean = {
    val change = undoStack.redoAction()
    change.apply(grid)
  }

  def checkAddTileBand(band: TileBandKind, idx: Int, count: Int): Boolean = {
    count > 0 && grid.checkAddOrRemoveTileBand(band, idx, count)
  }

  def addTileBandUnchecked(band: TileBandKind, idx: Int, count: Int): GridChange = {
    new BandChange(band, idx, count)
  }

  def checkRemoveTileBand(band: TileBandKind, idx: Int, count: Int): Boolean = {
    count > 0 && grid.checkAddOrRemoveTileBand(band, idx, -count)
  }

  def removeTileBandUnchecked(band: TileBandKind, idx: Int, count: Int): GridChange = {
    new BandChange(band, idx, count)
  }

  def checkSetTile(pos: GridPosition, tile: Tile): Boolean = {
    grid.validPosition(pos.i, pos.j)
  }

  def setTileUnchecked(pos: GridPosition, tile: Tile): GridChange = {
    val result = new TileChange(pos.i, pos.j, grid.getTile(pos.i, pos.j), tile)
    result
  }

  def invertBoxesAndGoals(): GridChange = {
    val result = new GridChangeList()

    grid.foreach((i, j, tile) => {
      tile match {
        case Tile.Box => result.addOne(new TileChange(i, j, tile, Tile.Goal))
        case Tile.Goal => result.addOne(new TileChange(i, j, tile, Tile.Box))
        case _ =>
      }
    })

    result
  }

  def checkMinimizeWall(): Boolean = {
    checkExactlyOnePlayerOnMap()
  }

  def minimizeWallUnchecked(): GridChange = {
    val result = new GridChangeList()

    val playerPositionList = grid.findAll((i, j, tile) => tile.isPlayer)
    val playerPos = playerPositionList(0)

    val ff = grid.floodFillFind(playerPos,
      (i, j, tile) => !tile.isWall,
      /*calculateSurfaceLayer*/ true,
      /*breakOnReachingOutsideGrid*/ true
    )

    val ffOutside = getAreaOutsidePlayArea(ff.area, ff.surfaceLayer)
    ffOutside.area.foreach(pos => {
      val tile = grid.getTile(pos.i, pos.j)
      if (tile.isWall) {
        result.addOne(new TileChange(pos.i, pos.j, tile, Tile.Floor))
      }
    })

    result
  }

  def checkFilterWall(pos: GridPosition, radius: Int): Boolean = {
    radius > 0 && grid.validPosition(pos.i, pos.j)
  }

  def filterWallUnchecked(pos: GridPosition, radius: Int): GridChange = {
    val result = new GridChangeList()

    val ff = grid.floodFillFind(pos,
      (i, j, tile) => {
        val distX = j - pos.j
        val distY = i - pos.i
        val isInRadius = distY * distY + distX * distX <= radius * radius
        if (isInRadius && tile.isWall) {
          result.addOne(new TileChange(i, j, tile, Tile.Floor))
        }
        isInRadius
      },
      /*calculateSurfaceLayer*/ false,
      /*breakOnReachingOutsideGrid*/ false
    )

    result
  }

  def checkFractalizeWall(pos: GridPosition, origin: GridPosition): Boolean = {
    if (!grid.validPosition(origin.i, origin.j) || !grid.validPosition(pos.i, pos.j)) {
      return false
    }

    // Check if the tile-to-be-fractalized is a wall.
    if (!grid.getTile(pos.i, pos.j).isWall) {
      return false
    }

    // Check if the origin tile is surrounded by a wall.
    val ff = grid.floodFillFind(origin,
      (i, j, tile) => (i == pos.i && j == pos.j) || !tile.isWall,
      /*calculateSurfaceLayer*/ true,
      /*breakOnReachingOutsideGrid*/ true
    )
    val originReachableAreaIsConfined = !ff.reachedOutsideGrid
    if (!originReachableAreaIsConfined) {
      return false
    }

    true
  }

  def fractalizeWallUnchecked(pos: GridPosition, origin: GridPosition): GridChange = {
    val result = new GridChangeList()
    //    val m = grid.size.m
    //    val n = grid.size.n
    //
    //    // May have holes.
    //    val ffOrigin = grid.floodFillFind(origin,
    //      (i, j, tile) => (i == pos.i && j == pos.j) || !tile.isWall,
    //      /*calculateSurfaceLayer*/ true,
    //      /*breakOnReachingOutsideGrid*/ false
    //    )
    //    val ffOuter = getAreaOutsidePlayArea(ffOrigin.area, ffOrigin.surfaceLayer)
    //    val outerWall = ffOuter.surfaceLayer
    //
    //
    //    // TODO:
    //    def addTileToResults(i: Int, j: Int, newTile: Tile): Unit = {
    //      if (i < grid.size
    //      , j
    //      ) )
    //      {
    //        val band: TileBandKind = TileBandKind.Row
    //        result.addOne(new BandChange(band, bi, bj, 1))
    //        bandChange.apply()
    //
    //      }
    //
    //      result.addOne(new TileChange(i, j, grid.getTile(i, j), newTile))
    //    }
    //
    //    addTileToResults(i, j, Tile.Floor)
    //
    //    val upPos = new GridPosition(pos.i - 1, pos.j - 1)
    //    val downPos = new GridPosition(pos.i - 1, pos.j - 1)
    //    val leftPos = new GridPosition(pos.i - 1, pos.j - 1)
    //    val rightPos = new GridPosition(pos.i - 1, pos.j - 1)
    //
    //
    //    result.apply(grid)
    //
    //
    //    result.undo(grid)
    result
  }

  // Doesn't check if the level is solvable.
  def validateLevel(): Boolean = {
    // Check if there is only one player on the map.
    val playerPositionList = grid.findAll((i, j, tile) => tile.isPlayer)
    if (playerPositionList.length != 1) {
      return false
    }
    val playerPos = playerPositionList(0)

    val ff = grid.floodFillFind(playerPos,
      (i, j, tile) => !tile.isWall,
      /*calculateSurfaceLayer*/ true,
      /*breakOnReachingOutsideGrid*/ true
    )
    val playerReachableAreaIsConfined = !ff.reachedOutsideGrid

    if (!playerReachableAreaIsConfined) {
      return false
    }
    if (!checkSameNumberOfReachableBoxesAndGoals(ff.area)) {
      return false
    }
    // The outer wall should have all corners filled so that it looks nice.
    if (!checkPlayAreaOuterLayerSolidWall(ff.area, ff.surfaceLayer)) {
      return false
    }
    if (!checkAllBoxesAndGoalsReachable(ff.area)) {
      return false
    }

    true
  }


  private def checkExactlyOnePlayerOnMap(): Boolean = {
    val playerPositionList = grid.findAll((i, j, tile) => tile.isPlayer)
    if (playerPositionList.length != 1) {
      return false
    }

    true
  }

  private def checkSameNumberOfReachableBoxesAndGoals(playerReachableArea: mutable.HashSet[GridPosition]): Boolean = {
    var reachableBoxCount = 0
    var reachableGoalCount = 0
    for (pos <- playerReachableArea) {
      val tile = grid.getTile(pos.i, pos.j)
      if (tile.isBox) reachableBoxCount += 1
      if (tile.isGoal) reachableGoalCount += 1
    }
    reachableBoxCount == reachableGoalCount
  }

  private def checkPlayAreaOuterLayerSolidWall(playerReachableArea: mutable.HashSet[GridPosition],
                                               outOfReachSurfaceLayer: mutable.HashSet[GridPosition]): Boolean = {
    val ffOuter = getAreaOutsidePlayArea(playerReachableArea, outOfReachSurfaceLayer)
    val isSolid = ffOuter.surfaceLayer.forall(pos => grid.getTile(pos.i, pos.j).isWall)
    isSolid
  }

  private def getAreaOutsidePlayArea(playerReachableArea: mutable.HashSet[GridPosition],
                                     outOfReachSurfaceLayer: mutable.HashSet[GridPosition]
                                    ): FloodFillResult = {
    val tempPos = new GridPosition(0, 0)
    val firstFloorOutsidePosOption = grid.findFirst((i, j, tile) => {
      tempPos.i = i
      tempPos.j = j
      tile.isFloor && !playerReachableArea.contains(tempPos)
    })
    if (firstFloorOutsidePosOption.isEmpty) {
      return new FloodFillResult()
    }

    // There may be circular walls inside the play area, cutting off access to an inner area.
    // We only want to check the play area outer wall, thus we have to flood fill from outside the play area
    // and check the intersection of that outside area to the out of reach surface layer.
    val ffOutside = grid.floodFillFind(firstFloorOutsidePosOption.get,
      (i, j, tile) => {
        tempPos.i = i
        tempPos.j = j
        !playerReachableArea.contains(tempPos)
      },
      /*calculateSurfaceLayer*/ false,
      /*breakOnReachingOutsideGrid*/ false
    )

    val result = new FloodFillResult()
    result.surfaceLayer.addAll(ffOutside.area.intersect(outOfReachSurfaceLayer))
    result.area.addAll(ffOutside.area.diff(result.surfaceLayer))
    result.reachedOutsideGrid = ffOutside.reachedOutsideGrid

    result
  }

  private def checkAllBoxesAndGoalsReachable(playerReachableArea: mutable.HashSet[GridPosition]): Boolean = {
    val tempPos = new GridPosition(0, 0)
    val allBoxesAndGoalsReachable = grid.forall((i, j, tile) => {
      tempPos.i = i
      tempPos.j = j
      val reachableBoxOrGoal = (tile.isBox || tile.isGoal) && playerReachableArea.contains(tempPos)
      reachableBoxOrGoal
    })
    if (!allBoxesAndGoalsReachable) {
      return false
    }

    true
  }
}
