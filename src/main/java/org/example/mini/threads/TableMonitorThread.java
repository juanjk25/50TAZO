package org.example.mini.util;

import javafx.application.Platform;

public class TableMonitorThread extends Thread {
    private final Runnable updateCallback;
    private volatile boolean running = true;
    private final int refreshInterval; // en milisegundos

    public TableMonitorThread(Runnable updateCallback, int refreshInterval) {
        this.updateCallback = updateCallback;
        this.refreshInterval = refreshInterval;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Ejecutar el callback en el hilo de JavaFX
                Platform.runLater(updateCallback);

                // Esperar antes de la siguiente actualización
                Thread.sleep(refreshInterval);
            } catch (InterruptedException e) {
                System.out.println("Table monitor interrupted");
                break;
            }
        }
    }

    public void stopMonitoring() {
        running = false;
        interrupt();
    }
}