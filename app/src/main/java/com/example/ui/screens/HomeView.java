package com.example.ui.screens;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.data.model.Goal;
import com.example.data.model.Transaction;
import com.example.data.model.TransactionType;
import com.example.data.model.UserProfile;
import com.example.ui.components.ModuleIconView;
import com.example.ui.components.TransactionRowView;
import com.example.ui.components.UiUtils;
import com.example.ui.viewmodel.AppScreen;
import com.example.ui.viewmodel.DashboardData;
import com.example.ui.viewmodel.PocketPalViewModel;
import java.util.List;

public class HomeView {

    public static View create(final Context context, final PocketPalViewModel viewModel) {
        final DashboardData data = viewModel.getDashboardData();
        final boolean isDark = data.preferences != null && data.preferences.isDarkMode;
        final UserProfile user = data.activeUser;

        ScrollView scroll = new ScrollView(context);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(UiUtils.getBgColor(isDark));

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = UiUtils.dp(context, 18);
        root.setPadding(pad, UiUtils.dp(context, 18), pad, UiUtils.dp(context, 24));

        // 1. Clean Top Greeting Header (matches reference screenshot)
        LinearLayout headerRow = new LinearLayout(context);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout greetCol = new LinearLayout(context);
        greetCol.setOrientation(LinearLayout.VERTICAL);
        greetCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView greetTv = new TextView(context);
        String userName = user != null ? user.name : "Student";
        greetTv.setText("Hello, " + userName + "! ");
        greetTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        greetTv.setTypeface(Typeface.DEFAULT_BOLD);
        greetTv.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        greetCol.addView(greetTv);

        greetCol.addView(UiUtils.makeSpace(context, 1, 2));

        TextView subGreetTv = new TextView(context);
        subGreetTv.setText("Keep going! You're doing great!");
        subGreetTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        subGreetTv.setTextColor(UiUtils.getTextSecondaryColor(isDark));
        greetCol.addView(subGreetTv);

        headerRow.addView(greetCol);

        // Right circular avatar button (opens profile switcher)
        FrameLayout avatarWrap = new FrameLayout(context);
        avatarWrap.setTag("btn_user_switcher");
        int avOuter = UiUtils.dp(context, 52);
        avatarWrap.setLayoutParams(new LinearLayout.LayoutParams(avOuter, avOuter));
        avatarWrap.setBackground(UiUtils.makeCircleWithStroke(
                context,
                isDark ? 0xFF1E332D : 0xFFBAE6FD,
                isDark ? 0xFF2ED3B7 : 0xFF7DD3FC,
                2f
        ));
        avatarWrap.setElevation(UiUtils.dp(context, 2));

        TextView avTv = new TextView(context);
        avTv.setText(user != null ? user.avatarEmoji : "🎓");
        avTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        avTv.setGravity(Gravity.CENTER);
        avatarWrap.addView(avTv, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        UiUtils.applyBounceClick(avatarWrap, new Runnable() {
            @Override
            public void run() {
                viewModel.setShowUserSwitcher(true);
            }
        });
        headerRow.addView(avatarWrap);

        root.addView(headerRow);
        root.addView(UiUtils.makeSpace(context, 1, 18));

        // 2. Module 1: "This Month's Pocket Money" White Card with Green -> Yellow -> Red Gradient Slider
        LinearLayout pocketCard = new LinearLayout(context);
        pocketCard.setOrientation(LinearLayout.VERTICAL);
        int cardPad = UiUtils.dp(context, 20);
        pocketCard.setPadding(cardPad, cardPad, cardPad, cardPad);
        pocketCard.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 24f));
        pocketCard.setElevation(UiUtils.dp(context, 3));

        LinearLayout topPocketRow = new LinearLayout(context);
        topPocketRow.setOrientation(LinearLayout.HORIZONTAL);
        topPocketRow.setGravity(Gravity.CENTER_VERTICAL);

        ModuleIconView walletIcon = new ModuleIconView(context, ModuleIconView.TYPE_WALLET);
        int walletSz = UiUtils.dp(context, 66);
        walletIcon.setLayoutParams(new LinearLayout.LayoutParams(walletSz, walletSz));
        topPocketRow.addView(walletIcon);

