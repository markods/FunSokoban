import javax.swing.*

final class MainFrame(private val mainPanel: MainPanel,
                private val icon: ImageIcon
               ) extends JFrame {
  configure()

  private def configure(): Unit = {
    setTitle("Sokoban")
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    setIconImage(icon.getImage)
    getContentPane.add(mainPanel)
  }

  def display(): Unit = {
    pack()
    setLocationRelativeTo(null)
    setVisible(true)
  }
}
