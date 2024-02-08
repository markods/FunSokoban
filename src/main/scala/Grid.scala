import scala.collection.mutable.ArrayBuffer

final class Grid(private val gs: GridSize) {
  private val tiles: ArrayBuffer[Tile] = ArrayBuffer.fill(gs.m * gs.n)(Tile.FloorOutside)

  def validPosition(i: Int, j: Int): Boolean = i >= 0 && i < gs.m && j >= 0 && j < gs.n

  def getTile(i: Int, j: Int): Tile = tiles(gs.n * i + j)

  def setTile(i: Int, j: Int, tile: Tile): Unit = {
    tiles(gs.n * i + j) = tile
  }

  // 1 -> 2 ->
  def rotateTiles(i1: Int, j1: Int, i2: Int, j2: Int): Unit = {
    val tileA: Tile = getTile(i1, j1)
    val tileB: Tile = getTile(i2, j2)
    setTile(i1, j1, tileB)
    setTile(i2, j2, tileA)
  }

  // Right: 1 -> 2 -> 3 ->
  // Left:  1 <- 2 <- 3 <-
  def rotateTiles(i1: Int, j1: Int, i2: Int, j2: Int, i3: Int, j3: Int, linearRotation: LinearRotation): Unit = {
    val tileA: Tile = getTile(i1, j1)
    val tileB: Tile = getTile(i2, j2)
    val tileC: Tile = getTile(i3, j3)
    linearRotation match {
      // Default.
      case LinearRotation.Right => {
        setTile(i1, j1, tileC)
        setTile(i2, j2, tileA)
        setTile(i3, j3, tileB)
      }
      // Inverse.
      case LinearRotation.Left => {
        setTile(i1, j1, tileB)
        setTile(i2, j2, tileC)
        setTile(i3, j3, tileA)
      }
    }
  }

  def findTiles(onSuccess: Tile => Boolean): ArrayBuffer[GridPosition] = {
    val result = ArrayBuffer[GridPosition]()

    for (i <- 0 until gs.m; j <- 0 until gs.n) {
      if (onSuccess(getTile(i, j))) {
        result += GridPosition(i, j)
      }
    }

    result
  }

  def size: GridSize = gs
}
