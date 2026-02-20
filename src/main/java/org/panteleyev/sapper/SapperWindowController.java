// Copyright © 2024-2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.sapper;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.panteleyev.freedesktop.Utility;
import org.panteleyev.freedesktop.entry.DesktopEntryBuilder;
import org.panteleyev.freedesktop.entry.DesktopEntryType;
import org.panteleyev.freedesktop.menu.Category;
import org.panteleyev.fx.Controller;
import org.panteleyev.sapper.game.BoardSize;
import org.panteleyev.sapper.game.Cell;
import org.panteleyev.sapper.game.Game;
import org.panteleyev.sapper.game.GameStatus;
import org.panteleyev.sapper.score.GameScore;
import org.panteleyev.sapper.score.ScoreBoardDialog;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.panteleyev.freedesktop.Utility.isLinux;
import static org.panteleyev.freedesktop.entry.DesktopEntryBuilder.localeString;
import static org.panteleyev.fx.factories.MenuFactory.menu;
import static org.panteleyev.fx.factories.MenuFactory.menuBar;
import static org.panteleyev.fx.factories.MenuFactory.menuItem;
import static org.panteleyev.fx.factories.StringFactory.ELLIPSIS;
import static org.panteleyev.fx.factories.StringFactory.string;
import static org.panteleyev.fx.factories.grid.GridPaneFactory.gridPane;
import static org.panteleyev.fx.factories.grid.GridRow.gridRow;
import static org.panteleyev.sapper.Constants.APP_TITLE;
import static org.panteleyev.sapper.Constants.UI;
import static org.panteleyev.sapper.GlobalContext.scoreboard;
import static org.panteleyev.sapper.GlobalContext.settings;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_ABOUT;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_CREATE_DESKTOP_ENTRY;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_EXIT;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_FILE;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_GAME;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_HELP;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_NEW;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_RESULTS;
import static org.panteleyev.sapper.bundles.Internationalization.I18N_USER_GAME;
import static org.panteleyev.sapper.game.BoardSize.MAX_HEIGHT;
import static org.panteleyev.sapper.game.BoardSize.MAX_WIDTH;
import static org.panteleyev.sapper.game.BoardSize.STANDARD_SIZES;

public class SapperWindowController extends Controller implements Game.CellChangeCallback, Game.GameStatusChangeCallback {
    private static final int CELL_SIZE = 40;
    private static final int IMAGE_SIZE = 24;

    private static final String CELL_FONT_FAMILY = "Mine-Sweeper";
    private static final double CELL_FONT_SIZE = 20;

    private static final String IND_FONT_FAMILY = "Pixel LCD7";
    private static final double IND_FONT_SIZE = 28;

    private static final double CTRL_BUTTON_IMAGE_SIZE = 48;

    private BoardSize boardSize = BoardSize.BIG;
    private final Game game = new Game(this, this);
    private final ToggleButton[] buttons = new ToggleButton[MAX_WIDTH * MAX_HEIGHT];

    private final Font indicatorFont = Font.font(IND_FONT_FAMILY, FontWeight.BOLD, IND_FONT_SIZE);

    private final Label remainingMinesLabel = counterLabel(indicatorFont);
    private final ImageView controlButtonImageView = controlButtonImageView();
    private final GridPane grid = new GridPane();

    private final Menu customGameMenu = menu(string(UI, I18N_USER_GAME, ELLIPSIS));
    private final MenuItem newCustomGameMenuItem = newCustomGameMenuItem();

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

    private static final Insets ZERO_INSETS = new Insets(0);

    public SapperWindowController(Stage stage) {
        super(stage, null);
        stage.setResizable(false);
        stage.getIcons().add(Picture.ICON.getImage());

        var cellFont = Font.font(CELL_FONT_FAMILY, FontWeight.BOLD, CELL_FONT_SIZE);

        for (int x = 0; x < buttons.length; x++) {
            buttons[x] = createCellButton(x, cellFont, this::onButtonPress);
        }

        var toolBar = gridPane(
                List.of(gridRow(
                        remainingMinesLabel,
                        controlButton(controlButtonImageView, _ -> newGame(boardSize)),
                        timerLabel(indicatorFont, timer))),
                List.of(constraints(HPos.LEFT), constraints(HPos.CENTER), constraints(HPos.RIGHT))
        );

        var innerPane = new BorderPane(grid, toolBar, null, null, null);

        remainingMinesLabel.setAlignment(Pos.CENTER_LEFT);
        BorderPane.setMargin(toolBar, new Insets(10));

        setupWindow(new BorderPane(innerPane, createMainMenu(), null, null, null));

        newGame(settings().getLastBoardSize());
        stage.centerOnScreen();
    }

    @Override
    public String getTitle() {
        return APP_TITLE;
    }

    private void newGame(BoardSize boardSize) {
        controlButtonImageView.setImage(Picture.SMILING_FACE.getImage());

        this.boardSize = boardSize;
        game.newGame(boardSize);
        initButtons();

        timer.stop();
        timer.reset();

        remainingMinesLabel.setText(Integer.toString(boardSize.mines()));
        getStage().sizeToScene();
    }

    private void onCustomGame() {
        new BoardSizeDialog(this).showAndWait().ifPresent(this::newGame);
    }

    private void initButtons() {
        grid.getChildren().clear();

        var index = 0;
        for (var y = 0; y < boardSize.height(); y++) {
            for (var x = 0; x < boardSize.width(); x++) {
                grid.add(resetCellButton(buttons[index++]), x, y);
            }
        }
    }

