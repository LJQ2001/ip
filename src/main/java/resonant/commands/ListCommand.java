package resonant.commands;

import resonant.TaskList;
import resonant.Ui;
import resonant.Storage;
import resonant.tasks.Task;

/**
 * Represents a command that lists all tasks currently stored in the {@link TaskList}.
 * <p>
 * When executed, this command displays each task in a numbered format.
 * If there are no tasks, it informs the user that the list is empty.
 */
public class ListCommand extends Command {

    /**
     * Executes the command by printing all tasks in the {@link TaskList}
     * to the user interface in a numbered list format.
     * <p>
     * If there are no tasks, a message stating that the list is empty is shown instead.
     *
     * @param tasks   The current list of tasks.
     * @param ui      The user interface handler used to display messages.
     * @param storage The storage handler (not used in this command).
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.size() == 0) {
            ui.box(" Your list is empty.");
        } else {
            StringBuilder sb = new StringBuilder(" Here are the tasks in your list:");
            int i = 1;
            for (Task t : tasks.asList()) {
                sb.append('\n').append(' ').append(i++).append('.').append(t);
            }
            ui.box(sb.toString());
        }
    }
}
