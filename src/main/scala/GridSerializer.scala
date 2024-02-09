//final class GridSerializer extends Serializer[Grid] {
// override def toString(grid: Grid): String = {
//   val stringBuilder = StringBuilder()
//   val gridSize = grid.size
//   grid.foreach((i, j, tile) => {
//     val tile = grid.getTile(i, j)
//     val symbol = TileSymbol.toSymbol(tile)
//     stringBuilder.append(symbol)
//     stringBuilder.append(if (j < gridSize.n - 1) " " else "\n")
//   })
//
//   stringBuilder.mkString
// }
//
// override def toObject(str: String): Option[Grid] = {
//   val gridSize = new GridSize(0, 0)
//
// }
//}
