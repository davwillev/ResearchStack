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
     * @param context                can be app or activity, used for resources
     * @param identifier             The task identifier to use for this task, appropriate to the study.
     * @param limbOption             The limb in which ROM is being measured.
     * @param intendedUseDescription A localized string describing the intended use of the data
     *                               collected. If the value of this parameter is `nil`, the default
     *                               localized text is displayed.
     * @param optionList             Options that affect the features of the predefined task.
     * @return                       An active range of motion task that can be presented with an
     *                               `ActiveTaskActivity` object.
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

        if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {

            if (limbOption == TaskOptions.Limb.RIGHT || limbOption == TaskOptions.Limb.BOTH) {

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, capitalize(context.getString(R.string.rsb_LIMB_RIGHT)));
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_0);
                    String text = String.format(textFormat, context.getString(R.string.rsb_LIMB_RIGHT));
                    InstructionStep step = new InstructionStep(stepIdentifierWithLimbId(
                            Instruction0StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, capitalize(context.getString(R.string.rsb_LIMB_RIGHT)));
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_1);
                    String text = String.format(textFormat, context.getString(R.string.rsb_LIMB_RIGHT));
                    InstructionStep step = new InstructionStep(stepIdentifierWithLimbId(
                            Instruction1StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                    step.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, capitalize(context.getString(R.string.rsb_LIMB_RIGHT)));
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_2);
                    String text = String.format(textFormat, context.getString(R.string.rsb_LIMB_RIGHT));
                    InstructionStep step = new InstructionStep(stepIdentifierWithLimbId(
                            Instruction2StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                    step.setImage(ResUtils.RangeOfMotion.KNEE_START_RIGHT);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, capitalize(context.getString(R.string.rsb_LIMB_RIGHT)));
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_3);
                    String text = String.format(textFormat, context.getString(R.string.rsb_LIMB_RIGHT));
                    InstructionStep step = new InstructionStep(stepIdentifierWithLimbId(
                            Instruction3StepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                    step.setImage(ResUtils.RangeOfMotion.KNEE_MAXIMUM_RIGHT);
                    stepList.add(step);
                }

                /* When this next step (TouchAnywhereStep) begins, the spoken instruction commences
                automatically. Touching the screen ends the step and the next step begins. */

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, capitalize(context.getString(R.string.rsb_LIMB_RIGHT)));
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_touch_anywhere_step_instruction);
                    String text = String.format(textFormat, context.getString(R.string.rsb_LIMB_RIGHT));
                    TouchAnywhereStep step = new TouchAnywhereStep(stepIdentifierWithLimbId(
                            TouchAnywhereStepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                    step.setSpokenInstruction(text);
                    stepList.add(step);
                }

                /* When the RangeOfMotionStep begins, the spoken instruction commences automatically and device motion recording
                begins. Touching the screen ends the step and recording of device motion, and the next step begins */

                {
                    List<RecorderConfig> recorderConfigList = new ArrayList<>();

                    if (!optionList.contains(TaskExcludeOption.ACCELEROMETER)) {
                        recorderConfigList.add(new AccelerometerRecorderConfig(AccelerometerRecorderIdentifier, sensorFreq));
                    }

                    if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                        recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                    }

                    {
                        String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                        String title = String.format(titleFormat, capitalize(context.getString(R.string.rsb_LIMB_RIGHT)));
                        String textFormat = context.getString(R.string.rsb_knee_range_of_motion_spoken_instruction);
                        String text = String.format(textFormat, context.getString(R.string.rsb_LIMB_RIGHT));
                        RangeOfMotionStep step = new RangeOfMotionStep(stepIdentifierWithLimbId(
                                RangeOfMotionStepIdentifier, ActiveTaskRightLimbIdentifier), title, text);
                        step.setSpokenInstruction(text);
                        step.setRecorderConfigurationList(recorderConfigList);
                        stepList.add(step);
                    }
                }
            }

            if (limbOption == TaskOptions.Limb.LEFT || limbOption == TaskOptions.Limb.BOTH) {

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, capitalize(context.getString(R.string.rsb_LIMB_LEFT)));
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_0);
                    String text = String.format(textFormat, context.getString(R.string.rsb_LIMB_LEFT));
                    InstructionStep step = new InstructionStep(stepIdentifierWithLimbId(
                            Instruction0StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, capitalize(context.getString(R.string.rsb_LIMB_LEFT)));
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_1);
                    String text = String.format(textFormat, context.getString(R.string.rsb_LIMB_LEFT));
                    InstructionStep step = new InstructionStep(stepIdentifierWithLimbId(
                            Instruction1StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                    step.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, capitalize(context.getString(R.string.rsb_LIMB_LEFT)));
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_2);
                    String text = String.format(textFormat, context.getString(R.string.rsb_LIMB_LEFT));
                    InstructionStep step = new InstructionStep(stepIdentifierWithLimbId(
                            Instruction2StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                    step.setImage(ResUtils.RangeOfMotion.KNEE_START_LEFT);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, capitalize(context.getString(R.string.rsb_LIMB_LEFT)));
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_3);
                    String text = String.format(textFormat, context.getString(R.string.rsb_LIMB_LEFT));
                    InstructionStep step = new InstructionStep(stepIdentifierWithLimbId(
                            Instruction3StepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                    step.setImage(ResUtils.RangeOfMotion.KNEE_MAXIMUM_LEFT);
                    stepList.add(step);
                }

                /* When this next step (TouchAnywhereStep) begins, the spoken instruction commences
                automatically. Touching the screen ends the step and the next step begins. */

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, capitalize(context.getString(R.string.rsb_LIMB_LEFT)));
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_touch_anywhere_step_instruction);
                    String text = String.format(textFormat, context.getString(R.string.rsb_LIMB_LEFT));
                    TouchAnywhereStep step = new TouchAnywhereStep(stepIdentifierWithLimbId(
                            TouchAnywhereStepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                    step.setSpokenInstruction(text);
                    stepList.add(step);
                }

                /* When the RangeOfMotionStep begins, the spoken instruction commences automatically and device motion recording
                begins. Touching the screen ends the step and recording of device motion, and the next step begins */

                {
                    List<RecorderConfig> recorderConfigList = new ArrayList<>();

                    if (!optionList.contains(TaskExcludeOption.ACCELEROMETER)) {
                        recorderConfigList.add(new AccelerometerRecorderConfig(AccelerometerRecorderIdentifier, sensorFreq));
                    }

                    if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                        recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                    }

                    {
                        String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                        String title = String.format(titleFormat, capitalize(context.getString(R.string.rsb_LIMB_LEFT)));
                        String textFormat = context.getString(R.string.rsb_knee_range_of_motion_spoken_instruction);
                        String text = String.format(textFormat, context.getString(R.string.rsb_LIMB_LEFT));
                        RangeOfMotionStep step = new RangeOfMotionStep(stepIdentifierWithLimbId(
                                RangeOfMotionStepIdentifier, ActiveTaskLeftLimbIdentifier), title, text);
                        step.setSpokenInstruction(text);
                        step.setRecorderConfigurationList(recorderConfigList);
                        stepList.add(step);
                    }
                }

                if (!optionList.contains(TaskExcludeOption.CONCLUSION)) {
                    stepList.add(TaskFactory.makeCompletionStep(context));
                }
            }
        }
        return new OrderedTask(identifier, stepList);
    }

    public static String stepIdentifierWithLimbId(String stepId, String limbId) {
        if (limbId == null) {
            return stepId;
        }
        return String.format("%s.%s", stepId, limbId);
    }

    private static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}