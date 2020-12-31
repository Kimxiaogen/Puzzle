package model;

/**
 * @author Kim小根
 * @date 2020/12/29 13:44
 * <p>Description:</p>
 */
public class Bit {
    private int[] tree;
    private int n;

    public Bit(int n) {
        this.n = n;
        this.tree = new int[n + 1];
    }

    public int lowbit(int x) {
        return x & (-x);
    }

    public int query(int x) {
        int ret = 0;
        while (x != 0) {
            ret += tree[x];
            x -= lowbit(x);
        }
        return ret;
    }

    public void update(int x) {
        while (x <= n) {
            ++tree[x];
            x += lowbit(x);
        }
    }
}
