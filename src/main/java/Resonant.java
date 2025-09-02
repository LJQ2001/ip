// package resonant; // ← Uncomment and set your package if required by your project.

/**
 * Resonant is a simple CLI chatbot that:
 * <ul>
 *   <li>Echoes and stores user inputs as tasks,</li>
 *   <li>Lists tasks with indices and status,</li>
 *   <li>Marks/unmarks tasks as done using commands {@input mark N} / {@input unmark N},</li>
 *   <li>Exits on bye.</li>
 * </ul>
 * <p>Storage is an in-memory fixed-size array of up to 100 tasks.</p>
 */
import java.util.Scanner;

public class Resonant {

    /**
     * Represents a single to-do task with a description and done-state.
     */
    static class Task {
        private final String description;
        private boolean isDone;

        /**
         * Constructs a task with the given description, initially not done.
         *
         * @param description the task description
         */
        Task(String description) {
            this.description = description;
            this.isDone = false;
        }

        /** Marks this task as done. */
        void mark() { this.isDone = true; }

        /** Marks this task as not done. */
        void unmark() { this.isDone = false; }

        /** Returns "X" if done, otherwise a single space. */
        String getStatusIcon() { return isDone ? "X" : " "; }

        @Override
        public String toString() {
            return "[" + getStatusIcon() + "] " + description;
        }
    }

    // ----- Simple fixed-size store (<=100 tasks) -----
    private static final Task[] tasks = new Task[100];
    private static int taskCount = 0;

    /**
     * Runs the Resonant chatbot REPL until the user types "bye".
     *
     * Supported commands :
     *   "list" – prints all tasks
     *   "mark N" – marks task N as done (1-based)
     *   "nmark N" – marks task N as not done (1-based)
     *    any other non-empty line – added as a new task
     *   "bye" – exits
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        box(
                " Hello! I'm Resonant",
                " What can I do for you?"
        );

        while (true) {
            String input = sc.nextLine().trim();

            if (input.equals("bye")) {
                box(" Bye. Hope to see you again soon!");
                break;

            } else if (input.equals("list")) {
                printList();

            } else if (input.startsWith("mark ")) {
                handleMarkUnmark(input, true);

            } else if (input.startsWith("unmark ")) {
                handleMarkUnmark(input, false);

            } else if (!input.isEmpty()) {
                addTask(input);
            }
        }

        sc.close();
    }

    /**
     * Adds a task with the given description, if capacity allows.
     *
     * @param description the task description to add
     */
    private static void addTask(String description) {
        if (taskCount >= 100) {
            box(" Sorry, your task list is full (100 items).");
            return;
        }
        tasks[taskCount++] = new Task(description);
        box(" added: " + description);
    }

    /** Prints the current task list with indices and status icons. */
    private static void printList() {
        if (taskCount == 0) {
            box(" Your list is empty.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            sb.append("\n ").append(i + 1).append(".").append(tasks[i].toString());
        }
        box(sb.toString());
    }

    /**
     * Handles mark N / unmark N
     *
     * @param input full user input (e.g., "mark 2")
     * @param mark  true to mark, false to unmark
     */
    private static void handleMarkUnmark(String input, boolean mark) {
        String[] parts = input.split("\\s+", 2);
        if (parts.length < 2) {
            box(" Please provide a task number, e.g., " + (mark ? "mark 2" : "unmark 2"));
            return;
        }
        try {
            int idx = Integer.parseInt(parts[1]); // 1-based
            if (idx < 1 || idx > taskCount) {
                box(" Task number out of range. You have " + taskCount + " task(s).");
                return;
            }
            Task t = tasks[idx - 1];
            if (mark) {
                t.mark();
                box(
                        " Nice! I've marked this task as done:",
                        "   " + t.toString()
                );
            } else {
                t.unmark();
                box(
                        " OK, I've marked this task as not done yet:",
                        "   " + t.toString()
                );
            }
        } catch (NumberFormatException e) {
            box(" Invalid number. Use e.g., " + (mark ? "mark 2" : "unmark 2"));
        }
    }

    /** Prints a box with the given lines inside. */
    private static void box(String... lines) {
        System.out.println("____________________________________________________________");
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println("____________________________________________________________");
    }
}
