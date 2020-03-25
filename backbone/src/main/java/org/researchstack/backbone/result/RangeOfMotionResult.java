package org.researchstack.backbone.result;

/**
 * Created by David Evans, 2019.
 **/

public class RangeOfMotionResult extends Result {

    /**
     * Boolean variable to return true if the rotation vector sensor is available on the device.
     */
    private boolean hasRotationVector;

    /**
     * Boolean variable to return true if the accelerometer sensor is available on the device.
     */
    private boolean hasAccelerometer;

    /**
     * The physical orientation of the device at the start position (the commencement of recording):
     * ORIENTATION_UNDETECTABLE = -2;
     * ORIENTATION_UNSPECIFIED = -1
     * ORIENTATION_LANDSCAPE = 0
     * ORIENTATION_PORTRAIT = 1
     * ORIENTATION_REVERSE_LANDSCAPE = 2
     * ORIENTATION_REVERSE_PORTRAIT = 3
     **/
    private int orientation;

    /**
     * Time duration (seconds) of the task recording.
     */
    private double duration;

    /**
     * The highest magnitude of acceleration (ms^-2) recorded during the task along the positive direction of the x-axis.
     */
    private double maximumAx;

    /**
     * The highest magnitude of acceleration (ms^-2) recorded during the task along the negative direction of the x-axis.
     */
    private double minimumAx;

    /**
     * The highest magnitude of acceleration (ms^-2) recorded during the task along the positive direction of the y-axis.
     */
    private double maximumAy;

    /**
     * The highest magnitude of acceleration (ms^-2) recorded during the task along the negative direction of the y-axis.
     */
    private double minimumAy;

    /**
     * The highest magnitude of acceleration (ms^-2) recorded during the task along the positive direction of the z-axis.
     */
    private double maximumAz;

    /**
     * The highest magnitude of acceleration (ms^-2) recorded during the task along the negative direction of the z-axis.
     */
    private double minimumAz;

    /**
     * The maximum resultant acceleration (ms^-2) recorded during the task.
     */
    private double maximumAr;

    /**
     * Mean resultant acceleration.
     */
    private double meanAr;

    /**
     * Standard deviation of resultant acceleration.
     */
    private double SDAr;

    /**
     * The highest magnitude of jerk (ms^-3) recorded during the task along the positive direction of the x-axis.
     */
    private double maximumJx;

    /**
     * The highest magnitude of jerk (ms^-3) recorded during the task along the negative direction of the x-axis.
     */
    private double minimumJx;

    /**
     * The highest magnitude of jerk (ms^-3) recorded during the task along the positive direction of the y-axis.
     */
    private double maximumJy;

    /**
     * The highest magnitude of jerk (ms^-3) recorded during the task along the negative direction of the y-axis.
     */
    private double minimumJy;

    /**
     * The highest magnitude of jerk (ms^-3) recorded during the task along the positive direction of the z-axis.
     */
    private double maximumJz;

    /**
     * The highest magnitude of jerk (ms^-3) recorded during the task along the negative direction of the z-axis.
     */
    private double minimumJz;

    /**
     * The maximum resultant jerk (ms^-3) recorded during the task.
     */
    private double maximumJr;

    /**
     * Mean resultant jerk; the time derivative of acceleration (ms^-3)
     */
    private double meanJerk;

    /**
     * Standard deviation of resultant jerk; the time derivative of acceleration (ms^-3)
     */
    private double SDJerk;

    /**
     * The time integral of resultant jerk, normalized by the total time of the task (ms^-1)
     */
    private double timeNormIntegratedJerk;

    /**
     * The angle (degrees) from the device reference position at the start position.
     */
    private double start;

    /**
     * The angle (degrees) from the device reference position when the task finishes recording.
     */
    private double finish;

    /**
     * The angle (degrees) from the device reference position at the minimum angle (e.g. when the knee is most bent, such as at the end of the task).
     */
    private double minimum;
    
    /**
     * The angle (degrees) from the device reference position at the maximum angle (e.g. when the knee is most extended during the task).
     */
    private double maximum;
    
