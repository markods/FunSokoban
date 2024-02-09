trait Serializer[T] {
  def toString(obj: T): String

  def toObject(str: String): Option[T]
}
