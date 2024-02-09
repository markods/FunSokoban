import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


// TODO:
sealed class Command(val name: String,
                     val param: List[CommandParam],
                     val subcommands: List[Command],
                     protected val editor: Editor) {
  def testParams(): Boolean = false

  def apply(): Boolean = false

  def undo(): Unit = ()
}


final class Editor(private val grid: Grid,
                   private val editorPos: GridPosition,
                   /*private val undoStack: ActionStack[Command], TODO*/
                   /*private val commandMap: MutableMap[String, Command] TODO*/) {

  def position: GridPosition = editorPos

  def addTileBandUnchecked(band: TileBandKind, idx: Int, count: Int): Unit = {
    // TODO:
  }

  def removeTileBandUnchecked(band: TileBandKind, idx: Int, count: Int): ArrayBuffer[TileChange] = {
    val result = ArrayBuffer[TileChange]()
    // TODO:
    result
  }

  def checkSetTile(pos: GridPosition, tile: Tile): Boolean = {
    grid.validPosition(pos.i, pos.j)
  }

  def setTileUnchecked(pos: GridPosition, tile: Tile): TileChange = {
    val result = TileChange(pos.i, pos.j, grid.getTile(pos.i, pos.j), tile)
    result
  }

  def swapBoxesAndGoals(): ArrayBuffer[TileChange] = {
    val result = ArrayBuffer[TileChange]()

    grid.foreach((i, j, tile) => {
      tile match {
        case Tile.Box => result.addOne(TileChange(i, j, tile, Tile.Goal))
        case Tile.Goal => result.addOne(TileChange(i, j, tile, Tile.Box))
        case _ =>
      }
    })

    result
  }

  def minimizeWallUnchecked(pos: GridPosition): ArrayBuffer[TileChange] = {
    val result = ArrayBuffer[TileChange]()
    // TODO:
    result
  }

  def filterWallUnchecked(pos: GridPosition, radius: Int): ArrayBuffer[TileChange] = {
    val result = ArrayBuffer[TileChange]()
    // TODO:
    result
  }

  def fractalizeWallUnchecked(pos: GridPosition): ArrayBuffer[TileChange] = {
    val result = ArrayBuffer[TileChange]()
    // TODO:
    result
  }

  // Doesn't check if the level is solvable.
  def validateLevel(): Boolean = {
    val playerPositionList = grid.findTiles(_ == Tile.Player)
    if (playerPositionList.length != 1) {
      return false
    }
    val playerPosition = playerPositionList(0)
    var reachableBoxCount = 0
    var reachableGoalCount = 0
    var reachedOutsideGrid = false
    var unreachableBoxOrGoal = false

    val checkedPositions = mutable.HashSet[GridPosition]()
    val uncheckedPositions = mutable.ArrayDeque[GridPosition]()
    uncheckedPositions.append(playerPosition)

    def appendUncheckedPosition(pos: GridPosition): Unit = {
      if (!checkedPositions.contains(pos)) {
        uncheckedPositions.append(pos)
      }
    }

    @tailrec
    def floodFill(): Unit = {
      if (uncheckedPositions.isEmpty) {
        return
      }

      val position = uncheckedPositions.removeHead()
      if (!grid.validPosition(position.i, position.j)) {
        reachedOutsideGrid = true
        return
      }
      checkedPositions.add(position)

      val tile = grid.getTile(position.i, position.j)
      if (tile.isBox) {
        reachableBoxCount += 1
      }
      if (tile.isGoal) {
        reachableGoalCount += 1
      }
      if (tile.isFloor) {
        appendUncheckedPosition(new GridPosition(position.i - 1, position.j))
        appendUncheckedPosition(new GridPosition(position.i + 1, position.j))
        appendUncheckedPosition(new GridPosition(position.i, position.j - 1))
        appendUncheckedPosition(new GridPosition(position.i, position.j + 1))
        floodFill()
      }
    }

    floodFill()
    if (reachedOutsideGrid || reachableBoxCount != reachableGoalCount) {
      return false
    }

    val focusedPosition = new GridPosition(0, 0)
    grid.foreach((i, j, tile) => {
      focusedPosition.i = i
      focusedPosition.j = j
      val isBoxOrGoal = tile.isBox || tile.isGoal
      if (isBoxOrGoal && !checkedPositions.contains(focusedPosition)) {
        unreachableBoxOrGoal = true
      }
    })
    if (unreachableBoxOrGoal) {
      return false
    }

    true
  }

  def undo(): Boolean = {
    // TODO:
    true
  }

  def redo(): Boolean = {
    // TODO:
    true
  }
}
