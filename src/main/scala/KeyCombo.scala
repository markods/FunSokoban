import javax.swing.KeyStroke

enum KeyCombo(private val _keyStroke: KeyStroke):
  case CtrlZ extends KeyCombo(KeyStroke.getKeyStroke("CTRL Z"))
  case CtrlY extends KeyCombo(KeyStroke.getKeyStroke("CTRL Y"))
  case AltLeft extends KeyCombo(KeyStroke.getKeyStroke("ALT LEFT"))
  case AltRight extends KeyCombo(KeyStroke.getKeyStroke("ALT RIGHT"))

  case Tab extends KeyCombo(KeyStroke.getKeyStroke("TAB"))
  case Enter extends KeyCombo(KeyStroke.getKeyStroke("ENTER"))

  case Up extends KeyCombo(KeyStroke.getKeyStroke("UP"))
  case Down extends KeyCombo(KeyStroke.getKeyStroke("DOWN"))
  case Left extends KeyCombo(KeyStroke.getKeyStroke("LEFT"))
  case Right extends KeyCombo(KeyStroke.getKeyStroke("RIGHT"))

  def keyStroke: KeyStroke = _keyStroke
