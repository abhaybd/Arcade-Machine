package com.coolioasjulio.arcademachine.snake;

import com.coolioasjulio.arcademachine.launcher.gameutils.InputManager;
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
}
