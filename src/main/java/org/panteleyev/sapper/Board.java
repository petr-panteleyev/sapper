/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

import java.util.Arrays;
import java.util.Random;

import static org.panteleyev.sapper.BoardUtil.getNeighbours;
import static org.panteleyev.sapper.BoardUtil.getSurroundingArea;

public class Board {
    public interface CellChangeCallback {
        void onCellChanged(int x, int newValue);
    }

    public interface GameStatusChangeCallback {
        void onGameStatusChanged(int x, GameStatus newStatus);
    }

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    static final int MAX_MINES = 8;

    static final int FLAG_MASK = 0x2;

    static final int CELL_EMPTY = 1000;
    static final int CELL_MINE = 1001;
    static final int CELL_EMPTY_FLAG = CELL_EMPTY | FLAG_MASK;
    static final int CELL_MINE_FLAG = CELL_MINE | FLAG_MASK;

    private GameStatus gameStatus = GameStatus.INITIAL;
    private final int[] board;
    private int remainingMines;

    private final int width;
    private final int height;
    private final int size;
    private final int mineCount;

    private final CellChangeCallback cellChangeCallback;
    private final GameStatusChangeCallback gameStatusChangeCallback;

    public Board(
            GameType gameType,
            CellChangeCallback cellChangeCallback,
            GameStatusChangeCallback gameStatusChangeCallback
    ) {
        this.cellChangeCallback = cellChangeCallback;
        this.gameStatusChangeCallback = gameStatusChangeCallback;

        width = gameType.getWidth();
        height = gameType.getHeight();
        size = width * height;
        mineCount = gameType.getMines();

        board = new int[size];
        remainingMines = mineCount;

        Arrays.fill(board, CELL_EMPTY);
    }

    public int getSize() {
        return size;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getValue(int x) {
        return board[x];
    }

    public int getRemainingMines() {
        return remainingMines;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    /**
     * Seeds board with mines and makes sure center and surrounding area is empty.
     *
     * @param center center of the free area
     */
    private void initialize(int center) {
        var cleanArea = getSurroundingArea(center, width, size);

        for (int i = 0; i < mineCount; i++) {
            while (true) {
                var x = RANDOM.nextInt(size);
                if (hasMine(x) || cleanArea.contains(x)) {
                    continue;
                }

                board[x] = hasFlag(x) ? CELL_MINE_FLAG : CELL_MINE;
                break;
            }
        }
    }

    private boolean hasMine(int x) {
        var value = board[x];
        return value == CELL_MINE || value == CELL_MINE_FLAG;
    }

    private boolean hasFlag(int x) {
        var value = board[x];
        return value == CELL_EMPTY_FLAG || value == CELL_MINE_FLAG;
    }

    public void toggleFlag(int x) {
        var value = board[x];
        if (value > MAX_MINES) {
            board[x] = value ^ FLAG_MASK;
            cellChangeCallback.onCellChanged(x, board[x]);

            if (hasFlag(x)) {
                remainingMines--;
            } else {
                remainingMines++;
            }

            if (gameStatus != GameStatus.INITIAL) {
                var newStatus = checkForGameStatus();
                if (newStatus != gameStatus) {
                    gameStatus = newStatus;
                    gameStatusChangeCallback.onGameStatusChanged(x, newStatus);
                }
            }
        }
    }

    public void processHit(int x) {
        var value = board[x];

        if (value <= MAX_MINES || value == CELL_MINE_FLAG || value == CELL_EMPTY_FLAG) {
            return;
        }

        if (gameStatus == GameStatus.INITIAL) {
            initialize(x);
            gameStatus = GameStatus.IN_PROGRESS;
            gameStatusChangeCallback.onGameStatusChanged(x, gameStatus);
        }

        if (board[x] == CELL_MINE) {
            gameStatus = GameStatus.FAILURE;
            gameStatusChangeCallback.onGameStatusChanged(x, gameStatus);
            return;
        }

        countMines(x);
        remainingMines = countRemainingMines();

        var newStatus = checkForGameStatus();
        if (newStatus != gameStatus) {
            gameStatus = newStatus;
            gameStatusChangeCallback.onGameStatusChanged(x, newStatus);
        }
    }

    private void countMines(int x) {
        var neighbours = getNeighbours(x, width, size, board);

        var mineCount = (int) neighbours.stream()
                .map(this::getValue)
                .filter(v -> v == CELL_MINE || v == CELL_MINE_FLAG)
                .count();
        board[x] = mineCount;
        cellChangeCallback.onCellChanged(x, mineCount);
        if (mineCount == 0) {
            for (var neighbour : neighbours) {
                countMines(neighbour);
            }
        }
    }

    private int countRemainingMines() {
        int flagCount = 0;

        for (int value : board) {
            if (value == CELL_EMPTY_FLAG || value == CELL_MINE_FLAG) {
                flagCount++;
            }
        }

        return mineCount - flagCount;
    }

    private GameStatus checkForGameStatus() {
        for (int value : board) {
            if (value == CELL_EMPTY || value == CELL_EMPTY_FLAG || value == CELL_MINE) {
                return GameStatus.IN_PROGRESS;
            }
        }
        return GameStatus.SUCCESS;
    }
}