    /**
     * The angle (degrees) passed through from the minimum angle to the maximum angle (e.g. from when the knee is most flexed to when it is most extended).
     */
    private double range;


    /* Default identifier for serilization/deserialization */
    RangeOfMotionResult() {
        super();
    }

    public RangeOfMotionResult(String identifier) {
        super(identifier);
    }

    public boolean getHasRotationVector() {
        return hasRotationVector;
    }

    public void setHasRotationVector(boolean hasRotationVector) {
        this.hasRotationVector = hasRotationVector;
    }

    public boolean getHasAccelerometer() {
        return hasAccelerometer;
    }

    public void setHasAccelerometer(boolean hasAccelerometer) {
        this.hasAccelerometer = hasAccelerometer;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getMaximumAx() {
        return maximumAx;
    }

    public void setMaximumAx(double maximumAx) {
        this.maximumAx = maximumAx;
    }

    public double getMinimumAx() {
        return minimumAx;
    }

    public void setMinimumAx(double minimumAx) {
        this.minimumAx = minimumAx;
    }

    public double getMaximumAy() {
        return maximumAy;
    }

    public void setMaximumAy(double maximumAy) {
        this.maximumAy = maximumAy;
    }

    public double getMinimumAy() {
        return minimumAy;
    }

    public void setMinimumAy(double minimumAy) {
        this.minimumAy = minimumAy;
    }

    public double getMaximumAz() {
        return maximumAz;
    }

    public void setMaximumAz(double maximumAz) {
        this.maximumAz = maximumAz;
    }

    public double getMinimumAz() {
        return minimumAz;
    }

    public void setMinimumAz(double minimumAz) {
        this.minimumAz = minimumAz;
    }

    public double getMaximumAr() {
        return maximumAr;
    }

    public void setMaximumAr(double maximumAr) {
        this.maximumAr = maximumAr;
    }

    public double getMeanAr() {
        return meanAr;
    }

    public void setMeanAr(double meanAr) {
        this.meanAr = meanAr;
    }

    public double getSDAr() {
        return SDAr;
    }

    public void setSDAr(double SDAr) {
        this.SDAr = SDAr;
    }

    public double getMaximumJx() {
        return maximumJx;
    }

    public void setMaximumJx(double maximumJx) {
        this.maximumJx = maximumJx;
    }

    public double getMinimumJx() {
        return minimumJx;
    }

    public void setMinimumJx(double minimumJx) {
        this.minimumJx = minimumJx;
    }

    public double getMaximumJy() {
        return maximumJy;
    }

    public void setMaximumJy(double maximumJy) {
        this.maximumJy = maximumJy;
    }

    public double getMinimumJy() {
        return minimumJy;
    }

    public void setMinimumJy(double minimumJy) {
        this.minimumJy = minimumJy;
    }

    public double getMaximumJz() {
        return maximumJz;
    }

    public void setMaximumJz(double maximumJz) {
        this.maximumJz = maximumJz;
    }

    public double getMinimumJz() {
        return minimumJz;
    }

    public void setMinimumJz(double minimumJz) {
        this.minimumJz = minimumJz;
    }

    public double getMaximumJr() {
        return maximumJr;
    }

    public void setMaximumJr(double maximumJr) {
        this.maximumJr = maximumJr;
    }
    public double getMeanJerk() {
        return meanJerk;
    }

    public void setMeanJerk(double meanJerk) {
        this.meanJerk = meanJerk;
    }

    public double getSDJerk() {
        return SDJerk;
    }

    public void setSDJerk(double SDJerk) {
        this.SDJerk = SDJerk;
    }

    public double getTimeNormIntegratedJerk() {
        return timeNormIntegratedJerk;
    }

    public void setTimeNormIntegratedJerk(double timeNormIntegratedJerk) {
        this.timeNormIntegratedJerk = timeNormIntegratedJerk;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getFinish() { return finish; }

    public void setFinish(double finish) {
        this.finish = finish;
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) { this.minimum = minimum; }

    public double getMaximum() {
        return maximum;
    }
    
    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) { this.range = range; }
}
