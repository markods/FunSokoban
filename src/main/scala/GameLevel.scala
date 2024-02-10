import java.nio.file.Path

final case class GameLevel(name: String, path: Path) {
  override def toString: String = name
}