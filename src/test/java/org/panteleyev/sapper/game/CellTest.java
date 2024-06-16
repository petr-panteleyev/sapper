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

public class CellTest {

    private static List<Arguments> testMineArguments() {
        return List.of(
                Arguments.of(0, false),
                Arguments.of(8, false),
                Arguments.of(0x20, true),
                Arguments.of(0x30, true)
        );
    }

    @ParameterizedTest
    @MethodSource("testMineArguments")
    public void testMine(int value, boolean expected) {
        assertEquals(expected, Cell.mine(value));
    }

    private static List<Arguments> testToggleFlagArguments() {
        return List.of(
                Arguments.of(Cell.EMPTY, Cell.EMPTY_WITH_FLAG),
                Arguments.of(Cell.MINE, Cell.MINE_WITH_FLAG),
                Arguments.of(Cell.EMPTY_WITH_FLAG, Cell.EMPTY),
                Arguments.of(Cell.MINE_WITH_FLAG, Cell.MINE)
        );
    }


    @ParameterizedTest
    @MethodSource("testToggleFlagArguments")
    public void testToggleFlag(int value, int expected) {
        assertEquals(expected, Cell.toggleFlag(value));
    }

    private static List<Arguments> testPutMineArguments() {
        return List.of(
               Arguments.of(Cell.EMPTY, Cell.MINE),
               Arguments.of(Cell.EMPTY_WITH_FLAG, Cell.MINE_WITH_FLAG)
        );
    }

    @ParameterizedTest
    @MethodSource("testPutMineArguments")
    public void testPutMine(int value, int expected) {
        assertEquals(expected, Cell.putMine(value));
    }
}
