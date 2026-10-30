package com.example.ui.screens;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.data.model.ExpenseCategory;
import com.example.ui.components.DonutChartView;
import com.example.ui.components.InteractiveBarChartView;
import com.example.ui.components.UiUtils;
import com.example.ui.viewmodel.BreakdownPeriod;
import com.example.ui.viewmodel.CategoryBreakdownItem;
import com.example.ui.viewmodel.DashboardData;
import com.example.ui.viewmodel.PocketPalViewModel;

public class SpendingBreakdownView {

    public static View create(final Context context, final PocketPalViewModel viewModel) {
        final DashboardData data = viewModel.getDashboardData();
        final boolean isDark = data.preferences != null && data.preferences.isDarkMode;

        ScrollView scroll = new ScrollView(context);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(UiUtils.getBgColor(isDark));

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = UiUtils.dp(context, 16);
        root.setPadding(pad, UiUtils.dp(context, 16), pad, UiUtils.dp(context, 28));

        // Top Bar
        LinearLayout topBar = new LinearLayout(context);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);

        TextView backBtn = new TextView(context);
        backBtn.setText("← Back");
        backBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        backBtn.setTypeface(Typeface.DEFAULT_BOLD);
        backBtn.setTextColor(UiUtils.COLOR_PRIMARY_TEAL);
        backBtn.setPadding(UiUtils.dp(context, 8), UiUtils.dp(context, 6), UiUtils.dp(context, 8), UiUtils.dp(context, 6));
        UiUtils.applyBounceClick(backBtn, new Runnable() {
            @Override
            public void run() {
                viewModel.navigateBack();
            }
        });
        topBar.addView(backBtn);

        TextView title = new TextView(context);
        title.setText("Spending Reports");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        title.setGravity(Gravity.CENTER);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        topBar.addView(title);

        topBar.addView(UiUtils.makeSpace(context, 48, 1));
        root.addView(topBar);

        root.addView(UiUtils.makeSpace(context, 1, 14));

        // Equal-width Breakdown Period Selector (This Month | Last Month | Custom)
        LinearLayout periodRow = new LinearLayout(context);
        periodRow.setOrientation(LinearLayout.HORIZONTAL);
        periodRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        BreakdownPeriod[] bPeriods = BreakdownPeriod.values();
        for (int i = 0; i < bPeriods.length; i++) {
            final BreakdownPeriod bp = bPeriods[i];
            boolean isSelected = bp == data.selectedBreakdownPeriod;
            TextView chip = makeEqualPeriodChip(
                    context,
                    bp.label,
                    isSelected,
                    isDark,
                    new Runnable() {
                        @Override
                        public void run() {
                            viewModel.updateBreakdownPeriod(bp);
                        }
                    }
            );
            chip.setTag("period_btn_" + bp.name());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            if (i > 0) lp.leftMargin = UiUtils.dp(context, 8);
            chip.setLayoutParams(lp);
            periodRow.addView(chip);
        }
        root.addView(periodRow);

        root.addView(UiUtils.makeSpace(context, 1, 16));

