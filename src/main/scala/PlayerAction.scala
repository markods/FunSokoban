enum PlayerAction(private val _idx: Int):
  case None extends PlayerAction(0)

  // Only use basic movements - up, down, left, right - as arguments to methods.
  // They'll be automatically promoted to the correct action based on the grid.
  case PlayerUp extends PlayerAction(1)
  case PlayerDown extends PlayerAction(2)
  case PlayerLeft extends PlayerAction(3)
  case PlayerRight extends PlayerAction(4)

  case PlayerBoxUp extends PlayerAction(5)
  case PlayerBoxDown extends PlayerAction(6)
  case PlayerBoxLeft extends PlayerAction(7)
  case PlayerBoxRight extends PlayerAction(8)

  def idx: Int = _idx

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
