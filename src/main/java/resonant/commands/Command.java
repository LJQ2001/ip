package resonant.commands;

import resonant.*;

/**
 * Represents an abstract command that can be executed by the program.
 * <p>
 * Each concrete subclass of {@code Command} defines its own behavior
 * in the {@link #execute(TaskList, Ui, Storage)} method.
 */
public abstract class Command {

    /**
     * Executes the command using the provided task list, user interface, and storage.
     *
     * @param tasks   The {@link TaskList} containing the user's tasks.
     * @param ui      The {@link Ui} instance used to display messages to the user.
     * @param storage The {@link Storage} instance used to save or load task data.
     * @throws Exception If an error occurs during command execution.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws Exception;

    /**
     * Determines whether this command will cause the program to exit.
     * <p>
     * By default, this method returns {@code false}. Commands that should
     * terminate the program (e.g., {@code ExitCommand}) override this to return {@code true}.
     *
     * @return {@code true} if this command exits the program; {@code false} otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
