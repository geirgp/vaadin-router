package tv.geir.commons;

/**
 * Created by geir on 08/01/17.
 */
public class Pair<L, R> {

    public L left;
    public R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Pair<L, R> of(L left, R right) {
        if (left != null && right != null) {
            return new Pair(left, right);
        }
        throw new NullPointerException("left or right is null");
    }

    public static <L, R> Pair<L, R> ofNullable(L left, R right) {
        return new Pair(left, right);
    }

    public boolean hasRight() {
        return right != null;
    }
}
