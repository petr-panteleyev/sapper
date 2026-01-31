// Copyright © 2024-2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.sapper;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.fx.Controller;
import org.panteleyev.sapper.game.BoardSize;

import java.util.List;

import static org.panteleyev.fx.factories.LabelFactory.label;
import static org.panteleyev.fx.factories.StringFactory.COLON;
import static org.panteleyev.fx.factories.StringFactory.string;
import static org.panteleyev.fx.factories.grid.GridPaneFactory.gridPane;
import static org.panteleyev.fx.factories.grid.GridRow.gridRow;
import static org.panteleyev.sapper.Constants.UI;
import static org.panteleyev.sapper.Styles.DIALOG_STYLE_SHEET;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_HEIGHT;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_MINES;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_USER_GAME;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_WIDTH;
import static org.panteleyev.sapper.game.BoardSize.MAX_HEIGHT;
import static org.panteleyev.sapper.game.BoardSize.MAX_WIDTH;
import static org.panteleyev.sapper.game.BoardSize.MIN_HEIGHT;
import static org.panteleyev.sapper.game.BoardSize.MIN_WIDTH;

public class BoardSizeDialog extends BaseDialog<BoardSize> {
    private final Spinner<Integer> widthSpinner = new Spinner<>(MIN_WIDTH, MAX_WIDTH, MIN_WIDTH);
    private final Spinner<Integer> heightSpinner = new Spinner<>(MIN_HEIGHT, MAX_HEIGHT, MIN_HEIGHT);
    private final Spinner<Integer> mineSpinner = new Spinner<>(1, (MIN_WIDTH - 1) * (MIN_HEIGHT - 1), 1);
    private final Label mineLabel = label("1 - " + (MIN_WIDTH - 1) * (MIN_HEIGHT - 1));

    public BoardSizeDialog(Controller owner) {
        super(owner, DIALOG_STYLE_SHEET);
        setTitle(string(UI, I18N_USER_GAME));

        widthSpinner.valueProperty().addListener((_, _, _) -> adjustMineSpinner());
        heightSpinner.valueProperty().addListener((_, _, _) -> adjustMineSpinner());

        var grid = gridPane(List.of(
                gridRow(label(string(UI, I18N_WIDTH, COLON)), widthSpinner,
                        label(MIN_WIDTH + " - " + MAX_WIDTH)),
                gridRow(label(string(UI, I18N_HEIGHT, COLON)), heightSpinner,
                        label(MIN_HEIGHT + " - " + MAX_HEIGHT)),
                gridRow(label(string(UI, I18N_MINES, COLON)), mineSpinner, mineLabel)
        ));
        grid.getStyleClass().add("gridPane");
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setPrefWidth(Region.USE_COMPUTED_SIZE);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(buttonType -> {
            if (buttonType != ButtonType.OK) {
                return null;
            }
            return new BoardSize(
                    widthSpinner.getValue(),
                    heightSpinner.getValue(),
                    mineSpinner.getValue()
            );
        });
    }

    private void adjustMineSpinner() {
        if (mineSpinner.getValueFactory() instanceof SpinnerValueFactory.IntegerSpinnerValueFactory intFactory) {
            var maxValue = (widthSpinner.getValue() - 1) * (heightSpinner.getValue() - 1);
            intFactory.setMax(maxValue);
            mineLabel.setText("1 - " + maxValue);

            var stage = (Stage) getDialogPane().getScene().getWindow();
            stage.sizeToScene();
        }
    }
}
