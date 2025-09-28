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

public class Storage {
    private final Path dataDir;
    private final Path dataFile;

    public Storage(String filePath) {
        // keep compatibility with your original relative path
        this.dataDir = Paths.get("data");
        this.dataFile = dataDir.resolve(filePath == null || filePath.isBlank() ? "resonant.txt" : filePath);
    }

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

            // Formats:
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
                    default -> { /* skip unknown */ }
                }
            } catch (Exception ignored) { /* skip malformed */ }
        }
        return tasks;
    }

    public void save(List<Task> tasks) throws IOException {
        if (Files.notExists(dataDir)) Files.createDirectories(dataDir);

        List<String> lines = new ArrayList<>(tasks.size());
        for (Task t : tasks) {
            boolean done = t.isDone();
            if (t instanceof Todo todo) {
                lines.add(String.join(" | ", "T", done ? "1" : "0", todo.description()));
            } else if (t instanceof Deadline d) {
                lines.add(String.join(" | ", "D", done ? "1" : "0", d.description(), d.by()));
            } else if (t instanceof Event e) {
                lines.add(String.join(" | ", "E", done ? "1" : "0", e.description(), e.from(), e.to()));
            } else {
                lines.add(String.join(" | ", "T", done ? "1" : "0", t.description()));
            }
        }
        Files.write(dataFile, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static String joinRest(String[] parts, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < parts.length; i++) {
            if (i > start) sb.append(" | ");
            sb.append(parts[i].trim());
        }
        return sb.toString();
    }
}
