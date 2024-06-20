/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.score;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.fx.Controller;
import org.panteleyev.fx.ToStringConverter;
import org.panteleyev.sapper.game.BoardSize;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.function.Function;

import static org.panteleyev.fx.BoxFactory.hBox;
import static org.panteleyev.fx.FxUtils.COLON;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.LabelFactory.label;
import static org.panteleyev.fx.combobox.ComboBoxBuilder.comboBox;
import static org.panteleyev.sapper.Constants.UI;
import static org.panteleyev.sapper.GlobalContext.scoreboard;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_DATE;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_MINEFIELD;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_RESULTS;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_TIME;

public class ScoreBoardDialog extends BaseDialog<Object> {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("m 'minutes' s 'seconds'");


    private final GridPane grid = new GridPane(15, 15);

    private final Label[] gridHeaders = {
            new Label(""),
            label(fxString(UI, I18N_TIME)),
            label(fxString(UI, I18N_DATE))
    };

    public ScoreBoardDialog(Controller owner, BoardSize boardSize) {
        super(owner, ScoreBoardDialog.class.getResource("/dialog.css"));
        setTitle(fxString(UI, I18N_RESULTS));

        var root = new BorderPane();

        var items = FXCollections.observableList(scoreboard().getBoardSizes())
                .sorted(BoardSize.COMPARATOR.reversed());
        var sizeComboBox = comboBox(items);

        var toolBar = hBox(5, label(fxString(UI, I18N_MINEFIELD, COLON)), sizeComboBox);
        toolBar.setAlignment(Pos.CENTER_LEFT);

        root.setTop(toolBar);

        root.setCenter(grid);
        BorderPane.setMargin(toolBar, new Insets(20, 40, 0, 40));
        BorderPane.setMargin(grid, new Insets(30, 20, 0, 20));

        getDialogPane().setContent(root);

        sizeComboBox.getSelectionModel().selectedItemProperty().addListener((_, _, sel) -> onTypeSelected(sel));
        sizeComboBox.getSelectionModel().select(boardSize);

        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
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
                    new Label(Integer.toString(index - 1)),
                    new Label(TIME_FORMATTER.format(score.time())),
                    new Label(score.date().toString())
            );
        }
        while (index <= 10) {
            grid.addRow(index++, new Label(""), new Label(""), new Label(""));
        }

        var stage = (Stage) getDialogPane().getScene().getWindow();
        stage.sizeToScene();
    }
}
