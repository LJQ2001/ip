package resonant.commands;

import resonant.*;
import resonant.tasks.Todo;

public class AddTodoCommand extends Command {
    private final String desc;
    public AddTodoCommand(String desc) { this.desc = desc; }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws Exception {
        if (desc == null || desc.isBlank()) {
            throw new DukeException("A todo needs a description. Usage: todo <desc>");
        }
        tasks.add(new Todo(desc));
        storage.save(tasks.asList());
        ui.box(" Got it. I've added this task:",
                "   " + tasks.get(tasks.size()).toString(),
                " Now you have " + tasks.size() + (tasks.size() == 1 ? " task" : " tasks") + " in the list.");
    }
}
