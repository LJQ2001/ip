//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package resonant;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List; // only for loading/saving buffers
import java.util.Scanner;

public class Resonant {
    private static final int MAX_TASKS = 100;
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

    // === Level-7: Save (paths) ===
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path DATA_FILE = DATA_DIR.resolve("resonant.txt");

    private static final Task[] tasks = new Task[100];
    private static int taskCount = 0;

    private static Command parseCommand(String input) {
        if (input != null && !input.isBlank()) {
            if (input.equals("bye")) {
                return new Command(Resonant.CommandType.BYE, (String)null);
            } else if (input.equals("list")) {
                return new Command(Resonant.CommandType.LIST, (String)null);
            } else if (input.startsWith("mark ")) {
                return new Command(Resonant.CommandType.MARK, input.substring("mark ".length()).trim());
            } else if (input.startsWith("unmark ")) {
                return new Command(Resonant.CommandType.UNMARK, input.substring("unmark ".length()).trim());
            } else if (input.startsWith("todo ")) {
                return new Command(Resonant.CommandType.TODO, input.substring("todo ".length()).trim());
            } else if (input.startsWith("deadline ")) {
                return new Command(Resonant.CommandType.DEADLINE, input.substring("deadline ".length()).trim());
            } else {
                return input.startsWith("event ") ? new Command(Resonant.CommandType.EVENT, input.substring("event ".length()).trim()) : new Command(Resonant.CommandType.UNKNOWN, input);
            }
        } else {
            return new Command(Resonant.CommandType.UNKNOWN, (String)null);
        }
    }

    public static void main(String[] args) {
        // Level-7: load previous session first
        try {
            loadTasks();
        } catch (IOException e) {
            // Non-fatal: start with empty list if load fails
            box(" OOPS!!! Couldn't load saved tasks. Starting fresh.");
        }

        greet();

        try (Scanner scanner = new Scanner(System.in)) {
            while(true) {
                String input = scanner.nextLine().trim();
                Command cmd = parseCommand(input);
                if (cmd.type() == Resonant.CommandType.BYE) {
                    box(" Bye. Hope to see you again soon!");
                    return;
                }

                execute(cmd);
            }
        }
    }

    private static void execute(Command cmd) {
        try {
            switch (cmd.type().ordinal()) {
                case 0:
                    printList();
                    break;
                case 1:
                    handleMarkUnmark(cmd.arg(), true);
                    break;
                case 2:
                    handleMarkUnmark(cmd.arg(), false);
                    break;
                case 3:
                    handleTodo(cmd.arg());
                    break;
                case 4:
                    handleDeadline(cmd.arg());
                    break;
                case 5:
                    handleEvent(cmd.arg());
                case 6:
                default:
                    break;
                case 7:
                    String unknown = cmd.arg() == null ? "" : " '" + cmd.arg() + "'";
                    throw new DukeException("I don’t recognise that command" + unknown + ".\nTry: list | todo <desc> | deadline <desc> /by <when> | event <desc> /from <start> /to <end> | mark N | unmark N | bye");
            }
        } catch (DukeException e) {
            box(" OOPS!!! " + e.getMessage());
        }

    }

    private static void handleTodo(String desc) throws DukeException {
        if (desc != null && !desc.isEmpty()) {
            addTask(new Todo(desc));
        } else {
            throw new DukeException("A todo needs a description. Usage: todo <desc>");
        }
    }

    private static void handleDeadline(String body) throws DukeException {
        if (body != null && !body.isEmpty()) {
            String[] split = splitOnKeyword(body, "/by");
            String desc = split[0];
            String by = split[1];
            if (by == null) {
                throw new DukeException("Missing '/by'. Usage: deadline <desc> /by <when>");
            } else if (desc.isEmpty()) {
                throw new DukeException("Deadline description cannot be empty.");
            } else {
                addTask(new Deadline(desc, by));
            }
        } else {
            throw new DukeException("Deadline requires a description and '/by'. Usage: deadline <desc> /by <when>");
        }
    }

