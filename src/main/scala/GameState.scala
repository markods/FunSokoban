final class GameState(val player: Player,
                      val editor: Editor,
                      val playerUndoStack: ActionStack[PlayerAction],
                      val editorUndoStack: ActionStack[GridChange]) {
  private var currGrid: Grid = _
  private var currLevel: GameFile = _
  private var selectedActorKind: ActorKind = ActorKind.Player

  def level: GameFile = currLevel

  def grid: Grid = currGrid

  def setLevel(level: GameFile, grid: Grid): Boolean = {
    currLevel = level
    currGrid = grid
    actor.setGrid(grid)
  }

  def actor: Actor = selectedActorKind.match {
    case ActorKind.Player => player
    case ActorKind.Editor => editor
  }

  def setActiveActor(actorKind: ActorKind): Unit = {
    selectedActorKind = actorKind
  }

  def actorKind: ActorKind = selectedActorKind
}
