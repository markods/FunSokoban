import scala.util.parsing.combinator.*

// TODO: use fastparse
final class CmdParser(private val gameAssets: GameAssets) extends RegexParsers {
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

  private def param: Parser[CmdParam] = ident ~ ":" ~ """Num|Pos|Tile|Ident""".r ^^ {
    case ident ~ ":" ~ "Num" => NumParam(ident.value)
    case ident ~ ":" ~ "Pos" => PosParam(ident.value)
    case ident ~ ":" ~ "Tile" => TileParam(ident.value)
    case ident ~ ":" ~ "Ident" => IdentParam(ident.value)
  }

  private def callCommand: Parser[CmdCallBase] = ident ~ "(" ~ repsep(value, ",") ~ ")" ^^ {
    case name ~ "(" ~ literals ~ ")" =>
      name.value match {
        case "extendRow" => CmdExtendRow(literals)
        case "extendCol" => CmdExtendCol(literals)
        case "deleteRow" => CmdDeleteRow(literals)
        case "deleteCol" => CmdDeleteCol(literals)
        case "setTile" => CmdSetTile(literals)
        case "invertBoxGoal" => CmdInvertBoxGoal(literals)
        case "minimizeWall" => CmdMinimizeWall(literals)
        case "filterWall" => CmdFilterWall(literals)
        case "fractalizeWall" => CmdFractalizeWall(literals)
        case "validateLevel" => CmdValidateLevel(literals)
        case "undef" => CmdUndef(literals)
        case "clear" => CmdClear(literals)
        case _ => CmdCall(name.value, literals)
      }
  }

  // TODO: account for whitespace between fn/tn and identifier
  private def defineCommand: Parser[CmdDefBase] = "fn|tn".r ~ ident ~ "(" ~ repsep(param, ",") ~ ")" ~ "=" ~ repsep(callCommand, "") ^^ {
    case "fn" ~ name ~ "(" ~ params ~ ")" ~ "=" ~ callCommands => CmdDef(name.value, params, callCommands, false)
    case "tn" ~ name ~ "(" ~ params ~ ")" ~ "=" ~ callCommands => CmdDef(name.value, params, callCommands, true)
  }

  // Order is important.
  private def language: Parser[Cmd] = defineCommand | callCommand

  def parse(input: String): (Cmd, String) = parseAll(language, input) match {
    case Success(result, _) => (result, "")
    case failure: NoSuccess => (CmdNone, failure.msg)
  }
}
