package resonant;

import java.util.Scanner;

/**
 * Resonant is a simple CLI chatbot that:
 * <ul>
 *   <li>Echoes and stores user inputs as tasks,</li>
 *   <li>Lists tasks with indices and status,</li>
 *   <li>Marks/unmarks tasks as done using commands "mark N" / "unmark N",</li>
 *   <li>Adds ToDos via "todo &lt;desc&gt;",</li>
 *   <li>Adds Deadlines via "deadline &lt;desc&gt; /by &lt;when&gt;",</li>
 *   <li>Adds Events via "event &lt;desc&gt; /from &lt;start&gt; /to &lt;end&gt;",</li>
 *   <li>Exits on "bye".</li>
 * </ul>
 *
 * <p>Storage is an in-memory fixed-size array of up to 100 tasks.
 * Dates/times are treated as plain strings (no parsing needed).</p>
 */
public class Resonant {

    // ===================== Constants & Configuration =====================
    private static final int MAX_TASKS = 100; // avoid magic number

    // Commands and keywords (avoid magic strings)
    private static final String CMD_BYE = "bye";
    private static final String CMD_LIST = "list";
    private static final String CMD_MARK = "mark ";
    private static final String CMD_UNMARK = "unmark ";
    private static final String CMD_TODO = "todo ";
    private static final String CMD_DEADLINE = "deadline ";
    private static final String CMD_EVENT = "event ";

    private static final String KW_BY = "/by";
    private static final String KW_FROM = "/from";
    private static final String KW_TO = "/to";

