package resonant.commands;

import resonant.*;

public class DeleteCommand extends Command {
    private final int index1Based;
    public DeleteCommand(int index1Based) { this.index1Based = index1Based; }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws Exception {
        var removed = tasks.remove(index1Based);
        storage.save(tasks.asList());
        int n = tasks.size();
        ui.box(" Noted. I've removed this task:",
                "   " + removed,
                " Now you have " + n + " " + (n == 1 ? "task" : "tasks") + " in the list.");
    }
}
