trait Serializer[T] {
  def readFromFile(path: String): Option[T]
  def writeToFile(path: String, contents: String): Boolean
}
