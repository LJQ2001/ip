package resonant.commands;

import resonant.*;

/**
 * Represents a command that deletes a task from the {@link TaskList}.
 * The task to be deleted is identified by its one-based index as shown in the task list.
 */
public class DeleteCommand extends Command {
    private final int index1Based;

    /**
     * Constructs a {@code DeleteCommand} with the specified task index.
     *
     * @param index1Based The one-based index of the task to delete from the list.
     */
    public DeleteCommand(int index1Based) {
        this.index1Based = index1Based;
    }

    /**
     * Executes the command by removing the specified task from the {@link TaskList},
     * saving the updated list to {@link Storage}, and displaying a confirmation message
     * through the {@link Ui}.
     *
     * @param tasks   The current list of tasks.
     * @param ui      The user interface handler used to display output messages.
     * @param storage The storage handler used to persist the updated task list.
     * @throws Exception If the index is invalid or an error occurs while saving.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws Exception {
        var removed = tasks.remove(index1Based);
        storage.save(tasks.asList());
        int n = tasks.size();
        ui.box(" Noted. I've removed this task:",
                "   " + removed,
                " Now you have " + n + " " + (n == 1 ? "task" : "tasks") + " in the list.");
    }
}
