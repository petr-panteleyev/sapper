/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

import org.panteleyev.sapper.bundles.UiBundle;

import java.util.ResourceBundle;

import static java.util.ResourceBundle.getBundle;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_SAPPER;

public final class Constants {
    public static final ResourceBundle BUILD_INFO_BUNDLE = getBundle("buildInfo");
    public static final ResourceBundle UI = getBundle(UiBundle.class.getCanonicalName());

    public static final String APP_TITLE = fxString(UI, I18N_SAPPER);

    private Constants() {
    }
}
