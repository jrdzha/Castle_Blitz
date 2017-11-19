package com.jaredzhao.castleblitz.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.ByteBuffer;

public class BlurUtils {
    /**
     * <p>
     * Blurs the source pixels into the destination pixels. The force of the
     * blur is specified by the radius which must be greater than 0.
     * </p>
     * <p>
     * The source and destination pixels arrays are expected to be in the RGBA
     * format.
     * </p>
     *
     * @param srcPixels
     *            the source pixels
     * @param dstPixels
     *            the destination pixels
     * @param width
     *            the width of the source picture
     * @param height
     *            the height of the source picture
     * @param radius
     *            the radius of the blur effect
     * @author Romain Guy <romain.guy@mac.com>
     */
    public static void blurPass(int[] srcPixels, int[] dstPixels, int width,
                                int height, int radius) {
        final int windowSize = radius * 2 + 1;
        final int radiusPlusOne = radius + 1;

        int sumRed;
        int sumGreen;
        int sumBlue;
        int sumAlpha;

        int srcIndex = 0;
        int dstIndex;
        int pixel;

        int[] sumLookupTable = new int[256 * windowSize];
        for (int i = 0; i < sumLookupTable.length; i++) {
            sumLookupTable[i] = i / windowSize;
        }

        int[] indexLookupTable = new int[radiusPlusOne];
        if (radius < width) {
            for (int i = 0; i < indexLookupTable.length; i++) {
                indexLookupTable[i] = i;
            }
        } else {
            for (int i = 0; i < width; i++) {
                indexLookupTable[i] = i;
            }
            for (int i = width; i < indexLookupTable.length; i++) {
                indexLookupTable[i] = width - 1;
            }
        }

        for (int y = 0; y < height; y++) {
            sumAlpha = sumRed = sumGreen = sumBlue = 0;
            dstIndex = y;

            pixel = srcPixels[srcIndex];
            sumRed += radiusPlusOne * ((pixel >> 24) & 0xFF);
            sumGreen += radiusPlusOne * ((pixel >> 16) & 0xFF);
            sumBlue += radiusPlusOne * ((pixel >> 8) & 0xFF);
            sumAlpha += radiusPlusOne * (pixel & 0xFF);

            for (int i = 1; i <= radius; i++) {
                pixel = srcPixels[srcIndex + indexLookupTable[i]];
                sumRed += (pixel >> 24) & 0xFF;
                sumGreen += (pixel >> 16) & 0xFF;
                sumBlue += (pixel >> 8) & 0xFF;
                sumAlpha += pixel & 0xFF;
            }

            for (int x = 0; x < width; x++) {
                dstPixels[dstIndex] = sumLookupTable[sumRed] << 24
                        | sumLookupTable[sumGreen] << 16
                        | sumLookupTable[sumBlue] << 8
                        | sumLookupTable[sumAlpha];
                dstIndex += height;

                int nextPixelIndex = x + radiusPlusOne;
                if (nextPixelIndex >= width) {
                    nextPixelIndex = width - 1;
                }

                int previousPixelIndex = x - radius;
                if (previousPixelIndex < 0) {
                    previousPixelIndex = 0;
                }

                int nextPixel = srcPixels[srcIndex + nextPixelIndex];
                int previousPixel = srcPixels[srcIndex + previousPixelIndex];

                sumRed += (nextPixel >> 24) & 0xFF;
                sumRed -= (previousPixel >> 24) & 0xFF;

                sumGreen += (nextPixel >> 16) & 0xFF;
                sumGreen -= (previousPixel >> 16) & 0xFF;

                sumBlue += (nextPixel >> 8) & 0xFF;
                sumBlue -= (previousPixel >> 8) & 0xFF;

                sumAlpha += nextPixel & 0xFF;
                sumAlpha -= previousPixel & 0xFF;
            }

            srcIndex += width;
        }
    }

    /**
     * Blurs (in both horizontal and vertical directions) the specified RGBA
     * image with the given radius and iterations.
     *
     * @param inputRGBA
     *            the image pixels, in RGBA format
     * @param width
     *            the width of the image in pixels
     * @param height
     *            the height of the image in pixels
     * @param radius
     *            the radius of the blur effect
     * @param iterations
     *            the number of times to perform the blur; i.e. to increase
     *            quality
     * @return the blurred pixels
     */
    public static int[] blur(int[] inputRGBA, int width, int height,
                             int radius, int iterations) {
        int[] srcPixels = new int[width * height];
        int[] dstPixels = new int[width * height];

        // copy input into srcPixels
        System.arraycopy(inputRGBA, 0, srcPixels, 0, srcPixels.length);

        for (int i = 0; i < iterations; i++) {
            // horizontal pass
            blurPass(srcPixels, dstPixels, width, height, radius);
            // vertical pass
            blurPass(dstPixels, srcPixels, height, width, radius);
        }

        // the result is now stored in srcPixels due to the 2nd pass
        return srcPixels;
    }

    /**
     * Convenience method to blur using ByteBuffers instead of arrays.
     * Note that this requires unnecessary copies of data and is only
     * for convenience; a proper solution would be
     * to re-write the blur algorithm using a ByteBuffer.
     *
     * @param inputRGBA
     * @param width
     * @param height
     * @param radius
     * @param iterations
     * @return
     */
    public static ByteBuffer blur(ByteBuffer inputRGBA, int width, int height, int radius, int iterations) {
        if (inputRGBA.limit() != (width * height * 4))
            throw new IllegalArgumentException("inputRGBA must be in RGBA format");
        int[] pixels = pack(inputRGBA);
        int[] out = blur(pixels, width, height, radius, iterations);
        return unpack(out);
    }

