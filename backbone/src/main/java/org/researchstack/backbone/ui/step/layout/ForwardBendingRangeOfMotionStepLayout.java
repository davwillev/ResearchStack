package org.researchstack.backbone.ui.step.layout;

import java.lang.Math;
import android.content.Context;
import android.util.AttributeSet;
import org.researchstack.backbone.result.RangeOfMotionResult;


/**
 * Created by David Evans, Simon Hartley, Laurence Hurst, David Jimenez, 2019.
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
     * Method to calculate Euler angles from the device attitude quaternion, as a function of
     * screen orientation
     **/


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
        ground), whereas in iOS ResearchKit zero is parallel with the ground. Hence, there will be
        a 90 degree reported difference between these configurations from the same task **/

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
