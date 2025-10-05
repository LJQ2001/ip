package resonant.commands;

import resonant.*;
import resonant.tasks.Deadline;

/**
 * Represents a command that adds a {@link Deadline} task to the task list.
 * The command requires a task description and a due date/time (after the /by keyword).
 */
public class AddDeadlineCommand extends Command {

    private final String desc;
    private final String by;

    /**
     * Constructs an AddDeadlineCommand with the specified description and deadline.
     *
     * @param desc Description of the deadline task.
     * @param by   The due date/time string provided by the user.
     */
    public AddDeadlineCommand(String desc, String by) {
        this.desc = desc;
        this.by = by;
    }

    /**
     * Executes the command by creating a new {@link Deadline} task,
     * adding it to the task list, saving the updated list to storage,
     * and showing a confirmation message through the UI.
     *
     * @param tasks   The current {@link TaskList} to which the task will be added.
     * @param ui      The {@link Ui} object responsible for displaying messages.
     * @param storage The {@link Storage} handler used to persist task data.
     * @throws Exception If the description or date is missing, or if saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws Exception {
        if (desc == null || desc.isBlank())
            throw new DukeException("Deadline description cannot be empty.");
        if (by == null || by.isBlank())
            throw new DukeException("Missing '/by'. Usage: deadline <desc> /by <when>");

        tasks.add(new Deadline(desc, by));
        storage.save(tasks.asList());
        ui.box(" Got it. I've added this task:",
                "   " + tasks.get(tasks.size()).toString(),
                " Now you have " + tasks.size() + (tasks.size() == 1 ? " task" : " tasks") + " in the list.");
    }
}
