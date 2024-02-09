import scala.annotation.tailrec

final class Player(private val grid: Grid,
                   private val playerPos: GridPosition,
                   private val undoStack: ActionStack[PlayerAction]) {
  def position: GridPosition = playerPos

  def moveNumber: Int = undoStack.size

  def setPositionUnchecked(i: Int, j: Int): Unit = {
    playerPos.i = i
    playerPos.j = j

    undoStack.clear()
  }

  def checkMove(wanted: PlayerAction): PlayerAction = {
    if (!wanted.isBasicMovement) {
      return PlayerAction.None
    }

    val iNext = playerPos.i + wanted.movementY
    val jNext = playerPos.j + wanted.movementX
    if (!grid.validPosition(iNext, jNext)) {
      return PlayerAction.None
    }

    // Only the player moves.
    val tileNext = grid.getTile(iNext, jNext)
    if (tileNext.isWalkable) {
      return wanted
    }
    if (!tileNext.isBox) {
      return PlayerAction.None
    }

    // Player + box move together.
    val iNextNext = iNext + wanted.movementY
    val jNextNext = jNext + wanted.movementX
    if (!grid.validPosition(iNextNext, jNextNext)) {
      return PlayerAction.None
    }

    val tileNextNext = grid.getTile(iNextNext, jNextNext)
    if (!tileNextNext.isWalkable) {
      return PlayerAction.None
    }
    wanted.promoteToBoxAction
  }

  def moveUnchecked(checked: PlayerAction, actionKind: UndoActionKind): Boolean = {
    val tilesToMove = checked.tilesToMove
    if (tilesToMove == 0) {
      return false
    }

    val isUndo = actionKind == UndoActionKind.Undo
    val dY = checked.movementY
    val dX = checked.movementX
    val i = playerPos.i + (if (!isUndo) 0 else -dY)
    val j = playerPos.j + (if (!isUndo) 0 else -dX)
    val iNext = i + dY
    val jNext = j + dX
    val isGoalA = grid.getTile(i, j).isGoal
    val isGoalB = grid.getTile(iNext, jNext).isGoal

    // Update board.
    if (tilesToMove == 1) {
      // player -> walkableTile ->   // DO
      // player <- walkableTile <-   // UNDO (effectively the same as DO for two tiles)
      grid.rotateTiles(i, j, iNext, jNext)
      grid.setTile(i, j, grid.getTile(i, j).setGoal(isGoalA))
      grid.setTile(iNext, jNext, grid.getTile(iNext, jNext).setGoal(isGoalB))
    }
    else if (tilesToMove == 2) {
      val iNextNext = iNext + dY
      val jNextNext = jNext + dX
      val isGoalC = grid.getTile(iNextNext, jNextNext).isGoal
      val rotation = if (!isUndo) LinearRotation.Right else LinearRotation.Left

      // player -> box -> walkableTile ->   // DO
      // walkableTile <- player <- box <-   // UNDO
      grid.rotateTiles(i, j, iNext, jNext, iNextNext, jNextNext, rotation)
      grid.setTile(i, j, grid.getTile(i, j).setGoal(isGoalA))
      grid.setTile(iNext, jNext, grid.getTile(iNext, jNext).setGoal(isGoalB))
      grid.setTile(iNextNext, jNextNext, grid.getTile(iNextNext, jNextNext).setGoal(isGoalC))
    }
    else {
      throw new NotImplementedError
    }

    // Update player position.
    playerPos.i += (if (!isUndo) dY else -dY)
    playerPos.j += (if (!isUndo) dX else -dX)
    if (actionKind eq UndoActionKind.Apply) {
      undoStack.applyAction(checked)
    }
    true
  }

  def move(wanted: PlayerAction): Boolean = {
    moveUnchecked(checkMove(wanted), UndoActionKind.Apply)
  }

  def undo(): Boolean = {
    moveUnchecked(undoStack.undoAction(), UndoActionKind.Undo)
  }

  def redo(): Boolean = {
    moveUnchecked(undoStack.redoAction(), UndoActionKind.Redo)
  }

  // - for undo, + for redo
  def undoRedo(moveNumber: Int): Int = {
    @tailrec
    def undoRedoTail(moveNumber: Int, completedMoves: Int): Int = {
      if (moveNumber < 0 && undo()) {
        return undoRedoTail(moveNumber + 1, completedMoves + 1)
      }
      else if (moveNumber > 0 && redo()) {
        return undoRedoTail(moveNumber - 1, completedMoves + 1)
      }
      completedMoves
    }

    undoRedoTail(moveNumber, 0)
  }
}
