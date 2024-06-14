/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardUtilTest {

    private static List<Arguments> testGetSurroundingAreaArguments() {
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
    @MethodSource("testGetSurroundingAreaArguments")
    public void testGetSurroundingArea(int center, int width, int size, List<Integer> expected) {
        assertEquals(expected, BoardUtil.getSurroundingArea(center, width, size));
    }
}
