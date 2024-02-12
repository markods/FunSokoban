import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

class CmdLiteralStack {
  private val stack: ArrayBuffer[List[CmdLiteral]] = ArrayBuffer()

  private def currFrame(i: Int): List[CmdLiteral] = {
    if (i < 0 || i >= stack.size) {
      return List()
    }
    stack(i)
  }

  def pushFrame(literals: List[CmdLiteral]): Unit = {
    stack.addOne(literals)
  }

  def popFrame(): Boolean = {
    if (stack.isEmpty) {
      return false
    }
    stack.remove(stack.size - 1)
    stack.trimToSize()
    true
  }

  def getLiteral(i: Int): CmdLiteral = {
    @tailrec
    def getLiteralTailrec(frameIdx: Int, i: Int): CmdLiteral = {
      val frame = currFrame(frameIdx)
      if (i < 0 || i >= frame.size) {
        return NoneLiteral
      }

      val literal = frame(i)
      literal match {
        case ident: IdentLiteral => getLiteralTailrec(frameIdx - 1, ident.outerFrameIdx)
        case _ => literal
      }
    }

    getLiteralTailrec(stack.size - 1, i)
  }
}
