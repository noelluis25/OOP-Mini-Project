package com.example.ui.screens;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.data.model.Goal;
import com.example.ui.components.UiUtils;
import com.example.ui.viewmodel.DashboardData;
import com.example.ui.viewmodel.PocketPalViewModel;
import java.util.List;

public class GoalsView {

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

        LinearLayout headerRow = new LinearLayout(context);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout leftCol = new LinearLayout(context);
        leftCol.setOrientation(LinearLayout.VERTICAL);
        leftCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(context);
        title.setText("Savings Goals 🎯");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        leftCol.addView(title);

        TextView sub = new TextView(context);
        sub.setText("Save up for gadgets, trips, and textbooks");
        sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        sub.setTextColor(UiUtils.getTextSecondaryColor(isDark));
        leftCol.addView(sub);
        headerRow.addView(leftCol);

        TextView newGoalBtn = new TextView(context);
        newGoalBtn.setText("＋ New Goal");
        newGoalBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        newGoalBtn.setTypeface(Typeface.DEFAULT_BOLD);
        newGoalBtn.setTextColor(Color.WHITE);
        newGoalBtn.setPadding(UiUtils.dp(context, 14), UiUtils.dp(context, 10), UiUtils.dp(context, 14), UiUtils.dp(context, 10));
        newGoalBtn.setBackground(UiUtils.makeRoundedRect(context, UiUtils.COLOR_PRIMARY_TEAL, 14f));
        newGoalBtn.setTag("btn_add_goal");
        UiUtils.applyBounceClick(newGoalBtn, new Runnable() {
            @Override
            public void run() {
                showNewGoalDialog(context, viewModel);
            }
        });
        headerRow.addView(newGoalBtn);
        root.addView(headerRow);

        root.addView(UiUtils.makeSpace(context, 1, 14));

        // Financial Balance Breakdown Card
        LinearLayout summaryCard = new LinearLayout(context);
        summaryCard.setOrientation(LinearLayout.VERTICAL);
        int sumPad = UiUtils.dp(context, 14);
        summaryCard.setPadding(sumPad, sumPad, sumPad, sumPad);
        summaryCard.setBackground(UiUtils.makeRoundedRectWithStroke(
                context,
                isDark ? 0xFF13221E : 0xFFF8FAFC,
                18f,
                isDark ? 0xFF1E332D : 0xFFE2E8F0,
                1.5f
        ));
        summaryCard.setElevation(UiUtils.dp(context, 2));

        TextView sumTitle = new TextView(context);
        sumTitle.setText("Financial Balance Breakdown");
        sumTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        sumTitle.setTypeface(Typeface.DEFAULT_BOLD);
        sumTitle.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        summaryCard.addView(sumTitle);

        summaryCard.addView(UiUtils.makeSpace(context, 1, 8));

        LinearLayout sumGrid = new LinearLayout(context);
        sumGrid.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout col1 = makeSummaryCol(context, "💰 Total Money", "₹" + UiUtils.formatAmount(data.totalMoney), 0xFF0F1E36, isDark);
        LinearLayout col2 = makeSummaryCol(context, "🏺 Goal Savings", "₹" + UiUtils.formatAmount(data.totalSavedInGoals), UiUtils.COLOR_PRIMARY_TEAL, isDark);
        LinearLayout col3 = makeSummaryCol(context, "💳 Available Spend", "₹" + UiUtils.formatAmount(data.currentBalance), 0xFF10B981, isDark);

        LinearLayout.LayoutParams sumLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        col1.setLayoutParams(sumLp);
        col2.setLayoutParams(sumLp);
        col3.setLayoutParams(sumLp);

        sumGrid.addView(col1);
        sumGrid.addView(col2);
        sumGrid.addView(col3);
        summaryCard.addView(sumGrid);

        root.addView(summaryCard);
        root.addView(UiUtils.makeSpace(context, 1, 16));

