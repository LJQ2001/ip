package resonant.commands;

import resonant.*;
import resonant.tasks.Event;

public class AddEventCommand extends Command {
    private final String desc, from, to;

    public AddEventCommand(String desc, String from, String to) {
        this.desc = desc; this.from = from; this.to = to;
    }

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
