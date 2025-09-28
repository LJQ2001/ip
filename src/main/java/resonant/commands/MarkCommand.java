package resonant.commands;

import resonant.*;

public class MarkCommand extends Command {
    private final int index1Based;
    public MarkCommand(int index1Based) { this.index1Based = index1Based; }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws Exception {
        var t = tasks.get(index1Based);
        t.mark();
        storage.save(tasks.asList());
        ui.box(" Nice! I've marked this task as done:", "   " + t);
    }
}
