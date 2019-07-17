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
 * Created by David Evans, Laurence Hurst, Simon Hartley, 2019.
 *
 * In iOS's ResearchKit, they included static methods for building OrderedTasks in the OrderedTask
 * class. However, this class was created to extend the Range of Motion (ROM) Tasks,
 * specifically creating the Forward Bending ROM task.
 */

public class ForwardBendingRangeOfMotionTaskFactory {

    public static final String ForwardBendingRangeOfMotionStepIdentifier = "forwardBendingRangeOfMotion";

    /**
     * Returns a predefined task that measures the range of motion during forward bending, with the device held against the chest with the left hand, right hand, or both hands.
     * <p>
     * The data collected by this task is device motion data.
     *
     * @param context                can be app or activity, used for resources
     * @param identifier             The task identifier to use for this task, appropriate to the study.
     * @param sideOption             The hand by which the device is held against the chest during the task.
     * @param intendedUseDescription A localized string describing the intended use of the data
     *                               collected. If the value of this parameter is `nil`, the default
     *                               localized text is displayed.
     * @param optionList             Options that affect the features of the predefined task.
     * @return                       An active range of motion task that can be presented with an
     *                               `ActiveTaskActivity` object.
     */

    public static OrderedTask forwardBendingRangeOfMotionTask(
            Context context,
            String identifier,
            String intendedUseDescription,
            TaskOptions.Side sideOption,
            List<TaskExcludeOption> optionList)
    {
        List<Step> stepList = new ArrayList<>();

        // Obtain sensor frequency for Range of Motion Task recorders
        double sensorFreq = context.getResources().getInteger(R.integer.rsb_sensor_frequency_range_of_motion_task);

        if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {

            if (sideOption == TaskOptions.Side.RIGHT || sideOption == TaskOptions.Side.BOTH) {

                {
                    String titleFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_title);
                    String title = String.format(titleFormat);
                    String textFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_text_instruction_0);
                    String text = String.format(textFormat);
                    InstructionStep step = new InstructionStep(Instruction0StepIdentifier, title, text);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_title);
                    String title = String.format(titleFormat);
                    String textFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_text_instruction_1);
                    String text = String.format(textFormat);
                    InstructionStep step = new InstructionStep(Instruction1StepIdentifier, title, text);
                    step.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_title);
                    String title = String.format(titleFormat);
                    String textFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_text_instruction_2);
                    String text = String.format(textFormat, TaskOptions.Side.RIGHT);
                    InstructionStep step = new InstructionStep(Instruction2StepIdentifier, title, text);
                    step.setImage(ResUtils.RangeOfMotion.FORWARD_BENDING_START_RIGHT);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_title);
                    String title = String.format(titleFormat);
                    String textFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_text_instruction_3);
                    String text = String.format(textFormat, TaskOptions.Side.RIGHT, TaskOptions.Side.LEFT);
                    InstructionStep step = new InstructionStep(Instruction3StepIdentifier, title, text);
                    step.setImage(ResUtils.RangeOfMotion.FORWARD_BENDING_MAXIMUM_RIGHT);
                    stepList.add(step);
                }

                /* When this next step (TouchAnywhereStep) begins, the spoken instruction commences
                automatically. Touching the screen ends the step and the next step begins. */

                {
                    String titleFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_title);
                    String title = String.format(titleFormat);
                    String textFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_touch_anywhere_step_instruction);
                    String text = String.format(textFormat, TaskOptions.Side.RIGHT);
                    TouchAnywhereStep step = new TouchAnywhereStep(TouchAnywhereStepIdentifier, title, text);
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
                        String titleFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_title);
                        String title = String.format(titleFormat);
                        String textFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_spoken_instruction);
                        String text = String.format(textFormat);
                        RangeOfMotionStep step = new RangeOfMotionStep(ForwardBendingRangeOfMotionStepIdentifier, title, text);
                        step.setSpokenInstruction(text);
                        step.setRecorderConfigurationList(recorderConfigList);
                        stepList.add(step);
                    }
                }
            }

            if (sideOption == TaskOptions.Side.LEFT || sideOption == TaskOptions.Side.BOTH) {

                {
                    String titleFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_title);
                    String title = String.format(titleFormat);
                    String textFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_text_instruction_0);
                    String text = String.format(textFormat);
                    InstructionStep step = new InstructionStep(Instruction0StepIdentifier, title, text);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_title);
                    String title = String.format(titleFormat);
                    String textFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_text_instruction_1);
                    String text = String.format(textFormat);
                    InstructionStep step = new InstructionStep(Instruction1StepIdentifier, title, text);
                    step.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_title);
                    String title = String.format(titleFormat);
                    String textFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_text_instruction_2);
                    String text = String.format(textFormat, TaskOptions.Side.LEFT);
                    InstructionStep step = new InstructionStep(Instruction2StepIdentifier, title, text);
                    step.setImage(ResUtils.RangeOfMotion.FORWARD_BENDING_START_LEFT);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_title);
                    String title = String.format(titleFormat);
                    String textFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_text_instruction_3);
                    String text = String.format(textFormat, TaskOptions.Side.LEFT, TaskOptions.Side.RIGHT);
                    InstructionStep step = new InstructionStep(Instruction3StepIdentifier, title, text);
                    step.setImage(ResUtils.RangeOfMotion.FORWARD_BENDING_MAXIMUM_LEFT);
                    stepList.add(step);
                }

                /* When this next step (TouchAnywhereStep) begins, the spoken instruction commences
                automatically. Touching the screen ends the step and the next step begins. */

                {
                    String titleFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_title);
                    String title = String.format(titleFormat);
                    String textFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_touch_anywhere_step_instruction);
                    String text = String.format(textFormat, TaskOptions.Side.LEFT);
                    TouchAnywhereStep step = new TouchAnywhereStep(TouchAnywhereStepIdentifier, title, text);
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
                        String titleFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_title);
                        String title = String.format(titleFormat);
                        String textFormat = context.getString(R.string.rsb_forward_bending_range_of_motion_spoken_instruction);
                        String text = String.format(textFormat);
                        RangeOfMotionStep step = new RangeOfMotionStep(ForwardBendingRangeOfMotionStepIdentifier, title, text);
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
}