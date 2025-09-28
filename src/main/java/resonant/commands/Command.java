package resonant.commands;

import resonant.*;

public abstract class Command {
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws Exception;
    public boolean isExit() { return false; }
}