    private static void handleEvent(String body) throws DukeException {
        if (body != null && !body.isEmpty()) {
            String[] splitFrom = splitOnKeyword(body, "/from");
            String desc = splitFrom[0];
            String fromPart = splitFrom[1];
            if (fromPart == null) {
                throw new DukeException("Missing '/from'. Usage: event <desc> /from <start> /to <end>");
            } else {
                String[] splitTo = splitOnKeyword(fromPart, "/to");
                String from = splitTo[0];
                String to = splitTo[1];
                if (to == null) {
                    throw new DukeException("Missing '/to'. Usage: event <desc> /from <start> /to <end>");
                } else if (desc.isEmpty()) {
                    throw new DukeException("Event description cannot be empty.");
                } else if (!from.isEmpty() && !to.isEmpty()) {
                    addTask(new Event(desc, from, to));
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
        if (indexText != null && !indexText.isEmpty()) {
            int idx;
            try {
                idx = Integer.parseInt(indexText);
            } catch (NumberFormatException var5) {
                throw new DukeException("Task number must be a positive integer. Example: " + action + " 2");
            }

            if (idx >= 1 && idx <= taskCount) {
                Task t = tasks[idx - 1];
                if (mark) {
                    t.mark();
                    box(" Nice! I've marked this task as done:", "   " + String.valueOf(t));
                } else {
                    t.unmark();
                    box(" OK, I've marked this task as not done yet:", "   " + String.valueOf(t));
                }
                // Level-7: save on change
                try { saveTasks(); } catch (IOException ignored) { }
            } else {
                throw new DukeException("Task number " + idx + " is out of range. You have " + taskCount + " task(s).");
            }
        } else {
            throw new DukeException("Provide a task number. Usage: " + action + " N");
        }
    }

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
        if (taskCount >= 100) {
            throw new DukeException("Your task list is full (100 items). Consider deleting some tasks.");
        } else {
            tasks[taskCount++] = task;
            box(" Got it. I've added this task:",
                    "   " + task,
                    " Now you have " + taskCount + " " + (taskCount == 1 ? "task" : "tasks") + " in the list.");
            // Level-7: save on change
            try { saveTasks(); } catch (IOException ignored) { }
        }
    }

    private static void printList() {
        if (taskCount == 0) {
            box(" Your list is empty.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(" Here are the tasks in your list:");

            for(int i = 0; i < taskCount; ++i) {
                sb.append('\n').append(' ').append(i + 1).append('.').append(tasks[i]);
            }

            box(sb.toString());
        }
    }

    private static void box(String... lines) {
        System.out.println("____________________________________________________________");

        for(String line : lines) {
            System.out.println(line);
        }

        System.out.println("____________________________________________________________");
    }

    // =========================
    // Level-7: Save / Load
    // =========================

    private static void saveTasks() throws IOException {
        // Ensure folder exists
        if (Files.notExists(DATA_DIR)) {
            Files.createDirectories(DATA_DIR);
        }

        List<String> lines = new ArrayList<>(taskCount);
        for (int i = 0; i < taskCount; i++) {
            Task t = tasks[i];
            String type, rest;
            boolean done = t.isDone;

            if (t instanceof Todo) {
                type = "T";
                rest = t.description;
                lines.add(String.join(" | ", type, done ? "1" : "0", rest));
            } else if (t instanceof Deadline d) {
                type = "D";
                rest = t.description + " | " + d.by;
                lines.add(String.join(" | ", type, done ? "1" : "0", rest));
            } else if (t instanceof Event e) {
                type = "E";
                rest = t.description + " | " + e.from + " | " + e.to;
                lines.add(String.join(" | ", type, done ? "1" : "0", rest));
            } else {
                // Unknown subtype fallback
                type = "T";
                rest = t.description;
                lines.add(String.join(" | ", type, done ? "1" : "0", rest));
            }
        }

        Files.write(DATA_FILE, lines, StandardCharsets.UTF_8);
    }

    private static void loadTasks() throws IOException {
        if (Files.notExists(DATA_FILE)) {
            // First run: no file yet — make sure directory exists for later saves
            if (Files.notExists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }
            return;
        }

        List<String> lines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
        taskCount = 0; // reset before loading

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;

            // Expected:
            // T | 1 | desc
            // D | 0 | desc | by
            // E | 1 | desc | from | to
            String[] parts = line.split("\\|");
            // Basic corruption guard
            if (parts.length < 3) {
                // skip corrupted line
                continue;
            }

            String type = parts[0].trim();
            String doneStr = parts[1].trim();
            boolean done = "1".equals(doneStr);

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
                        break;
                }
            } catch (Exception ignored) {
                // skip corrupted/malformed line (stretch goal handling)
            }
        }
    }

    private static String joinRest(String[] parts, int start) {
        // Re-join with '|' to preserve user-entered separators inside fields
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

        void mark() {
            this.isDone = true;
        }

        void unmark() {
            this.isDone = false;
        }

        String getStatusIcon() {
            return this.isDone ? "X" : " ";
        }

        public String toString() {
            String var10000 = this.getStatusIcon();
            return "[" + var10000 + "] " + this.description;
        }
    }

    static class Todo extends Task {
        Todo(String description) {
            super(description);
        }

        public String toString() {
            return "[T]" + super.toString();
        }
    }

    static class Deadline extends Task {
        private final String by;

        Deadline(String description, String by) {
            super(description);
            this.by = by;
        }

        public String toString() {
            String var10000 = super.toString();
            return "[D]" + var10000 + " (by: " + this.by + ")";
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

        public String toString() {
            String var10000 = super.toString();
            return "[E]" + var10000 + " (from: " + this.from + " to: " + this.to + ")";
        }
    }

    private static enum CommandType {
        LIST,
        MARK,
        UNMARK,
        TODO,
        DEADLINE,
        EVENT,
        BYE,
        UNKNOWN;
    }

    private static record Command(CommandType type, String arg) {
    }
}
