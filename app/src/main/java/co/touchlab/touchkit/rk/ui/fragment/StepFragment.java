package co.touchlab.touchkit.rk.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.Result;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.QuestionStep;
import co.touchlab.touchkit.rk.common.step.Step;

public abstract class StepFragment extends Fragment
{
    public static final String KEY_QUESTION_STEP = "KEY_STEP";

    protected QuestionStep step;
    protected StepResult stepResult;
    private StepCallbacks callbacks;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        callbacks = ((StepCallbacks) context);
    }

    public StepFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        step = (QuestionStep) getArguments().getSerializable(KEY_QUESTION_STEP);

        stepResult = callbacks.getResultStep(step.getIdentifier());
        if(stepResult == null)
        {
            stepResult = createNewStepResult(step.getIdentifier());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        Step step = getStep();

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(step.getTitle());

        LinearLayout stepViewContainer = (LinearLayout) view.findViewById(R.id.step_view_container);
        int bodyIndex = stepViewContainer.indexOfChild(title) + 1;
        View bodyView = getBodyView(inflater);
        stepViewContainer.addView(bodyView, bodyIndex);

        TextView next = (TextView) view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callbacks.onNextPressed(getStep());
            }
        });

        TextView skip = (TextView) view.findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callbacks.onSkipStep(getStep());
            }
        });

        return view;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        callbacks = null;
    }

    public abstract StepResult createNewStepResult(String stepIdentifier);

    public abstract View getBodyView(LayoutInflater inflater);

    public Step getStep()
    {
        return step;
    }

    public StepResult getStepResult()
    {
        return stepResult;
    }

    public void setStepResult(Result result)
    {
        // TODO this is bad and we should feel bad
        Map<String, Result> results = new HashMap<>();
        results.put(result.getIdentifier(), result);
        stepResult.setResults(results);

        callbacks.onStepResultChanged(step, stepResult);
    }

    public interface StepCallbacks {
        void onNextPressed(Step step);
        void onStepResultChanged(Step step, StepResult result);
        void onSkipStep(Step step);
        StepResult getResultStep(String stepId);
    }
}
