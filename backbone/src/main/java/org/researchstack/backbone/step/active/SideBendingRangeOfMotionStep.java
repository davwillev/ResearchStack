package org.researchstack.backbone.step.active;

/**
 * Created by David Evans, 2019.
 */

public class SideBendingRangeOfMotionStep extends RangeOfMotionStep {

    /* Default constructor needed for serilization/deserialization of object */

    public SideBendingRangeOfMotionStep() {
        super();
    }

    public SideBendingRangeOfMotionStep(String identifier) {
        super(identifier);
    }

    public SideBendingRangeOfMotionStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

    @Override
    public Class getStepLayoutClass() {
        return SideBendingRangeOfMotionStepLayout.class;
    }

}
