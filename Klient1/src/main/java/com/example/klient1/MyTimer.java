package com.example.klient1;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class MyTimer implements Runnable {
    private long gameTime;
    private Thread thread;
    private Label gameTimeLabel;

    public MyTimer(Label gameTimeLabel) {
        gameTime = 0;
        thread = null;
        this.gameTimeLabel = gameTimeLabel;
    }

    public long getGameTime() {
        return gameTime;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void koniec() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                long currentTime = gameTime;
                Platform.runLater(() -> gameTimeLabel.setText("Czas: " + currentTime));
                gameTime++;
                Thread.sleep(1000); // Odczekaj 1 sekundę
            }
        } catch (InterruptedException e) {
            // Przechwytuj wyjątek InterruptedException, aby wątek mógł zostać poprawnie zatrzymany
        }
    }
}
