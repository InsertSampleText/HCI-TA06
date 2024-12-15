
package com.example.ecocompass.scanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.gms.common.images.Size;
import com.google.common.base.Preconditions;
import com.google.mlkit.common.model.LocalModel;
import com.example.ecocompass.scanner.CameraSource.SizePair;
import com.example.ecocompass.R;
import com.google.mlkit.vision.objects.ObjectDetectorOptionsBase.DetectorMode;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;


/** Utility class to retrieve shared preferences. */
public class PreferenceUtils {

  @Nullable
  public static SizePair getCameraPreviewSizePair(Context context, int cameraId) {
    Preconditions.checkArgument(
        cameraId == CameraSource.CAMERA_FACING_BACK
            || cameraId == CameraSource.CAMERA_FACING_FRONT);
    String previewSizePrefKey;
    String pictureSizePrefKey;
    if (cameraId == CameraSource.CAMERA_FACING_BACK) {
      previewSizePrefKey = context.getString(R.string.pref_key_rear_camera_preview_size);
      pictureSizePrefKey = context.getString(R.string.pref_key_rear_camera_picture_size);
    } else {
      previewSizePrefKey = context.getString(R.string.pref_key_front_camera_preview_size);
      pictureSizePrefKey = context.getString(R.string.pref_key_front_camera_picture_size);
    }

    try {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      return new SizePair(
          Size.parseSize(sharedPreferences.getString(previewSizePrefKey, null)),
          Size.parseSize(sharedPreferences.getString(pictureSizePrefKey, null)));
    } catch (Exception e) {
      return null;
    }
  }

  public static ObjectDetectorOptions getObjectDetectorOptionsForStillImage(Context context) {
    return getObjectDetectorOptions(
        context,
        R.string.pref_key_still_image_object_detector_enable_multiple_objects,
        R.string.pref_key_still_image_object_detector_enable_classification,
        ObjectDetectorOptions.SINGLE_IMAGE_MODE);
  }

  private static ObjectDetectorOptions getObjectDetectorOptions(
      Context context,
      @StringRes int prefKeyForMultipleObjects,
      @StringRes int prefKeyForClassification,
      @DetectorMode int mode) {

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    boolean enableMultipleObjects =
        sharedPreferences.getBoolean(context.getString(prefKeyForMultipleObjects), false);
    boolean enableClassification =
        sharedPreferences.getBoolean(context.getString(prefKeyForClassification), true);

    ObjectDetectorOptions.Builder builder =
        new ObjectDetectorOptions.Builder().setDetectorMode(mode);
    if (enableMultipleObjects) {
      builder.enableMultipleObjects();
    }
    if (enableClassification) {
      builder.enableClassification();
    }
    return builder.build();
  }

  public static CustomObjectDetectorOptions getCustomObjectDetectorOptionsForStillImage(
      Context context, LocalModel localModel) {
    return getCustomObjectDetectorOptions(
        context,
        localModel,
        R.string.pref_key_still_image_object_detector_enable_multiple_objects,
        R.string.pref_key_still_image_object_detector_enable_classification,
        CustomObjectDetectorOptions.SINGLE_IMAGE_MODE);
  }

  private static CustomObjectDetectorOptions getCustomObjectDetectorOptions(
      Context context,
      LocalModel localModel,
      @StringRes int prefKeyForMultipleObjects,
      @StringRes int prefKeyForClassification,
      @DetectorMode int mode) {

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    boolean enableMultipleObjects =
        sharedPreferences.getBoolean(context.getString(prefKeyForMultipleObjects), false);
    boolean enableClassification =
        sharedPreferences.getBoolean(context.getString(prefKeyForClassification), true);

    CustomObjectDetectorOptions.Builder builder =
        new CustomObjectDetectorOptions.Builder(localModel).setDetectorMode(mode);
    if (enableMultipleObjects) {
      builder.enableMultipleObjects();
    }
    if (enableClassification) {
      builder.enableClassification().setMaxPerObjectLabelCount(1);
    }
    return builder.build();
  }


  public static boolean isCameraLiveViewportEnabled(Context context) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    String prefKey = context.getString(R.string.pref_key_camera_live_viewport);
    return sharedPreferences.getBoolean(prefKey, false);
  }
}