        // Donut Chart Card
        LinearLayout donutCard = new LinearLayout(context);
        donutCard.setOrientation(LinearLayout.VERTICAL);
        int cPad = UiUtils.dp(context, 16);
        donutCard.setPadding(cPad, cPad, cPad, cPad);
        donutCard.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 20f));
        donutCard.setElevation(UiUtils.dp(context, 2));

        TextView dTitle = new TextView(context);
        dTitle.setText("Category Breakdown (" + data.selectedBreakdownPeriod.label + ")");
        dTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        dTitle.setTypeface(Typeface.DEFAULT_BOLD);
        dTitle.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        donutCard.addView(dTitle);

        donutCard.addView(UiUtils.makeSpace(context, 1, 12));

        DonutChartView donutChart = new DonutChartView(context, isDark);
        double periodTotal = 0.0;
        for (CategoryBreakdownItem item : data.categoryBreakdown) {
            periodTotal += item.amount;
        }
        donutChart.setData(data.categoryBreakdown, periodTotal, null, isDark, new DonutChartView.OnCategorySelectedListener() {
            @Override
            public void onCategorySelected(ExpenseCategory category) {
            }
        });
        donutCard.addView(donutChart);
        root.addView(donutCard);

        root.addView(UiUtils.makeSpace(context, 1, 16));

        // Ranked Category Progress List
        TextView rankHeader = new TextView(context);
        rankHeader.setText("Ranked Category Spending");
        rankHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        rankHeader.setTypeface(Typeface.DEFAULT_BOLD);
        rankHeader.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        root.addView(rankHeader);

        root.addView(UiUtils.makeSpace(context, 1, 10));

        for (final CategoryBreakdownItem item : data.categoryBreakdown) {
            LinearLayout rowCard = new LinearLayout(context);
            rowCard.setOrientation(LinearLayout.VERTICAL);
            rowCard.setPadding(UiUtils.dp(context, 14), UiUtils.dp(context, 12), UiUtils.dp(context, 14), UiUtils.dp(context, 12));
            rowCard.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 16f));

            LinearLayout top = new LinearLayout(context);
            top.setOrientation(LinearLayout.HORIZONTAL);
            top.setGravity(Gravity.CENTER_VERTICAL);

            TextView badge = new TextView(context);
            badge.setText(item.category.emoji);
            badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            badge.setGravity(Gravity.CENTER);
            int sz = UiUtils.dp(context, 38);
            badge.setLayoutParams(new LinearLayout.LayoutParams(sz, sz));
            badge.setBackground(UiUtils.makeRoundedRect(context, item.category.bgLightColor, 10f));
            top.addView(badge);

            top.addView(UiUtils.makeSpace(context, 10, 1));

            LinearLayout mid = new LinearLayout(context);
            mid.setOrientation(LinearLayout.VERTICAL);
            mid.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView cName = new TextView(context);
            cName.setText(item.category.label);
            cName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            cName.setTypeface(Typeface.DEFAULT_BOLD);
            cName.setTextColor(UiUtils.getTextPrimaryColor(isDark));
            mid.addView(cName);

            TextView cCount = new TextView(context);
            cCount.setText(item.transactionCount + (item.transactionCount == 1 ? " transaction" : " transactions"));
            cCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            cCount.setTextColor(UiUtils.getTextSecondaryColor(isDark));
            mid.addView(cCount);
            top.addView(mid);

            LinearLayout right = new LinearLayout(context);
            right.setOrientation(LinearLayout.VERTICAL);
            right.setGravity(Gravity.END);

            TextView amt = new TextView(context);
            amt.setText("₹" + (int) item.amount);
            amt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            amt.setTypeface(Typeface.DEFAULT_BOLD);
            amt.setTextColor(UiUtils.getTextPrimaryColor(isDark));
            right.addView(amt);

            TextView pct = new TextView(context);
            pct.setText((int) item.percentage + "%");
            pct.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            pct.setTypeface(Typeface.DEFAULT_BOLD);
            pct.setTextColor(item.category.colorValue);
            right.addView(pct);

            top.addView(right);
            rowCard.addView(top);

            rowCard.addView(UiUtils.makeSpace(context, 1, 8));

            final FrameLayout barTrack = new FrameLayout(context);
            barTrack.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    UiUtils.dp(context, 8)
            ));
            barTrack.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getSurfaceVariantColor(isDark), 4f));

            final View barFill = new View(context);
            barFill.setBackground(UiUtils.makeRoundedRect(context, item.category.colorValue, 4f));
            barTrack.addView(barFill);
            barTrack.post(new Runnable() {
                @Override
                public void run() {
                    float ratio = Math.max(0.04f, Math.min(1f, item.percentage / 100f));
                    final int targetW = (int) (barTrack.getWidth() * ratio);
                    if (UiUtils.areAnimationsEnabled(context)) {
                        android.animation.ValueAnimator anim = android.animation.ValueAnimator.ofInt(0, targetW);
                        anim.setDuration(550);
                        anim.setInterpolator(new android.view.animation.DecelerateInterpolator());
                        anim.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                                int currentW = (Integer) animation.getAnimatedValue();
                                barFill.setLayoutParams(new FrameLayout.LayoutParams(currentW, FrameLayout.LayoutParams.MATCH_PARENT));
                            }
                        });
                        anim.start();
                    } else {
                        barFill.setLayoutParams(new FrameLayout.LayoutParams(targetW, FrameLayout.LayoutParams.MATCH_PARENT));
                    }
                }
            });
            rowCard.addView(barTrack);

            root.addView(rowCard);
            root.addView(UiUtils.makeSpace(context, 1, 8));
        }

        root.addView(UiUtils.makeSpace(context, 1, 16));

        TextView trendHeader = new TextView(context);
        trendHeader.setText("Spending Activity Trend");
        trendHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        trendHeader.setTypeface(Typeface.DEFAULT_BOLD);
        trendHeader.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        root.addView(trendHeader);

        root.addView(UiUtils.makeSpace(context, 1, 10));

        LinearLayout barCard = new LinearLayout(context);
        barCard.setOrientation(LinearLayout.VERTICAL);
        barCard.setPadding(cPad, cPad, cPad, cPad);
        barCard.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 20f));
        InteractiveBarChartView barChart = new InteractiveBarChartView(context, isDark);
        barChart.setData(data.barChartPoints, isDark);
        barCard.addView(barChart);
        root.addView(barCard);

        scroll.addView(root);
        return scroll;
    }

    private static TextView makeEqualPeriodChip(
            Context context,
            String text,
            boolean isSelected,
            boolean isDark,
            Runnable onClick
    ) {
        TextView tv = new TextView(context);
        tv.setText(isSelected ? "✓ " + text : text);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine(true);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tv.setPadding(UiUtils.dp(context, 8), UiUtils.dp(context, 12), UiUtils.dp(context, 8), UiUtils.dp(context, 12));
        if (isSelected) {
            tv.setTextColor(Color.WHITE);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv.setBackground(UiUtils.makeRoundedRectWithStroke(
                    context,
                    UiUtils.COLOR_PRIMARY_TEAL,
                    12f,
                    UiUtils.COLOR_PRIMARY_TEAL_DARK,
                    2f
            ));
        } else {
            tv.setTextColor(UiUtils.getTextPrimaryColor(isDark));
            tv.setTypeface(Typeface.DEFAULT);
            tv.setBackground(UiUtils.makeRoundedRectWithStroke(
                    context,
                    UiUtils.getCardColor(isDark),
                    12f,
                    UiUtils.getOutlineColor(isDark),
                    1f
            ));
        }
        UiUtils.applyBounceClick(tv, onClick);
        return tv;
    }
}
