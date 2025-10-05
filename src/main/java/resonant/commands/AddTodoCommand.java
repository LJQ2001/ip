package resonant.commands;

import resonant.*;
import resonant.tasks.Todo;

/**
 * Represents a command that adds a {@link Todo} task to the task list.
 * A Todo task consists only of a description without a specific date or time.
 */
public class AddTodoCommand extends Command {
    private final String desc;

    /**
     * Constructs an {@code AddTodoCommand} with the specified description.
     *
     * @param desc Description of the todo task.
     */
    public AddTodoCommand(String desc) {
        this.desc = desc;
    }

    /**
     * Executes the command by creating a new {@link Todo} task,
     * adding it to the {@link TaskList}, saving the updated list to {@link Storage},
     * and displaying a confirmation message through the {@link Ui}.
     *
     * @param tasks   The current list of tasks.
     * @param ui      The user interface handler for displaying messages.
     * @param storage The storage handler used to persist the task list.
     * @throws Exception If the description is missing or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws Exception {
        if (desc == null || desc.isBlank()) {
            throw new DukeException("A todo needs a description. Usage: todo <desc>");
        }
        tasks.add(new Todo(desc));
        storage.save(tasks.asList());
        ui.box(" Got it. I've added this task:",
                "   " + tasks.get(tasks.size()).toString(),
                " Now you have " + tasks.size() + (tasks.size() == 1 ? " task" : " tasks") + " in the list.");
    }
}
