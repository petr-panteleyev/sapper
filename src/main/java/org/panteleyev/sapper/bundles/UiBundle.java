/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.bundles;

import java.util.ListResourceBundle;

import static org.panteleyev.sapper.bundles.Internationalization.I18N_ABOUT;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_CREATE_DESKTOP_ENTRY;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_DATE;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_EXIT;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_FILE;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_GAME;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_HEIGHT;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_HELP;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_MINEFIELD;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_MINES;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_NEW;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_RESULTS;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_SAPPER;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_TIME;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_USER_GAME;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_WIDTH;

public class UiBundle extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {I18N_ABOUT, "About"},
                {I18N_DATE, "Date"},
                {I18N_EXIT, "Exit"},
                {I18N_FILE, "File"},
                {I18N_GAME, "Game"},
                {I18N_HEIGHT, "Height"},
                {I18N_HELP, "Help"},
                {I18N_MINEFIELD, "Minefield"},
                {I18N_MINES, "Mines"},
                {I18N_NEW, "New"},
                {I18N_RESULTS, "Results"},
                {I18N_SAPPER, "Sapper"},
                {I18N_TIME, "Time"},
                {I18N_WIDTH, "Width"},
                {I18N_CREATE_DESKTOP_ENTRY, "Create Desktop Entry"},
                {I18N_USER_GAME, "Custom"}
        };
    }
}
