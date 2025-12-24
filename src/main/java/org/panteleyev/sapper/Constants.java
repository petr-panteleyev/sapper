// Copyright © 2024-2025 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.sapper;

import org.panteleyev.sapper.bundles.UiBundle;

import java.util.ResourceBundle;

import static java.util.ResourceBundle.getBundle;
import static org.panteleyev.fx.factories.StringFactory.string;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_SAPPER;

public final class Constants {
    public static final ResourceBundle BUILD_INFO_BUNDLE = getBundle("buildInfo");
    public static final ResourceBundle UI = getBundle(UiBundle.class.getCanonicalName());

    public static final String APP_TITLE = string(UI, I18N_SAPPER);

    private Constants() {
    }
}
