package com.example.ecocompass.scanner;

import android.content.Context;

import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.objects.ObjectDetectorOptionsBase.DetectorMode;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;

public class PreferenceUtils {

  public static CustomObjectDetectorOptions getCustomObjectDetectorOptionsForStillImage(
      Context context, LocalModel localModel) {
    return getCustomObjectDetectorOptions(
        context,
        localModel,
        CustomObjectDetectorOptions.SINGLE_IMAGE_MODE);
  }

  private static CustomObjectDetectorOptions getCustomObjectDetectorOptions(
      Context context,
      LocalModel localModel,
      @DetectorMode int mode) {


    boolean enableMultipleObjects = false;
    boolean enableClassification = true;

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
