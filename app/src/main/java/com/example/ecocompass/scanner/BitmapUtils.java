package com.example.ecocompass.scanner;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import java.io.IOException;
import java.io.InputStream;


public class BitmapUtils {
  private static final String TAG = "BitmapUtils";

  private static Bitmap rotateBitmap(
      Bitmap bitmap, int rotationDegrees, boolean flipX, boolean flipY) {
    Matrix matrix = new Matrix();

    matrix.postRotate(rotationDegrees);

    matrix.postScale(flipX ? -1.0f : 1.0f, flipY ? -1.0f : 1.0f);
    Bitmap rotatedBitmap =
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    if (rotatedBitmap != bitmap) {
      bitmap.recycle();
    }
    return rotatedBitmap;
  }

  @Nullable
  public static Bitmap getBitmapFromContentUri(ContentResolver contentResolver, Uri imageUri)
      throws IOException {
    Bitmap decodedBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
    if (decodedBitmap == null) {
      return null;
    }
    int orientation = getExifOrientationTag(contentResolver, imageUri);

    int rotationDegrees = 0;
    boolean flipX = false;
    boolean flipY = false;
    switch (orientation) {
      case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
        flipX = true;
        break;
      case ExifInterface.ORIENTATION_ROTATE_90:
        rotationDegrees = 90;
        break;
      case ExifInterface.ORIENTATION_TRANSPOSE:
        rotationDegrees = 90;
        flipX = true;
        break;
      case ExifInterface.ORIENTATION_ROTATE_180:
        rotationDegrees = 180;
        break;
      case ExifInterface.ORIENTATION_FLIP_VERTICAL:
        flipY = true;
        break;
      case ExifInterface.ORIENTATION_ROTATE_270:
        rotationDegrees = -90;
        break;
      case ExifInterface.ORIENTATION_TRANSVERSE:
        rotationDegrees = -90;
        flipX = true;
        break;
      case ExifInterface.ORIENTATION_UNDEFINED:
      case ExifInterface.ORIENTATION_NORMAL:
      default:
    }

    return rotateBitmap(decodedBitmap, rotationDegrees, flipX, flipY);
  }

  private static int getExifOrientationTag(ContentResolver resolver, Uri imageUri) {
    if (!ContentResolver.SCHEME_CONTENT.equals(imageUri.getScheme())
        && !ContentResolver.SCHEME_FILE.equals(imageUri.getScheme())) {
      return 0;
    }

    ExifInterface exif;
    try (InputStream inputStream = resolver.openInputStream(imageUri)) {
      if (inputStream == null) {
        return 0;
      }

      exif = new ExifInterface(inputStream);
    } catch (IOException e) {
      Log.e(TAG, "failed to open file to read rotation meta data: " + imageUri, e);
      return 0;
    }

    return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
  }
}
