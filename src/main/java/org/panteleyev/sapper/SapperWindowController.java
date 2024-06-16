/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.panteleyev.freedesktop.Utility;
import org.panteleyev.freedesktop.entry.DesktopEntryBuilder;
import org.panteleyev.freedesktop.entry.DesktopEntryType;
import org.panteleyev.freedesktop.menu.Category;
import org.panteleyev.fx.Controller;
import org.panteleyev.sapper.game.Cell;
import org.panteleyev.sapper.game.Game;
import org.panteleyev.sapper.game.GameStatus;
import org.panteleyev.sapper.game.GameType;
import org.panteleyev.sapper.score.GameScore;
import org.panteleyev.sapper.score.ScoreBoardDialog;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.panteleyev.freedesktop.Utility.isLinux;
import static org.panteleyev.freedesktop.entry.DesktopEntryBuilder.localeString;
import static org.panteleyev.fx.FxUtils.ELLIPSIS;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.MenuFactory.menu;
import static org.panteleyev.fx.MenuFactory.menuItem;
import static org.panteleyev.fx.grid.ColumnConstraintsBuilder.columnConstraints;
import static org.panteleyev.fx.grid.GridBuilder.gridPane;
import static org.panteleyev.fx.grid.GridRowBuilder.gridRow;
import static org.panteleyev.sapper.Constants.APP_TITLE;
import static org.panteleyev.sapper.Constants.UI;
import static org.panteleyev.sapper.GlobalContext.scoreboard;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_ABOUT;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_CREATE_DESKTOP_ENTRY;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_EXIT;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_FILE;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_GAME;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_HELP;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_RESULTS;

public class SapperWindowController extends Controller implements Game.CellChangeCallback, Game.GameStatusChangeCallback {
    private static final int CELL_SIZE = 40;
    private static final int IMAGE_SIZE = 24;

    private GameType gameType = GameType.BIG;
    private Game board;
    private ToggleButton[] buttons;

    private final Label remainingMinesLabel = new Label();
    private final ImageView controlButtonImageView;
    private final GridPane grid = new GridPane();

    private static final Color[] NUMBER_COLORS = {
            null,
            Color.BLUE,
            Color.GREEN,
            Color.RED,
            Color.DARKBLUE,
            Color.BROWN,
            Color.rgb(0x00, 0x80, 0x80),
            Color.BLACK,
            Color.GRAY
    };

    private final GameTimer timer = new GameTimer();

    public SapperWindowController(Stage stage) {
        super(stage, "/main.css");
        stage.setResizable(false);
        stage.getIcons().add(Picture.ICON.getImage());

        var timerLabel = new Label();
        timerLabel.textProperty().bind(timer.timeStringProperty());

        remainingMinesLabel.getStyleClass().add("remainingCount");
        timerLabel.getStyleClass().add("remainingCount");

        controlButtonImageView = new ImageView(Picture.SMILING_FACE.getImage());
        controlButtonImageView.setFitWidth(48);
        controlButtonImageView.setFitHeight(48);
        var controlButton = new Button(null, controlButtonImageView);
        controlButton.setFocusTraversable(false);
        controlButton.setOnAction(_ -> newGame(gameType));

        var innerPane = new BorderPane(grid);
        var toolBar = gridPane(List.of(
                gridRow(remainingMinesLabel, controlButton, timerLabel)
        ), b -> b.withConstraints(
                List.of(
                        columnConstraints(ccb -> ccb.withPercentWidth(33.33)
                                .withHalignment(HPos.LEFT)),
                        columnConstraints(ccb -> ccb.withPercentWidth(33.33)
                                .withHalignment(HPos.CENTER)),
                        columnConstraints(ccb -> ccb.withPercentWidth(33.33)
                                .withHalignment(HPos.RIGHT))
                )
        ));

        remainingMinesLabel.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        controlButton.setAlignment(javafx.geometry.Pos.CENTER);
        timerLabel.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        innerPane.setTop(toolBar);
        BorderPane.setMargin(toolBar, new Insets(10));

        var borderPane = new BorderPane(innerPane);
        borderPane.setTop(createMainMenu());

        setupWindow(borderPane);

        newGame(GameType.BIG);
    }

    @Override
    public String getTitle() {
        return APP_TITLE;
    }

    private void newGame(GameType gameType) {
        controlButtonImageView.setImage(Picture.SMILING_FACE.getImage());

        this.gameType = gameType;
        board = new Game(gameType, this, this);
        initButtons();

        timer.stop();
        timer.reset();

        remainingMinesLabel.setText(Integer.toString(gameType.getMines()));
        getStage().sizeToScene();
    }

    private void initButtons() {
        grid.getChildren().clear();
        buttons = new ToggleButton[board.getSize()];

        var index = 0;
        for (var y = 0; y < board.getHeight(); y++) {
            for (var x = 0; x < board.getWidth(); x++) {
                var button = new ToggleButton();
                button.getStyleClass().add("cellButton");
                button.setPrefSize(CELL_SIZE, CELL_SIZE);
                button.setMaxSize(CELL_SIZE, CELL_SIZE);
                button.setMinSize(CELL_SIZE, CELL_SIZE);
                button.setFocusTraversable(false);
                button.setUserData(index);
                button.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
                button.addEventFilter(MouseEvent.MOUSE_RELEASED, this::onButtonPress);
                buttons[index++] = button;
                grid.add(button, x, y);
            }
        }
    }

