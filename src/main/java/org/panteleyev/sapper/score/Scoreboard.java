/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.score;

import org.panteleyev.sapper.ApplicationFiles;
import org.panteleyev.sapper.game.GameType;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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

    private static final String ATTR_TYPE = "type";
    private static final String ATTR_DATE = "date";
    private static final String ATTR_TIME = "time";

    private final Map<GameType, List<GameScore>> scores = new EnumMap<>(GameType.class);

    public List<GameScore> getScores(GameType gameType) {
        return scores.computeIfAbsent(gameType, _ -> new ArrayList<>());
    }

    public boolean add(GameScore score) {
        var current = getScores(score.gameType());
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
            scoreNode.setAttribute(ATTR_TYPE, score.gameType().name());
            scoreNode.setAttribute(ATTR_DATE, Long.toString(score.date().toEpochDay()));
            scoreNode.setAttribute(ATTR_TIME, Long.toString(score.time().toSecondOfDay()));
        }

        writeDocument(root.getOwnerDocument(), outputStream);
    }

    public void load() {
        files().read(ApplicationFiles.AppFile.SCORES, this::load);
    }

    public void load(InputStream inputStream) {
        scores.clear();

        var root = readDocument(inputStream);
        var nodes = root.getElementsByTagName(XML_ELEMENT);
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element e) {
                var type = GameType.valueOf(e.getAttribute(ATTR_TYPE));
                var epochDay = parseLong(e.getAttribute(ATTR_DATE));
                var seconds = parseLong(e.getAttribute(ATTR_TIME));

                var score = new GameScore(
                        type,
                        LocalDate.ofEpochDay(epochDay),
                        LocalTime.ofSecondOfDay(seconds)
                );
                var list = getScores(score.gameType());
                list.add(score);
            }
        }
    }
}
