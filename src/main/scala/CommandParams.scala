//import scala.collection.mutable
//
//class CommandParams extends Cloneable {
//  private val params: mutable.ArrayBuffer[String] = mutable.ArrayBuffer[CommandParam]()
//  private val defaults: mutable.ArrayBuffer[Option[Any]] = mutable.ArrayBuffer[CommandParam]()
//
//  def getIndex(name: String): Int = {
//    paramList.indexWhere(param => param.name == name)
//  }
//
//  def get(i: Int): Option[CommandParam] = {
//    if (i < 0 || i >= paramList.length) {
//      return Option.empty
//    }
//    Option(paramList(i))
//  }
//
//  def add(param: CommandParam): Int = {
//    // Cannot add a parameter with the same name.
//    if (paramList.contains(param)) {
//      return -1
//    }
//    paramList.addOne(param)
//    paramList.length - 1
//  }
//
//  def resetAll(): Unit = {
//    paramList.foreach(param => param.reset())
//  }
//
//  override def clone(prefix: String): CommandParams = {
//    val other = new CommandParams()
//    paramList.foreach(param => {
//      other.paramList.addOne(param.clone())
//    })
//    other
//  }
//}
