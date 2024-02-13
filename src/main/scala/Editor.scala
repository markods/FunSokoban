import scala.collection.mutable

final class Editor(private val undoStack: ActionStack[GridChange]) extends Actor {
  private var editorGrid: Grid = _
  private val editorPos: GridPosition = new GridPosition(0, 0)
  private val predefCommands: mutable.Set[Cmd] = mutable.Set()
  private val userCommands: mutable.Set[Cmd] = mutable.Set()
  private val commandLiteralStack: CmdLiteralStack = new CmdLiteralStack()
  private val keywords: mutable.Set[String] = mutable.Set()
  configure()

  def configure(): Unit = {
    predefCommands.add(CmdAddRowDef())
    predefCommands.add(CmdAddColDef())
    predefCommands.add(CmdRemoveRowDef())
    predefCommands.add(CmdRemoveColDef())
    predefCommands.add(CmdSetTileDef())
    predefCommands.add(CmdInvertBoxGoalDef())
    predefCommands.add(CmdMinimizeWallDef())
    predefCommands.add(CmdFilterWallDef())
    predefCommands.add(CmdFractalizeWallDef())
    predefCommands.add(CmdValidateLevelDef())
    predefCommands.add(CmdUndefDef())
    predefCommands.add(CmdClearDef())

    keywords.add("fn")
    keywords.add("tn")
    keywords.add("Num")
    keywords.add("Pos")
    keywords.add("Tile")
    keywords.add("Ident")
    keywords.add("undef")
    keywords.add("clear")
  }

  def setGrid(grid: Grid): Boolean = {
    editorGrid = grid
    editorPos.i = (grid.size.m - 1) / 2
    editorPos.j = (grid.size.n - 1) / 2
    undoStack.clear()
    true
  }

  def grid: Grid = editorGrid

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
    if (!editorGrid.validPosition(iNext, jNext)) {
      return false
    }

