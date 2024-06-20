/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.score;

import org.panteleyev.sapper.ApplicationFiles;
import org.panteleyev.sapper.game.BoardSize;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static org.panteleyev.sapper.GlobalContext.files;
import static org.panteleyev.sapper.XMLUtils.appendElement;
import static org.panteleyev.sapper.XMLUtils.createDocument;
import static org.panteleyev.sapper.XMLUtils.readDocument;
import static org.panteleyev.sapper.XMLUtils.writeDocument;

public class Scoreboard {
    private static final int TOP_SIZE = 10;

    private static final String XML_ROOT = "Scores";
    private static final String XML_ELEMENT = "Score";

    private static final String ATTR_WIDTH = "width";
    private static final String ATTR_HEIGHT = "height";
    private static final String ATTR_MINES = "mines";
    private static final String ATTR_DATE = "date";
    private static final String ATTR_TIME = "time";

    private final Map<BoardSize, List<GameScore>> scores = new HashMap<>();

    public Scoreboard() {
        scores.put(BoardSize.SMALL, new ArrayList<>());
        scores.put(BoardSize.MEDIUM, new ArrayList<>());
        scores.put(BoardSize.BIG, new ArrayList<>());
    }

    public List<BoardSize> getBoardSizes() {
        return scores.keySet().stream().toList();
    }

    public List<GameScore> getScores(BoardSize boardSize) {
        return scores.computeIfAbsent(boardSize, _ -> new ArrayList<>());
    }

    public boolean add(GameScore score) {
        var current = getScores(score.boardSize());
        if (current.isEmpty()) {
            current.add(score);
            return true;
        }

        current.sort(Comparator.comparing(GameScore::time));
        var newTop = score.time().isBefore(current.getFirst().time());

        if (current.size() < TOP_SIZE) {
            current.add(score);
            return newTop;
        }

        if (score.time().isBefore(current.getLast().time())) {
            current.removeLast();
            current.add(score);
            return newTop;
        }

        return false;
    }

    public void save() {
        files().write(ApplicationFiles.AppFile.SCORES, this::save);
    }

    public void save(OutputStream outputStream) {
        var root = createDocument(XML_ROOT);

        var flatScores = scores.values()
                .stream()
                .flatMap(List::stream)
                .toList();

        for (var score : flatScores) {
            var scoreNode = appendElement(root, XML_ELEMENT);
            scoreNode.setAttribute(ATTR_WIDTH, Integer.toString(score.boardSize().width()));
            scoreNode.setAttribute(ATTR_HEIGHT, Integer.toString(score.boardSize().height()));
            scoreNode.setAttribute(ATTR_MINES, Integer.toString(score.boardSize().mines()));
            scoreNode.setAttribute(ATTR_DATE, Long.toString(score.date().toEpochDay()));
            scoreNode.setAttribute(ATTR_TIME, Long.toString(score.time().toSecondOfDay()));
        }

        writeDocument(root.getOwnerDocument(), outputStream);
    }

    public void load() {
        files().read(ApplicationFiles.AppFile.SCORES, this::load);
    }

    private void load(InputStream inputStream) {
        scores.clear();
        scores.put(BoardSize.SMALL, new ArrayList<>());
        scores.put(BoardSize.MEDIUM, new ArrayList<>());
        scores.put(BoardSize.BIG, new ArrayList<>());

        var root = readDocument(inputStream);
        var nodes = root.getElementsByTagName(XML_ELEMENT);
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element e) {
                var width = parseInt(e.getAttribute(ATTR_WIDTH));
                var height = parseInt(e.getAttribute(ATTR_HEIGHT));
                var mines = parseInt(e.getAttribute(ATTR_MINES));
                var epochDay = parseLong(e.getAttribute(ATTR_DATE));
                var seconds = parseLong(e.getAttribute(ATTR_TIME));

                var score = new GameScore(
                        new BoardSize(width, height, mines),
                        LocalDate.ofEpochDay(epochDay),
                        LocalTime.ofSecondOfDay(seconds)
                );
                var list = getScores(score.boardSize());
                list.add(score);
            }
        }
    }
}
