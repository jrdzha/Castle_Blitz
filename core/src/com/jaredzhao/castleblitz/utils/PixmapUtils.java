package com.jaredzhao.castleblitz.utils;

import com.badlogic.gdx.graphics.Pixmap;

/**
 * Utility for flipping pixmaps
 */
public class PixmapUtils {

    /**
     * Returns pixmap flipped in the y-direction
     *
     * @param src   Source Pixmap
     * @return      Flipped Pixmap
     */
    public static Pixmap flipPixmap(Pixmap src) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        Pixmap flipped = new Pixmap(width, height, src.getFormat());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                flipped.drawPixel(x, y, src.getPixel(x, height - y - 1));
            }
        }
        return flipped;
    }

}
