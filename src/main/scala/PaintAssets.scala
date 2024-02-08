import java.awt.font.TextAttribute
import java.awt.{BasicStroke, Font}

final class PaintAssets {
  var textLeftMargin = 2
  var textUpMargin = 12
  var gridMarginThickness = 20
  var mediumThickness = 2.0f
  var highThickness = 4.0f
  var recenterPlayerThreshold = 20 /*percent*/

  var mediumStroke = new BasicStroke(mediumThickness)
  var highStroke = new BasicStroke(highThickness)
  var font = new Font("SansSerif", Font.PLAIN, 12)

  configure()

  def configure(): Unit = {
    val attributes = new java.util.HashMap[TextAttribute, Any]()
    attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM)
    attributes.put(TextAttribute.SIZE, 12f)
    attributes.put(TextAttribute.FOREGROUND, Palette.TileText.color)
    font = font.deriveFont(attributes)
  }
}
