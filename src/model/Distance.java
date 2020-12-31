package model;

/**
 * @author Kim小根
 * @date 2020/12/29 14:44
 * <p>Description:计算当前状态到最终状态的距离</p>
 */
public class Distance {
    /**
     * 当前使用算法
     */
    private int method;

    /**
     * 0表示使用曼哈顿距离
     *
     * @param method
     */
    public Distance(int method) {
        this.method = method;
    }

    public int score(int[] arr, int n) {
        switch (this.method) {
            case 0:
                return manhattanDistance(arr,n);
            default:
                return 0;
        }
    }

    private int manhattanDistance(int[] arr,int n) {
        int distance = 0;
        for (int i = 0; i < arr.length; i++) {
            int x_goal = arr[i] / n, y_goal = arr[i] - x_goal * n;
            int x_current = i / n, y_current = i - x_current * n;
            distance += Math.abs(x_goal - x_current) + Math.abs(y_goal - y_current);
        }
        return distance;
    }
}
