package resonant.commands;

import resonant.*;

public class UnmarkCommand extends Command {
    private final int index1Based;
    public UnmarkCommand(int index1Based) { this.index1Based = index1Based; }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws Exception {
        var t = tasks.get(index1Based);
        t.unmark();
        storage.save(tasks.asList());
        ui.box(" OK, I've marked this task as not done yet:", "   " + t);
    }
}
