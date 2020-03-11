package com.coolioasjulio.pacman.engine;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public abstract class Sprite implements Locatable {

    private static SpriteAnimationManager manager;

    private static class SpriteAnimationManager {
        private Map<Sprite, Long> nextAnimationTime = new HashMap<>();
        private Thread animationThread;

        private void animationTask() {
            while (!Thread.interrupted()) {
                Sprite nextSprite = nextAnimationTime.keySet().stream()
                        .min(Comparator.comparing(nextAnimationTime::get)).orElse(null);
                if (nextSprite == null) {
                    stop();
                    break;
                }
                long sleepTime = nextAnimationTime.get(nextSprite) - System.currentTimeMillis();
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                nextAnimationTime.put(nextSprite, System.currentTimeMillis() + nextSprite.getPeriodMillis());
                nextSprite.nextImage();
            }
        }

        private synchronized void stop() {
            animationThread.interrupt();
            animationThread = null;
        }

        private synchronized void start() {
            animationThread = new Thread(this::animationTask);
            animationThread.setDaemon(true);
            animationThread.start();
        }

        public synchronized void addSprite(Sprite sprite) {
            if (sprite.getFPS() == 0) return;
            nextAnimationTime.put(sprite, System.currentTimeMillis() + sprite.getPeriodMillis());
            if (nextAnimationTime.size() == 1) {
                start();
            }
        }

        public synchronized void removeSprite(Sprite sprite) {
            nextAnimationTime.remove(sprite);
            if (nextAnimationTime.isEmpty()) {
                stop();
            }
        }
    }

    public static void addSprite(Sprite sprite) {
        if (manager == null) {
            manager = new SpriteAnimationManager();
        }
        manager.addSprite(sprite);
    }

    public static void removeSprite(Sprite sprite) {
        if (manager != null) {
            manager.removeSprite(sprite);
        }
    }

    private Locatable parent;
    private int x, y;
    private int activeImage;
    private int fps;

    public Sprite(int x, int y, int fps) {
        this.x = x;
        this.y = y;
        this.fps = fps;
        addSprite(this);
    }

    abstract public int numImages();

    abstract public void drawActiveImage(Drawer d);

    public int getActiveImageIndex() {
        return activeImage;
    }

    public long getPeriodMillis() {
        return 1000 / fps;
    }

    public int getFPS() {
        return fps;
    }

    public void nextImage() {
        activeImage = (activeImage + 1) % numImages();
    }

    public void setParent(Locatable parent) {
        this.parent = parent;
    }

    public Locatable getParent() {
        return parent;
    }

    public Coord getLocalPosition() {
        return new Coord(x, y);
    }
}
