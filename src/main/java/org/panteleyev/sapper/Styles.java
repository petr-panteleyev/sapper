/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

import org.panteleyev.fx.Controller;

public final class Styles {
    public static final double BIG_SPACING = 10.0;
    public static final double SMALL_SPACING = 5.0;

    public static final String STYLE_ABOUT_LABEL = "aboutLabel";

    public static final String DIALOG_STYLE_SHEET = Controller.encodeStyleSheet("""
            .label {
                -fx-font-size: 14
            }
            
            .button {
                -fx-font-size: 14
            }
            
            .tableHeader {
                -fx-font-weight: bold;
            }
            
            .gridPane {
                -fx-hgap: 5;
                -fx-vgap: 10;
            }
            """);

    public static final String ABOUT_DIALOG_STYLE_SHEET = Controller.encodeStyleSheet("""
            .gridPane {
                -fx-hgap: 5;
                -fx-vgap: 5;
            }
            
            .dialog-pane:header .header-panel .label {
                -fx-font-family: "Dialog";
                -fx-font-size: 28;
                -fx-font-weight: bold;
            }
            
            .label {
                -fx-font-family: "Dialog";
                -fx-font-size: 14;
            }
            
            .aboutLabel {
                -fx-font-family: "Dialog";
                -fx-font-size: 28;
                -fx-font-weight: bold;
            }
            """);

    private Styles() {
    }
}
