package resonant.commands;

import resonant.TaskList;
import resonant.Ui;
import resonant.Storage;
import resonant.tasks.Task;

public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.size() == 0) {
            ui.box(" Your list is empty.");
        } else {
            StringBuilder sb = new StringBuilder(" Here are the tasks in your list:");
            int i = 1;
            for (Task t : tasks.asList()) sb.append('\n').append(' ').append(i++).append('.').append(t);
            ui.box(sb.toString());
        }
    }
}
