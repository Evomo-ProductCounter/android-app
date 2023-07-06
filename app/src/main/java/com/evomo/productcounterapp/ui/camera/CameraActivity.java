package com.evomo.productcounterapp.ui.camera;

import androidx.annotation.NonNull;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.evomo.productcounterapp.R;
import com.evomo.productcounterapp.data.db.CountObject;
import com.evomo.productcounterapp.databinding.ActivityCameraBinding;
import com.evomo.productcounterapp.utils.DateHelper;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
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
    public static int cameraWidth;
    public static int cameraHeight;
    public static int centerX;
    public static int centerY;

//    String[] machineOptions = {"Machine 1", "Machine 2", "Machine 3", "Machine 4"};
    public static String[] machineOptions;
    String[] parameterOptions = {"In", "Out", "Reject"};
    String[] sizeOptions = {"Small", "Medium", "Large"};

    AutoCompleteTextView machineTextView;
    AutoCompleteTextView parameterTextView;
    AutoCompleteTextView sizeTextView;

    ArrayAdapter<String> machineAdapterItems;
    ArrayAdapter<String> parameterAdapterItems;
    ArrayAdapter<String> sizeAdapterItems;

    private String selectedMachine;
    private String selectedParameter;
    private String selectedSize;

    private boolean startCount = false;

    private CameraViewModel cameraViewModel;
    private CountObject countObject;
    public static String userName;
//    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getPermission();

        cameraBridgeViewBase = binding.cameraView;
        startCamera();

        machineTextView = binding.autocompleteMesin;
        machineTextView.setInputType(InputType.TYPE_NULL);
        machineAdapterItems = new ArrayAdapter<String>(this, R.layout.dropdown_items, machineOptions);
        machineTextView.setAdapter(machineAdapterItems);
        machineTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                selectedMachine = item;
            }
        });

        parameterTextView = binding.autocompleteParameter;
        parameterTextView.setInputType(InputType.TYPE_NULL);
        parameterAdapterItems = new ArrayAdapter<String>(this, R.layout.dropdown_items, parameterOptions);
        parameterTextView.setAdapter(parameterAdapterItems);
        parameterTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                selectedParameter = item;
            }
        });

        sizeTextView = binding.autocompleteUkuran;
        sizeTextView.setInputType(InputType.TYPE_NULL);
        sizeAdapterItems = new ArrayAdapter<String>(this, R.layout.dropdown_items, sizeOptions);
        sizeTextView.setAdapter(sizeAdapterItems);
        sizeTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (item == "Small") {
                    cameraWidth = 200;
                    cameraHeight = 80;
                    centerX = 100;
                    centerY = 35;
                }
                else if (item == "Medium") {
                    cameraWidth = 400;
                    cameraHeight = 150;
                    centerX = 200;
                    centerY = 75;
                } else {
                    cameraWidth = 800;
                    cameraHeight = 300;
                    centerX = 400;
                    centerY = 150;
                }
                selectedSize = item;
                cameraBridgeViewBase.disableView();
                pointsList.clear();
                startCamera();
            }
        });

        CameraViewModelFactory viewModelFactory = new CameraViewModelFactory(getApplication());
        cameraViewModel = viewModelFactory.create(CameraViewModel.class);

        binding.stopCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startCount == true) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this, R.style.LogoutDialog);
                    builder.setTitle(R.string.modal_logout_title)
                            .setMessage(R.string.modal_stop_desc)
                            .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .setPositiveButton(R.string.btn_stop, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    countObject = new CountObject();
                                    countObject.setMachine(selectedMachine);
                                    countObject.setParameter(selectedParameter);
                                    countObject.setCount(counter);
                                    countObject.setDate(DateHelper.INSTANCE.getCurrentDate());
                                    countObject.setOperator(userName);
//
//                                    auth = FirebaseAuth.getInstance();
//                                    FirebaseUser firebaseUser = auth.getCurrentUser();
//                                    if (firebaseUser != null){
//                                        countObject.setOperator(String.valueOf(firebaseUser.getDisplayName()));
//                                    }

                                    cameraViewModel.insert(countObject);
                                    finish();
                                }
                            })
                            .setIcon(R.drawable.ic_baseline_warning_24_yellow);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    positiveButton.setAllCaps(false);
                    positiveButton.setTextColor(getResources().getColor(R.color.red));

                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    negativeButton.setAllCaps(false);
                    negativeButton.setTextColor(getResources().getColor(R.color.black));
                } else {
                    if (selectedMachine == null || selectedSize == null || selectedParameter == null) {
                        Toast.makeText(CameraActivity.this, getResources().getString(R.string.error_start), Toast.LENGTH_SHORT).show();
                    } else {
                        binding.stopCount.setText(getResources().getString(R.string.stop_camera));
                        binding.dropdownMesin.setEnabled(false);
                        binding.dropdownParameter.setEnabled(false);
                        binding.dropdownUkuran.setEnabled(false);
                        startTimer();
                    }
                }
            }
        });
    }

    private void startCamera() {
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
                if (startCount == true) {
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
                    } else if (status != parkingStatus.get(0) && parkingBuffer.get(0) != null) {
                        if (videoFrames - parkingBuffer.get(0) > 0.001) {
                            if (status == false) {
                                counter = counter + 1;

                            }
                            parkingStatus.set(0, status);
                            parkingBuffer.set(0, null);
                        }
                    } else if (status == parkingStatus.get(0) && parkingBuffer.get(0) != null) {
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

                    return outFrame;
                }
                else {
                    // Initialize frame
                    rgbFrame = inputFrame.rgba();
                    outFrame = rgbFrame.clone();

                    // Draw the preview rectangle
                    Imgproc.rectangle(outFrame, roi.tl(), roi.br(), new Scalar(0, 255, 0), 4);

                    return outFrame;
                }
            }
        });

        if (OpenCVLoader.initDebug()) {
            cameraBridgeViewBase.enableView();
        }
    }

    private void startTimer() {
        binding.blurBackground.setVisibility(View.VISIBLE);
        binding.timerCount.setVisibility(View.VISIBLE);
        binding.stopCount.setVisibility(View.INVISIBLE);

        new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
                int sec = (int) ((millisUntilFinished / 1000) % 60);
                binding.timerCount.setText(String.valueOf(sec));
            }

            public void onFinish() {
                binding.blurBackground.setVisibility(View.INVISIBLE);
                binding.timerCount.setVisibility(View.INVISIBLE);
                binding.stopCount.setVisibility(View.VISIBLE);
                startCount = true;
            }
        }.start();
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
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate();
            } else {
                getPermission();
            }
        }
    }
}