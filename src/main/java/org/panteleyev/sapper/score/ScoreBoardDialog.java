// Copyright © 2024-2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.sapper.score;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.fx.Controller;
import org.panteleyev.sapper.game.BoardSize;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import static org.panteleyev.fx.factories.BoxFactory.hBox;
import static org.panteleyev.fx.factories.ComboBoxFactory.comboBox;
import static org.panteleyev.fx.factories.LabelFactory.label;
import static org.panteleyev.fx.factories.StringFactory.COLON;
import static org.panteleyev.fx.factories.StringFactory.string;
import static org.panteleyev.sapper.Constants.UI;
import static org.panteleyev.sapper.GlobalContext.scoreboard;
import static org.panteleyev.sapper.Styles.DIALOG_STYLE_SHEET;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_DATE;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_MINEFIELD;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_RESULTS;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_TIME;

public class ScoreBoardDialog extends BaseDialog<Object> {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("m 'minutes' s 'seconds'");

    private final GridPane grid = new GridPane(15, 15);

    private final Label[] gridHeaders = {
            label(""),
            label(string(UI, I18N_TIME)),
            label(string(UI, I18N_DATE))
    };

    public ScoreBoardDialog(Controller owner, BoardSize boardSize) {
        super(owner, DIALOG_STYLE_SHEET);
        setTitle(string(UI, I18N_RESULTS));

        var items = FXCollections.observableList(scoreboard().getBoardSizes())
                .sorted(BoardSize.COMPARATOR.reversed());
        var sizeComboBox = comboBox(items);

        var toolBar = hBox(5, label(string(UI, I18N_MINEFIELD, COLON)), sizeComboBox);
        toolBar.setAlignment(Pos.CENTER_LEFT);

        BorderPane.setMargin(toolBar, new Insets(20, 40, 0, 40));
        BorderPane.setMargin(grid, new Insets(30, 20, 0, 20));

        getDialogPane().setContent(new BorderPane(grid, toolBar, null, null, null));

        sizeComboBox.getSelectionModel().selectedItemProperty().addListener((_, _, sel) -> onTypeSelected(sel));
        sizeComboBox.getSelectionModel().select(boardSize);

        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        centerOnScreen();
    }

    private void onTypeSelected(BoardSize selected) {
        var scores = scoreboard().getScores(selected).stream()
                .sorted(Comparator.comparing(GameScore::time))
                .toList();
        grid.getChildren().clear();

        for (var label : gridHeaders) {
            label.getStyleClass().add("tableHeader");
        }

        grid.addRow(0, gridHeaders);
        var index = 1;
        for (var score : scores) {
            grid.addRow(index++,
                    label(Integer.toString(index - 1)),
                    label(TIME_FORMATTER.format(score.time())),
                    label(score.date().toString())
            );
        }
        while (index <= 10) {
            grid.addRow(index++, label(""), label(""), label(""));
        }

        getDialogPane().getScene().getWindow().sizeToScene();
    }
}
