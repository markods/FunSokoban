import java.awt.HeadlessException
import java.io.*
import java.nio.file.*
import java.util.logging.{Level, Logger}
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.{JFileChooser, JFrame}
import scala.io.Source
import scala.jdk.CollectionConverters.*
import scala.util.Using
import scala.util.matching.Regex


object FileUtil {
  private val logger = Logger.getLogger(getClass.getName)

  def getPath(filePathString: String): Option[Path] = {
    try {
      Option(Paths.get(filePathString))
    } catch {
      case ex: InvalidPathException =>
        logger.log(Level.INFO, "The file path is invalid.", ex)
        Option.empty
    }
  }

  def getFile(filePathString: String): Option[File] = {
    try {
      Option(Paths.get(filePathString).toFile)
    } catch {
      case ex: InvalidPathException =>
        logger.log(Level.INFO, "The file path is invalid.", ex)
        Option.empty
      case ex: UnsupportedOperationException =>
        logger.log(Level.INFO, "The path is not associated with the default provider.", ex)
        Option.empty
    }
  }

  def getFileNameNoExtension(filePath: Path): String = {
    // Good enough for this purpose. Better extension stripping should be done.
    val fileNameExtension = filePath.getFileName.toString
    val regex = new Regex(".*\\.")
    val fileNameNoExtension = regex.replaceFirstIn(fileNameExtension.reverse, "").reverse
    fileNameNoExtension
  }

  def ensureExtension(filePathString: String, fileKind: FileKind): String = {
    val result = filePathString +
      (fileKind match {
        case FileKind.TextFile if !filePathString.endsWith(".txt") => ".txt"
        case FileKind.MovesTextFile if !filePathString.endsWith(".moves.txt") => ".moves.txt"
        case _ => ""
      })
    result
  }

  def readFromFile(filePathString: String): Option[String] = {
    try Using.resource(Source.fromFile(filePathString)) { source =>
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

  def writeToFile(filePathString: String, content: String): Boolean = {
    try Using.resource(Files.newBufferedWriter(Paths.get(filePathString))) { writer =>
      writer.write(content)
      true
    } catch {
      case ex: InvalidPathException =>
        logger.log(Level.INFO, "The file path is invalid.", ex)
        false
      case ex: FileNotFoundException =>
        logger.log(Level.INFO, "Could not find file with given path.", ex)
        false
      case ex: IOException =>
        logger.log(Level.INFO, "Could not open/write to file.", ex)
        false
    }
  }

  def moveFile(oldFilePathString: String, newFilePathString: String): Boolean = {
    try {
      val oldPath: Path = Paths.get(oldFilePathString)
      val newPath: Path = Paths.get(newFilePathString)
      Files.move(oldPath, newPath)
      true
    } catch {
      case ex: InvalidPathException =>
        logger.log(Level.INFO, "At least one of the file paths is invalid.", ex)
        false
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

  def deleteFile(filePathString: String): Boolean = {
    try {
      Files.delete(Paths.get(filePathString))
      true
    } catch {
      case ex: InvalidPathException =>
        logger.log(Level.INFO, "At least one of the file paths is invalid.", ex)
        false
      case ex: NoSuchFileException =>
        logger.log(Level.INFO, "No file exists with the given path.", ex)
        false
      case ex: DirectoryNotEmptyException =>
        logger.log(Level.INFO, "Could not delete non-empty directory.", ex)
        false
      case ex: IOException =>
        logger.log(Level.INFO, "Could not move file.", ex)
        false
      case ex: SecurityException =>
        logger.log(Level.INFO, "Insufficient permissions.", ex)
        false
    }
  }

  def listFilesInDirectory(directoryPathString: String): List[Path] = {
    try {
      val path = Paths.get(directoryPathString)
      if (!Files.exists(path) || !Files.isDirectory(path)) {
        return Nil
      }

      val stream = Files.walk(path, FileVisitOption.FOLLOW_LINKS)
      val files = stream.iterator().asScala
        .filter(Files.isRegularFile(_))
        .toList

      files
    } catch {
      case ex: InvalidPathException =>
        logger.log(Level.INFO, "The directory path is invalid.", ex)
        Nil
      case ex: SecurityException =>
        logger.log(Level.INFO, "Could not create parent directories - insufficient privileges.", ex)
        Nil
      case ex: IOException =>
        logger.log(Level.INFO, "Could not ensure the file exists.", ex)
        Nil
    }
  }

  def getUserSelectedPath(actionKind: FileActionKind, fileKind: FileKind, previousDirectory: Option[File] = None): (Option[String], Option[File]) = {
    val FileChooser = new JFileChooser
    FileChooser.setMultiSelectionEnabled(false)
    FileChooser.setFileSelectionMode(fileKind match {
      case FileKind.Directory => JFileChooser.DIRECTORIES_ONLY
      case FileKind.TextFile => JFileChooser.FILES_ONLY
      case FileKind.MovesTextFile => JFileChooser.FILES_ONLY
    })
    if (previousDirectory.nonEmpty) {
      FileChooser.setCurrentDirectory(previousDirectory.orNull)
    }
    fileKind match {
      case FileKind.TextFile =>
        FileChooser.setFileFilter(new FileNameExtensionFilter("Text file (*.txt)", "txt"))
      case FileKind.MovesTextFile =>
        FileChooser.setFileFilter(new FileNameExtensionFilter("Moves text file (*.moves.txt)", "txt"))
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
        return (None, previousDirectory)
      }

      val usPath = FileChooser.getSelectedFile.getAbsolutePath
      val path = FileUtil.ensureExtension(usPath, fileKind)

      (Option(path), Option(FileChooser.getCurrentDirectory))
    }
    catch {
      case ex: HeadlessException =>
        logger.log(Level.INFO, "Could not open file chooser.", ex)
        (None, previousDirectory)
      case ex: SecurityException =>
        logger.log(Level.INFO, "Could not create parent directories - insufficient privileges.", ex)
        (None, previousDirectory)
    }
  }
}
