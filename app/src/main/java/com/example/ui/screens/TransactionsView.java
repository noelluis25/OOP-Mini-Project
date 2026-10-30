package com.example.ui.screens;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.data.model.Transaction;
import com.example.ui.components.TransactionRowView;
import com.example.ui.components.UiUtils;
import com.example.ui.viewmodel.DashboardData;
import com.example.ui.viewmodel.PocketPalViewModel;
import com.example.ui.viewmodel.TransactionFilter;
import java.util.List;

public class TransactionsView {

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

        TextView title = new TextView(context);
        title.setText("Transaction History");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        root.addView(title);

        TextView sub = new TextView(context);
        sub.setText("Tap any item to edit or tap 🗑 to remove");
        sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        sub.setTextColor(UiUtils.getTextSecondaryColor(isDark));
        root.addView(sub);

        root.addView(UiUtils.makeSpace(context, 1, 12));

        // Search input
        final EditText searchEt = new EditText(context);
        searchEt.setText(data.searchQuery);
        searchEt.setSelection(searchEt.getText().length());
        searchEt.setHint("🔍 Search by title, category, or note...");
        searchEt.setHintTextColor(UiUtils.getTextSecondaryColor(isDark));
        searchEt.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        searchEt.setSingleLine(true);
        searchEt.setPadding(UiUtils.dp(context, 14), UiUtils.dp(context, 12), UiUtils.dp(context, 14), UiUtils.dp(context, 12));
        searchEt.setBackground(UiUtils.makeRoundedRectWithStroke(context, UiUtils.getCardColor(isDark), 14f, UiUtils.getOutlineColor(isDark), 1f));
        searchEt.setTag("input_search_transactions");
        root.addView(searchEt);

        root.addView(UiUtils.makeSpace(context, 1, 10));

        // Filter Chips
        HorizontalScrollView filterScroll = new HorizontalScrollView(context);
        filterScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout filterRow = new LinearLayout(context);
        filterRow.setOrientation(LinearLayout.HORIZONTAL);

        for (final TransactionFilter f : TransactionFilter.values()) {
            boolean isSel = f == data.selectedFilter;
            TextView chip = UiUtils.createHighlightedChip(
                    context,
                    f.label,
                    isSel,
                    new Runnable() {
                        @Override
                        public void run() {
                            viewModel.updateTransactionFilter(f);
                        }
                    }
            );
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.rightMargin = UiUtils.dp(context, 8);
            chip.setLayoutParams(lp);
            filterRow.addView(chip);
        }
        filterScroll.addView(filterRow);
        root.addView(filterScroll);

        root.addView(UiUtils.makeSpace(context, 1, 14));

        final LinearLayout listContainer = new LinearLayout(context);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        root.addView(listContainer);

        final Runnable populateList = new Runnable() {
            @Override
            public void run() {
                listContainer.removeAllViews();
                List<Transaction> list = viewModel.getDashboardData().filteredTransactions;
                if (list.isEmpty()) {
                    LinearLayout empty = new LinearLayout(context);
                    empty.setOrientation(LinearLayout.VERTICAL);
                    empty.setGravity(Gravity.CENTER);
                    empty.setPadding(UiUtils.dp(context, 20), UiUtils.dp(context, 32), UiUtils.dp(context, 20), UiUtils.dp(context, 32));
                    empty.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 18f));

                    TextView tv = new TextView(context);
                    tv.setText("No matching transactions found");
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                    tv.setTextColor(UiUtils.getTextPrimaryColor(isDark));
                    empty.addView(tv);
                    listContainer.addView(empty);
                } else {
                    for (final Transaction tx : list) {
                        View row = TransactionRowView.create(
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
                        listContainer.addView(row);
                        listContainer.addView(UiUtils.makeSpace(context, 1, 8));
                    }
                }
            }
        };
        populateList.run();

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String q = s.toString();
                if (!q.equals(viewModel.getDashboardData().searchQuery)) {
                    viewModel.updateSearchQuery(q);
                }
            }
        });

        scroll.addView(root);
        return scroll;
    }
}
