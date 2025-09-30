package resonant;

import resonant.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskList {
    private final List<Task> tasks;

    public TaskList() { this.tasks = new ArrayList<>(); }

    public TaskList(List<Task> initial) {
        this.tasks = new ArrayList<>(initial == null ? List.of() : initial);
    }

    public int size() { return tasks.size(); }

    public Task get(int index1Based) throws DukeException {
        int i = index1Based - 1;
        if (i < 0 || i >= tasks.size()) {
            throw new DukeException("Task number " + index1Based + " is out of range. You have " + size() + " task(s).");
        }
        return tasks.get(i);
    }

    public void add(Task t) throws DukeException {
        if (tasks.size() >= 100) {
            throw new DukeException("Your task list is full (100 items). Consider deleting some tasks.");
        }
        tasks.add(t);
    }

    public Task remove(int index1Based) throws DukeException {
        Task t = get(index1Based);
        tasks.remove(index1Based - 1);
        return t;
    }

    public List<Task> find(String keyword) {
        String kw = keyword.toLowerCase();
        List<Task> out = new ArrayList<>();
        for (Task t : tasks) {
            if (t.description().toLowerCase().contains(kw)) {
                out.add(t);
            }
        }
        return out;
    }

    public List<Task> asList() { return Collections.unmodifiableList(tasks); }
}
