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


    val inputMap = getRootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    inputMap.put(KeyCombo.Left.keyStroke, null)
    inputMap.put(KeyCombo.Right.keyStroke, null)
  }

  def display(): Unit = {
    pack()
    setLocationRelativeTo(null)
    setVisible(true)
  }
}
