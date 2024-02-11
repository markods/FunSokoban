import java.awt.Dimension
import java.nio.file.Paths
import java.util.logging.{Level, Logger}
import javax.swing.ImageIcon

private object GameAssets {
  private val logger = Logger.getLogger(getClass.getName)
}

final class GameAssets {
  private val tilePath = new Array[String](Tile.values.length)
  private val tileIconMap = new Array[ImageIcon](Tile.values.length)
  val tileDim = new Dimension(0, 0)
  val initialCommandHistory = "// Keys\n// ------------------------\n// Ctrl+z, Ctrl+y - undo, redo\n// enter - input command\n// arrow keys - move selection\n\n\n// Language\n// ------------------------\n// Parameters:\n// - num: number\n// - tile: can be \"floor\", \"wall\", \"box\", \"goal\", \"player\", \"boxOnGoal\", \"playerOnGoal\".\n// - pos: position x:y.\n// - ident: identifier.\n// - type: can be Num, Pos, Tile, Ident.\n\nextendRow(iBetween: Num, count: Num)\nextendCol(jBetween: Num, count: Num)\ndeleteRow(i: Num, count: Num)\ndeleteCol(i: Num, count: Num)\nsetTile(pos: Pos, tile: Tile)\n\ninvertBoxGoal()\nminimizeWall()\nfilterWall(pos: Pos, circumference: Num)\nfractalizeWall(pos: Pos, origin: Pos)\nvalidateLevel()\nundef(name: Ident)\n\n// Define a function. Commands are executed in order until one fails, considered success if at least one succeeds.\nfn foo(pos1: Pos, pos2: Pos, n: Num) = tile_set(\"wall\", pos1) wall_fractalize(pos1) wall_filter(pos2, n)\n\n// Define a transaction. Commands are executed in order and either all succeed or none do.\ntn bar(t, Tile, pos1: Pos) = tile_set(t, pos1) wall_fractalize(pos1) level_validate()\n\n\n// Commands\n// ------------------"
  val rootPath = "./src/main"
  val mapsRootPath = "./src/main/maps"
  private val defaultLevelName = "----- | level | -----"
  val defaultLevel: GameFile = GameFile(defaultLevelName, null)
  val zeroTimeString = "00:00:00"
  val defaultGridSize = new GridSize(10, 12)
  val defaultPlayerActionWrap = 80
  configure()

  def configure(): Unit = {
    tilePath(Tile.Floor.idx) = "/floor.png"
    tilePath(Tile.Wall.idx) = "/wall.png"
    tilePath(Tile.Box.idx) = "/box.png"
    tilePath(Tile.Goal.idx) = "/goal.png"
    tilePath(Tile.BoxGoal.idx) = "/box_goal.png"
    tilePath(Tile.Player.idx) = "/player.png"
    tilePath(Tile.PlayerGoal.idx) = "/player_goal.png"

    try {
      for (i <- tileIconMap.indices) {
        val tilePath = this.tilePath(i)
        tileIconMap(i) = new ImageIcon(getClass.getResource(tilePath))
      }
    } catch {
      case ex: NullPointerException =>
        GameAssets.logger.log(Level.SEVERE, "Not all icons have been loaded.", ex)
        throw new RuntimeException("Could not start up program.")
    }

    for (i <- tileIconMap.indices) {
      val currIconWidth = tileIconMap(i).getIconWidth
      val currIconHeight = tileIconMap(i).getIconHeight

      if (i == 0) {
        tileDim.width = currIconWidth
        tileDim.height = currIconHeight
      } else if (currIconWidth != tileDim.width || currIconHeight != tileDim.height) {
        val ex = new RuntimeException("Could not start up program.")
        GameAssets.logger.log(Level.SEVERE, "Not all icons have the same dimension.", ex)
        throw ex
      }
    }
  }


  def tileIcon(tile: Tile): ImageIcon = tileIconMap(tile.idx)
}