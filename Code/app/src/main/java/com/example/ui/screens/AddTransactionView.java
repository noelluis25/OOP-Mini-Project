package com.example.ui.screens;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.data.model.CategoryItem;
import com.example.data.model.ExpenseCategory;
import com.example.data.model.IncomeCategory;
import com.example.data.model.Transaction;
import com.example.data.model.TransactionType;
import com.example.ui.components.UiUtils;
import com.example.ui.viewmodel.DashboardData;
import com.example.ui.viewmodel.PocketPalViewModel;
import java.util.List;

public class AddTransactionView {

    public static View create(final Context context, final PocketPalViewModel viewModel) {
        final DashboardData data = viewModel.getDashboardData();
        final boolean isDark = data.preferences != null && data.preferences.isDarkMode;
        final Transaction editing = viewModel.getEditingTransaction();

        final TransactionType[] selectedType = {
                editing != null
                        ? (TransactionType.INCOME.name().equals(editing.type) ? TransactionType.INCOME : TransactionType.EXPENSE)
                        : viewModel.getInitialTransactionType()
        };
        final CategoryItem[] selectedCategory = {
                editing != null
                        ? editing.getCategoryItem()
                        : (selectedType[0] == TransactionType.INCOME
                                ? IncomeCategory.ALLOWANCE.toItem()
                                : ExpenseCategory.FOOD.toItem())
        };
        final String[] selectedPaymentMethod = {
                editing != null ? editing.paymentMethod : "UPI"
        };

        ScrollView scroll = new ScrollView(context);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(UiUtils.getBgColor(isDark));

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = UiUtils.dp(context, 16);
        root.setPadding(pad, UiUtils.dp(context, 16), pad, UiUtils.dp(context, 28));

        // Top Header
        LinearLayout topBar = new LinearLayout(context);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);

        TextView backBtn = new TextView(context);
        backBtn.setText("← Back");
        backBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        backBtn.setTypeface(Typeface.DEFAULT_BOLD);
        backBtn.setTextColor(UiUtils.COLOR_PRIMARY_TEAL);
        backBtn.setPadding(UiUtils.dp(context, 10), UiUtils.dp(context, 6), UiUtils.dp(context, 10), UiUtils.dp(context, 6));
        UiUtils.applyBounceClick(backBtn, new Runnable() {
            @Override
            public void run() {
                viewModel.navigateBack();
            }
        });
        topBar.addView(backBtn);

        final TextView headerTitle = new TextView(context);
        headerTitle.setText(editing != null
                ? "Edit Transaction"
                : (selectedType[0] == TransactionType.EXPENSE ? "Add Expense" : "Add Income"));
        headerTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        headerTitle.setTypeface(Typeface.DEFAULT_BOLD);
        headerTitle.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        headerTitle.setGravity(Gravity.CENTER);
        headerTitle.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        topBar.addView(headerTitle);

        TextView voiceBtn = new TextView(context);
        voiceBtn.setText("🎙️ Voice");
        voiceBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        voiceBtn.setTypeface(Typeface.DEFAULT_BOLD);
        voiceBtn.setTextColor(Color.WHITE);
        voiceBtn.setPadding(UiUtils.dp(context, 10), UiUtils.dp(context, 6), UiUtils.dp(context, 10), UiUtils.dp(context, 6));
        voiceBtn.setBackground(UiUtils.makeRoundedRect(context, UiUtils.COLOR_PRIMARY_TEAL, 12f));
        UiUtils.applyBounceClick(voiceBtn, new Runnable() {
            @Override
            public void run() {
                if (context instanceof android.app.Activity) {
                    com.example.ui.components.VoiceConfirmationDialogHelper.startVoiceEntry((android.app.Activity) context, viewModel);
                }
            }
        });
        topBar.addView(voiceBtn);
        root.addView(topBar);

        root.addView(UiUtils.makeSpace(context, 1, 14));

