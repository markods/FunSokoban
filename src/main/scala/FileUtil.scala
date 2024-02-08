import java.awt.HeadlessException
import java.io.*
import java.nio.file.*
import java.util.logging.{Level, Logger}
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.{JFileChooser, JFrame}
import scala.io.Source
import scala.jdk.CollectionConverters.*
import scala.util.Using


object FileUtil {
  private val logger = Logger.getLogger(getClass.getName)

  def readFromFile(filePath: String): Option[String] = {
    try Using.resource(Source.fromFile(filePath)) { source =>
      val contents = source.mkString
      val wrappedContents = if (contents.nonEmpty) Option(contents) else Option.empty
      wrappedContents
    } catch {
      case ex: FileNotFoundException =>
        logger.log(Level.INFO, "Could not find file with given path.", ex)
        None
      case ex: IOException =>
        logger.log(Level.INFO, "Could not open/read file.", ex)
        None
    }
  }

  def writeToFile(filePath: String, content: String): Boolean = {
    try Using.resource(Files.newBufferedWriter(Paths.get(filePath))) { writer =>
      writer.write(content)
      true
    } catch {
      case ex: FileNotFoundException =>
        logger.log(Level.INFO, "Could not find file with given path.", ex)
        false
      case ex: IOException =>
        logger.log(Level.INFO, "Could not open/write to file.", ex)
        false
    }
  }

  def moveFile(oldFilePath: String, newFilePath: String): Boolean = {
    try {
      val oldPath: Path = Paths.get(oldFilePath)
      val newPath: Path = Paths.get(newFilePath)
      Files.move(oldPath, newPath)
      true
    } catch {
      case ex: FileAlreadyExistsException =>
        logger.log(Level.INFO, "The destination file already exists.", ex)
        false
      case ex: FileNotFoundException =>
        logger.log(Level.INFO, "Could not find file with given path.", ex)
        false
      case ex: IOException =>
        logger.log(Level.INFO, "Could not move file.", ex)
        false
    }
  }

  def listFilesInDirectory(directoryPath: String): List[String] = {
    val path = Paths.get(directoryPath)
    if (!Files.exists(path) || !Files.isDirectory(path)) {
      return Nil
    }

    try {
      val stream = Files.walk(path, FileVisitOption.FOLLOW_LINKS)
      val files = stream.iterator().asScala
        .filter(Files.isRegularFile(_))
        .map(_.toString)
        .toList
      files
    } catch {
      case ex: SecurityException =>
        logger.log(Level.INFO, "Could not create parent directories - insufficient privileges.", ex)
        Nil
      case ex: IOException =>
        logger.log(Level.INFO, "Could not ensure the file exists.", ex)
        Nil
    }
  }

  def getUserSelectedPath(actionKind: FileActionKind, fileKind: FileKind, previousFilePath: Option[File] = None): (Option[String], Option[File]) = {
    val FileChooser = new JFileChooser
    FileChooser.setMultiSelectionEnabled(false)
    FileChooser.setFileSelectionMode(fileKind match {
      case FileKind.Directory => JFileChooser.DIRECTORIES_ONLY
      case FileKind.TextFile => JFileChooser.FILES_ONLY
    })
    if (previousFilePath.nonEmpty) {
      FileChooser.setCurrentDirectory(previousFilePath.orNull)
    }
    fileKind match {
      case FileKind.TextFile =>
        FileChooser.setFileFilter(new FileNameExtensionFilter("Text file (*.txt)", "txt"))
      case _ =>
    }

    try {
      val Frame = new JFrame
      Frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE)
      // This won't work since we don't have access to showOpenDialog and showSaveDialog.
      // Frame.setTitle("Choose file")
      // Frame.getContentPane.setSize(new Dimension(640, 480))

      val dialogStatus = actionKind match {
        case FileActionKind.Open => FileChooser.showOpenDialog(Frame)
        case FileActionKind.Save => FileChooser.showSaveDialog(Frame)
      }
      if (dialogStatus != JFileChooser.APPROVE_OPTION) {
        return (None, previousFilePath)
      }

      val usPath = FileChooser.getSelectedFile.getAbsolutePath
      val path = usPath +
        (fileKind match {
          case FileKind.TextFile if !usPath.endsWith(".txt") => ".txt"
          case _ => ""
        })

      (Option(path), Option(FileChooser.getCurrentDirectory))
    }
    catch {
      case ex: HeadlessException =>
        logger.log(Level.INFO, "Could not open file chooser.", ex)
        (None, previousFilePath)
      case ex: SecurityException =>
        logger.log(Level.INFO, "Could not create parent directories - insufficient privileges.", ex)
        (None, previousFilePath)
    }
  }
}
