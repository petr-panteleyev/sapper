/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.game;

import org.panteleyev.sapper.MineCountResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

final class Board {
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final int MAX_MINES = 8;

    private final int width;
    private final int height;
    private final int mines;
    private final int[] board;

    private int remainingMines;

    public Board(int width, int height, int mines) {
        this.width = width;
        this.height = height;
        this.mines = mines;

        this.board = new int[width * height];

        remainingMines = mines;
        Arrays.fill(board, Cell.EMPTY);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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
        var size = board.length;
        var cleanArea = getCleanArea(center, width, size);

        for (int i = 0; i < mines; i++) {
            while (true) {
                var x = RANDOM.nextInt(size);
                int value = board[x];

                if (Cell.mine(value) || cleanArea.contains(x)) {
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
        for (int value : board) {
            if (Cell.empty(value) || Cell.mineNoFlag(value)) {
                return true;
            }
        }
        return false;
    }

    MineCountResult countMines(int x) {
        var neighbours = getUnopenedNeighbours(x, width, board);
        var mineCount = (int) neighbours.stream()
                .map(this::getValue)
                .filter(Cell::mine)
                .count();
        board[x] = mineCount;
        return new MineCountResult(mineCount, neighbours);
    }

    void countRemainingMines() {
        remainingMines = mines - getFlagCount(board);
    }

    static List<Integer> getUnopenedNeighbours(int center, int width, int[] board) {
        var result = new ArrayList<Integer>(8);

        var x = center % width;
        int lowerAdd = x == 0 ? 0 : -1;
        int upperAdd = x == width -1 ? 0: 1;

        for (int w = -width; w <= width; w += width) {
            for (int add = lowerAdd; add <= upperAdd; add += 1) {
                var pos = center + w + add;
                if (pos == center || pos < 0 || pos >= board.length) {
                    continue;
                }

                if (board[pos] > MAX_MINES) {
                    result.add(pos);
                }
            }
        }

        return result;
    }

    static List<Integer> getCleanArea(int center, int width, int size) {
        var area = new ArrayList<Integer>(9);

        var x = center % width;
        int lowerAdd = x == 0 ? 0 : -1;
        int upperAdd = x == width -1 ? 0: 1;

        for (int w = -width; w <= width; w += width) {
            for (int add = lowerAdd; add <= upperAdd; add += 1) {
                var pos = center + w + add;
                if (pos >= 0 && pos < size) {
                    area.add(pos);
                }
            }
        }
        return area;
    }

    private static int getFlagCount(int[] board) {
        var result = 0;
        for (var value: board) {
            result += value & Cell.FLAG_MASK;
        }
        return result >>> 6;
    }
}
