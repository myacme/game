package com.ljx.code.象棋;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChessGame extends JFrame {
    // 棋盘大小
    private static final int BOARD_SIZE = 10;
    private static final int CELL_SIZE = 60;
    // 边距
    private static final int MARGIN = 40;
    private static final int RIGHT_PANEL = 60; // 减小右侧空间
    
    // 棋子数组
    private ChessPiece[][] board;
    
    // 当前选中的棋子
    private Point selectedPiece = null;
    
    // 当前玩家(红方true,黑方false) 
    private boolean redTurn = true;
    
    // 是否将军状态
    private boolean isCheck = false;
    
    public ChessGame() {
        setTitle("中国象棋");
        // 调整窗口大小
        setSize(9 * CELL_SIZE + MARGIN * 2 + RIGHT_PANEL, 
               10 * CELL_SIZE + MARGIN * 2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // 初始化棋盘数组
        board = new ChessPiece[10][9];
        initializeBoard();
        
        // 创建棋盘面板
        JPanel boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 移动绘图原点，添加边距
                g.translate(MARGIN, MARGIN);
                drawBoard(g);
                drawPieces(g);
                // 绘制将军提示
                if (isCheck) {
                    g.setColor(Color.RED);
                    g.setFont(new Font("黑体", Font.BOLD, 24)); // 调整字体大小
                    String checkText = "将军！";
                    g.drawString(checkText, 
                               9 * CELL_SIZE - 20,  // 调整位置
                               5 * CELL_SIZE);
                }
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(9 * CELL_SIZE + MARGIN * 2 + RIGHT_PANEL, 
                                   10 * CELL_SIZE + MARGIN * 2);
            }
        };
        
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX() - MARGIN, e.getY() - MARGIN);
                boardPanel.repaint();
            }
        });
        
        add(boardPanel);
    }
    
    // 初始化棋子位置
    private void initializeBoard() {
        // 红方棋子（底部）
        board[9][0] = new ChessPiece("车", true);
        board[9][1] = new ChessPiece("马", true);
        board[9][2] = new ChessPiece("相", true);
        board[9][3] = new ChessPiece("士", true);
        board[9][4] = new ChessPiece("帅", true);
        board[9][5] = new ChessPiece("士", true);
        board[9][6] = new ChessPiece("相", true);
        board[9][7] = new ChessPiece("马", true);
        board[9][8] = new ChessPiece("车", true);
        board[7][1] = new ChessPiece("炮", true);
        board[7][7] = new ChessPiece("炮", true);
        board[6][0] = new ChessPiece("兵", true);
        board[6][2] = new ChessPiece("兵", true);
        board[6][4] = new ChessPiece("兵", true);
        board[6][6] = new ChessPiece("兵", true);
        board[6][8] = new ChessPiece("兵", true);
        
        // 黑方棋子（顶部）
        board[0][0] = new ChessPiece("车", false);
        board[0][1] = new ChessPiece("马", false);
        board[0][2] = new ChessPiece("象", false);
        board[0][3] = new ChessPiece("士", false);
        board[0][4] = new ChessPiece("将", false);
        board[0][5] = new ChessPiece("士", false);
        board[0][6] = new ChessPiece("象", false);
        board[0][7] = new ChessPiece("马", false);
        board[0][8] = new ChessPiece("车", false);
        board[2][1] = new ChessPiece("炮", false);
        board[2][7] = new ChessPiece("炮", false);
        board[3][0] = new ChessPiece("卒", false);
        board[3][2] = new ChessPiece("卒", false);
        board[3][4] = new ChessPiece("卒", false);
        board[3][6] = new ChessPiece("卒", false);
        board[3][8] = new ChessPiece("卒", false);
    }
    
    // 绘制棋盘
    private void drawBoard(Graphics g) {
        // 设置背景色
        g.setColor(new Color(238, 203, 173));
        g.fillRect(0, 0, (BOARD_SIZE-1) * CELL_SIZE + CELL_SIZE, BOARD_SIZE * CELL_SIZE);
        
        // 设置线条颜色
        g.setColor(Color.BLACK);
        
        // 绘制横线（10条）
        for (int i = 0; i < BOARD_SIZE; i++) {
            g.drawLine(CELL_SIZE/2, i * CELL_SIZE + CELL_SIZE/2, 
                      CELL_SIZE * 8 + CELL_SIZE/2, i * CELL_SIZE + CELL_SIZE/2);
        }
        
        // 绘制竖线（9条）
        for (int i = 0; i < 9; i++) {
            // 上半部分
            g.drawLine(i * CELL_SIZE + CELL_SIZE/2, CELL_SIZE/2,
                      i * CELL_SIZE + CELL_SIZE/2, CELL_SIZE * 4 + CELL_SIZE/2);
            // 下半部分
            g.drawLine(i * CELL_SIZE + CELL_SIZE/2, CELL_SIZE * 5 + CELL_SIZE/2,
                      i * CELL_SIZE + CELL_SIZE/2, CELL_SIZE * 9 + CELL_SIZE/2);
        }
        
        // 绘制楚河汉界
        g.setColor(new Color(200, 150, 100));
        g.fillRect(CELL_SIZE/2, CELL_SIZE * 4 + CELL_SIZE/2, 
                   CELL_SIZE * 8, CELL_SIZE);
        
        // 重新绘制河界的上下边界线
        g.setColor(Color.BLACK);
        g.drawLine(CELL_SIZE/2, CELL_SIZE * 4 + CELL_SIZE/2, 
                  CELL_SIZE * 8 + CELL_SIZE/2, CELL_SIZE * 4 + CELL_SIZE/2);
        g.drawLine(CELL_SIZE/2, CELL_SIZE * 5 + CELL_SIZE/2, 
                  CELL_SIZE * 8 + CELL_SIZE/2, CELL_SIZE * 5 + CELL_SIZE/2);
        
        // 绘制楚河汉界文字
        g.setFont(new Font("隶", Font.BOLD, 40));
        FontMetrics metrics = g.getFontMetrics();
        String riverText = "楚 河          汉 界";
        int stringWidth = metrics.stringWidth(riverText);
        int x = (CELL_SIZE * 8 + CELL_SIZE - stringWidth) / 2;
        int y = CELL_SIZE * 4 + CELL_SIZE + metrics.getAscent()/2;
        g.drawString(riverText, x, y);
        
        // 绘制九宫格斜线
        // 上方九宫格
        g.drawLine(CELL_SIZE * 3 + CELL_SIZE/2, CELL_SIZE/2, 
                  CELL_SIZE * 5 + CELL_SIZE/2, CELL_SIZE * 2 + CELL_SIZE/2);
        g.drawLine(CELL_SIZE * 5 + CELL_SIZE/2, CELL_SIZE/2, 
                  CELL_SIZE * 3 + CELL_SIZE/2, CELL_SIZE * 2 + CELL_SIZE/2);
        
        // 下方九宫格
        g.drawLine(CELL_SIZE * 3 + CELL_SIZE/2, CELL_SIZE * 7 + CELL_SIZE/2, 
                  CELL_SIZE * 5 + CELL_SIZE/2, CELL_SIZE * 9 + CELL_SIZE/2);
        g.drawLine(CELL_SIZE * 5 + CELL_SIZE/2, CELL_SIZE * 7 + CELL_SIZE/2, 
                  CELL_SIZE * 3 + CELL_SIZE/2, CELL_SIZE * 9 + CELL_SIZE/2);
        
        // 绘制边框
        g.setColor(Color.BLACK);
        g.drawRect(CELL_SIZE/2, CELL_SIZE/2, 
                   CELL_SIZE * 8, CELL_SIZE * 9);
    }
    
    // 绘制棋子位置标记（田字格）
    private void drawPositionMark(Graphics g, int row, int col) {
        int x = col * CELL_SIZE + CELL_SIZE/2;
        int y = row * CELL_SIZE + CELL_SIZE/2;
        int size = 10; // 标记大小
        
        // 左上
        if (col > 0) g.drawLine(x - size, y - size/2, x - size/2, y - size/2);
        if (row > 0) g.drawLine(x - size/2, y - size, x - size/2, y - size/2);
        
        // 右上
        if (col < BOARD_SIZE-1) g.drawLine(x + size/2, y - size/2, x + size, y - size/2);
        if (row > 0) g.drawLine(x + size/2, y - size, x + size/2, y - size/2);
        
        // 左下
        if (col > 0) g.drawLine(x - size, y + size/2, x - size/2, y + size/2);
        if (row < BOARD_SIZE-1) g.drawLine(x - size/2, y + size/2, x - size/2, y + size);
        
        // 右下
        if (col < BOARD_SIZE-1) g.drawLine(x + size/2, y + size/2, x + size, y + size/2);
        if (row < BOARD_SIZE-1) g.drawLine(x + size/2, y + size/2, x + size/2, y + size);
    }
    
    // 绘制棋子
    private void drawPieces(Graphics g) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != null) {
                    ChessPiece piece = board[i][j];
                    int x = j * CELL_SIZE + CELL_SIZE/2;
                    int y = i * CELL_SIZE + CELL_SIZE/2;
                    
                    // 绘制棋子底色
                    g.setColor(new Color(238, 203, 173));
                    g.fillOval(x - CELL_SIZE/3, y - CELL_SIZE/3, 
                              CELL_SIZE*2/3, CELL_SIZE*2/3);
                    
                    // 绘制棋子边框
                    g.setColor(Color.BLACK);
                    g.drawOval(x - CELL_SIZE/3, y - CELL_SIZE/3, 
                              CELL_SIZE*2/3, CELL_SIZE*2/3);
                    
                    // 设置字体
                    g.setFont(new Font("宋体", Font.BOLD, 30));
                    
                    // 绘制棋子文字
                    g.setColor(piece.isRed() ? Color.RED : Color.BLACK);
                    FontMetrics metrics = g.getFontMetrics();
                    int stringWidth = metrics.stringWidth(piece.getName());
                    int stringHeight = metrics.getHeight();
                    g.drawString(piece.getName(), 
                               x - stringWidth/2,
                               y + stringHeight/3);
                }
            }
        }
        
        // 如果有选中的棋子，绘制选中标记
        if (selectedPiece != null) {
            g.setColor(Color.GREEN);
            int x = selectedPiece.x * CELL_SIZE + CELL_SIZE/2;
            int y = selectedPiece.y * CELL_SIZE + CELL_SIZE/2;
            g.drawRect(x - CELL_SIZE/3, y - CELL_SIZE/3, 
                      CELL_SIZE*2/3, CELL_SIZE*2/3);
        }
    }
    
    // 处理鼠标点击
    private void handleClick(int x, int y) {
        int boardX = x / CELL_SIZE;
        int boardY = y / CELL_SIZE;
        
        // 确保点击在有效范围内
        if (boardX < 0 || boardX >= 9 || boardY < 0 || boardY >= 10 || 
            x < 0 || y < 0) {
            return;
        }
        
        if (selectedPiece == null) {
            // 确保选中的是当前回合的棋子
            if (board[boardY][boardX] != null && 
                board[boardY][boardX].isRed() == redTurn) {
                selectedPiece = new Point(boardX, boardY);
            }
        } else {
            // 尝试移动棋子
            if (isValidMove(selectedPiece.x, selectedPiece.y, 
                           boardX, boardY)) {
                movePiece(selectedPiece.x, selectedPiece.y,
                         boardX, boardY);
            }
            selectedPiece = null;
        }
    }
    
    // 判断移动是否合法
    private boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        ChessPiece piece = board[fromY][fromX];
        if (piece == null) return false;
        
        // 不能吃自己的棋子
        if (board[toY][toX] != null && 
            board[toY][toX].isRed() == piece.isRed()) {
            return false;
        }
        
        String pieceName = piece.getName();
        boolean isRedPiece = piece.isRed();
        
        switch (pieceName) {
            case "帅":
            case "将":
                return isValidGeneralMove(fromX, fromY, toX, toY, isRedPiece);
            case "士":
                return isValidAdvisorMove(fromX, fromY, toX, toY, isRedPiece);
            case "相":
            case "象":
                return isValidElephantMove(fromX, fromY, toX, toY, isRedPiece);
            case "马":
                return isValidHorseMove(fromX, fromY, toX, toY);
            case "车":
                return isValidChariotMove(fromX, fromY, toX, toY);
            case "炮":
                return isValidCannonMove(fromX, fromY, toX, toY);
            case "兵":
            case "卒":
                return isValidPawnMove(fromX, fromY, toX, toY, isRedPiece);
            default:
                return false;
        }
    }
    
    // 将/帅的移动规则
    private boolean isValidGeneralMove(int fromX, int fromY, int toX, int toY, boolean isRed) {
        // 限制在九宫格内
        int minX = 3, maxX = 5;
        int minY = isRed ? 7 : 0;
        int maxY = isRed ? 9 : 2;
        
        if (toX < minX || toX > maxX || toY < minY || toY > maxY) {
            return false;
        }
        
        // 只能上下左右走一步
        return (Math.abs(fromX - toX) + Math.abs(fromY - toY) == 1);
    }
    
    // 士的移动规则
    private boolean isValidAdvisorMove(int fromX, int fromY, int toX, int toY, boolean isRed) {
        // 限制在九宫格内
        int minX = 3, maxX = 5;
        int minY = isRed ? 7 : 0;
        int maxY = isRed ? 9 : 2;
        
        if (toX < minX || toX > maxX || toY < minY || toY > maxY) {
            return false;
        }
        
        // 只能斜着走一步
        return (Math.abs(fromX - toX) == 1 && Math.abs(fromY - toY) == 1);
    }
    
    // 相/象的移动规则
    private boolean isValidElephantMove(int fromX, int fromY, int toX, int toY, boolean isRed) {
        // 不能过河
        if (isRed && toY < 5 || !isRed && toY > 4) {
            return false;
        }
        
        // 走田字
        if (Math.abs(fromX - toX) != 2 || Math.abs(fromY - toY) != 2) {
            return false;
        }
        
        // 检查象眼是否被塞住
        int eyeX = (fromX + toX) / 2;
        int eyeY = (fromY + toY) / 2;
        return board[eyeY][eyeX] == null;
    }
    
    // 马的移动规则
    private boolean isValidHorseMove(int fromX, int fromY, int toX, int toY) {
        int deltaX = Math.abs(fromX - toX);
        int deltaY = Math.abs(fromY - toY);
        
        if (!((deltaX == 1 && deltaY == 2) || (deltaX == 2 && deltaY == 1))) {
            return false;
        }
        
        // 检查马腿
        if (deltaX == 2) {
            return board[fromY][(fromX + toX) / 2] == null;
        } else {
            return board[(fromY + toY) / 2][fromX] == null;
        }
    }
    
    // 车的移动规则
    private boolean isValidChariotMove(int fromX, int fromY, int toX, int toY) {
        if (fromX != toX && fromY != toY) {
            return false;
        }
        
        // 检查路径上是否有其他棋子
        if (fromX == toX) {
            int minY = Math.min(fromY, toY);
            int maxY = Math.max(fromY, toY);
            for (int y = minY + 1; y < maxY; y++) {
                if (board[y][fromX] != null) {
                    return false;
                }
            }
        } else {
            int minX = Math.min(fromX, toX);
            int maxX = Math.max(fromX, toX);
            for (int x = minX + 1; x < maxX; x++) {
                if (board[fromY][x] != null) {
                    return false;
                }
            }
        }
        return true;
    }
    
    // 炮的移动规则
    private boolean isValidCannonMove(int fromX, int fromY, int toX, int toY) {
        if (fromX != toX && fromY != toY) {
            return false;
        }
        
        int count = 0; // 路径上的棋子数
        if (fromX == toX) {
            int minY = Math.min(fromY, toY);
            int maxY = Math.max(fromY, toY);
            for (int y = minY + 1; y < maxY; y++) {
                if (board[y][fromX] != null) count++;
            }
        } else {
            int minX = Math.min(fromX, toX);
            int maxX = Math.max(fromX, toX);
            for (int x = minX + 1; x < maxX; x++) {
                if (board[fromY][x] != null) count++;
            }
        }
        
        // 移动时不能有棋子吃子时必须翻过一个棋子
        return (board[toY][toX] == null && count == 0) || 
               (board[toY][toX] != null && count == 1);
    }
    
    // 兵/卒的移动规���
    private boolean isValidPawnMove(int fromX, int fromY, int toX, int toY, boolean isRed) {
        int deltaX = Math.abs(fromX - toX);
        int deltaY = toY - fromY;
        
        // 不能后退
        if (isRed && deltaY >= 0 || !isRed && deltaY <= 0) {
            return false;
        }
        
        // 过河前
        if ((isRed && fromY > 4) || (!isRed && fromY < 5)) {
            // 只能前进一步
            return deltaX == 0 && Math.abs(deltaY) == 1;
        } else {
            // 过河后可以左右移动
            return (deltaX + Math.abs(deltaY) == 1);
        }
    }
    
    // 移动棋子
    private void movePiece(int fromX, int fromY, int toX, int toY) {
        // 保存原来的状态
        ChessPiece targetPiece = board[toY][toX];
        ChessPiece movingPiece = board[fromY][fromX];
        
        // 执行移动
        board[toY][toX] = movingPiece;
        board[fromY][fromX] = null;
        
        // 检查是否将军
        isCheck = false; // 先重置状态
        Point kingPos = findKing(!redTurn);
        if (kingPos != null) {
            // 检查是否将军
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 9; j++) {
                    if (board[i][j] != null && 
                        board[i][j].isRed() == redTurn &&
                        isValidMove(j, i, kingPos.x, kingPos.y)) {
                        isCheck = true;
                        break;
                    }
                }
                if (isCheck) break;
            }
        }
        
        // 更换回合
        redTurn = !redTurn;
    }
    
    // 添加找到将/帅位置的方法
    private Point findKing(boolean findRedKing) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != null && 
                    board[i][j].isRed() == findRedKing && 
                    (board[i][j].getName().equals("帅") || 
                     board[i][j].getName().equals("将"))) {
                    return new Point(j, i);
                }
            }
        }
        return null;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessGame game = new ChessGame();
            game.setLocationRelativeTo(null);  // 使窗口在屏幕中央显示
            game.setVisible(true);
        });
    }
}

// 棋子类
class ChessPiece {
    private String name;
    private boolean isRed;
    
    public ChessPiece(String name, boolean isRed) {
        this.name = name;
        this.isRed = isRed;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isRed() {
        return isRed;
    }
}

