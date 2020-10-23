package com.enecuum.app.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;

public class IdenticonGenerator {
    public static int height = 5;
    public static int width = 5;

    public static Bitmap generate(String id) {

        byte[] hash = id.getBytes();

        Bitmap identicon = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        identicon.setHasAlpha(true);

        // get byte values as unsigned ints
        int r = hash[hash.length - 3] & 255;
        int g = hash[hash.length - 2] & 255;
        int b = hash[hash.length - 1] & 255;

        int background = Color.TRANSPARENT;
        int foreground = Color.argb(255, r, g, b);

        for (int i = 0; i < 15; i++) {
            String charAt = String.valueOf(id.charAt(i));
            int color = (Integer.parseInt(charAt, 16) % 2 == 1) ? background : foreground;
            if (i < 5) {
                identicon.setPixel(2, i, color);
            } else if (i < 10) {
                identicon.setPixel(1, i - 5, color);
                identicon.setPixel(3, i - 5, color);
            } else {
                identicon.setPixel(0, i - 10, color);
                identicon.setPixel(4, i - 10, color);
            }
        }

        //scale image by 2 to add border
        Bitmap bmpWithBorder = Bitmap.createBitmap(14, 14, identicon.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(background);
        identicon = Bitmap.createScaledBitmap(identicon, 10, 10, false);
        canvas.drawBitmap(identicon, 2, 2, null);

        return bmpWithBorder;
    }
}