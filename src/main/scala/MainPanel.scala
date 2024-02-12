import java.awt.event.{ActionEvent, ActionListener, KeyAdapter, KeyEvent}
import java.awt.{Dimension, Insets, KeyboardFocusManager}
import java.io.File
import javax.swing.*
import javax.swing.event.{ChangeEvent, ChangeListener}

final class MainPanel(private val jCanvas: Canvas,
                      private val gameAssets: GameAssets,
                      private val gameState: GameState,
                      private val commandParser: CmdParser) extends JPanel {
  private val jTabbedPane: JTabbedPane = new JTabbedPane

  private val jPlay_Panel: JPanel = new JPanel
  private val jPlay_LevelLabel: JLabel = new JLabel
  private val jPlay_LevelComboBoxModel: DefaultComboBoxModel[GameFile] = new DefaultComboBoxModel[GameFile]()
  private val jPlay_LevelComboBox: JComboBox[GameFile] = new JComboBox[GameFile]
  private val jPlay_MovesLabel: JLabel = new JLabel
  private val jPlay_UndoMultipleMovesButton: JButton = new JButton
  private val jPlay_UndoMoveButton: JButton = new JButton
  private val jPlay_MovesTextField: JTextField = new JTextField
  private val jPlay_RedoMoveButton: JButton = new JButton
  private val jPlay_RedoMultipleMovesButton: JButton = new JButton
  private val jPlay_ImportButton: JButton = new JButton
  private val jPlay_SaveButton: JButton = new JButton
  private val jPlay_TimeLabel: JLabel = new JLabel
  private val jPlay_TimeTextField: JTextField = new JTextField
  private val jPlay_RestartButton: JButton = new JButton
  private val jPlay_SolveButton: JButton = new JButton
  private val jPlay_InfoTextArea: JTextArea = new JTextArea

  private val jCreate_Panel: JPanel = new JPanel
  private val jCreate_LevelLabel: JLabel = new JLabel
  private val jCreate_LevelComboBoxModel: DefaultComboBoxModel[GameFile] = new DefaultComboBoxModel[GameFile]()
  private val jCreate_LevelComboBox: JComboBox[GameFile] = new JComboBox[GameFile]
  private val jCreate_DeleteButton: JButton = new JButton
  private val jCreate_ImportButton: JButton = new JButton
  private val jCreate_NameLabel: JLabel = new JLabel
  private val jCreate_NameTextField: JTextField = new JTextField
  private val jCreate_SaveButton: JButton = new JButton
  private val jCreate_SetFloorButton: JButton = new JButton
  private val jCreate_SetWallButton: JButton = new JButton
  private val jCreate_SetBoxButton: JButton = new JButton
  private val jCreate_SetGoalButton: JButton = new JButton
  private val jCreate_SetPlayerButton: JButton = new JButton
  private val jCreate_SetBoxGoalButton: JButton = new JButton
  private val jCreate_SetPlayerGoalButton: JButton = new JButton
  private val jCreate_CommandHistoryScrollPane: JScrollPane = new JScrollPane
  private val jCreate_CommandHistoryTextArea: JTextArea = new JTextArea
  private val jCreate_CommandTextScrollPane: JScrollPane = new JScrollPane
  private val jCreate_CommandTextArea: JTextArea = new JTextArea

  private val jCanvasSeparator: JSeparator = new JSeparator
  /*private val jCanvas: JCanvas = new Canvas(...)*/
  private var previousDirectory: Option[File] = Option.empty

  private val gameTimer: GameTimer = new GameTimer(
    () => jPlay_TimeTextField.setText(gameAssets.zeroTimeString),
    () => jPlay_TimeTextField.setText(gameTimer.currentTime()),
    () => jPlay_TimeTextField.setText(gameAssets.zeroTimeString)
  )

  configure()

  private def configure(): Unit = {
    jTabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT)
    jTabbedPane.addChangeListener((evt: ChangeEvent) => ChangeTab())
    val jCanvasKeyListener = new KeyAdapter() {
      override def keyPressed(evt: KeyEvent): Unit = {
        CanvasPanelKeyPressed(evt)
      }
    }
    jPlay_LevelLabel.setText("Level")
    jPlay_LevelComboBoxModel.addElement(gameAssets.defaultLevel)
    jPlay_LevelComboBox.setModel(jPlay_LevelComboBoxModel)
    jPlay_LevelComboBox.addActionListener((evt: ActionEvent) => SelectLevelActionListener(jPlay_LevelComboBoxModel, ActorKind.Player, false))
    jPlay_MovesLabel.setText("Moves")
    jPlay_UndoMultipleMovesButton.setText("<<")
    jPlay_UndoMultipleMovesButton.setMargin(new Insets(2, 5, 3, 5))
    jPlay_UndoMultipleMovesButton.addActionListener((evt: ActionEvent) => Play_UndoRedoMoves(-10))
    jPlay_UndoMultipleMovesButton.addKeyListener(jCanvasKeyListener)
    jPlay_UndoMoveButton.setText("<")
    jPlay_UndoMoveButton.addActionListener((evt: ActionEvent) => Play_UndoRedoMoves(-1))
    jPlay_UndoMoveButton.addKeyListener(jCanvasKeyListener)
    jPlay_MovesTextField.setEditable(false)
    jPlay_MovesTextField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)
    jPlay_MovesTextField.setText("0")
    jPlay_MovesTextField.setFocusable(false)
    jPlay_MovesTextField.setMinimumSize(new Dimension(64, 32))
    jPlay_MovesTextField.setPreferredSize(new Dimension(64, 32))
    jPlay_RedoMoveButton.setText(">")
    jPlay_RedoMoveButton.addActionListener((evt: ActionEvent) => Play_UndoRedoMoves(1))
    jPlay_RedoMoveButton.addKeyListener(jCanvasKeyListener)
    jPlay_RedoMultipleMovesButton.setText(">>")
    jPlay_RedoMultipleMovesButton.setMargin(new Insets(2, 5, 3, 5))
    jPlay_RedoMultipleMovesButton.addActionListener((evt: ActionEvent) => Play_UndoRedoMoves(10))
    jPlay_RedoMultipleMovesButton.addKeyListener(jCanvasKeyListener)
    jPlay_ImportButton.setText("Import")
    jPlay_ImportButton.addActionListener((evt: ActionEvent) => Play_ImportMoves())
    jPlay_ImportButton.addKeyListener(jCanvasKeyListener)
    jPlay_SaveButton.setText("Save")
    jPlay_SaveButton.addActionListener((evt: ActionEvent) => Play_SaveMoves())
    jPlay_SaveButton.addKeyListener(jCanvasKeyListener)
    jPlay_TimeLabel.setText("Time")
    jPlay_TimeTextField.setEditable(false)
    jPlay_TimeTextField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)
    jPlay_TimeTextField.setText("00:00:00")
    jPlay_TimeTextField.setFocusable(false)
    jPlay_TimeTextField.setMinimumSize(new Dimension(64, 32))
    jPlay_TimeTextField.setPreferredSize(new Dimension(64, 32))
    jPlay_RestartButton.setText("Restart")
    jPlay_RestartButton.addActionListener((evt: ActionEvent) => SelectLevelActionListener(jPlay_LevelComboBoxModel, ActorKind.Player, true))
    jPlay_RestartButton.addKeyListener(jCanvasKeyListener)
    jPlay_SolveButton.setText("Solve")
    jPlay_SolveButton.addActionListener(evt => Play_SolveLevel())
    jPlay_InfoTextArea.setEditable(false)
    jPlay_InfoTextArea.setColumns(20)
    jPlay_InfoTextArea.setLineWrap(true)
    jPlay_InfoTextArea.setRows(5)
    jPlay_InfoTextArea.setTabSize(4)
    jPlay_InfoTextArea.setText("Keys\n------------------------\narrow keys - move player\nCtrl+z, Ctrl+y - undo, redo\n\n")
    jPlay_InfoTextArea.setWrapStyleWord(true)
    jPlay_InfoTextArea.setDoubleBuffered(true)
    jPlay_InfoTextArea.setFocusable(false)
    val jPlay_PanelLayout = new GroupLayout(jPlay_Panel)
    jPlay_Panel.setLayout(jPlay_PanelLayout)
    jPlay_PanelLayout.setHorizontalGroup(
      jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPlay_PanelLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPlay_PanelLayout.createSequentialGroup()
              .addGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(jPlay_MovesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MaxValue)
                .addComponent(jPlay_TimeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MaxValue))
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPlay_PanelLayout.createSequentialGroup()
                  .addComponent(jPlay_TimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jPlay_RestartButton)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jPlay_SolveButton)
                  .addContainerGap())
                .addGroup(jPlay_PanelLayout.createSequentialGroup()
                  .addComponent(jPlay_UndoMultipleMovesButton)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jPlay_UndoMoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jPlay_MovesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jPlay_RedoMoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jPlay_RedoMultipleMovesButton)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)
                  .addComponent(jPlay_ImportButton)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jPlay_SaveButton))))
            .addGroup(jPlay_PanelLayout.createSequentialGroup()
              .addComponent(jPlay_LevelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(jPlay_LevelComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue))))
        .addComponent(jPlay_InfoTextArea)
    )
    jPlay_PanelLayout.setVerticalGroup(
      jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPlay_PanelLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPlay_LevelLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPlay_LevelComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPlay_MovesLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPlay_UndoMultipleMovesButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPlay_UndoMoveButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPlay_MovesTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPlay_RedoMoveButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPlay_RedoMultipleMovesButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPlay_ImportButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPlay_SaveButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPlay_TimeLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPlay_TimeTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPlay_RestartButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPlay_SolveButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(jPlay_InfoTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MaxValue))
    )
    jTabbedPane.addTab("Play", jPlay_Panel)
    jCreate_LevelLabel.setText("Level")
    jCreate_LevelComboBoxModel.addElement(gameAssets.defaultLevel)
    jCreate_LevelComboBox.setModel(jCreate_LevelComboBoxModel)
    jCreate_LevelComboBox.addActionListener((evt: ActionEvent) => SelectLevelActionListener(jCreate_LevelComboBoxModel, ActorKind.Editor, false))
    jCreate_DeleteButton.setText("Delete")
    jCreate_DeleteButton.setToolTipText("")
    jCreate_DeleteButton.addActionListener((evt: ActionEvent) => Create_DeleteLevel())
    jCreate_DeleteButton.addKeyListener(jCanvasKeyListener)
    jCreate_ImportButton.setText("Import")
    jCreate_ImportButton.addActionListener((evt: ActionEvent) => Create_ImportLevel())
    jCreate_ImportButton.addKeyListener(jCanvasKeyListener)
    jCreate_NameLabel.setText("Name")
    jCreate_NameTextField.setMinimumSize(new Dimension(64, 32))
    jCreate_NameTextField.setPreferredSize(new Dimension(64, 32))
    jCreate_SaveButton.setText("Save")
    jCreate_SaveButton.addActionListener((evt: ActionEvent) => Create_SaveLevel())
    jCreate_SaveButton.addKeyListener(jCanvasKeyListener)
    jCreate_SetFloorButton.setIcon(gameAssets.tileIcon(Tile.Floor))
    jCreate_SetFloorButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetFloorButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetFloorButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetFloorButton.addActionListener((evt: ActionEvent) => Create_SetTile(Tile.Floor))
    jCreate_SetFloorButton.addKeyListener(jCanvasKeyListener)
    jCreate_SetWallButton.setIcon(gameAssets.tileIcon(Tile.Wall))
    jCreate_SetWallButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetWallButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetWallButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetWallButton.addActionListener((evt: ActionEvent) => Create_SetTile(Tile.Wall))
    jCreate_SetWallButton.addKeyListener(jCanvasKeyListener)
    jCreate_SetBoxButton.setIcon(gameAssets.tileIcon(Tile.Box))
    jCreate_SetBoxButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetBoxButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetBoxButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetBoxButton.addActionListener((evt: ActionEvent) => Create_SetTile(Tile.Box))
    jCreate_SetBoxButton.addKeyListener(jCanvasKeyListener)
    jCreate_SetGoalButton.setIcon(gameAssets.tileIcon(Tile.Goal))
    jCreate_SetGoalButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetGoalButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetGoalButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetGoalButton.addActionListener((evt: ActionEvent) => Create_SetTile(Tile.Goal))
    jCreate_SetGoalButton.addKeyListener(jCanvasKeyListener)
    jCreate_SetPlayerButton.setIcon(gameAssets.tileIcon(Tile.Player))
    jCreate_SetPlayerButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetPlayerButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetPlayerButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetPlayerButton.addActionListener((evt: ActionEvent) => Create_SetTile(Tile.Player))
    jCreate_SetPlayerButton.addKeyListener(jCanvasKeyListener)
    jCreate_SetBoxGoalButton.setIcon(gameAssets.tileIcon(Tile.BoxGoal))
    jCreate_SetBoxGoalButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetBoxGoalButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetBoxGoalButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetBoxGoalButton.addActionListener((evt: ActionEvent) => Create_SetTile(Tile.BoxGoal))
    jCreate_SetBoxGoalButton.addKeyListener(jCanvasKeyListener)
    jCreate_SetPlayerGoalButton.setIcon(gameAssets.tileIcon(Tile.PlayerGoal))
    jCreate_SetPlayerGoalButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetPlayerGoalButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetPlayerGoalButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetPlayerGoalButton.addActionListener((evt: ActionEvent) => Create_SetTile(Tile.PlayerGoal))
    jCreate_SetPlayerGoalButton.addKeyListener(jCanvasKeyListener)
    jCreate_CommandHistoryTextArea.setEditable(false)
    jCreate_CommandHistoryTextArea.setColumns(20)
    jCreate_CommandHistoryTextArea.setLineWrap(true)
    jCreate_CommandHistoryTextArea.setRows(5)
    jCreate_CommandHistoryTextArea.setTabSize(4)
    jCreate_CommandHistoryTextArea.setText(gameAssets.initialCommandHistory)
    jCreate_CommandHistoryTextArea.setWrapStyleWord(true)
    jCreate_CommandHistoryTextArea.setDoubleBuffered(true)
    jCreate_CommandHistoryScrollPane.setViewportView(jCreate_CommandHistoryTextArea)
    jCreate_CommandTextArea.setColumns(20)
    jCreate_CommandTextArea.setLineWrap(true)
    jCreate_CommandTextArea.setRows(5)
    jCreate_CommandTextArea.setTabSize(4)
    jCreate_CommandTextArea.setWrapStyleWord(true)
    jCreate_CommandTextArea.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR))
    jCreate_CommandTextArea.setDoubleBuffered(true)
    val keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager
    jCreate_CommandTextArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, keyboardFocusManager.getDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS))
    jCreate_CommandTextArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, keyboardFocusManager.getDefaultFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS))
    val jCreate_CommandTextAreaKeymap = jCreate_CommandTextArea.getKeymap
    jCreate_CommandTextAreaKeymap.addActionForKeyStroke(KeyCombo.Enter.keyStroke, new AbstractAction() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Create_CommandTextAreaEnterTyped()
      }
    })
    jCreate_CommandTextScrollPane.setViewportView(jCreate_CommandTextArea)
    val jCreate_PanelLayout = new GroupLayout(jCreate_Panel)
    jCreate_Panel.setLayout(jCreate_PanelLayout)
    jCreate_PanelLayout.setHorizontalGroup(
      jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jCreate_CommandHistoryScrollPane, javax.swing.GroupLayout.Alignment.TRAILING)
        .addComponent(jCreate_CommandTextScrollPane)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jCreate_PanelLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jCreate_PanelLayout.createSequentialGroup()
              .addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jCreate_PanelLayout.createSequentialGroup()
                  .addComponent(jCreate_LevelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jCreate_LevelComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue))
                .addGroup(jCreate_PanelLayout.createSequentialGroup()
                  .addComponent(jCreate_NameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jCreate_NameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)))
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jCreate_DeleteButton)
                .addComponent(jCreate_SaveButton)
                .addComponent(jCreate_ImportButton)))
            .addGroup(jCreate_PanelLayout.createSequentialGroup()
              .addGap(44, 44, 44)
              .addComponent(jCreate_SetFloorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(jCreate_SetWallButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(jCreate_SetBoxButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(jCreate_SetGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(jCreate_SetPlayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MaxValue)
              .addComponent(jCreate_SetBoxGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(jCreate_SetPlayerGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
    )
    jCreate_PanelLayout.setVerticalGroup(
      jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jCreate_PanelLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jCreate_PanelLayout.createSequentialGroup()
              .addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jCreate_LevelLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jCreate_LevelComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
              .addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jCreate_NameLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jCreate_NameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGap(38, 38, 38))
            .addGroup(jCreate_PanelLayout.createSequentialGroup()
              .addComponent(jCreate_DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
              .addComponent(jCreate_SaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
              .addComponent(jCreate_ImportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addGap(18, 18, 18)
          .addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jCreate_SetFloorButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jCreate_SetWallButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jCreate_SetBoxButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jCreate_SetGoalButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jCreate_SetPlayerButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jCreate_SetBoxGoalButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jCreate_SetPlayerGoalButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGap(18, 18, 18)
          .addComponent(jCreate_CommandHistoryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MaxValue)
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(jCreate_CommandTextScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
    )
    jTabbedPane.addTab("Create", jCreate_Panel)
    jCanvasSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL)
    val jCanvasLayout = new GroupLayout(jCanvas)
    jCanvas.setLayout(jCanvasLayout)
    jCanvasLayout.setHorizontalGroup(jCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 563, Short.MaxValue))
    jCanvasLayout.setVerticalGroup(jCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MaxValue))
    val layout = new GroupLayout(this)
    this.setLayout(layout)
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
          .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(jCanvasSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(jCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue))
    )
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jCanvas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)
        .addComponent(jTabbedPane)
        .addComponent(jCanvasSeparator)
    )

    previousDirectory = FileUtil.getFile(gameAssets.rootPath)
  }

  private def ChangeTab(): Unit = {
    // TODO: show modal if there is unfinished work in the create tab / if the player made some moves on the map

    var actorKind = ActorKind.Player

    gameState.synchronized {
      actorKind = jTabbedPane.getSelectedIndex match {
        case 0 => ActorKind.Player
        case 1 => ActorKind.Editor
        case _ => gameState.actorKind
      }
      gameState.setActiveActor(actorKind)
      gameState.setLevel(gameAssets.defaultLevel, new Grid(gameAssets.defaultGridSize, Tile.Floor))
    }
    jCanvas.repaint()

    actorKind match {
      case ActorKind.Player =>
        SwingUtilities.invokeLater(() => PopulateLevelComboBox(jPlay_LevelComboBoxModel))
        jPlay_MovesTextField.setText("0")
        jPlay_TimeTextField.setText(gameAssets.zeroTimeString)
      case ActorKind.Editor =>
        SwingUtilities.invokeLater(() => {
          jCreate_CommandHistoryTextArea.setText(gameAssets.initialCommandHistory)
          PopulateLevelComboBox(jCreate_LevelComboBoxModel)
          val defaultLevel = jCreate_LevelComboBoxModel.getElementAt(0)
          jCreate_NameTextField.setText(defaultLevel.name)
        })
    }
    gameTimer.stop()
  }

  private def ClearLevelComboBox(model: DefaultComboBoxModel[GameFile]): Unit = {
    val firstElem: GameFile = model.getElementAt(0)
    model.removeAllElements()
    model.addElement(firstElem)
  }

  private def PopulateLevelComboBox(model: DefaultComboBoxModel[GameFile]): Unit = {
    ClearLevelComboBox(model)

    val pathList = FileUtil.listFilesInDirectory(gameAssets.mapsRootPath)
    if (pathList.isEmpty) {
      return
    }

    for (path <- pathList) {
      val levelName = FileUtil.getFileNameNoExtension(path)
      model.addElement(GameFile(levelName, path))
    }
  }

  private def SelectLevelActionListener(model: DefaultComboBoxModel[GameFile], actorKind: ActorKind, force: Boolean): Unit = {
    var level = Option(model.getSelectedItem.asInstanceOf[GameFile]).getOrElse(gameAssets.defaultLevel)
    gameState.synchronized {
      // TODO: when we leave the combo box, there is no selection
      if (!force && gameState.level == level) {
        return
      }

      var grid = readLevelFromFile(level)
      if (grid.isEmpty) {
        level = gameAssets.defaultLevel
        grid = Option(new Grid(gameAssets.defaultGridSize, Tile.Floor))
      }

      gameState.setLevel(level, grid.get)
    }
    jCanvas.repaint()

    actorKind match {
      case ActorKind.Player =>
        jPlay_MovesTextField.setText("0")
        gameTimer.restart()
      case ActorKind.Editor =>
        jCreate_NameTextField.setText(level.name)
        gameTimer.stop()
    }
  }

  private def readLevelFromFile(level: GameFile): Option[Grid] = {
    var grid: Option[Grid] = Option.empty

    if (level != null && level != gameAssets.defaultLevel) {
      val fileContents = FileUtil.readFromFile(level.path.toString)
      if (fileContents != null) {
        val gridSerializer = new GridSerializer()
        val deserializedGrid = gridSerializer.toObject(fileContents.get)
        if (deserializedGrid.isDefined) {
          grid = Option(deserializedGrid.get)
        }
      }
    }

    grid
  }

  private def Play_UndoRedoMoves(moves: Int): Unit = {
    var completedMoves = 0
    var moveNumber = 0
    gameState.synchronized {
      completedMoves = gameState.actor.undoRedo(moves)
      moveNumber = gameState.actor.moveNumber
    }
    if (completedMoves > 0) {
      jPlay_MovesTextField.setText(moveNumber.toString)
      jCanvas.repaint()
    }
  }

  private def Play_ImportMoves(): Unit = {
    val (filePath, directory) = FileUtil.getUserSelectedPath(FileActionKind.Open, FileKind.TextFile, previousDirectory)
    if (filePath.isEmpty) {
      return
    }
    previousDirectory = directory

    val fileContents = FileUtil.readFromFile(filePath.get)
    if (fileContents.isEmpty) {
      return
    }

    val playerActionStackSerializer = new PlayerActionStackSerializer(gameAssets.defaultPlayerActionWrap)
    val deserializedMoves = playerActionStackSerializer.toObject(fileContents.get)
    if (deserializedMoves.isEmpty) {
      return
    }
    val moves = deserializedMoves.get
    moves.undoAllActions()

    gameState.synchronized {
      // Reset board.
      val grid = readLevelFromFile(gameState.level)
      if (grid.isEmpty) {
        return
      }
      gameState.setLevel(gameState.level, grid.get)

      // Do all moves until one fails.
      val player = gameState.player
      while (player.move(moves.redoAction())) {}
      player.undoRedo(-player.moveNumber)
    }

    jCanvas.repaint()
  }

  private def Play_SaveMoves(): Unit = {
    gameState.synchronized {
      if (gameState.player.moveNumber == 0) {
        return
      }
    }

    val (filePath, directory) = FileUtil.getUserSelectedPath(FileActionKind.Save, FileKind.MovesTextFile, previousDirectory)
    if (filePath.isEmpty) {
      return
    }
    previousDirectory = directory

    val playerActionStackSerializer = new PlayerActionStackSerializer(gameAssets.defaultPlayerActionWrap)
    val content = gameState.synchronized {
      playerActionStackSerializer.toString(gameState.playerUndoStack)
    }
    if (content.isEmpty) {
      return
    }

    if (!FileUtil.writeToFile(filePath.get, content)) {
      return
    }
  }

  private def Play_SolveLevel(): Unit = {
    // TODO:
  }

  private def CanvasPanelKeyPressed(evt: KeyEvent): Unit = {
    val keyStroke = KeyStroke.getKeyStroke(evt.getKeyCode, evt.getModifiersEx)
    val keyCombo = KeyCombo.toKeyCombo(keyStroke)
    var canvasChanged = false
    var moveNumber = 0

    gameState.synchronized {
      keyCombo match {
        case KeyCombo.Up | KeyCombo.Down | KeyCombo.Left | KeyCombo.Right =>
          val playerAction = keyCombo match {
            case KeyCombo.Up => PlayerAction.PlayerUp
            case KeyCombo.Down => PlayerAction.PlayerDown
            case KeyCombo.Left => PlayerAction.PlayerLeft
            case KeyCombo.Right => PlayerAction.PlayerRight
            case _ => PlayerAction.None
          }
          canvasChanged = gameState.actor.move(playerAction)
        case KeyCombo.CtrlZ =>
          canvasChanged = gameState.actor.undo()
        case KeyCombo.CtrlY =>
          canvasChanged = gameState.actor.redo()
        case _ =>
      }
      moveNumber = gameState.actor.moveNumber
    }
    jPlay_MovesTextField.setText(moveNumber.toString)
    if (canvasChanged) {
      jCanvas.repaint()
    }
  }

  private def Create_DeleteLevel(): Unit = {
    // TODO: show modal to user to confirm

    val level = gameState.synchronized {
      val level = gameState.level
      gameState.setLevel(gameAssets.defaultLevel, new Grid(gameAssets.defaultGridSize, Tile.Floor))
      level
    }
    if (level != gameAssets.defaultLevel) {
      FileUtil.deleteFile(level.path.toString)
    }
    jCanvas.repaint()
    SwingUtilities.invokeLater(() => PopulateLevelComboBox(jCreate_LevelComboBoxModel))
  }

  private def Create_ImportLevel(): Unit = {
    val (filePath, directory) = FileUtil.getUserSelectedPath(FileActionKind.Open, FileKind.TextFile, previousDirectory)
    if (filePath.isEmpty) {
      return
    }
    previousDirectory = directory

    val fileContents = FileUtil.readFromFile(filePath.get)
    if (fileContents.isEmpty) {
      return
    }

    val gridSerializer = new GridSerializer()
    val deserializedGrid = gridSerializer.toObject(fileContents.get)
    if (deserializedGrid.isEmpty) {
      return
    }
    val grid = deserializedGrid.get
    val defaultLevel = jCreate_LevelComboBoxModel.getElementAt(0)

    // Order is important here.
    jCreate_LevelComboBoxModel.setSelectedItem(defaultLevel)
    gameState.synchronized {
      gameState.setLevel(defaultLevel, grid)
    }
    jCanvas.repaint()
    jCreate_NameTextField.setText(defaultLevel.name)
  }

  private def Create_SaveLevel(): Unit = {
    val text = jCreate_NameTextField.getText
    if (text.isBlank) {
      return
    }
    val newLevelName = text.trim
    if (newLevelName.isBlank || newLevelName == gameAssets.defaultLevel.name) {
      return
    }
    val newLevelPath = FileUtil.getPath(FileUtil.ensureExtension(s"${gameAssets.mapsRootPath}/$newLevelName", FileKind.TextFile))
    if (newLevelPath.isEmpty) {
      return
    }

    val oldLevel = Option(jCreate_LevelComboBoxModel.getSelectedItem.asInstanceOf[GameFile]).getOrElse(gameAssets.defaultLevel)
    val newLevel = GameFile(newLevelName, newLevelPath.get)
    val level = if (oldLevel != gameAssets.defaultLevel) oldLevel else newLevel

    if (oldLevel != gameAssets.defaultLevel && oldLevel != newLevel) {
      // Only move game file if it already exists.
      if (!FileUtil.moveFile(oldLevel.path.toString, newLevel.path.toString)) {
        return
      }
    }

    val gridSerializer = new GridSerializer()
    val content = gameState.synchronized {
      gridSerializer.toString(gameState.grid)
    }
    if (content.isBlank) {
      return
    }

    if (!FileUtil.writeToFile(newLevel.path.toString, content)) {
      return
    }

    SwingUtilities.invokeLater(() => {
      PopulateLevelComboBox(jCreate_LevelComboBoxModel)
      jCreate_LevelComboBoxModel.setSelectedItem(newLevel)
      jCreate_NameTextField.setText(newLevel.name)
    })
  }

  private def Create_SetTile(tile: Tile): Unit = {
    gameState.synchronized {
      val editor = gameState.editor
      if (!editor.checkSetTile(editor.position, tile)) {
        return
      }
      editor.apply(editor.setTileUnchecked(editor.position, tile))
    }
    jCanvas.repaint()
  }

  // TODO: add up/down key handlers that bring up command history
  private def Create_CommandTextAreaEnterTyped(): Unit = {
    try {
      val text = jCreate_CommandTextArea.getText()
      jCreate_CommandTextArea.setText("")
      if (text.isEmpty) {
        return
      }

      val command = commandParser.parse(text)
      if (command == CmdNone) {
        return
      }

      val message = gameState.synchronized {
        val msg = command.sema(gameState.editor)
        if (msg.isEmpty) {
          gameState.editor.applyCmd(command)
        }
        msg.getOrElse("")
      }

      val scrollBar = jCreate_CommandHistoryScrollPane.getVerticalScrollBar
      val isScrollBarAtBottom = scrollBar.getValue + scrollBar.getVisibleAmount == scrollBar.getMaximum
      jCreate_CommandHistoryTextArea.append("\n")
      jCreate_CommandHistoryTextArea.append(text)
      if (!message.isBlank) {
        jCreate_CommandHistoryTextArea.append("ERROR: ")
        jCreate_CommandHistoryTextArea.append(message)
      }
      if (isScrollBarAtBottom) {
        SwingUtilities.invokeLater(() => scrollBar.setValue(scrollBar.getMaximum - scrollBar.getVisibleAmount))
      }
    } catch {
      case ex: NullPointerException =>
    }
  }
}
