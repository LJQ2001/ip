package resonant;

import resonant.commands.*;

public class Parser {
    private static final String CMD_BYE = "bye";
    private static final String CMD_LIST = "list";
    private static final String CMD_MARK = "mark ";
    private static final String CMD_UNMARK = "unmark ";
    private static final String CMD_TODO = "todo ";
    private static final String CMD_DEADLINE = "deadline ";
    private static final String CMD_EVENT = "event ";
    private static final String CMD_DELETE = "delete ";
    private static final String CMD_FIND = "find ";

    private static final String KW_BY = "/by";
    private static final String KW_FROM = "/from";
    private static final String KW_TO = "/to";

    public static Command parse(String input) throws DukeException {
        if (input == null || input.isBlank()) throw unknown(input);

        if (input.equals(CMD_BYE)) return new ExitCommand();
        if (input.equals(CMD_LIST)) return new ListCommand();

        if (input.startsWith(CMD_MARK)) {
            return new MarkCommand(parseIndex(input.substring(CMD_MARK.length()), "mark"));
        }
        if (input.startsWith(CMD_UNMARK)) {
            return new UnmarkCommand(parseIndex(input.substring(CMD_UNMARK.length()), "unmark"));
        }
        if (input.startsWith(CMD_DELETE)) {
            return new DeleteCommand(parseIndex(input.substring(CMD_DELETE.length()), "delete"));
        }
        if (input.startsWith(CMD_TODO)) {
            String desc = input.substring(CMD_TODO.length()).trim();
            return new AddTodoCommand(desc);
        }
        if (input.startsWith(CMD_DEADLINE)) {
            String body = input.substring(CMD_DEADLINE.length()).trim();
            String[] s = splitOnKeyword(body, KW_BY);
            if (s[1] == null) throw new DukeException("Missing '/by'. Usage: deadline <desc> /by <when>");
            return new AddDeadlineCommand(s[0], s[1]);
        }
        if (input.startsWith(CMD_EVENT)) {
            String body = input.substring(CMD_EVENT.length()).trim();
            String[] fromSplit = splitOnKeyword(body, KW_FROM);
            if (fromSplit[1] == null) throw new DukeException("Missing '/from'. Usage: event <desc> /from <start> /to <end>");
            String[] toSplit = splitOnKeyword(fromSplit[1], KW_TO);
            if (toSplit[1] == null) throw new DukeException("Missing '/to'. Usage: event <desc> /from <start> /to <end>");
            return new AddEventCommand(fromSplit[0], toSplit[0], toSplit[1]);
        }

        if (input.startsWith(CMD_FIND)) {
            String kw = input.substring(CMD_FIND.length()).trim();
            return new FindCommand(kw);
        }

        throw unknown(input);
    }

    private static DukeException unknown(String raw) {
        String unknown = raw == null ? "" : " '" + raw + "'";
        return new DukeException(
                "I don’t recognise that command" + unknown + ".\n" +
                        "Try: list | todo <desc> | deadline <desc> /by <when> | " +
                        "event <desc> /from <start> /to <end> | mark N | unmark N | delete N | find <keyword> | bye"
        );
    }

    private static int parseIndex(String s, String action) throws DukeException {
        if (s == null || s.isBlank()) throw new DukeException("Provide a task number. Usage: " + action + " N");
        try {
            int idx = Integer.parseInt(s.trim());
            if (idx < 1) throw new NumberFormatException();
            return idx;
        } catch (NumberFormatException e) {
            throw new DukeException("Task number must be a positive integer. Example: " + action + " 2");
        }
    }

    private static String[] splitOnKeyword(String text, String keyword) {
        int idx = text.indexOf(keyword);
        if (idx == -1) return new String[]{ text.trim(), null };
        String left = text.substring(0, idx).trim();
        String right = text.substring(idx + keyword.length()).trim();
        return new String[]{ left, right };
    }
}
