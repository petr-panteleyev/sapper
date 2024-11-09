/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.game;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static java.lang.Integer.MAX_VALUE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardTest {
    private static List<Arguments> testGetCleanAreaArguments() {
        return List.of(
                Arguments.of(
                        16, 7, 35,
                        new int[]{8, 9, 10, 15, 16, 17, 22, 23, 24}
                ),
                Arguments.of(
                        0, 7, 35,
                        new int[]{0, 1, 7, 8, MAX_VALUE, MAX_VALUE, MAX_VALUE, MAX_VALUE, MAX_VALUE}
                ),
                Arguments.of(
                        1, 7, 35,
                        new int[]{0, 1, 2, 7, 8, 9, MAX_VALUE, MAX_VALUE, MAX_VALUE}
                ),
                Arguments.of(
                        6, 7, 35,
                        new int[]{5, 6, 12, 13, MAX_VALUE, MAX_VALUE, MAX_VALUE, MAX_VALUE, MAX_VALUE}
                ),
                Arguments.of(
                        28, 7, 35,
                        new int[]{21, 22, 28, 29, MAX_VALUE, MAX_VALUE, MAX_VALUE, MAX_VALUE, MAX_VALUE}
                ),
                Arguments.of(
                        29, 7, 35,
                        new int[]{21, 22, 23, 28, 29, 30, MAX_VALUE, MAX_VALUE, MAX_VALUE}
                ),
                Arguments.of(
                        34, 7, 35,
                        new int[]{26, 27, 33, 34, MAX_VALUE, MAX_VALUE, MAX_VALUE, MAX_VALUE, MAX_VALUE}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testGetCleanAreaArguments")
    public void testGetCleanArea(int center, int width, int size, int[] expected) {
        assertArrayEquals(expected, Board.getCleanArea(center, width, size));
    }

    private static List<Arguments> testGetUnopenedNeighboursArguments() {
        return List.of(
                Arguments.of(
                        0, 5, new int[]{
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.MINE, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                        }, new int[]{1, 5, 6, -1, 0, 0, 0, 0, 0}
                ),
                Arguments.of(
                        0, 5, new int[]{
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, 2, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                        }, new int[]{1, 5, -1, 0, 0, 0, 0, 0, 0}
                ),
                Arguments.of(
                        1, 5, new int[]{
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, 2, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                        }, new int[]{0, 2, 5, 7, -1, 0, 0, 0, 0}
                ),
                Arguments.of(
                        6, 5, new int[]{
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                        }, new int[]{0, 1, 2, 5, 7, 10, 11, 12, -1}
                ),
                Arguments.of(
                        15, 5, new int[]{
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                        }, new int[]{10, 11, 16, -1, 0, 0, 0, 0, 0}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testGetUnopenedNeighboursArguments")
    public void testGetUnopenedNeighbours(int center, int width, int[] board, int[] expected) {
        assertArrayEquals(expected, Board.getUnopenedNeighbours(center, width, board, 20));
    }

    private static List<Arguments> testHasUnexploredCellsArguments() {
        return List.of(
                Arguments.of(
                        new int[] { Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY}, 3, true
                ),
                Arguments.of(
                        new int[] { Cell.MINE, Cell.MINE, Cell.MINE, Cell.EMPTY}, 3, false
                ),
                Arguments.of(
                        new int[] { Cell.MINE, Cell.MINE, Cell.EMPTY_WITH_FLAG, Cell.EMPTY}, 3, true
                ),
                Arguments.of(
                        new int[] { Cell.MINE, Cell.MINE, 3, Cell.EMPTY}, 3, false
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testHasUnexploredCellsArguments")
    public void testHasUnexploredCells(int[] board, int size, boolean expected) {
        assertEquals(expected, Board.hasUnexploredCells(board, size));
    }

    private static List<Arguments> testGetFlagCountArguments() {
        return List.of(
                Arguments.of(
                    new int[] {Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.MINE_WITH_FLAG}, 3, 0
                ),
                Arguments.of(
                    new int[] {Cell.EMPTY, Cell.EMPTY, Cell.MINE_WITH_FLAG, Cell.MINE_WITH_FLAG}, 3, 1
                ),
                Arguments.of(
                    new int[] {Cell.MINE_WITH_FLAG, Cell.EMPTY, Cell.EMPTY_WITH_FLAG, Cell.MINE_WITH_FLAG}, 3, 2
                ),
                Arguments.of(
                    new int[] {Cell.MINE_WITH_FLAG, 2, Cell.EMPTY_WITH_FLAG, Cell.MINE_WITH_FLAG}, 3, 2
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testGetFlagCountArguments")
    public void testGetFlagCount(int[] board, int size, int expected) {
        assertEquals(expected, Board.getFlagCount(board, size));
    }
}
