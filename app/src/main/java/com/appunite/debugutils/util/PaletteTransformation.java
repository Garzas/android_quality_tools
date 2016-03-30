package com.appunite.debugutils.util;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.squareup.picasso.Transformation;

import java.util.Map;
import java.util.WeakHashMap;

public class PaletteTransformation implements Transformation {

    private Palette palette;
    private static final PaletteTransformation INSTANCE = new PaletteTransformation();
    private static final Map<Bitmap, Palette> CACHE = new WeakHashMap<>();

    public static PaletteTransformation getInstance() {
        return INSTANCE;
    }


    @Override
    public Bitmap transform(Bitmap source) {
        palette = Palette.from(source).generate();
        CACHE.put(source, palette);
        return source;
    }

    @Override
    public String key() {
        return "";
    }

    public static Palette getPalette(Bitmap bitmap) {
        return CACHE.get(bitmap);
    }
}