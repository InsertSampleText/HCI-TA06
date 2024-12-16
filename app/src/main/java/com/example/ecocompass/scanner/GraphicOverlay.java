package com.example.ecocompass.scanner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;

public class GraphicOverlay extends View {
  private final Object lock = new Object();
  private final List<Graphic> graphics = new ArrayList<>();
  private final Matrix transformationMatrix = new Matrix();
  private int imageWidth;
  private int imageHeight;
  private float scaleFactor = 1.0f;
  private float postScaleWidthOffset;
  private float postScaleHeightOffset;
  private boolean isImageFlipped;
  private boolean needUpdateTransformation = true;

  public abstract static class Graphic {
    private GraphicOverlay overlay;

    public Graphic(GraphicOverlay overlay) {
      this.overlay = overlay;
    }

    public abstract void draw(Canvas canvas);

    protected void drawRect(
        Canvas canvas, float left, float top, float right, float bottom, Paint paint) {
      canvas.drawRect(left, top, right, bottom, paint);
    }

    protected void drawText(Canvas canvas, String text, float x, float y, Paint paint) {
      canvas.drawText(text, x, y, paint);
    }

    public float scale(float imagePixel) {
      return imagePixel * overlay.scaleFactor;
    }

    public float translateX(float x) {
      if (overlay.isImageFlipped) {
        return overlay.getWidth() - (scale(x) - overlay.postScaleWidthOffset);
      } else {
        return scale(x) - overlay.postScaleWidthOffset;
      }
    }

    public float translateY(float y) {
      return scale(y) - overlay.postScaleHeightOffset;
    }

    public Matrix getTransformationMatrix() {
      return overlay.transformationMatrix;
    }
  }

  public GraphicOverlay(Context context, AttributeSet attrs) {
    super(context, attrs);
    addOnLayoutChangeListener(
        (view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
            needUpdateTransformation = true);
  }

  public void clear() {
    synchronized (lock) {
      graphics.clear();
    }
    postInvalidate();
  }

  public void add(Graphic graphic) {
    synchronized (lock) {
      graphics.add(graphic);
    }
  }

  public void setImageSourceInfo(int imageWidth, int imageHeight, boolean isFlipped) {
    Preconditions.checkState(imageWidth > 0, "image width must be positive");
    Preconditions.checkState(imageHeight > 0, "image height must be positive");
    synchronized (lock) {
      this.imageWidth = imageWidth;
      this.imageHeight = imageHeight;
      this.isImageFlipped = isFlipped;
      needUpdateTransformation = true;
    }
    postInvalidate();
  }

  private void updateTransformationIfNeeded() {
    if (!needUpdateTransformation || imageWidth <= 0 || imageHeight <= 0) {
      return;
    }
    float viewAspectRatio = (float) getWidth() / getHeight();
    float imageAspectRatio = (float) imageWidth / imageHeight;
    postScaleWidthOffset = 0;
    postScaleHeightOffset = 0;
    if (viewAspectRatio > imageAspectRatio) {
      // The image needs to be vertically cropped to be displayed in this view.
      scaleFactor = (float) getWidth() / imageWidth;
      postScaleHeightOffset = ((float) getWidth() / imageAspectRatio - getHeight()) / 2;
    } else {
      // The image needs to be horizontally cropped to be displayed in this view.
      scaleFactor = (float) getHeight() / imageHeight;
      postScaleWidthOffset = ((float) getHeight() * imageAspectRatio - getWidth()) / 2;
    }

    transformationMatrix.reset();
    transformationMatrix.setScale(scaleFactor, scaleFactor);
    transformationMatrix.postTranslate(-postScaleWidthOffset, -postScaleHeightOffset);

    if (isImageFlipped) {
      transformationMatrix.postScale(-1f, 1f, getWidth() / 2f, getHeight() / 2f);
    }

    needUpdateTransformation = false;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    synchronized (lock) {
      updateTransformationIfNeeded();

      for (Graphic graphic : graphics) {
        graphic.draw(canvas);
      }
    }
  }
}
