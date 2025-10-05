package resonant;

import resonant.commands.Command;

/**
 * The main entry point of the Resonant application.
 * <p>
 * This class initializes the user interface, storage, and task list components.
 * It then enters a command-processing loop, where it reads user input, parses it into a
 * {@link Command}, executes it, and updates the task list accordingly.
 * </p>
 *
 * <p>Resonant supports saving and loading tasks from persistent storage, and gracefully
 * handles both user and unexpected runtime errors.</p>
 */
public class Resonant {

    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    /**
     * Constructs a new {@code Resonant} instance using the given file path.
     * <p>
     * The constructor initializes the {@link Ui} and {@link Storage} objects and attempts to
     * load previously saved tasks. If loading fails, an empty {@link TaskList} is created instead.
     * </p>
     *
     * @param filePath The file path used for saving and loading task data.
     */
    public Resonant(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (Exception e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main event loop of the Resonant program.
     * <p>
     * This method displays a welcome message, reads commands from the user, parses them,
     * executes them, and handles any exceptions that occur during execution.
     * The loop continues until an exit command is issued.
     * </p>
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command c = Parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (DukeException e) {
                ui.showError(e.getMessage());
            } catch (Exception e) {
                ui.showError("Unexpected error: " + e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }
}
