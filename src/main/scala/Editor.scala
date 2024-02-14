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
    val success = change.undo(editorGrid)
    clipEditorPosition()
    success
  }

  def redo(): Boolean = {
    val change = undoStack.redoAction()
    val success = change.applyChange(editorGrid)
    clipEditorPosition()
    success
  }

  def applyChange(change: GridChange): Boolean = {
    if (change == GridChangeUnit) {
      return true
    }
    if (!change.applyChange(editorGrid)) {
      return false
    }
    clipEditorPosition()

    undoStack.addAction(change)
    true
  }

  private def clipEditorPosition(): Unit = {
    // If we removed rows/cols, we may need to reposition the editor.
    if (position.i >= grid.size.m) {
      position.i = grid.size.m - 1
    }
    else if (position.i < 0) {
      position.i = 0
    }

    if (position.j >= grid.size.n) {
      position.j = grid.size.n - 1
    }
    else if (position.j < 0) {
      position.j = 0
    }
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
      /*breakOnReachingOutsideGrid*/ false
    )

    val ffOutside = getOutsideArea(ff.area, ff.surfaceLayer, /*calculateSurfaceLayer*/ false)
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
  // TODO: add a parameter for fractalization radius. Use flood fill from the pos, then set the difference of that+surface with the origin area+surface to walls. Remove origin - pos is the origin for flood fill. Update the command history initial string.
  def fractalizeWallUnchecked(wallPos: GridPosition, origin: GridPosition): GridChange = {
    val result = new GridChangeList()
    val gridSize = editorGrid.size

    // May have holes/reach end of grid.
    val ffOrigin = editorGrid.floodFillFind(origin,
      (i, j, tile) => !tile.isWall,
      /*calculateSurfaceLayer*/ true,
      /*breakOnReachingOutsideGrid*/ false
    )
    val ffOutside = getOutsideArea(ffOrigin.area, ffOrigin.surfaceLayer, /*calculateSurfaceLayer*/ true)

    // Possibly need to extend grid. Don't actually extend it though.
    // Order is important.
    var iInc = 0
    var jInc = 0
    if (wallPos.i == gridSize.m - 1) {
      result.addOne(addTileBandUnchecked(TileBandKind.Row, gridSize.m, 1))
    }
    if (wallPos.i == 0) {
      result.addOne(addTileBandUnchecked(TileBandKind.Row, 0, 1))
      iInc = 1
    }
    if (wallPos.j == gridSize.n - 1) {
      result.addOne(addTileBandUnchecked(TileBandKind.Column, gridSize.n, 1))
    }
    if (wallPos.j == 0) {
      result.addOne(addTileBandUnchecked(TileBandKind.Column, 0, 1))
      jInc = 1
    }

    def maybeAddWallToResults(pos: GridPosition): Unit = {
      // Add a wall if this position is in one of the newly added rows/columns.
      if (!grid.validPosition(pos.i, pos.j)) {
        result.addOne(new TileChange(pos.i + iInc, pos.j + jInc, Tile.Floor, Tile.Wall))
        return
      }
      // Don't set a wall to the newly added floor tile.
      if (pos.i == wallPos.i && pos.j == wallPos.j) {
        return
      }
      // Add a wall if this position is not inside the origin area+surface, as we shouldn't reduce its size.
      if (ffOutside.area.contains(pos) || ffOutside.surfaceLayer.contains(pos)) {
        // +1 if we added a row/column.
        result.addOne(new TileChange(pos.i + iInc, pos.j + jInc, editorGrid.getTile(pos.i, pos.j), Tile.Wall))
      }
    }

    // Dot.
    result.addOne(new TileChange(wallPos.i + iInc, wallPos.j + jInc, editorGrid.getTile(wallPos.i, wallPos.j), Tile.Floor))
    // Plus.
    maybeAddWallToResults(new GridPosition(wallPos.i - 1, wallPos.j))
    maybeAddWallToResults(new GridPosition(wallPos.i + 1, wallPos.j))
    maybeAddWallToResults(new GridPosition(wallPos.i, wallPos.j - 1))
    maybeAddWallToResults(new GridPosition(wallPos.i, wallPos.j + 1))
    // Diagonals.
    maybeAddWallToResults(new GridPosition(wallPos.i - 1, wallPos.j - 1))
    maybeAddWallToResults(new GridPosition(wallPos.i + 1, wallPos.j + 1))
    maybeAddWallToResults(new GridPosition(wallPos.i + 1, wallPos.j - 1))
    maybeAddWallToResults(new GridPosition(wallPos.i - 1, wallPos.j + 1))

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
    if (!checkNonZeroSameNumberOfReachableBoxesAndGoals(ff.area)) {
      return false
    }
    // The outer wall should have all corners filled so that it looks nice.
    if (!checkPlayAreaSurfaceLayerSolidWall(ff.surfaceLayer)) {
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

  private def checkNonZeroSameNumberOfReachableBoxesAndGoals(playerReachableArea: mutable.HashSet[GridPosition]): Boolean = {
    var reachableBoxCount = 0
    var reachableGoalCount = 0
    for (pos <- playerReachableArea) {
      val tile = editorGrid.getTile(pos.i, pos.j)
      if (tile.isBox) reachableBoxCount += 1
      if (tile.isGoal) reachableGoalCount += 1
    }
    reachableBoxCount > 0 && reachableBoxCount == reachableGoalCount
  }

  private def checkPlayAreaSurfaceLayerSolidWall(surfaceLayer: mutable.HashSet[GridPosition]): Boolean = {
    val isSolid = surfaceLayer.forall(pos => editorGrid.getTile(pos.i, pos.j).isWall)
    isSolid
  }

  private def getOutsideArea(originArea: mutable.HashSet[GridPosition],
                             originSurfaceLayer: mutable.HashSet[GridPosition],
                             calculateSurfaceLayer: Boolean
                            ): FloodFillResult = {
    val change = new GridChangeList()
    // Extend board surface, so that outer flood fill works.
    // Order is important.
    change.addOne(addTileBandUnchecked(TileBandKind.Row, editorGrid.size.m, 1))
    change.addOne(addTileBandUnchecked(TileBandKind.Column, editorGrid.size.n, 1))
    change.addOne(addTileBandUnchecked(TileBandKind.Row, 0, 1))
    change.addOne(addTileBandUnchecked(TileBandKind.Column, 0, 1))
    change.applyChange(editorGrid)

    // There may be circular walls inside the origin area, cutting off access to inner areas.
    // We have to flood fill from outside the origin area.
    val tempPos = new GridPosition(0, 0)
    val ffOutside = editorGrid.floodFillFind(new GridPosition(0, 0),
      (i, j, tile) => {
        // -1 to map origin grid to current grid.
        tempPos.i = i - 1
        tempPos.j = j - 1
        // Both are needed, in case the surface layer isn't closed.
        !originArea.contains(tempPos) && !originSurfaceLayer.contains(tempPos)
      },
      /*calculateSurfaceLayer*/ calculateSurfaceLayer,
      /*breakOnReachingOutsideGrid*/ false
    )

    change.undo(editorGrid)
    // Remap the positions as if we haven't artificially added the zeroth row and column.
    // Then remove invalid positions.
    ffOutside.area.foreach(pos => {
      pos.i -= 1
      pos.j -= 1
    })
    ffOutside.area = ffOutside.area.filter(pos => editorGrid.validPosition(pos.i, pos.j))

    if (calculateSurfaceLayer) {
      // Same thing for the surface layer.
      ffOutside.surfaceLayer.foreach(pos => {
        pos.i -= 1
        pos.j -= 1
      })
      ffOutside.surfaceLayer = ffOutside.surfaceLayer.filter(pos => editorGrid.validPosition(pos.i, pos.j))
    }

    // It can happen that there is no outside area.
    ffOutside.reachedOutsideGrid = ffOutside.area.nonEmpty

    ffOutside
  }

  private def checkAllBoxesAndGoalsReachable(playerReachableArea: mutable.HashSet[GridPosition]): Boolean = {
    val tempPos = new GridPosition(0, 0)
    val allBoxesAndGoalsReachable = editorGrid.forall((i, j, tile) => {
      tempPos.i = i
      tempPos.j = j
      if (tile == Tile.BoxGoal) {
        true
      } else if ((tile.isBox || tile.isGoal) && !playerReachableArea.contains(tempPos)) {
        false
      } else {
        true
      }
    })
    if (!allBoxesAndGoalsReachable) {
      return false
    }

    true
  }
}
