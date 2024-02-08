import java.awt.event.{ActionEvent, ActionListener, KeyAdapter, KeyEvent}
import java.awt.{Dimension, Insets}
import javax.swing.*
import javax.swing.event.{ChangeEvent, ChangeListener}

final class MainPanel(private val jCanvas: Canvas,
                      private val gameAssets: GameAssets,
                      private val gameState: GameState) extends JPanel {
  private val jTabbedPane: JTabbedPane = new JTabbedPane

  private val jPlay_Panel: JPanel = new JPanel
  private val jPlay_LevelLabel: JLabel = new JLabel
  private val jPlay_LevelComboBox: JComboBox[String] = new JComboBox[String]
  private val jPlay_MovesLabel: JLabel = new JLabel
  private val jPlay_UndoMultipleMovesButton: JButton = new JButton
  private val jPlay_UndoMoveButton: JButton = new JButton
  private val jPlay_MovesTextField: JTextField = new JTextField
  private val jPlay_RedoMoveButton: JButton = new JButton
  private val jPlay_RedoMultipleMovesButton: JButton = new JButton
  private val jPlay_ImportButton: JButton = new JButton
  private val jPlay_SolveButton: JButton = new JButton
  private val jPlay_TimeLabel: JLabel = new JLabel
  private val jPlay_TimeTextField: JTextField = new JTextField
  private val jPlay_RestartButton: JButton = new JButton
  private val jPlay_InfoTextArea: JTextArea = new JTextArea

  private val jCreate_Panel: JPanel = new JPanel
  private val jCreate_LevelLabel: JLabel = new JLabel
  private val jCreate_LevelComboBox: JComboBox[String] = new JComboBox[String]
  private val jCreate_DeleteButton: JButton = new JButton
  private val jCreate_StatusLabel: JLabel = new JLabel
  private val jCreate_StatusTextField: JTextField = new JTextField
  private val jCreate_ImportButton: JButton = new JButton
  private val jCreate_NameLabel: JLabel = new JLabel
  private val jCreate_NameTextField: JTextField = new JTextField
  private val jCreate_SaveButton: JButton = new JButton
  private val jCreate_SetWallButton: JButton = new JButton
  private val jCreate_SetFloorButton: JButton = new JButton
  private val jCreate_SetBoxButton: JButton = new JButton
  private val jCreate_SetGoalButton: JButton = new JButton
  private val jCreate_SetBoxGoalButton: JButton = new JButton
  private val jCreate_SetPlayerButton: JButton = new JButton
  private val jCreate_CommandHistoryScrollPane: JScrollPane = new JScrollPane
  private val jCreate_CommandHistoryTextArea: JTextArea = new JTextArea
  private val jCreate_CommandScrollPane: JScrollPane = new JScrollPane
  private val jCreate_CommandTextArea: JTextArea = new JTextArea

  private val jCanvasSeparator: JSeparator = new JSeparator
  /*private val jCanvasPanel: JCanvas = new Canvas(...)*/

  private val gameTimer: GameTimer = new GameTimer(
    () => jPlay_TimeTextField.setText("00:00:00"),
    () => jPlay_TimeTextField.setText(gameTimer.currentTime()),
    () => jPlay_TimeTextField.setText("00:00:00")
  )

  configure()

  private def configure(): Unit = {
    jTabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT)
    jTabbedPane.addChangeListener((evt: ChangeEvent) => TabbedPaneStateChanged(evt))
    jPlay_LevelLabel.setText("Level")
    jPlay_LevelComboBox.setModel(new DefaultComboBoxModel[String](Array[String]("Level 01 - Whale Island")))
    jPlay_LevelComboBox.addActionListener((evt: ActionEvent) => Play_LevelComboBoxActionPerformed(evt))
    jPlay_MovesLabel.setText("Moves")
    jPlay_UndoMultipleMovesButton.setText("<<")
    jPlay_UndoMultipleMovesButton.setMargin(new Insets(2, 5, 3, 5))
    jPlay_UndoMultipleMovesButton.addActionListener((evt: ActionEvent) => Play_UndoRedoMovesButtonsActionPerformed(evt, -10))
    jPlay_UndoMoveButton.setText("<")
    jPlay_UndoMoveButton.addActionListener((evt: ActionEvent) => Play_UndoRedoMovesButtonsActionPerformed(evt, -1))
    jPlay_MovesTextField.setEditable(false)
    jPlay_MovesTextField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)
    jPlay_MovesTextField.setText("0")
    jPlay_MovesTextField.setFocusable(false)
    jPlay_MovesTextField.setMinimumSize(new Dimension(64, 32))
    jPlay_MovesTextField.setPreferredSize(new Dimension(64, 32))
    jPlay_RedoMoveButton.setText(">")
    jPlay_RedoMoveButton.addActionListener((evt: ActionEvent) => Play_UndoRedoMovesButtonsActionPerformed(evt, 1))
    jPlay_RedoMultipleMovesButton.setText(">>")
    jPlay_RedoMultipleMovesButton.setMargin(new Insets(2, 5, 3, 5))
    jPlay_RedoMultipleMovesButton.addActionListener((evt: ActionEvent) => Play_UndoRedoMovesButtonsActionPerformed(evt, 10))
    jPlay_ImportButton.setText("Import")
    jPlay_ImportButton.addActionListener((evt: ActionEvent) => Play_ImportButtonActionPerformed(evt))
    jPlay_SolveButton.setText("Solve")
    jPlay_SolveButton.addActionListener((evt: ActionEvent) => Play_SolveButtonActionPerformed(evt))
    jPlay_TimeLabel.setText("Time")
    jPlay_TimeTextField.setEditable(false)
    jPlay_TimeTextField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)
    jPlay_TimeTextField.setText("00:00:00")
    jPlay_TimeTextField.setFocusable(false)
    jPlay_TimeTextField.setMinimumSize(new Dimension(64, 32))
    jPlay_TimeTextField.setPreferredSize(new Dimension(64, 32))
    jPlay_RestartButton.setText("Restart")
    jPlay_RestartButton.addActionListener((evt: ActionEvent) => Play_RestartButtonActionPerformed(evt))
    jPlay_InfoTextArea.setEditable(false)
    jPlay_InfoTextArea.setColumns(20)
    jCreate_CommandTextArea.setLineWrap(true)
    jCreate_CommandTextArea.setWrapStyleWord(true)
    jPlay_InfoTextArea.setRows(5)
    jPlay_InfoTextArea.setTabSize(4)
    jPlay_InfoTextArea.setText("Keys\n------------------------\narrow keys - move player\nCtrl+z, Ctrl+y - undo, redo\n\n")
    jPlay_InfoTextArea.setFocusable(false)
    val Play_PanelLayout = new GroupLayout(jPlay_Panel)
    jPlay_Panel.setLayout(Play_PanelLayout)
    Play_PanelLayout.setHorizontalGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(Play_PanelLayout.createSequentialGroup.addContainerGap().addGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(Play_PanelLayout.createSequentialGroup.addComponent(jPlay_LevelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPlay_LevelComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)).addGroup(Play_PanelLayout.createSequentialGroup.addGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false).addComponent(jPlay_MovesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue).addComponent(jPlay_TimeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MaxValue)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(Play_PanelLayout.createSequentialGroup.addComponent(jPlay_TimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPlay_RestartButton)).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Play_PanelLayout.createSequentialGroup.addComponent(jPlay_UndoMultipleMovesButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPlay_UndoMoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPlay_MovesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPlay_RedoMoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPlay_RedoMultipleMovesButton))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPlay_ImportButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue).addComponent(jPlay_SolveButton))).addContainerGap()).addComponent(jPlay_InfoTextArea))
    Play_PanelLayout.setVerticalGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(Play_PanelLayout.createSequentialGroup.addContainerGap().addGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPlay_LevelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_LevelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPlay_MovesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_UndoMultipleMovesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_UndoMoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_MovesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_RedoMoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_RedoMultipleMovesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jPlay_ImportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_SolveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPlay_TimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_TimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_RestartButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jPlay_InfoTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MaxValue)))
    jTabbedPane.addTab("Play", jPlay_Panel)
    jCreate_LevelLabel.setText("Level")
    jCreate_LevelComboBox.setModel(new DefaultComboBoxModel[String](Array[String]("----- new level -----")))
    jCreate_LevelComboBox.addActionListener((evt: ActionEvent) => Create_LevelComboBoxActionPerformed(evt))
    jCreate_DeleteButton.setText("Delete")
    jCreate_DeleteButton.setToolTipText("")
    jCreate_DeleteButton.addActionListener((evt: ActionEvent) => Create_DeleteButtonActionPerformed(evt))
    jCreate_StatusLabel.setText("Status")
    jCreate_StatusTextField.setEditable(false)
    jCreate_StatusTextField.setFocusable(false)
    jCreate_StatusTextField.setMinimumSize(new Dimension(64, 32))
    jCreate_StatusTextField.setPreferredSize(new Dimension(64, 32))
    jCreate_ImportButton.setText("Import")
    jCreate_ImportButton.addActionListener((evt: ActionEvent) => Create_ImportButtonActionPerformed(evt))
    jCreate_NameLabel.setText("Name")
    jCreate_NameTextField.setMinimumSize(new Dimension(64, 32))
    jCreate_NameTextField.setPreferredSize(new Dimension(64, 32))
    jCreate_SaveButton.setText("Save")
    jCreate_SaveButton.addActionListener((evt: ActionEvent) => Create_SaveButtonActionPerformed(evt))
    jCreate_SetWallButton.setIcon(gameAssets.tileIcon(Tile.Wall))

    jCreate_SetWallButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetWallButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetWallButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetWallButton.addActionListener((evt: ActionEvent) => Create_SetTileButtonsActionPerformed(evt, Tile.Wall))
    jCreate_SetFloorButton.setIcon(gameAssets.tileIcon(Tile.FloorInside))

    jCreate_SetFloorButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetFloorButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetFloorButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetFloorButton.addActionListener((evt: ActionEvent) => Create_SetTileButtonsActionPerformed(evt, Tile.FloorOutside))
    jCreate_SetBoxButton.setIcon(gameAssets.tileIcon(Tile.Box))

    jCreate_SetBoxButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetBoxButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetBoxButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetBoxButton.addActionListener((evt: ActionEvent) => Create_SetTileButtonsActionPerformed(evt, Tile.Box))
    jCreate_SetGoalButton.setIcon(gameAssets.tileIcon(Tile.Goal))

    jCreate_SetGoalButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetGoalButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetGoalButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetGoalButton.addActionListener((evt: ActionEvent) => Create_SetTileButtonsActionPerformed(evt, Tile.Goal))
    jCreate_SetBoxGoalButton.setIcon(gameAssets.tileIcon(Tile.BoxGoal))

    jCreate_SetBoxGoalButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetBoxGoalButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetBoxGoalButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetBoxGoalButton.addActionListener((evt: ActionEvent) => Create_SetTileButtonsActionPerformed(evt, Tile.BoxGoal))
    jCreate_SetPlayerButton.setIcon(gameAssets.tileIcon(Tile.Player))

    jCreate_SetPlayerButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetPlayerButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetPlayerButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetPlayerButton.addActionListener((evt: ActionEvent) => Create_SetTileButtonsActionPerformed(evt, Tile.Player))
    jCreate_CommandHistoryTextArea.setEditable(false)
    jCreate_CommandHistoryTextArea.setColumns(20)
    jCreate_CommandTextArea.setLineWrap(true)
    jCreate_CommandTextArea.setWrapStyleWord(true)
    jCreate_CommandHistoryTextArea.setRows(5)
    jCreate_CommandHistoryTextArea.setTabSize(4)
    jCreate_CommandHistoryTextArea.setText("// Keys\n// ------------------------\n// Ctrl+z, Ctrl+y - undo, redo\n// enter - input command\n// arrow keys - move selection\n\n\n// Predefined commands\n// ------------------------\n// Parameters:\n//   @  - currently selected location (expands to coordinates)\n//   dir - {row, col}.\n//   i    - index of rows/cols.\n//   is   - index of the space between rows/cols.\n//   n   - number of rows/cols.\n//   t    - {- empty, # wall, X box, O Goal, B BoxOnGoal, P Player, R PlayerOnGoal}.\n//   x:y - position.\n\ntiles_extend(dir is n)\ntiles_delete(dir i n)\ntile_set(t x:y)\n\ngoal_invert()\nwall_minimize(x:y)\nwall_filter(x:y n)      // n: number of tiles\nwall_fractalize(x:y)\nlevel_validate()\n\n// Commands are executed in order, until one fails its check.\n// Angular brackets make a transaction. For example:\ndef foo(v1 v2) = tile_set(wall v1) [wall_fractalize(v2) level_validate()]\nundef foo\n\n\nUser commands\n------------------")
    jCreate_CommandHistoryTextArea.setSelectionEnd(0)
    jCreate_CommandHistoryTextArea.setSelectionStart(0)
    jCreate_CommandHistoryScrollPane.setViewportView(jCreate_CommandHistoryTextArea)
    jCreate_CommandTextArea.setColumns(20)
    jCreate_CommandTextArea.setLineWrap(true)
    jCreate_CommandTextArea.setWrapStyleWord(true)
    jCreate_CommandTextArea.setRows(5)
    jCreate_CommandTextArea.setTabSize(4)
    jCreate_CommandTextArea.addKeyListener(new KeyAdapter() {
      override def keyTyped(evt: KeyEvent): Unit = {
        Create_CommandTextAreaKeyTyped(evt)
      }
    })
    jCreate_CommandScrollPane.setViewportView(jCreate_CommandTextArea)
    val Create_PanelLayout = new GroupLayout(jCreate_Panel)
    jCreate_Panel.setLayout(Create_PanelLayout)
    Create_PanelLayout.setHorizontalGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jCreate_CommandHistoryScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MaxValue).addComponent(jCreate_CommandScrollPane).addGroup(Create_PanelLayout.createSequentialGroup.addContainerGap().addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(Create_PanelLayout.createSequentialGroup.addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Create_PanelLayout.createSequentialGroup.addComponent(jCreate_LevelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_LevelComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)).addGroup(Create_PanelLayout.createSequentialGroup.addComponent(jCreate_NameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_NameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)).addGroup(Create_PanelLayout.createSequentialGroup.addComponent(jCreate_StatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_StatusTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(jCreate_SaveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue).addComponent(jCreate_DeleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MaxValue).addComponent(jCreate_ImportButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)).addContainerGap()).addGroup(Create_PanelLayout.createSequentialGroup.addGap(44, 44, 44).addComponent(jCreate_SetWallButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_SetFloorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_SetBoxButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addComponent(jCreate_SetGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_SetBoxGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_SetPlayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)))))
    Create_PanelLayout.setVerticalGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Create_PanelLayout.createSequentialGroup.addContainerGap().addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jCreate_LevelComboBox).addComponent(jCreate_DeleteButton)).addComponent(jCreate_LevelLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jCreate_ImportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_StatusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_StatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(12, 12, 12).addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jCreate_SaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_NameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jCreate_SetFloorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_SetWallButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_SetBoxButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_SetGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_SetBoxGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_SetPlayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addComponent(jCreate_CommandHistoryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MaxValue).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_CommandScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
    jTabbedPane.addTab("Create", jCreate_Panel)
    jCanvasSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL)
    jCanvas.addKeyListener(new KeyAdapter() {
      override def keyPressed(evt: KeyEvent): Unit = {
        CanvasPanelKeyPressed(evt)
      }
    })
    val CanvasPanelLayout = new GroupLayout(jCanvas)
    jCanvas.setLayout(CanvasPanelLayout)
    CanvasPanelLayout.setHorizontalGroup(CanvasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 573, Short.MaxValue))
    CanvasPanelLayout.setVerticalGroup(CanvasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MaxValue))
    val layout = new GroupLayout(this)
    this.setLayout(layout)
    layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup.addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCanvasSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)))
    layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jCanvas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue).addComponent(jTabbedPane).addComponent(jCanvasSeparator))


    // TODO
    val jTabbedPaneInputMap = jTabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    jTabbedPaneInputMap.put(KeyCombo.Left.keyStroke, null)
    jTabbedPaneInputMap.put(KeyCombo.Right.keyStroke, null)

    val jPlay_PanelInputMap = jPlay_Panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    jPlay_PanelInputMap.put(KeyCombo.Left.keyStroke, null)
    jPlay_PanelInputMap.put(KeyCombo.Right.keyStroke, null)

    val jCreate_PanelInputMap = jCreate_Panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    jCreate_PanelInputMap.put(KeyCombo.Left.keyStroke, null)
    jCreate_PanelInputMap.put(KeyCombo.Right.keyStroke, null)
  }

  private def TabbedPaneStateChanged(evt: ChangeEvent): Unit = {
    jTabbedPane.getSelectedIndex match {
      case 0 => {
        gameTimer.start()
      }
      case 1 => {
        gameTimer.stop()
      }
      case _ =>
    }
  }

  private def Play_LevelComboBoxActionPerformed(evt: ActionEvent): Unit = {
    // TODO
  }

  private def Play_UndoRedoMovesButtonsActionPerformed(evt: ActionEvent, moves: Int): Unit = {
    gameState.synchronized {
      val completedMoves = gameState.player.undoRedo(moves)
      if (completedMoves > 0) {
        jPlay_MovesTextField.setText(gameState.player.moveNumber.toString)
        jCanvas.repaint()
      }
    }
  }

  private def Play_ImportButtonActionPerformed(evt: ActionEvent): Unit = {
    // TODO
  }

  private def Play_SolveButtonActionPerformed(evt: ActionEvent): Unit = {
    // TODO
  }

  private def Play_RestartButtonActionPerformed(evt: ActionEvent): Unit = {
    gameState.synchronized {
      // TODO
      gameState.player.setPositionUnchecked(5, 6)
      gameTimer.restart()
    }
  }

  private def CanvasPanelKeyPressed(evt: KeyEvent): Unit = {
    val keyStroke = KeyStroke.getKeyStroke(evt.getKeyCode, evt.getModifiersEx)
    var canvasChanged = false

    // TODO
    System.out.println(keyStroke)

    if (KeyCombo.Up.keyStroke.equals(keyStroke)) gameState.synchronized {
      canvasChanged = gameState.player.move(PlayerAction.PlayerUp)
    }
    else if (KeyCombo.Down.keyStroke.equals(keyStroke)) gameState.synchronized {
      canvasChanged = gameState.player.move(PlayerAction.PlayerDown)
    }
    else if (KeyCombo.Left.keyStroke.equals(keyStroke)) gameState.synchronized {
      canvasChanged = gameState.player.move(PlayerAction.PlayerLeft)
    }
    else if (KeyCombo.Right.keyStroke.equals(keyStroke)) gameState.synchronized {
      canvasChanged = gameState.player.move(PlayerAction.PlayerRight)
    }
    else if (KeyCombo.CtrlZ.keyStroke.equals(keyStroke)) gameState.synchronized {
      canvasChanged = gameState.player.undo()
    }
    else if (KeyCombo.CtrlY.keyStroke.equals(keyStroke)) gameState.synchronized {
      canvasChanged = gameState.player.redo()
    }
    else return

    if (canvasChanged) {
      jCanvas.repaint()
    }
  }

  private def Create_LevelComboBoxActionPerformed(evt: ActionEvent): Unit = {
    // TODO
  }

  private def Create_DeleteButtonActionPerformed(evt: ActionEvent): Unit = {
    // TODO
  }

  private def Create_ImportButtonActionPerformed(evt: ActionEvent): Unit = {
    // TODO
  }

  private def Create_SaveButtonActionPerformed(evt: ActionEvent): Unit = {
    // TODO
  }

  private def Create_SetTileButtonsActionPerformed(evt: ActionEvent, tile: Tile): Unit = {
    // TODO: add editor undo action
    // TODO: recalculate if the floor is outside or inside
    gameState.synchronized {
      gameState.grid.setTile(gameState.player.position.i, gameState.player.position.j, tile)
    }
    jCanvas.repaint()
  }

  private def Create_CommandTextAreaKeyTyped(evt: KeyEvent): Unit = {
    // TODO
  }
}
