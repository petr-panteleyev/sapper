/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.panteleyev.fx.Controller;

import java.util.List;

import static org.panteleyev.fx.MenuFactory.menu;
import static org.panteleyev.fx.MenuFactory.menuItem;
import static org.panteleyev.fx.grid.ColumnConstraintsBuilder.columnConstraints;
import static org.panteleyev.fx.grid.GridBuilder.gridPane;
import static org.panteleyev.fx.grid.GridRowBuilder.gridRow;
import static org.panteleyev.sapper.Board.CELL_EMPTY_FLAG;
import static org.panteleyev.sapper.Board.CELL_MINE;

public class SapperWindowController extends Controller {
    private static final int CELL_SIZE = 48;
    private static final int IMAGE_SIZE = 32;

    private BoardSize boardSize = BoardSize.BIG;
    private Board board;
    private Button[][] buttons;
    private GameStatus gameStatus = GameStatus.INITIAL;

    private final Label remainingMinesLabel = new Label();
    private final Button controlButton = new Button();
    private final Label timerLabel = new Label();
    private final ImageView controlButtonImageView;
    private final GridPane grid = new GridPane();

    private static final CornerRadii BUTTON_CORNER = new CornerRadii(5);
    private static final Insets BUTTON_INSETS = new Insets(1);

    private static final BackgroundFill BASE_FILL = new BackgroundFill(
            Color.LIGHTGRAY, null, null
    );
    private static final Background[] NUMBER_BACKGROUNDS = {
            new Background(BASE_FILL, new BackgroundFill(Color.rgb(0xDE, 0xDE, 0xDC), BUTTON_CORNER, BUTTON_INSETS)),
            new Background(BASE_FILL, new BackgroundFill(Color.rgb(0xDD, 0xFA, 0xC3), BUTTON_CORNER, BUTTON_INSETS)),
            new Background(BASE_FILL, new BackgroundFill(Color.rgb(0xEC, 0xED, 0xBF), BUTTON_CORNER, BUTTON_INSETS)),
            new Background(BASE_FILL, new BackgroundFill(Color.rgb(0xED, 0xDA, 0xB4), BUTTON_CORNER, BUTTON_INSETS)),
            new Background(BASE_FILL, new BackgroundFill(Color.rgb(0xED, 0xC3, 0x8A), BUTTON_CORNER, BUTTON_INSETS)),
            new Background(BASE_FILL, new BackgroundFill(Color.rgb(0xF7, 0xA1, 0xA2), BUTTON_CORNER, BUTTON_INSETS)),
            new Background(BASE_FILL, new BackgroundFill(Color.rgb(0xFE, 0xA7, 0x85), BUTTON_CORNER, BUTTON_INSETS)),
            new Background(BASE_FILL, new BackgroundFill(Color.rgb(0xFF, 0xB6, 0xC1), BUTTON_CORNER, BUTTON_INSETS)),
            new Background(BASE_FILL, new BackgroundFill(Color.rgb(0x8B, 0x45, 0x13), BUTTON_CORNER, BUTTON_INSETS))
    };

    private final GameTimer timer = new GameTimer();

    public SapperWindowController(Stage stage) {
        super(stage, "/main.css");
        stage.setResizable(false);
        stage.getIcons().add(Picture.ICON.getImage());

        timerLabel.textProperty().bind(timer.timeStringProperty());

        remainingMinesLabel.getStyleClass().add("remainingCount");
        timerLabel.getStyleClass().add("remainingCount");

        controlButtonImageView = new ImageView(Picture.SMILING_FACE.getImage());
        controlButtonImageView.setFitWidth(48);
        controlButtonImageView.setFitHeight(48);
        controlButton.setGraphic(controlButtonImageView);
        controlButton.setFocusTraversable(false);
        controlButton.setOnAction(_ -> newGame(boardSize));

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

        newGame(BoardSize.BIG);
    }

    @Override
    public String getTitle() {
        return "Sapper";
    }

    private void newGame(BoardSize boardSize) {
        controlButtonImageView.setImage(Picture.SMILING_FACE.getImage());
        gameStatus = GameStatus.INITIAL;

        this.boardSize = boardSize;
        board = new Board(boardSize);
        initButtons();

        timer.stop();
        timer.reset();


        remainingMinesLabel.setText(Integer.toString(boardSize.mineCount()));
        getStage().sizeToScene();
    }