        List<Goal> goals = data.goals;
        if (goals.isEmpty()) {
            LinearLayout empty = new LinearLayout(context);
            empty.setOrientation(LinearLayout.VERTICAL);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(UiUtils.dp(context, 20), UiUtils.dp(context, 32), UiUtils.dp(context, 20), UiUtils.dp(context, 32));
            empty.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 20f));

            TextView emIcon = new TextView(context);
            emIcon.setText("🏺");
            emIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 34);
            empty.addView(emIcon);

            TextView emTitle = new TextView(context);
            emTitle.setText("No savings jars created yet");
            emTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            emTitle.setTypeface(Typeface.DEFAULT_BOLD);
            emTitle.setTextColor(UiUtils.getTextPrimaryColor(isDark));
            empty.addView(emTitle);

            TextView emSub = new TextView(context);
            emSub.setText("Tap '+ New Goal' above to create a target for headphones, a college trip, or an emergency fund!");
            emSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            emSub.setTextColor(UiUtils.getTextSecondaryColor(isDark));
            emSub.setGravity(Gravity.CENTER);
            empty.addView(emSub);

            root.addView(empty);
        } else {
            for (final Goal goal : goals) {
                LinearLayout card = new LinearLayout(context);
                card.setOrientation(LinearLayout.VERTICAL);
                int cPad = UiUtils.dp(context, 16);
                card.setPadding(cPad, cPad, cPad, cPad);
                card.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 20f));
                card.setElevation(UiUtils.dp(context, 2));

                LinearLayout top = new LinearLayout(context);
                top.setOrientation(LinearLayout.HORIZONTAL);
                top.setGravity(Gravity.CENTER_VERTICAL);

                TextView badge = new TextView(context);
                badge.setText(goal.emoji);
                badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                badge.setGravity(Gravity.CENTER);
                int sz = UiUtils.dp(context, 46);
                badge.setLayoutParams(new LinearLayout.LayoutParams(sz, sz));
                badge.setBackground(UiUtils.makeRoundedRect(context, ((int) goal.colorHex & 0x00FFFFFF) | 0x26000000, 14f));
                top.addView(badge);

                top.addView(UiUtils.makeSpace(context, 12, 1));

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);
                info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                TextView gTitle = new TextView(context);
                gTitle.setText(goal.title);
                gTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                gTitle.setTypeface(Typeface.DEFAULT_BOLD);
                gTitle.setTextColor(UiUtils.getTextPrimaryColor(isDark));
                info.addView(gTitle);

                TextView gSub = new TextView(context);
                gSub.setText("Saved ₹" + (int) goal.savedAmount + " of ₹" + (int) goal.targetAmount + " (" + goal.getProgressPercentage() + "%)");
                gSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                gSub.setTextColor(UiUtils.getTextSecondaryColor(isDark));
                info.addView(gSub);

                top.addView(info);

                TextView editBtn = new TextView(context);
                editBtn.setText("✏️");
                editBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                editBtn.setPadding(UiUtils.dp(context, 6), UiUtils.dp(context, 4), UiUtils.dp(context, 6), UiUtils.dp(context, 4));
                UiUtils.applyBounceClick(editBtn, new Runnable() {
                    @Override
                    public void run() {
                        showEditGoalDialog(context, viewModel, goal);
                    }
                });
                top.addView(editBtn);

                TextView delBtn = new TextView(context);
                delBtn.setText("🗑");
                delBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                delBtn.setPadding(UiUtils.dp(context, 6), UiUtils.dp(context, 4), UiUtils.dp(context, 6), UiUtils.dp(context, 4));
                UiUtils.applyBounceClick(delBtn, new Runnable() {
                    @Override
                    public void run() {
                        viewModel.deleteGoal(goal);
                    }
                });
                top.addView(delBtn);
                card.addView(top);

                card.addView(UiUtils.makeSpace(context, 1, 10));

                final FrameLayout barTrack = new FrameLayout(context);
                barTrack.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        UiUtils.dp(context, 10)
                ));
                barTrack.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getSurfaceVariantColor(isDark), 5f));

                final View barFill = new View(context);
                barFill.setBackground(UiUtils.makeRoundedRect(context, (int) goal.colorHex, 5f));
                barTrack.addView(barFill);
                barTrack.post(new Runnable() {
                    @Override
                    public void run() {
                        float ratio = Math.max(0.03f, goal.getProgressFraction());
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
                card.addView(barTrack);

                if (goal.savedAmount < goal.targetAmount) {
                    card.addView(UiUtils.makeSpace(context, 1, 12));

                    // Quick Buttons (+₹50, +₹100, +₹500)
                    LinearLayout addRow = new LinearLayout(context);
                    addRow.setOrientation(LinearLayout.HORIZONTAL);
                    int[] steps = {50, 100, 500};
                    for (int i = 0; i < steps.length; i++) {
                        final int step = steps[i];
                        TextView btn = new TextView(context);
                        btn.setText("+₹" + step);
                        btn.setGravity(Gravity.CENTER);
                        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        btn.setTypeface(Typeface.DEFAULT_BOLD);
                        btn.setTextColor(UiUtils.COLOR_PRIMARY_TEAL);
                        btn.setPadding(UiUtils.dp(context, 10), UiUtils.dp(context, 8), UiUtils.dp(context, 10), UiUtils.dp(context, 8));
                        btn.setBackground(UiUtils.makeRoundedRect(
                                context,
                                isDark ? 0xFF04382E : UiUtils.COLOR_PRIMARY_TEAL_CONTAINER,
                                10f
                        ));
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                        if (i > 0) lp.leftMargin = UiUtils.dp(context, 6);
                        btn.setLayoutParams(lp);
                        UiUtils.applyBounceClick(btn, new Runnable() {
                            @Override
                            public void run() {
                                boolean success = viewModel.contributeToGoal(goal, step);
                                if (!success) {
                                    android.widget.Toast.makeText(context, "Insufficient wallet balance! Available: ₹" + (int) data.currentBalance, android.widget.Toast.LENGTH_SHORT).show();
                                } else {
                                    android.widget.Toast.makeText(context, "Added ₹" + step + " to " + goal.title + "! 🎯", android.widget.Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        addRow.addView(btn);
                    }
                    card.addView(addRow);

                    card.addView(UiUtils.makeSpace(context, 1, 8));

                    // Custom Savings Input Row
                    LinearLayout customRow = new LinearLayout(context);
                    customRow.setOrientation(LinearLayout.HORIZONTAL);
                    customRow.setGravity(Gravity.CENTER_VERTICAL);

                    final EditText customInputEt = new EditText(context);
                    customInputEt.setHint("Custom ₹");
                    customInputEt.setHintTextColor(UiUtils.getTextSecondaryColor(isDark));
                    customInputEt.setTextColor(UiUtils.getTextPrimaryColor(isDark));
                    customInputEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    customInputEt.setSingleLine(true);
                    customInputEt.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 8), UiUtils.dp(context, 12), UiUtils.dp(context, 8));
                    customInputEt.setBackground(UiUtils.makeRoundedRectWithStroke(context, UiUtils.getBgColor(isDark), 10f, UiUtils.getOutlineColor(isDark), 1f));
                    LinearLayout.LayoutParams customLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                    customInputEt.setLayoutParams(customLp);
                    customRow.addView(customInputEt);

                    customRow.addView(UiUtils.makeSpace(context, 8, 1));

                    TextView addCustomBtn = new TextView(context);
                    addCustomBtn.setText("Add Savings ＋");
                    addCustomBtn.setGravity(Gravity.CENTER);
                    addCustomBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    addCustomBtn.setTypeface(Typeface.DEFAULT_BOLD);
                    addCustomBtn.setTextColor(Color.WHITE);
                    addCustomBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 10), UiUtils.dp(context, 12), UiUtils.dp(context, 10));
                    addCustomBtn.setBackground(UiUtils.makeRoundedRect(context, UiUtils.COLOR_PRIMARY_TEAL, 10f));
                    UiUtils.applyBounceClick(addCustomBtn, new Runnable() {
                        @Override
                        public void run() {
                            String input = customInputEt.getText().toString().trim();
                            if (input.isEmpty()) {
                                android.widget.Toast.makeText(context, "Please enter an amount", android.widget.Toast.LENGTH_SHORT).show();
                                return;
                            }
                            double customAmt = 0.0;
                            try {
                                customAmt = Double.parseDouble(input);
                            } catch (Exception ignored) {
                            }
                            if (customAmt <= 0) {
                                android.widget.Toast.makeText(context, "Please enter an amount > 0", android.widget.Toast.LENGTH_SHORT).show();
                                return;
                            }
                            boolean success = viewModel.contributeToGoal(goal, customAmt);
                            if (!success) {
                                android.widget.Toast.makeText(context, "Insufficient wallet balance! Available: ₹" + (int) data.currentBalance, android.widget.Toast.LENGTH_SHORT).show();
                            } else {
                                customInputEt.setText("");
                                android.widget.Toast.makeText(context, "Saved ₹" + (int) customAmt + " into " + goal.title + "! 🎯", android.widget.Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    customRow.addView(addCustomBtn);

                    card.addView(customRow);

                    if (goal.savedAmount > 0) {
                        card.addView(UiUtils.makeSpace(context, 1, 8));
                        TextView withdrawLink = new TextView(context);
                        withdrawLink.setText("Release Money Back to Wallet ↩");
                        withdrawLink.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        withdrawLink.setTypeface(Typeface.DEFAULT_BOLD);
                        withdrawLink.setTextColor(0xFF0EA5E9);
                        withdrawLink.setGravity(Gravity.END);
                        UiUtils.applyBounceClick(withdrawLink, new Runnable() {
                            @Override
                            public void run() {
                                showWithdrawDialog(context, viewModel, goal);
                            }
                        });
                        card.addView(withdrawLink);
                    }

                } else {
                    card.addView(UiUtils.makeSpace(context, 1, 8));
                    LinearLayout doneRow = new LinearLayout(context);
                    doneRow.setOrientation(LinearLayout.HORIZONTAL);
                    doneRow.setGravity(Gravity.CENTER_VERTICAL);

                    TextView doneBadge = new TextView(context);
                    doneBadge.setText("🎉 Goal Completed! Awesome job!");
                    doneBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    doneBadge.setTypeface(Typeface.DEFAULT_BOLD);
                    doneBadge.setTextColor(UiUtils.COLOR_INCOME_GREEN);
                    doneBadge.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    doneRow.addView(doneBadge);

                    TextView withdrawLink = new TextView(context);
                    withdrawLink.setText("Release Funds ↩");
                    withdrawLink.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    withdrawLink.setTypeface(Typeface.DEFAULT_BOLD);
                    withdrawLink.setTextColor(0xFF0EA5E9);
                    UiUtils.applyBounceClick(withdrawLink, new Runnable() {
                        @Override
                        public void run() {
                            showWithdrawDialog(context, viewModel, goal);
                        }
                    });
                    doneRow.addView(withdrawLink);
                    card.addView(doneRow);
                }

                root.addView(card);
                root.addView(UiUtils.makeSpace(context, 1, 12));
            }
        }

        scroll.addView(root);
        return scroll;
    }

    private static void showNewGoalDialog(final Context context, final PocketPalViewModel viewModel) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        LinearLayout box = new LinearLayout(context);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = UiUtils.dp(context, 20);
        box.setPadding(pad, pad, pad, pad);
        box.setBackground(UiUtils.makeRoundedRect(context, Color.WHITE, 22f));

        TextView title = new TextView(context);
        title.setText("Create Savings Goal");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.BLACK);
        box.addView(title);
        box.addView(UiUtils.makeSpace(context, 1, 12));

        final EditText nameEt = new EditText(context);
        nameEt.setHint("Goal Name (e.g., Wireless Earbuds)");
        nameEt.setHintTextColor(0xFF64748B);
        nameEt.setTextColor(Color.BLACK);
        nameEt.setSingleLine(true);
        nameEt.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 10), UiUtils.dp(context, 12), UiUtils.dp(context, 10));
        nameEt.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 12f, 0xFF94A3B8, 1.5f));
        box.addView(nameEt);

        box.addView(UiUtils.makeSpace(context, 1, 10));

        final EditText targetEt = new EditText(context);
        targetEt.setHint("Target Amount (₹) e.g. 2000");
        targetEt.setHintTextColor(0xFF64748B);
        targetEt.setTextColor(Color.BLACK);
        targetEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        targetEt.setSingleLine(true);
        targetEt.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 10), UiUtils.dp(context, 12), UiUtils.dp(context, 10));
        targetEt.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 12f, 0xFF94A3B8, 1.5f));
        box.addView(targetEt);

        box.addView(UiUtils.makeSpace(context, 1, 16));

        LinearLayout actions = new LinearLayout(context);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        TextView cancelBtn = new TextView(context);
        cancelBtn.setText("Cancel");
        cancelBtn.setGravity(Gravity.CENTER);
        cancelBtn.setTextColor(Color.BLACK);
        cancelBtn.setTypeface(Typeface.DEFAULT_BOLD);
        cancelBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12));
        cancelBtn.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 12f, 0xFFCBD5E1, 1f));
        cancelBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        UiUtils.applyBounceClick(cancelBtn, new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
        actions.addView(cancelBtn);

        actions.addView(UiUtils.makeSpace(context, 10, 1));

        TextView saveBtn = new TextView(context);
        saveBtn.setText("Create Goal");
        saveBtn.setGravity(Gravity.CENTER);
        saveBtn.setTextColor(Color.WHITE);
        saveBtn.setTypeface(Typeface.DEFAULT_BOLD);
        saveBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12));
        saveBtn.setBackground(UiUtils.makeRoundedRect(context, UiUtils.COLOR_PRIMARY_TEAL, 12f));
        saveBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        UiUtils.applyBounceClick(saveBtn, new Runnable() {
            @Override
            public void run() {
                String n = nameEt.getText().toString().trim();
                if (n.isEmpty()) return;
                double t = 1500.0;
                try {
                    t = Double.parseDouble(targetEt.getText().toString().trim());
                } catch (Exception ignored) {
                }
                if (t <= 0) t = 1500.0;
                viewModel.addGoal(n, t, 0.0, "🎯", 0xFF0F8A74L, 45L);
                dialog.dismiss();
            }
        });
        actions.addView(saveBtn);
        box.addView(actions);

        dialog.setContentView(box, new ViewGroup.LayoutParams(UiUtils.dp(context, 330), ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();
    }

    private static void showEditGoalDialog(final Context context, final PocketPalViewModel viewModel, final Goal goal) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        LinearLayout box = new LinearLayout(context);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = UiUtils.dp(context, 20);
        box.setPadding(pad, pad, pad, pad);
        box.setBackground(UiUtils.makeRoundedRect(context, Color.WHITE, 22f));

        TextView title = new TextView(context);
        title.setText("Edit Savings Goal ✏️");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.BLACK);
        box.addView(title);
        box.addView(UiUtils.makeSpace(context, 1, 12));

        final EditText nameEt = new EditText(context);
        nameEt.setText(goal.title);
        nameEt.setHint("Goal Name");
        nameEt.setHintTextColor(0xFF64748B);
        nameEt.setTextColor(Color.BLACK);
        nameEt.setSingleLine(true);
        nameEt.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 10), UiUtils.dp(context, 12), UiUtils.dp(context, 10));
        nameEt.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 12f, 0xFF94A3B8, 1.5f));
        box.addView(nameEt);

        box.addView(UiUtils.makeSpace(context, 1, 10));

        final EditText targetEt = new EditText(context);
        targetEt.setText(String.valueOf((int) goal.targetAmount));
        targetEt.setHint("Target Amount (₹)");
        targetEt.setHintTextColor(0xFF64748B);
        targetEt.setTextColor(Color.BLACK);
        targetEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        targetEt.setSingleLine(true);
        targetEt.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 10), UiUtils.dp(context, 12), UiUtils.dp(context, 10));
        targetEt.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 12f, 0xFF94A3B8, 1.5f));
        box.addView(targetEt);

        box.addView(UiUtils.makeSpace(context, 1, 16));

        LinearLayout actions = new LinearLayout(context);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        TextView cancelBtn = new TextView(context);
        cancelBtn.setText("Cancel");
        cancelBtn.setGravity(Gravity.CENTER);
        cancelBtn.setTextColor(Color.BLACK);
        cancelBtn.setTypeface(Typeface.DEFAULT_BOLD);
        cancelBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12));
        cancelBtn.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 12f, 0xFFCBD5E1, 1f));
        cancelBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        UiUtils.applyBounceClick(cancelBtn, new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
        actions.addView(cancelBtn);

        actions.addView(UiUtils.makeSpace(context, 10, 1));

        TextView saveBtn = new TextView(context);
        saveBtn.setText("Save Changes");
        saveBtn.setGravity(Gravity.CENTER);
        saveBtn.setTextColor(Color.WHITE);
        saveBtn.setTypeface(Typeface.DEFAULT_BOLD);
        saveBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12));
        saveBtn.setBackground(UiUtils.makeRoundedRect(context, UiUtils.COLOR_PRIMARY_TEAL, 12f));
        saveBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        UiUtils.applyBounceClick(saveBtn, new Runnable() {
            @Override
            public void run() {
                String n = nameEt.getText().toString().trim();
                if (n.isEmpty()) return;
                double t = goal.targetAmount;
                try {
                    t = Double.parseDouble(targetEt.getText().toString().trim());
                } catch (Exception ignored) {
                }
                if (t <= 0) t = goal.targetAmount;
                viewModel.updateGoalDetails(goal, n, t, goal.emoji);
                dialog.dismiss();
            }
        });
        actions.addView(saveBtn);
        box.addView(actions);

        dialog.setContentView(box, new ViewGroup.LayoutParams(UiUtils.dp(context, 330), ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();
    }

    private static LinearLayout makeSummaryCol(Context context, String label, String value, int valColor, boolean isDark) {
        LinearLayout col = new LinearLayout(context);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER_HORIZONTAL);
        col.setPadding(UiUtils.dp(context, 4), UiUtils.dp(context, 4), UiUtils.dp(context, 4), UiUtils.dp(context, 4));

        TextView lbl = new TextView(context);
        lbl.setText(label);
        lbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        lbl.setTextColor(UiUtils.getTextSecondaryColor(isDark));
        lbl.setGravity(Gravity.CENTER);
        col.addView(lbl);

        col.addView(UiUtils.makeSpace(context, 1, 2));

        TextView val = new TextView(context);
        val.setText(value);
        val.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        val.setTypeface(Typeface.DEFAULT_BOLD);
        val.setTextColor(valColor);
        val.setGravity(Gravity.CENTER);
        col.addView(val);

        return col;
    }

    private static void showWithdrawDialog(final Context context, final PocketPalViewModel viewModel, final Goal goal) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        LinearLayout box = new LinearLayout(context);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = UiUtils.dp(context, 20);
        box.setPadding(pad, pad, pad, pad);
        box.setBackground(UiUtils.makeRoundedRect(context, Color.WHITE, 22f));

        TextView title = new TextView(context);
        title.setText("Release Saved Money ↩");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.BLACK);
        box.addView(title);

        TextView sub = new TextView(context);
        sub.setText("Currently saved in '" + goal.title + "': ₹" + (int) goal.savedAmount);
        sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        sub.setTextColor(0xFF475569);
        box.addView(sub);

        box.addView(UiUtils.makeSpace(context, 1, 12));

        final EditText amountEt = new EditText(context);
        amountEt.setText(String.valueOf((int) goal.savedAmount));
        amountEt.setHint("Amount to release (₹)");
        amountEt.setHintTextColor(0xFF64748B);
        amountEt.setTextColor(Color.BLACK);
        amountEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        amountEt.setSingleLine(true);
        amountEt.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 10), UiUtils.dp(context, 12), UiUtils.dp(context, 10));
        amountEt.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 12f, 0xFF94A3B8, 1.5f));
        box.addView(amountEt);

        box.addView(UiUtils.makeSpace(context, 1, 16));

        LinearLayout actions = new LinearLayout(context);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        TextView cancelBtn = new TextView(context);
        cancelBtn.setText("Cancel");
        cancelBtn.setGravity(Gravity.CENTER);
        cancelBtn.setTextColor(Color.BLACK);
        cancelBtn.setTypeface(Typeface.DEFAULT_BOLD);
        cancelBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12));
        cancelBtn.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 12f, 0xFFCBD5E1, 1f));
        cancelBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        UiUtils.applyBounceClick(cancelBtn, new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
        actions.addView(cancelBtn);

        actions.addView(UiUtils.makeSpace(context, 10, 1));

        TextView releaseBtn = new TextView(context);
        releaseBtn.setText("Release Funds");
        releaseBtn.setGravity(Gravity.CENTER);
        releaseBtn.setTextColor(Color.WHITE);
        releaseBtn.setTypeface(Typeface.DEFAULT_BOLD);
        releaseBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12));
        releaseBtn.setBackground(UiUtils.makeRoundedRect(context, 0xFF0EA5E9, 12f));
        releaseBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        UiUtils.applyBounceClick(releaseBtn, new Runnable() {
            @Override
            public void run() {
                String input = amountEt.getText().toString().trim();
                if (input.isEmpty()) return;
                double amt = 0.0;
                try {
                    amt = Double.parseDouble(input);
                } catch (Exception ignored) {
                }
                if (amt <= 0) return;
                if (amt > goal.savedAmount) {
                    android.widget.Toast.makeText(context, "Cannot release more than saved in this goal (₹" + (int) goal.savedAmount + ")", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean ok = viewModel.withdrawFromGoal(goal, amt);
                if (ok) {
                    android.widget.Toast.makeText(context, "Released ₹" + (int) amt + " back to Available Balance! 💳", android.widget.Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        actions.addView(releaseBtn);
        box.addView(actions);

        dialog.setContentView(box, new ViewGroup.LayoutParams(UiUtils.dp(context, 330), ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();
    }
}
