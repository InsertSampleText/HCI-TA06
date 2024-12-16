package com.example.ecocompass.scanner;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.gms.tasks.Tasks;
import com.google.android.odml.image.BitmapMlImageBuilder;
import com.google.android.odml.image.MlImage;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.common.InputImage;

public abstract class VisionProcessorBase<T> implements VisionImageProcessor {
  private static final String TAG = "VisionProcessorBase";
  private final ActivityManager activityManager;
  private final ScopedExecutor executor;
  private boolean isShutdown;
  private int numRuns = 0;
  private long totalFrameMs = 0;
  private long maxFrameMs = 0;
  private long minFrameMs = Long.MAX_VALUE;
  private long totalDetectorMs = 0;
  private long maxDetectorMs = 0;
  private long minDetectorMs = Long.MAX_VALUE;
  protected VisionProcessorBase(Context context) {
    activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    executor = new ScopedExecutor(TaskExecutors.MAIN_THREAD);
  }

  @Override
  public void processBitmap(Bitmap bitmap, final GraphicOverlay graphicOverlay) {
    long frameStartMs = SystemClock.elapsedRealtime();

    if (isMlImageEnabled(graphicOverlay.getContext())) {
      MlImage mlImage = new BitmapMlImageBuilder(bitmap).build();
      requestDetectInImage(
          mlImage,
          graphicOverlay,
null,
false,
          frameStartMs);
      mlImage.close();

      return;
    }

    requestDetectInImage(
        InputImage.fromBitmap(bitmap, 0),
        graphicOverlay,
 null,
false,
        frameStartMs);
  }

  // -----------------Common processing logic-------------------------------------------------------
  private Task<T> requestDetectInImage(
      final InputImage image,
      final GraphicOverlay graphicOverlay,
      @Nullable final Bitmap originalCameraImage,
      boolean shouldShowFps,
      long frameStartMs) {
    return setUpListener(
        detectInImage(image), graphicOverlay, originalCameraImage, shouldShowFps, frameStartMs);
  }

  private Task<T> requestDetectInImage(
      final MlImage image,
      final GraphicOverlay graphicOverlay,
      @Nullable final Bitmap originalCameraImage,
      boolean shouldShowFps,
      long frameStartMs) {
    return setUpListener(
        detectInImage(image), graphicOverlay, originalCameraImage, shouldShowFps, frameStartMs);
  }

  private Task<T> setUpListener(
      Task<T> task,
      final GraphicOverlay graphicOverlay,
      @Nullable final Bitmap originalCameraImage,
      boolean shouldShowFps,
      long frameStartMs) {
    final long detectorStartMs = SystemClock.elapsedRealtime();
    return task.addOnSuccessListener(
            executor,
            results -> {
              long endMs = SystemClock.elapsedRealtime();
              long currentFrameLatencyMs = endMs - frameStartMs;
              long currentDetectorLatencyMs = endMs - detectorStartMs;
              if (numRuns >= 500) {
                resetLatencyStats();
              }
              numRuns++;
              totalFrameMs += currentFrameLatencyMs;
              maxFrameMs = max(currentFrameLatencyMs, maxFrameMs);
              minFrameMs = min(currentFrameLatencyMs, minFrameMs);
              totalDetectorMs += currentDetectorLatencyMs;
              maxDetectorMs = max(currentDetectorLatencyMs, maxDetectorMs);
              minDetectorMs = min(currentDetectorLatencyMs, minDetectorMs);

              graphicOverlay.clear();
              if (originalCameraImage != null) {
                graphicOverlay.add(new CameraImageGraphic(graphicOverlay, originalCameraImage));
              }
              VisionProcessorBase.this.onSuccess(results, graphicOverlay);

              graphicOverlay.postInvalidate();
            })
        .addOnFailureListener(
            executor,
            e -> {
              graphicOverlay.clear();
              graphicOverlay.postInvalidate();
              String error = "Failed to process. Error: " + e.getLocalizedMessage();
              Toast.makeText(
                      graphicOverlay.getContext(),
                      error + "\nCause: " + e.getCause(),
                      Toast.LENGTH_SHORT)
                  .show();
              Log.d(TAG, error);
              e.printStackTrace();
              VisionProcessorBase.this.onFailure(e);
            });
  }

  @Override
  public void stop() {
    executor.shutdown();
    isShutdown = true;
    resetLatencyStats();
  }

  private void resetLatencyStats() {
    numRuns = 0;
    totalFrameMs = 0;
    maxFrameMs = 0;
    minFrameMs = Long.MAX_VALUE;
    totalDetectorMs = 0;
    maxDetectorMs = 0;
    minDetectorMs = Long.MAX_VALUE;
  }

  protected abstract Task<T> detectInImage(InputImage image);

  protected Task<T> detectInImage(MlImage image) {
    return Tasks.forException(
        new MlKitException(
            "MlImage is currently not demonstrated for this feature",
            MlKitException.INVALID_ARGUMENT));
  }

  protected abstract void onSuccess(@NonNull T results, @NonNull GraphicOverlay graphicOverlay);

  protected abstract void onFailure(@NonNull Exception e);

  protected boolean isMlImageEnabled(Context context) {
    return false;
  }
}