        topPocketRow.addView(UiUtils.makeSpace(context, 14, 1));

        LinearLayout pocketInfoCol = new LinearLayout(context);
        pocketInfoCol.setOrientation(LinearLayout.VERTICAL);
        pocketInfoCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView pmLabel = new TextView(context);
        pmLabel.setText("This Month's Pocket Money");
        pmLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        pmLabel.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        pmLabel.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        pocketInfoCol.addView(pmLabel);

        pocketInfoCol.addView(UiUtils.makeSpace(context, 1, 2));

        // Main balance displays positive value initially, only showing '-' if expenses exceed the total limit
        double monthlyBudget = user != null && user.monthlyBudget > 0 ? Math.abs(user.monthlyBudget) : 5000.0;
        double totalPocketLimit = monthlyBudget + data.totalIncomeThisMonth;
        double currentPocketBalance = totalPocketLimit - data.totalSpentThisMonth;

        TextView pmValue = new TextView(context);
        pmValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        pmValue.setTypeface(Typeface.DEFAULT_BOLD);
        pmValue.setTag("tv_total_pocket_balance");
        if (Math.round(currentPocketBalance) < 0) {
            pmValue.setTextColor(UiUtils.COLOR_EXPENSE_RED);
            UiUtils.animateCounter(pmValue, 0.0, Math.abs(currentPocketBalance), "-₹", "", 600);
        } else {
            pmValue.setTextColor(UiUtils.getTextPrimaryColor(isDark));
            UiUtils.animateCounter(pmValue, 0.0, Math.max(0, currentPocketBalance), "₹", "", 600);
        }
        pocketInfoCol.addView(pmValue);

        TextView limitSub = new TextView(context);
        if (data.totalIncomeThisMonth > 0) {
            limitSub.setText("Monthly limit • ₹" + UiUtils.formatAmount(totalPocketLimit) + " (incl. +₹" + UiUtils.formatAmount(data.totalIncomeThisMonth) + " income)");
        } else {
            limitSub.setText("Monthly limit");
        }
        limitSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        limitSub.setTextColor(UiUtils.getTextSecondaryColor(isDark));
        pocketInfoCol.addView(limitSub);

        topPocketRow.addView(pocketInfoCol);
        pocketCard.addView(topPocketRow);

        pocketCard.addView(UiUtils.makeSpace(context, 1, 16));

