final class GameState(private val player: Player,
                      private val editor: Editor) {
  private var currGrid: Grid = _
  private var selectedActorKind: ActorKind = ActorKind.Player

  def grid: Grid = currGrid

  def setGrid(grid: Grid): Boolean = {
    currGrid = grid
    actor.setGrid(grid)
  }

  def actor: Actor = selectedActorKind.match {
    case ActorKind.Player => player
    case ActorKind.Editor => editor
  }

  def setActiveActor(actorKind: ActorKind): Unit = selectedActorKind = actorKind

  def actorKind: ActorKind = selectedActorKind
}
