import com.formdev.flatlaf.FlatDarculaLaf

import java.util.logging.{Level, Logger}
import javax.swing.*


object Main {
  def main(args: Array[String]): Unit = {
    try UIManager.setLookAndFeel(new FlatDarculaLaf)
    catch {
      case e: UnsupportedLookAndFeelException =>
        logger.log(Level.WARNING, "Could not set FlatDarkLaf theme.")
    }
    SwingUtilities.invokeLater(() => createAndShowGUI())
  }

  private def createAndShowGUI(): Unit = {
    val mainFrame = new JFrame
    mainFrame.setTitle("Sokoban")
    mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    val icon = new ImageIcon(classOf[MainPanel].getResource("/box.png"))
    mainFrame.setIconImage(icon.getImage)

    val mainPanel = new MainPanel
    mainFrame.getContentPane.add(mainPanel)

    mainFrame.pack()
    mainFrame.setLocationRelativeTo(null)
    mainFrame.setVisible(true)
  }

  private val logger = Logger.getLogger(Main.getClass.getName)
}
