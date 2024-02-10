// TODO:
sealed class EditorCommand(val name: String,
                           val param: List[CommandParam],
                           val subcommands: List[EditorCommand],
                           val editor: Editor) {
  def checkParams(): Boolean = false

  def checkState(): Boolean = false

  def apply(): Boolean = false
}
