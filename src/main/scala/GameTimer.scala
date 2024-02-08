import java.awt.event.ActionListener
import javax.swing.Timer

final class GameTimer(private val startAction: () => Unit,
                private val tickAction: () => Unit,
                private val stopAction: () => Unit) {
  private var startNanos: Long = 0
  private var currNanos: Long = 0
  private val timer: Timer = new Timer(1000 /*ms*/ , evt => {
    currNanos = System.nanoTime()
    tickAction()
  })

  def start(): Unit = {
    startNanos = System.nanoTime()
    timer.start()
    startAction()
  }

  def stop(): Unit = {
    timer.stop()
    startNanos = 0
    currNanos = 0
    stopAction()
  }

  def restart(): Unit = {
    stop()
    start()
  }

  def currentTime(): String = {
    val diff = (currNanos - startNanos) / 1000_000_000
    val seconds = diff % 60
    val minutes = diff / 60 % 60
    val hours = diff / 3600

    f"$hours%02d:$minutes%02d:$seconds%02d"
  }
}