package com.example.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.ui.viewmodel.AppScreen;

public class BottomNavView {

    public interface NavListener {
        void onNavigate(AppScreen screen);
        void onQuickAdd();
    }

    private static final int ICON_HOME = 1;
    private static final int ICON_TRANSACTIONS = 2;
    private static final int ICON_GOALS = 3;
    private static final int ICON_MORE = 4;

    public static View create(
            Context context,
            AppScreen currentScreen,
            boolean isDark,
            final NavListener listener
    ) {
        LinearLayout bar = new LinearLayout(context);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setBackgroundColor(UiUtils.getCardColor(isDark));
        bar.setElevation(UiUtils.dp(context, 12));
        int padH = UiUtils.dp(context, 8);
        bar.setPadding(padH, UiUtils.dp(context, 10), padH, UiUtils.dp(context, 12));

        bar.addView(makeNavItem(
                context,
                ICON_HOME,
                "Home",
                currentScreen == AppScreen.HOME || currentScreen == AppScreen.SPENDING_BREAKDOWN,
                isDark,
                "nav_home",
                new Runnable() {
                    @Override
                    public void run() {
                        listener.onNavigate(AppScreen.HOME);
                    }
                }
        ));

        bar.addView(makeNavItem(
                context,
                ICON_TRANSACTIONS,
                "Transaction",
                currentScreen == AppScreen.TRANSACTIONS,
                isDark,
                "nav_transactions",
                new Runnable() {
                    @Override
                    public void run() {
                        listener.onNavigate(AppScreen.TRANSACTIONS);
                    }
                }
        ));

        bar.addView(makeNavItem(
                context,
                ICON_GOALS,
                "Goals",
                currentScreen == AppScreen.GOALS,
                isDark,
                "nav_goals",
                new Runnable() {
                    @Override
                    public void run() {
                        listener.onNavigate(AppScreen.GOALS);
                    }
                }
        ));

        bar.addView(makeNavItem(
                context,
                ICON_MORE,
                "More",
                currentScreen == AppScreen.MORE,
                isDark,
                "nav_more",
                new Runnable() {
                    @Override
                    public void run() {
                        listener.onNavigate(AppScreen.MORE);
                    }
                }
        ));

        return bar;
    }

    private static View makeNavItem(
            Context context,
            int iconType,
            String label,
            boolean isSelected,
            boolean isDark,
            String tag,
            Runnable onClick
    ) {
        LinearLayout col = new LinearLayout(context);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER);
        col.setTag(tag);
        col.setPadding(UiUtils.dp(context, 6), UiUtils.dp(context, 4), UiUtils.dp(context, 6), UiUtils.dp(context, 4));
        col.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        int activeColor = 0xFF10B981;
        int inactiveColor = isDark ? 0xFF94A3B8 : 0xFF64748B;
        int tint = isSelected ? activeColor : inactiveColor;

        NavIconView iconView = new NavIconView(context, iconType, tint, isSelected);
        int sz = UiUtils.dp(context, 24);
        iconView.setLayoutParams(new LinearLayout.LayoutParams(sz, sz));
        col.addView(iconView);

        col.addView(UiUtils.makeSpace(context, 1, 4));

        TextView labelTv = new TextView(context);
        labelTv.setText(label);
        labelTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        labelTv.setTypeface(isSelected ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        labelTv.setTextColor(tint);
        labelTv.setGravity(Gravity.CENTER);
        col.addView(labelTv);

        if (isSelected) {
            col.addView(UiUtils.makeSpace(context, 1, 3));
            View indicator = new View(context);
            int indW = UiUtils.dp(context, 24);
            int indH = UiUtils.dp(context, 3);
            LinearLayout.LayoutParams indLp = new LinearLayout.LayoutParams(indW, indH);
            indicator.setLayoutParams(indLp);
            indicator.setBackground(UiUtils.makeRoundedRect(context, activeColor, 2f));
            col.addView(indicator);
        }

        UiUtils.applyBounceClick(col, onClick);
        return col;
    }

    private static class NavIconView extends View {
        private final int type;
        private final int color;
        private final boolean filled;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();
        private final RectF rect = new RectF();

        NavIconView(Context context, int type, int color, boolean filled) {
            super(context);
            this.type = type;
            this.color = color;
            this.filled = filled;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            float cx = w / 2f;
            float cy = h / 2f;

            paint.setColor(color);
            paint.setStrokeWidth(UiUtils.dp(getContext(), 2f));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);

            if (type == ICON_HOME) {
                path.reset();
                path.moveTo(cx, h * 0.14f);
                path.lineTo(w * 0.14f, h * 0.48f);
                path.lineTo(w * 0.24f, h * 0.48f);
                path.lineTo(w * 0.24f, h * 0.86f);
                path.lineTo(w * 0.43f, h * 0.86f);
                path.lineTo(w * 0.43f, h * 0.62f);
                path.lineTo(w * 0.57f, h * 0.62f);
                path.lineTo(w * 0.57f, h * 0.86f);
                path.lineTo(w * 0.76f, h * 0.86f);
                path.lineTo(w * 0.76f, h * 0.48f);
                path.lineTo(w * 0.86f, h * 0.48f);
                path.close();
                paint.setStyle(filled ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE);
                canvas.drawPath(path, paint);
            } else if (type == ICON_TRANSACTIONS) {
                paint.setStyle(Paint.Style.STROKE);
                rect.set(w * 0.20f, h * 0.15f, w * 0.80f, h * 0.85f);
                float r = UiUtils.dp(getContext(), 3f);
                canvas.drawRoundRect(rect, r, r, paint);
                canvas.drawLine(w * 0.34f, h * 0.36f, w * 0.66f, h * 0.36f, paint);
                canvas.drawLine(w * 0.34f, h * 0.52f, w * 0.66f, h * 0.52f, paint);
                canvas.drawLine(w * 0.34f, h * 0.68f, w * 0.54f, h * 0.68f, paint);
            } else if (type == ICON_GOALS) {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(cx, cy, w * 0.34f, paint);
                canvas.drawCircle(cx, cy, w * 0.17f, paint);
                canvas.drawLine(cx + w * 0.04f, cy - h * 0.04f, w * 0.84f, h * 0.16f, paint);
            } else if (type == ICON_MORE) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(UiUtils.dp(getContext(), 2.4f));
                canvas.drawLine(w * 0.20f, h * 0.28f, w * 0.80f, h * 0.28f, paint);
                canvas.drawLine(w * 0.20f, h * 0.50f, w * 0.80f, h * 0.50f, paint);
                canvas.drawLine(w * 0.20f, h * 0.72f, w * 0.80f, h * 0.72f, paint);
            }
        }
    }
}
