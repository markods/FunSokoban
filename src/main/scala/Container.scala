final class Container {
  private var mainFrame: MainFrame = _
  private var mainPanel: MainPanel = _
  private var canvas: Canvas = _
  private var paintAssets: PaintAssets = _
  private var gameAssets: GameAssets = _
  private var gameState: GameState = _

  def configure(): Unit = {
    paintAssets = new PaintAssets
    gameAssets = new GameAssets

    // TODO
    val grid = new Grid(new GridSize(10, 12))
    for (i <- 0 until grid.size.m; j <- 0 until grid.size.n) {
      if (Math.random >= 0.2) {
        grid.setTile(i, j, Tile.FloorInside)
      } else {
        val index = (Math.random * Tile.values.length).toInt
        val tile = Tile.values.find(_.idx == index).get
        grid.setTile(i, j, tile)
      }
    }
    val playerPosition = new GridPosition(5, 6)
    grid.setTile(playerPosition.i, playerPosition.j, Tile.Player)

    val playerActionStack = new ActionStack[PlayerAction](PlayerAction.None)
    val player = new Player(grid, playerPosition, playerActionStack)
    gameState = new GameState(grid, player)

    canvas = new Canvas(paintAssets, gameAssets, gameState)
    mainPanel = new MainPanel(canvas, gameAssets, gameState)
    mainFrame = new MainFrame(mainPanel, gameAssets.tileIcon(Tile.Box))
  }

  def getMainFrame: MainFrame = mainFrame
}
