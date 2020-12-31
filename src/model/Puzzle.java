package model;

import java.util.*;

/**
 * @author Kim小根
 * @date 2020/12/28 15:43
 * <p>Description:拼图类</p>
 */
public class Puzzle {
    private static final int LEFT = 0;
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;

    /**
     * 定义空白块
     */
    private static final String BLANK = " ";

    /**
     * 拼图结构，这里使用一维数组来表示二维结构的拼图
     */
    private int[] arr;

    /**
     * 拼图阶数
     */
    private int n;

    /**
     * 空白块所在下标
     */
    private int blank;

    /**
     * 记录移动步骤，以空白块为移动单位，0表示左；1表示上；2表示右；3表示下
     */
    private Path finalPath;

    /**
     * 计算距离模型
     */
    private Distance distance;

    public int[] getArr() {
        return arr;
    }

    public void setArr(int[] arr) {
        this.arr = arr;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getBlank() {
        return blank;
    }

    public void setBlank(int blank) {
        this.blank = blank;
    }

    public Path getFinalPath() {
        return finalPath;
    }

    public void setFinalPath(Path finalPath) {
        this.finalPath = finalPath;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Puzzle(int n, int method) {
        this.n = n;
        this.arr = new int[n * n];
        this.distance = new Distance(method);
        for (int i = 0; i < this.n * this.n - 1; i++) {
            this.arr[i] = i;
        }
        //定义空白块
        this.blank = n * n - 1;
        this.arr[this.blank] = n * n - 1;
        do {
            shufflePuzzle();    //打乱拼图
        } while (!solvability());
    }

    /**
     * 使用Fisher–Yates洗牌算法打乱拼图顺序，默认右下角为空白块
     */
    private void shufflePuzzle() {
        Random rd = new Random();
        int i = 0;
        while (i < n * n - 1) {
            int j = rd.nextInt(i + 1);
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
            i++;
        }

    }

    /**
     * 验证当前拼图是否可还原
     *
     * @return
     */
    private boolean solvability() {
        int[] tmp = Arrays.copyOf(this.arr, this.arr.length);
        int count = reversePairs(tmp);//记录逆序对个数
        return (count & 1) == 0;    //判断逆序对是否为偶数，因为目标逆序对为0（偶数）
    }

    public int reversePairs(int[] nums) {
        int n = nums.length;
        int[] tmp = new int[n];
        System.arraycopy(nums, 0, tmp, 0, n);
        // 离散化
        Arrays.sort(tmp);
        for (int i = 0; i < n; ++i) {
            nums[i] = Arrays.binarySearch(tmp, nums[i]) + 1;
        }
        // 树状数组统计逆序对
        Bit bit = new Bit(n);
        int ans = 0;
        for (int i = n - 1; i >= 0; --i) {
            ans += bit.query(nums[i] - 1);
            bit.update(nums[i]);
        }
        return ans;
    }

    /**
     * 根据坐标（x,y）找到对应拼图块下标
     *
     * @param x x轴坐标（0到n-1）
     * @param y y轴坐标（0到n-1）
     * @return 拼图块下标（0到n*n-1）
     */
    public int getPuzzleIndex(int x, int y) {
        return y * this.n + x;
    }

    /**
     * 控制台打印拼图
     */
    public void printPuzzle() {
        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.n; j++) {
                int index = getPuzzleIndex(j, i);
                if (this.arr[index] == n * n - 1) {
                    System.out.print(BLANK + "\t");
                } else {
                    System.out.print(this.arr[index] + "\t");
                }
            }
            System.out.println();
        }
    }

    /**
     * 按照最终路径执行拼图
     */
    public void moveAsFinalPath() {
        List<Integer> move = this.finalPath.getMove();
        if (move.size() == 0) {
            System.out.println("无需移动");
            return;
        }
        int n = move.size(), i = 0;
        do {
            int temp = this.arr[this.blank], x = this.blank % this.n, y = this.blank / this.n;
            int next = -1;
            switch (move.get(i)) {
                case LEFT:
                    next = getPuzzleIndex(x - 1, y);
                    break;
                case UP:
                    next = getPuzzleIndex(x, y - 1);
                    break;
                case RIGHT:
                    next = getPuzzleIndex(x + 1, y);
                    break;
                case DOWN:
                    next = getPuzzleIndex(x, y + 1);
                    break;
            }
            this.arr[this.blank] = this.arr[next];
            this.arr[next] = temp;
            this.blank = next;
            System.out.println("第" + (i + 1) + "次移动后：");
            printPuzzle();
        } while (++i < n);
    }


    /**
     * 还原拼图
     */
    public void solution() {
        List<Path> open = new LinkedList<>(), close = new LinkedList<>();   //open为待验证路径集合，path为已验证路径集合
        int[] tmp = Arrays.copyOf(this.arr, this.arr.length);
        Path start = Path.foundPath(tmp, this.n, this.blank, new LinkedList<>(), 0, this.distance.score(tmp, this.n));
        open.add(start);
        while (true) {
            Path p = open.remove(0);   //每次获取最小代价的路径，并从open中删除
            List<Path> nextAvailablePath;
            if (close.contains(p)) {
                continue;
            }
            if (p.ReachGoal()) {
                this.finalPath = p;
                break;
            }
            nextAvailablePath = p.getAvailablePath();
            for (Path next : nextAvailablePath) {
                insertPath(open, next);
            }
            close.add(p);
        }
        //moveAsFinalPath();
    }

    /**
     * 采用二分插入法插入路径
     *
     * @param open 路径集合
     * @param path 待插入路径
     */
    private void insertPath(List<Path> open, Path path) {
        int left = 0, right = open.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int mid_score = open.get(mid).getTotalCost(), cmp_score = path.getTotalCost();
            if (mid_score == cmp_score) {
                open.add(mid, path);
                return;
            } else if (mid_score < cmp_score) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        open.add(left, path);
    }
}
