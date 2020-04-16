package com.coolioasjulio.arcademachine.tetris;

import com.coolioasjulio.arcademachine.launcher.gameutils.InputManager;

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
    /**
     * This value is completely arbitrary. It affects the slope/curvature of the delay curve.
     * Increasing this value makes the delay drop faster.
     */
    private static final double DIFFICULTY = 0.2;
    /**
     * The delay, in ms, at level 0 (0-9 lines)
     */
    private static final double LEVEL_0_DELAY = 400;

    private static final long BURST_START_DELAY = 500;
    private static final long BURST_DELAY = 80;

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
    private Map<Integer, Long> burstKeys;

    public TetrisGUI() {
        tetris = new TetrisBase(GAME_WIDTH, GAME_HEIGHT);
        burstKeys = new HashMap<>();
        burstKeys.put(KeyEvent.VK_DOWN, Long.MAX_VALUE);
        burstKeys.put(KeyEvent.VK_LEFT, Long.MAX_VALUE);
        burstKeys.put(KeyEvent.VK_RIGHT, Long.MAX_VALUE);

        frame = new JFrame("Tetris");
        frame.getContentPane().setBackground(FRAME_BG_COLOR);
        if (FULL_SCREEN) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
        }
        addMockInput(); // If we're mocking the inputs (using keyboard) then add that mocked input listener
        frame.setVisible(true);
        if (FULL_SCREEN) resize(); // resize the game to fit different screen sizes
        pane = new TetrisPane(GAME_WIDTH, GAME_HEIGHT);
        // This panel holds all the game elements
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
        // For some reason, adding the panel to the frame clears the preferred size, so we're setting it again
        panel.setPreferredSize(size);
        if (!FULL_SCREEN) frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addMockInput() {
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

    /**
     * This calculates an optimal block size based on the screen resolution.
     */
    public void resize() {
        int size = frame.getHeight() / (GAME_HEIGHT + 1);
        setBlockSize(size);
    }

    /**
     * Set the block size, and recalculate all related vaules.
     *
     * @param size The size of a block, in pixels.
     */
    public void setBlockSize(int size) {
        blockSize = size;
        borderWidth = blockSize / 15; // The border is 1/15th of the block
        lineLabelSize = blockSize * 7 / 5; // the text size is 1.4x the block size
    }

    private void renderTask() {
        try {
            // Render the game at 50Hz
            while (!Thread.interrupted()) {
                frame.repaint();
                Thread.sleep(20);
            }
        } catch (InterruptedException e) {
        }
    }

    /**
     * Play the game.
     */
    public void playGame() {
        done = false;
        // Start the render and input threads
        renderThread = new Thread(this::renderTask);
        renderThread.start();
        // We only need the input thread if we're not mocking the input
        if (!MOCK_INPUT) {
            Thread inputThread = new Thread(this::inputThread);
            inputThread.start();
        }
        while (true) {
            if (!done && !paused) {
                // Advance a timestep, update the info
                done = tetris.advanceStep();
                lineLabel.setText(String.format("Lines: % 4d", tetris.getClearedLines()));
                // Sleep for the appropriate amount
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
        // Run on repeat. Consume input from the input manager and fire the keyPressed event
        while (!Thread.interrupted()) {
            InputManager.fetchInputs();
            int[] codes = InputManager.getPressed();
            for (int keyCode : codes) {
                keyPressed(keyCode);
            }

            // Handle key burst (holding a key down will fire the pressed event repeatedly)
            long time = System.currentTimeMillis();
            for (int code : burstKeys.keySet()) {
                // If a burst key was just pressed, schedule the next press event.
                // If it was released, schedule it for at the end of time (basically)
                if (InputManager.keyPressed(code)) {
                    burstKeys.put(code, time + BURST_START_DELAY);
                } else if (InputManager.keyReleased(code)) {
                    burstKeys.put(code, Long.MAX_VALUE);
                }

                // If we've passed the scheduled event time, fire the event and reschedule
                if (time >= burstKeys.get(code)) {
                    burstKeys.put(code, time + BURST_DELAY);
                    keyPressed(code);
                }
            }
            Thread.yield();
        }
    }

    /**
     * Calculate the desired delay between timesteps based on the level. This is calibrated, and can be tweaked to adjust difficulty.
     *
     * @return The delay between timesteps, in milliseconds.
     */
    private long getDelay() {
        // This is essentially a modified softplus function, which is very linear at the beginning, and then asymptotically approaches 0
        // The softplus function is f(x)=ln(1+e^x), we want to flip it across the y-axis, and then scale it in both x and y
        int level = tetris.getClearedLines() / 10;
        double a = LEVEL_0_DELAY / Math.log(2.0); // solve for a where f(0) = LEVEL_0_DELAY
        return Math.round(a * Math.log(1.0 + Math.exp(-DIFFICULTY * level)));
    }

    /**
     * Reset the game to the starting state.
     */
    private void resetGame() {
        tetris.reset();
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
                // Reset the game if the game is over and A is pressed
                if (tetris.hasLost()) {
                    resetGame();
                }
                break;

            case KeyEvent.VK_B:
                // Exit the game if the game is over and B is pressed
                if (tetris.hasLost()) {
                    System.exit(0);
                }
                break;

            case KeyEvent.VK_P:
                // This is only really possible for mocked input, since the arcade machine has no pause button
                paused = !paused;
                break;
        }
    }

    /**
     * Get the color associated with the piece type.
     *
     * @param type The type of piece to get the color for.
     * @return The color of the supplied piece. The piece should be rendered in this color.
     */
    private Color getBlockColor(Piece.PieceType type) {
        switch (type) {
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

    /**
     * Draw a block of the supplied piece type at the supplied location.
     *
     * @param g    The {@link Graphics} object to use for rendering.
     * @param x    The x-coordinate of the top left of the block, in pixels.
     * @param y    The y-coordinate of the top left of the block, in pixels. (In screen space, so +y -> down)
     * @param type The type of block to draw.
     */
    private void drawBlock(Graphics g, int x, int y, Piece.PieceType type) {
        // Draw the block
        g.setColor(getBlockColor(type));
        g.fillRect(x, y, blockSize, blockSize);
        // Draw the border around the block
        g.setColor(Color.BLACK);
        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setStroke(new BasicStroke(borderWidth));
        }
        g.drawRect(x, y, blockSize, blockSize);
    }

    /**
     * This is the pane on the side that displays the next piece that will be spawned.
     */
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
            // Put the piece in the middle of the pane
            p.setPos(width / 2, height / 2);

            // Render all the blocks of the piece
            for (Vector v : p.getGlobalVectors()) {
                int x = MathUtils.round(v.get(0)) * blockSize;
                int y = blockSize * (height - MathUtils.round(v.get(1)) - 1);

                drawBlock(g, x, y, p.getType());
            }
        }
    }

    /**
     * This is the main game screen, where the game takes place.
     */
    private class TetrisPane extends JPanel {
        private Map<Integer, Font> pixelSizeToFont = new HashMap<>();

        public TetrisPane(int width, int height) {
            setPreferredSize(new Dimension(width * blockSize, height * blockSize));
            setMaximumSize(new Dimension(width * blockSize, height * blockSize));
        }

        @Override
        public void paintComponent(Graphics g) {
            // Draw the background
            g.setColor(GAME_BG_COLOR);
            g.fillRect(0, 0, getWidth(), getHeight());
            // Generate a grid representing the game state, and render all blocks
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

            // If the player has lost, draw the end game panel, and prompt the user to restart or quit
            if (tetris.hasLost()) {
                // width and height of the panel in pixels
                int panelWidth = blockSize * GAME_WIDTH * 4 / 5;
                int panelHeight = blockSize * GAME_HEIGHT / 4;

                // Used for alignment of all text, center of panel in pixels
                int centerX = getWidth() / 2;

                // X and Y coordinate of top left of panel, in screen space
                int panelX = centerX - panelWidth / 2;
                int panelY = getHeight() / 4;

                // This value will be added to as things get added. It represents the y coordinate of the thing being drawn.
                int y = panelY - blockSize / 2;

                // Draw the outline of the panel
                g.setColor(TEXT_COLOR);
                g.fillRect(panelX, panelY, panelWidth, panelHeight);

                // Draw the panel
                g.setColor(FRAME_BG_COLOR);
                int border = blockSize / 8;
                g.fillRect(panelX + border, panelY + border,
                        panelWidth - 2 * border, panelHeight - 2 * border);

                // Draw all text on the panel
                drawText(g, centerX, y, blockSize * 2, "You lose!");
                y += blockSize * 2;
                drawText(g, centerX, y, blockSize * 3 / 4, String.format("Lines: % 2d", tetris.getClearedLines()));

                y += blockSize * 3 / 4;
                drawText(g, centerX, y, blockSize, "A - Play again");
                y += blockSize;
                drawText(g, centerX, y, blockSize, "B - Quit");
            }
        }

        /**
         * Create a font that has approximately the requested height in pixels.
         *
         * @param g              The Graphics object to use for Font manipulation
         * @param font           The base font to use to create new fonts
         * @param fontSizePixels The requested font height in pixels
         * @return A new font that has approximately the requested height in pixels
         */
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

        /**
         * Draws the supplied text at the supplied location, with approximately the supplied height.
         *
         * @param g              The Graphics object to use for drawing.
         * @param x              The x coordinate of the center of the text.
         * @param y              The y coordinate of the top of the text. (approximately)
         * @param fontSizePixels The height of the text, in pixels.
         * @param s              The text to draw.
         */
        private void drawText(Graphics g, int x, int y, int fontSizePixels, String s) {
            Font f = getFontWithSize(g, g.getFont(), fontSizePixels);
            g.setFont(f);
            FontMetrics metrics = g.getFontMetrics();
            int textWidth = metrics.stringWidth(s);
            int textHeight = metrics.getHeight();
            g.setColor(TEXT_COLOR);
            // drawString() expects x as left and y as baseline, so offset x and y to match this
            g.drawString(s, x - textWidth / 2, y + textHeight);
        }
    }
}