    /**
     * Converts an RGBA byte buffer into an array of RGBA packed ints.
     *
     * @param rgba
     * @return
     */
    public static int[] pack(ByteBuffer rgba) {
        int[] pixels = new int[rgba.limit() / 4];
        for (int i = 0; i < pixels.length; i++) {
            int r = rgba.get() & 0xFF;
            int g = rgba.get() & 0xFF;
            int b = rgba.get() & 0xFF;
            int a = rgba.get() & 0xFF;
            pixels[i] = (r << 24) | (g << 16) | (b << 8) | a;
        }
        return pixels;
    }

    /**
     * Unpacks the RGBA pixels array into a ByteBuffer with red, green, blue,
     * and alpha bytes in order; it is then flipped to "read mode" before being
     * returned.
     *
     * @param pixels
     *            the pixels to use
     * @return the new byte buffer using RGBA bytes
     */
    public static ByteBuffer unpack(int[] pixels) {
        ByteBuffer buf = BufferUtils.newByteBuffer(pixels.length * 4);
        for (int src = 0; src < pixels.length; src++) {
            int value = pixels[src];
            buf.put((byte) ((value & 0xff000000) >>> 24))
                    .put((byte) ((value & 0x00ff0000) >>> 16))
                    .put((byte) ((value & 0x0000ff00) >>> 8))
                    .put((byte) ((value & 0x000000ff)));
        }
        buf.flip();
        return buf;
    }

    /**
     * A convenience method to apply the blur to the entire Pixmap.
     *
     * @param pixmap
     *            the pixmap to blur
     * @param radius
     *            the radius of the blur effect
     * @param iterations
     *            the number of iterations to blur
     * @param disposePixmap
     *            whether to dispose the given pixmap after blurring
     * @return a new Pixmap containing the blurred image
     */
    public static Pixmap blur(Pixmap pixmap, int radius, int iterations,
                              boolean disposePixmap) {
        return blur(pixmap, 0, 0, pixmap.getWidth(), pixmap.getHeight(), 0, 0,
                pixmap.getWidth(), pixmap.getHeight(), radius, iterations,
                disposePixmap);
    }

    /**
     * Blurs the specified pixmap with the given source and destination regions.
     *
     * The pixmap does not need to be in RGBA8888 format, however, it is
     * recommended for better performance.
     *
     * A new pixmap will be returned containing the blurred image. The old
     * pixmap will only be disposed of if <tt>disposePixmap</tt> returns true.
     *
     * @param pixmap
     *            the pixmap to blur
     * @param srcx
     *            the x of the pixmap region to blur
     * @param srcy
     *            the y of the pixmap region to blur
     * @param srcwidth
     *            the width of the pixmap region to blur
     * @param srcheight
     *            the height of the pixmap region to blur
     * @param dstx
     *            the destination x to place the blurred image on the resulting
     *            pixmap
     * @param dsty
     *            the destination y to place the blurred image on the resulting
     *            pixmap
     * @param dstwidth
     *            the desired width of the resulting pixmap
     * @param dstheight
     *            the desired height of the resulting pixmap
     * @param radius
     *            the radius of the blur effect, in pixels
     * @param iterations
     *            the number of iterations to apply the blur
     * @param disposePixmap
     *            whether to dispose the specified pixmap after applying the
     *            blur
     * @return a new RGBA8888 Pixmap containing the blurred image
     */
    public static Pixmap blur(Pixmap pixmap, int srcx, int srcy, int srcwidth,
                              int srcheight, int dstx, int dsty, int dstwidth, int dstheight,
                              int radius, int iterations, boolean disposePixmap) {
        boolean srcEq = srcx == 0 && srcy == 0 && srcwidth == pixmap.getWidth()
                && srcheight == pixmap.getHeight();
        boolean dstEq = dstx == 0 && dsty == 0 && dstwidth == pixmap.getWidth()
                && dstheight == pixmap.getHeight();

        // we may need to re-draw the pixmap if a different region or format is
        // passed
        if (pixmap.getFormat() != Format.RGBA8888 || !srcEq || !dstEq) {
            Pixmap tmp = new Pixmap(dstwidth, dstheight, Format.RGBA8888);
            tmp.drawPixmap(pixmap, srcx, srcy, srcwidth, srcheight, dstx, dsty,
                    dstwidth, dstheight);
            if (disposePixmap) {
                pixmap.dispose(); // discard old pixmap
                disposePixmap = false;
            }
            pixmap = tmp;
        }

        // blur the pixmap
        ByteBuffer blurred = BlurUtils.blur(pixmap.getPixels(), dstwidth,
                dstheight, radius, iterations);

        Pixmap newPixmap = new Pixmap(dstwidth, dstheight, Format.RGBA8888);
        ByteBuffer newRGBA = newPixmap.getPixels();
        newRGBA.clear();
        newRGBA.put(blurred);
        newRGBA.flip();

        if (disposePixmap)
            pixmap.dispose();
        return newPixmap;
    }
}