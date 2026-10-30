package com.example.ui.screens;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.ui.components.UiUtils;
import com.example.ui.viewmodel.PocketPalViewModel;

public class CreateProfileView {

    public static View create(final Context context, final PocketPalViewModel viewModel) {
        final String[] selectedRole = {"Student"};
        final String[] selectedEmoji = {"🎓"};
        final long[] selectedColor = {0xFF0F8A74L};

        ScrollView scroll = new ScrollView(context);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.WHITE);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(Color.WHITE);
        int pad = UiUtils.dp(context, 22);
        root.setPadding(pad, UiUtils.dp(context, 28), pad, UiUtils.dp(context, 32));

        final TextView topAvatar = new TextView(context);
        topAvatar.setText(selectedEmoji[0]);
        topAvatar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
        topAvatar.setGravity(Gravity.CENTER);
        int topAvSize = UiUtils.dp(context, 78);
        topAvatar.setLayoutParams(new LinearLayout.LayoutParams(topAvSize, topAvSize));
        topAvatar.setBackground(UiUtils.makeCircle((int) selectedColor[0]));
        root.addView(topAvatar);

        root.addView(UiUtils.makeSpace(context, 1, 14));

        TextView welcomeTitle = new TextView(context);
        welcomeTitle.setText("Welcome to PocketPal!");
        welcomeTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        welcomeTitle.setTypeface(Typeface.DEFAULT_BOLD);
        welcomeTitle.setTextColor(Color.BLACK);
        welcomeTitle.setGravity(Gravity.CENTER);
        root.addView(welcomeTitle);

        root.addView(UiUtils.makeSpace(context, 1, 6));

        TextView welcomeSub = new TextView(context);
        welcomeSub.setText("Create your personal profile to start tracking your pocket money, budgets, and savings goals.");
        welcomeSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        welcomeSub.setTextColor(0xFF334155);
        welcomeSub.setGravity(Gravity.CENTER);
        root.addView(welcomeSub);

