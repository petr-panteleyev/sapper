// Copyright © 2024-2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.sapper;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class GameTimer {
    private static final long FIXED_RATE = 1000L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("mm:ss");

    private Timer timer;
    private LocalTime localTime;

    private final StringProperty timeStringProperty = new SimpleStringProperty("00:00");

    public ReadOnlyStringProperty timeStringProperty() {
        return timeStringProperty;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void start() {
        localTime = LocalTime.MIN;

        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handle();
            }
        }, FIXED_RATE, FIXED_RATE);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void handle() {
        localTime = localTime.plusSeconds(1);
        Platform.runLater(() -> timeStringProperty.set(FORMATTER.format(localTime)));
    }

    public void reset() {
        timeStringProperty.set("00:00");
    }
}
