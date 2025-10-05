package resonant.commands;

import resonant.TaskList;
import resonant.Ui;
import resonant.Storage;
import resonant.DukeException;
import resonant.tasks.Task;

import java.util.List;

/**
 * Represents a command that searches for tasks containing a specific keyword
 * in their descriptions.
 * <p>
 * When executed, this command filters the task list to display only
 * the matching tasks, or shows a message if no matches are found.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Constructs a {@code FindCommand} with the given search keyword.
     *
     * @param keyword The keyword to search for within task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Executes the command by searching for tasks that contain the given keyword,
     * displaying all matches in a numbered list through the {@link Ui}.
     * <p>
     * If no matching tasks are found, a message is shown instead.
     *
     * @param tasks   The current list of tasks.
     * @param ui      The user interface handler used to display results.
     * @param storage The storage handler (not used in this command).
     * @throws DukeException If the keyword is missing or blank.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        if (keyword == null || keyword.isBlank()) {
            throw new DukeException("Provide a keyword. Usage: find <keyword>");
        }

        List<Task> matches = tasks.find(keyword);
        if (matches.isEmpty()) {
            ui.box(" No matching tasks found for \"" + keyword + "\".");
            return;
        }

        StringBuilder sb = new StringBuilder(" Here are the matching tasks in your list:");
        int i = 1;
        for (Task t : matches) {
            sb.append('\n').append(' ').append(i++).append('.').append(t);
        }
        ui.box(sb.toString());
    }
}
