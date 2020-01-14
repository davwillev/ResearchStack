package org.researchstack.backbone.step.active;

/**
 * Created by David Evans, 2019.
 */

public class ForwardBendingRangeOfMotionStep extends RangeOfMotionStep {

    /* Default constructor needed for serilization/deserialization of object */

    public ForwardBendingRangeOfMotionStep() {
        super();
    }

    public ForwardBendingRangeOfMotionStep(String identifier) {
        super(identifier);
    }

    public ForwardBendingRangeOfMotionStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

    @Override
    public Class getStepLayoutClass() {
        return ForwardBendingRangeOfMotionStepLayout.class;
    }

}
