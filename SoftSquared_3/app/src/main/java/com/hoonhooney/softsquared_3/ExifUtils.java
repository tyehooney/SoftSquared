package com.hoonhooney.softsquared_3;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import java.io.IOException;

public class ExifUtils {

    public static int getExifOrientation(String filePath) {
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return 90;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                }
            }
        }

        return 0;
    }

    public static Bitmap getRotatedBitmap(Bitmap bitmap, int degree) {
        if (degree != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            try {
                Bitmap tmpBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                if (bitmap != tmpBitmap) {
                    bitmap.recycle();
                    bitmap = tmpBitmap;
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }
}

