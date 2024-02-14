// TODO: redesign solver to be incremental. Don't save grid in state, instead use areaIds. Use even numbers to designate none, wall and walkable areas, odd numbers to designate player and boxes.

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

private final class SolverParams(var zoobrist: ArrayBuffer[Long],
                                 var nearestGoalIgnoringBoxes: ArrayBuffer[Int],
                                 var unvisitedStates: mutable.HashSet[SolverState],
                                 var unsolvableStateHashes: mutable.HashSet[Int],
                                 var remainingMoves: Int) {
}

private final class SolverState(var parent: Option[SolverState],
                                var grid: Grid,
                                var playerNij: Int,
                                var zoobristHash: Long,
                                var prevBoxMove: PlayerAction) {

  // Never mind that we resize the Zoobrist hash, equals will make sure that they match.
  override def hashCode(): Int = zoobristHash.hashCode()

  override def equals(obj: Any): Boolean = {
    obj match {
      case null => false
      case other: SolverState =>
        this.zoobristHash == other.zoobristHash
      case _ => false
    }
  }
}

final class Solver {
  private val noneAreaId = -2
  private val wallAreaId = 0
  private val playerAreaId = 1

  // Grid should be valid first.
  def solveUnchecked(grid: Grid, maxMoves: Int): ArrayBuffer[PlayerAction] = {
    val gs = grid.size
    val gsMN = gs.m * gs.n
    val random = new Random()

    val zoobrist = ArrayBuffer.fill[Long](gsMN)(random.nextLong)
    val initialPlayerNij = playerNij(grid)
    val (initTileAreaIds, initReachableBoxNijs) = tileAreaIdsAndReachableBoxNijs(grid, initialPlayerNij)
    val nearestGoalIgnoringBoxes = getNearestGoalIgnoringBoxesMap(grid)

    // Remove player and goals from the grid to simplify calculation.
    grid.setTile(initialPlayerNij, Tile.Floor)
    grid.foreach((i, j, tile) => {
      if (tile.isGoal) {
        grid.setTile(i, j, if (tile.isBox) Tile.Box else Tile.Floor)
      }
    })

    val root = new SolverState(
      Option.empty,
      grid,
      initialPlayerNij,
      zoobristHash(grid, initTileAreaIds, zoobrist, initialPlayerNij),
      PlayerAction.None)

    val params = new SolverParams(
      zoobrist,
      nearestGoalIgnoringBoxes,
      mutable.HashSet[SolverState](),
      mutable.HashSet[Int](),
      maxMoves
    )
    params.unvisitedStates.add(root)

    val solution = solveTailrec(params)
    if (solution.isEmpty) {
      return ArrayBuffer.empty
    }

    val playerActions = getPlayerActions(solution.get)
    playerActions
  }

  // TODO: move to grid
  private def nijMap(gs: GridSize, i: Int, j: Int): Int = gs.n * i + j

  private def nijMap(gs: GridSize, pos: GridPosition): Int = gs.n * pos.i + pos.j

  private def nijMap(gs: GridSize, nij: Int, iDiff: Int, jDiff: Int): Int = nij + gs.n * iDiff + jDiff

  private def nijValid(gs: GridSize, nij: Int): Boolean = nij >= 0 && nij < gs.m * gs.n


  private def playerNij(grid: Grid): Int = {
    val playerPosOption = grid.findFirst((i, j, tile) => tile.isPlayer)
    val playerPos = playerPosOption.get
    val playerNij = grid.size.n * playerPos.i + playerPos.j
    playerNij
  }

  private def tileAreaIdsAndReachableBoxNijs(grid: Grid, nij: Int): ( /*areaIds*/ ArrayBuffer[Int], /*reachableBoxNijs*/ ArrayBuffer[Int]) = {
    val tileAreaIds = ArrayBuffer.fill[Int](noneAreaId)(grid.size.m * grid.size.n)
    val reachableBoxNijs = ArrayBuffer[Int]()
    val gs = grid.size

    grid.foreachNij((nij: Int, tile: Tile) => {
      if (tile.isWall) {
        tileAreaIds(nij) = wallAreaId
      }
    })

    def floodFillRec(nij: Int, areaId: Int): Unit = {
      if (!nijValid(gs, nij)) {
        return
      }

      if (tileAreaIds(nij) != noneAreaId) {
        return
      }
      val tile = grid.getTile(nij)
      if (tile.isBox) {
        reachableBoxNijs.addOne(areaId)
        return
      }

      tileAreaIds(nij) = areaId

      floodFillRec(nij, nijMap(gs, nij, -1, 0))
      floodFillRec(nij, nijMap(gs, nij, 1, 0))
      floodFillRec(nij, nijMap(gs, nij, 0, -1))
      floodFillRec(nij, nijMap(gs, nij, 0, 1))
    }

    floodFillRec(nij, playerAreaId)

    (tileAreaIds, reachableBoxNijs)
  }

  private def getNearestGoalIgnoringBoxesMap(grid: Grid): ArrayBuffer[Int] = {
    val gs = grid.size
    val gsMN = gs.m * gs.n
    val nearestGoalIgnoringBoxes: ArrayBuffer[Int] = ArrayBuffer.fill(gsMN)(Int.MaxValue)

    val goalPositionList = grid.findAll((i, j, tile) => tile.isBox)

    def floodFillRec(nij: Int, cost: Int): Unit = {
      if (!nijValid(gs, nij)) {
        return
      }

      val tile = grid.getTile(nij)
      if (tile.isWall) {
        return
      }

      val oldCost = nearestGoalIgnoringBoxes(nij)
      nearestGoalIgnoringBoxes(nij) = math.min(oldCost, cost)

      floodFillRec(nij, nijMap(gs, nij, -1, 0))
      floodFillRec(nij, nijMap(gs, nij, 1, 0))
      floodFillRec(nij, nijMap(gs, nij, 0, -1))
      floodFillRec(nij, nijMap(gs, nij, 0, 1))
    }

    for (goalPos <- goalPositionList) {
      floodFillRec(nijMap(gs, goalPos), 0)
    }

    nearestGoalIgnoringBoxes
  }

  private def zoobristHash(grid: Grid,
                           tileAreaIds: ArrayBuffer[Int],
                           zoobrist: ArrayBuffer[Long],
                           playerNij: Int): Long = {
    var hash: Long = 0
    val playerArea = tileAreaIds(playerNij)
    grid.foreachNij((nij: Int, tile: Tile) => {
      if (tile.isBox || tileAreaIds(nij) == playerArea) {
        hash = hash ^ zoobrist(nij)
      }
    })

    hash
  }

  // @tailrec
  private def solveTailrec(params: SolverParams): Option[SolverState] = {
    params.remainingMoves -= 1
    if (params.remainingMoves < 0) {
      return Option.empty
    }

    // TODO:
    Option.empty
  }

  private def getPlayerActions(solution: SolverState): ArrayBuffer[PlayerAction] = {
    // TODO:
    ArrayBuffer.empty
  }
}
