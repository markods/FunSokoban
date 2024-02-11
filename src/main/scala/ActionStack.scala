import scala.collection.mutable.ArrayBuffer

final class ActionStack[T](private val noneAction: T) {
  private val actionStack: ArrayBuffer[T] = ArrayBuffer[T]()
  // Points after! the last executed action.
  private var iNextAction: Int = 0

  def size: Int = iNextAction

  def foreach[U](f: (i: Int, t: T) => U): Unit = {
    for (i <- 0 until iNextAction) {
      f(i, actionStack(i))
    }
  }

  def forall(p: (i: Int, t: T) => Boolean): Boolean = {
    var i = 0
    var shouldContinue = false
    while (shouldContinue && i < iNextAction) {
      shouldContinue = p(i, actionStack(i))
      i += 1
    }

    shouldContinue
  }


  def addAction(action: T): Boolean = {
    if (noneAction == action) {
      return false
    }
    dropLaterActions()
    actionStack.addOne(action)
    iNextAction += 1
    true
  }

  def undoAction(): T = {
    if (iNextAction == 0) {
      return noneAction
    }
    iNextAction -= 1
    val action: T = actionStack(iNextAction)
    action
  }

  def redoAction(): T = {
    if (iNextAction == actionStack.size) {
      return noneAction
    }
    val action: T = actionStack(iNextAction)
    iNextAction += 1
    action
  }

  def undoAllActions(): Unit = {
    iNextAction = 0
  }

  def redoAllActions(): Unit = {
    iNextAction = actionStack.size
  }

  private def dropLaterActions(): Unit = {
    if (iNextAction < actionStack.size) {
      actionStack.dropRightInPlace(actionStack.size - iNextAction)
    }
  }

  def clear(): Unit = {
    actionStack.clear()
    iNextAction = 0
  }
}
