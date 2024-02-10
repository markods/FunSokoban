import java.util.logging.{Level, Logger}

private object PlayerActionStackSerializer {
  private val logger = Logger.getLogger(getClass.getName)
}

final class PlayerActionStackSerializer(private val iWrap: Int
                                       ) extends Serializer[ActionStack[PlayerAction]] {
  override def toString(playerActionStack: ActionStack[PlayerAction]): String = {
    val stringBuilder = StringBuilder()
    var i = 0
    playerActionStack.foreach(elem => {
      stringBuilder.append(PlayerActionSymbol.toSymbol(elem))
      if (i == iWrap) {
        i = -1
        stringBuilder.append("\n")
      }
      i = i + 1
    })
    stringBuilder.mkString
  }

  override def toObject(str: String): Option[ActionStack[PlayerAction]] = {
    val playerActionStack = new ActionStack[PlayerAction](PlayerAction.None)

    try {
      str.lines.forEach(line => {
        for (char <- line.filterNot(_.isWhitespace)) {
          playerActionStack.addAction(PlayerActionSymbol.toPlayerAction(char))
        }
      })
    } catch {
      case ex: Exception =>
        PlayerActionStackSerializer.logger.log(Level.INFO, "", ex)
        return Option.empty
    }

    if (playerActionStack.size == 0) {
      return Option.empty
    }

    Option(playerActionStack)
  }
}
