/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
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
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("mm:ss");

    private Timer timer;

    private long seconds;
    private LocalTime localTime;

    private final StringProperty timeStringProperty = new SimpleStringProperty("00:00");

    public ReadOnlyStringProperty timeStringProperty() {
        return timeStringProperty;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void start() {
        seconds = 0;

        timer= new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handle();
            }
        }, 1000, 1000);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }


    public void handle() {
        seconds++;
        localTime = LocalTime.ofSecondOfDay(seconds);
        Platform.runLater(() -> timeStringProperty.set(FORMATTER.format(localTime)));
    }

    public void reset() {
        timeStringProperty.set("00:00");
    }
}