    editorPos.i = iNext
    editorPos.j = jNext
    true
  }

  def undo(): Boolean = {
    val change = undoStack.undoAction()
    change.undo(editorGrid)
  }

  def redo(): Boolean = {
    val change = undoStack.redoAction()
    change.applyChange(editorGrid)
  }

  def applyChange(change: GridChange): Boolean = {
    if (change == GridChangeUnit) {
      return true
    }
    if (!change.applyChange(editorGrid)) {
      return false
    }

    // If we removed rows/cols, we may need to reposition the editor.
    if (position.i >= grid.size.m) {
      position.i = grid.size.m - 1
    }
    if (position.j >= grid.size.n) {
      position.j = grid.size.n - 1
    }

    undoStack.addAction(change)
    true
  }

  def isKeyword(name: String): Boolean = keywords.contains(name)

  def cmdLiteralStack: CmdLiteralStack = commandLiteralStack

  def applyCmd(command: Cmd): Boolean = {
    val change = command.getGridChange(this)
    if (change == GridChangeUnit) {
      return true
    }
    applyChange(change)
  }

  def addUserCmdDef(command: Cmd): Boolean = {
    command match {
      case definition: CmdDef => // keep command definitions
      case _ => return false
    }
    if (predefCommands.contains(command)) {
      return false
    }
    userCommands.add(command)
  }

  def getCmdDef(name: String): Cmd = {
    val predefCmdDef = predefCommands.find(_.name == name)
    if (predefCmdDef.isDefined) {
      return predefCmdDef.get
    }
    val userCmdDef = userCommands.find(_.name == name)
    if (userCmdDef.isDefined) {
      return userCmdDef.get
    }
    CmdNone
  }

  def removeUserCmdDef(name: String): Boolean = {
    val command = userCommands.find(_.name == name)
    if (command.isEmpty) {
      return false
    }
    userCommands.remove(command.get)
  }

  def clearUserCmdDefs(): Unit = {
    userCommands.clear()
  }

  def checkAddTileBand(band: TileBandKind, idx: Int, count: Int): Boolean = {
    count > 0 && editorGrid.checkAddOrRemoveTileBand(band, idx, count)
  }

  def addTileBandUnchecked(band: TileBandKind, idx: Int, count: Int): GridChange = {
    new BandChange(band, idx, count)
  }

  def checkRemoveTileBand(band: TileBandKind, idx: Int, count: Int): Boolean = {
    count > 0 && editorGrid.checkAddOrRemoveTileBand(band, idx, -count)
  }

  def removeTileBandUnchecked(band: TileBandKind, idx: Int, count: Int): GridChange = {
    new BandChange(band, idx, -count)
  }

  def checkSetTile(pos: GridPosition, tile: Tile): Boolean = {
    editorGrid.validPosition(pos.i, pos.j)
  }

  def setTileUnchecked(pos: GridPosition, tile: Tile): GridChange = {
    val result = new TileChange(pos.i, pos.j, editorGrid.getTile(pos.i, pos.j), tile)
    result
  }

  def invertBoxesAndGoals(): GridChange = {
    val result = new GridChangeList()

    editorGrid.foreach((i, j, tile) => {
      tile match {
        case Tile.Box => result.addOne(new TileChange(i, j, tile, Tile.Goal))
        case Tile.Goal => result.addOne(new TileChange(i, j, tile, Tile.Box))
        case _ =>
      }
    })

    if (!result.isEmpty) result else GridChangeUnit
  }

  def checkMinimizeWall(): Boolean = {
    checkExactlyOnePlayerOnMap()
  }

  def minimizeWallUnchecked(): GridChange = {
    val result = new GridChangeList()

    val playerPositionList = editorGrid.findAll((i, j, tile) => tile.isPlayer)
    val playerPos = playerPositionList(0)

    val ff = editorGrid.floodFillFind(playerPos,
      (i, j, tile) => !tile.isWall,
      /*calculateSurfaceLayer*/ true,
      /*breakOnReachingOutsideGrid*/ true
    )

    val ffOutside = getOuterArea(ff.area, ff.surfaceLayer)
    ffOutside.area.foreach(pos => {
      val tile = editorGrid.getTile(pos.i, pos.j)
      if (tile.isWall) {
        result.addOne(new TileChange(pos.i, pos.j, tile, Tile.Floor))
      }
    })

    if (!result.isEmpty) result else GridChangeUnit
  }

  def checkFilterWall(pos: GridPosition, radius: Int): Boolean = {
    radius > 0 && editorGrid.validPosition(pos.i, pos.j)
  }

  def filterWallUnchecked(pos: GridPosition, radius: Int): GridChange = {
    val result = new GridChangeList()

    val ff = editorGrid.floodFillFind(pos,
      (i, j, tile) => {
        val distX = j - pos.j
        val distY = i - pos.i
        val isInRadius = distY * distY + distX * distX < radius * radius
        if (isInRadius && tile.isWall) {
          result.addOne(new TileChange(i, j, tile, Tile.Floor))
        }
        isInRadius
      },
      /*calculateSurfaceLayer*/ false,
      /*breakOnReachingOutsideGrid*/ false
    )

    if (!result.isEmpty) result else GridChangeUnit
  }

  def checkFractalizeWall(pos: GridPosition, origin: GridPosition): Boolean = {
    if (!editorGrid.validPosition(origin.i, origin.j) || !editorGrid.validPosition(pos.i, pos.j)) {
      return false
    }

    // Check if the tile-to-be-fractalized is a wall.
    if (!editorGrid.getTile(pos.i, pos.j).isWall) {
      return false
    }

    true
  }

  // Only adds a wall if it increases the play area.
  def fractalizeWallUnchecked(pos: GridPosition, origin: GridPosition): GridChange = {
    val result = new GridChangeList()
    val gridSize = editorGrid.size

    // May have holes/reach end of grid.
    val ffOrigin = editorGrid.floodFillFind(origin,
      (i, j, tile) => (i == pos.i && j == pos.j) || !tile.isWall,
      /*calculateSurfaceLayer*/ true,
      /*breakOnReachingOutsideGrid*/ false
    )
    val ffOuter = getOuterArea(ffOrigin.area, ffOrigin.surfaceLayer)

    // Possibly need to extend play area.
    if (pos.i == 0) {
      result.addOne(addTileBandUnchecked(TileBandKind.Column, 0, 1))
      // We don't actually extend the play area here, so we don't need to update pos.i.
    }
    if (pos.i == gridSize.m - 1) {
      result.addOne(addTileBandUnchecked(TileBandKind.Column, gridSize.m, 1))
    }
    if (pos.j == 0) {
      result.addOne(addTileBandUnchecked(TileBandKind.Column, 0, 1))
      // We don't actually extend the play area here, so we don't need to update pos.j.
    }
    if (pos.j == gridSize.n - 1) {
      result.addOne(addTileBandUnchecked(TileBandKind.Column, gridSize.n, 1))
    }


    def maybeAddWallToResults(pos: GridPosition): Unit = {
      // Add a wall if this position is in one of the newly added rows/columns.
      if (!grid.validPosition(pos.i, pos.j)) {
        result.addOne(new TileChange(pos.i, pos.j, Tile.Floor, Tile.Wall))
        return
      }
      // Add a wall if this position is not inside the origin area, as we shouldn't reduce its size.
      if (ffOuter.area.contains(pos) || ffOuter.surfaceLayer.contains(pos)) {
        result.addOne(new TileChange(pos.i, pos.j, editorGrid.getTile(pos.i, pos.j), Tile.Wall))
      }
    }

    // Plus.
    maybeAddWallToResults(new GridPosition(position.i - 1, position.j))
    maybeAddWallToResults(new GridPosition(position.i + 1, position.j))
    maybeAddWallToResults(new GridPosition(position.i, position.j - 1))
    maybeAddWallToResults(new GridPosition(position.i, position.j + 1))
    // Diagonals.
    maybeAddWallToResults(new GridPosition(position.i - 1, position.j - 1))
    maybeAddWallToResults(new GridPosition(position.i + 1, position.j + 1))
    maybeAddWallToResults(new GridPosition(position.i + 1, position.j - 1))
    maybeAddWallToResults(new GridPosition(position.i - 1, position.j + 1))


    if (!result.isEmpty) result else GridChangeUnit
  }

  // Doesn't check if the level is solvable.
  def validateLevel(): Boolean = {
    // Check if there is only one player on the map.
    val playerPositionList = editorGrid.findAll((i, j, tile) => tile.isPlayer)
    if (playerPositionList.length != 1) {
      return false
    }
    val playerPos = playerPositionList(0)

    val ff = editorGrid.floodFillFind(playerPos,
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
    val playerPositionList = editorGrid.findAll((i, j, tile) => tile.isPlayer)
    if (playerPositionList.length != 1) {
      return false
    }

    true
  }

  private def checkSameNumberOfReachableBoxesAndGoals(playerReachableArea: mutable.HashSet[GridPosition]): Boolean = {
    var reachableBoxCount = 0
    var reachableGoalCount = 0
    for (pos <- playerReachableArea) {
      val tile = editorGrid.getTile(pos.i, pos.j)
      if (tile.isBox) reachableBoxCount += 1
      if (tile.isGoal) reachableGoalCount += 1
    }
    reachableBoxCount == reachableGoalCount
  }

  private def checkPlayAreaOuterLayerSolidWall(playerReachableArea: mutable.HashSet[GridPosition],
                                               outOfReachSurfaceLayer: mutable.HashSet[GridPosition]): Boolean = {
    val ffOuter = getOuterArea(playerReachableArea, outOfReachSurfaceLayer)
    val isSolid = ffOuter.surfaceLayer.forall(pos => editorGrid.getTile(pos.i, pos.j).isWall)
    isSolid
  }

  private def getOuterArea(originArea: mutable.HashSet[GridPosition],
                           originSurfaceLayer: mutable.HashSet[GridPosition]
                          ): FloodFillResult = {
    val tempPos = new GridPosition(0, 0)
    val firstFloorOutsidePosOption = editorGrid.findFirst((i, j, tile) => {
      tempPos.i = i
      tempPos.j = j
      tile.isFloor && !originArea.contains(tempPos)
    })
    if (firstFloorOutsidePosOption.isEmpty) {
      return new FloodFillResult()
    }

    // There may be circular walls inside the play area, cutting off access to an inner area.
    // We only want to check the play area outer wall, thus we have to flood fill from outside the play area
    // and check the intersection of that outside area to the out of reach surface layer.
    val ffOutside = editorGrid.floodFillFind(firstFloorOutsidePosOption.get,
      (i, j, tile) => {
        tempPos.i = i
        tempPos.j = j
        !originArea.contains(tempPos)
      },
      /*calculateSurfaceLayer*/ false,
      /*breakOnReachingOutsideGrid*/ false
    )

    val result = new FloodFillResult()
    result.surfaceLayer.addAll(ffOutside.area.intersect(originSurfaceLayer))
    result.area.addAll(ffOutside.area.diff(result.surfaceLayer))
    result.reachedOutsideGrid = ffOutside.reachedOutsideGrid

    result
  }

  private def checkAllBoxesAndGoalsReachable(playerReachableArea: mutable.HashSet[GridPosition]): Boolean = {
    val tempPos = new GridPosition(0, 0)
    val allBoxesAndGoalsReachable = editorGrid.forall((i, j, tile) => {
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
