/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.game;

import org.panteleyev.sapper.MineCountResult;

import java.util.Arrays;
import java.util.Random;

import static org.panteleyev.sapper.game.BoardSize.MAX_HEIGHT;
import static org.panteleyev.sapper.game.BoardSize.MAX_WIDTH;

final class Board {
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final int[] CLEAN_AREA_INIT = new int[]{
            Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE,
            Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE,
            Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE
    };

    private static final int MAX_MINES = 8;

    private final int[] board = new int[MAX_WIDTH * MAX_HEIGHT];

    private int width;
    private int size;
    private int mines;

    private int remainingMines;

    public Board() {
    }

    public void setup(BoardSize boardSize) {
        this.width = boardSize.width();
        this.size = width * boardSize.height();
        this.mines = boardSize.mines();

        remainingMines = mines;
        Arrays.fill(board, Cell.EMPTY);
    }

    public int getSize() {
        return size;
    }

    public int getRemainingMines() {
        return remainingMines;
    }

    /**
     * Seeds board with mines and makes sure center and surrounding area is empty.
     *
     * @param center center of the free area
     */
    void initialize(int center) {
        var cleanArea = getCleanArea(center, width, size);

        for (int i = 0; i < mines; i++) {
            while (true) {
                var x = RANDOM.nextInt(size);
                int value = board[x];

                if (Cell.mine(value) || Arrays.binarySearch(cleanArea, x) >= 0) {
                    continue;
                }

                board[x] = Cell.putMine(value);
                break;
            }
        }
    }

    /**
     * Toggles flag on unopened cell.
     *
     * @param x position
     * @return resulting cell value
     * @throws IllegalArgumentException in case of attempt to toggle flag on opened cell
     */
    int toggleFlag(int x) {
        var value = board[x];
        if (value <= MAX_MINES) {
            throw new IllegalArgumentException("Cannot toggle mine on open cell");
        }
        value = Cell.toggleFlag(value);

        board[x] = value;
        if (Cell.flag(value)) {
            remainingMines--;
        } else {
            remainingMines++;
        }
        return value;
    }

    int getValue(int x) {
        return board[x];
    }

    boolean hasUnexploredCells() {
        for (var x = 0; x < size; x++) {
            if (Cell.empty(board[x])) {
                return true;
            }
        }
        return false;
    }

    MineCountResult countMines(int x) {
        var neighbours = getUnopenedNeighbours(x, width, board, size);
        var mineCount = 0;
        for (var neighbour : neighbours) {
            if (neighbour < 0) {
                break;
            }
            if (Cell.mine(getValue(neighbour))) {
                mineCount++;
            }
        }
        board[x] = mineCount;
        return new MineCountResult(mineCount, neighbours);
    }

    void countRemainingMines() {
        remainingMines = mines - getFlagCount(board, size);
    }

    static int[] getUnopenedNeighbours(int center, int width, int[] board, int size) {
        var result = new int[8 + 1];

        var x = center % width;
        int lowerAdd = x == 0 ? 0 : -1;
        int upperAdd = x == width - 1 ? 0 : 1;

        var index = 0;
        for (int w = -width; w <= width; w += width) {
            for (int add = lowerAdd; add <= upperAdd; add += 1) {
                var pos = center + w + add;
                if (pos == center || pos < 0 || pos >= size) {
                    continue;
                }

                if (board[pos] > MAX_MINES) {
                    result[index++] = pos;
                }
            }
        }
        result[index] = -1;
        return result;
    }

    static int[] getCleanArea(int center, int width, int size) {
        var area = Arrays.copyOf(CLEAN_AREA_INIT, CLEAN_AREA_INIT.length);

        var x = center % width;
        int lowerAdd = x == 0 ? 0 : -1;
        int upperAdd = x == width - 1 ? 0 : 1;

        var index = 0;
        for (int w = -width; w <= width; w += width) {
            for (int add = lowerAdd; add <= upperAdd; add += 1) {
                var pos = center + w + add;
                if (pos >= 0 && pos < size) {
                    area[index++] = pos;
                }
            }
        }
        return area;
    }

    private static int getFlagCount(int[] board, int size) {
        var result = 0;
        for (var x = 0; x < size; x++) {
            result += board[x] & Cell.FLAG_MASK;
        }
        return result >>> 6;
    }
}
