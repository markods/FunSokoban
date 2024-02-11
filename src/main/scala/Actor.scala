import scala.annotation.tailrec

trait Actor {
  def setGrid(grid: Grid): Boolean

  def grid: Grid

  def position: GridPosition

  def moveNumber: Int

  def setPositionUnchecked(i: Int, j: Int): Unit

  def move(wanted: PlayerAction): Boolean

  def undo(): Boolean

  def redo(): Boolean

  def undoRedo(moveNumber: Int): Int = {
    @tailrec
    def undoRedoTailrec(moveNumber: Int, completedMoves: Int): Int = {
      if (moveNumber < 0 && undo()) {
        return undoRedoTailrec(moveNumber + 1, completedMoves + 1)
      }
      else if (moveNumber > 0 && redo()) {
        return undoRedoTailrec(moveNumber - 1, completedMoves + 1)
      }
      completedMoves
    }

    undoRedoTailrec(moveNumber, 0)
  }

}