        // Type Toggle (Expense vs Income)
        final LinearLayout typeToggleRow = new LinearLayout(context);
        typeToggleRow.setOrientation(LinearLayout.HORIZONTAL);
        typeToggleRow.setPadding(UiUtils.dp(context, 4), UiUtils.dp(context, 4), UiUtils.dp(context, 4), UiUtils.dp(context, 4));
        typeToggleRow.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getSurfaceVariantColor(isDark), 16f));

        final LinearLayout categoryGridContainer = new LinearLayout(context);
        categoryGridContainer.setOrientation(LinearLayout.VERTICAL);

        final TextView catSectionTitle = new TextView(context);
        catSectionTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        catSectionTitle.setTypeface(Typeface.DEFAULT_BOLD);
        catSectionTitle.setTextColor(UiUtils.getTextPrimaryColor(isDark));

        final Runnable[] refreshTypeAndCategories = new Runnable[1];
        refreshTypeAndCategories[0] = new Runnable() {
            @Override
            public void run() {
                typeToggleRow.removeAllViews();
                boolean isExp = selectedType[0] == TransactionType.EXPENSE;
                if (editing == null) {
                    headerTitle.setText(isExp ? "Add Expense" : "Add Income");
                }

                TextView expTab = new TextView(context);
                expTab.setText("💸 Expense");
                expTab.setGravity(Gravity.CENTER);
                expTab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                expTab.setTypeface(Typeface.DEFAULT_BOLD);
                expTab.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12));
                expTab.setTextColor(isExp ? Color.WHITE : UiUtils.getTextSecondaryColor(isDark));
                expTab.setBackground(isExp ? UiUtils.makeRoundedRect(context, UiUtils.COLOR_EXPENSE_RED, 12f) : null);
                expTab.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                expTab.setTag("toggle_expense");
                UiUtils.applyBounceClick(expTab, new Runnable() {
                    @Override
                    public void run() {
                        if (selectedType[0] != TransactionType.EXPENSE) {
                            selectedType[0] = TransactionType.EXPENSE;
                            selectedCategory[0] = ExpenseCategory.FOOD.toItem();
                            refreshTypeAndCategories[0].run();
                        }
                    }
                });
                typeToggleRow.addView(expTab);

                TextView incTab = new TextView(context);
                incTab.setText("💰 Income");
                incTab.setGravity(Gravity.CENTER);
                incTab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                incTab.setTypeface(Typeface.DEFAULT_BOLD);
                incTab.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12));
                incTab.setTextColor(!isExp ? Color.WHITE : UiUtils.getTextSecondaryColor(isDark));
                incTab.setBackground(!isExp ? UiUtils.makeRoundedRect(context, UiUtils.COLOR_INCOME_GREEN, 12f) : null);
                incTab.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                incTab.setTag("toggle_income");
                UiUtils.applyBounceClick(incTab, new Runnable() {
                    @Override
                    public void run() {
                        if (selectedType[0] != TransactionType.INCOME) {
                            selectedType[0] = TransactionType.INCOME;
                            selectedCategory[0] = IncomeCategory.ALLOWANCE.toItem();
                            refreshTypeAndCategories[0].run();
                        }
                    }
                });
                typeToggleRow.addView(incTab);

                catSectionTitle.setText(isExp ? "Tap Expense Category" : "Tap Income Source");

                categoryGridContainer.removeAllViews();
                List<CategoryItem> cats = CategoryItem.getCategoriesForType(selectedType[0]);
                for (int rowIdx = 0; rowIdx < cats.size(); rowIdx += 4) {
                    LinearLayout row = new LinearLayout(context);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    for (int colIdx = 0; colIdx < 4; colIdx++) {
                        int idx = rowIdx + colIdx;
                        if (idx < cats.size()) {
                            final CategoryItem item = cats.get(idx);
                            boolean sel = item.name.equals(selectedCategory[0].name);

                            FrameLayout cellWrap = new FrameLayout(context);
                            cellWrap.setBackground(UiUtils.makeRoundedRectWithStroke(
                                    context,
                                    sel ? 0xFFE8F7EE : UiUtils.getCardColor(isDark),
                                    16f,
                                    sel ? UiUtils.COLOR_PRIMARY_TEAL : UiUtils.getOutlineColor(isDark),
                                    sel ? 2.5f : 1f
                            ));
                            cellWrap.setPadding(UiUtils.dp(context, 4), UiUtils.dp(context, 8), UiUtils.dp(context, 4), UiUtils.dp(context, 8));
                            cellWrap.setTag("cat_chip_" + item.name);

                            LinearLayout cell = new LinearLayout(context);
                            cell.setOrientation(LinearLayout.VERTICAL);
                            cell.setGravity(Gravity.CENTER);

                            TextView em = new TextView(context);
                            em.setText(item.emoji);
                            em.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                            em.setGravity(Gravity.CENTER);
                            int iconBgSz = UiUtils.dp(context, 42);
                            LinearLayout.LayoutParams emParams = new LinearLayout.LayoutParams(iconBgSz, iconBgSz);
                            em.setLayoutParams(emParams);
                            em.setBackground(UiUtils.makeCircle(item.bgLightColor));
                            cell.addView(em);

                            cell.addView(UiUtils.makeSpace(context, 1, 4));

                            TextView lb = new TextView(context);
                            lb.setText(item.label);
                            lb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                            lb.setTypeface(sel ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
                            lb.setTextColor(sel ? UiUtils.COLOR_NAVY_HEADING : UiUtils.getTextPrimaryColor(isDark));
                            lb.setGravity(Gravity.CENTER);
                            lb.setSingleLine(true);
                            cell.addView(lb);

                            cellWrap.addView(cell);

                            if (sel) {
                                TextView chk = new TextView(context);
                                chk.setText("✓");
                                chk.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                                chk.setTypeface(Typeface.DEFAULT_BOLD);
                                chk.setTextColor(Color.WHITE);
                                chk.setGravity(Gravity.CENTER);
                                int chkSz = UiUtils.dp(context, 18);
                                FrameLayout.LayoutParams chkLp = new FrameLayout.LayoutParams(chkSz, chkSz, Gravity.TOP | Gravity.END);
                                chkLp.topMargin = UiUtils.dp(context, 4);
                                chkLp.rightMargin = UiUtils.dp(context, 4);
                                chk.setLayoutParams(chkLp);
                                chk.setBackground(UiUtils.makeCircle(UiUtils.COLOR_PRIMARY_TEAL));
                                cellWrap.addView(chk);
                            }

                            LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                            if (colIdx > 0) cellParams.leftMargin = UiUtils.dp(context, 8);
                            cellWrap.setLayoutParams(cellParams);

                            UiUtils.applyBounceClick(cellWrap, new Runnable() {
                                @Override
                                public void run() {
                                    selectedCategory[0] = item;
                                    refreshTypeAndCategories[0].run();
                                }
                            });
                            row.addView(cellWrap);
                        }
                    }
                    categoryGridContainer.addView(row);
                    categoryGridContainer.addView(UiUtils.makeSpace(context, 1, 8));
                }
            }
        };

        root.addView(typeToggleRow);
        root.addView(UiUtils.makeSpace(context, 1, 14));

        // Amount Input Card (with 4 preset chips: +₹20, +₹50, +₹100, +₹250)
        LinearLayout amountCard = new LinearLayout(context);
        amountCard.setOrientation(LinearLayout.VERTICAL);
        int cPad = UiUtils.dp(context, 16);
        amountCard.setPadding(cPad, cPad, cPad, cPad);
        amountCard.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 20f));
        amountCard.setElevation(UiUtils.dp(context, 2));

        TextView amtLabel = new TextView(context);
        amtLabel.setText("Enter Amount (₹)");
        amtLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        amtLabel.setTextColor(UiUtils.getTextSecondaryColor(isDark));
        amountCard.addView(amtLabel);

        final EditText amountEt = new EditText(context);
        if (editing != null) {
            amountEt.setText(String.valueOf((int) editing.amount));
        }
        amountEt.setHint("0");
        amountEt.setHintTextColor(UiUtils.getTextSecondaryColor(isDark));
        amountEt.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        amountEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        amountEt.setTypeface(Typeface.DEFAULT_BOLD);
        amountEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        amountEt.setSingleLine(true);
        amountEt.setTag("input_amount");
        amountCard.addView(amountEt);

        LinearLayout quickRow = new LinearLayout(context);
        quickRow.setOrientation(LinearLayout.HORIZONTAL);
        quickRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        int[] quickAmounts = {20, 50, 100, 250};
        for (int i = 0; i < quickAmounts.length; i++) {
            final int addAmt = quickAmounts[i];
            TextView qChip = new TextView(context);
            qChip.setText("+₹" + addAmt);
            qChip.setGravity(Gravity.CENTER);
            qChip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            qChip.setTypeface(Typeface.DEFAULT_BOLD);
            qChip.setTextColor(UiUtils.COLOR_PRIMARY_TEAL);
            qChip.setPadding(UiUtils.dp(context, 8), UiUtils.dp(context, 10), UiUtils.dp(context, 8), UiUtils.dp(context, 10));
            qChip.setBackground(UiUtils.makeRoundedRect(
                    context,
                    isDark ? 0xFF04382E : UiUtils.COLOR_PRIMARY_TEAL_CONTAINER,
                    10f
            ));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            if (i > 0) lp.leftMargin = UiUtils.dp(context, 8);
            qChip.setLayoutParams(lp);
            UiUtils.applyBounceClick(qChip, new Runnable() {
                @Override
                public void run() {
                    double cur = 0.0;
                    try {
                        cur = Double.parseDouble(amountEt.getText().toString().trim());
                    } catch (Exception ignored) {
                    }
                    int next = (int) (cur + addAmt);
                    amountEt.setText(String.valueOf(next));
                    amountEt.setSelection(amountEt.getText().length());
                }
            });
            quickRow.addView(qChip);
        }
        amountCard.addView(quickRow);
        root.addView(amountCard);

        root.addView(UiUtils.makeSpace(context, 1, 16));

        // Category Grid Section
        root.addView(catSectionTitle);
        root.addView(UiUtils.makeSpace(context, 1, 8));
        root.addView(categoryGridContainer);
        refreshTypeAndCategories[0].run();

        root.addView(UiUtils.makeSpace(context, 1, 8));

        // Details Card ("What was this for?" and Payment Mode — no food examples, no optional notes)
        LinearLayout detailsCard = new LinearLayout(context);
        detailsCard.setOrientation(LinearLayout.VERTICAL);
        detailsCard.setPadding(cPad, cPad, cPad, cPad);
        detailsCard.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 20f));

        TextView titleLbl = new TextView(context);
        titleLbl.setText("What was this for?");
        titleLbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        titleLbl.setTypeface(Typeface.DEFAULT_BOLD);
        titleLbl.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        detailsCard.addView(titleLbl);
        detailsCard.addView(UiUtils.makeSpace(context, 1, 6));

        final EditText titleEt = new EditText(context);
        if (editing != null) {
            titleEt.setText(editing.title);
        }
        titleEt.setHint("Enter description");
        titleEt.setHintTextColor(UiUtils.getTextSecondaryColor(isDark));
        titleEt.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        titleEt.setSingleLine(true);
        titleEt.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 10), UiUtils.dp(context, 12), UiUtils.dp(context, 10));
        titleEt.setBackground(UiUtils.makeRoundedRectWithStroke(context, UiUtils.getBgColor(isDark), 12f, UiUtils.getOutlineColor(isDark), 1f));
        titleEt.setTag("input_title");
        detailsCard.addView(titleEt);

        detailsCard.addView(UiUtils.makeSpace(context, 1, 14));

        TextView payLbl = new TextView(context);
        payLbl.setText("Payment Mode");
        payLbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        payLbl.setTypeface(Typeface.DEFAULT_BOLD);
        payLbl.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        detailsCard.addView(payLbl);
        detailsCard.addView(UiUtils.makeSpace(context, 1, 6));

        final LinearLayout payRow = new LinearLayout(context);
        payRow.setOrientation(LinearLayout.HORIZONTAL);
        final String[] methods = {"UPI", "Cash", "Card"};
        final Runnable[] refreshPayMethods = new Runnable[1];
        refreshPayMethods[0] = new Runnable() {
            @Override
            public void run() {
                payRow.removeAllViews();
                for (int i = 0; i < methods.length; i++) {
                    final String m = methods[i];
                    boolean sel = m.equals(selectedPaymentMethod[0]);
                    TextView chip = UiUtils.createHighlightedChip(
                            context,
                            m,
                            sel,
                            new Runnable() {
                                @Override
                                public void run() {
                                    selectedPaymentMethod[0] = m;
                                    refreshPayMethods[0].run();
                                }
                            }
                    );
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                    if (i > 0) lp.leftMargin = UiUtils.dp(context, 8);
                    chip.setLayoutParams(lp);
                    payRow.addView(chip);
                }
            }
        };
        refreshPayMethods[0].run();
        detailsCard.addView(payRow);

        root.addView(detailsCard);
        root.addView(UiUtils.makeSpace(context, 1, 18));

        TextView saveBtn = new TextView(context);
        saveBtn.setText(editing != null ? "Update Transaction ✓" : "Save Transaction ✓");
        saveBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        saveBtn.setTypeface(Typeface.DEFAULT_BOLD);
        saveBtn.setTextColor(Color.WHITE);
        saveBtn.setGravity(Gravity.CENTER);
        saveBtn.setPadding(UiUtils.dp(context, 16), UiUtils.dp(context, 15), UiUtils.dp(context, 16), UiUtils.dp(context, 15));
        saveBtn.setBackground(UiUtils.makeRoundedRect(context, UiUtils.COLOR_PRIMARY_TEAL, 16f));
        saveBtn.setTag("btn_save_transaction");
        UiUtils.applyBounceClick(saveBtn, new Runnable() {
            @Override
            public void run() {
                double amt = 0.0;
                try {
                    amt = Double.parseDouble(amountEt.getText().toString().trim());
                } catch (Exception ignored) {
                }
                if (amt <= 0) return;
                long ts = editing != null ? editing.timestamp : System.currentTimeMillis();
                viewModel.saveTransaction(
                        titleEt.getText().toString(),
                        amt,
                        selectedType[0],
                        selectedCategory[0],
                        "",
                        selectedPaymentMethod[0],
                        ts
                );
            }
        });
        root.addView(saveBtn);

        scroll.addView(root);
        return scroll;
    }
}
