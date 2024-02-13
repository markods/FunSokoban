import java.util.logging.{Level, Logger}
import scala.collection.mutable.ArrayBuffer

private object GridSerializer {
  private val logger = Logger.getLogger(getClass.getName)
}

final class GridSerializer extends Serializer[Grid] {
  override def toString(grid: Grid): String = {
    val stringBuilder = StringBuilder()
    val gridSize = grid.size
    grid.foreach((i, j, tile) => {
      val tile = grid.getTile(i, j)
      val symbol = TileSymbol.toSymbol(tile)
      stringBuilder.append(symbol)
      stringBuilder.append(if (j < gridSize.n - 1) " " else "\n")
    })

    stringBuilder.mkString
  }

  override def toObject(str: String): Option[Grid] = {
    val tiles = ArrayBuffer[Tile]()
    val gs = new GridSize(0, -1)

    try {
      str.lines.forEach(line => {
        var tilesOnLine = 0

        for (char <- line.filterNot(_.isWhitespace)) {
          tiles.addOne(TileSymbol.toTile(char))
          tilesOnLine += 1
        }

        if (tilesOnLine > 0) {
          if (gs.n == -1) {
            gs.n = tilesOnLine
          }
          else if (tilesOnLine != gs.n) {
            throw new IllegalArgumentException("Line doesn't have the appropriate number of tiles")
          }
          gs.m += 1
        }
      })
    } catch {
      case ex: Exception =>
        GridSerializer.logger.log(Level.INFO, "", ex)
        return Option.empty
    }

    if (gs.n == -1) {
      return Option.empty
    }

    val grid = new Grid(gs, tiles, Tile.Floor)
    Option(grid)
  }
}
