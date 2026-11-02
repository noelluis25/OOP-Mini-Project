package com.example.ui.components;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.data.model.DecisionResult;
import com.example.data.model.ExpenseCategory;
import com.example.data.model.TransactionType;
import com.example.data.repository.DecisionEngine;
import com.example.ui.viewmodel.DashboardData;
import com.example.ui.viewmodel.PocketPalViewModel;

public class InstantDecisionDialogHelper {

    public static void show(final Activity activity, final PocketPalViewModel viewModel) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final ScrollView scrollRoot = new ScrollView(activity);
        final LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = UiUtils.dp(activity, 20);
        container.setPadding(pad, pad, pad, pad);
        container.setBackground(UiUtils.makeRoundedRect(activity, Color.WHITE, 24f));
        scrollRoot.addView(container);

        final boolean[] showingResult = {false};
        final DecisionResult[] lastResult = {null};

        final Runnable[] renderRef = new Runnable[1];
        renderRef[0] = new Runnable() {
            @Override
            public void run() {
                container.removeAllViews();

                // Header
                LinearLayout topRow = new LinearLayout(activity);
                topRow.setOrientation(LinearLayout.HORIZONTAL);
                topRow.setGravity(Gravity.CENTER_VERTICAL);

                TextView title = new TextView(activity);
                title.setText("Check Before Buying");
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                title.setTypeface(Typeface.DEFAULT_BOLD);
                title.setTextColor(Color.BLACK);
                title.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                topRow.addView(title);

                TextView closeBtn = new TextView(activity);
                closeBtn.setText("✕");
                closeBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                closeBtn.setTextColor(Color.BLACK);
                closeBtn.setPadding(UiUtils.dp(activity, 8), UiUtils.dp(activity, 4), UiUtils.dp(activity, 8), UiUtils.dp(activity, 4));
                UiUtils.applyBounceClick(closeBtn, new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
                topRow.addView(closeBtn);
                container.addView(topRow);

                container.addView(UiUtils.makeSpace(activity, 1, 12));

                if (!showingResult[0]) {
                    // Input View
                    TextView sub = new TextView(activity);
                    sub.setText("Instant smart recommendation based on your balance, essential needs, and savings goals.");
                    sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    sub.setTextColor(0xFF475569);
                    container.addView(sub);

                    container.addView(UiUtils.makeSpace(activity, 1, 14));

                    TextView nameLbl = new TextView(activity);
                    nameLbl.setText("Item Name");
                    nameLbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    nameLbl.setTypeface(Typeface.DEFAULT_BOLD);
                    nameLbl.setTextColor(Color.BLACK);
                    container.addView(nameLbl);

                    container.addView(UiUtils.makeSpace(activity, 1, 6));

                    final EditText nameEt = new EditText(activity);
                    nameEt.setHint("e.g. Wireless Earbuds, Gaming Mouse");
                    nameEt.setHintTextColor(0xFF64748B);
                    nameEt.setTextColor(Color.BLACK);
                    nameEt.setSingleLine(true);
                    nameEt.setPadding(UiUtils.dp(activity, 14), UiUtils.dp(activity, 12), UiUtils.dp(activity, 14), UiUtils.dp(activity, 12));
                    nameEt.setBackground(UiUtils.makeRoundedRectWithStroke(activity, Color.WHITE, 12f, 0xFF94A3B8, 1.5f));
                    container.addView(nameEt);

                    container.addView(UiUtils.makeSpace(activity, 1, 12));

                    TextView priceLbl = new TextView(activity);
                    priceLbl.setText("Price (₹)");
                    priceLbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    priceLbl.setTypeface(Typeface.DEFAULT_BOLD);
                    priceLbl.setTextColor(Color.BLACK);
                    container.addView(priceLbl);

                    container.addView(UiUtils.makeSpace(activity, 1, 6));

                    final EditText priceEt = new EditText(activity);
                    priceEt.setHint("e.g. 800");
                    priceEt.setHintTextColor(0xFF64748B);
                    priceEt.setTextColor(Color.BLACK);
                    priceEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    priceEt.setSingleLine(true);
                    priceEt.setPadding(UiUtils.dp(activity, 14), UiUtils.dp(activity, 12), UiUtils.dp(activity, 14), UiUtils.dp(activity, 12));
                    priceEt.setBackground(UiUtils.makeRoundedRectWithStroke(activity, Color.WHITE, 12f, 0xFF94A3B8, 1.5f));
                    container.addView(priceEt);

                    container.addView(UiUtils.makeSpace(activity, 1, 18));

                    TextView checkBtn = new TextView(activity);
                    checkBtn.setText("Analyze Purchase 🔍");
                    checkBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    checkBtn.setTypeface(Typeface.DEFAULT_BOLD);
                    checkBtn.setTextColor(Color.WHITE);
                    checkBtn.setGravity(Gravity.CENTER);
                    checkBtn.setPadding(UiUtils.dp(activity, 16), UiUtils.dp(activity, 14), UiUtils.dp(activity, 16), UiUtils.dp(activity, 14));
                    checkBtn.setBackground(UiUtils.makeRoundedRect(activity, UiUtils.COLOR_PRIMARY_TEAL, 14f));

                    UiUtils.applyBounceClick(checkBtn, new Runnable() {
                        @Override
                        public void run() {
                            String name = nameEt.getText().toString().trim();
                            String priceStr = priceEt.getText().toString().trim();
                            if (priceStr.isEmpty()) {
                                Toast.makeText(activity, "Please enter a valid price", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            double price = 0.0;
                            try {
                                price = Double.parseDouble(priceStr);
                            } catch (Exception ignored) {
                            }
                            if (price <= 0) {
                                Toast.makeText(activity, "Please enter a price greater than 0", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            DashboardData currentData = viewModel.getDashboardData();
                            lastResult[0] = DecisionEngine.evaluatePurchase(name, price, currentData);
                            showingResult[0] = true;
                            renderRef[0].run();
                        }
                    });
                    container.addView(checkBtn);

                } else if (lastResult[0] != null) {
                    // Result View
                    DecisionResult res = lastResult[0];

                    // Status Banner Card
                    LinearLayout statusBanner = new LinearLayout(activity);
                    statusBanner.setOrientation(LinearLayout.VERTICAL);
                    statusBanner.setGravity(Gravity.CENTER);
                    statusBanner.setPadding(UiUtils.dp(activity, 16), UiUtils.dp(activity, 16), UiUtils.dp(activity, 16), UiUtils.dp(activity, 16));
                    statusBanner.setBackground(UiUtils.makeRoundedRectWithStroke(
                            activity,
                            res.status.bgColor,
                            18f,
                            res.status.color,
                            2f
                    ));

                    TextView statusTitle = new TextView(activity);
                    statusTitle.setText(res.status.label);
                    statusTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    statusTitle.setTypeface(Typeface.DEFAULT_BOLD);
                    statusTitle.setTextColor(res.status.color);
                    statusTitle.setGravity(Gravity.CENTER);
                    statusBanner.addView(statusTitle);

                    TextView itemSub = new TextView(activity);
                    itemSub.setText("Checking: \"" + res.itemName + "\" for ₹" + (int) res.itemPrice);
                    itemSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    itemSub.setTypeface(Typeface.DEFAULT_BOLD);
                    itemSub.setTextColor(0xFF334155);
                    itemSub.setGravity(Gravity.CENTER);
                    statusBanner.addView(itemSub);

                    container.addView(statusBanner);

                    container.addView(UiUtils.makeSpace(activity, 1, 14));

                    // Financial Breakdown Grid
                    LinearLayout gridCard = new LinearLayout(activity);
                    gridCard.setOrientation(LinearLayout.VERTICAL);
                    gridCard.setPadding(UiUtils.dp(activity, 14), UiUtils.dp(activity, 12), UiUtils.dp(activity, 14), UiUtils.dp(activity, 12));
                    gridCard.setBackground(UiUtils.makeRoundedRect(activity, 0xFFF8FAFC, 16f));

                    gridCard.addView(makeBreakdownRow(activity, "Current Balance", "₹" + (int) res.currentBalance, 0xFF0F1E36));
                    gridCard.addView(UiUtils.makeSpace(activity, 1, 6));
                    gridCard.addView(makeBreakdownRow(activity, "Item Price", "-₹" + (int) res.itemPrice, UiUtils.COLOR_EXPENSE_RED));
                    gridCard.addView(UiUtils.makeSpace(activity, 1, 6));
                    gridCard.addView(makeBreakdownRow(activity, "Balance After Purchase", "₹" + (int) res.balanceAfterPurchase,
                            res.balanceAfterPurchase < 0 ? UiUtils.COLOR_EXPENSE_RED : UiUtils.COLOR_PRIMARY_TEAL));
                    gridCard.addView(UiUtils.makeSpace(activity, 1, 6));
                    gridCard.addView(makeBreakdownRow(activity, "Estimated Essentials (" + res.daysRemainingInMonth + " days)", "₹" + (int) res.estimatedEssentialsNeeded, 0xFF64748B));

                    container.addView(gridCard);

                    container.addView(UiUtils.makeSpace(activity, 1, 12));

                    // Explanation Box
                    LinearLayout expBox = new LinearLayout(activity);
                    expBox.setOrientation(LinearLayout.VERTICAL);
                    expBox.setPadding(UiUtils.dp(activity, 14), UiUtils.dp(activity, 12), UiUtils.dp(activity, 14), UiUtils.dp(activity, 12));
                    expBox.setBackground(UiUtils.makeRoundedRect(activity, 0xFFF1F5F9, 14f));

                    TextView expText = new TextView(activity);
                    expText.setText("💡 " + res.explanation);
                    expText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    expText.setTextColor(0xFF1E293B);
                    expBox.addView(expText);

                    expBox.addView(UiUtils.makeSpace(activity, 1, 6));

                    TextView sugText = new TextView(activity);
                    sugText.setText("👉 Tip: " + res.suggestion);
                    sugText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    sugText.setTypeface(Typeface.DEFAULT_BOLD);
                    sugText.setTextColor(res.status.color);
                    expBox.addView(sugText);

                    container.addView(expBox);

                    container.addView(UiUtils.makeSpace(activity, 1, 16));

                    // Action Buttons (Confirm & Buy | Save as Goal | Re-check)
                    if (res.status != DecisionResult.Status.NOT_RIGHT_NOW) {
                        TextView confirmBtn = new TextView(activity);
                        confirmBtn.setText("Confirm & Buy 💳");
                        confirmBtn.setGravity(Gravity.CENTER);
                        confirmBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                        confirmBtn.setTypeface(Typeface.DEFAULT_BOLD);
                        confirmBtn.setTextColor(Color.WHITE);
                        confirmBtn.setPadding(UiUtils.dp(activity, 14), UiUtils.dp(activity, 12), UiUtils.dp(activity, 14), UiUtils.dp(activity, 12));
                        confirmBtn.setBackground(UiUtils.makeRoundedRect(activity, UiUtils.COLOR_PRIMARY_TEAL, 12f));

                        UiUtils.applyBounceClick(confirmBtn, new Runnable() {
                            @Override
                            public void run() {
                                viewModel.saveTransaction(
                                        res.itemName,
                                        res.itemPrice,
                                        TransactionType.EXPENSE,
                                        ExpenseCategory.OTHERS.toItem(),
                                        "Bought via Check Before Buying",
                                        "UPI",
                                        System.currentTimeMillis()
                                );
                                Toast.makeText(activity, "Purchase logged & balance updated! 🛒", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                        container.addView(confirmBtn);
                        container.addView(UiUtils.makeSpace(activity, 1, 8));
                    }

                    LinearLayout secondRow = new LinearLayout(activity);
                    secondRow.setOrientation(LinearLayout.HORIZONTAL);

                    TextView goalBtn = new TextView(activity);
                    goalBtn.setText("Save as Goal Jar 🏺");
                    goalBtn.setGravity(Gravity.CENTER);
                    goalBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    goalBtn.setTypeface(Typeface.DEFAULT_BOLD);
                    goalBtn.setTextColor(UiUtils.COLOR_PRIMARY_TEAL);
                    goalBtn.setPadding(UiUtils.dp(activity, 10), UiUtils.dp(activity, 12), UiUtils.dp(activity, 10), UiUtils.dp(activity, 12));
                    goalBtn.setBackground(UiUtils.makeRoundedRect(activity, UiUtils.COLOR_PRIMARY_TEAL_CONTAINER, 12f));
                    goalBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                    UiUtils.applyBounceClick(goalBtn, new Runnable() {
                        @Override
                        public void run() {
                            viewModel.addGoal(
                                    res.itemName,
                                    res.itemPrice,
                                    0.0,
                                    "🎯",
                                    0xFF0F8A74L,
                                    30
                            );
                            Toast.makeText(activity, "Created Goal Jar for '" + res.itemName + "'! 🎯", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    secondRow.addView(goalBtn);

                    secondRow.addView(UiUtils.makeSpace(activity, 8, 1));

                    TextView retryBtn = new TextView(activity);
                    retryBtn.setText("Re-check 🔄");
                    retryBtn.setGravity(Gravity.CENTER);
                    retryBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    retryBtn.setTypeface(Typeface.DEFAULT_BOLD);
                    retryBtn.setTextColor(Color.BLACK);
                    retryBtn.setPadding(UiUtils.dp(activity, 10), UiUtils.dp(activity, 12), UiUtils.dp(activity, 10), UiUtils.dp(activity, 12));
                    retryBtn.setBackground(UiUtils.makeRoundedRectWithStroke(activity, Color.WHITE, 12f, 0xFFCBD5E1, 1f));
                    retryBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                    UiUtils.applyBounceClick(retryBtn, new Runnable() {
                        @Override
                        public void run() {
                            showingResult[0] = false;
                            lastResult[0] = null;
                            renderRef[0].run();
                        }
                    });
                    secondRow.addView(retryBtn);

                    container.addView(secondRow);
                }
            }
        };

        renderRef[0].run();
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        int dialogWidth = Math.min(UiUtils.dp(activity, 340), screenWidth - UiUtils.dp(activity, 32));
        dialog.setContentView(scrollRoot, new ViewGroup.LayoutParams(
                dialogWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        dialog.show();
    }

    private static View makeBreakdownRow(Activity activity, String label, String value, int valueColor) {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView lbl = new TextView(activity);
        lbl.setText(label);
        lbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        lbl.setTextColor(0xFF475569);
        lbl.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(lbl);

        TextView val = new TextView(activity);
        val.setText(value);
        val.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        val.setTypeface(Typeface.DEFAULT_BOLD);
        val.setTextColor(valueColor);
        row.addView(val);

        return row;
    }
}
