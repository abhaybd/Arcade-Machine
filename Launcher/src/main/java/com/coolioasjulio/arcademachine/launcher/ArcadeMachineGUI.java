package com.coolioasjulio.arcademachine.launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ArcadeMachineGUI extends ArcadeMachine {

    static boolean MOCK_INPUT = false;

    public static void main(String[] args) {
        ArcadeMachineGUI am = new ArcadeMachineGUI();
        am.start();
    }

    private static final Color BG_COLOR = Color.BLACK;
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final int TEXT_ALPHA_MIN = 63;
    private static final double TEXT_ALPHA_RATE = 0.5;

    private JFrame frame;
    private String[] games;
    private volatile boolean acceptInput = true;

    public ArcadeMachineGUI() {
        super();
        games = Arrays.stream(super.games)
                .map(f -> f.getName().replace(".jar", "")).toArray(String[]::new);
    }

    public void start() {
        this.frame = new JFrame();
        Input.getInstance().addEventCallback(this::keyEvent);
        frame.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("Gained focus!");
                acceptInput = true;
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("Lost focus!");
                acceptInput = false;
            }
        });
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.add(new GameChooserPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    protected void resetEventCallbacks() {
        Input.getInstance().clearEventCallbacks();
        Input.getInstance().addEventCallback(this::keyEvent);
    }

    public void keyEvent(boolean pressed, int keycode) {
        if (!pressed) return;
        if (!acceptInput) return;
        if (keycode == KeyEvent.VK_DOWN) {
            super.incIndex();
        } else if (keycode == KeyEvent.VK_UP) {
            super.decIndex();
        } else if (keycode == KeyEvent.VK_A) {
            Thread t = new Thread(super::launchGame);
            acceptInput = false;
            t.start();

            if (!MOCK_INPUT) {
                Thread waiter = new Thread(() -> {
                    try {
                        t.join();
                        frame.requestFocus();
                        acceptInput = true;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                });
                waiter.setDaemon(true);
                waiter.start();
            }
        }
        frame.repaint();
    }

    private class GameChooserPanel extends JPanel {

        private Map<Integer, Font> pixelSizeToFont;
        private Image titleImage;
        private boolean failedToLoad = false;

        public GameChooserPanel() {
            pixelSizeToFont = new HashMap<>();
        }

        private void loadImgIfNecessary() {
            if (titleImage == null && !failedToLoad) {
                try {
                    titleImage = ImageIO.read(getClass().getResourceAsStream("/title.png"));
                    double scaleFactor = (getHeight()/4.0) / titleImage.getHeight(null);
                    titleImage = titleImage.getScaledInstance((int)(titleImage.getWidth(null) * scaleFactor),
                            (int) (titleImage.getHeight(null) * scaleFactor), Image.SCALE_SMOOTH);
                } catch (IOException e) {
                    failedToLoad = true;
                    e.printStackTrace();
                }
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

        private Color copyColorWithAlpha(Color c, int alpha) {
            return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(BG_COLOR);
            g.fillRect(0, 0, getWidth(), getHeight());

            loadImgIfNecessary();
            if (titleImage != null) {
                g.drawImage(titleImage, getWidth()/2 - titleImage.getWidth(null)/2, 0, null);
            } else {
                g.setFont(getFontWithSize(g, g.getFont(), getHeight() / 8));
                g.setColor(Color.RED);
                String title = "Arcade Machine!";
                g.drawString(title, getWidth() / 2 - g.getFontMetrics().stringWidth(title) / 2, getHeight() / 8);
            }

            int startY = getHeight() * 3 / 8;
            int y = startY;
            int x = getWidth() / 3;

            g.setFont(getFontWithSize(g, g.getFont(), getHeight() / 15));
            g.setColor(TEXT_COLOR);
            int height = g.getFontMetrics().getHeight();

            String caret = ">";
            int width = g.getFontMetrics().stringWidth(caret);
            g.drawString(caret, x - width * 5 / 4, y);

            for (int i = getIndex(); i < games.length; i++) {
                g.setColor(copyColorWithAlpha(TEXT_COLOR,
                        TEXT_ALPHA_MIN +
                                (int) Math.round((255 - TEXT_ALPHA_MIN) * Math.pow(TEXT_ALPHA_RATE, i - getIndex()))));
                g.drawString(games[i], x, y);
                y += height;
            }

            if (getIndex() > 0) {
                String game = games[getIndex() - 1];
                g.setColor(new Color(TEXT_COLOR.getRed(), TEXT_COLOR.getGreen(), TEXT_COLOR.getBlue(), 128));
                g.drawString(game, x, startY - height);
            }
        }
    }
}
