//package org.researchstack.backbone.step.active;
package com.spineapp;

import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.task.factory.HandTaskOptions;
import org.researchstack.backbone.task.factory.TaskOptions;
//import org.researchstack.backbone.ui.step.layout.LeftRightJudgementStepLayout;

/**
 * Created by David Evans in January 2021.
 */

public class LeftRightJudgementStep extends ActiveStep {

    private int numberOfAttempts;
    private double minimumInterStimulusInterval;
    private double maximumInterStimulusInterval;
    private double timeout;
    private boolean shouldDisplayAnswer;
    private TaskOptions.ImageOption imageOption; //enum

    /* Default constructor needed for serialization/deserialization of object */
    //LeftRightJudgementStep() {
    //    super();
    //}

    public LeftRightJudgementStep(String identifier) {
        super(identifier);
        commonInit();
    }

    public LeftRightJudgementStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        commonInit();
    }

    private void commonInit() {
        setShouldShowDefaultTimer(false);
        setOptional(false);
    }

    @Override
    public Class getStepLayoutClass() {
        return LeftRightJudgementStepLayout.class;
    }

    public int getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public void setNumberOfAttempts(int numberOfAttempts) {
        this.numberOfAttempts = numberOfAttempts;
    }

    public double getMinimumInterStimulusInterval() {
        return minimumInterStimulusInterval;
    }

    public void setMinimumInterStimulusInterval(double minimumInterStimulusInterval) {
        this.minimumInterStimulusInterval = minimumInterStimulusInterval;
    }

    public double getMaximumInterStimulusInterval() {
        return maximumInterStimulusInterval;
    }

    public void setMaximumInterStimulusInterval(double maximumInterStimulusInterval) {
        this.maximumInterStimulusInterval = maximumInterStimulusInterval;
    }

    public double getTimeout() {
        return timeout;
    }

    public void setTimeout(double timeout) {
        this.timeout = timeout;
    }

    public boolean getShouldDisplayAnswer() {
        return shouldDisplayAnswer;
    }

    public void setShouldDisplayAnswer(boolean shouldDisplayAnswer) {
        this.shouldDisplayAnswer = shouldDisplayAnswer;
    }

    public TaskOptions.ImageOption getImageOption() {
        return imageOption;
    }

    public void setImageOption(TaskOptions.ImageOption imageOption) {
        this.imageOption = imageOption;
    }

    public int numberOfImages() {
        String directory = getDirectoryForImages();
        String[] pathArray = [[NSBundle bundleForClass:[self class]] pathsForResourcesOfType:@"png" inDirectory:directory];
        int count = pathArray.length;
        return count;
    }

    public String getDirectoryForImages() {
        String directory = null;
        if (getImageOption().equals(TaskOptions.ImageOption.HANDS)) {
            directory = "Images/Hands";
        } else if (getImageOption().equals(TaskOptions.ImageOption.FEET)) {
            directory = "Images/Feet";
        }
        return directory;
    }
}