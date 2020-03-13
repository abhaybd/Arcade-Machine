package com.coolioasjulio.pacman;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        LevelMap map = LevelMap.loadFromImg(ImageIO.read(Main.class.getResourceAsStream("/pacmanlevel.png")));
        PacManGameGUI game = new PacManGameGUI(map, 30);
        game.playGame();
    }
}
