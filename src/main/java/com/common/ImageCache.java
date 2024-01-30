package com.common;
import ij.ImagePlus;

import java.util.HashMap;
import java.util.Map;

public class ImageCache {

    private static final Map<String, ImagePlus> cache = new HashMap<>();

    public static ImagePlus getImage(String url) {
        return cache.get(url);
    }

    public static void cacheImage(String url, ImagePlus imagePlus) {
        cache.put(url, imagePlus);
    }

    public static void clearCache() {
        cache.clear();
    }

    // Other methods to manage the cache as needed
}
