package com.example.travel.util;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class ImageUtils {

    public static void round(Node node,
                             double topLeft,
                             double topRight,
                             double bottomRight,
                             double bottomLeft) {

        double addWidth = 30;
        double addHeight = 30;

        double width = node.getLayoutBounds().getWidth() / 2 + addWidth;
        double height = node.getLayoutBounds().getHeight() / 2 + addHeight;

        Rectangle topLeftRect = new Rectangle(0, 0, width, height);
        Rectangle topRightRect = new Rectangle(width - addWidth * 2, 0, width, height);
        Rectangle bottomRightRect = new Rectangle(width - addWidth * 2, height - addHeight * 2, width, height);
        Rectangle bottomLeftRect = new Rectangle(0, height - addHeight * 2, width, height);

        topLeftRect.setArcWidth(topLeft);
        topLeftRect.setArcHeight(topLeft);
        topRightRect.setArcWidth(topRight);
        topRightRect.setArcHeight(topRight);
        bottomRightRect.setArcWidth(bottomRight);
        bottomRightRect.setArcHeight(bottomRight);
        bottomLeftRect.setArcWidth(bottomLeft);
        bottomLeftRect.setArcHeight(bottomLeft);

        Group clipGroup = new Group(
                topLeftRect,
                topRightRect,
                bottomRightRect,
                bottomLeftRect
        );

        node.setClip(clipGroup);
    }
}
