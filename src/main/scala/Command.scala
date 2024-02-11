//import scala.collection.mutable
//
//sealed trait Command(val name: String,
//                     val params: CommandParams) extends Cloneable {
//  // TODO: remove default implementation
////  def setParams(editor: Editor, param: CommandParams): Boolean = {
////
////  }
//
//  def checkState(editor: Editor): Boolean = false
//
//  def apply(editor: Editor): Boolean = false
//
//
//  override def hashCode(): Int = name.hashCode
//
//  override def equals(obj: Any): Boolean = {
//    obj match {
//      case null => false
//      case other: Command =>
//        this.name == other.name
//      case _ => false
//    }
//  }
//
//  override def toString: String = name
//}
//
////class RowExtend(name: String,
////                iBetween: CommandParamNum,
////                count: CommandParamNum) extends Command(name) {
////  param.addOne(iBetween)
////  param.addOne(count)
////
////}
//
////class ColExtend(iBetween: Int, count: Int) extends Command
//
////class RowDelete(i: Int, n: Int) extends Command
//
////class ColDelete(i: Int, n: Int) extends Command
//
////class TileSet(t: Tile, pos: GridPosition) extends Command
//
////class BoxGoalSwap extends Command
//
////class WallMinimize(pos: GridPosition) extends Command
//
////class WallFilter(pos: GridPosition, n: Int) extends Command
//
////class WallFractalize(pos: GridPosition) extends Command
//
////class LevelValidate extends Command
//
////class Definition(name: String,
////                 parameters: List[String],
////                 commands: List[Command]) extends Command {
////  override def checkParams(editor: Editor): Boolean = false
////
////  override def checkState(editor: Editor): Boolean = false
////
////  override def apply(editor: Editor): Boolean = false
////
////}
//
////class TransactionDef(name: String, parameters: List[String], commands: List[Command]) extends Command
//
////class Undef(name: String) extends Command