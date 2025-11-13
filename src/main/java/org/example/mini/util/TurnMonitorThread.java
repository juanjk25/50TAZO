package org.example.mini.util;

import javafx.application.Platform;
import org.example.mini.model.game.Game;
import org.example.mini.model.player.HumanPlayer;
import org.example.mini.model.player.MachinePlayer;

/**
 * Background thread that observes the current turn and notifies the UI layer.
 * <p>
 * A {@code TurnMonitorThread} periodically checks which player has the turn
 * in a {@link Game} instance and triggers one of two callbacks:
 * </p>
 * <ul>
 *     <li>{@code onHumanTurn} when the current player is a {@link HumanPlayer}.</li>
 *     <li>{@code onCpuTurn} when the current player is a {@link MachinePlayer}.</li>
 * </ul>
 * <p>
 * Callbacks are always dispatched on the JavaFX Application Thread via
 * {@link Platform#runLater(Runnable)}, making them safe for UI updates.
 * The thread runs as a daemon and can be stopped via {@link #stopMonitoring()}.
 * </p>
 */
public class TurnMonitorThread extends Thread {

    /**
     * Game whose current player is being observed.
     */
    private final Game game;

    /**
     * Callback executed when it is the human player's turn.
     * <p>
     * Dispatched on the JavaFX Application Thread.
     * </p>
     */
    private final Runnable onHumanTurn;

    /**
     * Callback executed when it is a CPU player's turn.
     * <p>
     * Dispatched on the JavaFX Application Thread.
     * </p>
     */
    private final Runnable onCpuTurn;

    /**
     * Flag that controls the main loop of the thread.
     */
    private volatile boolean running = true;

    /**
     * Creates a new {@code TurnMonitorThread}.
     *
     * @param game       the game whose turn state should be monitored
     * @param onHumanTurn callback to invoke when the current player is human;
     *                    must be safe to run on the JavaFX Application Thread
     * @param onCpuTurn   callback to invoke when the current player is a CPU;
     *                    must be safe to run on the JavaFX Application Thread
     */
    public TurnMonitorThread(Game game, Runnable onHumanTurn, Runnable onCpuTurn) {
        this.game = game;
        this.onHumanTurn = onHumanTurn;
        this.onCpuTurn = onCpuTurn;
        setDaemon(true);
    }

    /**
     * Main monitoring loop.
     * <p>
     * While {@link #running} is {@code true} and the game is not over, this
     * method:
     * </p>
     * <ol>
     *     <li>Checks the type of the current player.</li>
     *     <li>Schedules {@link #onHumanTurn} or {@link #onCpuTurn} on the
     *         JavaFX Application Thread.</li>
     *     <li>Sleeps for 200 ms before checking again.</li>
     * </ol>
     */
    @Override
    public void run() {
        while (running && !game.isGameOver()) {
            if (game.getCurrentPlayer() instanceof HumanPlayer) {
                Platform.runLater(onHumanTurn);
            } else if (game.getCurrentPlayer() instanceof MachinePlayer) {
                Platform.runLater(onCpuTurn);
            }

            try {
                // Check roughly 5 times per second
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
                // Allow thread to exit gracefully if interrupted
                break;
            }
        }
    }

    /**
     * Requests the monitoring thread to stop.
     * <p>
     * Sets {@link #running} to {@code false}. The thread will exit its loop
     * after the next sleep interval completes or is interrupted.
     * </p>
     */
    public void stopMonitoring() {
        running = false;
    }
}
