package com.ljx.code.拼图;



import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Stack;

public class PuzzleGame extends JFrame {
    // 游戏面板
    private JPanel panel;
    // 拼图尺寸(3x3)
    private int size = 4;
    // 每个方块的像素大小
    private int tileSize = 100;
    // 存储拼图数字的二维数组
    private int[][] tiles;
    // 空白方块的位置坐标
    private int emptyX, emptyY;
    // 是否完成拼图的标志
    private boolean solved = false;
    // 存储拼图方块图片的二维数组
    private BufferedImage[][] imageTiles;

    /**
     * 构造函数：初始化游戏界面和组件
     */
    public PuzzleGame() {
        setTitle("拼图游戏");
        setSize(size * tileSize + 16, size * tileSize + 62);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        tiles = new int[size][size];
        imageTiles = splitImage("E:\\project\\Java\\game\\src\\main\\resources\\image.png");
        initializeTiles();
        shuffleTiles();
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTiles(g);
            }
        };
        panel.setPreferredSize(new Dimension(size * tileSize, size * tileSize));
        panel.setBackground(Color.WHITE);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / tileSize;
                int y = e.getY() / tileSize;
                moveTile(x, y);
                panel.repaint();
                checkSolution();
            }
        });
        JButton resetButton = new JButton("重置");
        JButton oneClickRestoreButton = new JButton("一键复原");
        JButton hintButton = new JButton("提示");
        resetButton.addActionListener(e -> {
            shuffleTiles();
            solved = false;
            panel.repaint();
        });
        oneClickRestoreButton.addActionListener(e -> {
            restorePuzzle();
            solved = true;
            panel.repaint();
        });
        hintButton.addActionListener(e -> {
            if (!solved) {
//                giveHint();
                panel.repaint();
            }
        });
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        // 创建一个面板来放置按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetButton);
        buttonPanel.add(oneClickRestoreButton);
        buttonPanel.add(hintButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 初始化拼图数组
     */
    private void initializeTiles() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                tiles[i][j] = count++;
            }
        }
        emptyX = size - 1;
        emptyY = size - 1;
    }

    /**
     * 随机打乱拼图
     */
    private void shuffleTiles() {
        for (int i = 0; i < 100; i++) {
            int random = new Random().nextInt(4);
            if (random == 0) {
                moveTile(emptyX, emptyY - 1);
            }
            if (random == 1) {
                moveTile(emptyX, emptyY + 1);
            }
            if (random == 2) {
                moveTile(emptyX - 1, emptyY);
            }
            if (random == 3) {
                moveTile(emptyX + 1, emptyY);
            }
        }
    }

    /**
     * 移动方块
     */
    private void moveTile(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) {
            return;
        }
        if ((x == emptyX && Math.abs(y - emptyY) == 1) ||
                (y == emptyY && Math.abs(x - emptyX) == 1)) {
            int temp = tiles[y][x];
            tiles[y][x] = tiles[emptyY][emptyX];
            tiles[emptyY][emptyX] = temp;
            BufferedImage tempImage = imageTiles[y][x];
            imageTiles[y][x] = imageTiles[emptyY][emptyX];
            imageTiles[emptyY][emptyX] = tempImage;
            emptyX = x;
            emptyY = y;
        }
    }

    /**
     * 加载并分割图片
     */
    private BufferedImage[][] splitImage(String imagePath) {
        BufferedImage[][] imageTiles = new BufferedImage[size][size];
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            // 缩放图片到拼图面板的大小
            Image scaledImage = originalImage.getScaledInstance(size * tileSize, size * tileSize, Image.SCALE_SMOOTH);
            BufferedImage scaledBufferedImage = new BufferedImage(size * tileSize, size * tileSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledBufferedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();
            int tileWidth = scaledBufferedImage.getWidth() / size;
            int tileHeight = scaledBufferedImage.getHeight() / size;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    imageTiles[i][j] = scaledBufferedImage.getSubimage(
                            j * tileWidth,
                            i * tileHeight,
                            tileWidth,
                            tileHeight
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("无法加载图片: " + e.getMessage());
        }
        return imageTiles;
    }

    /**
     * 绘制拼图方块
     */
    private void drawTiles(Graphics g) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (tiles[i][j] == size * size - 1) {
                    g.setColor(Color.WHITE);
                    g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                } else if (imageTiles != null) {
                    g.drawImage(imageTiles[i][j], j * tileSize, i * tileSize, null);
                }
            }
        }
    }

    /**
     * 检查拼图是否完成
     */
    private void checkSolution() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (tiles[i][j] != count++) {
                    return;
                }
            }
        }
        solved = true;
        JOptionPane.showMessageDialog(this, "恭喜你完成拼图！");
    }

    /**
     * 提示功能：自动移动一个方块
     */
    private void giveHint() {
        // 找到空白方块周围的方块
        int[] directions = {-1, 0, 1, 0, -1}; // 方向数组，表示上、右、下、左
        for (int i = 0; i < 4; i++) {
            int newX = emptyX + directions[i];
            int newY = emptyY + directions[i + 1];
            if (newX >= 0 && newX < size && newY >= 0 && newY < size) {
                // 尝试移动该方块
                moveTile(newX, newY);
                // 检查是否更接近完成
                if (solved) {
                    return;
                }
                // 如果没有更接近完成，则撤销移动
                moveTile(newX, newY);
            }
        }
    }

    /**
     * 一键复原拼图
     */
    private void restorePuzzle() {
        EightDigit.init(size);
        Stack<int[][]> path = EightDigit.findPath(tiles);
        JOptionPane.showMessageDialog(this, "需要步数：" + path.size());
        Timer timer = new Timer(200, null);
        timer.addActionListener(e -> {
            if (path.isEmpty()) {
                timer.stop();
                return;
            }
            int[][] next = path.pop();
            sgin:
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (tiles[i][j] != next[i][j] && next[i][j] == size * size - 1) {
                        moveTile(j, i);
                        panel.repaint();
                        break sgin;
                    }
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        PuzzleGame puzzleGame = new PuzzleGame();
        SwingUtilities.invokeLater(() -> {
            puzzleGame.setVisible(true);
        });
        System.out.println();
    }
}
