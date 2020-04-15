package com.coolioasjulio.arcademachine.snake;

import com.coolioasjulio.arcademachine.launcher.gameutils.InputManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class GameUI extends Game {
    private static final boolean MOCK_INPUT = false;

    private SnakePanel gamePanel;
    JPanel root;
    private JLabel timeLabel;
    private JLabel levelLabel;
    private long startTime;

    private Thread inputThread;
    private final Object snakeLock = new Object();
    private volatile Boolean restart = null;
    private final Object deathButtonLock = new Object();

    public GameUI() {
        super(20, 20);
    }

    private void createUIComponents() {
        gamePanel = new SnakePanel(this);
    }

    public void playGame() {
        if (MOCK_INPUT) {
            mockInput();
        } else {
            InputManager.enable();
        }
        inputThread = new Thread(this::inputTask);
        inputThread.setDaemon(true);
        inputThread.start();
        startTime = System.currentTimeMillis();
        gamePanel.setDead(false);
        reset();
        while (!Thread.interrupted()) {
            DateFormat df = new SimpleDateFormat("mm:ss");
            timeLabel.setText(df.format(new Date(System.currentTimeMillis() - startTime)));
            gamePanel.repaint();
            if (update()) {
                break;
            } else {
                try {
                    Thread.sleep(getDelay());
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private void mockInput() {
        try {
            PipedInputStream in = new PipedInputStream();
            DataOutputStream out = new DataOutputStream(new PipedOutputStream(in));
            gamePanel.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {
                    try {
                        out.writeBoolean(true);
                        out.writeInt(e.getKeyCode());
                        out.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    try {
                        out.writeBoolean(false);
                        out.writeInt(e.getKeyCode());
                        out.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            gamePanel.requestFocusInWindow();
            InputManager.enable(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReset() {
        onLevelUp();
        if (gamePanel != null) gamePanel.setDead(false);
    }

    @Override
    public void onLevelUp() {
        if (levelLabel != null) levelLabel.setText("Level: " + getLevel());
    }

    @Override
    public boolean onDeath() {
        try {
            System.out.println("You died!");
            Thread.sleep(1000);
            gamePanel.setDead(true);
            gamePanel.repaint();
            restart = null;
            synchronized (deathButtonLock) {
                while (restart == null) {
                    deathButtonLock.wait();
                }
            }
            return restart;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return true;
        }
    }

    private void inputTask() {
        while (!Thread.interrupted()) {
            InputManager.fetchInputs();

            Snake.Direction dir = null;
            if (InputManager.keyPressed(KeyEvent.VK_UP)) {
                dir = Snake.Direction.NORTH;
            } else if (InputManager.keyPressed(KeyEvent.VK_RIGHT)) {
                dir = Snake.Direction.EAST;
            } else if (InputManager.keyPressed(KeyEvent.VK_DOWN)) {
                dir = Snake.Direction.SOUTH;
            } else if (InputManager.keyPressed(KeyEvent.VK_LEFT)) {
                dir = Snake.Direction.WEST;
            }
            if (dir != null) {
                synchronized (snakeLock) {
                    super.turnTo(dir);
                }
            }

            if (InputManager.keyPressed(KeyEvent.VK_A) || InputManager.keyPressed(KeyEvent.VK_B)) {
                restart = InputManager.keyPressed(KeyEvent.VK_A);
                synchronized (deathButtonLock) {
                    deathButtonLock.notify();
                }
            }

            Thread.yield();
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        root = new JPanel();
        root.setLayout(new BorderLayout(0, 0));
        root.add(gamePanel, BorderLayout.CENTER);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBackground(new Color(-16777216));
        root.add(panel1, BorderLayout.NORTH);
        timeLabel = new JLabel();
        timeLabel.setBackground(new Color(-16777216));
        Font timeLabelFont = this.$$$getFont$$$(null, -1, 26, timeLabel.getFont());
        if (timeLabelFont != null) timeLabel.setFont(timeLabelFont);
        timeLabel.setForeground(new Color(-1));
        timeLabel.setText("Label");
        panel1.add(timeLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        levelLabel = new JLabel();
        levelLabel.setBackground(new Color(-16777216));
        Font levelLabelFont = this.$$$getFont$$$(null, -1, 26, levelLabel.getFont());
        if (levelLabelFont != null) levelLabel.setFont(levelLabelFont);
        levelLabel.setForeground(new Color(-1));
        levelLabel.setText("Label");
        panel1.add(levelLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }
}
