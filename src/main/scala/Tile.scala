enum Tile(private val _idx: Int):
  case FloorOutside extends Tile(0)
  case FloorInside extends Tile(1)
  case Wall extends Tile(2)
  case Box extends Tile(3)
  case Goal extends Tile(4)
  case BoxGoal extends Tile(5)
  case Player extends Tile(6)
  case PlayerGoal extends Tile(7)

  def idx: Int = _idx

  def isPlayer: Boolean =
    this == Tile.Player || this == Tile.PlayerGoal

  def isBox: Boolean =
    this == Tile.Box || this == Tile.BoxGoal

  def isGoal: Boolean =
    this == Tile.Goal || this == Tile.PlayerGoal || this == Tile.BoxGoal

  def isWalkable: Boolean =
    this == Tile.FloorInside || this == Tile.Goal

  def setGoal(isGoal: Boolean): Tile =
    if isGoal then
      this match
        case Tile.FloorInside => Tile.Goal
        case Tile.Box => Tile.BoxGoal
        case Tile.Player => Tile.PlayerGoal
        case _ => this
    else
      this match
        case Tile.Goal => Tile.FloorInside
        case Tile.BoxGoal => Tile.Box
        case Tile.PlayerGoal => Tile.Player
        case _ => this
