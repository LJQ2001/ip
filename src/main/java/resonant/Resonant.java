package resonant;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Resonant {
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

    // Storage with Collections
    private static final List<Task> tasks = new ArrayList<>();

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
                default -> {
                    // no-op
                }
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
            String[] split = splitOnKeyword(body, KW_BY);
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
            } catch (NumberFormatException e) {
                throw new DukeException("Task number must be a positive integer. Example: " + action + " 2");
            }

            if (idx >= 1 && idx <= tasks.size()) {
                Task t = tasks.get(idx - 1);
                if (mark) {
                    t.mark();
                    box(" Nice! I've marked this task as done:", "   " + t);
                } else {
                    t.unmark();
                    box(" OK, I've marked this task as not done yet:", "   " + t);
                }
            } else {
                throw new DukeException("Task number " + idx + " is out of range. You have " + tasks.size() + " task(s).");
            }
        } else {
            throw new DukeException("Provide a task number. Usage: " + action + " N");
        }
    }

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

        if (idx < 1 || idx > tasks.size()) {
            throw new DukeException("Task number " + idx + " is out of range. You have " + tasks.size() + " task(s).");
        }

        Task removed = tasks.remove(idx - 1);

        box(
                " Noted. I've removed this task:",
                "   " + removed,
                " Now you have " + tasks.size() + " " + (tasks.size() == 1 ? "task" : "tasks") + " in the list."
        );
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

    private static void addTask(Task task) {
        tasks.add(task);
        box(
                " Got it. I've added this task:",
                "   " + task,
                " Now you have " + tasks.size() + " " + (tasks.size() == 1 ? "task" : "tasks") + " in the list."
        );
    }

    private static void printList() {
        if (tasks.isEmpty()) {
            box(" Your list is empty.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(" Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); ++i) {
                sb.append("\n ").append(i + 1).append('.').append(tasks.get(i));
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

    // ===== Data model =====

    static class Task {
        protected final String description;
        protected boolean isDone;

        Task(String description) {
            this.description = description;
            this.isDone = false;
        }

        void mark() { this.isDone = true; }
        void unmark() { this.isDone = false; }

        String getStatusIcon() {
            return this.isDone ? "X" : " ";
        }

        @Override
        public String toString() {
            return "[" + getStatusIcon() + "] " + this.description;
        }
    }

    static class Todo extends Task {
        Todo(String description) { super(description); }

        @Override
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
