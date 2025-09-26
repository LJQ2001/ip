package resonant;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList; // for save buffers
import java.util.List;     // for save buffers
import java.util.Scanner;

public class Resonant {
    private static final int MAX_TASKS = 100;

    // Commands
    private static final String CMD_BYE = "bye";
    private static final String CMD_LIST = "list";
    private static final String CMD_MARK = "mark ";
    private static final String CMD_UNMARK = "unmark ";
    private static final String CMD_TODO = "todo ";
    private static final String CMD_DEADLINE = "deadline ";
    private static final String CMD_EVENT = "event ";
    private static final String CMD_DELETE = "delete ";

    // Keywords
    private static final String KW_BY = "/by";
    private static final String KW_FROM = "/from";
    private static final String KW_TO = "/to";

    // Level 7: Save paths (relative + OS-independent)
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path DATA_FILE = DATA_DIR.resolve("resonant.txt");

    // Storage (array-based)
    private static final Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;

    private static Command parseCommand(String input) {
        if (input != null && !input.isBlank()) {
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
            } else if (input.startsWith(CMD_DELETE)) {
                return new Command(CommandType.DELETE, input.substring(CMD_DELETE.length()).trim());
            } else {
                return new Command(CommandType.UNKNOWN, input);
            }
        } else {
            return new Command(CommandType.UNKNOWN, null);
        }
    }

    public static void main(String[] args) {
        // Level 7: Load tasks before greeting
        try {
            loadTasks();
        } catch (IOException e) {
            box(" OOPS!!! Couldn't load saved tasks. Starting fresh.");
        }

        greet();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = scanner.nextLine().trim();
                Command cmd = parseCommand(input);
                if (cmd.type() == CommandType.BYE) {
                    box(" Bye. Hope to see you again soon!");
                    return;
                }
                execute(cmd);
            }
        }
    }

    private static void execute(Command cmd) {
        try {
            // Use enum-based switch (safe when adding commands)
            switch (cmd.type()) {
                case LIST -> printList();
                case MARK -> handleMarkUnmark(cmd.arg(), true);
                case UNMARK -> handleMarkUnmark(cmd.arg(), false);
                case TODO -> handleTodo(cmd.arg());
                case DEADLINE -> handleDeadline(cmd.arg());
                case EVENT -> handleEvent(cmd.arg());
                case DELETE -> handleDelete(cmd.arg());
                case UNKNOWN -> {
                    String unknown = cmd.arg() == null ? "" : " '" + cmd.arg() + "'";
                    throw new DukeException(
                            "I don’t recognise that command" + unknown + ".\n" +
                                    "Try: list | todo <desc> | deadline <desc> /by <when> | " +
                                    "event <desc> /from <start> /to <end> | mark N | unmark N | delete N | bye"
                    );
                }
                case BYE -> { /* handled in main loop */ }
            }
        } catch (DukeException e) {
            box(" OOPS!!! " + e.getMessage());
        }
    }

    // ===== Handlers =====

    private static void handleTodo(String desc) throws DukeException {
        if (desc != null && !desc.isEmpty()) {
            addTask(new Todo(desc));
            try { saveTasks(); } catch (IOException ignored) {}
        } else {
            throw new DukeException("A todo needs a description. Usage: todo <desc>");
        }
    }

    private static void handleDeadline(String body) throws DukeException {
        if (body != null && !body.isEmpty()) {
            String[] split = splitOnKeyword(body, KW_BY);
            String desc = split[0];
            String by = split[1];
            if (by == null) {
                throw new DukeException("Missing '/by'. Usage: deadline <desc> /by <when>");
            } else if (desc.isEmpty()) {
                throw new DukeException("Deadline description cannot be empty.");
            } else {
                addTask(new Deadline(desc, by));
                try { saveTasks(); } catch (IOException ignored) {}
            }
        } else {
            throw new DukeException("Deadline requires a description and '/by'. Usage: deadline <desc> /by <when>");
        }
    }

    private static void handleEvent(String body) throws DukeException {
        if (body != null && !body.isEmpty()) {
            String[] splitFrom = splitOnKeyword(body, KW_FROM);
            String desc = splitFrom[0];
            String fromPart = splitFrom[1];
            if (fromPart == null) {
                throw new DukeException("Missing '/from'. Usage: event <desc> /from <start> /to <end>");
            } else {
                String[] splitTo = splitOnKeyword(fromPart, KW_TO);
                String from = splitTo[0];
                String to = splitTo[1];
                if (to == null) {
                    throw new DukeException("Missing '/to'. Usage: event <desc> /from <start> /to <end>");
                } else if (desc.isEmpty()) {
                    throw new DukeException("Event description cannot be empty.");
                } else if (!from.isEmpty() && !to.isEmpty()) {
                    addTask(new Event(desc, from, to));
                    try { saveTasks(); } catch (IOException ignored) {}
                } else {
                    throw new DukeException("Please provide both start and end times. Example: event project /from Mon 2pm /to 4pm");
                }
            }
        } else {
            throw new DukeException("Event requires a description, '/from', and '/to'. Usage: event <desc> /from <start> /to <end>");
        }
    }

    private static void handleMarkUnmark(String indexText, boolean mark) throws DukeException {
        String action = mark ? "mark" : "unmark";
        if (indexText == null || indexText.isEmpty()) {
            throw new DukeException("Provide a task number. Usage: " + action + " N");
        }

        int idx;
        try {
            idx = Integer.parseInt(indexText);
        } catch (NumberFormatException e) {
            throw new DukeException("Task number must be a positive integer. Example: " + action + " 2");
        }

        if (idx < 1 || idx > taskCount) {
            throw new DukeException("Task number " + idx + " is out of range. You have " + taskCount + " task(s).");
        }

        Task t = tasks[idx - 1];
        if (mark) {
            t.mark();
            box(" Nice! I've marked this task as done:", "   " + t);
        } else {
            t.unmark();
            box(" OK, I've marked this task as not done yet:", "   " + t);
        }
        try { saveTasks(); } catch (IOException ignored) {}
    }

    // Level 6: Delete
    private static void handleDelete(String indexText) throws DukeException {
        if (indexText == null || indexText.isEmpty()) {
            throw new DukeException("Provide a task number. Usage: delete N");
        }

        int idx;
        try {
            idx = Integer.parseInt(indexText);
        } catch (NumberFormatException e) {
            throw new DukeException("Task number must be a positive integer. Example: delete 3");
        }

        if (idx < 1 || idx > taskCount) {
            throw new DukeException("Task number " + idx + " is out of range. You have " + taskCount + " task(s).");
        }

        Task removed = tasks[idx - 1];

        // Shift left to fill the gap
        for (int i = idx; i < taskCount; i++) {
            tasks[i - 1] = tasks[i];
        }
        tasks[--taskCount] = null; // clear last slot

        box(
                " Noted. I've removed this task:",
                "   " + removed,
                " Now you have " + taskCount + " " + (taskCount == 1 ? "task" : "tasks") + " in the list."
        );

        try { saveTasks(); } catch (IOException ignored) {}
    }

    // ===== Helpers =====

    private static void greet() {
        box(" Hello! I'm Resonant", " What can I do for you?");
    }

    private static String[] splitOnKeyword(String text, String keyword) {
        int idx = text.indexOf(keyword);
        if (idx == -1) {
            return new String[]{text.trim(), null};
        } else {
            String left = text.substring(0, idx).trim();
            String right = text.substring(idx + keyword.length()).trim();
            return new String[]{left, right};
        }
    }

    private static void addTask(Task task) throws DukeException {
        if (taskCount >= MAX_TASKS) {
            throw new DukeException("Your task list is full (100 items). Consider deleting some tasks.");
        } else {
            tasks[taskCount++] = task;
            box(
                    " Got it. I've added this task:",
                    "   " + task,
                    " Now you have " + taskCount + " " + (taskCount == 1 ? "task" : "tasks") + " in the list."
            );
        }
    }

    private static void printList() {
        if (taskCount == 0) {
            box(" Your list is empty.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(" Here are the tasks in your list:");
            for (int i = 0; i < taskCount; ++i) {
                sb.append('\n').append(' ').append(i + 1).append('.').append(tasks[i]);
            }
            box(sb.toString());
        }
    }

    private static void box(String... lines) {
        System.out.println("____________________________________________________________");
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println("____________________________________________________________");
    }

    // =========================
    // Level 7: Save / Load
    // =========================

    private static void saveTasks() throws IOException {
        if (Files.notExists(DATA_DIR)) {
            Files.createDirectories(DATA_DIR);
        }

        List<String> lines = new ArrayList<>(taskCount);
        for (int i = 0; i < taskCount; i++) {
            Task t = tasks[i];
            boolean done = t.isDone;

            if (t instanceof Todo) {
                lines.add(String.join(" | ", "T", done ? "1" : "0", t.description));
            } else if (t instanceof Deadline d) {
                lines.add(String.join(" | ", "D", done ? "1" : "0", d.description, d.by));
            } else if (t instanceof Event e) {
                lines.add(String.join(" | ", "E", done ? "1" : "0", e.description, e.from, e.to));
            } else {
                // fallback (shouldn't happen)
                lines.add(String.join(" | ", "T", done ? "1" : "0", t.description));
            }
        }

        Files.write(DATA_FILE, lines, StandardCharsets.UTF_8);
    }

    private static void loadTasks() throws IOException {
        if (Files.notExists(DATA_FILE)) {
            if (Files.notExists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }
            return; // first run: nothing to load
        }

        List<String> lines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
        taskCount = 0;

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;

            // Expected formats:
            // T | 1 | desc
            // D | 0 | desc | by
            // E | 1 | desc | from | to
            String[] parts = line.split("\\|");
            if (parts.length < 3) continue; // corrupted, skip

            String type = parts[0].trim();
            boolean done = "1".equals(parts[1].trim());

            try {
                switch (type) {
                    case "T": {
                        String desc = joinRest(parts, 2);
                        if (desc.isEmpty()) break;
                        Task t = new Todo(desc);
                        if (done) t.mark();
                        if (taskCount < MAX_TASKS) tasks[taskCount++] = t;
                        break;
                    }
                    case "D": {
                        if (parts.length < 4) break; // corrupt
                        String desc = parts[2].trim();
                        String by = joinRest(parts, 3);
                        Task t = new Deadline(desc, by);
                        if (done) t.mark();
                        if (taskCount < MAX_TASKS) tasks[taskCount++] = t;
                        break;
                    }
                    case "E": {
                        if (parts.length < 5) break; // corrupt
                        String desc = parts[2].trim();
                        String from = parts[3].trim();
                        String to = joinRest(parts, 4);
                        Task t = new Event(desc, from, to);
                        if (done) t.mark();
                        if (taskCount < MAX_TASKS) tasks[taskCount++] = t;
                        break;
                    }
                    default:
                        // unknown type — skip
                }
            } catch (Exception ignored) {
                // skip malformed lines gracefully
            }
        }
    }

    private static String joinRest(String[] parts, int start) {
        // Re-join with " | " to preserve user-entered separators
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < parts.length; i++) {
            if (i > start) sb.append(" | ");
            sb.append(parts[i].trim());
        }
        return sb.toString();
    }

    // =========================
    // Data model
    // =========================

    static class Task {
        protected final String description;
        protected boolean isDone;

        Task(String description) {
            this.description = description;
            this.isDone = false;
        }

        void mark() { this.isDone = true; }
        void unmark() { this.isDone = false; }

        String getStatusIcon() { return this.isDone ? "X" : " "; }

        @Override
        public String toString() {
            return "[" + getStatusIcon() + "] " + this.description;
        }
    }

    static class Todo extends Task {
        Todo(String description) { super(description); }

        @Override
        public String toString() { return "[T]" + super.toString(); }
    }

    static class Deadline extends Task {
        private final String by;

        Deadline(String description, String by) {
            super(description);
            this.by = by;
        }

        @Override
        public String toString() {
            return "[D]" + super.toString() + " (by: " + this.by + ")";
        }
    }

    static class Event extends Task {
        private final String from;
        private final String to;

        Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "[E]" + super.toString() + " (from: " + this.from + " to: " + this.to + ")";
        }
    }

    private static enum CommandType {
        LIST,
        MARK,
        UNMARK,
        TODO,
        DEADLINE,
        EVENT,
        DELETE,
        BYE,
        UNKNOWN;
    }

    private static record Command(CommandType type, String arg) { }
}
