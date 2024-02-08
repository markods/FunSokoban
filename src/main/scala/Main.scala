import com.formdev.flatlaf.FlatDarculaLaf

import java.util.logging.{Level, Logger}
import javax.swing.*


object Main {
  private val container = new Container
  private val logger = Logger.getLogger(getClass.getName)

  def main(args: Array[String]): Unit = {
    try {
      UIManager.setLookAndFeel(new FlatDarculaLaf)
    }
    catch {
      case ex: UnsupportedLookAndFeelException =>
        logger.log(Level.WARNING, "Could not set FlatDarkLaf theme.", ex)
    }

    try {
      container.configure()
      val mainFrame = container.getMainFrame
      SwingUtilities.invokeLater(() => mainFrame.display())
    }
    catch {
      case ex: Throwable =>
        logger.log(Level.SEVERE, "Could not configure app, exiting.", ex)
        System.exit(-1)
    }
  }
}
