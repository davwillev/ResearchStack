package org.researchstack.backbone.ui.step.layout;

import java.lang.Math;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import org.researchstack.backbone.result.RangeOfMotionResult;
import org.researchstack.backbone.utils.MathUtils;

/**
 * Created by David Evans, 2019.
 *
 * The TrunkRotationRangeOfMotionStepLayout is essentially the same as the RangeOfMotionStepLayout,
 * except that the calculations for device position angles in degrees are different, because the
 * device will rotate around a different axis during the task
 *
 */

public class TrunkRotationRangeOfMotionStepLayout extends RangeOfMotionStepLayout {

    public TrunkRotationRangeOfMotionStepLayout(Context context) {
        super(context);
    }

    public TrunkRotationRangeOfMotionStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrunkRotationRangeOfMotionStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TrunkRotationRangeOfMotionStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    /**
     * Method to calculate angles (in degrees) from the device attitude quaternion, as a function of
     * screen orientation
     **/
    @Override
    public double getDeviceAngleInDegreesFromQuaternion(float[] quaternion) {

        double angle_in_degrees = 0;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            angle_in_degrees = Math.toDegrees(MathUtils.allOrientationsForPitch (
                    quaternion[0],
                    quaternion[1],
                    quaternion[2],
                    quaternion[3])
            );
        }
        else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            angle_in_degrees = Math.toDegrees(MathUtils.allOrientationsForRoll (
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

        double start;
        double finish;
        double minimum;
        double maximum;
        double range;

        RangeOfMotionResult rangeOfMotionResult = new RangeOfMotionResult(rangeOfMotionStep.getIdentifier());
        
        /** The result of the Trunk Rotation task involves rotation in the transverse plane so will not require
        an adjustment with the phone is orientated in portrait or landscape modes.**/

        start = getShiftedStartAngle(); // reports absolute an angle between +270 and -90 degrees
        rangeOfMotionResult.setStart(start);

        finish = getShiftedFinishAngle(); // absolute angle
        rangeOfMotionResult.setFinish(finish);

        minimum = start + getMinimumAngle(); // captured minimum angle
        rangeOfMotionResult.setMinimum(minimum);

        maximum = start + getMaximumAngle(); // captured maximum angle
        rangeOfMotionResult.setMaximum(maximum);

        range = Math.abs(maximum - minimum); // largest range across all recorded angles
        rangeOfMotionResult.setRange(range);

        stepResult.setResultForIdentifier(rangeOfMotionResult.getIdentifier(), rangeOfMotionResult);
    }
}
