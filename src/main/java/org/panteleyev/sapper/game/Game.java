/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.game;

public final class Game {
    public interface CellChangeCallback {
        void onCellChanged(int x, int newValue);
    }

    public interface GameStatusChangeCallback {
        void onGameStatusChanged(int x, GameStatus newStatus);
    }

    private GameStatus gameStatus = GameStatus.INITIAL;
    private final Board board;

    private final CellChangeCallback cellChangeCallback;
    private final GameStatusChangeCallback gameStatusChangeCallback;

    public Game(
            GameType gameType,
            CellChangeCallback cellChangeCallback,
            GameStatusChangeCallback gameStatusChangeCallback
    ) {
        this.cellChangeCallback = cellChangeCallback;
        this.gameStatusChangeCallback = gameStatusChangeCallback;

        board = new Board(gameType.getWidth(), gameType.getHeight(), gameType.getMines());
    }

    public int getSize() {
        return board.getWidth() * board.getHeight();
    }

    public int getWidth() {
        return board.getWidth();
    }

    public int getHeight() {
        return board.getHeight();
    }

    public int getValue(int x) {
        return board.getValue(x);
    }

    public int getRemainingMines() {
        return board.getRemainingMines();
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void toggleFlag(int x) {
        var newValue = board.toggleFlag(x);

        cellChangeCallback.onCellChanged(x, newValue);

        if (gameStatus != GameStatus.INITIAL) {
            var newStatus = checkForGameStatus();
            if (newStatus != gameStatus) {
                gameStatus = newStatus;
                gameStatusChangeCallback.onGameStatusChanged(x, newStatus);
            }
        }
    }

    public void processHit(int x) {
        var value = board.getValue(x);

        if (Cell.isExplored(value) || Cell.flag(value)) {
            return;
        }

        if (gameStatus == GameStatus.INITIAL) {
            board.initialize(x);
            gameStatus = GameStatus.IN_PROGRESS;
            gameStatusChangeCallback.onGameStatusChanged(x, gameStatus);
        }

        if (Cell.mineNoFlag(value)) {
            gameStatus = GameStatus.FAILURE;
            gameStatusChangeCallback.onGameStatusChanged(x, gameStatus);
            return;
        }

        countMines(x);
        board.countRemainingMines();

        var newStatus = checkForGameStatus();
        if (newStatus != gameStatus) {
            gameStatus = newStatus;
            gameStatusChangeCallback.onGameStatusChanged(x, newStatus);
        }
    }

    private void countMines(int x) {
        var result = board.countMines(x);
        cellChangeCallback.onCellChanged(x, result.value());
        if (result.value() == 0) {
            for (var neighbour : result.neighbours()) {
                if (Cell.empty(board.getValue(neighbour))) {
                    countMines(neighbour);
                }
            }
        }
    }

    private GameStatus checkForGameStatus() {
        return board.hasUnexploredCells() ? GameStatus.IN_PROGRESS : GameStatus.SUCCESS;
    }
}
