package org.example.mini.util;

import javafx.application.Platform;
import org.example.mini.model.game.Game;
import org.example.mini.model.player.HumanPlayer;
import org.example.mini.model.player.MachinePlayer;

public class TurnMonitorThread extends Thread {
    private final Game game;
    private final Runnable onHumanTurn;
    private final Runnable onCpuTurn;
    private volatile boolean running = true;

    public TurnMonitorThread(Game game, Runnable onHumanTurn, Runnable onCpuTurn) {
        this.game = game;
        this.onHumanTurn = onHumanTurn;
        this.onCpuTurn = onCpuTurn;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (running && !game.isGameOver()) {
            if (game.getCurrentPlayer() instanceof HumanPlayer) {
                Platform.runLater(onHumanTurn);
            } else if (game.getCurrentPlayer() instanceof MachinePlayer) {
                Platform.runLater(onCpuTurn);
            }

            try {
                Thread.sleep(200); // check 5 times per second
            } catch (InterruptedException ignored) {}
        }
    }

    public void stopMonitoring() {
        running = false;
    }
}
