package resonant;

import resonant.commands.*;

/**
 * Parses raw user input into executable {@link Command} objects.
 * <p>
 * Supported commands include: {@code bye}, {@code list}, {@code mark},
 * {@code unmark}, {@code delete}, {@code todo}, {@code deadline}, {@code event},
 * and {@code find}.
 */
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

    /**
     * Parses a line of user input and returns the corresponding {@link Command}.
     *
     * @param input Full input line from the user.
     * @return A {@link Command} instance that performs the requested action.
     * @throws DukeException If the command is unknown or malformed (e.g., missing required arguments).
     */
    public static Command parse(String input) throws DukeException {
        if (input == null || input.isBlank()) {
            throw unknown(input);
        }

        if (input.equals(CMD_BYE)) {
            return new ExitCommand();
        }
        if (input.equals(CMD_LIST)) {
            return new ListCommand();
        }

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
            if (s[1] == null) {
                throw new DukeException("Missing '/by'. Usage: deadline <desc> /by <when>");
            }
            return new AddDeadlineCommand(s[0], s[1]);
        }
        if (input.startsWith(CMD_EVENT)) {
            String body = input.substring(CMD_EVENT.length()).trim();
            String[] fromSplit = splitOnKeyword(body, KW_FROM);
            if (fromSplit[1] == null) {
                throw new DukeException("Missing '/from'. Usage: event <desc> /from <start> /to <end>");
            }
            String[] toSplit = splitOnKeyword(fromSplit[1], KW_TO);
            if (toSplit[1] == null) {
                throw new DukeException("Missing '/to'. Usage: event <desc> /from <start> /to <end>");
            }
            return new AddEventCommand(fromSplit[0], toSplit[0], toSplit[1]);
        }

        if (input.startsWith(CMD_FIND)) {
            String kw = input.substring(CMD_FIND.length()).trim();
            return new FindCommand(kw);
        }

        throw unknown(input);
    }

    /**
     * Creates a standardized {@link DukeException} for unknown commands, including a help hint.
     *
     * @param raw The raw input that failed to parse (may be {@code null}).
     * @return A {@link DukeException} describing the error and listing valid commands.
     */
    private static DukeException unknown(String raw) {
        String unknown = raw == null ? "" : " '" + raw + "'";
        return new DukeException(
                "I don’t recognise that command" + unknown + ".\n" +
                        "Try: list | todo <desc> | deadline <desc> /by <when> | " +
                        "event <desc> /from <start> /to <end> | mark N | unmark N | delete N | find <keyword> | bye"
        );
    }

    /**
     * Parses a 1-based task index from a string for commands that operate on a single task.
     *
     * @param s      The string containing the index.
     * @param action The action name (used for error messages), e.g., {@code "mark"}.
     * @return The parsed 1-based index.
     * @throws DukeException If the index is missing, non-numeric, or not positive.
     */
    private static int parseIndex(String s, String action) throws DukeException {
        if (s == null || s.isBlank()) {
            throw new DukeException("Provide a task number. Usage: " + action + " N");
        }
        try {
            int idx = Integer.parseInt(s.trim());
            if (idx < 1) {
                throw new NumberFormatException();
            }
            return idx;
        } catch (NumberFormatException e) {
            throw new DukeException("Task number must be a positive integer. Example: " + action + " 2");
        }
    }

    /**
     * Splits a command body on the first occurrence of a keyword and returns
     * the trimmed left and right parts.
     *
     * @param text    The full command body, e.g., {@code "return book /by June 6th"}.
     * @param keyword The delimiter keyword, e.g., {@code "/by"}.
     * @return A two-element array: {@code [left, right]} where {@code right} may be {@code null} if the keyword is absent.
     */
    private static String[] splitOnKeyword(String text, String keyword) {
        int idx = text.indexOf(keyword);
        if (idx == -1) {
            return new String[]{ text.trim(), null };
        }
        String left = text.substring(0, idx).trim();
        String right = text.substring(idx + keyword.length()).trim();
        return new String[]{ left, right };
    }
}