    private MenuBar createMainMenu() {
        buildCustomGamesMenu();
        return menuBar(
                menu(string(UI, I18N_FILE),
                        isLinux() ? menuItem(string(UI, I18N_CREATE_DESKTOP_ENTRY),
                                _ -> onCreateDesktopEntry()) : null,
                        isLinux() ? new SeparatorMenuItem() : null,
                        menuItem(string(UI, I18N_EXIT), _ -> onExit())
                ),
                menu(string(UI, I18N_GAME),
                        newGameMenuItem(BoardSize.BIG),
                        newGameMenuItem(BoardSize.MEDIUM),
                        newGameMenuItem(BoardSize.SMALL),
                        new SeparatorMenuItem(),
                        customGameMenu,
                        new SeparatorMenuItem(),
                        resultsMenuItem()
                ),
                menu(string(UI, I18N_HELP),
                        menuItem(string(UI, I18N_ABOUT, ELLIPSIS), _ -> new AboutDialog(this).showAndWait()))
        );
    }

    private void buildCustomGamesMenu() {
        customGameMenu.getItems().clear();
        customGameMenu.getItems().add(newCustomGameMenuItem);

        var customSizes = scoreboard().getBoardSizes().stream()
                .filter(s -> !STANDARD_SIZES.contains(s))
                .sorted(BoardSize.COMPARATOR.reversed())
                .toList();

        if (!customSizes.isEmpty()) {
            customGameMenu.getItems().add(new SeparatorMenuItem());
            for (var size : customSizes) {
                customGameMenu.getItems().add(menuItem(size.toString(), _ -> newGame(size)));
            }
        }
    }

    private void renderSuccess() {
        timer.stop();

        for (var button : buttons) {
            button.setDisable(true);
        }

        controlButtonImageView.setImage(Picture.LAUGHING_FACE.getImage());
        var gameScore = new GameScore(boardSize, LocalDate.now(), timer.getLocalTime());
        var top = scoreboard().add(gameScore);
        scoreboard().save();
        buildCustomGamesMenu();
        if (top) {
            new ScoreBoardDialog(this, boardSize).showAndWait();
        }
    }

    private void renderFailure(int clickPoint) {
        timer.stop();
        controlButtonImageView.setImage(Picture.SAD_FACE.getImage());

        for (int x = 0; x < game.getSize(); x++) {
            var value = game.getValue(x);
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

        if (game.getGameStatus().isFinal()) return;

        if (event.getSource() instanceof ToggleButton button
                && button.getUserData() instanceof Integer hitPoint) {
            if (event.getButton() == MouseButton.SECONDARY) {
                game.toggleFlag(hitPoint);
            } else {
                game.processHit(hitPoint);
            }

            remainingMinesLabel.setText(Integer.toString(game.getRemainingMines()));
        }
    }

    private void onExit() {
        getStage().fireEvent(new WindowEvent(getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @Override
    protected void onWindowHiding() {
        super.onWindowHiding();
        settings().update(settings -> settings.setLastBoardSize(boardSize));
    }

    private void onCreateDesktopEntry() {
        if (!isLinux()) return;

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

    private MenuItem newGameMenuItem(BoardSize boardSize) {
        var menuItem = new MenuItem(boardSize.toString());
        menuItem.setOnAction(_ -> newGame(boardSize));
        return menuItem;
    }

    private MenuItem newCustomGameMenuItem() {
        var menuItem = new MenuItem(string(UI, I18N_NEW, ELLIPSIS));
        menuItem.setOnAction(_ -> onCustomGame());
        return menuItem;
    }

    private MenuItem resultsMenuItem() {
        var menuItem = new MenuItem(string(UI, I18N_RESULTS));
        menuItem.setOnAction(_ -> new ScoreBoardDialog(this, boardSize).showAndWait());
        return menuItem;
    }

    private static ColumnConstraints constraints(HPos alignment) {
        var constraints = new ColumnConstraints();
        constraints.setPercentWidth(33.333);
        constraints.setHalignment(alignment);
        return constraints;
    }

    private static ToggleButton createCellButton(int index, Font font, EventHandler<MouseEvent> eventHandler) {
        var button = new ToggleButton();
        button.setFont(font);
        button.setPadding(ZERO_INSETS);
        button.setOpacity(1);
        button.setPrefSize(CELL_SIZE, CELL_SIZE);
        button.setMaxSize(CELL_SIZE, CELL_SIZE);
        button.setMinSize(CELL_SIZE, CELL_SIZE);
        button.setFocusTraversable(false);
        button.setUserData(index);
        button.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
        button.addEventFilter(MouseEvent.MOUSE_RELEASED, eventHandler);
        return button;
    }

    private static ToggleButton resetCellButton(ToggleButton button) {
        button.setDisable(false);
        button.setSelected(false);
        button.setText(null);
        button.setGraphic(null);
        button.setTextFill(Color.BLACK);
        return button;
    }

    private static ImageView controlButtonImageView() {
        var imageView = new ImageView(Picture.SMILING_FACE.getImage());
        imageView.setFitWidth(CTRL_BUTTON_IMAGE_SIZE);
        imageView.setFitHeight(CTRL_BUTTON_IMAGE_SIZE);
        return imageView;
    }

    private static Button controlButton(ImageView imageView, EventHandler<ActionEvent> handler) {
        var button = new Button(null, imageView);
        button.setFocusTraversable(false);
        button.setOnAction(handler);
        button.setAlignment(Pos.CENTER);
        return button;
    }

    private static Label counterLabel(Font font) {
        var label = new Label();
        label.setFont(font);
        label.setTextFill(Color.RED);
        return label;
    }

    private static Label timerLabel(Font font, GameTimer timer) {
        var label = new Label();
        label.textProperty().bind(timer.timeStringProperty());
        label.setFont(font);
        label.setTextFill(Color.RED);
        label.setAlignment(Pos.CENTER_RIGHT);
        return label;
    }
}
