package com.ylxdzsw.pinball;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.ylxdzsw.kit.R;

public class Pinball extends AppCompatActivity implements Animation.AnimationListener {
    private ImageView ball;
    private int times; // the number of times that pinball reaches the border
    private Animation ani;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinball);

        ball = (ImageView) findViewById(R.id.imageView);
        times = 0;
        ani = new TranslateAnimation(Animation.ABSOLUTE, 0f,
                                     Animation.ABSOLUTE, 0f,
                                     Animation.RELATIVE_TO_PARENT, 0f,
                                     Animation.RELATIVE_TO_PARENT, .28f);

        ani.setAnimationListener(this);
        nextAnimation();
    }

    public void nextAnimation() {
        if (times % 2 == 0) {
            ani.setInterpolator(input -> input * input);
        } else {
            ani.setInterpolator(input -> (1-input) * (1-input));
        }
        ani.setDuration(20000 / (times+4));
        ball.startAnimation(ani);
        times += 1;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        nextAnimation();
    }

    @Override
    public void onAnimationStart(Animation animation) {}

    @Override
    public void onAnimationRepeat(Animation animation) {}
}