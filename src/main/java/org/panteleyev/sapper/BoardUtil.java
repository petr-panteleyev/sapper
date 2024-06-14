/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

import java.util.ArrayList;
import java.util.List;

final class BoardUtil {
    private BoardUtil() {
    }

    public static final int MAX_MINES = 8;

    public static List<Integer> getSurroundingArea(int center, int width, int size) {
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

    public static List<Integer> getNeighbours(int center, int width, int size, int[] board) {
        var result = new ArrayList<Integer>(8);

        var x = center % width;
        int lowerAdd = x == 0 ? 0 : -1;
        int upperAdd = x == width -1 ? 0: 1;

        for (int w = -width; w <= width; w += width) {
            for (int add = lowerAdd; add <= upperAdd; add += 1) {
                var pos = center + w + add;
                if (pos == center || pos < 0 || pos >= size) {
                    continue;
                }

                if (board[pos] > MAX_MINES) {
                    result.add(pos);
                }
            }
        }

        return result;
    }
}
