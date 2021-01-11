//package org.researchstack.backbone.ui.step.layout;
package com.spineapp;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.LeftRightJudgementResult;
import org.researchstack.backbone.step.Step;
//import org.researchstack.backbone.step.active.TappingIntervalStep;
import org.researchstack.backbone.task.factory.HandTaskOptions;
import org.researchstack.backbone.task.factory.TaskOptions;
import org.researchstack.backbone.ui.step.layout.ActiveStepLayout;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ResUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static org.researchstack.backbone.result.LeftRightJudgementResult.TappingButtonIdentifier.TappedButtonLeft;
import static org.researchstack.backbone.result.LeftRightJudgementResult.TappingButtonIdentifier.TappedButtonNone;
import static org.researchstack.backbone.result.LeftRightJudgementResult.TappingButtonIdentifier.TappedButtonRight;

/**
 * Created by David Evans in January 2021.
 *
 * The TappingIntervalStepLayout has two buttons at the bottom of the screen that the user
 * is instructed to tap one and then the other repeatably.
 *
 * This goes on for as long as the active step desires, and collects data on the taps
 * and packages them in a TappingIntervalResult
 */

public class LeftRightJudgementStepLayout extends ActiveStepLayout {

    protected LeftRightJudgementStep leftRightJudgementStep;
    protected LeftRightJudgementResult leftRightJudgementResult;
    //protected Context context;

    protected long startTime;
    protected int tapCount;
    protected List<LeftRightJudgementResult.Sample> sampleList;

    //NSMutableArray *_results; //NSMutableArray
    private double _startTime; // NSTimeInterval
    Timer _interStimulusIntervalTimer;
    Timer _timeoutTimer;
    Timer _timeoutNotificationTimer;
    Timer _displayAnswerTimer;
    //NSArray *_imageQueue;
    private File[] fileList;
    private String[] _imagePaths;
    private int _imageCount;
    private int _leftCount;
    private int _rightCount;
    private int _leftSumCorrect;
    private int _rightSumCorrect;
    private int _timedOutCount;
    private double _percentTimedOut;
    private double _leftPercentCorrect;
    private double _rightPercentCorrect;
    private double _meanLeftDuration;
    private double _varianceLeftDuration;
    private double _stdLeftDuration;
    private double _prevMl;
    private double _newMl;
    private double _prevSl;
    private double _newSl;
    private double _meanRightDuration;
    private double _varianceRightDuration;
    private double _stdRightDuration;
    private double _prevMr;
    private double _newMr;
    private double _prevSr;
    private double _newSr;
    private boolean _match;
    private boolean _timedOut;


    private static final int LEFT_BUTTON     = 0;
    private static final int RIGHT_BUTTON    = 1;
    private static final int NO_BUTTON       = 2;
    protected LeftRightJudgementResult.Sample[] buttonSamples = new LeftRightJudgementResult.Sample[NO_BUTTON + 1];

    protected int[] lastPointerIdx = new int[NO_BUTTON + 1];
    private static final int INVALID_POINTER_IDX  = -1;

    protected RelativeLayout leftRightJudgementStepLayout;
    protected TextView leftRightJudgementTextView;
    protected FloatingActionButton leftButton;
    protected FloatingActionButton rightButton;
    private String countText;
    private String timeoutText;
    private String answerText;
    private int image;

    public LeftRightJudgementStepLayout(Context context) {
        super(context);
    }

