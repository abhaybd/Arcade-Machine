package com.coolioasjulio.arcademachine.snake;

import java.awt.event.WindowEvent;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        GameUI g = new GameUI();
        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.add(g.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        g.playGame();
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
}
