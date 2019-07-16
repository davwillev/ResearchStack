package org.researchstack.backbone.ui.step.layout;

import java.lang.Math;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import org.researchstack.backbone.result.RangeOfMotionResult;
import org.researchstack.backbone.utils.MathUtils;

/**
 * Created by David Evans, Simon Hartley, Laurence Hurst, David Jimenez, 2019.
 *
 * The SideBendingRangeOfMotionStepLayout is essentially the same as the RangeOfMotionStepLayout,
 * except that the calculations for device position angles in degrees are different, because the
 * device will rotate around a different axis during the task
 *
 */

public class SideBendingRangeOfMotionStepLayout extends RangeOfMotionStepLayout {

    public SideBendingRangeOfMotionStepLayout(Context context) {
        super(context);
    }

    public SideBendingRangeOfMotionStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBendingRangeOfMotionStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SideBendingRangeOfMotionStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    /**
     * Method to calculate Euler angles from the device attitude quaternion, as a function of
     * screen orientation
     **/

    @Override
    public double getDeviceAngleInDegreesFromQuaternion(float[] quaternion) {

        double angle_in_degrees = 0;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getDeviceAttitudeAsQuaternion(sensorEvent.values);
            angle_in_degrees = Math.toDegrees(MathUtils.allOrientationsForYaw (
                    quaternion[0],
                    quaternion[1],
                    quaternion[2],
                    quaternion[3])
            );
        }
        else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            getDeviceAttitudeAsQuaternion(sensorEvent.values);
            angle_in_degrees = Math.toDegrees(MathUtils.allOrientationsForYaw (
                    quaternion[0],
                    quaternion[1],
                    quaternion[2],
                    quaternion[3])
            );
        }
        return angle_in_degrees;
    }

    @Override
    protected void stepResultFinished() {
        super.stepResultFinished();

        double start;
        double finish;
        double minimum;
        double maximum;
        double range;

        RangeOfMotionResult rangeOfMotionResult = new RangeOfMotionResult(rangeOfMotionStep.getIdentifier());
        
        /** In Android's zero orientation, the device is in portrait mode (i.e. perpendicular to the
        ground), whereas in iOS ResearchKit zero is parallel with the ground. However, this should
        not affect the result of the Side Bending task, since it involves rotation in the
        frontal plane **/

        start = getShiftedStartAngle(); // reports absolute an angle between +270 and -90 degrees
        rangeOfMotionResult.setStart(start);

        finish = getShiftedFinishAngle(); // absolute angle
        rangeOfMotionResult.setFinish(finish);

        minimum = start + getShiftedMinimumAngle(); // captured minimum angle
        rangeOfMotionResult.setMinimum(minimum);

        maximum = start + getShiftedMaximumAngle(); // captured maximum angle
        rangeOfMotionResult.setMaximum(maximum);

        range = Math.abs(maximum - minimum); // largest range across all recorded angles
        rangeOfMotionResult.setRange(range);

        stepResult.setResultForIdentifier(rangeOfMotionResult.getIdentifier(), rangeOfMotionResult);
    }
}
