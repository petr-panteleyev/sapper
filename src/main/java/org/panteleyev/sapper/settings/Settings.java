/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper.settings;

import org.panteleyev.sapper.ApplicationFiles;
import org.panteleyev.sapper.game.BoardSize;
import org.w3c.dom.Element;

import java.io.OutputStream;
import java.util.function.Consumer;

import static org.panteleyev.sapper.XMLUtils.appendElement;
import static org.panteleyev.sapper.XMLUtils.createDocument;
import static org.panteleyev.sapper.XMLUtils.getAttribute;
import static org.panteleyev.sapper.XMLUtils.readDocument;
import static org.panteleyev.sapper.XMLUtils.writeDocument;

public final class Settings {
    private static final String ROOT_ELEMENT = "settings";
    private static final String LAST_BOARD_SIZE_ELEMENT = "lastBoardSize";

    private final ApplicationFiles files;

    private BoardSize lastBoardSize = BoardSize.BIG;

    public Settings(ApplicationFiles files) {
        this.files = files;
    }

    public void update(Consumer<Settings> block) {
        block.accept(this);
        files.write(ApplicationFiles.AppFile.SETTINGS, this::save);
    }

    public void setLastBoardSize(BoardSize lastBoardSize) {
        this.lastBoardSize = lastBoardSize;
    }

    public BoardSize getLastBoardSize() {
        return lastBoardSize;
    }

    public void load() {
        files.read(ApplicationFiles.AppFile.SETTINGS, in -> {
            var root = readDocument(in);
            var lastBoardSizeNodes = root.getElementsByTagName(LAST_BOARD_SIZE_ELEMENT);
            if (lastBoardSizeNodes.getLength() != 0) {
                var lastBoardSizeElement = (Element)lastBoardSizeNodes.item(0);
                var width = getAttribute(lastBoardSizeElement, "width", BoardSize.BIG.width());
                var height = getAttribute(lastBoardSizeElement, "height", BoardSize.BIG.height());
                var mines = getAttribute(lastBoardSizeElement, "mines", BoardSize.BIG.mines());
                lastBoardSize = new BoardSize(width, height, mines);
            }
        });
    }

    private void save(OutputStream out) {
        var root = createDocument(ROOT_ELEMENT);
        var e = appendElement(root, LAST_BOARD_SIZE_ELEMENT);
        e.setAttribute("width", Integer.toString(lastBoardSize.width()));
        e.setAttribute("height", Integer.toString(lastBoardSize.height()));
        e.setAttribute("mines", Integer.toString(lastBoardSize.mines()));
        writeDocument(root.getOwnerDocument(), out);
    }
}
