package main;

import model.Path;
import model.Puzzle;

/**
 * @author Kim小根
 * @date 2020/12/28 16:02
 * <p>Description:</p>
 */
public class Main {

    public static void main(String[] args) {
        int n = 4;
        int method = 0;
        float start, end;
        float avg = 0;
        float t = 1000;
        int times = 100;//测试次数
        for (int i = 0; i < times; i++) {
            Puzzle p = new Puzzle(n, method);
            Path.initialMap();
            p.printPuzzle();
            start = System.currentTimeMillis();
            p.solution();
            end = System.currentTimeMillis();
            avg += (end - start) / t;
            System.out.println("还原拼图用时：" + (end - start) / t + "s");
        }
        avg /= times;
        System.out.println("平均用时：" + avg + "s");
    }
}
