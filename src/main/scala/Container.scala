import ActorKind.Player

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

    val grid = new Grid(gameAssets.defaultGridSize, Tile.Floor)
    val playerActionStack = new ActionStack[PlayerAction](PlayerAction.None)
    val player = new Player(playerActionStack)
    val editorActionStack = new ActionStack[GridChange](GridChangeNone)
    val editor = new Editor(editorActionStack)

    gameState = new GameState(player, editor)
    gameState.setActiveActor(Player)
    gameState.setLevel(gameAssets.defaultLevel, grid)

    canvas = new Canvas(paintAssets, gameAssets, gameState)
    mainPanel = new MainPanel(canvas, gameAssets, gameState, playerActionStack, editorActionStack)
    mainFrame = new MainFrame(mainPanel, gameAssets.tileIcon(Tile.Box))
  }

  def getMainFrame: MainFrame = mainFrame
}
