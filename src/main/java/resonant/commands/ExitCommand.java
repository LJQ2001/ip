package resonant.commands;

import resonant.*;

/**
 * Represents a command that exits the program.
 * <p>
 * When executed, this command displays a goodbye message
 * and signals to the main program loop that it should terminate.
 */
public class ExitCommand extends Command {

    /**
     * Executes the command by displaying a goodbye message to the user
     * through the {@link Ui}. This method does not modify the {@link TaskList}
     * or {@link Storage}.
     *
     * @param tasks   The current list of tasks (unused in this command).
     * @param ui      The user interface handler used to display messages.
     * @param storage The storage handler (unused in this command).
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.sayGoodbye();
    }

    /**
     * Indicates that this command will terminate the program.
     *
     * @return {@code true}, since this command causes the application to exit.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
