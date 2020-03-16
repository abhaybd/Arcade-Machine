package com.coolioasjulio.tetris;

import com.coolioasjulio.arcademachine.gameutils.InputManager;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class TetrisGUI {

    private static final int GAME_WIDTH = 10, GAME_HEIGHT = 20;
    private static final double DIFFICULTY = 0.2; // completely arbitrary, affects slope/curvature of delay curve
    private static final double LEVEL_0_DELAY = 400;

    private static final Color SIDE_PANEL_COLOR = new Color(0, 0, 128);
    private static final Color FRAME_BG_COLOR = Color.BLACK;
    private static final Color GAME_BG_COLOR = new Color(32, 32, 32);
    public static final Color TEXT_COLOR = Color.WHITE;

    private static final boolean MOCK_INPUT = false;
    private static final boolean FULL_SCREEN = true;

    private JFrame frame;
    private TetrisBase tetris;
    private TetrisPane pane;
    private JLabel lineLabel;
    private Thread renderThread;
    private volatile boolean paused = false;
    private int blockSize = 50;
    private int borderWidth = blockSize / 15;
    private int lineLabelSize = blockSize * 7 / 5;
    private boolean done;

    public TetrisGUI() {
        tetris = new TetrisBase(GAME_WIDTH, GAME_HEIGHT);

        frame = new JFrame("Tetris");
        frame.getContentPane().setBackground(FRAME_BG_COLOR);
        if (FULL_SCREEN) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
        }
        if (MOCK_INPUT) {
            frame.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {
                    TetrisGUI.this.keyPressed(e.getKeyCode());
                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });
        }
        frame.setVisible(true);
        if (FULL_SCREEN) resize(); // resize the game to fit different screen sizes
        pane = new TetrisPane(GAME_WIDTH, GAME_HEIGHT);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(SIDE_PANEL_COLOR);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(blockSize / 2, blockSize / 2, blockSize / 2, blockSize / 4);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 2;
        panel.add(pane, c);
        c.insets = new Insets(blockSize / 2, blockSize / 4, blockSize / 2, blockSize / 2);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 2;
        JPanel sidePanel = createSidePanel();
        panel.add(sidePanel, c);
        Dimension size = panel.getPreferredSize();
        panel.setMaximumSize(size);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(panel);
        panel.setPreferredSize(size);
        if (!FULL_SCREEN) frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setOpaque(true);
        sidePanel.setBackground(SIDE_PANEL_COLOR);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.add(new NextPiecePane());
        createLabel();
        sidePanel.add(lineLabel);
        return sidePanel;
    }

    private void createLabel() {
        lineLabel = new JLabel();
        Dimension labelSize = new Dimension(7 * blockSize, GAME_HEIGHT / 2 * blockSize);
        lineLabel.setMinimumSize(labelSize);
        lineLabel.setPreferredSize(labelSize);
        lineLabel.setMaximumSize(labelSize);
        lineLabel.setForeground(TEXT_COLOR);
        lineLabel.setFont(new Font("Sans-Serif", Font.PLAIN, lineLabelSize));
        lineLabel.setHorizontalAlignment(JLabel.CENTER);
        lineLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    }

    public void resize() {
        int size = frame.getHeight() / (GAME_HEIGHT + 1);
        setBlockSize(size);
    }

    public void setBlockSize(int size) {
        blockSize = size;
        borderWidth = blockSize / 15;
        lineLabelSize = blockSize * 7 / 5;
    }

    private void renderTask() {
        try {
            while (!Thread.interrupted()) {
                frame.repaint();
                Thread.sleep(20);
            }
        } catch (InterruptedException e) {
        }
    }

    public void playGame() {
        done = false;
        renderThread = new Thread(this::renderTask);
        renderThread.start();
        Thread inputThread = new Thread(this::inputThread);
        if (!MOCK_INPUT) {
            inputThread.start();
        }
        while (true) {
            if (!done && !paused) {
                done = tetris.advanceStep();
                lineLabel.setText(String.format("Lines: % 4d", tetris.getClearedLines()));
                try {
                    Thread.sleep(getDelay());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Thread.yield();
            }
        }
    }

    private void inputThread() {
        InputManager.enable();
        while (!Thread.interrupted()) {
            int[] codes = InputManager.getInputs();
            for (int keyCode : codes) {
                keyPressed(keyCode);
            }
            Thread.yield();
        }
    }

    private long getDelay() {
        int level = tetris.getClearedLines() / 10;
        double a = LEVEL_0_DELAY / Math.log(2.0); // solve for a where f(0) = LEVEL_0_DELAY
        return Math.round(a * Math.log(1.0 + Math.exp(-DIFFICULTY * level)));
    }

    private void resetGame() {
        tetris = new TetrisBase(GAME_WIDTH, GAME_HEIGHT);
        done = false;
    }

    public void keyPressed(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                tetris.left();
                break;

            case KeyEvent.VK_RIGHT:
                tetris.right();
                break;

            case KeyEvent.VK_UP:
                tetris.rotate();
                break;

            case KeyEvent.VK_DOWN:
                tetris.down();
                break;

            case KeyEvent.VK_A:
                if (tetris.hasLost()) {
                    resetGame();
                }
                break;

            case KeyEvent.VK_B:
                if (tetris.hasLost()) {
                    System.exit(0);
                }
                break;

            case KeyEvent.VK_P:
                paused = !paused;
                break;
        }
    }

    private Color getBlockColor(Piece.PieceType block) {
        switch (block) {
            case I:
                return new Color(255, 128, 0);

            case O:
                return new Color(255, 0, 0);

            case J:
                return new Color(128, 0, 128);

            case L:
                return new Color(0, 0, 255);

            case T:
                return new Color(255, 255, 0);

            case S:
                return new Color(0, 255, 255);

            case Z:
                return new Color(0, 255, 0);

            default:
                throw new IllegalArgumentException("Unrecognized PieceType!");
        }
    }

    private void drawBlock(Graphics g, int x, int y, Piece.PieceType type) {
        g.setColor(getBlockColor(type));
        g.fillRect(x, y, blockSize, blockSize);
        g.setColor(Color.BLACK);
        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setStroke(new BasicStroke(borderWidth));
        }
        g.drawRect(x, y, blockSize, blockSize);
    }

    private class NextPiecePane extends JPanel {

        public NextPiecePane() {
            Dimension d = new Dimension(5 * blockSize, GAME_HEIGHT / 2 * blockSize);
            setPreferredSize(d);
            setMaximumSize(d);
        }

        @Override
        public void paintComponent(Graphics g) {
            Piece.PieceType type = tetris.getNextPieceType();
            Piece p = Piece.createPiece(type);
            int width = getWidth() / blockSize;
            int height = getHeight() / blockSize;
            p.setPos(width / 2, height / 2);

            for (Vector v : p.getGlobalVectors()) {
                int x = MathUtils.round(v.get(0)) * blockSize;
                int y = blockSize * (height - MathUtils.round(v.get(1)) - 1);

                drawBlock(g, x, y, p.getType());
            }
        }
    }

    private class TetrisPane extends JPanel {
        private int width, height; // in blocks, not pixels
        private Map<Integer, Font> pixelSizeToFont = new HashMap<>();

        public TetrisPane(int width, int height) {
            this.width = width;
            this.height = height;
            setPreferredSize(new Dimension(width * blockSize, height * blockSize));
            setMaximumSize(new Dimension(width * blockSize, height * blockSize));
        }

        @Override
        public void paintComponent(Graphics g) {
            g.setColor(GAME_BG_COLOR);
            g.fillRect(0, 0, getWidth(), getHeight());
            Piece.PieceType[][] grid = tetris.generateGrid();
            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid[row].length; col++) {
                    Piece.PieceType type = grid[row][col];
                    if (type != null) {
                        int x = blockSize * col;
                        int y = blockSize * (GAME_HEIGHT - row - 1);

                        drawBlock(g, x, y, type);
                    }
                }
            }

            if (tetris.hasLost()) {
                int panelWidth = blockSize * GAME_WIDTH * 4 / 5;
                int panelHeight = blockSize * GAME_HEIGHT / 4;

                int centerX = getWidth() / 2;

                int panelX = centerX - panelWidth / 2;
                int panelY = getHeight() / 4;

                int y = panelY - blockSize / 2;

                g.setColor(TEXT_COLOR);
                g.fillRect(panelX, panelY, panelWidth, panelHeight);

                g.setColor(FRAME_BG_COLOR);
                int border = blockSize / 8;
                g.fillRect(panelX + border, panelY + border,
                        panelWidth - 2 * border, panelHeight - 2 * border);

                String s = "You lose!";
                drawText(g, centerX, y, blockSize * 2, s);
                y += blockSize * 2;
                drawText(g, centerX, y, blockSize * 3 / 4, String.format("Lines: % 2d", tetris.getClearedLines()));

                y += blockSize * 3 / 4;
                drawText(g, centerX, y, blockSize, "A - Play again");
                y += blockSize;
                drawText(g, centerX, y, blockSize, "B - Quit");
            }
        }

        private Font getFontWithSize(Graphics g, Font font, int fontSizePixels) {
            Font cached = pixelSizeToFont.get(fontSizePixels);
            if (cached != null && cached.getFontName().equals(font.getFontName())) {
                return cached;
            }
            Font f;
            float size = 1f;
            while (g.getFontMetrics(f = font.deriveFont(size)).getHeight() < fontSizePixels) {
                size++;
            }
            pixelSizeToFont.put(fontSizePixels, f);
            return f;
        }

        private void drawText(Graphics g, int x, int y, int fontSizePixels, String s) {
            Font f = getFontWithSize(g, g.getFont(), fontSizePixels);
            g.setFont(f);
            FontMetrics metrics = g.getFontMetrics();
            int textWidth = metrics.stringWidth(s);
            int textHeight = metrics.getHeight();
            g.setColor(TEXT_COLOR);
            g.drawString(s, x - textWidth / 2, y + textHeight);
        }
    }
}
