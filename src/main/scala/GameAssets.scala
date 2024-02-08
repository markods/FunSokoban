import java.awt.Dimension
import java.util.logging.{Level, Logger}
import javax.swing.ImageIcon

object GameAssets {
  private val logger = Logger.getLogger(getClass.getName)
}

final class GameAssets {
  private val tilePath = new Array[String](Tile.values.length)
  private val tileIconMap = new Array[ImageIcon](Tile.values.length)
  private val tileDimension = new Dimension(0, 0)
  configure()

  def configure(): Unit = {
    tilePath(Tile.FloorOutside.idx) = "/floor_outside.png"
    tilePath(Tile.FloorInside.idx) = "/floor_inside.png"
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

  def tileDim: Dimension = tileDimension

}