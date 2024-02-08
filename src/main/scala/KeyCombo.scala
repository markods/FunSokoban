import javax.swing.KeyStroke

enum KeyCombo(private val _keyStroke: KeyStroke):
  case CtrlZ extends KeyCombo(KeyStroke.getKeyStroke("control Z"))
  case CtrlY extends KeyCombo(KeyStroke.getKeyStroke("control Y"))

  case Tab extends KeyCombo(KeyStroke.getKeyStroke("TAB"))
  case Enter extends KeyCombo(KeyStroke.getKeyStroke("ENTER"))

  case Up extends KeyCombo(KeyStroke.getKeyStroke("UP"))
  case Down extends KeyCombo(KeyStroke.getKeyStroke("DOWN"))
  case Left extends KeyCombo(KeyStroke.getKeyStroke("LEFT"))
  case Right extends KeyCombo(KeyStroke.getKeyStroke("RIGHT"))

  def keyStroke: KeyStroke = _keyStroke
