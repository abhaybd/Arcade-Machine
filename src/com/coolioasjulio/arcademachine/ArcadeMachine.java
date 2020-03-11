package com.coolioasjulio.arcademachine;

import net.samuelcampos.usbdrivedetector.USBDeviceDetectorManager;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public abstract class ArcadeMachine {
    public static final String GAME_DIR = "out\\artifacts\\PacMan_Main_jar";
    public static final String GAME_INFO_FILE = "games.info";

    protected File[] games;
    private int index;

    public ArcadeMachine() {
        loadNewGames();
        File gameDir = new File(GAME_DIR);
        games = gameDir.listFiles((f, s) -> s.endsWith(".jar"));
        if (games == null) {
            throw new IllegalStateException("No games found!");
        }
        Arrays.sort(games, Comparator.comparing(File::getName));
        System.out.println(Arrays.toString(games));
    }

    protected abstract void resetOnPressedCallbacks();

    private void loadNewGames() {
        USBDeviceDetectorManager driveDetector = new USBDeviceDetectorManager();
        System.out.println("Loading new games from devices!");
        List<USBStorageDevice> devices = driveDetector.getRemovableDevices();
        if (devices.isEmpty()) {
            System.out.println("No removable devices found!");
        }
        for (USBStorageDevice device : devices) {
            System.out.printf("Loading from device: %s\n", device.getSystemDisplayName());
            if (!device.canRead()) {
                System.out.println("Unable to read from device. Skipping...");
                continue;
            }
            File dir = device.getRootDirectory();
            File[] files = dir.listFiles();
            if (files == null) {
                System.out.println("Error reading device! Root directory was not readable! Skipping...");
                continue;
            }
            if (Arrays.stream(files).map(File::getName).anyMatch(GAME_INFO_FILE::equals)) {
                System.out.println("Loading games from device...");
                try {
                    Scanner in = new Scanner(new File(dir, GAME_INFO_FILE));
                    in.useDelimiter("\\s*\n");
                    List<String> lines = new ArrayList<>();
                    in.forEachRemaining(lines::add);
                    if (!verifyGamesInfo(dir, lines)) {
                        System.out.println("Malformed games.info file! Skipping...");
                        continue;
                    }
                    installAndRemoveGames(dir, lines);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error reading games from device! Skipping...");
                }

            } else {
                System.out.println("No games.info file found at device root! Skipping...");
            }
        }
        System.out.println("Finished loading new games!");
    }

    private boolean verifyGamesInfo(File dir, List<String> lines) {
        for (String line : lines) {
            String file = line.substring(1);
            if (!file.endsWith(".jar")) return false;
            if (line.startsWith("+")) {
                if (!new File(dir, file).isFile()) return false;
            } else if (!line.startsWith("-")) return false;
        }
        return true;
    }

    private void installAndRemoveGames(File dir, List<String> gamesInfo) throws IOException {
        for (String line : gamesInfo) {
            String file = line.substring(1);
            if (line.startsWith("+")) {
                System.out.printf("Installing game %s...\n", file);
                File f = new File(dir, file);
                copyTo(f, new File(GAME_DIR, file));
            } else if (line.startsWith("-")) {
                System.out.printf("Removing game %s...\n", file);
                File f = new File(GAME_DIR, file);
                if (!f.isFile()) {
                    System.out.printf("No file found to delete: %s\n", f.getPath());
                } else if (!new File(GAME_DIR, file).delete()) {
                    throw new IOException("Error removing game!");
                }
            }
        }
    }

    private void copyTo(File from, File to) throws IOException {
        byte[] buff = new byte[1024];
        InputStream in = new FileInputStream(from);
        OutputStream out = new FileOutputStream(to);
        int read;
        while ((read = in.read(buff)) != -1) {
            out.write(buff, 0, read);
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void incIndex() {
        index = Utils.clamp(index + 1, 0, games.length);
    }

    public void decIndex() {
        index = Utils.clamp(index - 1, 0, games.length);
    }

    public void launchGame() {
        try {
            resetOnPressedCallbacks();
            File game = games[index];
            Process p = Runtime.getRuntime().exec("java -jar " + game.getAbsolutePath());
            DataOutputStream out = new DataOutputStream(p.getOutputStream());
            Input.getInstance().addOnPressedCallback(keycode -> {
                try {
                    out.writeInt(keycode);
                    out.flush();
                } catch (IOException ignored) {
                }
            });
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
