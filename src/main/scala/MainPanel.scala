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

  private val gameTimer: GameTimer = new GameTimer(
    () => jPlay_TimeTextField.setText("00:00:00"),
    () => jPlay_TimeTextField.setText(gameTimer.currentTime()),
    () => jPlay_TimeTextField.setText("00:00:00")
  )

  configure()

  private def configure(): Unit = {
    jTabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT)
    jTabbedPane.addChangeListener((evt: ChangeEvent) => TabbedPaneStateChanged(evt))
    val jCanvasKeyListener = new KeyAdapter() {
      override def keyPressed(evt: KeyEvent): Unit = {
        CanvasPanelKeyPressed(evt)
      }
    }
    jPlay_LevelLabel.setText("Level")
    jPlay_LevelComboBox.setModel(new DefaultComboBoxModel[String](Array[String]("Level 01 - Whale Island")))
    jPlay_LevelComboBox.addActionListener((evt: ActionEvent) => Play_LevelComboBoxActionPerformed(evt))
    jPlay_LevelComboBox.addKeyListener(jCanvasKeyListener)
    jPlay_MovesLabel.setText("Moves")
    jPlay_UndoMultipleMovesButton.setText("<<")
    jPlay_UndoMultipleMovesButton.setMargin(new Insets(2, 5, 3, 5))
    jPlay_UndoMultipleMovesButton.addActionListener((evt: ActionEvent) => Play_UndoRedoMovesButtonsActionPerformed(evt, -10))
    jPlay_UndoMultipleMovesButton.addKeyListener(jCanvasKeyListener)
    jPlay_UndoMoveButton.setText("<")
    jPlay_UndoMoveButton.addActionListener((evt: ActionEvent) => Play_UndoRedoMovesButtonsActionPerformed(evt, -1))
    jPlay_UndoMoveButton.addKeyListener(jCanvasKeyListener)
    jPlay_MovesTextField.setEditable(false)
    jPlay_MovesTextField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)
    jPlay_MovesTextField.setText("0")
    jPlay_MovesTextField.setFocusable(false)
    jPlay_MovesTextField.setMinimumSize(new Dimension(64, 32))
    jPlay_MovesTextField.setPreferredSize(new Dimension(64, 32))
    jPlay_RedoMoveButton.setText(">")
    jPlay_RedoMoveButton.addActionListener((evt: ActionEvent) => Play_UndoRedoMovesButtonsActionPerformed(evt, 1))
    jPlay_RedoMoveButton.addKeyListener(jCanvasKeyListener)
    jPlay_RedoMultipleMovesButton.setText(">>")
    jPlay_RedoMultipleMovesButton.setMargin(new Insets(2, 5, 3, 5))
    jPlay_RedoMultipleMovesButton.addActionListener((evt: ActionEvent) => Play_UndoRedoMovesButtonsActionPerformed(evt, 10))
    jPlay_RedoMultipleMovesButton.addKeyListener(jCanvasKeyListener)
    jPlay_ImportButton.setText("Import")
    jPlay_ImportButton.addActionListener((evt: ActionEvent) => Play_ImportButtonActionPerformed(evt))
    jPlay_ImportButton.addKeyListener(jCanvasKeyListener)
    jPlay_SolveButton.setText("Solve")
    jPlay_SolveButton.addActionListener((evt: ActionEvent) => Play_SolveButtonActionPerformed(evt))
    jPlay_SolveButton.addKeyListener(jCanvasKeyListener)
    jPlay_TimeLabel.setText("Time")
    jPlay_TimeTextField.setEditable(false)
    jPlay_TimeTextField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)
    jPlay_TimeTextField.setText("00:00:00")
    jPlay_TimeTextField.setFocusable(false)
    jPlay_TimeTextField.setMinimumSize(new Dimension(64, 32))
    jPlay_TimeTextField.setPreferredSize(new Dimension(64, 32))
    jPlay_RestartButton.setText("Restart")
    jPlay_RestartButton.addActionListener((evt: ActionEvent) => Play_RestartButtonActionPerformed(evt))
    jPlay_RestartButton.addKeyListener(jCanvasKeyListener)
    jPlay_InfoTextArea.setEditable(false)
    jPlay_InfoTextArea.setColumns(20);
    jPlay_InfoTextArea.setLineWrap(true);
    jPlay_InfoTextArea.setRows(5)
    jPlay_InfoTextArea.setTabSize(4)
    jPlay_InfoTextArea.setText("Keys\n------------------------\narrow keys - move player\nCtrl+z, Ctrl+y - undo, redo\n\n")
    jPlay_InfoTextArea.setWrapStyleWord(true)
    jPlay_InfoTextArea.setDoubleBuffered(true)
    jPlay_InfoTextArea.setFocusable(false)
    val jPlay_PanelLayout = new GroupLayout(jPlay_Panel)
    jPlay_Panel.setLayout(jPlay_PanelLayout)
    jPlay_PanelLayout.setHorizontalGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPlay_PanelLayout.createSequentialGroup.addContainerGap().addGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPlay_PanelLayout.createSequentialGroup.addComponent(jPlay_LevelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPlay_LevelComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)).addGroup(jPlay_PanelLayout.createSequentialGroup.addGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false).addComponent(jPlay_MovesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue).addComponent(jPlay_TimeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MaxValue)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPlay_PanelLayout.createSequentialGroup.addComponent(jPlay_TimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPlay_RestartButton)).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPlay_PanelLayout.createSequentialGroup.addComponent(jPlay_UndoMultipleMovesButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPlay_UndoMoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPlay_MovesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPlay_RedoMoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPlay_RedoMultipleMovesButton))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPlay_ImportButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue).addComponent(jPlay_SolveButton))).addContainerGap()).addComponent(jPlay_InfoTextArea))
    jPlay_PanelLayout.setVerticalGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPlay_PanelLayout.createSequentialGroup.addContainerGap().addGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPlay_LevelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_LevelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPlay_MovesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_UndoMultipleMovesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_UndoMoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_MovesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_RedoMoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_RedoMultipleMovesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jPlay_ImportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_SolveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(jPlay_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPlay_TimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_TimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPlay_RestartButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jPlay_InfoTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MaxValue)))
    jTabbedPane.addTab("Play", jPlay_Panel)
    jCreate_LevelLabel.setText("Level")
    jCreate_LevelComboBox.setModel(new DefaultComboBoxModel[String](Array[String]("----- new level -----")))
    jCreate_LevelComboBox.addActionListener((evt: ActionEvent) => Create_LevelComboBoxActionPerformed(evt))
    jCreate_LevelComboBox.addKeyListener(jCanvasKeyListener)
    jCreate_DeleteButton.setText("Delete")
    jCreate_DeleteButton.setToolTipText("")
    jCreate_DeleteButton.addActionListener((evt: ActionEvent) => Create_DeleteButtonActionPerformed(evt))
    jCreate_DeleteButton.addKeyListener(jCanvasKeyListener)
    jCreate_StatusLabel.setText("Status")
    jCreate_StatusTextField.setEditable(false)
    jCreate_StatusTextField.setFocusable(false)
    jCreate_StatusTextField.setMinimumSize(new Dimension(64, 32))
    jCreate_StatusTextField.setPreferredSize(new Dimension(64, 32))
    jCreate_ImportButton.setText("Import")
    jCreate_ImportButton.addActionListener((evt: ActionEvent) => Create_ImportButtonActionPerformed(evt))
    jCreate_ImportButton.addKeyListener(jCanvasKeyListener)
    jCreate_NameLabel.setText("Name")
    jCreate_NameTextField.setMinimumSize(new Dimension(64, 32))
    jCreate_NameTextField.setPreferredSize(new Dimension(64, 32))
    jCreate_SaveButton.setText("Save")
    jCreate_SaveButton.addActionListener((evt: ActionEvent) => Create_SaveButtonActionPerformed(evt))
    jCreate_SaveButton.addKeyListener(jCanvasKeyListener)
    jCreate_SetFloorButton.setIcon(gameAssets.tileIcon(Tile.FloorInside))
    jCreate_SetFloorButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetFloorButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetFloorButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetFloorButton.addActionListener((evt: ActionEvent) => Create_SetTileButtonsActionPerformed(evt, Tile.FloorOutside))
    jCreate_SetFloorButton.addKeyListener(jCanvasKeyListener)
    jCreate_SetWallButton.setIcon(gameAssets.tileIcon(Tile.Wall))
    jCreate_SetWallButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetWallButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetWallButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetWallButton.addActionListener((evt: ActionEvent) => Create_SetTileButtonsActionPerformed(evt, Tile.Wall))
    jCreate_SetWallButton.addKeyListener(jCanvasKeyListener)
    jCreate_SetBoxButton.setIcon(gameAssets.tileIcon(Tile.Box))
    jCreate_SetBoxButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetBoxButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetBoxButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetBoxButton.addActionListener((evt: ActionEvent) => Create_SetTileButtonsActionPerformed(evt, Tile.Box))
    jCreate_SetBoxButton.addKeyListener(jCanvasKeyListener)
    jCreate_SetGoalButton.setIcon(gameAssets.tileIcon(Tile.Goal))
    jCreate_SetGoalButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetGoalButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetGoalButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetGoalButton.addActionListener((evt: ActionEvent) => Create_SetTileButtonsActionPerformed(evt, Tile.Goal))
    jCreate_SetGoalButton.addKeyListener(jCanvasKeyListener)
    jCreate_SetPlayerButton.setIcon(gameAssets.tileIcon(Tile.Player))
    jCreate_SetPlayerButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetPlayerButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetPlayerButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetPlayerButton.addActionListener((evt: ActionEvent) => Create_SetTileButtonsActionPerformed(evt, Tile.Player))
    jCreate_SetPlayerButton.addKeyListener(jCanvasKeyListener)
    jCreate_SetBoxGoalButton.setIcon(gameAssets.tileIcon(Tile.BoxGoal))
    jCreate_SetBoxGoalButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetBoxGoalButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetBoxGoalButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetBoxGoalButton.addActionListener((evt: ActionEvent) => Create_SetTileButtonsActionPerformed(evt, Tile.BoxGoal))
    jCreate_SetBoxGoalButton.addKeyListener(jCanvasKeyListener)
    jCreate_SetPlayerGoalButton.setIcon(gameAssets.tileIcon(Tile.PlayerGoal))
    jCreate_SetPlayerGoalButton.setMaximumSize(new Dimension(42, 42))
    jCreate_SetPlayerGoalButton.setMinimumSize(new Dimension(42, 42))
    jCreate_SetPlayerGoalButton.setPreferredSize(new Dimension(42, 42))
    jCreate_SetPlayerGoalButton.addActionListener((evt: ActionEvent) => Create_SetTileButtonsActionPerformed(evt, Tile.PlayerGoal))
    jCreate_SetPlayerGoalButton.addKeyListener(jCanvasKeyListener)
    jCreate_CommandHistoryTextArea.setEditable(false);
    jCreate_CommandHistoryTextArea.setColumns(20);
    jCreate_CommandHistoryTextArea.setLineWrap(true);
    jCreate_CommandHistoryTextArea.setRows(5);
    jCreate_CommandHistoryTextArea.setTabSize(4);
    jCreate_CommandHistoryTextArea.setText("// Keys\n// ------------------------\n// Ctrl+z, Ctrl+y - undo, redo\n// enter - input command\n// arrow keys - move selection\n\n\n// Predefined commands\n// ------------------------\n// Parameters:\n//   @  - currently selected location (expands to coordinates)\n//   dir - {row, col}.\n//   i    - index of rows/cols.\n//   is   - index of the space between rows/cols.\n//   n   - number of rows/cols.\n//   t    - {- empty, # wall, X box, O Goal, B BoxOnGoal, P Player, R PlayerOnGoal}.\n//   x:y - position.\n\ntiles_extend(dir is n)\ntiles_delete(dir i n)\ntile_set(t x:y)\n\ngoal_invert()\nwall_minimize(x:y)\nwall_filter(x:y n)      // n: number of tiles\nwall_fractalize(x:y)\nlevel_validate()\n\n// Commands are executed in order, until one fails its check.\n// Angular brackets make a transaction. For example:\ndef foo(v1 v2) = tile_set(wall v1) [wall_fractalize(v2) level_validate()]\nundef foo\n\n\nUser commands\n------------------")
    jCreate_CommandHistoryTextArea.setWrapStyleWord(true);
    jCreate_CommandHistoryTextArea.setDoubleBuffered(true);
    jCreate_CommandHistoryScrollPane.setViewportView(jCreate_CommandHistoryTextArea);
    jCreate_CommandTextArea.setColumns(20);
    jCreate_CommandTextArea.setLineWrap(true);
    jCreate_CommandTextArea.setRows(5);
    jCreate_CommandTextArea.setTabSize(4);
    jCreate_CommandTextArea.setWrapStyleWord(true);
    jCreate_CommandTextArea.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
    jCreate_CommandTextArea.setDoubleBuffered(true);
    jCreate_CommandTextScrollPane.setViewportView(jCreate_CommandTextArea)
    val jCreate_PanelLayout = new GroupLayout(jCreate_Panel)
    jCreate_Panel.setLayout(jCreate_PanelLayout)
    jCreate_PanelLayout.setHorizontalGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jCreate_CommandHistoryScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MaxValue).addComponent(jCreate_CommandTextScrollPane).addGroup(jCreate_PanelLayout.createSequentialGroup.addContainerGap().addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jCreate_PanelLayout.createSequentialGroup.addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jCreate_PanelLayout.createSequentialGroup.addComponent(jCreate_LevelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_LevelComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)).addGroup(jCreate_PanelLayout.createSequentialGroup.addComponent(jCreate_NameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_NameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)).addGroup(jCreate_PanelLayout.createSequentialGroup.addComponent(jCreate_StatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_StatusTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(jCreate_SaveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue).addComponent(jCreate_DeleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MaxValue).addComponent(jCreate_ImportButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue))).addGroup(jCreate_PanelLayout.createSequentialGroup.addGap(44, 44, 44).addComponent(jCreate_SetFloorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_SetWallButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_SetBoxButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_SetGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_SetPlayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue).addComponent(jCreate_SetBoxGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_SetPlayerGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap()))
    jCreate_PanelLayout.setVerticalGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jCreate_PanelLayout.createSequentialGroup.addContainerGap().addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jCreate_LevelComboBox).addComponent(jCreate_DeleteButton)).addComponent(jCreate_LevelLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jCreate_ImportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_StatusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_StatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(12, 12, 12).addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jCreate_SaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_NameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addGroup(jCreate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jCreate_SetFloorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_SetWallButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_SetBoxButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_SetGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_SetBoxGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_SetPlayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jCreate_SetPlayerGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addComponent(jCreate_CommandHistoryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MaxValue).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCreate_CommandTextScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
    jTabbedPane.addTab("Create", jCreate_Panel)
    jCanvasSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL)
    val jCanvasLayout = new GroupLayout(jCanvas)
    jCanvas.setLayout(jCanvasLayout)
    jCanvasLayout.setHorizontalGroup(jCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 563, Short.MaxValue))
    jCanvasLayout.setVerticalGroup(jCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MaxValue))
    val layout = new GroupLayout(this)
    this.setLayout(layout)
    layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup.addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCanvasSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)))
    layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jCanvas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue).addComponent(jTabbedPane).addComponent(jCanvasSeparator))
  }

  private def TabbedPaneStateChanged(evt: ChangeEvent): Unit = {
    jTabbedPane.getSelectedIndex match {
      case 0 =>
        gameTimer.start()
      case 1 =>
        gameTimer.stop()
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
