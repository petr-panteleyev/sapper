/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.sapper;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static java.util.Objects.requireNonNull;

public enum Picture {
    ICON("icon.png"),
    SMILING_FACE("smiling-face.png"),
    SAD_FACE("sad-face.png"),
    LAUGHING_FACE("laughing-face.png"),
    RED_FLAG("red-flag.png"),
    BLACK_FLAG("black-flag.png");

    private final Image image;

    Picture(String fileName) {
        image = new Image(requireNonNull(getClass().getResourceAsStream("/images/" + fileName)));
    }

    public Image getImage() {
        return image;
    }

    public static ImageView imageView(Picture picture, int width, int height) {
        var view = new ImageView(picture.image);
        view.setFitWidth(width);
        view.setFitHeight(height);
        return view;
    }
}
