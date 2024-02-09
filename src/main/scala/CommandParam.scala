final class CommandParam(val name: String, val paramKind: CommandParamKind) {
  private var _value: Option[Any] = Option.empty

  def get[T]: T = _value.get.asInstanceOf[T]

  def set[T](value: T): Unit = _value = Option(value)
}
