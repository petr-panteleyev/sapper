/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GameTimer extends AnimationTimer {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("mm:ss");

    private long startNanos;
    private long seconds;
    private LocalTime localTime;

    private final StringProperty timeStringProperty = new SimpleStringProperty("00:00");

    public ReadOnlyStringProperty timeStringProperty() {
        return timeStringProperty;
    }

    @Override
    public void start() {
        startNanos = 0;
        seconds = 0;
        localTime = LocalTime.ofSecondOfDay(0);
        super.start();
    }

    @Override
    public void handle(long nanos) {
        if (startNanos == 0) {
            startNanos = nanos;
        }

        var diff = nanos - startNanos;
        var newSeconds = diff / 1_000_000_000;
        if (newSeconds > seconds) {
            seconds = newSeconds;
            localTime = LocalTime.ofSecondOfDay(seconds);
            timeStringProperty.set(FORMATTER.format(localTime));
        }
    }

    public void reset() {
        timeStringProperty.set("00:00");
    }
}
