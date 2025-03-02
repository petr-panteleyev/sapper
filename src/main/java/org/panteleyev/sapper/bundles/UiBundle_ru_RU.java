/*
 Copyright © 2024-2025 Petr Panteleyev <petr@panteleyev.org>
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

public class UiBundle_ru_RU extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {I18N_ABOUT, "О программе"},
                {I18N_DATE, "Дата"},
                {I18N_EXIT, "Выход"},
                {I18N_FILE, "Файл"},
                {I18N_GAME, "Игра"},
                {I18N_HEIGHT, "Высота"},
                {I18N_HELP, "Справка"},
                {I18N_MINEFIELD, "Минное поле"},
                {I18N_MINES, "Мины"},
                {I18N_NEW, "Новая"},
                {I18N_RESULTS, "Результаты"},
                {I18N_SAPPER, "Сапёр"},
                {I18N_TIME, "Время"},
                {I18N_WIDTH, "Ширина"},
                {I18N_CREATE_DESKTOP_ENTRY, "Создать ярлык приложения"},
                {I18N_USER_GAME, "Пользовательская"}
        };
    }
}
