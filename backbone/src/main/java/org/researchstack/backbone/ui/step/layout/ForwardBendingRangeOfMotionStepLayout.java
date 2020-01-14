package org.researchstack.backbone.ui.step.layout;

import java.lang.Math;
import android.content.Context;
import android.util.AttributeSet;
import org.researchstack.backbone.result.RangeOfMotionResult;

/**
 * Created by David Evans, 2019.
 *
 * The ForwardBendingRangeOfMotionStepLayout is essentially the same as the RangeOfMotionStepLayout,
 * except that the results for maximum and minimum angles in degrees are different, because the
 * device will rotate in the opposite direction during the task
 *
 */

public class ForwardBendingRangeOfMotionStepLayout extends RangeOfMotionStepLayout {

    public ForwardBendingRangeOfMotionStepLayout(Context context) {
        super(context);
    }

    public ForwardBendingRangeOfMotionStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForwardBendingRangeOfMotionStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ForwardBendingRangeOfMotionStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Method to shift default range of calculated angles to create an available range of +270 and -90,
     * whilst allowing for the necessary 90 degree adjustment to the start position in the stepResultFinished()
     * method required for tasks that begin in a vertical device orientation. This range is suitable for the
     * forward bending task, which rotates in the direction opposite to the knee and shoulder tasks.
     * However, if +/-180 is preferred, then the targetAngleRange should be changed to:
     * boolean targetAngleRange = ((original_angle < -90) && (original_angle >= -180));
     **/
    @Override
    public double shiftDeviceAngleRange(double original_angle) {
        double shifted_angle;
        boolean targetAngleRange = ((original_angle < 0) && (original_angle >= -180));
        if (targetAngleRange) {
            shifted_angle = 360 - Math.abs(original_angle);
        } else {
            shifted_angle = original_angle;
        }
        return shifted_angle;
    }

    @Override
    protected void stepResultFinished() {

        double start;
        double finish;
        double minimum;
        double maximum;
        double range;

        RangeOfMotionResult rangeOfMotionResult = new RangeOfMotionResult(rangeOfMotionStep.getIdentifier());
        
        /* Like iOS, when using quaternions via the rotation vector sensor in Android, the zero orientation
        {0,0,0,0} position is parallel with the ground (i.e. screen facing up). Hence, tasks in which
        portrait is the start position (i.e. perpendicular to the ground) require a 90 degree adjustment.
        In addition, the sign of angles calculated from the quaternion need to be reversed for the knee
        and shoulder tasks, in which the device will be rotated in the direction opposite to the orientation
        of the device axes (which use the right hand rule). */

        start = getShiftedStartAngle() - 90; // reports absolute an angle between +270 and -90 degrees
        rangeOfMotionResult.setStart(start);

        finish = getShiftedFinishAngle() -90; // absolute angle
        rangeOfMotionResult.setFinish(finish);

        /* Unlike the knee and shoulder tasks, which both use pitch in the direction opposite to the device
        axes (i.e. right hand rule), the forward bending task rotates the device (pitch) in the positive
        direction for the device axes (right hand rule. Therefore, maximum and minimum angles are reported
        the 'right' way around for this particular tasks. */

        minimum = start + getMinimumAngle(); // captured minimum angle
        rangeOfMotionResult.setMinimum(minimum);

        maximum = start + getMaximumAngle(); // captured maximum angle
        rangeOfMotionResult.setMaximum(maximum);

        range = Math.abs(maximum - minimum); // largest range across all recorded angles
        rangeOfMotionResult.setRange(range);

        stepResult.setResultForIdentifier(rangeOfMotionResult.getIdentifier(), rangeOfMotionResult);
    }
}
