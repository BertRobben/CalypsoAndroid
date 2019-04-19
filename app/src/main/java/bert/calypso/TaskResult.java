package bert.calypso;

public final class TaskResult<T> {
    private final T result;
    private final Exception exception;

    public TaskResult(T result) {
        this(result, null);
    }

    public TaskResult(Exception exception) {
        this(null, exception);
    }

    private TaskResult(T result, Exception exception) {
        this.result = result;
        this.exception = exception;
    }

    public T getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }
}
