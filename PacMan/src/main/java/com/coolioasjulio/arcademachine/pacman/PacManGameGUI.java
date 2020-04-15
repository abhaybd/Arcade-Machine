package com.coolioasjulio.arcademachine.pacman;

import com.coolioasjulio.arcademachine.launcher.gameutils.InputManager;
import com.coolioasjulio.arcademachine.pacman.engine.AwtGraphicsAdapter;
import com.coolioasjulio.arcademachine.pacman.engine.Drawer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;

public class PacManGameGUI extends PacManGame {

    private static final Color TEXT_COLOR = Color.WHITE;
    private static final boolean FULL_SCREEN = true;
    private static final int PANEL_WIDTH_TILE = 25;
    private static final int PANEL_HEIGHT_TILE = 9;

    public static void main(String[] args) throws IOException {
        LevelMap map = LevelMap.loadFromImg(ImageIO.read(PacManGameGUI.class.getResourceAsStream("/pacmanlevel.png")));
        PacManGameGUI game = new PacManGameGUI(map, 30);
        KeyListener listener;
        try {
            // Mock the input platform to use the Swing keybindings instead
            PipedOutputStream pipedOut = new PipedOutputStream();
            InputStream in = new PipedInputStream(pipedOut);
            final DataOutputStream out = new DataOutputStream(pipedOut);
            InputManager.enable(in);
            listener = new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {
                    try {
                        out.writeInt(e.getKeyCode());
                        out.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            };
            game.playGame(listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JFrame frame;
    private PacManPanel panel;
    private boolean playerWon;
    private boolean playerLost;

    public PacManGameGUI(LevelMap levelMap, int size) {
        super(levelMap, size);
    }

    @Override
    public void playGame() {
        playGame(null);
    }

    public void playGame(KeyListener listener) {
        this.frame = new JFrame();
        if (FULL_SCREEN) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
        }
        frame.setVisible(true);
        if (FULL_SCREEN) resize(); // resize the game to fit different screen sizes
        panel = new PacManPanel();
        frame.add(panel);
        if (listener != null) frame.addKeyListener(listener);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (!FULL_SCREEN) frame.pack();
        setup();
        super.playGame();
    }

    private void setup() {
        playerWon = false;
        playerLost = false;
        panel.updateInfo();
    }

    /**
     * Select a tile size in pixels to appropriately size the game for the screen size.
     */
    private void resize() {
        int size;
        if (frame.getWidth() >= frame.getHeight()) {
            size = frame.getHeight() / (levelMap.getHeight() + 2);
        } else {
            size = frame.getWidth() / levelMap.getWidth();
        }
        setSize(size);
    }

    /**
     * Render the UI.
     */
    @Override
    protected void update() {
        // Update the info panel and draw the game
        panel.updateInfo();
        frame.repaint();
        // If the player has won or lost, render it
        if (playerLost || playerWon) {
            // See if the player wants to play again or not
            Boolean playAgain = null;
            do {
                InputManager.fetchInputs();
                if (InputManager.keyPressed(KeyEvent.VK_A)) {
                    playAgain = true;
                } else if (InputManager.keyPressed(KeyEvent.VK_B)) {
                    playAgain = false;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while (playAgain == null);
            // Either restart game or kill the game
            if (playAgain) {
                setup();
                super.playGame();
            } else {
                frame.setVisible(false);
                SwingUtilities.invokeLater(frame::dispose);
            }
        }
    }

    @Override
    protected void onDeath() {
        System.out.println("You died!");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLose() {
        System.out.println("You lose!");
        playerLost = true;
        update();
    }

    @Override
    protected void onLevelComplete() {
        System.out.println("You win!");
        playerWon = true;
        update();
    }

    private class PacManPanel extends JPanel {
        private JLabel scoreLabel, livesLabel;

        public PacManPanel() {
            // Initialize all UI elements
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            scoreLabel = new JLabel("Score: 0");
            livesLabel = new JLabel("Lives: " + lives);

            String fontName = scoreLabel.getFont().getName();
            Font font = new Font(fontName, Font.PLAIN, size);
            scoreLabel.setFont(font);
            livesLabel.setFont(font);

            scoreLabel.setForeground(TEXT_COLOR);
            livesLabel.setForeground(TEXT_COLOR);
            scoreLabel.setBackground(BG_COLOR);
            livesLabel.setBackground(BG_COLOR);
            livesLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            GamePanel gamePanel = new GamePanel();

            JPanel info = new JPanel();
            info.setBackground(BG_COLOR);
            info.setLayout(new GridLayout(1, 2));
            info.add(scoreLabel);
            info.add(livesLabel);
            Dimension size = new Dimension(gamePanel.getPreferredSize().width, 2 * PacManGameGUI.this.size);
            info.setPreferredSize(size);
            info.setMaximumSize(size);

            add(info);
            add(gamePanel);

            setBackground(BG_COLOR);
        }

        private void updateInfo() {
            // Update the information panel
            scoreLabel.setText("Score: " + getScore());
            livesLabel.setText("Lives: " + lives);
        }
    }

    private class GamePanel extends JPanel {
        private Map<Integer, Font> pixelSizeToFont;

        public GamePanel() {
            Dimension size = new Dimension(levelMap.getWidth() * PacManGameGUI.this.size, levelMap.getHeight() * PacManGameGUI.this.size);
            setPreferredSize(size);
            setMaximumSize(size);
            pixelSizeToFont = new HashMap<>();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Drawer d = new AwtGraphicsAdapter(g);
            draw(d);

            if (playerWon || playerLost) {
                int panelWidth = size * PANEL_WIDTH_TILE;
                int panelHeight = size * PANEL_HEIGHT_TILE;

                int centerX = getWidth() / 2;

                int panelX = centerX - panelWidth / 2;
                int panelY = getHeight() / 4;

                int y = panelY;

                g.setColor(TEXT_COLOR);
                g.fillRect(panelX, panelY, panelWidth, panelHeight);

                g.setColor(BG_COLOR);
                int border = size / 4;
                g.fillRect(panelX + border, panelY + border,
                        panelWidth - 2 * border, panelHeight - 2 * border);

                String s = playerWon ? "You win!" : "You lose!";
                drawText(g, centerX, y, size * 4, s);
                System.out.println(g.getFontMetrics().getHeight());
                y += size * 4;
                drawText(g, centerX, y, size * 3 / 2, String.format("Score: % 3d", getScore()));

                y += size * 3 / 2;
                drawText(g, centerX - panelWidth / 4, y, size * 3 / 2, "A - Play again");
                drawText(g, centerX + panelWidth / 4, y, size * 3 / 2, "B - Quit");
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
