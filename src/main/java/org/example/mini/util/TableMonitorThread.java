package org.example.mini.util;

import javafx.application.Platform;

/**
 * Background thread that periodically refreshes the game table view.
 * <p>
 * A {@code TableMonitorThread} executes a provided {@link Runnable} callback
 * at a fixed interval. The callback is always dispatched on the JavaFX
 * Application Thread using {@link Platform#runLater(Runnable)}, making it
 * safe to update UI components from the callback code.
 * </p>
 * <p>
 * The thread runs as a daemon and can be stopped gracefully via
 * {@link #stopMonitoring()}.
 * </p>
 */
public class TableMonitorThread extends Thread {

    /**
     * Callback to execute on each refresh cycle.
     * <p>
     * This runnable is invoked on the JavaFX Application Thread via
     * {@link Platform#runLater(Runnable)}.
     * </p>
     */
    private final Runnable updateCallback;

    /**
     * Flag indicating whether the monitoring loop should continue running.
     */
    private volatile boolean running = true;

    /**
     * Delay between two consecutive updates, in milliseconds.
     */
    private final int refreshInterval;

    /**
     * Creates a new {@code TableMonitorThread}.
     *
     * @param updateCallback  the action to execute on each refresh; must be safe
     *                        to run on the JavaFX Application Thread
     * @param refreshInterval the interval between updates in milliseconds
     */
    public TableMonitorThread(Runnable updateCallback, int refreshInterval) {
        this.updateCallback = updateCallback;
        this.refreshInterval = refreshInterval;
        setDaemon(true);
    }

    /**
     * Main execution loop of the monitoring thread.
     * <p>
     * While {@link #running} is {@code true}, this method:
     * </p>
     * <ol>
     *     <li>Dispatches {@link #updateCallback} to the JavaFX Application Thread.</li>
     *     <li>Sleeps for {@link #refreshInterval} milliseconds.</li>
     * </ol>
     * If the thread is interrupted, the loop is terminated.
     */
    @Override
    public void run() {
        while (running) {
            try {
                // Execute the callback on the JavaFX thread
                Platform.runLater(updateCallback);

                // Wait before the next update
                Thread.sleep(refreshInterval);
            } catch (InterruptedException e) {
                System.out.println("Table monitor interrupted");
                break;
            }
        }
    }

    /**
     * Stops the monitoring loop and interrupts the underlying thread.
     * <p>
     * After calling this method, the thread will exit its run loop as soon
     * as possible and no further callbacks will be scheduled.
     * </p>
     */
    public void stopMonitoring() {
        running = false;
        interrupt();
    }
}