package com.example.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.ui.viewmodel.BarChartPoint;
import java.util.Collections;
import java.util.List;

public class InteractiveBarChartView extends LinearLayout {

    private List<BarChartPoint> points = Collections.emptyList();
    private int selectedIndex = 0;
    private boolean isDark = false;

    private final TextView summaryLabel;
    private final BarCanvasView barCanvasView;
    private final LinearLayout labelsRow;

    public InteractiveBarChartView(Context context, boolean isDark) {
        super(context);
        this.isDark = isDark;
        setOrientation(VERTICAL);

        LinearLayout headerRow = new LinearLayout(context);
        headerRow.setOrientation(HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        headerRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        summaryLabel = new TextView(context);
        summaryLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        summaryLabel.setTypeface(Typeface.DEFAULT_BOLD);
        int padH = UiUtils.dp(context, 10);
        int padV = UiUtils.dp(context, 5);
        summaryLabel.setPadding(padH, padV, padH, padV);
        LinearLayout.LayoutParams summaryParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
        headerRow.addView(summaryLabel, summaryParams);

        LinearLayout legendRow = new LinearLayout(context);
        legendRow.setOrientation(HORIZONTAL);
        legendRow.setGravity(Gravity.CENTER_VERTICAL);
        legendRow.addView(makeLegendDot(context, UiUtils.COLOR_PRIMARY_TEAL, "Income"));
        legendRow.addView(UiUtils.makeSpace(context, 10, 1));
        legendRow.addView(makeLegendDot(context, UiUtils.COLOR_SECONDARY_CORAL, "Spent"));
        headerRow.addView(legendRow);

        addView(headerRow);
        addView(UiUtils.makeSpace(context, 1, 14));

        barCanvasView = new BarCanvasView(context);
        barCanvasView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, UiUtils.dp(context, 160)));
        addView(barCanvasView);

        addView(UiUtils.makeSpace(context, 1, 6));

        labelsRow = new LinearLayout(context);
        labelsRow.setOrientation(HORIZONTAL);
        labelsRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(labelsRow);
    }

    private View makeLegendDot(Context context, int color, String label) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        View dot = new View(context);
        int d = UiUtils.dp(context, 8);
        dot.setLayoutParams(new LayoutParams(d, d));
        dot.setBackground(UiUtils.makeCircle(color));
        row.addView(dot);

        row.addView(UiUtils.makeSpace(context, 4, 1));

        TextView tv = new TextView(context);
        tv.setText(label);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tv.setTextColor(UiUtils.getTextSecondaryColor(isDark));
        row.addView(tv);
        return row;
    }

    private float animProgress = 0f;

    public void setData(List<BarChartPoint> points, boolean isDark) {
        this.points = points != null ? points : Collections.emptyList();
        this.isDark = isDark;
        if (selectedIndex >= this.points.size()) {
            selectedIndex = Math.max(0, this.points.size() - 1);
        } else if (!this.points.isEmpty() && selectedIndex < 0) {
            selectedIndex = this.points.size() - 1;
        }

        if (UiUtils.areAnimationsEnabled(getContext())) {
            android.animation.ValueAnimator anim = android.animation.ValueAnimator.ofFloat(0f, 1f);
            anim.setDuration(600);
            anim.setInterpolator(new android.view.animation.DecelerateInterpolator());
            anim.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                    animProgress = (Float) animation.getAnimatedValue();
                    barCanvasView.invalidate();
                }
            });
            anim.start();
        } else {
            animProgress = 1f;
        }

        refreshUi();
    }

    private void refreshUi() {
        summaryLabel.setBackground(UiUtils.makeRoundedRect(
                getContext(),
                isDark ? 0xFF04382E : UiUtils.COLOR_PRIMARY_TEAL_CONTAINER,
                8f
        ));
        summaryLabel.setTextColor(isDark ? 0xFF2ED3B7 : UiUtils.COLOR_ON_PRIMARY_TEAL_CONTAINER);

        if (!points.isEmpty() && selectedIndex >= 0 && selectedIndex < points.size()) {
            BarChartPoint p = points.get(selectedIndex);
            summaryLabel.setText(p.label + ": In ₹" + (int) p.income + " • Out ₹" + (int) p.expense);
        } else {
            summaryLabel.setText("Tap any bar to inspect");
        }

        labelsRow.removeAllViews();
        for (int i = 0; i < points.size(); i++) {
            final int idx = i;
            BarChartPoint pt = points.get(i);
            TextView tv = new TextView(getContext());
            tv.setText(pt.label);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            boolean sel = (i == selectedIndex);
            tv.setTypeface(sel ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            tv.setTextColor(sel ? UiUtils.COLOR_PRIMARY_TEAL : UiUtils.getTextSecondaryColor(isDark));
            LayoutParams lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
            tv.setLayoutParams(lp);
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedIndex = idx;
                    refreshUi();
                }
            });
            labelsRow.addView(tv);
        }
        barCanvasView.invalidate();
    }

    private class BarCanvasView extends View {
        private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        public BarCanvasView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (points.isEmpty()) return;

            float w = getWidth();
            float h = getHeight();
            gridPaint.setColor(0x2294A3B8);
            gridPaint.setStrokeWidth(2f);

            for (int i = 0; i <= 3; i++) {
                float y = h * (i / 3f);
                canvas.drawLine(0f, y, w, y, gridPaint);
            }

            double maxVal = 100.0;
            for (BarChartPoint p : points) {
                if (p.expense > maxVal) maxVal = p.expense;
                if (p.income > maxVal) maxVal = p.income;
            }

            float groupWidth = w / points.size();
            float barWidth = Math.min(groupWidth * 0.28f, UiUtils.dp(getContext(), 20));
            float corner = UiUtils.dp(getContext(), 6);

            for (int index = 0; index < points.size(); index++) {
                BarChartPoint pt = points.get(index);
                float centerX = groupWidth * index + groupWidth / 2f;
                boolean isSelected = index == selectedIndex;

                if (isSelected) {
                    barPaint.setColor(0x180F8A74);
                    rect.set(index * groupWidth + 4f, 0f, (index + 1) * groupWidth - 4f, h);
                    canvas.drawRoundRect(rect, UiUtils.dp(getContext(), 8), UiUtils.dp(getContext(), 8), barPaint);
                }

                float incRatio = (float) Math.max(0.04, Math.min(1.0, pt.income / maxVal));
                float incHeight = h * incRatio * animProgress;
                barPaint.setColor(isSelected ? UiUtils.COLOR_PRIMARY_TEAL : 0x990F8A74);
                rect.set(centerX - barWidth - 3f, h - incHeight, centerX - 3f, h);
                canvas.drawRoundRect(rect, corner, corner, barPaint);

                float expRatio = (float) Math.max(0.04, Math.min(1.0, pt.expense / maxVal));
                float expHeight = h * expRatio * animProgress;
                barPaint.setColor(isSelected ? UiUtils.COLOR_SECONDARY_CORAL : 0x99F97316);
                rect.set(centerX + 3f, h - expHeight, centerX + 3f + barWidth, h);
                canvas.drawRoundRect(rect, corner, corner, barPaint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP && !points.isEmpty()) {
                float groupWidth = (float) getWidth() / points.size();
                int idx = (int) (event.getX() / groupWidth);
                if (idx < 0) idx = 0;
                if (idx >= points.size()) idx = points.size() - 1;
                selectedIndex = idx;
                refreshUi();
                return true;
            }
            return true;
        }
    }
}
