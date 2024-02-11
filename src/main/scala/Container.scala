import ActorKind.Player

final class Container {
  private var mainFrame: MainFrame = _
  private var mainPanel: MainPanel = _
  private var canvas: Canvas = _
  private var paintAssets: PaintAssets = _
  private var gameAssets: GameAssets = _

  private var gameState: GameState = _
  private var player: Player = _
  private var editor: Editor = _
  private var playerActionStack: ActionStack[PlayerAction] = _
  private var editorActionStack: ActionStack[GridChange] = _
  private var commandParser: CmdParser = _

  def configure(): Unit = {
    gameAssets = new GameAssets
    val grid = new Grid(gameAssets.defaultGridSize, Tile.Floor)

    playerActionStack = new ActionStack[PlayerAction](PlayerAction.None)
    player = new Player(playerActionStack)
    editorActionStack = new ActionStack[GridChange](GridChangeNone)
    commandParser = new CmdParser()
    editor = new Editor(editorActionStack)

    gameState = new GameState(player, editor)
    gameState.setActiveActor(Player)
    gameState.setLevel(gameAssets.defaultLevel, grid)

    paintAssets = new PaintAssets
    canvas = new Canvas(paintAssets, gameAssets, gameState)
    mainPanel = new MainPanel(canvas, gameAssets, gameState, playerActionStack, editorActionStack, commandParser)
    mainFrame = new MainFrame(mainPanel, gameAssets.tileIcon(Tile.Box))
  }

  def getMainFrame: MainFrame = mainFrame
}
