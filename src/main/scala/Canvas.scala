import java.awt.{Dimension, Graphics, Graphics2D, Point}
import java.awt.event.{KeyAdapter, KeyEvent}
import javax.swing.JPanel

final class Canvas(private val paintAssets: PaintAssets,
                   private val gameAssets: GameAssets,
                   private val gameState: GameState,
                  ) extends JPanel {

  private def calculateGridOrigin(gridOriginOut: Point, pa: PaintAssets, canvasDim: Dimension, gridSize: GridSize, focusedPosition: GridPosition): Unit = {
    val tileDim = gameAssets.tileDim
    val gridWidth = gridSize.n * tileDim.width
    val gridHeight = gridSize.m * tileDim.height
    val canvasWidth = canvasDim.width
    val canvasHeight = canvasDim.height

    // Center the grid in the canvas.
    gridOriginOut.x = pa.gridMarginThickness + (canvasWidth - pa.gridMarginThickness - gridWidth) / 2
    gridOriginOut.y = pa.gridMarginThickness + (canvasHeight - pa.gridMarginThickness - gridHeight) / 2

    // The grid is bigger than the intended part of the canvas.
    // Keep the player in a central area and clip further outside that area.
    val playerLeftX = gridOriginOut.x + focusedPosition.j * tileDim.width
    val playerRightX = playerLeftX + tileDim.width

    val boundsLeftX = pa.gridMarginThickness + canvasWidth * pa.recenterPlayerThreshold / 100
    val boundsRightX = canvasWidth - canvasWidth * pa.recenterPlayerThreshold / 100

    gridOriginOut.x += Math.max(boundsLeftX - playerLeftX, 0) - Math.max(playerRightX - boundsRightX, 0)

    val playerUpY = gridOriginOut.y + focusedPosition.i * tileDim.height
    val playerDownY = playerUpY + tileDim.height

    val boundsUpY = pa.gridMarginThickness + canvasHeight * pa.recenterPlayerThreshold / 100
    val boundsDownY = canvasHeight - canvasHeight * pa.recenterPlayerThreshold / 100

    gridOriginOut.y += Math.max(boundsUpY - playerUpY, 0) - Math.max(playerDownY - boundsDownY, 0)
  }

  private def clearCanvas(g: Graphics2D): Unit = {
    val canvasWidth = this.getWidth
    val canvasHeight = this.getHeight

    g.setColor(Palette.GridBack.color)
    g.fillRect(0, 0, canvasWidth, canvasHeight)
  }

  private def drawGridLines(g: Graphics2D, gridOrigin: Point, gridSize: GridSize): Unit = {
    val tileDim = gameAssets.tileDim
    val gridWidth = gridSize.n * tileDim.width
    val gridHeight = gridSize.m * tileDim.height

    g.setColor(Palette.TileLine.color)
    g.setStroke(paintAssets.mediumStroke)

    for (i <- 0 to gridSize.m; j <- 0 to gridSize.n) {
      val tileX = gridOrigin.x + j * tileDim.width
      val tileY = gridOrigin.y + i * tileDim.height

      g.drawLine(tileX, gridOrigin.y, tileX, gridOrigin.y + gridHeight)
      g.drawLine(gridOrigin.x, tileY, gridOrigin.x + gridWidth, tileY)
    }
  }

  private def drawTiles(g: Graphics2D, gridOrigin: Point, gridSize: GridSize): Unit = {
    val tileDim = gameAssets.tileDim

    for (i <- 0 until gridSize.m; j <- 0 until gridSize.n) {
      val tileX = gridOrigin.x + j * tileDim.width
      val tileY = gridOrigin.y + i * tileDim.height
      val tile = gameState.grid.getTile(i, j)
      val tileImage = gameAssets.tileIcon(tile).getImage

      g.drawImage(tileImage, tileX, tileY, this)
    }
  }

  private def drawGridMargin(g: Graphics2D, canvasDim: Dimension): Unit = {
    val canvasWidth = canvasDim.width
    val canvasHeight = canvasDim.height
    val strokeHalfThickness = (paintAssets.mediumThickness / 2).round

    g.setColor(Palette.GridBack.color)

    g.fillRect(0, 0, canvasWidth, paintAssets.gridMarginThickness - strokeHalfThickness)
    g.fillRect(0, 0, paintAssets.gridMarginThickness - strokeHalfThickness, canvasHeight)
  }

  private def drawFocusFrame(g: Graphics2D, gridOrigin: Point, focusedPosition: GridPosition): Unit = {
    val tileDim = gameAssets.tileDim
    val focusedX = gridOrigin.x + focusedPosition.j * tileDim.width
    val focusedY = gridOrigin.y + focusedPosition.i * tileDim.height
    val strokeHalfThickness = (paintAssets.highThickness / 2).round

    g.setColor(Palette.FocusFrame.color)
    g.setStroke(paintAssets.highStroke)

    g.drawRect(focusedX - strokeHalfThickness, focusedY - strokeHalfThickness, tileDim.width + 2 * strokeHalfThickness, tileDim.height + 2 * strokeHalfThickness)
  }

  private def drawGridText(g: Graphics2D, gridOrigin: Point, gridSize: GridSize): Unit = {
    val tileDim = gameAssets.tileDim
    g.setFont(paintAssets.font)
    for (j <- 0 until gridSize.n) {
      val textX = tileDim.width / 2 + gridOrigin.x + j * tileDim.width
      val textY = paintAssets.textUpMargin

      g.drawString(Integer.toString(j), textX, textY)
    }
    for (i <- 0 until gridSize.m) {
      val textX = paintAssets.textLeftMargin
      val textY = tileDim.height / 2 + gridOrigin.y + i * tileDim.height

      g.drawString(Integer.toString(i), textX, textY)
    }
  }

  override protected def paintComponent(gOld: Graphics): Unit = {
    super.paintComponent(gOld)
    val g = gOld.asInstanceOf[Graphics2D]
    val gClipBounds = g.getClipBounds()
    val canvasDim = new Dimension(gClipBounds.width, gClipBounds.height)
    val gridOrigin = new Point(0, 0)

    gameState.synchronized {
      val gridSize = gameState.grid.size
      val focusedPosition = gameState.player.position

      calculateGridOrigin(gridOrigin, paintAssets, canvasDim, gridSize, focusedPosition)

      clearCanvas(g)
      drawTiles(g, gridOrigin, gridSize)
      drawGridLines(g, gridOrigin, gridSize)
      drawFocusFrame(g, gridOrigin, focusedPosition)
      drawGridMargin(g, canvasDim)
      drawGridText(g, gridOrigin, gridSize)
    }

    g.dispose()
  }
}