package com.coolioasjulio.arcademachine.pacman.engine;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public abstract class Sprite implements Locatable {

    private static SpriteAnimationManager manager;

    /**
     * This is a singleton manager for sprite animations. It schedules sprite changes and cycles through spritesheets.
     * The timing for animation isn't super precise and should not be depended on.
     */
    private static class SpriteAnimationManager {
        private Map<Sprite, Long> nextAnimationTime = Collections.synchronizedMap(new HashMap<>());
        private Thread animationThread;

        private void animationTask() {
            while (!Thread.interrupted()) {
                // Get the sprite with the soonest time until the next sprite change
                Sprite nextSprite = nextAnimationTime.keySet().stream()
                        .min(Comparator.comparing(nextAnimationTime::get)).orElse(null);
                // If the map is empty, stop the task
                if (nextSprite == null) {
                    stop();
                    break;
                }
                // Sleep until the sprite change
                long sleepTime = nextAnimationTime.get(nextSprite) - System.currentTimeMillis();
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                // Advance by one in the spritesheet and schedule the next sprite change
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

        /**
         * Add a sprite to the animation manager. No-op if the sprite's FPS is 0.
         *
         * @param sprite The sprite to be animated.
         */
        public synchronized void addSprite(Sprite sprite) {
            if (sprite.getFPS() == 0) return;
            nextAnimationTime.put(sprite, System.currentTimeMillis() + sprite.getPeriodMillis());
            if (nextAnimationTime.size() == 1) {
                start();
            }
        }

        /**
         * Remove a sprite from the animation manager. This sprite will no longer be animated.
         *
         * @param sprite The sprite to remove.
         */
        public synchronized void removeSprite(Sprite sprite) {
            nextAnimationTime.remove(sprite);
            if (nextAnimationTime.isEmpty()) {
                stop();
            }
        }
    }

    /**
     * Add a sprite to the animation manager. No-op if the sprite's FPS is 0.
     *
     * @param sprite The sprite to be animated.
     */
    public static void addSprite(Sprite sprite) {
        if (manager == null) {
            manager = new SpriteAnimationManager();
        }
        manager.addSprite(sprite);
    }

    /**
     * Remove a sprite from the animation manager. This sprite will no longer be animated.
     *
     * @param sprite The sprite to remove.
     */
    public static void removeSprite(Sprite sprite) {
        if (manager != null) {
            manager.removeSprite(sprite);
        }
    }

    private Locatable parent;
    private int x, y;
    private int activeImage;
    private int fps;

    /**
     * Create a new Sprite object and start animating it.
     *
     * @param x   The relative x position of the sprite.
     * @param y   The relative y position of the sprite.
     * @param fps The fps of the animation. If no animation is desired, set to 0.
     */
    public Sprite(int x, int y, int fps) {
        this.x = x;
        this.y = y;
        this.fps = fps;
        addSprite(this);
    }

    /**
     * Get the length of the spritesheet.
     *
     * @return The number of images in the spritesheet.
     */
    abstract public int numImages();

    /**
     * Use the supplied {@link Drawer} object to render the current sprite.
     *
     * @param d The drawer object to use for rendering.
     */
    abstract public void drawActiveImage(Drawer d);

    /**
     * Get the current index in the spritesheet.
     *
     * @return The current index.
     */
    public int getActiveImageIndex() {
        return activeImage;
    }

    /**
     * The time in milliseconds between each sprite change.
     *
     * @return The animation period, in ms.
     */
    public long getPeriodMillis() {
        return 1000 / fps;
    }

    /**
     * The rendered frames per second of the animation.
     *
     * @return The desired fps of the animation for this sprite.
     */
    public int getFPS() {
        return fps;
    }

    /**
     * Advance one in the spritesheet. Wrap around to the beginning if at the end.
     */
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
