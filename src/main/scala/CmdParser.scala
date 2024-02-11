import scala.util.parsing.combinator.*

class CmdParser extends RegexParsers {
  private def w: Parser[String] = """\s+""".r

  private def num: Parser[NumLiteral] = """0|[1-9]\d*""".r ^^ (value => NumLiteral(value.toInt))

  private def pos: Parser[PosLiteral] = num ~ ":" ~ num ^^ { case i ~ ":" ~ j => PosLiteral(new GridPosition(i.value, j.value)) }

  // Order is important.
  private def tile: Parser[TileLiteral] =
    """floor|wall|boxGoal|playerGoal|box|goal|player""".r ^^ {
      case "floor" => TileLiteral(Tile.Floor)
      case "wall" => TileLiteral(Tile.Wall)
      case "box" => TileLiteral(Tile.Box)
      case "goal" => TileLiteral(Tile.Goal)
      case "boxGoal" => TileLiteral(Tile.BoxGoal)
      case "player" => TileLiteral(Tile.Player)
      case "playerGoal" => TileLiteral(Tile.PlayerGoal)
    }

  private def ident: Parser[IdentLiteral] = """[a-zA-Z_][a-zA-Z0-9_]*""".r ^^ (value => IdentLiteral(value))

  // Order is important.
  private def value: Parser[CmdLiteral] = pos | num | tile | ident

  private def param: Parser[CmdParameter] = ident ~ ":" ~ """Num|Pos|Tile|Ident""".r ^^ {
    case ident ~ ":" ~ "Num" => NumParameter(ident.value)
    case ident ~ ":" ~ "Pos" => PosParameter(ident.value)
    case ident ~ ":" ~ "Tile" => TileParameter(ident.value)
    case ident ~ ":" ~ "Ident" => IdentParameter(ident.value)
  }

  private def callCommand: Parser[CmdCall] = ident ~ "(" ~ repsep(value, ",") ~ ")" ^^ {
    case name ~ "(" ~ literals ~ ")" => CmdCall(name.value, literals)
  }

  // TODO: account for whitespace between fn/tn and identifier
  private def defineCommand: Parser[CmdDef] = "fn|tn".r ~ ident ~ "(" ~ repsep(param, ",") ~ ")" ~ "=" ~ repsep(callCommand, "") ^^ {
    case "fn" ~ name ~ "(" ~ params ~ ")" ~ "=" ~ commands => CmdDef(name.value, params, commands, false)
    case "tn" ~ name ~ "(" ~ params ~ ")" ~ "=" ~ commands => CmdDef(name.value, params, commands, true)
  }

  // Order is important.
  private def language: Parser[CmdAction] = defineCommand | callCommand

  def parse(input: String): CmdAction = parseAll(language, input) match {
    case Success(result, _) => result
    case failure: NoSuccess => NoAction
  }
}

// TODO: remove
object AAA extends App {
  private val input0 = """aaa(box, pera, 1:3, 12)"""
  private val input1 = """fn ident(param1: Num, param2: Pos) = xoo1(wall, param1) yoo2(param2) zar7(param1, param2, 5:6, pera)"""
  private val input2 = """tn ident(param1: Num, param2: Pos) = xoo1(wall, param1) yoo2(param2) zar7(param1, param2, 5:6, pera)"""

  private val parser = new CmdParser()

  println(parser.parse(input0))
  println(parser.parse(input1))
  println(parser.parse(input2))
}
