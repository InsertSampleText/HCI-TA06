
package com.example.ecocompass.scanner.objectdetector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.ecocompass.scanner.GraphicOverlay;
import com.example.ecocompass.scanner.GraphicOverlay.Graphic;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.DetectedObject.Label;

/** Draw the detected object info in preview. */
public class ObjectGraphic extends Graphic {

  private static final float TEXT_SIZE = 54.0f;
  private static final float STROKE_WIDTH = 4.0f;
  private static final int NUM_COLORS = 10;
  private static final int[][] COLORS =
      new int[][] {
        // {Text color, background color}
        {Color.BLACK, Color.WHITE},
      };
  private final DetectedObject object;
  private final Paint[] boxPaints;
  private final Paint[] textPaints;
  private final Paint[] labelPaints;

  public ObjectGraphic(GraphicOverlay overlay, DetectedObject object) {
    super(overlay);

    this.object = object;

    int numColors = COLORS.length;
    textPaints = new Paint[numColors];
    boxPaints = new Paint[numColors];
    labelPaints = new Paint[numColors];
    for (int i = 0; i < numColors; i++) {
      textPaints[i] = new Paint();
      textPaints[i].setColor(COLORS[i][0]);
      textPaints[i].setTextSize(TEXT_SIZE);

      boxPaints[i] = new Paint();
      boxPaints[i].setColor(COLORS[i][1]);
      boxPaints[i].setStyle(Paint.Style.STROKE);
      boxPaints[i].setStrokeWidth(STROKE_WIDTH);

      labelPaints[i] = new Paint();
      labelPaints[i].setColor(COLORS[i][1]);
      labelPaints[i].setStyle(Paint.Style.FILL);
    }
  }

  @Override
  public void draw(Canvas canvas) {
    // Decide color based on object tracking ID
    int colorID =
            object.getTrackingId() == null ? 0 : Math.abs(object.getTrackingId() % NUM_COLORS);
    float textWidth = 0;
    float lineHeight = TEXT_SIZE + STROKE_WIDTH;
    float yLabelOffset = -lineHeight;

    // Determine if the item is recyclable based on its label
    for (Label label : object.getLabels()) {
      String itemName = label.getText();
      String recyclable;

      if ("container".equalsIgnoreCase(itemName)) {
        recyclable = "No";
      } else if ("box".equalsIgnoreCase(itemName) || "water bottle".equalsIgnoreCase(itemName)) {
        recyclable = "Yes";
      } else {
        recyclable = "Unknown";
      }

      // Calculate the text width to ensure proper spacing
      textWidth = Math.max(textWidth, textPaints[colorID].measureText("Item: " + itemName));
      textWidth = Math.max(textWidth, textPaints[colorID].measureText("Recyclable: " + recyclable));
      yLabelOffset -= 2 * lineHeight;

      // Draws the bounding box
      RectF rect = new RectF(object.getBoundingBox());
      float x0 = translateX(rect.left);
      float x1 = translateX(rect.right);
      rect.left = Math.min(x0, x1);
      rect.right = Math.max(x0, x1);
      rect.top = translateY(rect.top);
      rect.bottom = translateY(rect.bottom);
      canvas.drawRect(rect, boxPaints[colorID]);

      canvas.drawRect(
        rect.left - STROKE_WIDTH,
        rect.top + yLabelOffset,
        rect.left + textWidth + (2 * STROKE_WIDTH),
        rect.top,
        labelPaints[colorID]);
    yLabelOffset += lineHeight;

      // Draw item name and recyclability status
      canvas.drawText("Item: " + itemName, rect.left, rect.top + yLabelOffset, textPaints[colorID]);
      yLabelOffset += lineHeight;

      canvas.drawText("Recyclable: " + recyclable, rect.left, rect.top + yLabelOffset, textPaints[colorID]);
      yLabelOffset += lineHeight;
    }
  }
}
