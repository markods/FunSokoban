object PlayerActionSymbol {
  private val PlayerActionToSymbolMap = new Array[Char](PlayerAction.values.length)
  PlayerActionToSymbolMap(PlayerAction.None.idx) = ' '
  PlayerActionToSymbolMap(PlayerAction.PlayerUp.idx) = 'U'
  PlayerActionToSymbolMap(PlayerAction.PlayerDown.idx) = 'D'
  PlayerActionToSymbolMap(PlayerAction.PlayerLeft.idx) = 'L'
  PlayerActionToSymbolMap(PlayerAction.PlayerRight.idx) = 'R'
  PlayerActionToSymbolMap(PlayerAction.PlayerBoxUp.idx) = 'U'
  PlayerActionToSymbolMap(PlayerAction.PlayerBoxDown.idx) = 'D'
  PlayerActionToSymbolMap(PlayerAction.PlayerBoxLeft.idx) = 'L'
  PlayerActionToSymbolMap(PlayerAction.PlayerBoxRight.idx) = 'R'

  def toSymbol(playerAction: PlayerAction): Char = PlayerActionToSymbolMap(playerAction.idx)

  def toPlayerAction(playerActionSymbol: Char): PlayerAction = playerActionSymbol match {
    case 'U' => PlayerAction.PlayerUp
    case 'D' => PlayerAction.PlayerDown
    case 'L' => PlayerAction.PlayerLeft
    case 'R' => PlayerAction.PlayerRight
    case _ => throw new IllegalArgumentException("Invalid player action.")
  }
}

