package com.example.ui.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.Locale;

public class UiUtils {

    public static final int COLOR_PRIMARY_TEAL = 0xFF0F8A74;
    public static final int COLOR_PRIMARY_TEAL_DARK = 0xFF065F51;
    public static final int COLOR_PRIMARY_TEAL_CONTAINER = 0xFFD0F4EA;
    public static final int COLOR_ON_PRIMARY_TEAL_CONTAINER = 0xFF04382E;
    public static final int COLOR_SECONDARY_CORAL = 0xFFF97316;
    public static final int COLOR_INCOME_GREEN = 0xFF10B981;
    public static final int COLOR_INCOME_GREEN_BG = 0xFFD1FAE5;
    public static final int COLOR_EXPENSE_RED = 0xFFEF4444;
    public static final int COLOR_EXPENSE_RED_BG = 0xFFFEE2E2;
    public static final int COLOR_WARNING_AMBER = 0xFFF59E0B;
    public static final int COLOR_NAVY_HEADING = 0xFF0F1E36;
    public static final int COLOR_SLATE_SUBTITLE = 0xFF5A6B87;

    public static int dp(Context context, float dpValue) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpValue,
                context.getResources().getDisplayMetrics()
        ));
    }

    public static String formatAmount(double amount) {
        long rounded = Math.round(amount);
        return NumberFormat.getNumberInstance(Locale.US).format(rounded);
    }

    public static int getBgColor(boolean isDark) {
        return isDark ? 0xFF0B1512 : 0xFFF2F6FA;
    }

    public static int getCardColor(boolean isDark) {
        return isDark ? 0xFF13221E : 0xFFFFFFFF;
    }

    public static int getSurfaceVariantColor(boolean isDark) {
        return isDark ? 0xFF1E332D : 0xFFE6EEF6;
    }

    public static int getTextPrimaryColor(boolean isDark) {
        return isDark ? 0xFFECFDF5 : COLOR_NAVY_HEADING;
    }

    public static int getTextSecondaryColor(boolean isDark) {
        return isDark ? 0xFF94A3B8 : COLOR_SLATE_SUBTITLE;
    }

    public static int getOutlineColor(boolean isDark) {
        return isDark ? 0xFF334155 : 0xFFCBD5E1;
    }

    /**
     * Smoothly interpolates between two ARGB colors.
     */
    public static int lerpColor(int startColor, int endColor, float t) {
        float clamped = Math.max(0f, Math.min(1f, t));
        int a = (int) (Color.alpha(startColor) + (Color.alpha(endColor) - Color.alpha(startColor)) * clamped);
        int r = (int) (Color.red(startColor) + (Color.red(endColor) - Color.red(startColor)) * clamped);
        int g = (int) (Color.green(startColor) + (Color.green(endColor) - Color.green(startColor)) * clamped);
        int b = (int) (Color.blue(startColor) + (Color.blue(endColor) - Color.blue(startColor)) * clamped);
        return Color.argb(a, r, g, b);
    }

    /**
     * Returns the interpolated color for a budget usage ratio in [0f, 1f]:
     * 0.0 -> Green (#10B981)
     * 0.5 -> Yellow/Amber (#FACC15)
     * 1.0 -> Red (#EF4444)
     */
    public static int interpolateBudgetColor(float usedFraction) {
        float f = Math.max(0f, Math.min(1f, usedFraction));
        int green = 0xFF10B981;
        int yellow = 0xFFFACC15;
        int amber = 0xFFF59E0B;
        int red = 0xFFEF4444;
        if (f <= 0.5f) {
            return lerpColor(green, yellow, f / 0.5f);
        } else if (f <= 0.75f) {
            return lerpColor(yellow, amber, (f - 0.5f) / 0.25f);
        } else {
            return lerpColor(amber, red, (f - 0.75f) / 0.25f);
        }
    }

    /**
     * Creates a horizontal gradient drawable for the budget progress slider that smoothly
     * transitions from Green -> Yellow -> Red as the budget usage goes from 0% to 100%.
     */
    public static GradientDrawable makeBudgetSliderGradient(Context context, float usedFraction, float radiusDp) {
        float f = Math.max(0f, Math.min(1f, usedFraction));
        int greenStart = 0xFF10B981;
        int endColor = interpolateBudgetColor(f);
        int midColor = interpolateBudgetColor(f * 0.55f);

        int[] colors;
        if (f < 0.25f) {
            colors = new int[]{0xFF059669, greenStart, endColor};
        } else if (f < 0.65f) {
            colors = new int[]{greenStart, midColor, endColor};
        } else {
            colors = new int[]{greenStart, 0xFFFACC15, endColor};
        }

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                colors
        );
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(dp(context, radiusDp));
        return gd;
    }

    public static GradientDrawable makeRoundedRect(Context context, int fillColor, float radiusDp) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setColor(fillColor);
        gd.setCornerRadius(dp(context, radiusDp));
        return gd;
    }

    public static GradientDrawable makeRoundedRectWithStroke(
            Context context,
            int fillColor,
            float radiusDp,
            int strokeColor,
            float strokeWidthDp
    ) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setColor(fillColor);
        gd.setCornerRadius(dp(context, radiusDp));
        gd.setStroke(Math.max(1, dp(context, strokeWidthDp)), strokeColor);
        return gd;
    }

    public static GradientDrawable makeGradientRect(
            Context context,
            int[] colors,
            float radiusDp
    ) {
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                colors
        );
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(dp(context, radiusDp));
        return gd;
    }

    public static GradientDrawable makeCircle(int fillColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(fillColor);
        return gd;
    }

    public static GradientDrawable makeCircleWithStroke(Context context, int fillColor, int strokeColor, float strokeWidthDp) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(fillColor);
        gd.setStroke(Math.max(1, dp(context, strokeWidthDp)), strokeColor);
        return gd;
    }

    public static void applyBounceClick(final View view, final Runnable onClick) {
        view.setClickable(true);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                if (action == MotionEvent.ACTION_DOWN) {
                    v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(90).start();
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(110).start();
                }
                return false;
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick != null) {
                    onClick.run();
                }
            }
        });
    }

    public static TextView createHighlightedChip(
            Context context,
            String text,
            boolean isSelected,
            Runnable onClick
    ) {
        TextView chip = new TextView(context);
        chip.setText(isSelected ? "✓ " + text : text);
        chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        chip.setGravity(Gravity.CENTER);
        int padH = dp(context, 14);
        int padV = dp(context, 10);
        chip.setPadding(padH, padV, padH, padV);

        if (isSelected) {
            chip.setTextColor(Color.WHITE);
            chip.setTypeface(Typeface.DEFAULT_BOLD);
            chip.setBackground(makeRoundedRectWithStroke(
                    context,
                    COLOR_PRIMARY_TEAL,
                    12f,
                    COLOR_PRIMARY_TEAL_DARK,
                    2f
            ));
            chip.setElevation(dp(context, 4));
        } else {
            chip.setTextColor(0xFF0F172A);
            chip.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            chip.setBackground(makeRoundedRectWithStroke(
                    context,
                    0xFFF1F5F9,
                    12f,
                    0xFFCBD5E1,
                    1f
            ));
            chip.setElevation(0f);
        }
        applyBounceClick(chip, onClick);
        return chip;
    }

    public static View makeSpace(Context context, int widthDp, int heightDp) {
        View v = new View(context);
        v.setLayoutParams(new LinearLayout.LayoutParams(dp(context, widthDp), dp(context, heightDp)));
        return v;
    }

    public static boolean areAnimationsEnabled(Context context) {
        if (context == null) return true;
        try {
            float scale = android.provider.Settings.Global.getFloat(
                    context.getContentResolver(),
                    android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
                    1.0f
            );
            return scale > 0f;
        } catch (Exception e) {
            return true;
        }
    }

    public static void animateCounter(
            final TextView textView,
            double startVal,
            double endVal,
            final String prefix,
            final String suffix,
            long durationMs
    ) {
        if (textView == null) return;
        if (!areAnimationsEnabled(textView.getContext())) {
            textView.setText(prefix + formatAmount(endVal) + suffix);
            return;
        }
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofFloat((float) startVal, (float) endVal);
        animator.setDuration(durationMs);
        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());
        animator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                float val = (Float) animation.getAnimatedValue();
                textView.setText(prefix + formatAmount(val) + suffix);
            }
        });
        animator.start();
    }

    public static void animateEntrance(View view, long delayMs) {
        if (view == null) return;
        if (!areAnimationsEnabled(view.getContext())) return;
        view.setAlpha(0f);
        view.setTranslationY(dp(view.getContext(), 24));
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delayMs)
                .setDuration(350)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();
    }
}
