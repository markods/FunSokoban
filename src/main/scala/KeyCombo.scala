import javax.swing.KeyStroke

object KeyCombo {
  def toKeyCombo(keyStroke: KeyStroke): KeyCombo = {
    if (KeyCombo.Up.keyStroke.equals(keyStroke)) KeyCombo.Up
    else if (KeyCombo.Down.keyStroke.equals(keyStroke)) KeyCombo.Down
    else if (KeyCombo.Left.keyStroke.equals(keyStroke)) KeyCombo.Left
    else if (KeyCombo.Right.keyStroke.equals(keyStroke)) KeyCombo.Right
    else if (KeyCombo.CtrlZ.keyStroke.equals(keyStroke)) KeyCombo.CtrlZ
    else if (KeyCombo.CtrlY.keyStroke.equals(keyStroke)) KeyCombo.CtrlY
    else KeyCombo.Unspecified
  }
}

enum KeyCombo(private val _keyStroke: KeyStroke):
  case Unspecified extends KeyCombo(null)
  case CtrlZ extends KeyCombo(KeyStroke.getKeyStroke("control Z"))
  case CtrlY extends KeyCombo(KeyStroke.getKeyStroke("control Y"))

  case Tab extends KeyCombo(KeyStroke.getKeyStroke("TAB"))
  case Enter extends KeyCombo(KeyStroke.getKeyStroke("ENTER"))

  case Up extends KeyCombo(KeyStroke.getKeyStroke("UP"))
  case Down extends KeyCombo(KeyStroke.getKeyStroke("DOWN"))
  case Left extends KeyCombo(KeyStroke.getKeyStroke("LEFT"))
  case Right extends KeyCombo(KeyStroke.getKeyStroke("RIGHT"))

  def keyStroke: KeyStroke = _keyStroke
