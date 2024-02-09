//final class PlayerActionStackSerializer extends Serializer[ActionStack[PlayerAction]] {
// private val iWrap = 10
//
// override def toString(playerActionStack: ActionStack[PlayerAction]): String = {
//   val stringBuilder = StringBuilder()
//   var i = 0
//   playerActionStack.foreach(elem => {
//     stringBuilder.append(elem.toString)
//     if (i == iWrap) {
//       i = -1
//       stringBuilder.append("\n")
//     }
//     i = i + 1
//   })
//   stringBuilder.mkString
// }
//
// override def toObject(str: String): Option[ActionStack[PlayerAction]] = {
//   str.lines().forEach(line => )
// }
//}
