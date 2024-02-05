import java.awt.HeadlessException
import java.io.*
import java.nio.file.{FileVisitOption, Files, Paths}
import java.util.logging.{Level, Logger}
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.{JFileChooser, JFrame}
import scala.io.Source
import scala.jdk.CollectionConverters.*
import scala.util.Using


object FileUtil {
  def readFromFile(filePath: String): Option[String] = {
    try Using.resource(Source.fromFile(filePath)) { source =>
      Option(source.mkString)
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
    } catch {
      case ex: FileNotFoundException =>
        logger.log(Level.INFO, "Could not find file with given path.", ex)
        return false
      case ex: IOException =>
        logger.log(Level.INFO, "Could not open/write to file.", ex)
        return false
    }
    true
  }

  def ensureFileExists(file: File): Boolean = {
    try {
      file.getParentFile.mkdirs
      file.createNewFile
    }
    catch {
      case ex: SecurityException =>
        logger.log(Level.INFO, "Could not create parent directories - insufficient privileges.", ex)
        return false
      case ex: IOException =>
        logger.log(Level.INFO, "Could not ensure the file exists.", ex)
        return false
    }
    true
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

  def getUserSelectedPath(actionKind: ActionKind, fileKind: FileKind, previousPath: Option[File] = None): (Option[String], Option[File]) = {
    val FileChooser = new JFileChooser
    FileChooser.setMultiSelectionEnabled(false)
    FileChooser.setFileSelectionMode(fileKind match {
      case FileKind.Directory => JFileChooser.DIRECTORIES_ONLY
      case FileKind.TextFile => JFileChooser.FILES_ONLY
    })
    if (previousPath.nonEmpty) {
      FileChooser.setCurrentDirectory(previousPath.orNull)
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
        case ActionKind.Open => FileChooser.showOpenDialog(Frame)
        case ActionKind.Save => FileChooser.showSaveDialog(Frame)
      }
      if (dialogStatus != JFileChooser.APPROVE_OPTION) {
        return (None, previousPath)
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
        (None, previousPath)
      case ex: SecurityException =>
        logger.log(Level.INFO, "Could not create parent directories - insufficient privileges.", ex)
        (None, previousPath)
    }
  }


  enum ActionKind:
    case Open, Save

  enum FileKind:
    case Directory, TextFile

  private val logger = Logger.getLogger(FileUtil.getClass.getName)
}