    private MenuBar createMainMenu() {
        return new MenuBar(
                menu(fxString(UI, I18N_FILE),
                        isLinux() ? menuItem(fxString(UI, I18N_CREATE_DESKTOP_ENTRY),
                                _ -> onCreateDesktopEntry()) : null,
                        isLinux() ? new SeparatorMenuItem() : null,
                        menuItem(fxString(UI, I18N_EXIT), _ -> onExit())
                ),
                menu(fxString(UI, I18N_GAME),
                        menuItem(GameType.BIG.toString(), _ -> newGame(GameType.BIG)),
                        menuItem(GameType.MEDIUM.toString(), _ -> newGame(GameType.MEDIUM)),
                        menuItem(GameType.SMALL.toString(), _ -> newGame(GameType.SMALL)),
                        new SeparatorMenuItem(),
                        menuItem(fxString(UI, I18N_RESULTS),
                                _ -> new ScoreBoardDialog(this, gameType).showAndWait())
                ),
                menu(fxString(UI, I18N_HELP),
                        menuItem(fxString(UI, I18N_ABOUT) + " " + APP_TITLE + ELLIPSIS,
                                _ -> new AboutDialog(this).showAndWait()))
        );
    }

    private void renderSuccess() {
        timer.stop();

        for (var button : buttons) {
            button.setDisable(true);
        }

        controlButtonImageView.setImage(Picture.LAUGHING_FACE.getImage());
        var gameScore = new GameScore(
                gameType,
                LocalDate.now(),
                timer.getLocalTime()
        );
        var top = scoreboard().add(gameScore);
        scoreboard().save();
        if (top) {
            new ScoreBoardDialog(this, gameType).showAndWait();
        }
    }

    private void renderFailure(int clickPoint) {
        timer.stop();
        controlButtonImageView.setImage(Picture.SAD_FACE.getImage());

        for (int x = 0; x < board.getSize(); x++) {
            var value = board.getValue(x);
            var button = buttons[x];
            button.setDisable(true);

            if (x == clickPoint) {
                button.setText("*");
                button.setTextFill(Color.RED);
            } else {
                if (Cell.emptyWithFlag(value)) {
                    button.setText(null);
                    button.setGraphic(Picture.imageView(Picture.BLACK_FLAG, IMAGE_SIZE, IMAGE_SIZE));
                }

                if (Cell.mineNoFlag(value)) {
                    button.setText("*");
                    button.setGraphic(null);
                }
            }
        }
    }

    @Override
    public void onCellChanged(int x, int newValue) {
        var button = buttons[x];

        if (Cell.flag(newValue)) {
            button.setText(null);
            button.setGraphic(Picture.imageView(Picture.RED_FLAG, IMAGE_SIZE, IMAGE_SIZE));
        } else if (Cell.isExplored(newValue)) {
            button.setGraphic(null);
            button.setSelected(true);
            button.setDisable(true);

            if (newValue == 0) {
                button.setText(null);
            } else {
                button.setText(Integer.toString(newValue));
                button.setTextFill(NUMBER_COLORS[newValue]);
            }
        } else {
            button.setText(null);
            button.setGraphic(null);
        }
    }

    @Override
    public void onGameStatusChanged(int x, GameStatus newStatus) {
        switch (newStatus) {
            case SUCCESS -> renderSuccess();
            case FAILURE -> renderFailure(x);
            case IN_PROGRESS -> timer.start();
        }
    }

    private void onButtonPress(MouseEvent event) {
        event.consume();

        if (board.getGameStatus().isFinal()) {
            return;
        }

        if (event.getSource() instanceof ToggleButton button
                && button.getUserData() instanceof Integer hitPoint)
        {
            if (event.getButton() == MouseButton.SECONDARY) {
                board.toggleFlag(hitPoint);
            } else {
                board.processHit(hitPoint);
            }

            remainingMinesLabel.setText(Integer.toString(board.getRemainingMines()));
        }
    }

    private void onExit() {
        getStage().fireEvent(new WindowEvent(getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    private void onCreateDesktopEntry() {
        if (!isLinux()) {
            return;
        }
        Utility.getExecutablePath().ifPresent(command -> {
            var execFile = new File(command);
            var rootDir = execFile.getParentFile().getParentFile().getAbsolutePath();

            var desktopEntry = new DesktopEntryBuilder(DesktopEntryType.APPLICATION)
                    .version(DesktopEntryBuilder.VERSION_1_5)
                    .name("Sapper")
                    .name(localeString("Сапёр", "ru_RU"))
                    .categories(List.of(Category.GAME, Category.JAVA))
                    .comment("Sapper Game")
                    .comment(localeString("Игра Сапёр", "ru_RU"))
                    .exec("\"" + command + "\"")
                    .icon(rootDir + "/lib/Sapper.png")
                    .build();
            desktopEntry.write("sapper");
        });
    }
}
