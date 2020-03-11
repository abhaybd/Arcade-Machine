package com.coolioasjulio.pacman;

import com.coolioasjulio.arcademachine.gameutils.InputManager;
import com.coolioasjulio.pacman.behaviors.GhostAmbushBehavior;
import com.coolioasjulio.pacman.behaviors.GhostChaseBehavior;
import com.coolioasjulio.pacman.behaviors.GhostObliviousBehavior;
import com.coolioasjulio.pacman.behaviors.GhostPinchBehavior;
import com.coolioasjulio.pacman.engine.Coord;
import com.coolioasjulio.pacman.engine.Drawer;
import com.coolioasjulio.pacman.engine.GameObject;
import com.coolioasjulio.pacman.engine.Time;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class PacManGame {

    private static PacManGame instance;

    public static PacManGame getInstance() {
        return instance;
    }

    protected static final Color[] GHOST_COLORS = new Color[]{Color.RED, Color.CYAN, Color.GREEN, Color.PINK};
    protected static final Color GHOST_FLEE_COLOR = Color.BLUE;
    protected static final Color PACMAN_COLOR = Color.YELLOW;
    protected static final Color BG_COLOR = Color.BLACK;
    protected static final Color WALL_COLOR = Color.BLUE;
    protected static final Color PELLET_COLOR = Color.WHITE;

    private static final long GHOST_SPAWN_INTERVAL = 10000; // ms
    private static final double DEF_GHOST_SPEED = 3.5;
    private static final double DEF_PACMAN_SPEED = 4;
    private static final int START_LIVES = 3;
    private static final long POWERUP_TIME = 8000;

    protected LevelMap levelMap;
    protected Ghost[] ghosts;
    protected PacMan pacMan;
    protected int size;
    private int score;
    protected int lives;
    protected List<GameObject> pellets;
    protected List<GameObject> powerups;
    private long powerupTimeoutTime;
    private long nextGhostSpawnTime;
    private boolean powerupActive = false;

    public PacManGame(LevelMap levelMap, int size) {
        if (instance == null) instance = this;
        this.levelMap = levelMap;
        this.size = size;
        pellets = new ArrayList<>();
        powerups = new ArrayList<>();
    }

    /**
     * Called on every frame update. This method should call the draw() method to render the game.
     */
    protected abstract void update();

    /**
     * Called when the player dies.
     */
    protected abstract void onDeath();

    /**
     * Called when the player dies and has no more lives.
     */
    protected abstract void onLose();

    /**
     * Called when the player has completed the level, meaning there are no more pellets left.
     */
    protected abstract void onLevelComplete();

    /**
     * Draw the level and all the game objects. This method does not handle reporting wins, losses, or deaths.
     * Additionally, this method does not display the score or lives anywhere.
     *
     * @param d The Drawer object to use to render the frame. Specific implementation is platform-specific.
     */
    protected void draw(Drawer d) {
        d.setColor(BG_COLOR);
        d.fillRect(0, 0, levelMap.getWidth() * size, levelMap.getHeight() * size);
        d.setColor(WALL_COLOR);
        for (int y = 0; y < levelMap.getHeight(); y++) {
            for (int x = 0; x < levelMap.getWidth(); x++) {
                if (levelMap.isWall(x, y)) {
                    d.fillRect(x * size, y * size, size, size);
                }
            }
        }

        for (GameObject p : pellets) {
            p.draw(d);
        }

        for (GameObject p : powerups) {
            p.draw(d);
        }

        pacMan.draw(d);

        for (Ghost ghost : ghosts) {
            if (ghost != null) {
                ghost.draw(d);
            }
        }
    }

    private void spawnPacMan() {
        pacMan.setLocalPosition(levelMap.spawnX() * size, levelMap.spawnY() * size);
        pacMan.setDirection(Direction.EAST);
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getScore() {
        return score;
    }

    private void setUpLevel() {
        pacMan = new PacMan(levelMap.spawnX() * size, levelMap.spawnY() * size, size, PACMAN_COLOR, Color.BLACK);
        pacMan.setSpeed(Utils.round(DEF_PACMAN_SPEED * size));
        ghosts = new Ghost[4];
        resetPellets();
        resetPowerups();
        lives = START_LIVES;
        Time.start();
        nextGhostSpawnTime = System.currentTimeMillis() + GHOST_SPAWN_INTERVAL;
    }

    /**
     * Start the game. This method blocks until the game is finished.
     */
    protected void playGame() {
        InputManager.enable();
        setUpLevel();
        while (!Thread.interrupted()) {
            double dt = Time.deltaTime();
            InputManager.getInputs();

            if (powerupActive && System.currentTimeMillis() >= powerupTimeoutTime) {
                stopPowerup();
            }

            long start = System.currentTimeMillis();
            spawnGhostIfNecessary();
            Arrays.stream(ghosts).filter(Objects::nonNull).forEach(g -> g.update(levelMap, dt));
            pacMan.update(levelMap, dt);
            eatPellets();
            eatPowerups();
            eatGhosts();
            update();

            if (playerDied()) {
                onDeath();
                lives--;
                Time.start();
                ghosts = new Ghost[4];
                spawnPacMan();
                nextGhostSpawnTime = System.currentTimeMillis() + GHOST_SPAWN_INTERVAL;
                if (lives <= 0) {
                    onLose();
                    break;
                }
                continue;
            }

            if (pellets.isEmpty()) {
                onLevelComplete();
                setUpLevel();
                break;
            }

            try {
                Thread.sleep(Math.max(0, 50 - (System.currentTimeMillis() - start)));
            } catch (InterruptedException e) {
                break;
            }
            Time.update();
        }
    }

    private void resetPowerups() {
        powerups.clear();
        for (Coord c : levelMap.getPowerupsCoords()) {
            PowerupSprite sprite = new PowerupSprite(size, PELLET_COLOR);
            powerups.add(new GameObject(c.x * size, c.y * size, sprite.getBoundingCollider(), sprite));
        }
    }

    private void eatPowerups() {
        List<GameObject> eaten = powerups.stream().filter(c -> c.intersects(pacMan)).collect(Collectors.toList());
        powerups.removeAll(eaten);
        if (!eaten.isEmpty()) {
            powerupActive = true;
            powerupTimeoutTime = System.currentTimeMillis() + POWERUP_TIME;
            Arrays.stream(ghosts).filter(Objects::nonNull).forEach(g -> {
                g.setFleeing(true);
                g.setSpeed(g.getSpeed() * 2 / 3);
            });
        }
    }

    private void stopPowerup() {
        Arrays.stream(ghosts).filter(Objects::nonNull).forEach(g -> {
            g.setFleeing(false);
            g.setSpeed(Utils.round(DEF_GHOST_SPEED * size));
        });
        powerupActive = false;
    }

    private void eatGhosts() {
        if (!powerupActive) return;
        List<Ghost> eaten = Arrays.stream(ghosts).filter(Objects::nonNull).filter(g -> g.intersects(pacMan)).collect(Collectors.toList());
        for (Ghost g : eaten) {
            score += 10;
            for (int i = 0; i < ghosts.length; i++) {
                if (ghosts[i] == g) {
                    ghosts[i] = null;
                }
            }
        }
    }

    private void resetPellets() {
        pellets.clear();
        for (int y = 0; y < levelMap.getHeight(); y++) {
            for (int x = 0; x < levelMap.getWidth(); x++) {
                if (!levelMap.isWall(x, y)) {
                    PelletSprite sprite = new PelletSprite(size, PELLET_COLOR);
                    pellets.add(new GameObject(x * size, y * size, sprite.getBoundingCollider(), sprite));
                }
            }
        }
    }

    private void eatPellets() {
        List<GameObject> eaten = pellets.stream().filter(p -> p.intersects(pacMan)).collect(Collectors.toList());
        score += eaten.size();
        pellets.removeAll(eaten);
    }

    private boolean playerDied() {
        if (powerupActive) return false;
        return Arrays.stream(ghosts).filter(Objects::nonNull).anyMatch(g -> g.intersects(pacMan));
    }

    private void setBehavior(Ghost g, int index) {
        Ghost.GhostBehavior behavior;
        if (index == 0) {
            behavior = new GhostChaseBehavior(g);
        } else if (index == 1) {
            behavior = new GhostAmbushBehavior(g);
        } else if (index == 2) {
            behavior = new GhostPinchBehavior(g, ghosts[0]);
        } else {
            behavior = new GhostObliviousBehavior(g);
        }
        g.setBehavior(behavior);
    }

    private void spawnGhostIfNecessary() {
        if (!powerupActive && System.currentTimeMillis() >= nextGhostSpawnTime) {
            int index = -1;
            for (int i = 0; i < ghosts.length; i++) {
                if (ghosts[i] == null) {
                    index = i;
                    break;
                }
            }
            if (index == -1) return;
            Ghost g = new Ghost(levelMap.spawnX() * size, levelMap.spawnY() * size, size, GHOST_COLORS[index], GHOST_FLEE_COLOR);
            g.setSpeed(Utils.round(DEF_GHOST_SPEED * size));
            setBehavior(g, index);
            ghosts[index] = g;
            System.out.println("Spawned ghost!");
            nextGhostSpawnTime = System.currentTimeMillis() + GHOST_SPAWN_INTERVAL;
        }
    }

    public int getSize() {
        return size;
    }

    public int getPacManTileX() {
        return pacMan.getTileX(size);
    }

    public int getPacManTileY() {
        return pacMan.getTileY(size);
    }

    public PacMan getPacMan() {
        return pacMan;
    }

    public LevelMap getLevelMap() {
        return levelMap;
    }

    public Direction getPacManDirection() {
        return pacMan.getDirection();
    }
}
