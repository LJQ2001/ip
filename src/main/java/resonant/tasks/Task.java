package resonant.tasks;

public class Task {
    protected final String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void mark() { this.isDone = true; }
    public void unmark() { this.isDone = false; }
    public boolean isDone() { return isDone; }
    public String description() { return description; }

    public String getStatusIcon() { return this.isDone ? "X" : " "; }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + this.description;
    }
}