    private void initButtons() {
        grid.getChildren().clear();
        buttons = new Button[board.getWidth()][board.getHeight()];

        for (var x = 0; x < board.getWidth(); x++) {
            for (var y = 0; y < board.getHeight(); y++) {
                var button = new Button();
                button.setPrefSize(CELL_SIZE, CELL_SIZE);
                button.setMaxSize(CELL_SIZE, CELL_SIZE);
                button.setMinSize(CELL_SIZE, CELL_SIZE);
                button.setFocusTraversable(false);
                button.setUserData(new Pos(x, y));
                button.setOnMouseClicked(this::onButtonPress);

                buttons[x][y] = button;

                grid.add(buttons[x][y], x, y);
            }
        }
    }

    private MenuBar createMainMenu() {
        return new MenuBar(
                menu("File",
                        menuItem("Exit", _ -> onExit())
                ),
                menu("Game",
                        menuItem("Big", _ -> newGame(BoardSize.BIG)),
                        menuItem("Medium", _ -> newGame(BoardSize.MEDIUM)),
                        menuItem("Small", _ -> newGame(BoardSize.SMALL))
                )
        );
    }

    private void renderBoard() {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                renderButton(x, y);
            }
        }
    }

    private void renderSuccess() {
        timer.stop();
        controlButtonImageView.setImage(Picture.LAUGHING_FACE.getImage());
    }

    private void renderFailure(Pos pos) {
        timer.stop();
        controlButtonImageView.setImage(Picture.SAD_FACE.getImage());

        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                var value = board.getValue(x, y);
                var button = buttons[x][y];

                if (x == pos.x() && y == pos.y()) {
                    button.setText(null);
                    button.setGraphic(Picture.imageView(Picture.MINE_RED, IMAGE_SIZE, IMAGE_SIZE));
                } else {
                    if (value == CELL_EMPTY_FLAG) {
                        button.setText(null);
                        button.setGraphic(Picture.imageView(Picture.RED_FLAG, IMAGE_SIZE, IMAGE_SIZE));
                    }

                    if (value == CELL_MINE) {
                        button.setText(null);
                        button.setGraphic(Picture.imageView(Picture.MINE, IMAGE_SIZE, IMAGE_SIZE));
                    }
                }
            }
        }
    }

    private void renderButton(int x, int y) {
        var value = board.getValue(x, y);
        var button = buttons[x][y];

        switch (value) {
            case Board.CELL_EMPTY, Board.CELL_MINE -> {
                button.setText(null);
                button.setGraphic(null);
            }
            case Board.CELL_EMPTY_FLAG, Board.CELL_MINE_FLAG -> {
                button.setText("");
                button.setGraphic(Picture.imageView(Picture.GREEN_FLAG, IMAGE_SIZE, IMAGE_SIZE));
            }
            case 0 -> {
                button.setText(null);
                button.setGraphic(null);
                button.setBackground(NUMBER_BACKGROUNDS[0]);
            }
            default -> {
                button.setText(Integer.toString(value));
                button.setGraphic(null);
                button.setBackground(NUMBER_BACKGROUNDS[value]);
            }
        }

        if (value == 0) {
            button.setDisable(true);
        }
    }

    private void onButtonPress(MouseEvent event) {
        if (gameStatus == GameStatus.FAILURE) {
            event.consume();
            return;
        }

        var button = (Button) event.getSource();

        var coords = (Pos) button.getUserData();

        if (event.getButton() == MouseButton.SECONDARY) {
            var hit = board.toggleFlag(coords);
            if (!hit.ignore()) {
                renderButton(coords.x(), coords.y());
            }
            if (hit.gameStatus() == GameStatus.SUCCESS) {
                renderSuccess();
            }
        } else {
            var hit = board.calculateHit(coords);

            if (hit.ignore()) {
                return;
            }
            if (hit.gameStatus() == GameStatus.FAILURE) {
                gameStatus = GameStatus.FAILURE;
                renderFailure(coords);
                return;
            }

            if (gameStatus == GameStatus.INITIAL) {
                timer.start();
                gameStatus = GameStatus.IN_PROGRESS;
            }


            if (hit.cellStatus() == 0) {
                renderBoard();
            } else {
                renderButton(coords.x(), coords.y());
            }

            if (hit.gameStatus() == GameStatus.SUCCESS) {
                renderSuccess();
                return;
            }
        }

        remainingMinesLabel.setText(Integer.toString(board.getRemainingMines()));
    }

    private void onExit() {
        getStage().fireEvent(new WindowEvent(getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
