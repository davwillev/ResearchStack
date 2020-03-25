package org.researchstack.backbone.ui.step.layout;

import java.lang.Math;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.RelativeLayout;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.RangeOfMotionResult;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.RangeOfMotionStep;
import org.researchstack.backbone.step.active.recorder.DeviceMotionRecorder;
import org.researchstack.backbone.utils.MathUtils;

import static java.lang.Double.NaN;

/**
 * Created by David Evans, Simon Hartley, Laurence Hurst, David Jiménez-Grande, 2019.
 *
 * The behaviour of the RangeOfMotionStepLayout appears to be the same as the TouchAnywhereStepLayout,
 * but it captures device sensor data and automatically calculates a range of useful kinematic measures
 * from the motion task, once the screen has been tapped to finish the step.
 *
 */

public class RangeOfMotionStepLayout extends ActiveStepLayout {

    protected RangeOfMotionStep rangeOfMotionStep;
    protected RangeOfMotionResult rangeOfMotionResult;
    protected BroadcastReceiver deviceMotionReceiver;;
    protected RelativeLayout layout;
    //protected SensorEvent sensorEvent; // SensorEvent.timestamp is preferred to System.nanoTime()
    //SensorEventListener sensorEventListener;
    protected SensorManager sensorManager;

    private boolean firstOrientationCaptured = false;
    private int orientation;
    private int initialOrientation;
    private float[] updatedDeviceAttitudeAsQuaternion = new float[4];
    private float[] startAttitude = new float[4];
    private float[] finishAttitude = new float[4];
    private double duration;
    private double timestamp;
    private double minimumAngle, maximumAngle;
    private double maximumAx, maximumAy, maximumAz, maximumAr;
    private double minimumAx, minimumAy, minimumAz;
    private double maximumJx, maximumJy, maximumJz, maximumJr;
    private double minimumJx, minimumJy, minimumJz;
    private double meanAccel, SDAccel;
    private double meanJerk, SDJerk;
    private double timeNormIntegratedJerk;

    private int accel_count;
    private boolean firstAttitudeCaptured = false;
    private float[] prevAccel = new float [3];
    private float[] newAccel = new float [3];
    private double meanAr;
    private double meanJr;
    private double varianceAr;
    private double varianceJr;
    private double standardDevAr;
    private double standardDevJr;
    private double resultantJerk;
    private double integratedJerk;
    private double sumDeltaTime;
    private double minAngle, maxAngle;
    private double firstJerk, lastJerk;
    private double sumOdd, sumEven, h;
    private double maxAx, maxAy, maxAz, maxAr;
    private double minAx, minAy, minAz;
    private double maxJx, maxJy, maxJz, maxJr;
    private double minJx, minJy, minJz;
    private double prevMa, newMa, prevSa, newSa;
    private double prevMj, newMj, prevSj, newSj;
    private double first_time, prev_time, new_time, total_time;

    public static final int ORIENTATION_UNDETECTABLE = -2;
    public static final int ORIENTATION_UNSPECIFIED = -1;
    public static final int ORIENTATION_LANDSCAPE = 0;
    public static final int ORIENTATION_PORTRAIT = 1;
    public static final int ORIENTATION_REVERSE_LANDSCAPE = 2;
    public static final int ORIENTATION_REVERSE_PORTRAIT = 3;

    public RangeOfMotionStepLayout(Context context) {
        super(context);
    }

