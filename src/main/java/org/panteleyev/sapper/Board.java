/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Board {
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    static final int MAX_MINES = 8;

    static final int FLAG_MASK = 2;

    static final int CELL_EMPTY = 1000;
    static final int CELL_MINE = 1001;
    static final int CELL_EMPTY_FLAG = CELL_EMPTY | FLAG_MASK;
    static final int CELL_MINE_FLAG = CELL_MINE | FLAG_MASK;

    private GameStatus gameStatus = GameStatus.INITIAL;
    private final BoardSize boardSize;
    private final int[][] board;
    private int remainingMines;

    public Board(BoardSize boardSize) {
        this.boardSize = boardSize;
        this.board = new int[boardSize.width()][boardSize.height()];
        this.remainingMines = boardSize.mineCount();

        for (int x = 0; x < boardSize.width(); x++) {
            Arrays.fill(board[x], CELL_EMPTY);
        }
    }

    /**
     * Seeds board with mines and makes sure center and surrounding area is empty.
     *
     * @param center center of the free area
     */
    private void initialize(Pos center) {
        var cleanArea = getSurroundingArea(center, true);

        for (int i = 0; i < boardSize.mineCount(); i++) {
            while (true) {
                var pos = new Pos(
                        RANDOM.nextInt(boardSize.width()),
                        RANDOM.nextInt(boardSize.height())
                );
                if (hasMine(pos) || cleanArea.contains(pos)) {
                    continue;
                }

                board[pos.x()][pos.y()] = hasFlag(pos)? CELL_MINE_FLAG : CELL_MINE;
                break;
            }
        }
    }

    private List<Pos> getSurroundingArea(Pos center, boolean includeCenter) {
        var area = new ArrayList<Pos>(9);
        for (int x = max(0, center.x() - 1); x <= min(center.x() + 1, boardSize.width() - 1); x++) {
            for (int y = max(0, center.y() - 1); y <= min(center.y() + 1, boardSize.height() - 1); y++) {
                if (!includeCenter && x == center.x() && y == center.y()) {
                    continue;
                }
                area.add(new Pos(x, y));
            }
        }
        return area;
    }

    private boolean hasMine(Pos pos) {
        var value = getValue(pos);
        return value == CELL_MINE || value == CELL_MINE_FLAG;
    }

    private boolean hasFlag(Pos pos) {
        var value = getValue(pos);
        return value == CELL_EMPTY_FLAG || value == CELL_MINE_FLAG;
    }

    public int getWidth() {
        return boardSize.width();
    }

    public int getHeight() {
        return boardSize.height();
    }

    public int getValue(int x, int y) {
        return board[x][y];
    }

    public int getValue(Pos pos) {
        return getValue(pos.x(), pos.y());
    }

    public int getRemainingMines() {
        return remainingMines;
    }

    public HitResult toggleFlag(Pos coords) {
        var value = getValue(coords);
        if (value > MAX_MINES) {
            board[coords.x()][coords.y()] = value ^ FLAG_MASK;

            if (hasFlag(coords)) {
                remainingMines--;
            } else {
                remainingMines++;
            }

            return new HitResult(
                    false,
                    gameStatus == GameStatus.INITIAL? GameStatus.INITIAL : checkForGameStatus(),
                    0
            );
        } else {
            return new HitResult(true);
        }
    }

    public HitResult calculateHit(Pos pos) {
        var value = getValue(pos);

        if (value <= MAX_MINES || value == CELL_MINE_FLAG || value == CELL_EMPTY_FLAG) {
            return new HitResult(true);
        }

        if (gameStatus == GameStatus.INITIAL) {
            initialize(pos);
            gameStatus = GameStatus.IN_PROGRESS;
        }

        if (getValue(pos) == CELL_MINE) {
            return new HitResult(GameStatus.FAILURE);
        }

        var count = countMines(pos);
        remainingMines = countRemainingMines();
        return new HitResult(false, checkForGameStatus(), count);
    }

    private int countMines(Pos pos) {
        var neighbours = getNeighbours(pos);

        var mineCount = (int) neighbours.stream()
                .map(this::getValue)
                .filter(v -> v == CELL_MINE || v == CELL_MINE_FLAG)
                .count();
        board[pos.x()][pos.y()] = mineCount;
        if (mineCount > 0) {
            return mineCount;
        } else {
            for (var neighbour : neighbours) {
                countMines(neighbour);
            }
            return 0;
        }
    }

    private List<Pos> getNeighbours(Pos center) {
        var result = new ArrayList<Pos>(8);

        for (int x = max(0, center.x() - 1); x <= min(center.x() + 1, boardSize.width() - 1); x++) {
            for (int y = max(0, center.y() - 1); y <= min(center.y() + 1, boardSize.height() - 1); y++) {
                if (x == center.x() && y == center.y()) {
                    continue;
                }

                if (getValue(x, y) > MAX_MINES) {
                    result.add(new Pos(x, y));
                }
            }
        }

        return result;
    }

    private int countRemainingMines() {
        int flagCount = 0;

        for (int x = 0; x < boardSize.width(); x++) {
            for (int y = 0; y < boardSize.height(); y++) {
                var value = getValue(x, y);
                if (value == CELL_EMPTY_FLAG || value == CELL_MINE_FLAG) {
                    flagCount++;
                }
            }
        }

        return boardSize.mineCount() - flagCount;
    }

    private GameStatus checkForGameStatus() {
        for (int x = 0; x < boardSize.width(); x++) {
            for (int y = 0; y < boardSize.height(); y++) {
                var value = getValue(x, y);
                if (value == CELL_EMPTY || value == CELL_EMPTY_FLAG || value == CELL_MINE) {
                    return GameStatus.IN_PROGRESS;
                }
            }
        }
        return GameStatus.SUCCESS;
    }
}
