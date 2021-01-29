package org.researchstack.backbone.task.factory;

import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.TouchAnywhereStep;
import org.researchstack.backbone.step.active.RangeOfMotionStep;
import org.researchstack.backbone.step.active.recorder.AccelerometerRecorderConfig;
import org.researchstack.backbone.step.active.recorder.DeviceMotionRecorderConfig;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.utils.ResUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.researchstack.backbone.task.factory.TaskOptions.Limb.*;
import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;

/**
 * Created by Dr David W. Evans, 2019.
 *
 * In iOS's ResearchKit, they included static methods for building OrderedTasks in the OrderedTask
 * class. However, this class was created to encapsulate the creation of Range of Motion (ROM) Tasks,
 * specifically the knee ROM task.
 */

public class RangeOfMotionTaskFactory {

    /**
     * Returns a predefined task that measures the range of motion for a left knee, a right knee, or both knees.
     * <p>
     * The data collected by this task is device motion data.
     *
     * @param context                   can be app or activity, used for resources
     * @param identifier                The task identifier to use for this task, appropriate to the study.
     * @param limbOption                The limb in which ROM is being measured.
     * @param intendedUseDescription    A localized string describing the intended use of the data
     *                                  collected. If the value of this parameter is `nil`, the default
     *                                  localized text is displayed.
     * @param optionList                Options that affect the features of the predefined task.
     * @return                          An active range of motion task that can be presented with an
     *                                  `ActiveTaskActivity` object.
     */
    public static OrderedTask kneeRangeOfMotionTask(
            Context context,
            String identifier,
            String intendedUseDescription,
            TaskOptions.Limb limbOption,
            List<TaskExcludeOption> optionList)
    {
        List<Step> stepList = new ArrayList<>();

        // Obtain sensor frequency for Range of Motion Task recorders
        double sensorFreq = context.getResources().getInteger(R.integer.rsb_sensor_frequency_range_of_motion_task);


        // Setup which image sets (hands or feet) to start with and how many image sets (1 or both) to add, based on the imageOption parameter. If both image sets are selected, the order is randomly allocated
        int limbCount = (limbOption == BOTH) ? 2 : 1;
        boolean doingBoth = (limbCount == 2);
        boolean rightLimb;

        switch (limbOption) {
            case LEFT:
                rightLimb = false;
                break;
            case RIGHT:
            case UNSPECIFIED:
                rightLimb = true;
                break;
            default:
                // Coin toss for which limb to present first (in case we're doing both)
                rightLimb = (new Random()).nextBoolean();
                break;
        }

        for (int limb = 1; limb <= limbCount; limb++) {

            if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {

                {   // Instruction step 0

                    if (doingBoth) {
                        // Set the title and instructions based on the limb(s) selected
                        if (limb == 1) {
                            if (rightLimb) {
                                String title = context.getString(R.string.rsb_RANGE_OF_MOTION_TITLE);
                                String text = String.format("%1$s\n\n%2$s",
                                        context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_0_RIGHT_FIRST),
                                        context.getString(R.string.rsb_RANGE_OF_MOTION_SOUND)); // different instructions for right being first
                                InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithLimbId(
                                        Instruction0StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                                instructionStep0.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                                stepList.add(instructionStep0);
                            } else { // left limb
                                String title = context.getString(R.string.rsb_RANGE_OF_MOTION_TITLE);
                                String text = String.format("%1$s\n\n%2$s",
                                        context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_0_LEFT_FIRST),
                                        context.getString(R.string.rsb_RANGE_OF_MOTION_SOUND)); // different instructions for left being first
                                InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithLimbId(
                                        Instruction0StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                                instructionStep0.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                                stepList.add(instructionStep0);
                            }
                        } else { // limb == 2
                            if (rightLimb) {
                                String title = context.getString(R.string.rsb_RANGE_OF_MOTION_TITLE);
                                String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_0_RIGHT_SECOND); // different instructions for right being second
                                InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithLimbId(
                                        Instruction0StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                                stepList.add(instructionStep0);
                            } else {
                                String title = context.getString(R.string.rsb_RANGE_OF_MOTION_TITLE);
                                String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_0_LEFT_SECOND); // different instructions for left being first
                                InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithLimbId(
                                        Instruction0StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                                stepList.add(instructionStep0);
                            }
                        }
                    } else { // not doing both
                        if (rightLimb) {
                            String title = context.getString(R.string.rsb_RANGE_OF_MOTION_TITLE);
                            String text = String.format("%1$s\n\n%2$s",
                                    context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_0_RIGHT),
                                    context.getString(R.string.rsb_RANGE_OF_MOTION_SOUND));
                            InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction0StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                            instructionStep0.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                            stepList.add(instructionStep0);
                        } else {
                            String title = context.getString(R.string.rsb_RANGE_OF_MOTION_TITLE);
                            String text = String.format("%1$s\n\n%2$s",
                                    context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_0_LEFT),
                                    context.getString(R.string.rsb_RANGE_OF_MOTION_SOUND));
                            InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction0StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                            instructionStep0.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                            stepList.add(instructionStep0);
                        }
                    }
                }

                {   // Instruction step 1

                    if (limb == 1) {
                        if (rightLimb) {
                            String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_RIGHT);
                            String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_1_RIGHT);
                            InstructionStep instructionStep1 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction1StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                            stepList.add(instructionStep1);
                        } else {
                            String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_LEFT);
                            String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_1_LEFT);
                            InstructionStep instructionStep1 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction1StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                            stepList.add(instructionStep1);
                        }
                    } else { // limb == 2
                        if (rightLimb) {
                            String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_RIGHT);
                            String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_1_RIGHT_SECOND);
                            InstructionStep instructionStep1 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction1StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                            stepList.add(instructionStep1);
                        } else {
                            String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_LEFT);
                            String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_1_LEFT_SECOND);
                            InstructionStep instructionStep1 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction1StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                            stepList.add(instructionStep1);
                        }
                    }
                }

                {   // Instruction step 2

                    if (limb == 1) {
                        if (rightLimb) {
                            String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_RIGHT);
                            String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_2_RIGHT);
                            InstructionStep instructionStep2 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction2StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                            instructionStep2.setImage(ResUtils.RangeOfMotion.KNEE_START_RIGHT);
                            stepList.add(instructionStep2);
                        } else {
                            String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_LEFT);
                            String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_2_LEFT);
                            InstructionStep instructionStep2 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction2StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                            instructionStep2.setImage(ResUtils.RangeOfMotion.KNEE_START_LEFT);
                            stepList.add(instructionStep2);
                        }
                    } else {
                        if (rightLimb) {
                            String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_RIGHT);
                            String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_2_RIGHT);
                            InstructionStep instructionStep2 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction2StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                            instructionStep2.setImage(ResUtils.RangeOfMotion.KNEE_START_RIGHT);
                            stepList.add(instructionStep2);
                        } else {
                            String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_LEFT);
                            String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_2_LEFT);
                            InstructionStep instructionStep2 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction2StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                            instructionStep2.setImage(ResUtils.RangeOfMotion.KNEE_START_LEFT);
                            stepList.add(instructionStep2);
                        }
                    }
                }

                {   // Instruction step 3

                    if (limb == 1) {
                        if (rightLimb) {
                            String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_RIGHT);
                            String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_3_RIGHT);
                            InstructionStep instructionStep3 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction3StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                            instructionStep3.setImage(ResUtils.RangeOfMotion.KNEE_MAXIMUM_RIGHT);
                            stepList.add(instructionStep3);
                        } else {
                            String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_LEFT);
                            String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_3_LEFT);
                            InstructionStep instructionStep3 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction3StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                            instructionStep3.setImage(ResUtils.RangeOfMotion.KNEE_MAXIMUM_LEFT);
                            stepList.add(instructionStep3);
                        }
                    } else {
                        if (rightLimb) {
                            String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_RIGHT);
                            String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_3_RIGHT);
                            InstructionStep instructionStep3 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction3StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                            instructionStep3.setImage(ResUtils.RangeOfMotion.KNEE_MAXIMUM_RIGHT);
                            stepList.add(instructionStep3);
                        } else {
                            String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_LEFT);
                            String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_3_LEFT);
                            InstructionStep instructionStep3 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction3StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                            instructionStep3.setImage(ResUtils.RangeOfMotion.KNEE_MAXIMUM_LEFT);
                            stepList.add(instructionStep3);
                        }
                    }
                }
            }   // end of instructions

            {   // Touch anywhere step

                if (rightLimb) {
                    String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_RIGHT);
                    String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TOUCH_ANYWHERE_STEP_INSTRUCTION_RIGHT);
                    TouchAnywhereStep touchAnywhereStep = new TouchAnywhereStep(stepIdentifierWithLimbId(
                            TouchAnywhereStepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                    touchAnywhereStep.setSpokenInstruction(text);
                    stepList.add(touchAnywhereStep);
                } else {
                    String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_LEFT);
                    String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TOUCH_ANYWHERE_STEP_INSTRUCTION_LEFT);
                    TouchAnywhereStep touchAnywhereStep = new TouchAnywhereStep(stepIdentifierWithLimbId(
                            TouchAnywhereStepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                    touchAnywhereStep.setSpokenInstruction(text);
                    stepList.add(touchAnywhereStep);
                }

            }

            {   // Range of motion step

                List<RecorderConfig> recorderConfigList = new ArrayList<>();

                if (!optionList.contains(TaskExcludeOption.ACCELEROMETER)) {
                    recorderConfigList.add(new AccelerometerRecorderConfig(AccelerometerRecorderIdentifier, sensorFreq));
                }

                if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                    recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                }

                if (rightLimb) {
                    String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_RIGHT);
                    String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_SPOKEN_INSTRUCTION_RIGHT);
                    RangeOfMotionStep rangeOfMotionStep = new RangeOfMotionStep(stepIdentifierWithLimbId(
                            RangeOfMotionStepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                    rangeOfMotionStep.setSpokenInstruction(text);
                    rangeOfMotionStep.setRecorderConfigurationList(recorderConfigList);
                    stepList.add(rangeOfMotionStep);
                } else {
                    String title = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_TITLE_LEFT);
                    String text = context.getString(R.string.rsb_KNEE_RANGE_OF_MOTION_SPOKEN_INSTRUCTION_LEFT);
                    RangeOfMotionStep rangeOfMotionStep = new RangeOfMotionStep(stepIdentifierWithLimbId(
                            RangeOfMotionStepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                    rangeOfMotionStep.setSpokenInstruction(text);
                    rangeOfMotionStep.setRecorderConfigurationList(recorderConfigList);
                    stepList.add(rangeOfMotionStep);
                }
            }
            // Flip to the other limb if doing both (ignored if limbCount == 1)
            rightLimb = !rightLimb;
        }
        // Conclusion step
        if (!optionList.contains(TaskExcludeOption.CONCLUSION)) {
            stepList.add(TaskFactory.makeCompletionStep(context));
        }
        return new OrderedTask(identifier, stepList);
    }


    /**
     * Returns a predefined task that measures the range of motion for a left shoulder, a right shoulder, or both shoulders.
     * <p>
     * The data collected by this task is device motion data.
     *
     * @param context                   can be app or activity, used for resources
     * @param identifier                The task identifier to use for this task, appropriate to the study.
     * @param limbOption                The limb in which ROM is being measured.
     * @param intendedUseDescription    A localized string describing the intended use of the data
     *                                  collected. If the value of this parameter is `nil`, the default
     *                                  localized text is displayed.
     * @param optionList                Options that affect the features of the predefined task.
     * @return                          An active range of motion task that can be presented with an
     *                                  `ActiveTaskActivity` object.
     */
    public static OrderedTask shoulderRangeOfMotionTask(
            Context context,
            String identifier,
            String intendedUseDescription,
            TaskOptions.Limb limbOption,
            List<TaskExcludeOption> optionList)
    {
        List<Step> stepList = new ArrayList<>();

        // Obtain sensor frequency for Range of Motion Task recorders
        double sensorFreq = context.getResources().getInteger(R.integer.rsb_sensor_frequency_range_of_motion_task);


        // Setup which image sets (hands or feet) to start with and how many image sets (1 or both) to add, based on the imageOption parameter. If both image sets are selected, the order is randomly allocated
        int limbCount = (limbOption == BOTH) ? 2 : 1;
        boolean doingBoth = (limbCount == 2);
        boolean rightLimb;

        switch (limbOption) {
            case LEFT:
                rightLimb = false;
                break;
            case RIGHT:
            case UNSPECIFIED:
                rightLimb = true;
                break;
            default:
                // Coin toss for which limb to present first (in case we're doing both)
                rightLimb = (new Random()).nextBoolean();
                break;
        }

        for (int limb = 1; limb <= limbCount; limb++) {

            if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {

                {   // Instruction step 0

                    if (doingBoth) {
                        // Set the title and instructions based on the limb(s) selected
                        if (limb == 1) {
                            if (rightLimb) {
                                String title = context.getString(R.string.rsb_RANGE_OF_MOTION_TITLE);
                                String text = String.format("%1$s\n\n%2$s",
                                        context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_0_RIGHT_FIRST),
                                        context.getString(R.string.rsb_RANGE_OF_MOTION_SOUND)); // different instructions for right being first
                                InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithLimbId(
                                        Instruction0StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                                instructionStep0.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                                stepList.add(instructionStep0);
                            } else { // left limb
                                String title = context.getString(R.string.rsb_RANGE_OF_MOTION_TITLE);
                                String text = String.format("%1$s\n\n%2$s",
                                        context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_0_LEFT_FIRST),
                                        context.getString(R.string.rsb_RANGE_OF_MOTION_SOUND)); // different instructions for left being first
                                InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithLimbId(
                                        Instruction0StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                                instructionStep0.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                                stepList.add(instructionStep0);
                            }
                        } else { // limb == 2
                            if (rightLimb) {
                                String title = context.getString(R.string.rsb_RANGE_OF_MOTION_TITLE);
                                String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_0_RIGHT_SECOND); // different instructions for right being second
                                InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithLimbId(
                                        Instruction0StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                                stepList.add(instructionStep0);
                            } else {
                                String title = context.getString(R.string.rsb_RANGE_OF_MOTION_TITLE);
                                String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_0_LEFT_SECOND); // different instructions for left being first
                                InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithLimbId(
                                        Instruction0StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                                stepList.add(instructionStep0);
                            }
                        }
                    } else { // not doing both
                        if (rightLimb) {
                            String title = context.getString(R.string.rsb_RANGE_OF_MOTION_TITLE);
                            String text = String.format("%1$s\n\n%2$s",
                                    context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_0_RIGHT),
                                    context.getString(R.string.rsb_RANGE_OF_MOTION_SOUND));
                            InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction0StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                            instructionStep0.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                            stepList.add(instructionStep0);
                        } else {
                            String title = context.getString(R.string.rsb_RANGE_OF_MOTION_TITLE);
                            String text = String.format("%1$s\n\n%2$s",
                                    context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_0_LEFT),
                                    context.getString(R.string.rsb_RANGE_OF_MOTION_SOUND));
                            InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction0StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                            instructionStep0.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                            stepList.add(instructionStep0);
                        }
                    }
                }

                {   // Instruction step 1

                    if (limb == 1) {
                        if (rightLimb) {
                            String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_RIGHT);
                            String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_1_RIGHT);
                            InstructionStep instructionStep1 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction1StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                            stepList.add(instructionStep1);
                        } else {
                            String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_LEFT);
                            String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_1_LEFT);
                            InstructionStep instructionStep1 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction1StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                            stepList.add(instructionStep1);
                        }
                    } else { // limb == 2
                        if (rightLimb) {
                            String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_RIGHT);
                            String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_1_RIGHT_SECOND);
                            InstructionStep instructionStep1 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction1StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                            stepList.add(instructionStep1);
                        } else {
                            String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_LEFT);
                            String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_1_LEFT_SECOND);
                            InstructionStep instructionStep1 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction1StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                            stepList.add(instructionStep1);
                        }
                    }
                }

                {   // Instruction step 2

                    if (limb == 1) {
                        if (rightLimb) {
                            String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_RIGHT);
                            String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_2_RIGHT);
                            InstructionStep instructionStep2 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction2StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                            instructionStep2.setImage(ResUtils.RangeOfMotion.SHOULDER_START_RIGHT);
                            stepList.add(instructionStep2);
                        } else {
                            String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_LEFT);
                            String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_2_LEFT);
                            InstructionStep instructionStep2 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction2StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                            instructionStep2.setImage(ResUtils.RangeOfMotion.SHOULDER_START_LEFT);
                            stepList.add(instructionStep2);
                        }
                    } else {
                        if (rightLimb) {
                            String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_RIGHT);
                            String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_2_RIGHT);
                            InstructionStep instructionStep2 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction2StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                            instructionStep2.setImage(ResUtils.RangeOfMotion.SHOULDER_START_RIGHT);
                            stepList.add(instructionStep2);
                        } else {
                            String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_LEFT);
                            String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_2_LEFT);
                            InstructionStep instructionStep2 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction2StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                            instructionStep2.setImage(ResUtils.RangeOfMotion.SHOULDER_START_LEFT);
                            stepList.add(instructionStep2);
                        }
                    }
                }

                {   // Instruction step 3

                    if (limb == 1) {
                        if (rightLimb) {
                            String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_RIGHT);
                            String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_3_RIGHT);
                            InstructionStep instructionStep3 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction3StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                            instructionStep3.setImage(ResUtils.RangeOfMotion.SHOULDER_MAXIMUM_RIGHT);
                            stepList.add(instructionStep3);
                        } else {
                            String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_LEFT);
                            String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_3_LEFT);
                            InstructionStep instructionStep3 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction3StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                            instructionStep3.setImage(ResUtils.RangeOfMotion.SHOULDER_MAXIMUM_LEFT);
                            stepList.add(instructionStep3);
                        }
                    } else {
                        if (rightLimb) {
                            String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_RIGHT);
                            String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_3_RIGHT);
                            InstructionStep instructionStep3 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction3StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                            instructionStep3.setImage(ResUtils.RangeOfMotion.SHOULDER_MAXIMUM_RIGHT);
                            stepList.add(instructionStep3);
                        } else {
                            String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_LEFT);
                            String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TEXT_INSTRUCTION_3_LEFT);
                            InstructionStep instructionStep3 = new InstructionStep(stepIdentifierWithLimbId(
                                    Instruction3StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                            instructionStep3.setImage(ResUtils.RangeOfMotion.SHOULDER_MAXIMUM_LEFT);
                            stepList.add(instructionStep3);
                        }
                    }
                }
            }   // end of instructions

            {   // Touch anywhere step

                if (rightLimb) {
                    String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_RIGHT);
                    String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TOUCH_ANYWHERE_STEP_INSTRUCTION_RIGHT);
                    TouchAnywhereStep touchAnywhereStep = new TouchAnywhereStep(stepIdentifierWithLimbId(
                            TouchAnywhereStepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                    touchAnywhereStep.setSpokenInstruction(text);
                    stepList.add(touchAnywhereStep);
                } else {
                    String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_LEFT);
                    String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TOUCH_ANYWHERE_STEP_INSTRUCTION_LEFT);
                    TouchAnywhereStep touchAnywhereStep = new TouchAnywhereStep(stepIdentifierWithLimbId(
                            TouchAnywhereStepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                    touchAnywhereStep.setSpokenInstruction(text);
                    stepList.add(touchAnywhereStep);
                }

            }

            {   // Range of motion step

                List<RecorderConfig> recorderConfigList = new ArrayList<>();

                if (!optionList.contains(TaskExcludeOption.ACCELEROMETER)) {
                    recorderConfigList.add(new AccelerometerRecorderConfig(AccelerometerRecorderIdentifier, sensorFreq));
                }

                if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                    recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                }

                if (rightLimb) {
                    String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_RIGHT);
                    String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_SPOKEN_INSTRUCTION_RIGHT);
                    RangeOfMotionStep rangeOfMotionStep = new RangeOfMotionStep(stepIdentifierWithLimbId(
                            RangeOfMotionStepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                    rangeOfMotionStep.setSpokenInstruction(text);
                    rangeOfMotionStep.setRecorderConfigurationList(recorderConfigList);
                    stepList.add(rangeOfMotionStep);
                } else {
                    String title = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_TITLE_LEFT);
                    String text = context.getString(R.string.rsb_SHOULDER_RANGE_OF_MOTION_SPOKEN_INSTRUCTION_LEFT);
                    RangeOfMotionStep rangeOfMotionStep = new RangeOfMotionStep(stepIdentifierWithLimbId(
                            RangeOfMotionStepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                    rangeOfMotionStep.setSpokenInstruction(text);
                    rangeOfMotionStep.setRecorderConfigurationList(recorderConfigList);
                    stepList.add(rangeOfMotionStep);
                }
            }
            // Flip to the other limb if doing both (ignored if limbCount == 1)
            rightLimb = !rightLimb;
        }
        // Conclusion step
        if (!optionList.contains(TaskExcludeOption.CONCLUSION)) {
            stepList.add(TaskFactory.makeCompletionStep(context));
        }
        return new OrderedTask(identifier, stepList);
    }


    public static String stepIdentifierWithLimbId(String stepId, String limbId) {
        if (limbId == null) {
            return stepId;
        }
        return String.format("%s.%s", stepId, limbId);
    }
}