package resonant.commands;

import resonant.TaskList;
import resonant.Ui;
import resonant.Storage;
import resonant.DukeException;
import resonant.tasks.Task;

import java.util.List;

public class FindCommand extends Command {
    private final String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

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