    public LeftRightJudgementStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LeftRightJudgementStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LeftRightJudgementStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
    }

    @Override
    protected void validateStep(Step step) {
        super.validateStep(step);
        leftRightJudgementStep = (LeftRightJudgementStep) step;

        if (!(step instanceof LeftRightJudgementStep)) {
            throw new IllegalStateException("LeftRightJudgementStepLayout must have a LeftRightJudgementStep");
        }
        int minimumAttempts = 10;
        if (leftRightJudgementStep.getNumberOfAttempts() < minimumAttempts) {
            throw new IllegalStateException(String.format("number of attempts should be greater or equal to %1$s.", String.valueOf(minimumAttempts)));
        }
        if (leftRightJudgementStep.getMinimumInterStimulusInterval() <= 0) {
            throw new IllegalStateException("minimumInterStimulusInterval must be greater than zero");
        }
        if (leftRightJudgementStep.getMaximumInterStimulusInterval() < leftRightJudgementStep.getMinimumInterStimulusInterval()) {
            throw new IllegalStateException("maximumInterStimulusInterval cannot be less than minimumInterStimulusInterval");
        }
        if (leftRightJudgementStep.getTimeout() <= 0) {
            throw new IllegalStateException("timeout must be greater than zero");
        }
        if (!(leftRightJudgementStep.getImageOption().equals(TaskOptions.ImageOption.HANDS)) ||
                !(leftRightJudgementStep.getImageOption().equals(TaskOptions.ImageOption.FEET)) ||
                        !(leftRightJudgementStep.getImageOption().equals(TaskOptions.ImageOption.BOTH))) {
            throw new IllegalStateException("LEFT_RIGHT_JUDGEMENT_IMAGE_OPTION_ERROR");
        }
        if ((leftRightJudgementStep.getImageOption().equals(TaskOptions.ImageOption.HANDS) &&
                (leftRightJudgementStep.getNumberOfAttempts()) > leftRightJudgementStep.numberOfImages()))  {
            throw new IllegalStateException("Number of attempts is beyond number of available hand images");
        }
        if ((leftRightJudgementStep.getImageOption().equals(TaskOptions.ImageOption.FEET) &&
                (leftRightJudgementStep.getNumberOfAttempts() > leftRightJudgementStep.numberOfImages()))  {
            throw new IllegalStateException("Number of attempts is beyond number of available foot images");
        }
    }

    @Override
    public void setupSubmitBar() {
        super.setupSubmitBar();
        submitBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setupActiveViews() {
        super.setupActiveViews();

        remainingHeightOfContainer(new HeightCalculatedListener() {
            @Override
            public void heightCalculated(int height) {
                leftRightJudgementStepLayout = (RelativeLayout)layoutInflater.inflate(R.layout.rsb_step_layout_left_right_judgement, activeStepLayout, false);
                leftRightJudgementTextView = (TextView) leftRightJudgementStepLayout.findViewById(R.id.rsb_total_taps_counter);
                leftRightJudgementTextView.setText(String.format(Locale.getDefault(), "%2d", 0));
                leftButton = (FloatingActionButton) leftRightJudgementStepLayout.findViewById(R.id.rsb_tapping_interval_button_left);
                rightButton = (FloatingActionButton) leftRightJudgementStepLayout.findViewById(R.id.rsb_tapping_interval_button_right);

                progressBarHorizontal.setProgress(0);
                progressBarHorizontal.setMax(activeStep.getStepDuration());
                progressBarHorizontal.setVisibility(View.VISIBLE);

                activeStepLayout.addView(leftRightJudgementStepLayout, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, height));

                setupSampleResult();
            }
        });

        // added these
        start();
        hideImage(); // getImage() = -1;
    }

    /**
     * Should only be called after the UI has been laid out
     */
    protected void setupSampleResult() {
        sampleList = new ArrayList<>();
        tapCount = 0;
        for (int i = 0; i <= NO_BUTTON; i++) {
            lastPointerIdx[i] = INVALID_POINTER_IDX;
        }

        leftRightJudgementResult = new LeftRightJudgementResult(leftRightJudgementStep.getIdentifier());

        /*
        int[] activeStepLayoutXY = new int[2];
        activeStepLayout.getLocationOnScreen(activeStepLayoutXY);
        {
            View button = leftButton;

            button.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    button.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    int[] buttonXY = new int[2];
                    button.getLocationOnScreen(buttonXY);
                    int buttonLeft = buttonXY[0] - activeStepLayoutXY[0];
                    int buttonTop = buttonXY[1] - activeStepLayoutXY[1];
                    int buttonRight = buttonLeft + button.getWidth();
                    int buttonBottom = buttonRight + button.getHeight();
                    Rect buttonRect = new Rect(buttonLeft, buttonTop, buttonRight, buttonBottom);

                    setupTouchListener(button, LEFT_BUTTON, buttonRect, TappedButtonLeft, true);
                    leftRightJudgementResult.setButtonRect1(buttonLeft, buttonTop, button.getWidth(), button.getHeight());
                }
            });
        }

        {
            View button = rightButton;

            button.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    button.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    int[] buttonXY = new int[2];
                    button.getLocationOnScreen(buttonXY);
                    int buttonLeft = buttonXY[0] - activeStepLayoutXY[0];
                    int buttonTop = buttonXY[1] - activeStepLayoutXY[1];
                    int buttonRight = buttonLeft + button.getWidth();
                    int buttonBottom = buttonRight + button.getHeight();
                    Rect buttonRect = new Rect(buttonLeft, buttonTop, buttonRight, buttonBottom);

                    setupTouchListener(button, RIGHT_BUTTON, buttonRect, TappedButtonRight, true);
                    leftRightJudgementResult.setButtonRect2(buttonLeft, buttonTop, button.getWidth(), button.getHeight());
                }
            });
        }

        {
            View button = activeStepLayout;

            button.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    button.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    int[] buttonXY = new int[2];
                    button.getLocationOnScreen(buttonXY);
                    int buttonLeft = buttonXY[0] - activeStepLayoutXY[0];
                    int buttonTop = buttonXY[1] - activeStepLayoutXY[1];
                    int buttonRight = buttonLeft + button.getWidth();
                    int buttonBottom = buttonRight + button.getHeight();
                    Rect buttonRect = new Rect(buttonLeft, buttonTop, buttonRight, buttonBottom);

                    setupTouchListener(button, NO_BUTTON, buttonRect, TappedButtonNone, false);
                    leftRightJudgementResult.setStepViewSize(activeStepLayout.getWidth(), activeStepLayout.getHeight());
                }
            });
        }
         */
    }

    @Override
    public void doUIAnimationPerSecond() {
        super.doUIAnimationPerSecond();
        progressBarHorizontal.setProgress(progressBarHorizontal.getProgress() + 1);
    }

    //@Override
    //public void start() { // this is defined again within LRJ methods below
    //    super.start();

    //    startTime = System.currentTimeMillis();
    //    leftRightJudgementTextView.setText(String.format(Locale.getDefault(), "%2d", tapCount)); // might be useful
    //}

    protected void setupTouchListener(
            final View view,
            final int idx,
            final Rect buttonRect,
            LeftRightJudgementResult.TappingButtonIdentifier buttonId,
            boolean countsAsATap)
    {
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        // Make sure we aren't overriding another finger's down tap
                        if (lastPointerIdx[idx] == INVALID_POINTER_IDX) {
                            buttonSamples[idx] = new LeftRightJudgementResult.Sample();
                            buttonSamples[idx].setTimestamp(motionEvent.getEventTime() - startTime);
                            buttonSamples[idx].setButtonIdentifier(buttonId);
                            buttonSamples[idx].setLocation(
                                    (int)(motionEvent.getX() + buttonRect.left),
                                    (int)(motionEvent.getY() + buttonRect.top));
                            lastPointerIdx[idx] = motionEvent.getActionMasked();

                            LogExt.d(getClass(), "tap down with button idx " + idx);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL:

                        // We need to make sure the finger index matches up with the
                        // finger index that started the "down" motion event
                        boolean correctFingerForIdx =
                                motionEvent.getActionMasked() == MotionEvent.ACTION_CANCEL ||
                                (motionEvent.getActionMasked() == MotionEvent.ACTION_UP &&
                                lastPointerIdx[idx] == MotionEvent.ACTION_DOWN) ||
                                (motionEvent.getActionMasked() == MotionEvent.ACTION_POINTER_UP &&
                                lastPointerIdx[idx] == MotionEvent.ACTION_POINTER_DOWN);

                        // Make sure we have the same finger's up tap
                        if (buttonSamples[idx] != null && correctFingerForIdx) {
                            buttonSamples[idx].setDuration(motionEvent.getDownTime());
                            sampleList.add(buttonSamples[idx]);
                            buttonSamples[idx] = null;
                            lastPointerIdx[idx] = INVALID_POINTER_IDX;
                            if (countsAsATap) {
                                countATap();
                            }

                            LogExt.d(getClass(), "tap up with button idx " + idx);
                        }

                        break;
                }

                if (!countsAsATap) {
                    return true;
                } else {
                    return view.onTouchEvent(motionEvent);
                }
            }
        });
    }

    @Override
    public void stop() {
        super.stop();

        // Complete any touches that have had a down but no up
        for (int i = 0; i <= RIGHT_BUTTON; i++) {
            if (buttonSamples[i] != null) {
                buttonSamples[i].setDuration(System.currentTimeMillis() - buttonSamples[i].getTimestamp());
                sampleList.add(buttonSamples[i]);
                buttonSamples[i] = null;
            }
        }

        if (sampleList == null || sampleList.isEmpty()) {
            return;
        }

        leftRightJudgementResult.setStartDate(new Date(startTime));
        leftRightJudgementResult.setEndDate(new Date());
        leftRightJudgementResult.setSamples(sampleList);

        stepResult.getResults().put(leftRightJudgementResult.getIdentifier(), leftRightJudgementResult); // might not need this

        // remove listeners
        leftButton.setOnTouchListener(null);
        rightButton.setOnTouchListener(null);
        activeStepLayout.setOnTouchListener(null);
    }

    protected void countATap() {
        // Start official data logging with first tap on a button
        if (tapCount == 0) {
            start();
        }
        tapCount++;
        leftRightJudgementTextView.setText(String.format(Locale.getDefault(), "%2d", tapCount));
    }



    //LRJ methods

    private void setupButtons() {

        // From setupSampleResult()
        int[] activeStepLayoutXY = new int[2];
        activeStepLayout.getLocationOnScreen(activeStepLayoutXY);
        {
            View button = leftButton;

            button.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    button.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    int[] buttonXY = new int[2];
                    button.getLocationOnScreen(buttonXY);
                    int buttonLeft = buttonXY[0] - activeStepLayoutXY[0];
                    int buttonTop = buttonXY[1] - activeStepLayoutXY[1];
                    int buttonRight = buttonLeft + button.getWidth();
                    int buttonBottom = buttonRight + button.getHeight();
                    Rect buttonRect = new Rect(buttonLeft, buttonTop, buttonRight, buttonBottom);

                    setupTouchListener(button, LEFT_BUTTON, buttonRect, TappedButtonLeft, true);
                    leftRightJudgementResult.setButtonRect1(buttonLeft, buttonTop, button.getWidth(), button.getHeight());
                }
            });
        }

        {
            View button = rightButton;

            button.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    button.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    int[] buttonXY = new int[2];
                    button.getLocationOnScreen(buttonXY);
                    int buttonLeft = buttonXY[0] - activeStepLayoutXY[0];
                    int buttonTop = buttonXY[1] - activeStepLayoutXY[1];
                    int buttonRight = buttonLeft + button.getWidth();
                    int buttonBottom = buttonRight + button.getHeight();
                    Rect buttonRect = new Rect(buttonLeft, buttonTop, buttonRight, buttonBottom);

                    setupTouchListener(button, RIGHT_BUTTON, buttonRect, TappedButtonRight, true);
                    leftRightJudgementResult.setButtonRect2(buttonLeft, buttonTop, button.getWidth(), button.getHeight());
                }
            });
        }

        {
            View button = activeStepLayout;

            button.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    button.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    int[] buttonXY = new int[2];
                    button.getLocationOnScreen(buttonXY);
                    int buttonLeft = buttonXY[0] - activeStepLayoutXY[0];
                    int buttonTop = buttonXY[1] - activeStepLayoutXY[1];
                    int buttonRight = buttonLeft + button.getWidth();
                    int buttonBottom = buttonRight + button.getHeight();
                    Rect buttonRect = new Rect(buttonLeft, buttonTop, buttonRight, buttonBottom);

                    setupTouchListener(button, NO_BUTTON, buttonRect, TappedButtonNone, false);
                    leftRightJudgementResult.setStepViewSize(activeStepLayout.getWidth(), activeStepLayout.getHeight());
                }
            });
        }

        //leftRightJudgementContentView.leftButton addTarget:self
        //action:@selector(buttonPressed:)
        //forControlEvents:UIControlEventTouchUpInside];

        //[leftRightJudgementContentView.rightButton addTarget:self
        //action:@selector(buttonPressed:)
        //forControlEvents:UIControlEventTouchUpInside];

        setButtonsDisabled(); // buttons should not appear until a question starts
    }

    private void configureInstructions() {
        String instruction = null;
        Context appContext = getContext().getApplicationContext();
        if (leftRightJudgementStep.getImageOption().equals(TaskOptions.ImageOption.BOTH)) { //ORKPredefinedTaskImageOptionHands) {
            instruction = appContext.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_STEP_TEXT_HAND);
        } else if (leftRightJudgementStep.getImageOption().equals(TaskOptions.ImageOption.FEET)) {
            instruction= appContext.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_STEP_TEXT_FOOT);
        }
    //[self.activeStepView updateText:instruction];
        leftRightJudgementTextView.setText(instruction);
    }

    private void configureCountText() {
        Context appContext = getContext().getApplicationContext();
        String countText = String.format(appContext.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_IMAGE_COUNT),
                String.valueOf(_imageCount),
                String.valueOf(leftRightJudgementStep.getNumberOfAttempts()));
        setCountText(countText);
    }

    void startTimeoutTimer() {
        double timeout = leftRightJudgementStep.getTimeout();
        if (timeout > 0) {
            if (_timeoutNotificationTimer == null) {
                TimerTask timeoutNotificationTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        timeoutTimerDidFire();
                    }
                };
                _timeoutNotificationTimer.schedule(timeoutNotificationTimerTask, (long)(timeout * 1000));
            }
            //_timeoutTimer = [NSTimer scheduledTimerWithTimeInterval:timeout
            //target:self
            //selector:@selector(timeoutTimerDidFire)
            //userInfo:nil
            //repeats:NO];
        }
    }

    void displayTimeoutNotification(String sidePresented) {
        hideImage();
        setButtonsDisabled();
        Context appContext = getContext().getApplicationContext();
        String timeoutText =
                appContext.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TIMEOUT_NOTIFICATION);
        //leftRightJudgementContentView.timeoutText = timeoutText;
        setTimeoutText(timeoutText);
        if (leftRightJudgementStep.getShouldDisplayAnswer()) {
            //leftRightJudgementContentView.answerText = answerForSidePresented(sidePresented);
            setAnswerText(answerForSidePresented(sidePresented));
        }
        // initiate timer
        if (_timeoutNotificationTimer == null) {
            TimerTask timeoutNotificationTimerTask = new TimerTask() {
                @Override
                public void run() {
                    startInterStimulusInterval();
                }
            };
            _timeoutNotificationTimer.schedule(timeoutNotificationTimerTask, (long)(2.0 * 1000)); // display for 2.0 seconds
        }
        //_timeoutNotificationTimer = [NSTimer scheduledTimerWithTimeInterval:2.0
        //target:self
        //selector:@selector(startInterStimulusInterval)
        //userInfo:nil
        //repeats:NO];
    }

    String answerForSidePresented(String sidePresented) {
        hideImage();
        setButtonsDisabled();
        String answerText = null;
        Context appContext = getContext().getApplicationContext();
        if (leftRightJudgementStep.getImageOption().equals(TaskOptions.ImageOption.HANDS)) {
            if (sidePresented.equals("Left")) {
                answerText = appContext.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_ANSWER_LEFT_HAND);
            } else if (sidePresented.equals("Right")) {
                answerText = appContext.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_ANSWER_RIGHT_HAND);
            }
        } else if (leftRightJudgementStep.getImageOption().equals(TaskOptions.ImageOption.FEET)) {
            if (sidePresented.equals("Left")) {
                answerText = appContext.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_ANSWER_LEFT_FOOT);
            } else if (sidePresented.equals("Right")) {
                answerText = appContext.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_ANSWER_RIGHT_FOOT);
            }
        }
        return answerText;
    }

    private void displayAnswerWhenButtonPressed(String sidePresented, boolean match) {
        String answerText = answerForSidePresented(sidePresented);
        String text;
        Context appContext = getContext().getApplicationContext();
        if (match) {
            text = String.format("%1$s\n%2$s", appContext.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_ANSWER_CORRECT), answerText);
        } else {
            text = String.format("%1$s\n%2$s", appContext.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_ANSWER_INCORRECT), answerText);
        }
        //leftRightJudgementContentView.answerText = text;
        setAnswerText(text);

        // initiate timer
        if (_displayAnswerTimer == null) {
            TimerTask displayAnswerTimerTask = new TimerTask() {
                @Override
                public void run() {
                    startInterStimulusInterval();
                }
            };
            _displayAnswerTimer.schedule(displayAnswerTimerTask, (long)(2.0 * 1000)); // display for 2.0 seconds
        }
        //_displayAnswerTimer = [NSTimer scheduledTimerWithTimeInterval:2.0
        //target:self
        //selector:@selector(startInterStimulusInterval)
        //userInfo:nil
        //repeats:NO];
    }

    private void startInterStimulusInterval() {
        stopTimer(_timeoutNotificationTimer); //[_timeoutNotificationTimer invalidate];
        stopTimer(_displayAnswerTimer); //[_displayAnswerTimer invalidate];
        hideImage();
        hideCountText();
        hideTimeoutText();
        hideAnswerText();
        double interStimulusInterval = interStimulusInterval();
        // initiate timer
        if (interStimulusInterval > 0) {
            if (_interStimulusIntervalTimer == null) {
                TimerTask interStimulusIntervalTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        startNextQuestionOrFinish();
                    }
                };
                _interStimulusIntervalTimer.schedule(interStimulusIntervalTimerTask, (long)(interStimulusInterval * 1000));
            }
        }
        //_interStimulusIntervalTimer = [NSTimer scheduledTimerWithTimeInterval:[self interStimulusInterval]
        //target:self
        //selector:@selector(startNextQuestionOrFinish)
        //userInfo:nil
        //repeats:NO];
    }

    private double interStimulusInterval() {
        double timeInterval;
        //ORKLeftRightJudgementStep *step = [self leftRightJudgementStep];
        double range = leftRightJudgementStep.getMaximumInterStimulusInterval() - leftRightJudgementStep.getMinimumInterStimulusInterval();
        Random rand = new Random();
        double randomFactor = rand.nextInt((int)(range * 1000)) + 1; // non-zero random number of milliseconds between min/max limits
        // double randomFactor = (arc4random_uniform(range * 1000) + 1); // non-zero random number of milliseconds between min/max limits
        if (range == 0 || leftRightJudgementStep.getMaximumInterStimulusInterval() == leftRightJudgementStep.getMinimumInterStimulusInterval() ||
                _imageCount == leftRightJudgementStep.getNumberOfAttempts()) { // use min interval after last image of set
            timeInterval = leftRightJudgementStep.getMinimumInterStimulusInterval();
        } else {
            timeInterval = (randomFactor / 1000) + leftRightJudgementStep.getMinimumInterStimulusInterval(); // in seconds
        }
        return timeInterval;
    }

    private void buttonPressed(sender) {
        //if (!(leftRightJudgementContentView.imageToDisplay == [UIImage imageNamed:""])) {
        if ((getImage() == -1)) { // no image allocated
        setButtonsDisabled();
            stopTimer(_timeoutTimer); //[_timeoutTimer invalidate];
            _timedOut = false;
            double duration = reactionTime();
            String next = nextFileNameInQueue();
            String sidePresented = sidePresented();
            String view = viewPresented();
            String orientation = orientationPresented();
            int rotation = rotationPresented();
            // evaluate matches according to button pressed
            String sideSelected;
            if (sender == leftRightJudgementContentView.leftButton) {
                sideSelected = "Left";
                _match = sidePresented.equals(sideSelected);
                _leftSumCorrect = (_match) ? _leftSumCorrect + 1 : _leftSumCorrect;
            calculateMeanAndStdReactionTimes(sidePresented, duration, _match);
            calculatePercentagesForSides(sidePresented, _timedOut);
            createResultfromImage(next,view, rotation, orientation, _match, sidePresented, sideSelected, duration);
            }
            else if (sender == leftRightJudgementContentView.rightButton) {
                sideSelected = "Right";
                _match = sidePresented.equals(sideSelected);
                _rightSumCorrect = (_match) ? _rightSumCorrect + 1 : _rightSumCorrect;
            calculateMeanAndStdReactionTimes(sidePresented, duration, _match);
            calculatePercentagesForSides(sidePresented, _timedOut);
            createResultfromImage(next, view, rotation, orientation, _match, sidePresented, sideSelected, duration);
            }
            if (leftRightJudgementStep.getShouldDisplayAnswer()) {
            displayAnswerWhenButtonPressed(sidePresented, _match);
            } else {
            startInterStimulusInterval();
            }
        }
    }

    //private void viewDidAppear(boolean animated) { // need to find equivalent methods
    //    super viewDidAppear(animated);
    //    start();
    //    hideImage(); // getImage() = -1;
    //}

    //private void viewWillDisappear(boolean animated){ // need to find equivalent method
    //    super viewWillDisappear(animated);
    //}

    //private void stepDidFinish() { // need to find equivalent methods
    //    super stepDidFinish();
    //    stop(); //leftRightJudgementContentView.finishStep();
        // skip() for iOS goForward() ?
    //}

    private void setButtonsDisabled() {
        leftButton.setEnabled(false); // [leftRightJudgementContentView.leftButton setEnabled: NO];
        rightButton.setEnabled(false); // [leftRightJudgementContentView.rightButton setEnabled: NO];
    }

    private void setButtonsEnabled() {
        leftButton.setEnabled(true); // [leftRightJudgementContentView.leftButton setEnabled: YES];
        rightButton.setEnabled(true); //[leftRightJudgementContentView.rightButton setEnabled: YES];
    }

    @Override
    public void start() { // was previously defined in tapping task methods above
        super.start();
        startInterStimulusInterval();
        hideImage();
    }

    private void startQuestion() {
        //UIImage *image = nextImageInQueue();
        int image = nextImageInQueue();
        //leftRightJudgementContentView.imageToDisplay = image;
        setImage(image);
        if (_imageCount == 1) {
            setupButtons();
        }
        setButtonsEnabled();
        configureCountText();
        _startTime = System.currentTimeMillis();
        startTimeoutTimer();
    }

    private void stopTimer(Timer timer){
        if(timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    public String getCountText() {
        return countText;
    }

    private void setCountText(String countText) {
        this.countText = countText;
    }

    private void hideCountText() {
        setCountText(" ");
    }

    public String getTimeoutText() {
        return timeoutText;
    }

    public void setTimeoutText(String timeoutText) {
        this.timeoutText = timeoutText;
    }

    private void hideTimeoutText() {
        setTimeoutText(" ");
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    private void hideAnswerText() {
        setAnswerText(" ");
    }

    private void hideImage() {
        image = -1; // allocate no array element
        //leftRightJudgementContentView.imageToDisplay = [UIImage imageNamed:@""];
        setImage(image);
    }

    private void setImage(int image) {
    // from ActiveStepLayout
        imageView = contentContainer.findViewById(R.id.rsb_image_view);
        if (imageView != null) {
            if (activeStep.getImageResName() != null) {
                int drawableInt = ResUtils.getDrawableResourceId(getContext(), activeStep.getImageResName());
                if (drawableInt != 0) {
                    imageView.setImageResource(drawableInt);
                    imageView.setVisibility(View.VISIBLE);
                }
            } else {
                imageView.setVisibility(View.GONE);
            }
        }
        // my effort
        if (imageView == null) {
            ImageView imageView = new ImageView(getContext());
        }
        this.image = image; // used to pass to getter
        imageView.setImageResource(image);
        //[self addSubview:_imageView];
    }

    public int getImage() { // used to evaluate
        return image;
    }

    /* Results */

    private String sidePresented() {
        String fileName = nextFileNameInQueue();
        String sidePresented = null;
        if (fileName.contains("LH") || fileName.contains("LF")) {
            sidePresented = "Left";
            _leftCount ++;
        } else if (fileName.contains("RH") || fileName.contains("RF")) {
            sidePresented = "Right";
            _rightCount ++;
        }
        return sidePresented;
    }

    private String viewPresented() {
        String fileName = nextFileNameInQueue();
        String anglePresented = null;
        if (leftRightJudgementStep.getImageOption().equals(TaskOptions.ImageOption.HANDS)) {
            if (fileName.contains("LH1") ||
            fileName.contains("RH1")) {
                anglePresented = "Back";
            } else if (fileName.contains("LH2") ||
                   fileName.contains("RH2")) {
                anglePresented = "Palm";
            } else if (fileName.contains("LH3") ||
                   fileName.contains("RH3")) {
                anglePresented = "Pinkie";
            } else if (fileName.contains("LH4") ||
                   fileName.contains("RH4")) {
                anglePresented = "Thumb";
            } else if (fileName.contains("LH5") ||
                   fileName.contains("RH5")) {
                anglePresented = "Wrist";
            }
        } else if (leftRightJudgementStep.getImageOption().equals(TaskOptions.ImageOption.FEET)) {
            if (fileName.contains("LF1") ||
            fileName.contains("RF1")) {
                anglePresented = "Top";
            } else if (fileName.contains("LF2") ||
                   fileName.contains("RF2")) {
                anglePresented = "Sole";
            } else if (fileName.contains("LF3") ||
                   fileName.contains("RF3")) {
                anglePresented = "Heel";
            } else if (fileName.contains("LF4") ||
                   fileName.contains("RF4")) {
                anglePresented = "Toes";
            } else if (fileName.contains("LF5") ||
                   fileName.contains("RF5")) {
                anglePresented = "Inside";
            } else if (fileName.contains("LF6") ||
                   fileName.contains("RF6")) {
                anglePresented = "Outside";
            }
        }
        return anglePresented;
    }

    private String orientationPresented() {
        String fileName = nextFileNameInQueue();
        String anglePresented = null;
        String viewPresented = viewPresented();
        if (leftRightJudgementStep.getImageOption().equals(TaskOptions.ImageOption.HANDS)) {
            if (fileName.contains("LH")) { // left hand
                if (viewPresented.equals("Back") ||
                viewPresented.equals("Palm") ||
                viewPresented.equals("Pinkie") ||
                viewPresented.equals("Thumb")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                               fileName.contains("060cw") ||
                               fileName.contains("090cw") ||
                               fileName.contains("120cw") ||
                               fileName.contains("150cw")) {
                        anglePresented = "Medial";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                               fileName.contains("240cw") ||
                               fileName.contains("270cw") ||
                               fileName.contains("300cw") ||
                               fileName.contains("330cw")) {
                        anglePresented = "Lateral";
                    }
                } else if (viewPresented.equals("Wrist")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                           fileName.contains("060cw") ||
                           fileName.contains("090cw") ||
                           fileName.contains("120cw") ||
                           fileName.contains("150cw")) {
                        anglePresented = "Lateral";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                           fileName.contains("240cw") ||
                           fileName.contains("270cw") ||
                           fileName.contains("300cw") ||
                           fileName.contains("330cw")) {
                        anglePresented = "Medial";
                    }
                }
            } else if (fileName.contains("RH")) { // right hand
                if (viewPresented.equals("Back") ||
                viewPresented.equals("Palm") ||
                viewPresented.equals("Pinkie") ||
                viewPresented.equals("Thumb")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                           fileName.contains("060cw") ||
                           fileName.contains("090cw") ||
                           fileName.contains("120cw") ||
                           fileName.contains("150cw")) {
                        anglePresented = "Lateral";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                           fileName.contains("240cw") ||
                           fileName.contains("270cw") ||
                           fileName.contains("300cw") ||
                           fileName.contains("330cw")) {
                        anglePresented = "Medial";
                    }
                } else if (viewPresented.equals("Wrist")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                           fileName.contains("060cw") ||
                           fileName.contains("090cw") ||
                           fileName.contains("120cw") ||
                           fileName.contains("150cw")) {
                        anglePresented = "Medial";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                           fileName.contains("240cw") ||
                           fileName.contains("270cw") ||
                           fileName.contains("300cw") ||
                           fileName.contains("330cw")) {
                        anglePresented = "Lateral";
                    }
                }
            }
        } else if (leftRightJudgementStep.getImageOption().equals(TaskOptions.ImageOption.FEET)) {
            if (fileName.contains("LF")) { // left foot
                if (viewPresented.equals("Top") ||
                viewPresented.equals("Heel")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                               fileName.contains("060cw") ||
                               fileName.contains("090cw") ||
                               fileName.contains("120cw") ||
                               fileName.contains("150cw")) {
                        anglePresented = "Medial";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                               fileName.contains("240cw") ||
                               fileName.contains("270cw") ||
                               fileName.contains("300cw") ||
                               fileName.contains("330cw")) {
                        anglePresented = "Lateral";
                    }
                } else if (viewPresented.equals("Sole") ||
                       viewPresented.equals("Toes") ||
                       viewPresented.equals("Inside") ||
                       viewPresented.equals("Outside")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                           fileName.contains("060cw") ||
                           fileName.contains("090cw") ||
                           fileName.contains("120cw") ||
                           fileName.contains("150cw")) {
                        anglePresented = "Lateral";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                           fileName.contains("240cw") ||
                           fileName.contains("270cw") ||
                           fileName.contains("300cw") ||
                           fileName.contains("330cw")) {
                        anglePresented = "Medial";
                    }
                }
            } else if (fileName.contains("RF")) { // right foot
                if (viewPresented.equals("Top") ||
                viewPresented.equals("Heel")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                           fileName.contains("060cw") ||
                           fileName.contains("090cw") ||
                           fileName.contains("120cw") ||
                           fileName.contains("150cw")) {
                        anglePresented = "Lateral";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                           fileName.contains("240cw") ||
                           fileName.contains("270cw") ||
                           fileName.contains("300cw") ||
                           fileName.contains("330cw")) {
                        anglePresented = "Medial";
                    }
                } else if (viewPresented.equals("Sole") ||
                       viewPresented.equals("Toes") ||
                       viewPresented.equals("Inside") ||
                       viewPresented.equals("Outside")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                           fileName.contains("060cw") ||
                           fileName.contains("090cw") ||
                           fileName.contains("120cw") ||
                           fileName.contains("150cw")) {
                        anglePresented = "Medial";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                           fileName.contains("240cw") ||
                           fileName.contains("270cw") ||
                           fileName.contains("300cw") ||
                           fileName.contains("330cw")) {
                        anglePresented = "Lateral";
                    }
                }
            }
        }
        return anglePresented;
    }

    private int rotationPresented() {
        //String fileName = nextImageInQueue();
        String fileName = nextFileNameInQueue();
        int rotationPresented = 0;
        if (fileName.contains("000cw")) {
            rotationPresented = 0;
        } else if (fileName.contains("030cw") ||
        fileName.contains("330cw")) {
            rotationPresented = 30;
        } else if (fileName.contains("060cw") ||
            fileName.contains("300cw")) {
            rotationPresented = 60;
        } else if (fileName.contains("090cw") ||
            fileName.contains("270cw")) {
            rotationPresented = 90;
        } else if (fileName.contains("120cw") ||
            fileName.contains("240cw")) {
            rotationPresented = 120;
        } else if (fileName.contains("150cw") ||
            fileName.contains("210cw")) {
            rotationPresented = 150;
        } else if (fileName.contains("180cw")) {
            rotationPresented = 180;
        }
        return rotationPresented;
    }

    void timeoutTimerDidFire() {
        stopTimer(_timeoutTimer); //[_timeoutTimer invalidate];
        double duration = reactionTime();
        //String next = nextImageInQueue();
        String imageName = nextFileNameInQueue();
        String sidePresented = sidePresented();
        String view = viewPresented();
        String orientation = orientationPresented();
        int rotation = rotationPresented();
        String sideSelected = "None";
        _match = false;
        _timedOut = true;
        _timedOutCount++;
        calculatePercentagesForSides(sidePresented, _timedOut);
        createResultfromImage(imageName, view, rotation, orientation, _match, sidePresented, sideSelected, duration);
        displayTimeoutNotification(sidePresented);
    }

    void calculatePercentagesForSides(String sidePresented, boolean timeout) {
        if (sidePresented.equals("Left")) {
            if (_leftCount > 0) { // prevent zero denominator
                _leftPercentCorrect = (100 * (double)_leftSumCorrect) / (double)_leftCount;
            }
        } else if (sidePresented.equals("Right")) {
            if (_rightCount > 0) { // prevent zero denominator
                _rightPercentCorrect = (100 * (double)_rightSumCorrect) / (double)_rightCount;
            }
        }
        if (_imageCount > 0) { // prevent zero denominator
            _percentTimedOut = (100 * (double)_timedOutCount) / (double)_imageCount;
        }
    }

    void calculateMeanAndStdReactionTimes(String sidePresented, double duration, boolean match) {
        // calculate mean and unbiased standard deviation of duration for correct matches only
        // (using Welford's algorithm: Welford. (1962) Technometrics 4(3), 419-420)
        if (sidePresented.equals("Left") && (match)) {
            if (_leftSumCorrect == 1) {
                _prevMl = _newMl = duration;
                _prevSl = 0;
            } else {
                _newMl = _prevMl + (duration - _prevMl) / (double)_leftSumCorrect;
                _newSl += _prevSl + (duration - _prevMl) * (duration - _newMl);
                _prevMl = _newMl;
            }
            _meanLeftDuration = (_leftSumCorrect > 0) ? _newMl : 0;
            _varianceLeftDuration = (_leftSumCorrect > 1) ? _newSl / ((double)_leftSumCorrect - 1) : 0;
            if (_varianceLeftDuration > 0) {
                _stdLeftDuration = Math.sqrt(_varianceLeftDuration);
            }
        } else if (sidePresented.equals("Right") && (match)) {
            if (_rightSumCorrect == 1) {
                _prevMr = _newMr = duration;
                _prevSr = 0;
            } else {
                _newMr = _prevMr + (duration - _prevMr) / (double)_rightSumCorrect;
                _newSr += _prevSr + (duration - _prevMr) * (duration - _newMr);
                _prevMr = _newMr;
            }
            _meanRightDuration = (_rightSumCorrect > 0) ? _newMr : 0;
            _varianceRightDuration = (_rightSumCorrect > 1) ? _newSr / ((double)_rightSumCorrect - 1) : 0;
            if (_varianceRightDuration > 0) {
                _stdRightDuration = Math.sqrt(_varianceRightDuration);
            }
        }
    }

    private double reactionTime() {
        double endTime = System.currentTimeMillis();
        double duration = (endTime - _startTime);
        return duration;
    }

    private int nextImageInQueue() {
        String directory = leftRightJudgementStep.getDirectoryForImages();  // must use the directory to find the folder
        String imageName = nextFileNameInQueue();
        Context appContext = getContext().getApplicationContext();
        int imageID = getResources().getIdentifier(imageName, "drawable", appContext.getPackageName()); // unsure about arguments
        _imageCount++; // increment when called
        return imageID;
    }

    private String nextFileNameInQueue() {
        String[] fileNameArray = arrayOfImageFileNamesForEachAttempt();
        String fileName = fileNameArray[_imageCount - 1];
        //String fileName = [[path lastPathComponent] stringByDeletingPathExtension];
        return fileName;
    }

    private String[] arrayOfImageFileNamesForEachAttempt() { //- (NSArray *)arrayOfImagesForEachAttempt {
        int imageQueueLength = leftRightJudgementStep.getNumberOfAttempts();
        String directory = leftRightJudgementStep.getDirectoryForImages();
        if (_imageCount == 0) { // build shuffled array only once
            _imagePaths = arrayOfShuffledFileNamesFromDirectory(".png", directory);
        }
        // Copy required number of image queue elements to local array
        String[] imageQueueArray = new String[imageQueueLength]; //NSMutableArray *imageQueueArray = [NSMutableArray arrayWithCapacity:imageQueueLength];
        System.arraycopy(_imagePaths, 0, imageQueueArray, 0, imageQueueLength);
        return imageQueueArray;
    }

    private String[] arrayOfShuffledFileNamesFromDirectory(String type, String directory) {
        File folder = new File(directory);
        //File[] fileList = filePath.listFiles();
        if (folder.exists()) { // necessary?
            fileList = folder.listFiles(
                    new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return (name.endsWith(type));
                        }
                    });
        }
        List<String> fileNameList = new ArrayList<>();
        for(int i = 1; i <= fileList.length; i++) {
            fileNameList.add(fileList[i - 1].getName()); // add all filenames to list
        }
        Collections.shuffle(fileNameList); // shuffle list
        String[] fileNameArray = new String[fileNameList.size()];
        fileNameList.toArray(fileNameArray); // copy list elements to array
        return fileNameArray;
    }

    void startNextQuestionOrFinish() {
        stopTimer(_interStimulusIntervalTimer); //[_interStimulusIntervalTimer invalidate];
        if (_imageCount == (leftRightJudgementStep.getNumberOfAttempts())) {
            stop(); // same as iOS finish() ?
         } else {
            startQuestion();
         }
    }

    private void createResultfromImage (String imageName,
                                String view,
                                int rotation,
                                String orientation,
                                boolean match,
                                String sidePresented,
                                String sideSelected,
                                double duration) {

        leftRightJudgementResult.setImageNumber(_imageCount);
        leftRightJudgementResult.setImageName(imageName);
        leftRightJudgementResult.setViewPresented(view);
        leftRightJudgementResult.setOrientationPresented(orientation);
        leftRightJudgementResult.setRotationPresented(rotation);
        leftRightJudgementResult.setReactionTime(duration);
        leftRightJudgementResult.setSidePresented(sidePresented);
        leftRightJudgementResult.setSideSelected(sideSelected);
        leftRightJudgementResult.setSideMatch(match);
        leftRightJudgementResult.setTimedOut(_timedOut);
        // task results
        leftRightJudgementResult.setLeftImages(_leftCount);
        leftRightJudgementResult.setRightImages(_rightCount);
        leftRightJudgementResult.setLeftPercentCorrect(_leftPercentCorrect);
        leftRightJudgementResult.setRightPercentCorrect(_rightPercentCorrect);
        leftRightJudgementResult.setPercentTimedOut(_percentTimedOut);
        leftRightJudgementResult.setLeftMeanReactionTime(_meanLeftDuration);
        leftRightJudgementResult.setRightMeanReactionTime(_meanRightDuration);
        leftRightJudgementResult.setLeftSDReactionTime(_stdLeftDuration);
        leftRightJudgementResult.setRightSDReactionTime(_stdRightDuration);

        stepResult.setResultForIdentifier(leftRightJudgementResult.getIdentifier(), leftRightJudgementResult);
    }
}
