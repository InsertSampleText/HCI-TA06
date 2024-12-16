
package com.example.ecocompass.scanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.StringRes;
import com.google.mlkit.common.model.LocalModel;
import com.example.ecocompass.R;
import com.google.mlkit.vision.objects.ObjectDetectorOptionsBase.DetectorMode;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;

public class PreferenceUtils {

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
}
