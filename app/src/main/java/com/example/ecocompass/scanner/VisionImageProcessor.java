
package com.example.ecocompass.scanner;

import android.graphics.Bitmap;

/** An interface to process the images with different vision detectors and custom image models. */
public interface VisionImageProcessor {

  /** Processes a bitmap image. */
  void processBitmap(Bitmap bitmap, GraphicOverlay graphicOverlay);

  /** Stops the underlying machine learning model and release resources. */
  void stop();
}
