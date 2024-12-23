package com.ljx.code.拼图;


import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

/**
 * @author ljx
 * @version 1.0.0
 * @create 2024/12/11 下午4:34
 */
public class EightDigit {

    public static void init(int size) {
        SIZE = size;
        GOAL = new int[size][size];
        for (int i = 0; i < size * size; i++) {
            GOAL[i / size][i % size] = i;
        }
    }

    private static int SIZE = 0;

    private static int step = 0;

    private static final int[][] INCEPTION = {{7, 1, 3}, {5, 0, 4}, {8, 2, 6}};

    private static int[][] GOAL;

    private static Node currentNode;

    private static PriorityQueue<Node> queue = new PriorityQueue<>();

    private static Map<String, Node> cost_so_far = new HashMap<>();

    static class Node implements Comparable<Node> {
        private int cost;

        private int costAll;

        private final int[][] state;

        private Node last;

        public Node(int[][] state) {
            this.state = state;
        }

        @Override
        public int compareTo(Node o) {
            return this.costAll - o.costAll;
        }
    }

    public static int getAnticipateCost(int[][] state) {
        int count = 0;
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                if (state[i][j] == GOAL[i][j]) {
                    continue;
                }
                //目标坐标行数
                int x = state[i][j] / SIZE;
                //目标坐标列数
                int y = state[i][j] % SIZE;
                count += Math.abs(x - i) + Math.abs(y - j);
            }
        }
        return count;
    }

    public static boolean isGoal(int[][] state) {
        return Objects.equals(toString(state), toString(GOAL));
    }

    public static List<int[][]> getNext(Node currentNode) {
        List<int[][]> nextList = new ArrayList<>();
        int i;
        int j = 0;
        sgin:
        for (i = 0; i < currentNode.state.length; i++) {
            for (j = 0; j < currentNode.state[i].length; j++) {
                if (currentNode.state[i][j] == SIZE * SIZE - 1) {
                    break sgin;
                }
            }
        }
        if (i > 0) {
            nextList.add(swapArray(currentNode.state, i, j, i - 1, j));
        }
        if (i < SIZE - 1) {
            nextList.add(swapArray(currentNode.state, i, j, i + 1, j));
        }
        if (j > 0) {
            nextList.add(swapArray(currentNode.state, i, j, i, j - 1));
        }
        if (j < SIZE - 1) {
            nextList.add(swapArray(currentNode.state, i, j, i, j + 1));
        }
        return nextList;
    }

    public static int[][] swapArray(int[][] state, int i1, int j1, int i2, int j2) {
        int[][] swap = Arrays.copyOf(state, state.length);
        for (int i = 0; i < state.length; i++) {
            swap[i] = Arrays.copyOf(state[i], state[i].length);
        }
        swap[i1][j1] = swap[i1][j1] + swap[i2][j2];
        swap[i2][j2] = swap[i1][j1] - swap[i2][j2];
        swap[i1][j1] = swap[i1][j1] - swap[i2][j2];
        return swap;
    }

    public static String toString(int[][] start) {
        return Arrays.deepToString(start).replaceAll("[\\[\\] ]", "");
    }

    public static Stack<int[][]> findPath(int[][] start) {
        aStar(start);
        Stack<int[][]> stack = new Stack<>();
        if (!isGoal(currentNode.state)) {
            return stack;
        }
        Node node = currentNode;
        while (node.last != null) {
            stack.add(node.state);
            node = node.last;
        }
        System.out.println("cost:" + currentNode.cost);
        System.out.println("step:" + step);
        return stack;
    }

    /**
     * A*算法
     */
    public static void aStar(int[][] init) {
        step = 0;
        queue.clear();
        cost_so_far.clear();
        currentNode = new Node(init);
        queue.add(currentNode);
        while (!queue.isEmpty()) {
            currentNode = queue.poll();
            if (isGoal(currentNode.state)) {
                return;
            }
            List<int[][]> nextList = getNext(currentNode);
            for (int[][] next : nextList) {
                int cost = currentNode.cost + 1;
                Node nextNode = cost_so_far.get(toString(next));
                if (nextNode == null || cost < nextNode.cost) {
                    if (nextNode == null) {
                        nextNode = new Node(next);
                        cost_so_far.put(toString(next), nextNode);
                    }
                    nextNode.cost = cost;
                    nextNode.costAll = cost + getAnticipateCost(next);
                    nextNode.last = currentNode;
                    queue.add(nextNode);
                }
            }
            step++;
        }
    }


    public static void main(String[] args) {
        int[][] start = {
                {2, 1, 0, 5},
                {4, 10, 8, 3},
                {12, 14, 6, 7},
                {15, 13, 11, 9}};
//        int[][] start = {
//                {0, 1, 2, 3},
//                {4, 15, 5, 7},
//                {8, 9, 6, 10},
//                {12, 13, 14, 11}};
        init(4);
        LocalTime startTime = LocalTime.now();
        Stack<int[][]> path = findPath(start);
        Duration duration = Duration.between(startTime, LocalTime.now());
        System.out.println("时间：" + duration.toMillis());
//        while (!path.isEmpty()) {
//            int[][] pop = path.pop();
//            System.out.println(toString(pop));
//        }
    }
}





