package de.scit.imagelink.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Util {

    public static Bitmap getImageBitmap(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static String getFilenameFromKey(String key) {
        String keySuffix = key.substring(key.lastIndexOf("/") + 1);
        return keySuffix;
    }
}
