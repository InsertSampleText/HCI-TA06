
package com.example.ecocompass.scanner;

import android.graphics.Bitmap;

import androidx.camera.core.ImageProxy;

import com.google.mlkit.common.MlKitException;

import java.nio.ByteBuffer;

/** An interface to process the images with different vision detectors and custom image models. */
public interface VisionImageProcessor {

  /** Processes a bitmap image. */
  void processBitmap(Bitmap bitmap, GraphicOverlay graphicOverlay);

  /** Stops the underlying machine learning model and release resources. */
  void stop();
}
