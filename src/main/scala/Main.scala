import javax.swing._
import java.awt.event._

object Main extends JFrame {
  def main(args: Array[String]): Unit = {
    println("Hello World!")
    SwingUtilities.invokeLater(() => createAndShowGUI())
  }

  private def createAndShowGUI(): Unit = {
    setTitle("Scala Swing Example")
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

    val button = new JButton("Click Me!")
    button.addActionListener((e: ActionEvent) => {
      JOptionPane.showMessageDialog(null, "Button Clicked!")
    })

    getContentPane.add(button)
    pack()
    setLocationRelativeTo(null)
    setVisible(true)
  }
}
