package com.coolioasjulio.arcademachine.snake;

import com.coolioasjulio.arcademachine.launcher.gameutils.InputManager;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class GameUI extends Game {
    public static void main(String[] args) {
        GameUI g = new GameUI();
        JFrame frame = new JFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.add(g.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        g.playGame();
    }

    private SnakePanel gamePanel;
    private JPanel root;

    private Thread inputThread;
    private final Object snakeLock = new Object();
    public GameUI() {
        super(20, 20);
    }

    private void createUIComponents() {
        gamePanel = new SnakePanel(this);
    }

    public void playGame() {
        InputManager.enable();
        inputThread = new Thread(this::inputTask);
        inputThread.setDaemon(true);
        inputThread.start();
        while (!Thread.interrupted()) {
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

    @Override
    public void onLevelUp() {
        super.onLevelUp();
    }

    @Override
    public boolean onDeath() {
        System.out.println("You died!");
        return super.onDeath();
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

            Thread.yield();
        }
    }
}
