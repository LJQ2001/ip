package resonant.commands;

import resonant.*;
import resonant.tasks.Deadline;

public class AddDeadlineCommand extends Command {
    private final String desc;
    private final String by;

    public AddDeadlineCommand(String desc, String by) {
        this.desc = desc; this.by = by;
    }

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
