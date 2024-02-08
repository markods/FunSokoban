enum PlayerAction:
  case None
  // Only use these basic movements. They'll be automatically promoted
  // to the correct action based on the grid.
  case PlayerUp, PlayerDown, PlayerLeft, PlayerRight
  case PlayerBoxUp, PlayerBoxDown, PlayerBoxLeft, PlayerBoxRight

  def isBasicMovement: Boolean = this match
    case PlayerUp | PlayerDown | PlayerLeft | PlayerRight => true
    case _ => false

  def promoteToBoxAction: PlayerAction = this match
    case PlayerUp => PlayerBoxUp
    case PlayerDown => PlayerBoxDown
    case PlayerLeft => PlayerBoxLeft
    case PlayerRight => PlayerBoxRight
    case _ => this

  def movementX: Int = this match
    case PlayerLeft | PlayerBoxLeft => -1
    case PlayerRight | PlayerBoxRight => 1
    case _ => 0

  def movementY: Int = this match
    case PlayerUp | PlayerBoxUp => -1
    case PlayerDown | PlayerBoxDown => 1
    case _ => 0

  def tilesToMove: Int = this match
    case PlayerUp | PlayerDown | PlayerLeft | PlayerRight => 1
    case PlayerBoxUp | PlayerBoxDown | PlayerBoxLeft | PlayerBoxRight => 2
    case _ => 0
