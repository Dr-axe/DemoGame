package dataDeals;

public class Pair<L, R> {
    private L left;  
    private R right; 

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() { return left; }
    public R getRight() { return right; }

    public void setLeft(L newLeft) { this.left = newLeft; }
    public void setRight(R newRight) { this.right = newRight; }

    public Pair<L, R> withLeft(L newLeft) {
        this.left = newLeft;
        return this;
    }

    public Pair<R, L> swap() {
        return new Pair<>(this.right, this.left);
    }

    @Override
    public String toString() {
        return "(" + left + ", " + right + ")";
    }
}