    // ========================= Domain Model (A-Inheritance) =========================
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
        Todo(String description) { super(description); }
        @Override public String toString() { return "[T]" + super.toString(); }
    }

    static class Deadline extends Task {
        private final String by;
        Deadline(String description, String by) {
            super(description);
            this.by = by;
        }
        @Override public String toString() { return "[D]" + super.toString() + " (by: " + by + ")"; }
    }

    static class Event extends Task {
        private final String from;
        private final String to;
        Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }
        @Override public String toString() { return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")"; }
    }

    // ========================= Storage =========================
    private static final Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;

    // ========================= Command Parsing =========================
    private enum CommandType { LIST, MARK, UNMARK, TODO, DEADLINE, EVENT, BYE, UNKNOWN }

    private record Command(CommandType type, String arg) { }

    /** Parses the raw input into a high-level command and its argument (if any). */
    private static Command parseCommand(String input) {
        if (input == null || input.isBlank()) {
            return new Command(CommandType.UNKNOWN, null);
        }
        if (input.equals(CMD_BYE)) {
            return new Command(CommandType.BYE, null);
        } else if (input.equals(CMD_LIST)) {
            return new Command(CommandType.LIST, null);
        } else if (input.startsWith(CMD_MARK)) {
            return new Command(CommandType.MARK, input.substring(CMD_MARK.length()).trim());
        } else if (input.startsWith(CMD_UNMARK)) {
            return new Command(CommandType.UNMARK, input.substring(CMD_UNMARK.length()).trim());
        } else if (input.startsWith(CMD_TODO)) {
            return new Command(CommandType.TODO, input.substring(CMD_TODO.length()).trim());
        } else if (input.startsWith(CMD_DEADLINE)) {
            return new Command(CommandType.DEADLINE, input.substring(CMD_DEADLINE.length()).trim());
        } else if (input.startsWith(CMD_EVENT)) {
            return new Command(CommandType.EVENT, input.substring(CMD_EVENT.length()).trim());
        } else {
            // Level 5: unknown commands are errors (do not auto-convert to todo)
            return new Command(CommandType.UNKNOWN, input);
        }
    }

    // ========================= REPL =========================
    /** Runs the Resonant chatbot REPL until the user types "bye". */
    public static void main(String[] args) {
        greet();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = scanner.nextLine().trim();
                Command cmd = parseCommand(input);

                if (cmd.type() == CommandType.BYE) {
                    box(" Bye. Hope to see you again soon!");
                    break; // guard clause keeps happy path un-indented
                }

                execute(cmd);
            }
        }
    }

    // ========================= Command Execution =========================
    private static void execute(Command cmd) {
        switch (cmd.type()) {
            case LIST -> printList();
            case MARK -> handleMarkUnmark(cmd.arg(), true);
            case UNMARK -> handleMarkUnmark(cmd.arg(), false);
            case TODO -> handleTodo(cmd.arg());
            case DEADLINE -> handleDeadline(cmd.arg());
            case EVENT -> handleEvent(cmd.arg());
            case UNKNOWN -> {
                // Level 5: friendly error for unknown/empty commands
                box(" Sorry, I don't recognise that command.",
                        " Try one of: list | todo <desc> | deadline <desc> /by <when> | event <desc> /from <start> /to <end> | mark N | unmark N | bye");
            }
            case BYE -> { /* handled in main */ }
        }
    }

    // ===== Command Handlers =====
    private static void handleTodo(String desc) {
        if (desc == null || desc.isEmpty()) {
            // Level 5: explicit empty-description error
            box(" Whoops — a todo needs a description.",
                    " Example: todo borrow book");
            return;
        }
        addTask(new Todo(desc));
    }

    /** Handle body of "deadline <desc> /by <when>" (body excludes the leading keyword). */
    private static void handleDeadline(String body) {
        if (body == null || body.isEmpty()) {
            box(" Please provide a description and /by.",
                    " Example: deadline return book /by Sunday");
            return;
        }
        String[] split = splitOnKeyword(body, KW_BY);
        String desc = split[0];
        String by = split[1];
        if (by == null) {
            box(" Missing '/by'.",
                    " Example: deadline return book /by Sunday");
            return;
        }
        if (desc.isEmpty()) {
            box(" Deadline description cannot be empty.");
            return;
        }
        addTask(new Deadline(desc, by));
    }

    /** Handle body of "event <desc> /from <start> /to <end>" (body excludes the leading keyword). */
    private static void handleEvent(String body) {
        if (body == null || body.isEmpty()) {
            box(" Please provide a description, /from and /to.",
                    " Example: event project meeting /from Mon 2pm /to 4pm");
            return;
        }
        String[] splitFrom = splitOnKeyword(body, KW_FROM);
        String desc = splitFrom[0];
        String fromPart = splitFrom[1];
        if (fromPart == null) {
            box(" Missing '/from'.",
                    " Example: event project meeting /from Mon 2pm /to 4pm");
            return;
        }
        String[] splitTo = splitOnKeyword(fromPart, KW_TO);
        String from = splitTo[0];
        String to = splitTo[1];
        if (to == null) {
            box(" Missing '/to'.",
                    " Example: event project meeting /from Mon 2pm /to 4pm");
            return;
        }
        if (desc.isEmpty()) {
            box(" Event description cannot be empty.");
            return;
        }
        if (from.isEmpty() || to.isEmpty()) {
            box(" Please provide both start and end times for the event.");
            return;
        }
        addTask(new Event(desc, from, to));
    }

    /** Handles both "mark N" and "unmark N" (N is 1-based). */
    private static void handleMarkUnmark(String indexText, boolean mark) {
        if (indexText == null || indexText.isEmpty()) {
            box(" Please provide a task number, e.g., " + (mark ? "mark 2" : "unmark 2"));
            return;
        }
        try {
            int idx = Integer.parseInt(indexText);
            if (idx < 1 || idx > taskCount) {
                box(" Task number out of range. You have " + taskCount + " task(s).");
                return;
            }
            Task t = tasks[idx - 1];
            if (mark) {
                t.mark();
                box(" Nice! I've marked this task as done:", "   " + t);
            } else {
                t.unmark();
                box(" OK, I've marked this task as not done yet:", "   " + t);
            }
        } catch (NumberFormatException e) {
            box(" That doesn't look like a number. Use e.g., " + (mark ? "mark 2" : "unmark 2"));
        }
    }

    // ========================= Utilities =========================
    private static void greet() {
        box(" Hello! I'm Resonant", " What can I do for you?");
    }

    /** Splits a text by the first occurrence of {@code keyword}. Returns [leftTrimmed, rightTrimmedOrNull]. */
    private static String[] splitOnKeyword(String text, String keyword) {
        int idx = text.indexOf(keyword);
        if (idx == -1) {
            return new String[] { text.trim(), null };
        }
        String left = text.substring(0, idx).trim();
        String right = text.substring(idx + keyword.length()).trim();
        return new String[] { left, right };
    }

    /** Adds a task (any type), if capacity allows, and prints the standardized box. */
    private static void addTask(Task task) {
        if (taskCount >= MAX_TASKS) {
            box(" Sorry, your task list is full (" + MAX_TASKS + " items).");
            return;
        }
        tasks[taskCount++] = task;
        box(
                " Got it. I've added this task:",
                "   " + task,
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
            sb.append('\n').append(' ').append(i + 1).append('.').append(tasks[i]);
        }
        box(sb.toString());
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
