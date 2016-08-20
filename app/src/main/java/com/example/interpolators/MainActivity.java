package com.example.interpolators;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    View ball;
    Spinner spinner;
    Button change;
    ValueAnimator animator;
    List<Interpolator> interpolatorList;

    private final static int TYPE_FALL = 0;
    private final static int TYPE_SCALE = 1;
    int current = TYPE_FALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ball = findViewById(R.id.ball_view);
        spinner = (Spinner) findViewById(R.id.spinner);
        change = (Button) findViewById(R.id.change_animator_btn);

        setInterpolatorList();

        spinner.setAdapter(new InterpolatorAdapter());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                animateBall(interpolatorList.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animator.cancel();
                initBall();
                if (current == TYPE_FALL) {
                    setUpScaleAnimator();
                    current = TYPE_SCALE;
                } else {
                    setUpFallAnimator();
                    current = TYPE_FALL;
                }
            }
        });

        spinner.post(new Runnable() {
            @Override
            public void run() {
                setUpFallAnimator();
            }
        });
    }

    private void setInterpolatorList() {
        interpolatorList = new ArrayList<>();
        interpolatorList.add(new AccelerateInterpolator());
        interpolatorList.add(new DecelerateInterpolator());
        interpolatorList.add(new AccelerateDecelerateInterpolator());
        interpolatorList.add(new AnticipateInterpolator());
        interpolatorList.add(new AnticipateOvershootInterpolator());
        interpolatorList.add(new BounceInterpolator());
        interpolatorList.add(new CycleInterpolator(2));
        interpolatorList.add(new OvershootInterpolator());
        interpolatorList.add(new LinearInterpolator());
        interpolatorList.add(new FastOutLinearInInterpolator());
        interpolatorList.add(new FastOutSlowInInterpolator());
    }

    private void setUpFallAnimator() {
        animator = ValueAnimator.ofInt(0, (int)spinner.getY());
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(interpolatorList.get(spinner.getSelectedItemPosition()));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) ball.getLayoutParams();
                layoutParams.topMargin = value;
                ball.requestLayout();
            }
        });
        animator.start();
    }

    private void setUpScaleAnimator() {
        animator = ValueAnimator.ofFloat(1, 5f);
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(interpolatorList.get(spinner.getSelectedItemPosition()));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float value = (Float) valueAnimator.getAnimatedValue();
                ball.setScaleX(value);
                ball.setScaleY(value);
            }
        });
        animator.start();
    }

    private void animateBall(Interpolator interpolator) {
        animator.cancel();
        animator.setInterpolator(interpolator);
        animator.start();
    }

    private void initBall() {
        ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) ball.getLayoutParams();
        layoutParams.width = getResources().getDimensionPixelSize(R.dimen.ball_size);
        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.ball_size);
        layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.ball_margin);
        ball.requestLayout();
        ball.setScaleX(1f);
        ball.setScaleY(1f);
    }

    class InterpolatorAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return interpolatorList.size();
        }

        @Override
        public Object getItem(int i) {
            return interpolatorList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = LayoutInflater.from(MainActivity.this).inflate(android.R.layout.simple_spinner_dropdown_item, viewGroup, false);
                view.setTag(new ViewHolder(view));
            }

            viewHolder = (ViewHolder) view.getTag();
            viewHolder.view.setText(interpolatorList.get(i).getClass().getSimpleName());

            return view;
        }
    }

    class ViewHolder {
        TextView view;
        ViewHolder(View v) {
            view = (TextView) v;
        }
    }

}
