import java.awt.Color

enum Palette(private val _color: Color):
  case GridBack extends Palette(new Color(68, 71, 74)) // Medium gray
  case TileLine extends Palette(new Color(106, 108, 111)) // Light gray
  case TileText extends Palette(new Color(199, 201, 207)) // White
  case FocusFrame extends Palette(new Color(236, 75, 48)) // Red

  def color: Color = _color
