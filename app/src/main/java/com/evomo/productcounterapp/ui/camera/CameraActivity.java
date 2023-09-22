package com.evomo.productcounterapp.ui.camera;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.evomo.productcounterapp.R;
import com.evomo.productcounterapp.data.db.CountObject;
import com.evomo.productcounterapp.data.model.DataProduct;
import com.evomo.productcounterapp.data.model.Machine;
import com.evomo.productcounterapp.databinding.ActivityCameraBinding;
import com.evomo.productcounterapp.utils.DateHelper;

//import org.eclipse.paho.android.service.MqttAndroidClient;
import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;
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

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends org.opencv.android.CameraActivity {

    CameraBridgeViewBase cameraBridgeViewBase;
    Mat grayFrame, blurFrame, rgbFrame, outFrame;
    List<MatOfPoint> pointsList = new ArrayList<>();
    ArrayList<Boolean> parkingStatus = new ArrayList<>(1);
    ArrayList<Long> parkingBuffer = new ArrayList<>(1);
    ArrayList<Rect> parkingBoundingRect = new ArrayList<>();
    private ActivityCameraBinding binding;
    private int counter;
    private int tempCounted;
    private Rect roi;
    private Rect rectRoi;
    public static int cameraWidth;
    public static int cameraHeight;
    public static int centerX;
    public static int centerY;

    public static String[] machineOptions;
    public static Machine[] machinesList;
    public static DataProduct[] productsList = {};
    String[] parameterOptions = {"In", "Out", "Reject"};
    String[] sizeOptions = {"Small", "Medium", "Large"};

    AutoCompleteTextView machineTextView;
    AutoCompleteTextView parameterTextView;
    AutoCompleteTextView sizeTextView;
    AutoCompleteTextView productTextView;

    ArrayAdapter<Machine> machineAdapterItems;
    ArrayAdapter<String> parameterAdapterItems;
    ArrayAdapter<String> sizeAdapterItems;
    ArrayAdapter<DataProduct> productAdapterItems;

    private String selectedMachine;
    private String selectedMachineId;
    private String selectedProduct;
    private String selectedProductId;
    private String selectedParameter;
    private String selectedSize;
    private Long speed;
    private boolean status = false;
    private boolean startCount = false;

    private double objectStDev;
    private double objectMean;

    private LocalDateTime lastProductDetectionTime;
    private CameraViewModel cameraViewModel;
    private CountObject countObject;
    public static String userName;
    LocalDateTime start_time;

    private MqttAndroidClient mqttClient;
    public static final String TAG = "AndroidMqttClient";
    private Timer timer;
    private boolean isFirstExecution = true;
    public static String mToken;

    private CustomLifecycleOwner customLifecycleOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        customLifecycleOwner = new CustomLifecycleOwner();

        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getPermission();

        cameraBridgeViewBase = binding.cameraView;
        startCamera();

        binding.statusCircle.setVisibility(View.INVISIBLE);

        machineTextView = binding.autocompleteMesin;
        machineTextView.setInputType(InputType.TYPE_NULL);

        productTextView = binding.autocompleteProduct;
        productTextView.setInputType(InputType.TYPE_NULL);
        binding.dropdownProduct.setEnabled(false);

        CameraViewModelFactory viewModelFactory = new CameraViewModelFactory(getApplication(), mToken);
        cameraViewModel = viewModelFactory.create(CameraViewModel.class);

        machineAdapterItems = new ArrayAdapter<Machine>(this, R.layout.dropdown_items, machinesList);
        machineTextView.setAdapter(machineAdapterItems);
        machineTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Machine item = (Machine) parent.getItemAtPosition(position);
                selectedMachine = item.getName();
                selectedMachineId = item.getId();
                cameraViewModel.getProducts(selectedMachineId);

                cameraViewModel.listProduct.observe(customLifecycleOwner, new Observer<List<DataProduct>>() {
                    @Override
                    public void onChanged(List<DataProduct> dataProducts) {
                        productTextView.setText("");
                        selectedProduct = null;
                        selectedProductId = null;
                        if (dataProducts == null) {
                            binding.dropdownProduct.setEnabled(false);
                            productAdapterItems = new ArrayAdapter<DataProduct>(getApplicationContext(), R.layout.dropdown_items, new DataProduct[0]);
                            productTextView.setAdapter(productAdapterItems);
                        }
                        else {
                            productsList = dataProducts.toArray(new DataProduct[0]);
                            binding.dropdownProduct.setEnabled(true);
                            productAdapterItems = new ArrayAdapter<DataProduct>(getApplicationContext(), R.layout.dropdown_items, productsList);
                            productTextView.setAdapter(productAdapterItems);
                            productTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    DataProduct item = (DataProduct) parent.getItemAtPosition(position);
                                    selectedProduct = item.getProduct().getName();
                                    selectedProductId = item.getProduct().getId();
                                }
                            });
                        }
                    }
                });
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
                                    if (tempCounted != 0) {
                                        sendDataToMqtt();
                                    }
                                    stopSendingData();
                                    disconnect();

                                    countObject = new CountObject();
                                    countObject.setMachine(selectedMachine);
                                    countObject.setMachineId(selectedMachineId);
                                    countObject.setProduct(selectedProduct);
                                    countObject.setProductId(selectedProductId);
                                    countObject.setParameter(selectedParameter);
                                    countObject.setCount(counter);
                                    countObject.setDate(DateHelper.INSTANCE.getCurrentDate());
                                    countObject.setOperator(userName);
                                    countObject.setSpeed(speed);

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
                }
                else {
                    if (selectedMachine == null || selectedSize == null || selectedParameter == null || selectedProduct == null) {
                        Toast.makeText(CameraActivity.this, getResources().getString(R.string.error_start), Toast.LENGTH_SHORT).show();
                    } else {
                        binding.stopCount.setText(getResources().getString(R.string.stop_camera));
                        binding.dropdownMesin.setEnabled(false);
                        binding.dropdownParameter.setEnabled(false);
                        binding.dropdownUkuran.setEnabled(false);
                        binding.dropdownProduct.setEnabled(false);
                        startTimer();
                    }
                }
            }
        });

        binding.chooseObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chooseObject.setVisibility(View.INVISIBLE);
                binding.stopCount.setVisibility(View.VISIBLE);
