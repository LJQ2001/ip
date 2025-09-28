package resonant;

import java.util.Scanner;

public class Ui {
    private final Scanner scanner;

    public Ui() { this.scanner = new Scanner(System.in); }

    public void showWelcome() {
        box(" Hello! I'm Resonant", " What can I do for you?");
    }

    public String readCommand() { return scanner.nextLine().trim(); }

    public void showLine() {
        System.out.println("____________________________________________________________");
    }

    public void showError(String message) {
        box(" OOPS!!! " + message);
    }

    public void showLoadingError() {
        box(" OOPS!!! Couldn't load saved tasks. Starting fresh.");
    }

    public void sayGoodbye() {
        box(" Bye. Hope to see you again soon!");
    }

    public void box(String... lines) {
        showLine();
        for (String line : lines) System.out.println(line);
        showLine();
    }
}
