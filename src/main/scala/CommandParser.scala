import scala.collection.mutable
import scala.util.parsing.combinator.*

sealed trait ParserAction

case object NoAction extends ParserAction

case class CallAction(name: String, params: List[Any]) extends ParserAction

case class FnDefineAction(name: String, params: List[(String, CommandParamKind)], commands: List[CallAction]) extends ParserAction

case class TnDefineAction(name: String, params: List[(String, CommandParamKind)], commands: List[CallAction]) extends ParserAction

object CommandParser extends RegexParsers {
  private val predefinedCommands: mutable.Set[String] = mutable.Set() // TODO:
  private val userCommands: mutable.Set[String] = mutable.Set()
  configure()

  private def configure(): Unit = {
    // TODO:
    // predefinedCommands.add()
  }

  private def num: Parser[Int] = """0|[1-9]\d*""".r ^^ (_.toInt)

  private def pos: Parser[GridPosition] = num ~ ":" ~ num ^^ { case i ~ ":" ~ j => GridPosition(i, j) }

  private def tile: Parser[Tile] =
    """floor|wall|box|goal|boxGoal|player|playerGoal""".r ^^ {
      case "floor" => Tile.Floor
      case "wall" => Tile.Wall
      case "box" => Tile.Box
      case "goal" => Tile.Goal
      case "boxGoal" => Tile.BoxGoal
      case "player" => Tile.Player
      case "playerGoal" => Tile.PlayerGoal
    }

  private def ident: Parser[String] = """[a-zA-Z_][a-zA-Z0-9_]*""".r

  // Order is important.
  private def value: Parser[Any] = pos | num | tile | ident

  private def typ: Parser[CommandParamKind] =
    """Num|Tile|Pos|Ident""".r ^^ {
      case "Num" => CommandParamKind.Num
      case "Tile" => CommandParamKind.Tile
      case "Pos" => CommandParamKind.Pos
      case "Ident" => CommandParamKind.Ident
    }

  private def commandCall: Parser[CallAction] = ident ~ "(" ~ repsep(value, ",") ~ ")" ^^ {
    case name ~ "(" ~ values ~ ")" => CallAction(name, values)
  }

  private def defineFunction: Parser[FnDefineAction] = "fn" ~ ident ~ "(" ~ repsep(ident ~ ":" ~ typ, ",") ~ ")" ~ "=" ~ repsep(commandCall, "") ^^ {
    case "fn" ~ name ~ "(" ~ params ~ ")" ~ "=" ~ commands => FnDefineAction(name, params.map { case ident ~ ":" ~ typ => (ident, typ) }, commands)
  }

  private def defineTransaction: Parser[TnDefineAction] = "tn" ~ ident ~ "(" ~ repsep(ident ~ ":" ~ typ, ",") ~ ")" ~ "=" ~ repsep(commandCall, "") ^^ {
    case "tn" ~ name ~ "(" ~ params ~ ")" ~ "=" ~ commands => TnDefineAction(name, params.map { case ident ~ ":" ~ typ => (ident, typ) }, commands)
  }

  // Order is important.
  private def language: Parser[ParserAction] = defineFunction | defineTransaction | commandCall

  def parse(input: String): ParserAction = parseAll(language, input) match {
    case Success(result, _) => result
    case failure: NoSuccess => NoAction
  }
}

object AAA extends App {
  private val input0 = """aaa(box, pera, 1:3, 12)"""
  private val input1 = """fn ident(param1: Num, param2: Pos) = xoo1(wall, param1) yoo2(param2) zar7(param1, param2, 5:6, pera)"""
  private val input2 = """tn ident(param1: Num, param2: Pos) = xoo1(wall, param1) yoo2(param2) zar7(param1, param2, 5:6, pera)"""

  println(CommandParser.parse(input0))
  println(CommandParser.parse(input1))
  println(CommandParser.parse(input2))
}
