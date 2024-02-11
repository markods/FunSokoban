import java.nio.file.Path

final case class GameFile(name: String, path: Path) {
  override def toString: String = name
}