package com.example.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.data.model.ExpenseCategory;
import com.example.ui.viewmodel.CategoryBreakdownItem;
import java.util.Collections;
import java.util.List;

public class DonutChartView extends LinearLayout {

    public interface OnCategorySelectedListener {
        void onCategorySelected(ExpenseCategory category);
    }

    private List<CategoryBreakdownItem> items = Collections.emptyList();
    private double totalSpent = 0.0;
    private ExpenseCategory selectedCategory = null;
    private OnCategorySelectedListener listener;
    private boolean isDark = false;

    private final DonutCanvasView canvasView;
    private final TextView centerTopLabel;
    private final TextView centerAmountLabel;
    private final TextView centerPctLabel;

    public DonutChartView(Context context, boolean isDark) {
        super(context);
        this.isDark = isDark;
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        FrameLayout chartFrame = new FrameLayout(context);
        int sizePx = UiUtils.dp(context, 186);
        LayoutParams frameParams = new LayoutParams(sizePx, sizePx);
        frameParams.gravity = Gravity.CENTER_HORIZONTAL;
        chartFrame.setLayoutParams(frameParams);

        canvasView = new DonutCanvasView(context);
        chartFrame.addView(canvasView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        LinearLayout centerBox = new LinearLayout(context);
        centerBox.setOrientation(VERTICAL);
        centerBox.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams centerParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        centerBox.setLayoutParams(centerParams);

        centerTopLabel = new TextView(context);
        centerTopLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        centerTopLabel.setTextColor(UiUtils.getTextSecondaryColor(isDark));
        centerTopLabel.setGravity(Gravity.CENTER);
        centerBox.addView(centerTopLabel);

        centerAmountLabel = new TextView(context);
        centerAmountLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        centerAmountLabel.setTypeface(Typeface.DEFAULT_BOLD);
        centerAmountLabel.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        centerAmountLabel.setGravity(Gravity.CENTER);
        centerBox.addView(centerAmountLabel);

        centerPctLabel = new TextView(context);
        centerPctLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        centerPctLabel.setTypeface(Typeface.DEFAULT_BOLD);
        centerPctLabel.setGravity(Gravity.CENTER);
        centerPctLabel.setVisibility(GONE);
        centerBox.addView(centerPctLabel);

        chartFrame.addView(centerBox);
        addView(chartFrame);
    }

    private float animProgress = 0f;

    public void setData(
            List<CategoryBreakdownItem> items,
            double totalSpent,
            ExpenseCategory selectedCategory,
            boolean isDark,
            OnCategorySelectedListener listener
    ) {
        this.items = items != null ? items : Collections.emptyList();
        this.totalSpent = totalSpent;
        this.selectedCategory = selectedCategory;
        this.isDark = isDark;
        this.listener = listener;
        updateCenterLabels();

        if (UiUtils.areAnimationsEnabled(getContext())) {
            android.animation.ValueAnimator anim = android.animation.ValueAnimator.ofFloat(0f, 1f);
            anim.setDuration(700);
            anim.setInterpolator(new android.view.animation.DecelerateInterpolator());
            anim.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                    animProgress = (Float) animation.getAnimatedValue();
                    canvasView.invalidate();
                }
            });
            anim.start();
        } else {
            animProgress = 1f;
            canvasView.invalidate();
        }
    }

    private void updateCenterLabels() {
        CategoryBreakdownItem selectedItem = null;
        if (selectedCategory != null) {
            for (CategoryBreakdownItem it : items) {
                if (it.category == selectedCategory) {
                    selectedItem = it;
                    break;
                }
            }
        }
        centerTopLabel.setTextColor(UiUtils.getTextSecondaryColor(isDark));
        centerAmountLabel.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        if (selectedItem != null) {
            centerTopLabel.setText(selectedItem.category.emoji + " " + selectedItem.category.label);
            centerAmountLabel.setText("₹" + (int) selectedItem.amount);
            centerPctLabel.setText(((int) selectedItem.percentage) + "% of total");
            centerPctLabel.setTextColor(selectedItem.category.colorValue);
            centerPctLabel.setVisibility(VISIBLE);
        } else {
            centerTopLabel.setText("Total Spent");
            centerAmountLabel.setText("₹" + (int) totalSpent);
            centerPctLabel.setVisibility(GONE);
        }
    }

    private class DonutCanvasView extends View {
        private final Paint arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF arcRect = new RectF();

        public DonutCanvasView(Context context) {
            super(context);
            arcPaint.setStyle(Paint.Style.STROKE);
            arcPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            float strokePx = UiUtils.dp(getContext(), 26);
            float selectedStrokePx = UiUtils.dp(getContext(), 32);
            float pad = selectedStrokePx / 2f + UiUtils.dp(getContext(), 6);
            arcRect.set(pad, pad, w - pad, h - pad);

            if (items.isEmpty() || totalSpent <= 0.0) {
                arcPaint.setColor(0x3394A3B8);
                arcPaint.setStrokeWidth(strokePx);
                canvas.drawArc(arcRect, 0f, 360f, false, arcPaint);
                return;
            }

            float startAngle = -90f;
            float gapDegrees = items.size() > 1 ? 4f : 0f;

            for (CategoryBreakdownItem item : items) {
                float totalCategorySweep = (item.percentage / 100f) * 360f;
                float sweep = totalCategorySweep * animProgress;
                boolean isSelected = selectedCategory == item.category;
                arcPaint.setColor(item.category.colorValue);
                arcPaint.setStrokeWidth(isSelected ? selectedStrokePx : strokePx);
                float actualSweep = Math.max(sweep - gapDegrees, 1f);
                canvas.drawArc(arcRect, startAngle + gapDegrees / 2f, actualSweep, false, arcPaint);
                startAngle += totalCategorySweep * animProgress;
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP && !items.isEmpty()) {
                float cx = getWidth() / 2f;
                float cy = getHeight() / 2f;
                float dx = event.getX() - cx;
                float dy = event.getY() - cy;
                float dist = (float) Math.hypot(dx, dy);
                float outerRadius = Math.min(getWidth(), getHeight()) / 2f;
                float innerRadius = outerRadius * 0.35f;

                if (dist >= innerRadius && dist <= outerRadius) {
                    float touchAngle = (float) ((Math.toDegrees(Math.atan2(dy, dx)) + 90.0 + 360.0) % 360.0);
                    float cumulative = 0f;
                    ExpenseCategory hit = null;
                    for (CategoryBreakdownItem item : items) {
                        float sweep = (item.percentage / 100f) * 360f;
                        if (touchAngle >= cumulative && touchAngle <= cumulative + sweep) {
                            hit = item.category;
                            break;
                        }
                        cumulative += sweep;
                    }
                    if (listener != null) {
                        ExpenseCategory next = (selectedCategory == hit) ? null : hit;
                        selectedCategory = next;
                        updateCenterLabels();
                        invalidate();
                        listener.onCategorySelected(next);
                    }
                } else if (dist < innerRadius && listener != null) {
                    selectedCategory = null;
                    updateCenterLabels();
                    invalidate();
                    listener.onCategorySelected(null);
                }
                return true;
            }
            return true;
        }
    }
}
