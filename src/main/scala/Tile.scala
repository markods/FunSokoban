enum Tile(private val _idx: Int):
  case Floor extends Tile(0)
  case Wall extends Tile(1)
  case Box extends Tile(2)
  case Goal extends Tile(3)
  case BoxGoal extends Tile(4)
  case Player extends Tile(5)
  case PlayerGoal extends Tile(6)

  def idx: Int = _idx

  def isPlayer: Boolean =
    this == Tile.Player || this == Tile.PlayerGoal

  def isBox: Boolean =
    this == Tile.Box || this == Tile.BoxGoal

  def isGoal: Boolean =
    this == Tile.Goal || this == Tile.PlayerGoal || this == Tile.BoxGoal

  def isFloor: Boolean =
    this == Tile.Floor

  def isWalkable: Boolean =
    this == Tile.Floor || this == Tile.Goal

  def setGoal(isGoal: Boolean): Tile =
    if isGoal then
      this match
        case Tile.Floor => Tile.Goal
        case Tile.Box => Tile.BoxGoal
        case Tile.Player => Tile.PlayerGoal
        case _ => this
    else
      this match
        case Tile.Goal => Tile.Floor
        case Tile.BoxGoal => Tile.Box
        case Tile.PlayerGoal => Tile.Player
        case _ => this
