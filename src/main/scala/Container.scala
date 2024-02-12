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
  private var playerUndoStack: ActionStack[PlayerAction] = _
  private var editorUndoStack: ActionStack[GridChange] = _
  private var commandParser: CmdParser = _

  def configure(): Unit = {
    gameAssets = new GameAssets
    val grid = new Grid(gameAssets.defaultGridSize, Tile.Floor)

    playerUndoStack = new ActionStack[PlayerAction](PlayerAction.None)
    player = new Player(playerUndoStack)
    editorUndoStack = new ActionStack[GridChange](GridChangeNone)
    commandParser = new CmdParser(gameAssets)
    editor = new Editor(editorUndoStack)

    gameState = new GameState(player, editor, playerUndoStack, editorUndoStack)
    gameState.setActiveActor(Player)
    gameState.setLevel(gameAssets.defaultLevel, grid)

    paintAssets = new PaintAssets
    canvas = new Canvas(paintAssets, gameAssets, gameState)
    mainPanel = new MainPanel(canvas, gameAssets, gameState, commandParser)
    mainFrame = new MainFrame(mainPanel, gameAssets.tileIcon(Tile.Box))
  }

  def getMainFrame: MainFrame = mainFrame
}