//                objectChosen = true;
                startCount = true;
                binding.statusCircle.setVisibility(View.VISIBLE);
                setText(binding.countStatus, getResources().getString(R.string.status_idle));
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
                // Initialize frame
                rgbFrame = inputFrame.rgba();

                long videoFrames = System.currentTimeMillis();
                System.out.println("Video Frames: " + videoFrames);

                Imgproc.GaussianBlur(rgbFrame.clone(), blurFrame, new Size(3, 3), 0);
                Imgproc.cvtColor(blurFrame, grayFrame, Imgproc.COLOR_BGR2GRAY);
                outFrame = rgbFrame.clone();

                Mat roiGray = grayFrame.submat(rectRoi);

                MatOfDouble meandev = new MatOfDouble();
                MatOfDouble stddev = new MatOfDouble();
                Core.meanStdDev(roiGray, meandev, stddev);

                double stdev = stddev.get(0, 0)[0];
                double mean = meandev.get(0, 0)[0];

                if (startCount == false) { // set the object mean and stdev
                    objectStDev = stdev;
                    objectMean = mean;
                }

//                boolean area = (stdev < 14 && mean > 150);
                boolean area = (stdev < objectStDev && mean > objectMean);

                Log.d("pixel_stdev", String.valueOf(stdev));
                Log.d("pixel_mean", String.valueOf(mean));
                Log.d("roi_status", String.valueOf(area));
                Log.d("stdev_status", String.valueOf(stdev < 9));
                Log.d("mean_status", String.valueOf(mean > 150));
                if (startCount == true) {
                    if (area != parkingStatus.get(0) && parkingBuffer.get(0) == null) {
                        parkingBuffer.set(0, videoFrames);
                    } else if (area != parkingStatus.get(0) && parkingBuffer.get(0) != null) {
                        if (videoFrames - parkingBuffer.get(0) > 0.001) {
                            if (area == true) {
                                counter = counter + 1;
                                tempCounted = tempCounted + 1; // cek lagi
                                status = true;
                                lastProductDetectionTime = LocalDateTime.now();
                                Log.d("current_time", String.valueOf(lastProductDetectionTime));
                                Duration diff = Duration.between(start_time, lastProductDetectionTime);
                                double elapsed_time = diff.toMillis() / 60000.0;
                                double avg_ppm = Math.round(counter / elapsed_time);
                                speed = (long) avg_ppm;
                                Log.d("test_counter:", String.valueOf(counter));
                                Log.d("test_elapsed_time:", String.valueOf(elapsed_time));
                                Log.d("test_avg_ppm:", String.valueOf(avg_ppm));
                            }
                            parkingStatus.set(0, area);
                            parkingBuffer.set(0, null);
                        }
                    } else if (area == parkingStatus.get(0) && parkingBuffer.get(0) != null) {
                        parkingBuffer.set(0, null);
                    }

                    if (lastProductDetectionTime != null) {
                        Duration time_diff = Duration.between(lastProductDetectionTime, LocalDateTime.now());
                        double timeSinceLastDetection = time_diff.toMillis() / 60000.0;
                        Log.d("last_detection_time:", String.valueOf(timeSinceLastDetection));
                        if (timeSinceLastDetection > 2) { //2 minutes
                            status = false;
                        }
                    }
                    Log.d("status:", String.valueOf(status));

                    if (status) {
                        setText(binding.countStatus, getResources().getString(R.string.status_active));
                        binding.statusCircle.setBackgroundColor(getResources().getColor(R.color.green_700));
                    }
                    else {
                        setText(binding.countStatus, getResources().getString(R.string.status_idle));
                        binding.statusCircle.setBackgroundColor(getResources().getColor(R.color.red));
                    }

                    Scalar color;

                    if (parkingStatus.get(0)) {
                        color = new Scalar(255, 0, 0);
//                        color = new Scalar(0, 255, 0);
                    } else {
//                        color = new Scalar(255, 0, 0);
                        color = new Scalar(0, 255, 0);
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

                binding.chooseObject.setVisibility(View.VISIBLE);
                setText(binding.countText, "Point at the object!");
//                startCount = true;
                start_time = LocalDateTime.now();
                connect(getApplicationContext());
                startSendingData();
                Log.d("start_time", String.valueOf(start_time));
//                binding.statusCircle.setVisibility(View.VISIBLE);
//                setText(binding.countStatus, getResources().getString(R.string.status_idle));
            }
        }.start();
    }

    public void connect(Context context) {
        String serverURI = "tcp://36.92.168.180:9083";

        mqttClient = new MqttAndroidClient(context, serverURI, start_time.toString(), Ack.AUTO_ACK); //client bikin random (bisa timestamp)
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "Connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, "Receive message: " + message.toString() + " from topic: " + topic);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        MqttConnectOptions options = new MqttConnectOptions();
        mqttClient.connect(options, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d(TAG, "Connection success");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d(TAG, "Connection failure");
            }
        });
    }

    public void publish(String topic, String msg, int qos, boolean retained) {
        MqttMessage message = new MqttMessage();
        message.setPayload(
                msg.getBytes()
        );
        message.setQos(qos);
        message.setRetained(retained);
        mqttClient.publish(topic, message, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d(TAG, msg + " published to " + topic);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d(TAG, "Failed to publish " + msg + " to " + topic);
            }
        });
    }

    public void disconnect() {
        mqttClient.disconnect(null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d(TAG, "Disconnected");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d(TAG, "Failed to disconnect");
            }
        });
    }

    public void startSendingData() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isFirstExecution && !status) {
                    sendDataToMqtt();
                } else {
                    isFirstExecution = false;
                }
            }
        }, 0, 120000); // Execute every 2 minutes (120,000 milliseconds)
    }

    public void stopSendingData() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void sendDataToMqtt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.getDefault());
        String waktuKirim = sdf.format(new Date());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", status);
            jsonObject.put("machine_id", selectedMachineId);
            jsonObject.put("product_id", selectedProductId);
            jsonObject.put("total", tempCounted); // total
            jsonObject.put(selectedParameter.toLowerCase(), tempCounted);
            jsonObject.put("speed", speed);
            jsonObject.put("waktu_kirim", waktuKirim);

            if (selectedParameter.toLowerCase() == "out") {
                jsonObject.put("end", true);
            } else {
                jsonObject.put("end", false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonMessage = jsonObject.toString();

        publish("counting", jsonMessage, 1, true);
        tempCounted = 0; //reset temp
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

    @Override
    protected void onStart() {
        super.onStart();
        customLifecycleOwner.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        customLifecycleOwner.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customLifecycleOwner.onDestroy();
    }
}