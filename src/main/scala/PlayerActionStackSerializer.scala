import java.util.logging.{Level, Logger}

private object PlayerActionStackSerializer {
  private val logger = Logger.getLogger(getClass.getName)
}

final class PlayerActionStackSerializer(private val iWrap: Int
                                       ) extends Serializer[ActionStack[PlayerAction]] {
  override def toString(playerActionStack: ActionStack[PlayerAction]): String = {
    val stringBuilder = StringBuilder()
    playerActionStack.foreach((i, elem) => {
      stringBuilder.append(PlayerActionSymbol.toSymbol(elem))
      if ((i + 1) % iWrap == 0) {
        stringBuilder.append("\n")
      }
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
