package com.example.ui.screens;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.example.R;

public class OnboardingView {

    public static View create(final Context context, final Runnable onFinish) {
        final boolean[] finished = {false};
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable completeTask = new Runnable() {
            @Override
            public void run() {
                if (!finished[0]) {
                    finished[0] = true;
                    onFinish.run();
                }
            }
        };
        // Automatically display the welcome screen for 3 seconds then proceed
        handler.postDelayed(completeTask, 3000);

        FrameLayout root = new FrameLayout(context);
        root.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        root.setBackgroundColor(Color.WHITE);

        ImageView welcomeImg = new ImageView(context);
        welcomeImg.setImageResource(R.drawable.img_splash_welcome);
        welcomeImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        FrameLayout.LayoutParams imgLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        imgLp.gravity = Gravity.CENTER;
        welcomeImg.setLayoutParams(imgLp);
        root.addView(welcomeImg);

        // Tap anywhere on screen to proceed immediately
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(completeTask);
                completeTask.run();
            }
        });

        return root;
    }
}