        root.addView(UiUtils.makeSpace(context, 1, 20));

        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        int cardPad = UiUtils.dp(context, 18);
        card.setPadding(cardPad, cardPad, cardPad, cardPad);
        card.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 22f, 0xFFE2E8F0, 1.5f));
        card.setElevation(UiUtils.dp(context, 2));
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView nameLabel = new TextView(context);
        nameLabel.setText("Your Name");
        nameLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        nameLabel.setTypeface(Typeface.DEFAULT_BOLD);
        nameLabel.setTextColor(Color.BLACK);
        card.addView(nameLabel);
        card.addView(UiUtils.makeSpace(context, 1, 6));

        final EditText nameEt = new EditText(context);
        nameEt.setHint("Enter your name");
        nameEt.setHintTextColor(0xFF64748B);
        nameEt.setTextColor(Color.BLACK);
        nameEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        nameEt.setSingleLine(true);
        nameEt.setPadding(UiUtils.dp(context, 14), UiUtils.dp(context, 12), UiUtils.dp(context, 14), UiUtils.dp(context, 12));
        nameEt.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 14f, 0xFF94A3B8, 1.5f));
        nameEt.setTag("input_first_profile_name");
        card.addView(nameEt);

        final TextView nameErr = new TextView(context);
        nameErr.setText("Please enter your name to continue");
        nameErr.setTextColor(UiUtils.COLOR_EXPENSE_RED);
        nameErr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        nameErr.setVisibility(View.GONE);
        card.addView(nameErr);

        card.addView(UiUtils.makeSpace(context, 1, 16));

        TextView roleLabel = new TextView(context);
        roleLabel.setText("Who are you?");
        roleLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        roleLabel.setTypeface(Typeface.DEFAULT_BOLD);
        roleLabel.setTextColor(Color.BLACK);
        card.addView(roleLabel);
        card.addView(UiUtils.makeSpace(context, 1, 8));

        final LinearLayout rolesRow = new LinearLayout(context);
        rolesRow.setOrientation(LinearLayout.HORIZONTAL);
        final String[] roles = {"Student", "College", "School", "Teenager"};
        final Runnable[] refreshRoles = new Runnable[1];
        refreshRoles[0] = new Runnable() {
            @Override
            public void run() {
                rolesRow.removeAllViews();
                for (int i = 0; i < roles.length; i++) {
                    final String role = roles[i];
                    boolean isSelected = role.equals(selectedRole[0]);
                    TextView chip = UiUtils.createHighlightedChip(
                            context,
                            role,
                            isSelected,
                            new Runnable() {
                                @Override
                                public void run() {
                                    selectedRole[0] = role;
                                    refreshRoles[0].run();
                                }
                            }
                    );
                    chip.setTag("role_chip_" + role);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    lp.rightMargin = UiUtils.dp(context, 8);
                    chip.setLayoutParams(lp);
                    rolesRow.addView(chip);
                }
            }
        };
        refreshRoles[0].run();

        HorizontalScrollView roleScroll = new HorizontalScrollView(context);
        roleScroll.setHorizontalScrollBarEnabled(false);
        roleScroll.addView(rolesRow);
        card.addView(roleScroll);

        card.addView(UiUtils.makeSpace(context, 1, 16));

        TextView budgetLabel = new TextView(context);
        budgetLabel.setText("Monthly Budget Goal (₹)");
        budgetLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        budgetLabel.setTypeface(Typeface.DEFAULT_BOLD);
        budgetLabel.setTextColor(Color.BLACK);
        card.addView(budgetLabel);
        card.addView(UiUtils.makeSpace(context, 1, 6));

        final EditText budgetEt = new EditText(context);
        budgetEt.setText("5000");
        budgetEt.setHint("e.g. 5000");
        budgetEt.setHintTextColor(0xFF64748B);
        budgetEt.setTextColor(Color.BLACK);
        budgetEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        budgetEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        budgetEt.setSingleLine(true);
        budgetEt.setPadding(UiUtils.dp(context, 14), UiUtils.dp(context, 12), UiUtils.dp(context, 14), UiUtils.dp(context, 12));
        budgetEt.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 14f, 0xFF94A3B8, 1.5f));
        budgetEt.setTag("input_first_profile_budget");
        card.addView(budgetEt);

        card.addView(UiUtils.makeSpace(context, 1, 8));

        final LinearLayout presetsRow = new LinearLayout(context);
        presetsRow.setOrientation(LinearLayout.HORIZONTAL);
        final String[] presets = {"3000", "5000", "8000", "10000"};
        final Runnable[] refreshPresets = new Runnable[1];
        refreshPresets[0] = new Runnable() {
            @Override
            public void run() {
                presetsRow.removeAllViews();
                String currentBudget = budgetEt.getText().toString().trim();
                for (int i = 0; i < presets.length; i++) {
                    final String preset = presets[i];
                    boolean isSel = preset.equals(currentBudget);
                    TextView chip = UiUtils.createHighlightedChip(
                            context,
                            "₹" + preset,
                            isSel,
                            new Runnable() {
                                @Override
                                public void run() {
                                    budgetEt.setText(preset);
                                    budgetEt.setSelection(preset.length());
                                    refreshPresets[0].run();
                                }
                            }
                    );
                    chip.setTag("budget_chip_" + preset);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    lp.rightMargin = UiUtils.dp(context, 8);
                    chip.setLayoutParams(lp);
                    presetsRow.addView(chip);
                }
            }
        };
        refreshPresets[0].run();

        budgetEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshPresets[0].run();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        HorizontalScrollView presetScroll = new HorizontalScrollView(context);
        presetScroll.setHorizontalScrollBarEnabled(false);
        presetScroll.addView(presetsRow);
        card.addView(presetScroll);

        card.addView(UiUtils.makeSpace(context, 1, 16));

        TextView avatarLbl = new TextView(context);
        avatarLbl.setText("Pick Your Avatar");
        avatarLbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        avatarLbl.setTypeface(Typeface.DEFAULT_BOLD);
        avatarLbl.setTextColor(Color.BLACK);
        card.addView(avatarLbl);
        card.addView(UiUtils.makeSpace(context, 1, 8));

        final LinearLayout avatarRow = new LinearLayout(context);
        avatarRow.setOrientation(LinearLayout.HORIZONTAL);
        final String[] emojis = {"🎓", "🚀", "🎨", "⚽", "🎸", "💻"};
        final long[] colors = {0xFF0F8A74L, 0xFF3B82F6L, 0xFF8B5CF6L, 0xFFF97316L, 0xFFEC4899L, 0xFF10B981L};
        final Runnable[] refreshAvatars = new Runnable[1];
        refreshAvatars[0] = new Runnable() {
            @Override
            public void run() {
                avatarRow.removeAllViews();
                for (int i = 0; i < emojis.length; i++) {
                    final String em = emojis[i];
                    final long col = colors[i];
                    boolean sel = em.equals(selectedEmoji[0]);
                    TextView av = new TextView(context);
                    av.setText(em);
                    av.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    av.setGravity(Gravity.CENTER);
                    int sz = UiUtils.dp(context, 46);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sz, sz);
                    lp.rightMargin = UiUtils.dp(context, 8);
                    av.setLayoutParams(lp);
                    av.setBackground(UiUtils.makeCircleWithStroke(
                            context,
                            sel ? (int) col : 0xFFF1F5F9,
                            sel ? UiUtils.COLOR_PRIMARY_TEAL_DARK : Color.TRANSPARENT,
                            2.5f
                    ));
                    UiUtils.applyBounceClick(av, new Runnable() {
                        @Override
                        public void run() {
                            selectedEmoji[0] = em;
                            selectedColor[0] = col;
                            topAvatar.setText(em);
                            topAvatar.setBackground(UiUtils.makeCircle((int) col));
                            refreshAvatars[0].run();
                        }
                    });
                    avatarRow.addView(av);
                }
            }
        };
        refreshAvatars[0].run();
        card.addView(avatarRow);

        card.addView(UiUtils.makeSpace(context, 1, 22));

        TextView submitBtn = new TextView(context);
        submitBtn.setText("Start Using PocketPal  →");
        submitBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        submitBtn.setTypeface(Typeface.DEFAULT_BOLD);
        submitBtn.setTextColor(Color.WHITE);
        submitBtn.setGravity(Gravity.CENTER);
        submitBtn.setPadding(UiUtils.dp(context, 18), UiUtils.dp(context, 15), UiUtils.dp(context, 18), UiUtils.dp(context, 15));
        submitBtn.setBackground(UiUtils.makeRoundedRect(context, UiUtils.COLOR_PRIMARY_TEAL, 14f));
        submitBtn.setTag("btn_create_first_profile");
        UiUtils.applyBounceClick(submitBtn, new Runnable() {
            @Override
            public void run() {
                String n = nameEt.getText().toString().trim();
                if (n.isEmpty()) {
                    nameErr.setVisibility(View.VISIBLE);
                    return;
                }
                nameErr.setVisibility(View.GONE);
                double b = 5000.0;
                try {
                    b = Double.parseDouble(budgetEt.getText().toString().trim());
                    if (b <= 0) b = 5000.0;
                } catch (Exception ignored) {
                }
                viewModel.createNewProfile(n, selectedRole[0], selectedEmoji[0], selectedColor[0], b);
            }
        });
        card.addView(submitBtn);

        root.addView(card);
        scroll.addView(root);
        return scroll;
    }
}