    public RangeOfMotionStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RangeOfMotionStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public RangeOfMotionStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
    }

    @Override
    protected void validateStep(Step step) {
        if (!(step instanceof RangeOfMotionStep)) {
            throw new IllegalStateException("RangeOfMotionStepLayout must have a RangeOfMotionStep");
        }
        rangeOfMotionStep = (RangeOfMotionStep) step;
        super.validateStep(step);
    }

    @Override
    public void setupActiveViews() {
        super.setupActiveViews();

        LayoutInflater.from(getContext())
                .inflate(R.layout.rsb_step_layout_range_of_motion, this, true);

        titleTextview.setVisibility(View.VISIBLE);
        textTextview.setVisibility(View.VISIBLE);
        timerTextview.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        progressBarHorizontal.setVisibility(View.GONE);
        submitBar.setVisibility(View.GONE);

        setupOnClickListener();
    }

    public void setupOnClickListener() {
        layout = findViewById(R.id.rsb_step_layout_range_of_motion);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinish();
            }
        });
    }

    @Override
    protected void registerRecorderBroadcastReceivers(Context appContext) {
        super.registerRecorderBroadcastReceivers(appContext);

        deviceMotionReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                SensorEventListener sensorEventListener = new SensorEventListener() {

                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        timestamp = event.timestamp;
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    }
                };
                
                if (intent == null || intent.getAction() == null) {
                    return;
                }
                // This obtains values from the rotation vector sensor via a broadcast from DeviceMotionRecorder
                if (DeviceMotionRecorder.BROADCAST_ROTATION_VECTOR_UPDATE_ACTION.equals(intent.getAction())) {
                    DeviceMotionRecorder.RotationVectorUpdateHolder dataHolder =
                            DeviceMotionRecorder.getRotationVectorUpdateHolder(intent);
                    if (dataHolder != null) {
                        float[] rv;
                        if (dataHolder.getW() != 0.0f) {
                            rv = new float[]{
                                    dataHolder.getX(),
                                    dataHolder.getY(),
                                    dataHolder.getZ(),
                                    dataHolder.getW()
                            };
                            updatedDeviceAttitudeAsQuaternion = getDeviceAttitudeAsQuaternion(rv);
                        } else {
                            rv = new float[]{
                                    dataHolder.getX(),
                                    dataHolder.getY(),
                                    dataHolder.getZ()
                            };
                            updatedDeviceAttitudeAsQuaternion = getDeviceAttitudeAsQuaternion(rv);
                        }

                        double updatedAngle = calculateShiftedAngleRelativeToStart(updatedDeviceAttitudeAsQuaternion); // converts the current device attitude into an angle (degrees)
                        if (!firstAttitudeCaptured) {
                            setStartAttitude(updatedDeviceAttitudeAsQuaternion);
                            firstAttitudeCaptured = true; // prevents setStartAttitude() from being re-set after the first pass
                            }
                        if (updatedAngle < minAngle) { // captures the minimum angle (relative to start) that is recorded during the task
                            minAngle = updatedAngle;
                            setMinimumAngle(minAngle);
                            }
                        if (updatedAngle > maxAngle) { // captures the maximum angle (relative to start) that is recorded during the task
                            maxAngle = updatedAngle;
                            setMaximumAngle(maxAngle);
                            }
                        setFinishAttitude(updatedDeviceAttitudeAsQuaternion); // this will continually be reset until the last value
                    }
                }
                // This obtains values from the accelerometer sensor via a broadcast from DeviceMotionRecorder
                if (DeviceMotionRecorder.BROADCAST_ACCELEROMETER_UPDATE_ACTION.equals(intent.getAction())) {
                    DeviceMotionRecorder.AccelerometerUpdateHolder dataHolder =
                            DeviceMotionRecorder.getAccelerometerUpdateHolder(intent);
                    if (dataHolder != null) {
                        float[] accel;
                        accel = new float[]{
                                dataHolder.getAx(), // x
                                dataHolder.getAy(), // y
                                dataHolder.getAz(), // z
                                //dataHolder.getAt() // timestamp of accelerometer events (in seconds)
                        };
                        accel_count++; // counts the number of sensor events (n) from the accelerometer

                        if (accel[0] > maxAx) { // captures the maximum recorded acceleration along the x-axis (Ax)
                            maxAx = accel[0];
                            setMaximumAx(maxAx);
                        }
                        if (accel[0] < minAx) { // captures the minimum recorded acceleration along the x-axis (Ax)
                            minAx = accel[0];
                            setMinimumAx(minAx);
                        }
                        if (accel[1] > maxAy) { // captures the maximum recorded acceleration along the y-axis (Ay)
                            maxAy = accel[1];
                            setMaximumAy(maxAy);
                        }
                        if (accel[1] < minAy) { // captures the minimum recorded acceleration along the y-axis (Ay)
                            minAy = accel[1];
                            setMinimumAy(minAy);
                        }
                        if (accel[2] > maxAz) { // captures the maximum recorded acceleration along the z-axis (Az)
                            maxAz = accel[2];
                            setMaximumAz(maxAz);
                        }
                        if (accel[2] < minAz) { // captures the minimum recorded acceleration along the z-axis (Az)
                            minAz = accel[2];
                            setMinimumAz(minAz);
                        }
                        // calculate resultant acceleration (Ar)
                        double resultant_accel = Math.sqrt(
                                (accel[0] * accel[0]) + (accel[1] * accel[1]) + (accel[2] * accel[2])
                        );
                        if (resultant_accel > maxAr) { // captures the maximum recorded resultant acceleration
                            maxAr = resultant_accel;
                            setMaximumAr(maxAr);
                        }
                        // calculate mean and standard deviation of resultant acceleration (using Welford's algorithm)
                        // see: Welford. (1962) Technometrics 4(3): 419-420.
                        if (accel_count == 1) {
                            prevMa = newMa = resultant_accel;
                            prevSa = 0;
                        } else {
                            newMa = prevMa + (resultant_accel - prevMa) / accel_count;
                            newSa += prevSa + (resultant_accel - prevMa) * (resultant_accel - newMa);
                            prevMa = newMa;
                        }
                        meanAr = (accel_count > 0) ? newMa : 0;
                        varianceAr = ((accel_count > 1) ? newSa / (accel_count - 1) : 0);
                        if (varianceAr > 0) {
                            standardDevAr = Math.sqrt(varianceAr);
                        }
                        setSDAccel(standardDevAr);
                        setMeanAccel(meanAr);

                        // calculate jerk (time derivative of acceleration)
                        if (accel_count == 1) {
                            first_time = prev_time = new_time = System.nanoTime() / 10e8; // captures first time value (in seconds)
                            //first_time = prev_time = accel[3]; // TODO: SensorEvent.timestamp is preferred but currently not producing results
                            prevAccel[0] = newAccel[0] = accel[0]; // x
                            prevAccel[1] = newAccel[1] = accel[1]; // y
                            prevAccel[2] = newAccel[2] = accel[2]; // z
                        } else {
                            prev_time = new_time; // assigns previous time value
                            new_time = System.nanoTime() / 10e8; // immediately updates to the new time value (in seconds)
                            //new_time = accel[3]; // TODO: SensorEvent.timestamp is preferred but currently not producing results
                            double temp = sumDeltaTime + Math.abs(new_time - prev_time); // see: Press, Teukolsky, Vetterling, Flannery (2007) Numerical Recipes; p230.
                            double delta_time = temp - sumDeltaTime;
                            sumDeltaTime += delta_time; // sum of all deltas

                            // assign previous accel values
                            prevAccel[0] = newAccel[0]; // x
                            prevAccel[1] = newAccel[1]; // y
                            prevAccel[2] = newAccel[2]; // z
                            // assign new accel values
                            newAccel[0] = accel[0]; // x
                            newAccel[1] = accel[1]; // y
                            newAccel[2] = accel[2]; // z
                            // calculate difference in acceleration between consecutive sensor measurements
                            float[] delta_accel;
                            delta_accel = new float[]{
                                    (newAccel[0] - prevAccel[0]), // x
                                    (newAccel[1] - prevAccel[1]), // y
                                    (newAccel[2] - prevAccel[2])  // z
                            };
                            float[] jerk;
                            jerk = new float[]{
                                    (float) (delta_accel[0] / delta_time), // x
                                    (float) (delta_accel[1] / delta_time), // y
                                    (float) (delta_accel[2] / delta_time)  // z
                            };
                            if (jerk[0] > maxJx) { // captures the maximum recorded jerk along the x-axis (Ax)
                                maxJx = jerk[0];
                                setMaximumJx(maxJx);
                            }
                            if (jerk[0] < minJx) { // captures the minimum recorded jerk along the x-axis (Ax)
                                minJx = jerk[0];
                                setMinimumJx(minJx);
                            }
                            if (jerk[1] > maxJy) { // captures the maximum recorded jerk along the y-axis (Ay)
                                maxJy = jerk[1];
                                setMaximumJy(maxJy);
                            }
                            if (jerk[1] < minJy) { // captures the minimum recorded jerk along the y-axis (Ay)
                                minJy = jerk[1];
                                setMinimumJy(minJy);
                            }
                            if (jerk[2] > maxJz) { // captures the maximum recorded jerk along the z-axis (Az)
                                maxJz = jerk[2];
                                setMaximumJz(maxJz);
                            }
                            if (jerk[2] > minJz) { // captures the minimum recorded jerk along the z-axis (Az)
                                minJz = jerk[2];
                                setMinimumJz(minJz);
                            }
                            // calculate resultant jerk
                            resultantJerk = Math.sqrt(
                                    (jerk[0] * jerk[0]) + (jerk[1] * jerk[1]) + (jerk[2] * jerk[2])
                            );
                            if (resultantJerk > maxJr) { // captures the maximum recorded resultant jerk
                                maxJr = resultantJerk;
                                setMaximumJr(maxJr);
                            }
                        }
                        // calculate mean and standard deviation of resultant jerk (using Welford's algorithm)
                        if (accel_count == 1) {
                            prevMj = newMj = resultantJerk;
                            prevSj = 0;
                        } else {
                            newMj = prevMj + (resultantJerk - prevMj) / accel_count;
                            newSj = prevSj += (resultantJerk - prevMj) * (resultantJerk - newMj);
                            prevMj = newMj;
                        }
                        meanJr = (accel_count > 0) ? newMj : 0; // mean
                        varianceJr = ((accel_count > 1) ? newSj / (accel_count - 1) : 0); // variance
                        if (varianceJr > 0) {
                            standardDevJr = Math.sqrt(varianceJr); // standard deviation
                        }
                        setSDJerk(standardDevJr);
                        setMeanJerk(meanJr);

                        // calculate the numerical integral of jerk (using extended Simpson's rule)
                        // for original formula, see: Press, Teukolsky, Vetterling, Flannery (2007) Numerical Recipes; p160.
                        if (accel_count == 1) {
                            firstJerk = resultantJerk;
                        } else {
                            lastJerk = resultantJerk;// updates to last iteration
                        }
                        if (accel_count != 1) { // need to avoid a zero denominator at (n - 1)
                            h = total_time / (accel_count - 1);
                        }
                        // Sum of all odd (4/3) terms, excluding the first term (n == 1)
                        if (MathUtils.isOdd(accel_count) && accel_count != 1) {
                            sumOdd += 4.0 * resultantJerk;
                        }
                        // Sum of all even (2/3) terms
                        if (MathUtils.isEven(accel_count)) {
                            sumEven += 2.0 * resultantJerk;
                        }
                        if (MathUtils.isOdd(accel_count)) {
                            integratedJerk = h * (firstJerk + sumOdd + sumEven - (3.0 * lastJerk)) / 3.0; // lastJerk will have been added to SumEven 4 times, but we only want to retain one
                        } else if (MathUtils.isEven(accel_count)) {
                            integratedJerk = h * (firstJerk + sumOdd + sumEven - lastJerk) / 3.0; // lastJerk will have been added to SumEven 2 times, but we only want to retain one
                        }
                        // the time duration of each recorded task will be different, so comparable results must be normalized by duration
                        total_time = Math.abs(new_time - first_time); // total time duration of entire recording (in seconds)
                        double time_normalized_integrated_jerk = integratedJerk / total_time;
                        setTimeNormIntegratedJerk(time_normalized_integrated_jerk);
                        setDuration(total_time);
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(DeviceMotionRecorder.BROADCAST_ROTATION_VECTOR_UPDATE_ACTION);
        intentFilter.addAction(DeviceMotionRecorder.BROADCAST_ACCELEROMETER_UPDATE_ACTION);
        LocalBroadcastManager.getInstance(appContext)
                .registerReceiver(deviceMotionReceiver, intentFilter);
    }

    @Override
    public void startBackgroundRecorderService() {
        super.startBackgroundRecorderService();

        Context appContext = getContext().getApplicationContext();
        enableOrientationEventListener(appContext); // initiates capture of the initial device orientation
    }

    @Override
    protected void unregisterRecorderBroadcastReceivers() {
        super.unregisterRecorderBroadcastReceivers();
        Context appContext = getContext().getApplicationContext();
        LocalBroadcastManager.getInstance(appContext).
                unregisterReceiver(deviceMotionReceiver);
    }

    public void onFinish() {
        stepResultFinished();
        layout.setOnClickListener(null);
        stop(); // this should stop both device motion recording and broadcasts
    }

    /**
     * Method to get all possible physical orientations of the device (portrait and landscape, reverse
     * portrait and reverse landscape) to ensure that angles (in degrees) are calculated correctly
     * irrespective of the start orientation of the device. Capturing device orientation using Android's
     * Configuration and Display classes is unreliable, as these actually report the rotation of the
     * onscreen rendered image relative to the 'natural' device orientation (portrait on most devices),
     * especially for active tasks when the user may not be looking at the screen, as in the Range of
     * Motion task, and may not realise that auto-rotate is not enabled. We therefore want to use the
     * physical orientation of the device itself, which can be captured using the onOrientationChanged()
     * method from the OrientationEventListener class.
     */
    public void enableOrientationEventListener(Context appContext) {
        OrientationEventListener orientationEventListener = new OrientationEventListener(
                appContext, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int degrees) {
                if (degrees > 315 || degrees <= 45) { // 0 degrees
                    orientation = ORIENTATION_PORTRAIT;
                } else if (degrees > 45 && degrees <= 135) { // 90 degrees
                    orientation = ORIENTATION_REVERSE_LANDSCAPE;
                } else if (degrees > 135 && degrees <= 225) { // 180 degrees
                    orientation = ORIENTATION_REVERSE_PORTRAIT;
                } else if (degrees > 225 && degrees <= 315) { //270 degrees
                    orientation = ORIENTATION_LANDSCAPE;
                } else if (degrees < 0) { // flipped screen
                    orientation = ORIENTATION_UNSPECIFIED;
                    Log.i(ContentValues.TAG, "The device orientation is unspecified: value = "
                            + orientation );
                }
                if (!firstOrientationCaptured && orientation != ORIENTATION_UNSPECIFIED) {
                    setInitialOrientation(orientation);
                    firstOrientationCaptured = true; // prevents setFirstOrientation from being re-set
                }
            }
        };
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        } else {
            orientation = ORIENTATION_UNDETECTABLE;
            Log.i(ContentValues.TAG, "The device orientation is undetectable: value = "
                    + orientation );
        }
    }

    public double getMaximumAx() {
        return maximumAx;
    }

    private void setMaximumAx (double maximumAx) {
        this.maximumAx = maximumAx;
    }

    public double getMinimumAx() {
        return minimumAx;
    }

    private void setMinimumAx (double minimumAx) {
        this.minimumAx = minimumAx;
    }

    public double getMaximumAy() {
        return maximumAy;
    }

    private void setMaximumAy (double maximumAy) {
        this.maximumAy = maximumAy;
    }

    public double getMinimumAy() {
        return minimumAy;
    }

    private void setMinimumAy (double minimumAy) {
        this.minimumAy = minimumAy;
    }

    public double getMaximumAz() {
        return maximumAz;
    }

    private void setMaximumAz (double maximumAz) {
        this.maximumAz = maximumAz;
    }

    public double getMinimumAz() {
        return minimumAz;
    }

    private void setMinimumAz (double minimumAz) {
        this.minimumAz = minimumAz;
    }

    public double getMaximumAr() {
        return maximumAr;
    }

    private void setMaximumAr (double maximumAr) {
        this.maximumAr = maximumAr;
    }

    public double getMeanAccel() {
        return meanAccel;
    }

    private void setMeanAccel (double meanAccel) {
        this.meanAccel = meanAccel;
    }

    public double getSDAccel() {
        return SDAccel;
    }

    private void setSDAccel (double SDAccel) {
        this.SDAccel = SDAccel;
    }

    public double getMaximumJx() {
        return maximumJx;
    }

    private void setMaximumJx (double maximumJx) {
        this.maximumJx = maximumJx;
    }

    public double getMinimumJx() {
        return minimumJx;
    }

    private void setMinimumJx (double minimumJx) {
        this.minimumJx = minimumJx;
    }

    public double getMaximumJy() {
        return maximumJy;
    }

    private void setMaximumJy (double maximumJy) {
        this.maximumJy = maximumJy;
    }

    public double getMinimumJy() {
        return minimumJy;
    }

    private void setMinimumJy (double minimumJy) {
        this.minimumJy = minimumJy;
    }

    public double getMaximumJz() {
        return maximumJz;
    }

    private void setMaximumJz (double maximumJz) {
        this.maximumJz = maximumJz;
    }

    public double getMinimumJz() {
        return minimumJz;
    }

    private void setMinimumJz (double minimumJz) {
        this.minimumJz = minimumJz;
    }

    public double getMaximumJr() {
        return maximumJr;
    }

    private void setMaximumJr (double maximumJr) {
        this.maximumJr = maximumJr;
    }

    public double getMeanJerk() {
        return meanJerk;
    }

    private void setMeanJerk (double meanJerk) {
        this.meanJerk = meanJerk;
    }

    public double getSDJerk() {
        return SDJerk;
    }

    private void setSDJerk (double SDJerk) {
        this.SDJerk = SDJerk;
    }

    public double getTimeNormIntegratedJerk() {
        return timeNormIntegratedJerk;
    }

    private void setTimeNormIntegratedJerk (double timeNormIntegratedJerk) {
        this.timeNormIntegratedJerk = timeNormIntegratedJerk;
    }

    public double getDuration() {
        return duration;
    }

    private void setDuration(double duration) {
        this.duration = duration;
    }

    /**
     * Methods to obtain the device's physical orientation at the beginning of the task
     */
    public int getInitialOrientation() {
        return initialOrientation;
    }

    private void setInitialOrientation(int initialOrientation) {
        this.initialOrientation = initialOrientation;
    }

    /**
     * Methods to obtain the initial (start) device attitude as a unit quaternion
     */
    public float[] getStartAttitude() {
        return startAttitude;
    }

    private void setStartAttitude(float[] startAttitude) {
        this.startAttitude = startAttitude;
    }

    /**
     * Method to obtain range-shifted angle (degrees) of first (start) device attitude, relative to
     * the zero position
     */
    public double getShiftedStartAngle() {
        double shifted_start_angle;
        double raw_start_angle = getDeviceAngleInDegreesFromQuaternion(getStartAttitude());
        shifted_start_angle = shiftStartAndFinishAngleRanges(raw_start_angle);
        return shifted_start_angle;
    }

    /**
     * Methods to obtain the final (finish) device attitude as a unit quaternion
     */
    public float[] getFinishAttitude() {
        return finishAttitude;
    }

    private void setFinishAttitude(float[] finishAttitude) {
        this.finishAttitude = finishAttitude;
    }

    /**
     * Method to obtain range-shifted angle (degrees) of final (finish) device attitude, relative to
     * the zero position
     */
    public double getShiftedFinishAngle() {
        double shifted_finish_angle;
        double raw_finish_angle = getDeviceAngleInDegreesFromQuaternion(getFinishAttitude());
        shifted_finish_angle = shiftStartAndFinishAngleRanges(raw_finish_angle);
        return shifted_finish_angle;
    }

    /**
     * Method to shift default range of calculated angles for specific devices or screen orientations,
     * when required, for start and finish angles. Should be overridden in sub-classes where necessary.
     */
    public double shiftStartAndFinishAngleRanges(double original_angle) {
        double shifted_angle;
        int initial_orientation = getInitialOrientation();
        if (initial_orientation == ORIENTATION_REVERSE_PORTRAIT
                && (original_angle >= 0 && original_angle < 180)) {
            shifted_angle = Math.abs(original_angle) - 360;
        } else {
            shifted_angle = original_angle;
        }
        return shifted_angle;
    }
    
    /**
     * Methods to obtain and calculate the minimum and maximum range-shifted angle (degrees), calculated
     * for all device motion updates during recording, relative to the start position
     */
    private double calculateShiftedAngleRelativeToStart(float[] attitudeUpdates) {
        double shifted_angle;
        float[] attitudeUpdatesRelativeToStart = getDeviceAttitudeRelativeToStart(attitudeUpdates);
        double unadjusted_angle = getDeviceAngleInDegreesFromQuaternion(attitudeUpdatesRelativeToStart);
        shifted_angle = shiftMinAndMaxAngleRange(unadjusted_angle);
        return shifted_angle;
    }

    public double getMinimumAngle() {
        return minimumAngle;
    }

    private void setMinimumAngle (double minimumAngle) {
        this.minimumAngle = minimumAngle;
    }

    public double getMaximumAngle() {
        return maximumAngle;
    }

    private void setMaximumAngle (double maximumAngle) {
        this.maximumAngle = maximumAngle;
    }

    /**
     * Method to extend the available range for maximum angle from 180 degrees to 270 degrees, relative
     * to the start position. Range for minimum angle will be reduced to 90 degrees. Should be overridden
     * in sub-classes where necessary.
     */
    public double shiftMinAndMaxAngleRange(double original_angle) {
        double shifted_angle;
        int initial_orientation = getInitialOrientation();
        if ((initial_orientation == ORIENTATION_PORTRAIT || initial_orientation == ORIENTATION_REVERSE_PORTRAIT)
                && (original_angle > 90 && original_angle <= 180)) {
            shifted_angle = Math.abs(original_angle) - 360;
        } else {
            shifted_angle = original_angle;
        }
        return shifted_angle;
    }

    /**
     * Method to calculate angles in degrees from the device attitude quaternion, as a function of
     * device orientation (portrait or landscape) or screen orientation (portrait, landscape, reverse
     * portrait or reverse landscape).
     */
    public double getDeviceAngleInDegreesFromQuaternion(float[] quaternion) {
        double angle_in_degrees = 0;
        int initial_orientation = getInitialOrientation();

        if (initial_orientation == ORIENTATION_LANDSCAPE || initial_orientation == ORIENTATION_REVERSE_LANDSCAPE) {
            angle_in_degrees = Math.toDegrees(MathUtils.allOrientationsForRoll (
                    quaternion[0],
                    quaternion[1],
                    quaternion[2],
                    quaternion[3]));
        }
        else if (initial_orientation == ORIENTATION_PORTRAIT || initial_orientation == ORIENTATION_REVERSE_PORTRAIT) {
            angle_in_degrees = Math.toDegrees(MathUtils.allOrientationsForPitch (
                    quaternion[0],
                    quaternion[1],
                    quaternion[2],
                    quaternion[3]));
        }
        return angle_in_degrees;
    }

    /**
     * Method to obtain the updated device attitude relative to the start position by multiplying updates
     * of the attitude quaternion by the inverse of the quaternion that represents the start position.
     * This relativity is necessary if the task is being performed in different start positions, which
     * could result in angles that exceed the already shifted range.
     */
    public float[] getDeviceAttitudeRelativeToStart(float[] originalDeviceAttitude) {
        float[] relativeDeviceAttitude;
        float[] inverseOfStart = MathUtils.calculateInverseOfQuaternion(getStartAttitude());
        relativeDeviceAttitude = MathUtils.multiplyQuaternions(originalDeviceAttitude, inverseOfStart);
        return relativeDeviceAttitude;
    }

    /**
     * Method to obtain the device's attitude as a unit quaternion from the rotation vector sensor,
     * when it is available
     */
    public float[] getDeviceAttitudeAsQuaternion(float[] rotation_vector) {
        float[] attitudeQuaternion = new float[4];
        SensorManager.getQuaternionFromVector(attitudeQuaternion, rotation_vector);
        return attitudeQuaternion;
    }

    @Override
    protected void stepResultFinished() {

        Context appContext = getContext().getApplicationContext();
        sensorManager = (SensorManager) appContext.getSystemService(Context.SENSOR_SERVICE);

        // task
        boolean has_rotation_vector;
        boolean has_accelerometer;
        int initial_orientation;
        double duration;
        // angles
        double start, finish, minimum, maximum, range;
        // acceleration
        double maxAx, maxAy, maxAz, maxAr;
        double minAx, minAy, minAz, minAr;
        double meanAr, SDAr;
        // jerk
        double maxJx, maxJy, maxJz, maxJr;
        double minJx, minJy, minJz, minJr;
        double meanJr, SDJr;
        double timeNormIntegratedJerk;

        rangeOfMotionResult = new RangeOfMotionResult(rangeOfMotionStep.getIdentifier());

        // Check if rotation vector sensor is available on the device
        has_rotation_vector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null;
        rangeOfMotionResult.setHasRotationVector(has_rotation_vector);

        // Check if accelerometer sensor is available on the device
        has_accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
        rangeOfMotionResult.setHasAccelerometer(has_accelerometer);

        // Initial device orientation
        initial_orientation = getInitialOrientation();
        rangeOfMotionResult.setOrientation(initial_orientation);

        // Task duration (seconds)
        duration = getDuration();
        rangeOfMotionResult.setDuration(duration);

        // Acceleration values
        if (!has_accelerometer) {
            maxAx = NaN;
            minAx = NaN;
            maxAy = NaN;
            minAy = NaN;
            maxAz = NaN;
            minAz = NaN;
            maxAr = NaN;
            meanAr = NaN;
            SDAr = NaN;
        } else {
            maxAx = getMaximumAx();
            minAx = getMinimumAx();
            maxAy = getMaximumAy();
            minAy = getMinimumAy();
            maxAz = getMaximumAz();
            minAz = getMinimumAz();
            maxAr = getMaximumAr();
            meanAr = getMeanAccel();
            SDAr = getSDAccel();
        }
        rangeOfMotionResult.setMaximumAx(maxAx);
        rangeOfMotionResult.setMinimumAx(minAx);
        rangeOfMotionResult.setMaximumAy(maxAy);
        rangeOfMotionResult.setMinimumAy(minAy);
        rangeOfMotionResult.setMaximumAz(maxAz);
        rangeOfMotionResult.setMinimumAz(minAz);
        rangeOfMotionResult.setMaximumAr(maxAr);
        rangeOfMotionResult.setSDAr(SDAr);

        // Jerk values
        if (!has_accelerometer) {
            maxJx = NaN;
            minJx = NaN;
            maxJy = NaN;
            minJy = NaN;
            maxJz = NaN;
            minJz = NaN;
            maxJr = NaN;
            meanJr = NaN;
            SDJr = NaN;
            timeNormIntegratedJerk = NaN;
        } else {
            maxJx = getMaximumJx();
            minJx = getMinimumJx();
            maxJy = getMaximumJy();
            minJy = getMinimumJy();
            maxJz = getMaximumJz();
            minJz = getMinimumJz();
            maxJr = getMaximumJr();
            meanJr = getMeanJerk();
            SDJr = getSDJerk();
            timeNormIntegratedJerk = getTimeNormIntegratedJerk();
        }
        rangeOfMotionResult.setMaximumJx(maxJx);
        rangeOfMotionResult.setMinimumJx(minJx);
        rangeOfMotionResult.setMaximumJy(maxJy);
        rangeOfMotionResult.setMinimumJy(minJy);
        rangeOfMotionResult.setMaximumJz(maxJz);
        rangeOfMotionResult.setMinimumJz(minJz);
        rangeOfMotionResult.setMaximumJr(maxJr);
        rangeOfMotionResult.setMeanJerk(meanJr);
        rangeOfMotionResult.setSDJerk(SDJr);
        rangeOfMotionResult.setTimeNormIntegratedJerk(timeNormIntegratedJerk);

        /* Like iOS, when using quaternions via the rotation vector sensor in Android, the zero attitude
        {0,0,0,0} position is parallel with the ground (i.e. screen facing up). Hence, tasks in which
        portrait or landscape is the start position (i.e. perpendicular to the ground) require a 90 degree
        adjustment. These are set to report an absolute an angle between +270 and -90 degrees. These
        calculations will need to be overridden in tasks where this range is not appropriate.*/

        // Capture absolute start angle relative to device orientation
        if (!has_rotation_vector) {
            start = NaN;
        } else {
            if (initial_orientation == ORIENTATION_REVERSE_LANDSCAPE) {
                start = 90 + getShiftedStartAngle();
            } else if (initial_orientation == ORIENTATION_REVERSE_PORTRAIT) {
                start = -90 - getShiftedStartAngle();
            } else if (initial_orientation == ORIENTATION_UNSPECIFIED || initial_orientation == ORIENTATION_UNDETECTABLE) {
                start = NaN;
            } else {
                start = 90 - getShiftedStartAngle();
            }
        }
        rangeOfMotionResult.setStart(start);

        // Capture absolute finish angle relative to device orientation
        if (!has_rotation_vector) {
            finish = NaN;
        } else {
            if (initial_orientation == ORIENTATION_REVERSE_LANDSCAPE) {
                finish = 90 + getShiftedFinishAngle();
            } else if (initial_orientation == ORIENTATION_REVERSE_PORTRAIT) {
                finish = -90 - getShiftedFinishAngle();
            } else if (initial_orientation == ORIENTATION_UNSPECIFIED || initial_orientation == ORIENTATION_UNDETECTABLE) {
                finish = NaN;
            } else {
                finish = 90 - getShiftedFinishAngle();
            }
        }
        rangeOfMotionResult.setFinish(finish);

        /* Because both knee and shoulder tasks both use pitch in the direction opposite to the device
        axes (i.e. right hand rule), maximum and minimum angles are reported the 'wrong' way around
        for these particular tasks when the device is in portrait or landscape mode */

        // Capture minimum angle relative to device orientation
        if (!has_rotation_vector) {
            minimum = NaN;
        } else {
            if (initial_orientation == ORIENTATION_REVERSE_LANDSCAPE) {
                minimum = start + getMinimumAngle();
            } else if (initial_orientation == ORIENTATION_UNSPECIFIED || initial_orientation == ORIENTATION_UNDETECTABLE) {
                minimum = NaN;
            } else {
                minimum = start - getMaximumAngle(); // landscape, portrait and reverse portrait
            }
        }
        rangeOfMotionResult.setMinimum(minimum);

        // Capture maximum angle relative to device orientation
        if (!has_rotation_vector) {
            maximum = NaN;
        } else {
            if (initial_orientation == ORIENTATION_REVERSE_LANDSCAPE) {
                maximum = start + getMaximumAngle();
            } else if (initial_orientation == ORIENTATION_UNSPECIFIED || initial_orientation == ORIENTATION_UNDETECTABLE) {
                maximum = NaN;
            } else {
                maximum = start - getMinimumAngle(); // landscape, portrait and reverse portrait
            }
        }
        rangeOfMotionResult.setMaximum(maximum);

        // Capture range as largest difference across all recorded angles
        range = Math.abs(maximum - minimum);
        rangeOfMotionResult.setRange(range);

        stepResult.setResultForIdentifier(rangeOfMotionResult.getIdentifier(), rangeOfMotionResult);
    }
}
