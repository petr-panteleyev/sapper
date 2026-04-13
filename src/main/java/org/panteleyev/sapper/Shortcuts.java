// Copyright © 2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.sapper;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;

public final class Shortcuts {
    public static final KeyCodeCombination SHORTCUT_NEW_GAME = new KeyCodeCombination(KeyCode.N, SHORTCUT_DOWN);

    public static final KeyCodeCombination SHORTCUT_RESULTS = new KeyCodeCombination(KeyCode.R, SHORTCUT_DOWN);

    public static final KeyCodeCombination SHORTCUT_CUSTOM_GAME = new KeyCodeCombination(KeyCode.U, SHORTCUT_DOWN);

    public static final KeyCodeCombination SHORTCUT_LARGE_GAME = new KeyCodeCombination(KeyCode.L, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_MEDIUM_GAME = new KeyCodeCombination(KeyCode.M, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_SMALL_GAME = new KeyCodeCombination(KeyCode.S, SHORTCUT_DOWN);

    private Shortcuts() {
    }
}