package resonant.commands;

import resonant.*;
import resonant.tasks.Event;

/**
 * Represents a command that adds an {@link Event} task to the task list.
 * An event includes a description, a start time, and an end time.
 */
public class AddEventCommand extends Command {
    private final String desc;
    private final String from;
    private final String to;

    /**
     * Constructs an {@code AddEventCommand} with the specified description,
     * start time, and end time.
     *
     * @param desc Description of the event.
     * @param from The start time of the event (after the /from keyword).
     * @param to   The end time of the event (after the /to keyword).
     */
    public AddEventCommand(String desc, String from, String to) {
        this.desc = desc;
        this.from = from;
        this.to = to;
    }

    /**
     * Executes the command by creating a new {@link Event} task,
     * adding it to the {@link TaskList}, saving the updated list to {@link Storage},
     * and displaying a confirmation message through the {@link Ui}.
     *
     * @param tasks   The current list of tasks.
     * @param ui      The user interface handler to display output messages.
     * @param storage The storage handler used to persist the task list.
     * @throws Exception If any required parameter (description, from, or to)
     *                   is missing or invalid, or if saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws Exception {
        if (desc == null || desc.isBlank())
            throw new DukeException("Event description cannot be empty.");
        if (from == null || from.isBlank())
            throw new DukeException("Missing '/from'. Usage: event <desc> /from <start> /to <end>");
        if (to == null || to.isBlank())
            throw new DukeException("Missing '/to'. Usage: event <desc> /from <start> /to <end>");

        tasks.add(new Event(desc, from, to));
        storage.save(tasks.asList());
        ui.box(" Got it. I've added this task:",
                "   " + tasks.get(tasks.size()).toString(),
                " Now you have " + tasks.size() + (tasks.size() == 1 ? " task" : " tasks") + " in the list.");
    }
}
