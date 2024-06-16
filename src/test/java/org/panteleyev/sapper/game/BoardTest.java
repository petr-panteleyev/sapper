/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.game;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardTest {
    private static List<Arguments> testGetCleanAreaArguments() {
        return List.of(
                Arguments.of(
                        16, 7, 35, List.of(8, 9, 10, 15, 16, 17, 22, 23, 24)
                ),
                Arguments.of(
                        0, 7, 35, List.of(0, 1, 7, 8)
                ),
                Arguments.of(
                        1, 7, 35, List.of(0, 1, 2, 7, 8, 9)
                ),
                Arguments.of(
                        6, 7, 35, List.of(5, 6, 12, 13)
                ),
                Arguments.of(
                        28, 7, 35, List.of(21, 22, 28, 29)
                ),
                Arguments.of(
                        29, 7, 35, List.of(21, 22, 23, 28, 29, 30)
                ),
                Arguments.of(
                        34, 7, 35, List.of(26, 27, 33, 34)
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testGetCleanAreaArguments")
    public void testGetCleanArea(int center, int width, int size, List<Integer> expected) {
        assertEquals(expected, Board.getCleanArea(center, width, size));
    }

    private static List<Arguments> testGetUnopenedNeighboursArguments() {
        return List.of(
                Arguments.of(
                        0, 5, new int[]{
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.MINE, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                        }, List.of(1, 5, 6)
                ),
                Arguments.of(
                        0, 5, new int[]{
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, 2, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                        }, List.of(1, 5)
                ),
                Arguments.of(
                        1, 5, new int[]{
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, 2, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                        }, List.of(0, 2, 5, 7)
                ),
                Arguments.of(
                        6, 5, new int[]{
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                        }, List.of(0, 1, 2, 5, 7, 10, 11, 12)
                ),
                Arguments.of(
                        15, 5, new int[]{
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                        }, List.of(10, 11, 16)
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testGetUnopenedNeighboursArguments")
    public void testGetUnopenedNeighbours(int center, int width, int[] board, List<Integer> expected) {
        assertEquals(expected, Board.getUnopenedNeighbours(center, width, board));
    }
}
