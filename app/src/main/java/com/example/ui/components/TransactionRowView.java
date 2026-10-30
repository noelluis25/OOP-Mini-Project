package com.example.ui.components;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.data.model.CategoryItem;
import com.example.data.model.Transaction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransactionRowView {

    public static View create(
            Context context,
            final Transaction transaction,
            boolean isDark,
            final Runnable onClick,
            final Runnable onDelete
    ) {
        CategoryItem category = transaction.getCategoryItem();

        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setTag("tx_row_" + transaction.id);
        int padH = UiUtils.dp(context, 14);
        int padV = UiUtils.dp(context, 12);
        card.setPadding(padH, padV, padH, padV);
        card.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 16f));
        card.setElevation(UiUtils.dp(context, 1.5f));

        TextView badge = new TextView(context);
        badge.setText(category.emoji);
        badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        badge.setGravity(Gravity.CENTER);
        int badgeSize = UiUtils.dp(context, 48);
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(badgeSize, badgeSize);
        badge.setLayoutParams(badgeParams);
        badge.setBackground(UiUtils.makeRoundedRect(context, category.bgLightColor, 14f));
        card.addView(badge);

        card.addView(UiUtils.makeSpace(context, 12, 1));

        LinearLayout middleCol = new LinearLayout(context);
        middleCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams midParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        middleCol.setLayoutParams(midParams);

        TextView titleTv = new TextView(context);
        titleTv.setText(transaction.title);
        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        titleTv.setTypeface(Typeface.DEFAULT_BOLD);
        titleTv.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        titleTv.setSingleLine(true);
        titleTv.setEllipsize(TextUtils.TruncateAt.END);
        middleCol.addView(titleTv);

        middleCol.addView(UiUtils.makeSpace(context, 1, 3));

        LinearLayout metaRow = new LinearLayout(context);
        metaRow.setOrientation(LinearLayout.HORIZONTAL);
        metaRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView catPill = new TextView(context);
        catPill.setText(category.label);
        catPill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        catPill.setTypeface(Typeface.DEFAULT_BOLD);
        catPill.setTextColor(category.colorValue);
        catPill.setPadding(UiUtils.dp(context, 6), UiUtils.dp(context, 2), UiUtils.dp(context, 6), UiUtils.dp(context, 2));
        catPill.setBackground(UiUtils.makeRoundedRect(context, (category.colorValue & 0x00FFFFFF) | 0x24000000, 6f));
        metaRow.addView(catPill);

        metaRow.addView(UiUtils.makeSpace(context, 6, 1));

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
        TextView dateTv = new TextView(context);
        dateTv.setText(sdf.format(new Date(transaction.timestamp)) + " • " + transaction.paymentMethod);
        dateTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        dateTv.setTextColor(UiUtils.getTextSecondaryColor(isDark));
        dateTv.setSingleLine(true);
        dateTv.setEllipsize(TextUtils.TruncateAt.END);
        metaRow.addView(dateTv);

        middleCol.addView(metaRow);

        if (transaction.note != null && !transaction.note.trim().isEmpty()) {
            middleCol.addView(UiUtils.makeSpace(context, 1, 2));
            TextView noteTv = new TextView(context);
            noteTv.setText(transaction.note);
            noteTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            noteTv.setTextColor(UiUtils.getTextSecondaryColor(isDark));
            noteTv.setSingleLine(true);
            noteTv.setEllipsize(TextUtils.TruncateAt.END);
            middleCol.addView(noteTv);
        }

        card.addView(middleCol);
        card.addView(UiUtils.makeSpace(context, 8, 1));

        LinearLayout rightCol = new LinearLayout(context);
        rightCol.setOrientation(LinearLayout.VERTICAL);
        rightCol.setGravity(Gravity.END);

        String prefix = transaction.isExpense() ? "-₹" : "+₹";
        int amountColor = transaction.isExpense() ? UiUtils.COLOR_EXPENSE_RED : UiUtils.COLOR_INCOME_GREEN;

        TextView amountTv = new TextView(context);
        amountTv.setText(prefix + (int) transaction.amount);
        amountTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        amountTv.setTypeface(Typeface.DEFAULT_BOLD);
        amountTv.setTextColor(amountColor);
        rightCol.addView(amountTv);

        if (onDelete != null) {
            TextView delBtn = new TextView(context);
            delBtn.setText("🗑");
            delBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            delBtn.setPadding(UiUtils.dp(context, 6), UiUtils.dp(context, 4), UiUtils.dp(context, 2), UiUtils.dp(context, 2));
            delBtn.setContentDescription("Delete transaction");
            UiUtils.applyBounceClick(delBtn, onDelete);
            rightCol.addView(delBtn);
        }

        card.addView(rightCol);
        UiUtils.applyBounceClick(card, onClick);
        return card;
    }
}
