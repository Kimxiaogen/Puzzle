package model;

import java.util.*;

/**
 * @author Kim小根
 * @date 2020/12/30 11:19
 * <p>Description:记录当前拼图状态和路径</p>
 */
public class Path {
    private static final int LEFT = 0;
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;

    private static Map<String, Path> pathMap;

    private static String status(int[] map) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < map.length; i++) {
            s.append(map[i]);
        }
        return s.toString();
    }

    public static void initialMap(){
        pathMap = new HashMap<>();
    }

    public static Path foundPath(int[] map, int n, int blank, List<Integer> move, int g_score, int h_score) {
        String status = status(map);
        Path path;
        if (pathMap.containsKey(status)) {
            path = pathMap.get(status);
            /*if (path.getTotalCost() > g_score + h_score) {
                path.setMove(move);
                path.setG_score(g_score);
                path.setH_score(h_score);
            }*/
        } else {
            path = new Path(map, n, blank, move, g_score, h_score);
            pathMap.put(status, path);
        }
        return path;
    }

    /**
     * 当前拼图状态
     */
    private int[] map;

    /**
     * 拼图边长
     */
    private int n;

    /**
     * 当前空白块下标
     */
    private int blank;

    //记录移动步骤，以空白块为移动单位，0表示左；1表示上；2表示右；3表示下
    private List<Integer> move;


    /**
     * 当前状态距离起始状态得分
     */
    private int g_score;

    /**
     * 当前状态距离目标状态得分
     */
    private int h_score;

    private Path(int[] map, int n, int blank, List<Integer> move, int g_score, int h_score) {
        this.map = map;
        this.n = n;
        this.blank = blank;
        this.move = move;
        this.g_score = g_score;
        this.h_score = h_score;
    }

    public int[] getMap() {
        return map;
    }

    public void setMap(int[] map) {
        this.map = map;
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

    public List<Integer> getMove() {
        return move;
    }

    public void setMove(List<Integer> move) {
        this.move = move;
    }

    public int getG_score() {
        return g_score;
    }

    public void setG_score(int g_score) {
        this.g_score = g_score;
    }

    public int getH_score() {
        return h_score;
    }

    public void setH_score(int h_score) {
        this.h_score = h_score;
    }

    /**
     * 是否到达目标状态
     *
     * @return
     */
    public boolean ReachGoal() {
        for (int i = 0; i < this.map.length; i++) {
            if (this.map[i] != i) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算总得分
     *
     * @return
     */
    public int getTotalCost() {
        return this.g_score + 5 * this.h_score;
    }

    /**
     * 获取当前路径所有可行路线
     */
    public List<Path> getAvailablePath() {
        List<Path> result = new ArrayList<>();
        int lastMove = this.move.size() > 0 ? this.move.get(this.move.size() - 1) : -1;   //得到上一次移动
        int avoidMoveBack = lastMove == -1 ? -1 : (lastMove + 2) % 4;     //避免走回头路
        if (avoidMoveBack != LEFT && this.blank % this.n != 0) {  //可以向左移动
            int[] map = Arrays.copyOf(this.map, this.map.length);
            List<Integer> move = new ArrayList<>(this.move);
            move(map, move, LEFT);  //移动
            Path left = Path.foundPath(map, this.n, this.blank - 1, move, this.g_score + 1, moveLeft());
            result.add(left);
        }
        if (avoidMoveBack != UP && this.blank >= this.n) {    //可以向上移动
            int[] map = Arrays.copyOf(this.map, this.map.length);
            List<Integer> move = new ArrayList<>(this.move);
            move(map, move, UP);  //移动
            Path up =  Path.foundPath(map, this.n, this.blank - this.n, move, this.g_score + 1, moveUp());
            result.add(up);
        }
        if (avoidMoveBack != RIGHT && (this.blank + 1) % this.n != 0) {    //可以向右移动
            int[] map = Arrays.copyOf(this.map, this.map.length);
            List<Integer> move = new ArrayList<>(this.move);
            move(map, move, RIGHT);  //移动
            Path right =  Path.foundPath(map, this.n, this.blank + 1, move, this.g_score + 1, moveRight());
            result.add(right);
        }
        if (avoidMoveBack != DOWN && this.blank < this.n * (this.n - 1)) {    //可以向下移动
            int[] map = Arrays.copyOf(this.map, this.map.length);
            List<Integer> move = new ArrayList<>(this.move);
            move(map, move, DOWN);  //移动
            Path down =  Path.foundPath(map, this.n, this.blank + this.n, move, this.g_score + 1, moveDown());
            result.add(down);
        }
        return result;
    }

    private void move(int[] map, List<Integer> move, int direction) {
        int temp = map[this.blank]; //获取空格
        int next = 0;   //定义空格移动后的位置
        move.add(direction);
        switch (direction) {
            case LEFT:
                next = this.blank - 1;
                break;
            case UP:
                next = this.blank - this.n;
                break;
            case RIGHT:
                next = this.blank + 1;
                break;
            case DOWN:
                next = this.blank + this.n;
                break;
        }
        map[this.blank] = map[next];
        map[next] = temp;
    }

    /**
     * 计算左移后的h_score
     *
     * @return
     */
    private int moveLeft() {
        int left = this.blank - 1, left_goal = this.map[left];
        int x_blank = this.blank % this.n, x_blank_goal = this.n - 1;
        int x = left % this.n, x_goal = left_goal % this.n;
        return h_score - Math.abs(x_blank - x_blank_goal) - Math.abs(x - x_goal)
                + Math.abs(x - x_blank_goal) + Math.abs(x_blank - x_goal);
    }

    /**
     * 计算右移后的h_score
     *
     * @return
     */
    private int moveRight() {
        int right = this.blank + 1, right_goal = this.map[right];
        int x_blank = this.blank % this.n, x_blank_goal = this.n - 1;
        int x = right % this.n, x_goal = right_goal % this.n;
        return h_score - Math.abs(x_blank - x_blank_goal) - Math.abs(x - x_goal)
                + Math.abs(x - x_blank_goal) + Math.abs(x_blank - x_goal);
    }

    /**
     * 计算上移后的h_score
     *
     * @return
     */
    private int moveUp() {
        int up = this.blank - this.n, up_goal = this.map[up];
        int y_blank = this.blank / this.n, y_blank_goal = this.n - 1;
        int y = up / this.n, y_goal = up_goal / this.n;
        return h_score - Math.abs(y_blank - y_blank_goal) - Math.abs(y - y_goal)
                + Math.abs(y - y_blank_goal) + Math.abs(y_blank - y_goal);
    }

    /**
     * 计算下移后的h_score
     *
     * @return
     */
    private int moveDown() {
        int down = this.blank + this.n, down_goal = this.map[down];
        int y_blank = this.blank / this.n, y_blank_goal = this.n - 1;
        int y = down / this.n, y_goal = down_goal / this.n;
        return h_score - Math.abs(y_blank - y_blank_goal) - Math.abs(y - y_goal)
                + Math.abs(y - y_blank_goal) + Math.abs(y_blank - y_goal);
    }

    @Override
    public String toString() {
        String show = String.valueOf(this.getTotalCost()) + "-";
        int lastMove = this.move.size() > 0 ? this.move.get(this.move.size() - 1) : -1;   //得到上一次移动
        switch (lastMove) {
            case LEFT:
                show += "LEFT";
                break;
            case UP:
                show += "UP";
                break;
            case RIGHT:
                show += "RIGHT";
                break;
            case DOWN:
                show += "DOWN";
                break;
            default:
                show = "ERROR";
                break;
        }
        return show;
    }
}
