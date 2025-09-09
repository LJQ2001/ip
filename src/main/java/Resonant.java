/*
 * Resonant is a simple CLI chatbot that:
 * - Echoes and stores user inputs as tasks,
 * - Lists tasks with indices and status,
 * - Marks/unmarks tasks as done using commands "mark N" / "unmark N",
 * - Adds ToDos via "todo <desc>",
 * - Adds Deadlines via "deadline <desc> /by <when>",
 * - Adds Events via "event <desc> /from <start> /to <end>",
 * - Exits on "bye".
 *
 * Storage is an in-memory fixed-size array of up to 100 tasks.
 *
 * Notes on date/time:
 *   Dates/times are treated as plain strings (no parsing needed).
 *
 * Examples:
 *   todo borrow book
 *   deadline return book /by Sunday
 *   event project meeting /from Mon 2pm /to 4pm
 */

import java.util.Scanner;

public class Resonant {

    // ===== Base class and subclasses (A-Inheritance) =====
    static class Task {
        protected final String description;
        protected boolean isDone;

        Task(String description) {
            this.description = description;
            this.isDone = false;
        }

        void mark() { this.isDone = true; }
        void unmark() { this.isDone = false; }

        String getStatusIcon() { return isDone ? "X" : " "; }

        /** Base renders "[<status>] description". Subclasses prepend their type icon and optionally append details. */
        @Override
        public String toString() {
            return "[" + getStatusIcon() + "] " + description;
        }
    }

    static class Todo extends Task {
        Todo(String description) {
            super(description);
        }
        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }

    static class Deadline extends Task {
        protected final String by;

        Deadline(String description, String by) {
            super(description);
            this.by = by;
        }
        @Override
        public String toString() {
            return "[D]" + super.toString() + " (by: " + by + ")";
        }
    }

    static class Event extends Task {
        protected final String from;
        protected final String to;

        Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }
        @Override
        public String toString() {
            return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
        }
    }

    // ----- Simple fixed-size store (<=100 tasks) -----
    private static final Task[] tasks = new Task[100];
    private static int taskCount = 0;

    /**
     * Runs the Resonant chatbot REPL until the user types "bye".
     *
     * Supported commands:
     *   "list" – prints all tasks
     *   "mark N" – marks task N as done (1-based)
     *   "unmark N" – marks task N as not done (1-based)
     *   "todo <desc>" – adds a ToDo
     *   "deadline <desc> /by <when>" – adds a Deadline
     *   "event <desc> /from <start> /to <end>" – adds an Event
     *   any other non-empty line – added as a ToDo (fallback)
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

            } else if (input.startsWith("todo ")) {
                String desc = input.substring(5).trim();
                if (!desc.isEmpty()) {
                    addTask(new Todo(desc));
                } else {
                    box(" Please provide a description, e.g., todo borrow book");
                }

            } else if (input.startsWith("deadline ")) {
                handleDeadline(input);

            } else if (input.startsWith("event ")) {
                handleEvent(input);

            } else if (!input.isEmpty()) {
                // Fallback: treat any other non-empty line as a ToDo for convenience
                addTask(new Todo(input));
            }
        }

        sc.close();
    }

    /** Handle "deadline <desc> /by <when>" */
    private static void handleDeadline(String input) {
        String body = input.substring("deadline".length()).trim();
        if (body.isEmpty()) {
            box(" Please provide a description and /by, e.g., deadline return book /by Sunday");
            return;
        }
        String[] split = splitOnKeyword(body, "/by");
        String desc = split[0];
        String by = split[1];
        if (by == null) {
            box(" Missing '/by'. Use e.g., deadline return book /by Sunday");
            return;
        }
        addTask(new Deadline(desc, by));
    }

    /** Handle "event <desc> /from <start> /to <end>" */
    private static void handleEvent(String input) {
        String body = input.substring("event".length()).trim();
        if (body.isEmpty()) {
            box(" Please provide a description, /from and /to, e.g., event project meeting /from Mon 2pm /to 4pm");
            return;
        }
        String[] splitFrom = splitOnKeyword(body, "/from");
        String desc = splitFrom[0];
        String fromPart = splitFrom[1];
        if (fromPart == null) {
            box(" Missing '/from'. Use e.g., event project meeting /from Mon 2pm /to 4pm");
            return;
        }
        String[] splitTo = splitOnKeyword(fromPart, "/to");
        String from = splitTo[0];
        String to = splitTo[1];
        if (to == null) {
            box(" Missing '/to'. Use e.g., event project meeting /from Mon 2pm /to 4pm");
            return;
        }
        addTask(new Event(desc, from, to));
    }

    /**
     * Splits a command body by the first occurrence of a keyword (case sensitive),
     * returning a 2-element array: [leftTrimmed, rightTrimmedOrNull].
     * E.g., splitOnKeyword("return book /by Sunday", "/by") -> ["return book", "Sunday"]
     */
    private static String[] splitOnKeyword(String text, String keyword) {
        int idx = indexOfKeyword(text, keyword);
        if (idx == -1) return new String[]{ text.trim(), null };
        String left = text.substring(0, idx).trim();
        String right = text.substring(idx + keyword.length()).trim();
        return new String[]{ left, right };
    }

    /** Finds the index of the keyword considering optional leading spaces before it. */
    private static int indexOfKeyword(String text, String keyword) {
        return text.indexOf(keyword);
    }

    /**
     * Adds a task (any type), if capacity allows, and prints the standardized box.
     */
    private static void addTask(Task t) {
        if (taskCount >= 100) {
            box(" Sorry, your task list is full (100 items).");
            return;
        }
        tasks[taskCount++] = t;
        box(
                " Got it. I've added this task:",
                "   " + t.toString(),
                " Now you have " + taskCount + " " + (taskCount == 1 ? "task" : "tasks") + " in the list."
        );
    }

    /** Prints the current task list with indices and status/type icons. */
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
