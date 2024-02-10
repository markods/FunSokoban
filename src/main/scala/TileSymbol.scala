object TileSymbol {
  private val TileToSymbolMap = new Array[Char](Tile.values.length)
  TileToSymbolMap(Tile.Floor.idx) = '-'
  TileToSymbolMap(Tile.Wall.idx) = '#'
  TileToSymbolMap(Tile.Box.idx) = 'X'
  TileToSymbolMap(Tile.Goal.idx) = '.'
  TileToSymbolMap(Tile.BoxGoal.idx) = 'O'
  TileToSymbolMap(Tile.Player.idx) = 'S'
  TileToSymbolMap(Tile.PlayerGoal.idx) = 'T'

  def toSymbol(tile: Tile): Char = TileToSymbolMap(tile.idx)

  def toTile(tileSymbol: Char): Tile = tileSymbol match {
    case '-' => Tile.Floor
    case '#' => Tile.Wall
    case 'x' | 'X' => Tile.Box
    case '.' => Tile.Goal
    case 'o' | 'O' => Tile.BoxGoal
    case 's' | 'S' => Tile.Player
    case 't' | 'T' => Tile.PlayerGoal
    case _ => throw new IllegalArgumentException("Invalid tile.")
  }
}
