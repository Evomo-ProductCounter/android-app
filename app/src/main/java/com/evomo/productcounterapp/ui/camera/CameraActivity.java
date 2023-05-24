package com.evomo.productcounterapp.ui.camera;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.evomo.productcounterapp.R;
import com.evomo.productcounterapp.databinding.ActivityCameraBinding;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CameraActivity extends org.opencv.android.CameraActivity {

    CameraBridgeViewBase cameraBridgeViewBase;
    Mat grayFrame, blurFrame, rgbFrame, outFrame;
    List<MatOfPoint> pointsList = new ArrayList<>();
    ArrayList<Boolean> parkingStatus = new ArrayList<>(1);
    ArrayList<Long> parkingBuffer = new ArrayList<>(1);
    ArrayList<Rect> parkingBoundingRect = new ArrayList<>();
    private ActivityCameraBinding binding;
    private int counter;
    private Rect roi;
    private Rect rectRoi;
    public static String lastCount = "";
    public static int cameraWidth = 0;
    public static int cameraHeight = 0;
    public static int centerX = 0;
    public static int centerY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getPermission();

        cameraBridgeViewBase = binding.cameraView;

        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                rgbFrame = new Mat();
                blurFrame = new Mat();
                grayFrame = new Mat();
                outFrame = new Mat();
                int counter = 0;

                Point center = new Point(width / 2, height / 2);
                roi = new Rect((int) (center.x - centerX), (int) (center.y - centerY), cameraWidth, cameraHeight);
//                roi = new Rect(300, 600, 400, 150);

                // Get the edge points of the ROI rectangle
                Point tl = roi.tl();
                Point tr = new Point(roi.x + roi.width, roi.y);
                Point bl = new Point(roi.x, roi.y + roi.height);
                Point br = roi.br();

                MatOfPoint contour = new MatOfPoint();
                contour.fromArray(tr, tl, bl, br);
                pointsList.add(contour);

                rectRoi = Imgproc.boundingRect(contour);
                parkingBoundingRect.add(rectRoi);
                System.out.print("Parking Bounding Rect: " + parkingBoundingRect);

                parkingStatus.add(false);
                parkingBuffer.add(null);
            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                // Initialize frame
                rgbFrame = inputFrame.rgba();

                long videoFrames = System.currentTimeMillis();
                System.out.println("Video Frames: " + videoFrames);

                Imgproc.GaussianBlur(rgbFrame.clone(), blurFrame, new Size(3, 3), 0);
                Imgproc.cvtColor(blurFrame, grayFrame, Imgproc.COLOR_BGR2GRAY);
                outFrame = rgbFrame.clone();

                Rect rect = parkingBoundingRect.get(0);
                System.out.println("Rect[0]: " + rect);

                Mat roiGray = grayFrame.submat(rect);
                MatOfDouble meandev = new MatOfDouble();
                MatOfDouble stddev = new MatOfDouble();
                Core.meanStdDev(roiGray, meandev, stddev);

                double stdev = stddev.get(0, 0)[0];
//                double std = Core.meanStdDev(roiGray).stddev[0];
                double mean = Core.mean(roiGray).val[0];
                boolean status = (stdev < 22 && mean > 53);

                if (status != parkingStatus.get(0) && parkingBuffer.get(0) == null) {
                    parkingBuffer.set(0, videoFrames);
                }
                else if (status != parkingStatus.get(0) && parkingBuffer.get(0) != null){
                    if (videoFrames - parkingBuffer.get(0) > 0.001) {
                        if (status == false) {
                            counter = counter + 1;

                        }
                        parkingStatus.set(0, status);
                        parkingBuffer.set(0, null);
                    }
                }
                else if (status == parkingStatus.get(0) && parkingBuffer.get(0) != null){
                    parkingBuffer.set(0, null);
                }

                Scalar color;

                if (parkingStatus.get(0)) {
                    color = new Scalar(0, 255, 0);
                } else {
                    color = new Scalar(255, 0, 0);
                }

                Imgproc.drawContours(outFrame, pointsList, -1, color, 4, Imgproc.LINE_8);
                String counterStr = getResources().getString(R.string.counted_object, String.valueOf(counter));
                setText(binding.countText, counterStr);
//                Imgproc.putText(outFrame, counterStr, new Point(10, 40), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 0, 0), 2, Imgproc.LINE_AA);

                return outFrame;
            }
        });

        if (OpenCVLoader.initDebug()) {
            cameraBridgeViewBase.enableView();
        }

        binding.stopCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastCount = "Object Counted: " + counter;
                finish();
            }
        });
    }

    private void setText(final TextView text, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);
    }

    void getPermission() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA
            }, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]!= PackageManager.PERMISSION_GRANTED) {
            getPermission();
        }
    }
}