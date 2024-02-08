import scala.collection.mutable.ArrayBuffer

final class ActionStack[T](private val noneAction: T) {
  private val actionStack: ArrayBuffer[T] = ArrayBuffer[T]()
  // Points after! the last executed action.
  private var iCurrentAction: Int = 0

  def size: Int = iCurrentAction

  def applyAction(action: T): Unit = {
    if (noneAction == action) {
      return
    }
    if (iCurrentAction < actionStack.size) {
      actionStack.dropRightInPlace(actionStack.size - iCurrentAction)
    }
    actionStack.addOne(action)
    iCurrentAction += 1
  }

  def undoAction(): T = {
    if (iCurrentAction == 0) {
      return noneAction
    }
    iCurrentAction -= 1
    val action: T = actionStack(iCurrentAction)
    action
  }

  def redoAction(): T = {
    if (iCurrentAction >= actionStack.size) {
      return noneAction
    }
    val action: T = actionStack(iCurrentAction)
    iCurrentAction += 1
    action
  }

  def clear(): Unit = {
    actionStack.clear()
    iCurrentAction = 0
  }
}
