package resonant;

import java.util.Scanner;

/**
 * Handles all user interaction, including displaying messages,
 * reading commands, and formatting outputs for the user interface.
 * <p>
 * The {@code Ui} class is responsible for printing standard messages
 * (welcome, error, goodbye), drawing separator lines, and boxing
 * text messages for consistent visual formatting in the console.
 * </p>
 */
public class Ui {

    /** Scanner used to read user input from standard input (System.in). */
    private final Scanner scanner;

    /**
     * Constructs a {@code Ui} object and initializes the input scanner.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays a welcome message when the program starts.
     * <p>
     * The message is displayed within a bordered box for readability.
     * </p>
     */
    public void showWelcome() {
        box(" Hello! I'm Resonant", " What can I do for you?");
    }

    /**
     * Reads a single line of user input, trims any leading and trailing spaces,
     * and returns it as a command string.
     *
     * @return the trimmed user input command
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays a horizontal separator line for visual structure in the console.
     * <p>
     * Used to clearly separate command input and output sections.
     * </p>
     */
    public void showLine() {
        System.out.println("____________________________________________________________");
    }

    /**
     * Displays an error message in a formatted box.
     * <p>
     * Used when a {@link resonant.DukeException} or unexpected error occurs.
     * </p>
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        box(" OOPS!!! " + message);
    }

    /**
     * Displays a message indicating that the saved data could not be loaded.
     * <p>
     * This is typically called when the storage file is missing or corrupted.
     * </p>
     */
    public void showLoadingError() {
        box(" OOPS!!! Couldn't load saved tasks. Starting fresh.");
    }

    /**
     * Displays a goodbye message when the user exits the program.
     * <p>
     * Shown at the end of execution before termination.
     * </p>
     */
    public void sayGoodbye() {
        box(" Bye. Hope to see you again soon!");
    }

    /**
     * Displays one or more lines of text inside a formatted box with horizontal separators.
     * <p>
     * Each line is printed in order, enclosed by top and bottom border lines for visual clarity.
     * </p>
     *
     * @param lines one or more lines of text to display within the box
     */
    public void box(String... lines) {
        showLine();
        for (String line : lines) {
            System.out.println(line);
        }
        showLine();
    }
}
