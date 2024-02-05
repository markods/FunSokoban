import java.awt.event.{ActionEvent, ActionListener, KeyAdapter, KeyEvent}
import java.awt.{Dimension, Insets}
import javax.swing.event.{ChangeEvent, ChangeListener}
import javax.swing.*

class MainPanel extends JPanel {
  initComponents()

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings(Array("unchecked")) private // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  def initComponents(): Unit = {
    TabbedPane = new JTabbedPane
    Play_Panel = new JPanel
    Play_LevelLabel = new JLabel
    Play_LevelComboBox = new JComboBox[String]
    Play_MovesLabel = new JLabel
    Play_UndoAllMovesButton = new JButton
    Play_UndoMoveButton = new JButton
    Play_MovesTextField = new JTextField
    Play_RedoMoveButton = new JButton
    Play_RedoAllMovesButton = new JButton
    Play_ImportButton = new JButton
    Play_SolveButton = new JButton
    Play_TimeLabel = new JLabel
    Play_TimeTextField = new JTextField
    Play_RestartButton = new JButton
    Play_InfoTextArea = new JTextArea
    Create_Panel = new JPanel
    Create_LevelLabel = new JLabel
    Create_LevelComboBox = new JComboBox[String]
    Create_DeleteButton = new JButton
    Create_StatusLabel = new JLabel
    Create_StatusTextField = new JTextField
    Create_ImportButton = new JButton
    Create_NameLabel = new JLabel
    Create_NameTextField = new JTextField
    Create_SaveButton = new JButton
    Create_SetWallButton = new JButton
    Create_SetFloorButton = new JButton
    Create_SetBoxButton = new JButton
    Create_SetGoalButton = new JButton
    Create_SetBoxGoalButton = new JButton
    Create_SetPlayerButton = new JButton
    Create_CommandHistoryScrollPane = new JScrollPane
    Create_CommandHistoryTextArea = new JTextArea
    Create_CommandScrollPane = new JScrollPane
    Create_CommandTextArea = new JTextArea
    CanvasSeparator = new JSeparator
    CanvasPanel = new JPanel
    TabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT)
    TabbedPane.addChangeListener(new ChangeListener() {
      override def stateChanged(evt: ChangeEvent): Unit = {
        TabbedPaneStateChanged(evt)
      }
    })
    Play_LevelLabel.setText("Level")
    Play_LevelComboBox.setModel(new DefaultComboBoxModel[String](Array[String]("Level 1 - Whale Island")))
    Play_LevelComboBox.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Play_LevelComboBoxActionPerformed(evt)
      }
    })
    Play_MovesLabel.setText("Moves")
    Play_UndoAllMovesButton.setText("<<")
    Play_UndoAllMovesButton.setMargin(new Insets(2, 5, 3, 5))
    Play_UndoAllMovesButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Play_UndoAllMovesButtonActionPerformed(evt)
      }
    })
    Play_UndoMoveButton.setText("<")
    Play_UndoMoveButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Play_UndoMoveButtonActionPerformed(evt)
      }
    })
    Play_MovesTextField.setEditable(false)
    Play_MovesTextField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)
    Play_MovesTextField.setText("0")
    Play_MovesTextField.setFocusable(false)
    Play_MovesTextField.setMinimumSize(new Dimension(64, 32))
    Play_MovesTextField.setPreferredSize(new Dimension(64, 32))
    Play_MovesTextField.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Play_MovesTextFieldActionPerformed(evt)
      }
    })
    Play_RedoMoveButton.setText(">")
    Play_RedoMoveButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Play_RedoMoveButtonActionPerformed(evt)
      }
    })
    Play_RedoAllMovesButton.setText(">>")
    Play_RedoAllMovesButton.setMargin(new Insets(2, 5, 3, 5))
    Play_RedoAllMovesButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Play_RedoAllMovesButtonActionPerformed(evt)
      }
    })
    Play_ImportButton.setText("Import")
    Play_ImportButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Play_ImportButtonActionPerformed(evt)
      }
    })
    Play_SolveButton.setText("Solve")
    Play_SolveButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Play_SolveButtonActionPerformed(evt)
      }
    })
    Play_TimeLabel.setText("Time")
    Play_TimeTextField.setEditable(false)
    Play_TimeTextField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)
    Play_TimeTextField.setText("00:00:00")
    Play_TimeTextField.setFocusable(false)
    Play_TimeTextField.setMinimumSize(new Dimension(64, 32))
    Play_TimeTextField.setPreferredSize(new Dimension(64, 32))
    Play_RestartButton.setText("Restart")
    Play_RestartButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Play_RestartButtonActionPerformed(evt)
      }
    })
    Play_InfoTextArea.setEditable(false)
    Play_InfoTextArea.setColumns(20)
    Play_InfoTextArea.setLineWrap(true)
    Play_InfoTextArea.setRows(5)
    Play_InfoTextArea.setTabSize(4)
    Play_InfoTextArea.setText("Keys\n------------------------\narrow keys - move player\nCtrl+z, Ctrl+y - undo, redo\n\n")
    Play_InfoTextArea.setFocusable(false)
    val Play_PanelLayout = new GroupLayout(Play_Panel)
    Play_Panel.setLayout(Play_PanelLayout)
    Play_PanelLayout.setHorizontalGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(Play_PanelLayout.createSequentialGroup.addContainerGap().addGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(Play_PanelLayout.createSequentialGroup.addComponent(Play_LevelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Play_LevelComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)).addGroup(Play_PanelLayout.createSequentialGroup.addGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false).addComponent(Play_MovesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue).addComponent(Play_TimeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MaxValue)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(Play_PanelLayout.createSequentialGroup.addComponent(Play_TimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Play_RestartButton)).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Play_PanelLayout.createSequentialGroup.addComponent(Play_UndoAllMovesButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Play_UndoMoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Play_MovesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Play_RedoMoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Play_RedoAllMovesButton))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Play_ImportButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue).addComponent(Play_SolveButton))).addContainerGap()).addComponent(Play_InfoTextArea))
    Play_PanelLayout.setVerticalGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(Play_PanelLayout.createSequentialGroup.addContainerGap().addGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(Play_LevelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Play_LevelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(Play_MovesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Play_UndoAllMovesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Play_UndoMoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Play_MovesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Play_RedoMoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Play_RedoAllMovesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(Play_ImportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Play_SolveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(Play_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(Play_TimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Play_TimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Play_RestartButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(Play_InfoTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MaxValue)))
    TabbedPane.addTab("Play", Play_Panel)
    Create_LevelLabel.setText("Level")
    Create_LevelComboBox.setModel(new DefaultComboBoxModel[String](Array[String]("----- new level -----")))
    Create_LevelComboBox.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Create_LevelComboBoxActionPerformed(evt)
      }
    })
    Create_DeleteButton.setText("Delete")
    Create_DeleteButton.setToolTipText("")
    Create_DeleteButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Create_DeleteButtonActionPerformed(evt)
      }
    })
    Create_StatusLabel.setText("Status")
    Create_StatusTextField.setEditable(false)
    Create_StatusTextField.setFocusable(false)
    Create_StatusTextField.setMinimumSize(new Dimension(64, 32))
    Create_StatusTextField.setPreferredSize(new Dimension(64, 32))
    Create_ImportButton.setText("Import")
    Create_ImportButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Create_ImportButtonActionPerformed(evt)
      }
    })
    Create_NameLabel.setText("Name")
    Create_NameTextField.setMinimumSize(new Dimension(64, 32))
    Create_NameTextField.setPreferredSize(new Dimension(64, 32))
    Create_SaveButton.setText("Save")
    Create_SaveButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Create_SaveButtonActionPerformed(evt)
      }
    })
    Create_SetWallButton.setIcon(new ImageIcon(getClass.getResource("/wall.png"))) // NOI18N

    Create_SetWallButton.setMaximumSize(new Dimension(42, 42))
    Create_SetWallButton.setMinimumSize(new Dimension(42, 42))
    Create_SetWallButton.setPreferredSize(new Dimension(42, 42))
    Create_SetWallButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Create_SetWallButtonActionPerformed(evt)
      }
    })
    Create_SetFloorButton.setIcon(new ImageIcon(getClass.getResource("/floor_inside.png"))) // NOI18N

    Create_SetFloorButton.setMaximumSize(new Dimension(42, 42))
    Create_SetFloorButton.setMinimumSize(new Dimension(42, 42))
    Create_SetFloorButton.setPreferredSize(new Dimension(42, 42))
    Create_SetFloorButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Create_SetFloorButtonActionPerformed(evt)
      }
    })
    Create_SetBoxButton.setIcon(new ImageIcon(getClass.getResource("/box.png"))) // NOI18N

    Create_SetBoxButton.setMaximumSize(new Dimension(42, 42))
    Create_SetBoxButton.setMinimumSize(new Dimension(42, 42))
    Create_SetBoxButton.setPreferredSize(new Dimension(42, 42))
    Create_SetBoxButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Create_SetBoxButtonActionPerformed(evt)
      }
    })
    Create_SetGoalButton.setIcon(new ImageIcon(getClass.getResource("/goal.png"))) // NOI18N

    Create_SetGoalButton.setMaximumSize(new Dimension(42, 42))
    Create_SetGoalButton.setMinimumSize(new Dimension(42, 42))
    Create_SetGoalButton.setPreferredSize(new Dimension(42, 42))
    Create_SetGoalButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Create_SetGoalButtonActionPerformed(evt)
      }
    })
    Create_SetBoxGoalButton.setIcon(new ImageIcon(getClass.getResource("/box_goal.png"))) // NOI18N

    Create_SetBoxGoalButton.setMaximumSize(new Dimension(42, 42))
    Create_SetBoxGoalButton.setMinimumSize(new Dimension(42, 42))
    Create_SetBoxGoalButton.setPreferredSize(new Dimension(42, 42))
    Create_SetBoxGoalButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Create_SetBoxGoalButtonActionPerformed(evt)
      }
    })
    Create_SetPlayerButton.setIcon(new ImageIcon(getClass.getResource("/player.png"))) // NOI18N

    Create_SetPlayerButton.setMaximumSize(new Dimension(42, 42))
    Create_SetPlayerButton.setMinimumSize(new Dimension(42, 42))
    Create_SetPlayerButton.setPreferredSize(new Dimension(42, 42))
    Create_SetPlayerButton.addActionListener(new ActionListener() {
      override def actionPerformed(evt: ActionEvent): Unit = {
        Create_SetPlayerButtonActionPerformed(evt)
      }
    })
    Create_CommandHistoryTextArea.setEditable(false)
    Create_CommandHistoryTextArea.setColumns(20)
    Create_CommandHistoryTextArea.setLineWrap(true)
    Create_CommandHistoryTextArea.setRows(5)
    Create_CommandHistoryTextArea.setTabSize(4)
    Create_CommandHistoryTextArea.setText("// Keys\n// ------------------------\n// Ctrl+z, Ctrl+y - undo, redo\n// enter - input command\n// arrow keys - move selection\n\n\n// Predefined commands\n// ------------------------\n// Parameters:\n//   @  - currently selected location (expands to coordinates)\n//   dir - {row, col}.\n//   i    - index of rows/cols.\n//   is   - index of the space between rows/cols.\n//   n   - number of rows/cols.\n//   t    - {empty, wall, box, goal, player}.\n//   x:y - coordinate.\n\ntiles_extend(dir is n)\ntiles_delete(dir i n)\ntile_set(t x:y)\n\ngoal_invert()\nwall_minimize(x:y)\nwall_filter(x:y n)      // n: number of tiles\nwall_fractalize(x:y)\nlevel_validate()\n\n// Commands are executed in order, until one fails its check.\n// Angular brackets make a transaction. For example:\ndef foo(v1 v2) = tile_set(wall v1) [wall_fractalize(v2) level_validate()]\nundef foo\n\n\nUser commands\n------------------")
    Create_CommandHistoryTextArea.setSelectionEnd(0)
    Create_CommandHistoryTextArea.setSelectionStart(0)
    Create_CommandHistoryScrollPane.setViewportView(Create_CommandHistoryTextArea)
    Create_CommandTextArea.setColumns(20)
    Create_CommandTextArea.setRows(5)
    Create_CommandTextArea.setTabSize(4)
    Create_CommandTextArea.addKeyListener(new KeyAdapter() {
      override def keyTyped(evt: KeyEvent): Unit = {
        Create_CommandTextAreaKeyTyped(evt)
      }
    })
    Create_CommandScrollPane.setViewportView(Create_CommandTextArea)
    val Create_PanelLayout = new GroupLayout(Create_Panel)
    Create_Panel.setLayout(Create_PanelLayout)
    Create_PanelLayout.setHorizontalGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(Create_CommandHistoryScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MaxValue).addComponent(Create_CommandScrollPane).addGroup(Create_PanelLayout.createSequentialGroup.addContainerGap().addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(Create_PanelLayout.createSequentialGroup.addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Create_PanelLayout.createSequentialGroup.addComponent(Create_LevelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Create_LevelComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)).addGroup(Create_PanelLayout.createSequentialGroup.addComponent(Create_NameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Create_NameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)).addGroup(Create_PanelLayout.createSequentialGroup.addComponent(Create_StatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Create_StatusTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(Create_SaveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue).addComponent(Create_DeleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MaxValue).addComponent(Create_ImportButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)).addContainerGap()).addGroup(Create_PanelLayout.createSequentialGroup.addGap(44, 44, 44).addComponent(Create_SetWallButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Create_SetFloorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Create_SetBoxButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addComponent(Create_SetGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Create_SetBoxGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Create_SetPlayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)))))
    Create_PanelLayout.setVerticalGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Create_PanelLayout.createSequentialGroup.addContainerGap().addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(Create_LevelComboBox).addComponent(Create_DeleteButton)).addComponent(Create_LevelLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(Create_ImportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Create_StatusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Create_StatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(12, 12, 12).addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(Create_SaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Create_NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Create_NameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addGroup(Create_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(Create_SetFloorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Create_SetWallButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Create_SetBoxButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Create_SetGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Create_SetBoxGoalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(Create_SetPlayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addComponent(Create_CommandHistoryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MaxValue).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(Create_CommandScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
    TabbedPane.addTab("Create", Create_Panel)
    CanvasSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL)
    CanvasPanel.addKeyListener(new KeyAdapter() {
      override def keyPressed(evt: KeyEvent): Unit = {
        CanvasPanelKeyPressed(evt)
      }
    })
    val CanvasPanelLayout = new GroupLayout(CanvasPanel)
    CanvasPanel.setLayout(CanvasPanelLayout)
    CanvasPanelLayout.setHorizontalGroup(CanvasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 573, Short.MaxValue))
    CanvasPanelLayout.setVerticalGroup(CanvasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MaxValue))
    val layout = new GroupLayout(this)
    this.setLayout(layout)
    layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup.addComponent(TabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(CanvasSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(CanvasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue)))
    layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(CanvasPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MaxValue).addComponent(TabbedPane).addComponent(CanvasSeparator))
  } // </editor-fold>//GEN-END:initComponents

  private def Play_MovesTextFieldActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Play_MovesTextFieldActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Play_MovesTextFieldActionPerformed

  private def TabbedPaneStateChanged(evt: ChangeEvent): Unit = {
    //GEN-FIRST:event_TabbedPaneStateChanged
    // TODO add your handling code here:
  } //GEN-LAST:event_TabbedPaneStateChanged

  private def Play_LevelComboBoxActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Play_LevelComboBoxActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Play_LevelComboBoxActionPerformed

  private def Play_UndoAllMovesButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Play_UndoAllMovesButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Play_UndoAllMovesButtonActionPerformed

  private def Play_UndoMoveButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Play_UndoMoveButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Play_UndoMoveButtonActionPerformed

  private def Play_RedoMoveButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Play_RedoMoveButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Play_RedoMoveButtonActionPerformed

  private def Play_RedoAllMovesButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Play_RedoAllMovesButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Play_RedoAllMovesButtonActionPerformed

  private def Play_ImportButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Play_ImportButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Play_ImportButtonActionPerformed

  private def Play_SolveButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Play_SolveButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Play_SolveButtonActionPerformed

  private def Play_RestartButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Play_RestartButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Play_RestartButtonActionPerformed

  private def CanvasPanelKeyPressed(evt: KeyEvent): Unit = {
    //GEN-FIRST:event_CanvasPanelKeyPressed
    // TODO add your handling code here:
  } //GEN-LAST:event_CanvasPanelKeyPressed

  private def Create_LevelComboBoxActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Create_LevelComboBoxActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Create_LevelComboBoxActionPerformed

  private def Create_DeleteButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Create_DeleteButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Create_DeleteButtonActionPerformed

  private def Create_ImportButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Create_ImportButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Create_ImportButtonActionPerformed

  private def Create_SaveButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Create_SaveButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Create_SaveButtonActionPerformed

  private def Create_SetWallButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Create_SetWallButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Create_SetWallButtonActionPerformed

  private def Create_SetFloorButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Create_SetFloorButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Create_SetFloorButtonActionPerformed

  private def Create_SetBoxButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Create_SetBoxButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Create_SetBoxButtonActionPerformed

  private def Create_SetGoalButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Create_SetGoalButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Create_SetGoalButtonActionPerformed

  private def Create_SetBoxGoalButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Create_SetBoxGoalButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Create_SetBoxGoalButtonActionPerformed

  private def Create_SetPlayerButtonActionPerformed(evt: ActionEvent): Unit = {
    //GEN-FIRST:event_Create_SetPlayerButtonActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_Create_SetPlayerButtonActionPerformed

  private def Create_CommandTextAreaKeyTyped(evt: KeyEvent): Unit = {
    //GEN-FIRST:event_Create_CommandTextAreaKeyTyped
    // TODO add your handling code here:
  } //GEN-LAST:event_Create_CommandTextAreaKeyTyped

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private var CanvasPanel: JPanel = _
  private var CanvasSeparator: JSeparator = _
  private var Create_CommandHistoryScrollPane: JScrollPane = _
  private var Create_CommandHistoryTextArea: JTextArea = _
  private var Create_CommandScrollPane: JScrollPane = _
  private var Create_CommandTextArea: JTextArea = _
  private var Create_DeleteButton: JButton = _
  private var Create_ImportButton: JButton = _
  private var Create_LevelComboBox: JComboBox[String] = _
  private var Create_LevelLabel: JLabel = _
  private var Create_NameLabel: JLabel = _
  private var Create_NameTextField: JTextField = _
  private var Create_Panel: JPanel = _
  private var Create_SaveButton: JButton = _
  private var Create_SetBoxButton: JButton = _
  private var Create_SetBoxGoalButton: JButton = _
  private var Create_SetFloorButton: JButton = _
  private var Create_SetGoalButton: JButton = _
  private var Create_SetPlayerButton: JButton = _
  private var Create_SetWallButton: JButton = _
  private var Create_StatusLabel: JLabel = _
  private var Create_StatusTextField: JTextField = _
  private var Play_ImportButton: JButton = _
  private var Play_InfoTextArea: JTextArea = _
  private var Play_LevelComboBox: JComboBox[String] = _
  private var Play_LevelLabel: JLabel = _
  private var Play_MovesLabel: JLabel = _
  private var Play_MovesTextField: JTextField = _
  private var Play_Panel: JPanel = _
  private var Play_RedoAllMovesButton: JButton = _
  private var Play_RedoMoveButton: JButton = _
  private var Play_RestartButton: JButton = _
  private var Play_SolveButton: JButton = _
  private var Play_TimeLabel: JLabel = _
  private var Play_TimeTextField: JTextField = _
  private var Play_UndoAllMovesButton: JButton = _
  private var Play_UndoMoveButton: JButton = _
  private var TabbedPane: JTabbedPane = _
  // End of variables declaration//GEN-END:variables
}
