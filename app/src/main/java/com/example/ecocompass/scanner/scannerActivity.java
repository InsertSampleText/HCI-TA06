package com.example.ecocompass.scanner;

import static java.lang.Math.max;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import com.example.ecocompass.R;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;

import com.example.ecocompass.scanner.objectdetector.ObjectDetectorProcessor;

public class scannerActivity extends AppCompatActivity {
        private static final String TAG = "ScannerActivity";
        private static final String OBJECT_DETECTION = "Object Detection";
        private static final String OBJECT_DETECTION_CUSTOM = "Custom Object Detection";
        private static final String SIZE_SCREEN = "w:screen"; // Match screen width
        private static final String SIZE_ORIGINAL = "w:original"; // Original image size
        private static final String KEY_IMAGE_URI = "com.google.mlkit.vision.demo.KEY_IMAGE_URI";
        private static final String KEY_SELECTED_SIZE = "com.google.mlkit.vision.demo.KEY_SELECTED_SIZE";
        private static final int REQUEST_IMAGE_CAPTURE = 1001;
        private static final int REQUEST_CHOOSE_IMAGE = 1002;
        private ImageView preview;
        private GraphicOverlay graphicOverlay;
        private String selectedMode = OBJECT_DETECTION;
        private String selectedSize = SIZE_SCREEN;
        boolean isLandScape;
        private Uri imageUri;
        private int imageMaxWidth;
        private int imageMaxHeight;
        private VisionImageProcessor imageProcessor;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_scanner);

            // Back Button Functionality
            Button backButton = findViewById(R.id.backArrowScanner);
            backButton.setOnClickListener(v -> {
                getOnBackPressedDispatcher().onBackPressed();
            });

            findViewById(R.id.select_image_button)
                    .setOnClickListener(
                            view -> {
                                PopupMenu popup = new PopupMenu(scannerActivity.this, view);
                                popup.setOnMenuItemClickListener(
                                        menuItem -> {
                                            int itemId = menuItem.getItemId();
                                            if (itemId == R.id.select_images_from_local) {
                                                startChooseImageIntentForResult();
                                                return true;
                                            } else if (itemId == R.id.take_photo_using_camera) {
                                                startCameraIntentForResult();
                                                return true;
                                            }
                                            return false;
                                        });
                                MenuInflater inflater = popup.getMenuInflater();
                                inflater.inflate(R.menu.camera_button_menu, popup.getMenu());
                                popup.show();
                            });
            preview = findViewById(R.id.preview);
            graphicOverlay = findViewById(R.id.graphic_overlay);

            populateFeatureSelector();
            populateSizeSelector();

            isLandScape =
                    (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

            if (savedInstanceState != null) {
                imageUri = savedInstanceState.getParcelable(KEY_IMAGE_URI);
                selectedSize = savedInstanceState.getString(KEY_SELECTED_SIZE);
            }

            View rootView = findViewById(R.id.root);
            rootView
                    .getViewTreeObserver()
                    .addOnGlobalLayoutListener(
                            new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    imageMaxWidth = rootView.getWidth();
                                    imageMaxHeight = rootView.getHeight() - findViewById(R.id.control).getHeight();
                                    if (SIZE_SCREEN.equals(selectedSize)) {
                                        tryReloadAndDetectInImage();
                                    }
                                }
                            });


        }

        @Override
        public void onResume() {
            super.onResume();
            Log.d(TAG, "onResume");
            createImageProcessor();
            tryReloadAndDetectInImage();
        }

        @Override
        public void onPause() {
            super.onPause();
            if (imageProcessor != null) {
                imageProcessor.stop();
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (imageProcessor != null) {
                imageProcessor.stop();
            }
        }

        private void populateFeatureSelector() {
            selectedMode = OBJECT_DETECTION_CUSTOM;
            createImageProcessor();
            tryReloadAndDetectInImage();
        }

        private void populateSizeSelector() {
            selectedSize = SIZE_SCREEN;
            tryReloadAndDetectInImage();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelable(KEY_IMAGE_URI, imageUri);
            outState.putString(KEY_SELECTED_SIZE, selectedSize);
        }

        private void startCameraIntentForResult() {
            // Clean up last time's image
            imageUri = null;
            preview.setImageBitmap(null);

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

        private void startChooseImageIntentForResult() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CHOOSE_IMAGE);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                tryReloadAndDetectInImage();
            } else if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
                // In this case, imageUri is returned by the chooser, save it.
                imageUri = data.getData();
                tryReloadAndDetectInImage();
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

        private void tryReloadAndDetectInImage() {
            Log.d(TAG, "Try reload and detect image");
            try {
                if (imageUri == null) {
                    return;
                }

                if (SIZE_SCREEN.equals(selectedSize) && imageMaxWidth == 0) {
                    // UI layout has not finished yet, will reload once it's ready.
                    return;
                }

                Bitmap imageBitmap = BitmapUtils.getBitmapFromContentUri(getContentResolver(), imageUri);
                if (imageBitmap == null) {
                    return;
                }

                // Clear the overlay first
                graphicOverlay.clear();

                Bitmap resizedBitmap;
                if (selectedSize.equals(SIZE_ORIGINAL)) {
                    resizedBitmap = imageBitmap;
                } else {
                    // Get the dimensions of the image view
                    Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

                    // Determine how much to scale down the image
                    float scaleFactor =
                            max(
                                    (float) imageBitmap.getWidth() / (float) targetedSize.first,
                                    (float) imageBitmap.getHeight() / (float) targetedSize.second);

                    resizedBitmap =
                            Bitmap.createScaledBitmap(
                                    imageBitmap,
                                    (int) (imageBitmap.getWidth() / scaleFactor),
                                    (int) (imageBitmap.getHeight() / scaleFactor),
                                    true);
                }

                preview.setImageBitmap(resizedBitmap);

                if (imageProcessor != null) {
                    graphicOverlay.setImageSourceInfo(
                            resizedBitmap.getWidth(), resizedBitmap.getHeight(), /* isFlipped= */ false);
                    imageProcessor.processBitmap(resizedBitmap, graphicOverlay);
                } else {
                    Log.e(TAG, "Null imageProcessor, please check adb logs for imageProcessor creation error");
                }
            } catch (IOException e) {
                Log.e(TAG, "Error retrieving saved image");
                imageUri = null;
            }
        }

        private Pair<Integer, Integer> getTargetedWidthHeight() {
            int targetWidth;
            int targetHeight;

            targetWidth = imageMaxWidth;
            targetHeight = imageMaxHeight;

            return new Pair<>(targetWidth, targetHeight);
        }

        private void createImageProcessor() {
            if (imageProcessor != null) {
                imageProcessor.stop();
            }
            try {
                Log.i(TAG, "Using Custom Object Detector Processor");
                LocalModel localModel =
                        new LocalModel.Builder()
                                .setAssetFilePath("custom_models/object_labeler.tflite")
                                .build();
                CustomObjectDetectorOptions customObjectDetectorOptions =
                        PreferenceUtils.getCustomObjectDetectorOptionsForStillImage(this, localModel);
                imageProcessor = new ObjectDetectorProcessor(this, customObjectDetectorOptions);
            } catch (Exception e) {
                Log.e(TAG, "Can not create image processor: " + selectedMode, e);
                Toast.makeText(
                                getApplicationContext(),
                                "Can not create image processor: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
