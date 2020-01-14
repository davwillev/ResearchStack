package org.researchstack.backbone.step.active;

/**
 * Created by David Evans, 2019.
 */

public class TrunkRotationRangeOfMotionStep extends RangeOfMotionStep {

    /* Default constructor needed for serilization/deserialization of object */

    public TrunkRotationRangeOfMotionStep() {
        super();
    }

    public TrunkRotationRangeOfMotionStep(String identifier) {
        super(identifier);
    }

    public TrunkRotationRangeOfMotionStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

    @Override
    public Class getStepLayoutClass() {
        return TrunkRotationRangeOfMotionStepLayout.class;
    }

}
