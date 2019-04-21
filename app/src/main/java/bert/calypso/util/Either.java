package bert.calypso.util;

import androidx.annotation.NonNull;

public class Either<L, R> {

    private final L left;
    private final R right;

    @NonNull
    public static <L, R> Either<L, R> left(L left) {
        return new Either<>(left, null);
    }

    @NonNull
    public static <L, R> Either<L, R> right(R right) {
        return new Either<>(null, right);
    }

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
}
