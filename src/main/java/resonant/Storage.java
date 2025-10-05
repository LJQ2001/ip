package resonant;

import resonant.tasks.Deadline;
import resonant.tasks.Event;
import resonant.tasks.Task;
import resonant.tasks.Todo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading and writing of task data to persistent storage.
 * <p>
 * The {@code Storage} class is responsible for loading saved tasks from a file and
 * saving updated task lists back to disk. Tasks are stored in a simple pipe-delimited format,
 * compatible with previous Duke implementations.
 * </p>
 *
 * <p>
 * Example file contents:
 * <pre>
 * T | 1 | read book
 * D | 0 | submit report | 2025-10-10
 * E | 1 | attend meeting | 10am | 12pm
 * </pre>
 * </p>
 */
public class Storage {

    /** The directory where task data is stored. */
    private final Path dataDir;

    /** The file where task data is saved. */
    private final Path dataFile;

    /**
     * Constructs a {@code Storage} instance with the given file path.
     * <p>
     * If the provided path is {@code null} or blank, a default file
     * named {@code resonant.txt} will be used in the {@code data/} directory.
     * </p>
     *
     * @param filePath the relative path to the storage file (can be {@code null})
     */
    public Storage(String filePath) {
        this.dataDir = Paths.get("data");
        this.dataFile = dataDir.resolve(filePath == null || filePath.isBlank() ? "resonant.txt" : filePath);
    }

    /**
     * Loads all tasks from the data file into memory.
     * <p>
     * This method reads the file line-by-line, parses each entry, and reconstructs
     * the appropriate {@link Task} objects (e.g. {@link Todo}, {@link Deadline}, {@link Event}).
     * If the file does not exist, it will create the required directories and return an empty list.
     * </p>
     *
     * @return a list of {@link Task} objects loaded from the file
     * @throws IOException if an I/O error occurs while reading or creating the file
     */
    public List<Task> load() throws IOException {
        if (Files.notExists(dataFile)) {
            if (Files.notExists(dataDir)) Files.createDirectories(dataDir);
            return List.of();
        }
        List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
        List<Task> tasks = new ArrayList<>();

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;

            // Expected formats:
            // T | 1 | desc
            // D | 0 | desc | by
            // E | 1 | desc | from | to
            String[] parts = line.split("\\|");
            if (parts.length < 3) continue;

            try {
                String type = parts[0].trim();
                boolean done = "1".equals(parts[1].trim());
                switch (type) {
                    case "T" -> {
                        String desc = joinRest(parts, 2);
                        if (!desc.isEmpty()) {
                            Task t = new Todo(desc);
                            if (done) t.mark();
                            tasks.add(t);
                        }
                    }
                    case "D" -> {
                        if (parts.length >= 4) {
                            String desc = parts[2].trim();
                            String by = joinRest(parts, 3);
                            Task t = new Deadline(desc, by);
                            if (done) t.mark();
                            tasks.add(t);
                        }
                    }
                    case "E" -> {
                        if (parts.length >= 5) {
                            String desc = parts[2].trim();
                            String from = parts[3].trim();
                            String to = joinRest(parts, 4);
                            Task t = new Event(desc, from, to);
                            if (done) t.mark();
                            tasks.add(t);
                        }
                    }
                    default -> { /* no-op for unknown types */}
                }
            } catch (Exception ignored) {
                // Skip malformed lines gracefully
            }
        }
        return tasks;
    }

    /**
     * Saves the given list of tasks to the storage file.
     * <p>
     * Each task is serialized into a pipe-delimited line and written to disk.
     * If the storage directory does not exist, it will be created automatically.
     * </p>
     *
     * @param tasks the list of tasks to save
     * @throws IOException if an I/O error occurs while writing the file
     */
    public void save(List<Task> tasks) throws IOException {
        if (Files.notExists(dataDir)) Files.createDirectories(dataDir);

        List<String> lines = new ArrayList<>(tasks.size());
        for (Task t : tasks) {
            boolean done = t.isDone();
            if (t instanceof Todo) {
                Todo todo = (Todo) t;
                lines.add(String.join(" | ", "T", done ? "1" : "0", todo.description()));
            } else if (t instanceof Deadline) {
                Deadline d = (Deadline) t;
                lines.add(String.join(" | ", "D", done ? "1" : "0", d.description(), d.by()));
            } else if (t instanceof Event) {
                Event e = (Event) t;
                lines.add(String.join(" | ", "E", done ? "1" : "0", e.description(), e.from(), e.to()));
            } else {
                lines.add(String.join(" | ", "T", done ? "1" : "0", t.description()));
            }
        }
        Files.write(
                dataFile,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    /**
     * Joins all array elements starting from the given index using {@code " | "} as a separator.
     * <p>
     * This helper is used when reconstructing task fields (e.g. descriptions that may contain delimiters).
     * </p>
     *
     * @param parts the split string array
     * @param start the starting index for concatenation
     * @return a trimmed string consisting of all joined parts
     */
    private static String joinRest(String[] parts, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < parts.length; i++) {
            if (i > start) sb.append(" | ");
            sb.append(parts[i].trim());
        }
        return sb.toString();
    }
}
