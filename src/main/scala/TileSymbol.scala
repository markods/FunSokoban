object TileSymbol {
  private val TileToCharMap = new Array[Char](Tile.values.length)
  TileToCharMap(Tile.FloorOutside.idx) = '-'
  TileToCharMap(Tile.FloorInside.idx) = '-'
  TileToCharMap(Tile.Wall.idx) = '#'
  TileToCharMap(Tile.Box.idx) = 'X'
  TileToCharMap(Tile.Goal.idx) = '.'
  TileToCharMap(Tile.BoxGoal.idx) = 'O'
  TileToCharMap(Tile.Player.idx) = 'S'
  TileToCharMap(Tile.PlayerGoal.idx) = 'T'

  def tileToSymbol(tile: Tile): Char = TileToCharMap(tile.idx)

  def symbolToTile(tileChar: Char): Tile = tileChar match {
    case '-' => Tile.FloorOutside
    // case '-' -> Tile.FloorInside   // Needs to be determined based on the whole grid.
    case '#' => Tile.Wall
    case 'X' => Tile.Box
    case '.' => Tile.Goal
    case 'O' => Tile.BoxGoal
    case 'S' => Tile.Player
    case 'T' => Tile.PlayerGoal
    case _ => throw new IllegalArgumentException("Invalid tile.")
  }
}