        // Gradient Progress Slider (0% Green -> 50% Yellow -> 100% Red)
        final float usedFraction = Math.max(0f, Math.min(1f, data.budgetUsedFraction));
        final FrameLayout sliderTrack = new FrameLayout(context);
        sliderTrack.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                UiUtils.dp(context, 12)
        ));
        sliderTrack.setBackground(UiUtils.makeRoundedRect(
                context,
                isDark ? 0xFF1E332D : 0xFFE8EEF5,
                6f
        ));

        final View sliderFill = new View(context);
        sliderFill.setBackground(UiUtils.makeBudgetSliderGradient(context, usedFraction, 6f));
        sliderTrack.addView(sliderFill);
        sliderTrack.post(new Runnable() {
            @Override
            public void run() {
                float displayRatio = usedFraction <= 0f ? 0.04f : Math.max(0.06f, usedFraction);
                final int fillW = (int) (sliderTrack.getWidth() * displayRatio);
                if (UiUtils.areAnimationsEnabled(context)) {
                    android.animation.ValueAnimator anim = android.animation.ValueAnimator.ofInt(0, fillW);
                    anim.setDuration(600);
                    anim.setInterpolator(new android.view.animation.DecelerateInterpolator());
                    anim.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                            int w = (Integer) animation.getAnimatedValue();
                            sliderFill.setLayoutParams(new FrameLayout.LayoutParams(w, FrameLayout.LayoutParams.MATCH_PARENT));
                        }
                    });
                    anim.start();
                } else {
                    sliderFill.setLayoutParams(new FrameLayout.LayoutParams(fillW, FrameLayout.LayoutParams.MATCH_PARENT));
                }
            }
        });
        pocketCard.addView(sliderTrack);

        pocketCard.addView(UiUtils.makeSpace(context, 1, 10));

        // Bottom Spent (Left) vs Total Amount (Right) Row
        LinearLayout spentLeftRow = new LinearLayout(context);
        spentLeftRow.setOrientation(LinearLayout.HORIZONTAL);
        spentLeftRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView spentTv = new TextView(context);
        spentTv.setText(buildAmountWithSuffix(
                "₹" + UiUtils.formatAmount(data.totalSpentThisMonth),
                " spent",
                UiUtils.getTextPrimaryColor(isDark),
                UiUtils.getTextSecondaryColor(isDark)
        ));
        spentTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        spentTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        spentLeftRow.addView(spentTv);

        TextView totalRightTv = new TextView(context);
        totalRightTv.setText(buildAmountWithSuffix(
                "₹" + UiUtils.formatAmount(totalPocketLimit),
                " total",
                UiUtils.getTextPrimaryColor(isDark),
                UiUtils.getTextSecondaryColor(isDark)
        ));
        totalRightTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        totalRightTv.setGravity(Gravity.END);
        spentLeftRow.addView(totalRightTv);

        pocketCard.addView(spentLeftRow);
        root.addView(pocketCard);

        root.addView(UiUtils.makeSpace(context, 1, 16));

        // Voice Smart Entry Banner
        LinearLayout voiceBanner = new LinearLayout(context);
        voiceBanner.setOrientation(LinearLayout.HORIZONTAL);
        voiceBanner.setGravity(Gravity.CENTER_VERTICAL);
        int vPadH = UiUtils.dp(context, 16);
        int vPadV = UiUtils.dp(context, 14);
        voiceBanner.setPadding(vPadH, vPadV, vPadH, vPadV);
        voiceBanner.setBackground(UiUtils.makeRoundedRectWithStroke(
                context,
                isDark ? 0xFF04382E : UiUtils.COLOR_PRIMARY_TEAL_CONTAINER,
                20f,
                UiUtils.COLOR_PRIMARY_TEAL,
                1.5f
        ));
        voiceBanner.setElevation(UiUtils.dp(context, 2.5f));
        voiceBanner.setTag("btn_voice_entry_banner");

        TextView micIconBadge = new TextView(context);
        micIconBadge.setText("🎙️");
        micIconBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        micIconBadge.setGravity(Gravity.CENTER);
        int micSz = UiUtils.dp(context, 46);
        micIconBadge.setLayoutParams(new LinearLayout.LayoutParams(micSz, micSz));
        micIconBadge.setBackground(UiUtils.makeCircle(UiUtils.COLOR_PRIMARY_TEAL));
        voiceBanner.addView(micIconBadge);

        voiceBanner.addView(UiUtils.makeSpace(context, 12, 1));

        LinearLayout vInfoCol = new LinearLayout(context);
        vInfoCol.setOrientation(LinearLayout.VERTICAL);
        vInfoCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView vTitle = new TextView(context);
        vTitle.setText("Voice Smart Entry");
        vTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        vTitle.setTypeface(Typeface.DEFAULT_BOLD);
        vTitle.setTextColor(isDark ? 0xFF2ED3B7 : UiUtils.COLOR_ON_PRIMARY_TEAL_CONTAINER);
        vInfoCol.addView(vTitle);

        TextView vSub = new TextView(context);
        vSub.setText("Record expenses instantly using voice");
        vSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        vSub.setTextColor(isDark ? 0xFF94A3B8 : 0xFF065F51);
        vInfoCol.addView(vSub);

        voiceBanner.addView(vInfoCol);

        TextView speakBtn = new TextView(context);
        speakBtn.setText("Speak 🎙️");
        speakBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        speakBtn.setTypeface(Typeface.DEFAULT_BOLD);
        speakBtn.setTextColor(Color.WHITE);
        speakBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 8), UiUtils.dp(context, 12), UiUtils.dp(context, 8));
        speakBtn.setBackground(UiUtils.makeRoundedRect(context, UiUtils.COLOR_PRIMARY_TEAL, 12f));
        voiceBanner.addView(speakBtn);

        UiUtils.applyBounceClick(voiceBanner, new Runnable() {
            @Override
            public void run() {
                if (context instanceof android.app.Activity) {
                    com.example.ui.components.VoiceConfirmationDialogHelper.startVoiceEntry((android.app.Activity) context, viewModel);
                }
            }
        });

        root.addView(voiceBanner);
        root.addView(UiUtils.makeSpace(context, 1, 12));

        // Check Before Buying Banner
        LinearLayout decisionBanner = new LinearLayout(context);
        decisionBanner.setOrientation(LinearLayout.HORIZONTAL);
        decisionBanner.setGravity(Gravity.CENTER_VERTICAL);
        decisionBanner.setPadding(vPadH, vPadV, vPadH, vPadV);
        decisionBanner.setBackground(UiUtils.makeRoundedRectWithStroke(
                context,
                isDark ? 0xFF1E332D : 0xFFFEF3C7,
                20f,
                0xFFF59E0B,
                1.5f
        ));
        decisionBanner.setElevation(UiUtils.dp(context, 2.5f));
        decisionBanner.setTag("btn_check_before_buying");

        TextView decIconBadge = new TextView(context);
        decIconBadge.setText("🤔");
        decIconBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        decIconBadge.setGravity(Gravity.CENTER);
        decIconBadge.setLayoutParams(new LinearLayout.LayoutParams(micSz, micSz));
        decIconBadge.setBackground(UiUtils.makeCircle(0xFFF59E0B));
        decisionBanner.addView(decIconBadge);

        decisionBanner.addView(UiUtils.makeSpace(context, 12, 1));

        LinearLayout dInfoCol = new LinearLayout(context);
        dInfoCol.setOrientation(LinearLayout.VERTICAL);
        dInfoCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView dTitle = new TextView(context);
        dTitle.setText("Check Before Buying");
        dTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        dTitle.setTypeface(Typeface.DEFAULT_BOLD);
        dTitle.setTextColor(isDark ? 0xFFFCD34D : 0xFF78350F);
        dInfoCol.addView(dTitle);

        TextView dSub = new TextView(context);
        dSub.setText("Smart purchase check & advice");
        dSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        dSub.setTextColor(isDark ? 0xFF94A3B8 : 0xFF92400E);
        dInfoCol.addView(dSub);

        decisionBanner.addView(dInfoCol);

        TextView checkNavBtn = new TextView(context);
        checkNavBtn.setText("Check 🔍");
        checkNavBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        checkNavBtn.setTypeface(Typeface.DEFAULT_BOLD);
        checkNavBtn.setTextColor(Color.WHITE);
        checkNavBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 8), UiUtils.dp(context, 8), UiUtils.dp(context, 8));
        checkNavBtn.setBackground(UiUtils.makeRoundedRect(context, 0xFFF59E0B, 12f));
        decisionBanner.addView(checkNavBtn);

        UiUtils.applyBounceClick(decisionBanner, new Runnable() {
            @Override
            public void run() {
                if (context instanceof android.app.Activity) {
                    com.example.ui.components.InstantDecisionDialogHelper.show((android.app.Activity) context, viewModel);
                }
            }
        });

        root.addView(decisionBanner);
        root.addView(UiUtils.makeSpace(context, 1, 16));

        // 3. Module 2: 4 Clean Quick-Action Cards (Add Expense | Add Income | Set Goal | View Reports)
        LinearLayout actionsRow = new LinearLayout(context);
        actionsRow.setOrientation(LinearLayout.HORIZONTAL);
        actionsRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        View addExpenseCard = makeQuickModuleCard(
                context,
                ModuleIconView.TYPE_ADD_EXPENSE,
                "Add\nExpense",
                isDark,
                "btn_quick_add_expense",
                new Runnable() {
                    @Override
                    public void run() {
                        viewModel.startAddTransactionWithType(TransactionType.EXPENSE);
                    }
                }
        );
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        addExpenseCard.setLayoutParams(lp1);
        actionsRow.addView(addExpenseCard);

        actionsRow.addView(UiUtils.makeSpace(context, 10, 1));

        View addIncomeCard = makeQuickModuleCard(
                context,
                ModuleIconView.TYPE_ADD_INCOME,
                "Add\nIncome",
                isDark,
                "btn_quick_add_income",
                new Runnable() {
                    @Override
                    public void run() {
                        viewModel.startAddTransactionWithType(TransactionType.INCOME);
                    }
                }
        );
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        addIncomeCard.setLayoutParams(lp2);
        actionsRow.addView(addIncomeCard);

        actionsRow.addView(UiUtils.makeSpace(context, 10, 1));

        View setGoalCard = makeQuickModuleCard(
                context,
                ModuleIconView.TYPE_SET_GOAL,
                "Set\nGoal",
                isDark,
                "btn_quick_set_goal",
                new Runnable() {
                    @Override
                    public void run() {
                        viewModel.navigateTo(AppScreen.GOALS);
                    }
                }
        );
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        setGoalCard.setLayoutParams(lp3);
        actionsRow.addView(setGoalCard);

        actionsRow.addView(UiUtils.makeSpace(context, 10, 1));

        View viewReportsCard = makeQuickModuleCard(
                context,
                ModuleIconView.TYPE_VIEW_REPORTS,
                "View\nReports",
                isDark,
                "btn_full_breakdown",
                new Runnable() {
                    @Override
                    public void run() {
                        viewModel.navigateTo(AppScreen.SPENDING_BREAKDOWN);
                    }
                }
        );
        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        viewReportsCard.setLayoutParams(lp4);
        actionsRow.addView(viewReportsCard);

        root.addView(actionsRow);

        root.addView(UiUtils.makeSpace(context, 1, 18));

        // Active Savings Goals Section Card
        List<Goal> activeGoals = data.goals;
        LinearLayout goalsContainerCard = new LinearLayout(context);
        goalsContainerCard.setOrientation(LinearLayout.VERTICAL);
        int gPad = UiUtils.dp(context, 18);
        goalsContainerCard.setPadding(gPad, gPad, gPad, gPad);
        goalsContainerCard.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 24f));
        goalsContainerCard.setElevation(UiUtils.dp(context, 2.5f));

        LinearLayout goalsHeader = new LinearLayout(context);
        goalsHeader.setOrientation(LinearLayout.HORIZONTAL);
        goalsHeader.setGravity(Gravity.CENTER_VERTICAL);

        TextView gTitleHeader = new TextView(context);
        gTitleHeader.setText("Savings Goals 🎯");
        gTitleHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        gTitleHeader.setTypeface(Typeface.DEFAULT_BOLD);
        gTitleHeader.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        gTitleHeader.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        goalsHeader.addView(gTitleHeader);

        TextView viewAllGoalsBtn = new TextView(context);
        viewAllGoalsBtn.setText("Manage →");
        viewAllGoalsBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        viewAllGoalsBtn.setTypeface(Typeface.DEFAULT_BOLD);
        viewAllGoalsBtn.setTextColor(UiUtils.COLOR_PRIMARY_TEAL);
        UiUtils.applyBounceClick(viewAllGoalsBtn, new Runnable() {
            @Override
            public void run() {
                viewModel.navigateTo(AppScreen.GOALS);
            }
        });
        goalsHeader.addView(viewAllGoalsBtn);
        goalsContainerCard.addView(goalsHeader);

        goalsContainerCard.addView(UiUtils.makeSpace(context, 1, 12));

        if (activeGoals.isEmpty()) {
            TextView noGoalsTv = new TextView(context);
            noGoalsTv.setText("No active goal jars yet. Tap 'Set Goal' to start saving!");
            noGoalsTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            noGoalsTv.setTextColor(UiUtils.getTextSecondaryColor(isDark));
            goalsContainerCard.addView(noGoalsTv);
        } else {
            int displayCount = Math.min(2, activeGoals.size());
            for (int i = 0; i < displayCount; i++) {
                final Goal g = activeGoals.get(i);
                LinearLayout gRow = new LinearLayout(context);
                gRow.setOrientation(LinearLayout.VERTICAL);
                gRow.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 10), UiUtils.dp(context, 12), UiUtils.dp(context, 10));
                gRow.setBackground(UiUtils.makeRoundedRect(context, isDark ? 0xFF1A2C27 : 0xFFF8FAFC, 16f));

                LinearLayout gTop = new LinearLayout(context);
                gTop.setOrientation(LinearLayout.HORIZONTAL);
                gTop.setGravity(Gravity.CENTER_VERTICAL);

                TextView gBadge = new TextView(context);
                gBadge.setText(g.emoji);
                gBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                gTop.addView(gBadge);

                gTop.addView(UiUtils.makeSpace(context, 8, 1));

                TextView gName = new TextView(context);
                gName.setText(g.title);
                gName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                gName.setTypeface(Typeface.DEFAULT_BOLD);
                gName.setTextColor(UiUtils.getTextPrimaryColor(isDark));
                gName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                gTop.addView(gName);

                TextView gProg = new TextView(context);
                gProg.setText("₹" + (int) g.savedAmount + " / ₹" + (int) g.targetAmount + " (" + g.getProgressPercentage() + "%)");
                gProg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                gProg.setTypeface(Typeface.DEFAULT_BOLD);
                gProg.setTextColor(UiUtils.COLOR_PRIMARY_TEAL);
                gTop.addView(gProg);

                gRow.addView(gTop);
                gRow.addView(UiUtils.makeSpace(context, 1, 8));

                final FrameLayout gTrack = new FrameLayout(context);
                gTrack.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        UiUtils.dp(context, 8)
                ));
                gTrack.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getSurfaceVariantColor(isDark), 4f));

                final View gFill = new View(context);
                gFill.setBackground(UiUtils.makeRoundedRect(context, (int) g.colorHex, 4f));
                gTrack.addView(gFill);
                gTrack.post(new Runnable() {
                    @Override
                    public void run() {
                        float ratio = Math.max(0.03f, g.getProgressFraction());
                        int fillWidth = (int) (gTrack.getWidth() * ratio);
                        gFill.setLayoutParams(new FrameLayout.LayoutParams(fillWidth, FrameLayout.LayoutParams.MATCH_PARENT));
                    }
                });
                gRow.addView(gTrack);

                goalsContainerCard.addView(gRow);
                if (i < displayCount - 1) {
                    goalsContainerCard.addView(UiUtils.makeSpace(context, 1, 8));
                }
            }
        }

        root.addView(goalsContainerCard);
        root.addView(UiUtils.makeSpace(context, 1, 18));

        // 4. Module 3: "Recent Expenses" Large Clean White Container Card
        LinearLayout recentContainerCard = new LinearLayout(context);
        recentContainerCard.setOrientation(LinearLayout.VERTICAL);
        int rPad = UiUtils.dp(context, 18);
        recentContainerCard.setPadding(rPad, rPad, rPad, rPad);
        recentContainerCard.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 24f));
        recentContainerCard.setElevation(UiUtils.dp(context, 2.5f));
        recentContainerCard.setMinimumHeight(UiUtils.dp(context, 260));

        LinearLayout recentHeader = new LinearLayout(context);
        recentHeader.setOrientation(LinearLayout.HORIZONTAL);
        recentHeader.setGravity(Gravity.CENTER_VERTICAL);

        TextView recTitle = new TextView(context);
        recTitle.setText("Recent Expenses");
        recTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        recTitle.setTypeface(Typeface.DEFAULT_BOLD);
        recTitle.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        recTitle.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        recentHeader.addView(recTitle);

        TextView seeAllBtn = new TextView(context);
        seeAllBtn.setText("See all →");
        seeAllBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        seeAllBtn.setTypeface(Typeface.DEFAULT_BOLD);
        seeAllBtn.setTextColor(0xFF10B981);
        seeAllBtn.setTag("btn_see_all_transactions");
        UiUtils.applyBounceClick(seeAllBtn, new Runnable() {
            @Override
            public void run() {
                viewModel.navigateTo(AppScreen.TRANSACTIONS);
            }
        });
        recentHeader.addView(seeAllBtn);
        recentContainerCard.addView(recentHeader);

        recentContainerCard.addView(UiUtils.makeSpace(context, 1, 12));

        List<Transaction> allTx = data.allTransactions;
        if (allTx.isEmpty()) {
            LinearLayout emptyBox = new LinearLayout(context);
            emptyBox.setOrientation(LinearLayout.VERTICAL);
            emptyBox.setGravity(Gravity.CENTER);
            emptyBox.setPadding(UiUtils.dp(context, 16), UiUtils.dp(context, 36), UiUtils.dp(context, 16), UiUtils.dp(context, 36));

            TextView emTitle = new TextView(context);
            emTitle.setText("No expenses or income logged yet");
            emTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            emTitle.setTypeface(Typeface.DEFAULT_BOLD);
            emTitle.setTextColor(UiUtils.getTextPrimaryColor(isDark));
            emTitle.setGravity(Gravity.CENTER);
            emptyBox.addView(emTitle);

            emptyBox.addView(UiUtils.makeSpace(context, 1, 4));

            TextView emSub = new TextView(context);
            emSub.setText("Tap 'Add Expense' or 'Add Income' above to log your first entry.");
            emSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            emSub.setTextColor(UiUtils.getTextSecondaryColor(isDark));
            emSub.setGravity(Gravity.CENTER);
            emptyBox.addView(emSub);

            recentContainerCard.addView(emptyBox);
        } else {
            int count = Math.min(5, allTx.size());
            for (int i = 0; i < count; i++) {
                final Transaction tx = allTx.get(i);
                View rowView = TransactionRowView.create(
                        context,
                        tx,
                        isDark,
                        new Runnable() {
                            @Override
                            public void run() {
                                viewModel.startEditTransaction(tx);
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                viewModel.deleteTransaction(tx);
                            }
                        }
                );
                rowView.setBackground(UiUtils.makeRoundedRect(
                        context,
                        isDark ? 0xFF1A2C27 : 0xFFF8FAFC,
                        16f
                ));
                rowView.setElevation(0f);
                recentContainerCard.addView(rowView);
                if (i < count - 1) {
                    recentContainerCard.addView(UiUtils.makeSpace(context, 1, 8));
                }
            }
        }

        root.addView(recentContainerCard);

        UiUtils.animateEntrance(headerRow, 0);
        UiUtils.animateEntrance(pocketCard, 60);
        UiUtils.animateEntrance(voiceBanner, 120);
        UiUtils.animateEntrance(decisionBanner, 150);
        UiUtils.animateEntrance(actionsRow, 180);
        UiUtils.animateEntrance(goalsContainerCard, 210);
        UiUtils.animateEntrance(recentContainerCard, 240);
        UiUtils.animateEntrance(actionsRow, 180);
        UiUtils.animateEntrance(recentContainerCard, 240);

        scroll.addView(root);
        return scroll;
    }

    private static View makeQuickModuleCard(
            Context context,
            int iconType,
            String labelText,
            boolean isDark,
            String tag,
            Runnable onClick
    ) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setTag(tag);
        int padH = UiUtils.dp(context, 8);
        int padV = UiUtils.dp(context, 14);
        card.setPadding(padH, padV, padH, padV);
        card.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 20f));
        card.setElevation(UiUtils.dp(context, 2.5f));

        ModuleIconView iconView = new ModuleIconView(context, iconType);
        int iconSz = UiUtils.dp(context, 52);
        iconView.setLayoutParams(new LinearLayout.LayoutParams(iconSz, iconSz));
        card.addView(iconView);

        card.addView(UiUtils.makeSpace(context, 1, 10));

        TextView label = new TextView(context);
        label.setText(labelText);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        label.setTypeface(Typeface.DEFAULT_BOLD);
        label.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        label.setGravity(Gravity.CENTER);
        label.setLineSpacing(0f, 1.05f);
        card.addView(label);

        UiUtils.applyBounceClick(card, onClick);
        return card;
    }

    private static CharSequence buildAmountWithSuffix(
            String amountPart,
            String suffixPart,
            int amountColor,
            int suffixColor
    ) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(amountPart);
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, amountPart.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new ForegroundColorSpan(amountColor), 0, amountPart.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int start = ssb.length();
        ssb.append(suffixPart);
        ssb.setSpan(new ForegroundColorSpan(suffixColor), start, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }
}
