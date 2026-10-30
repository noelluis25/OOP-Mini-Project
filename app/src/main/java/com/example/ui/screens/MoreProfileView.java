package com.example.ui.screens;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import com.example.data.model.UserProfile;
import com.example.ui.components.UiUtils;
import com.example.ui.viewmodel.AppScreen;
import com.example.ui.viewmodel.DashboardData;
import com.example.ui.viewmodel.PocketPalViewModel;

public class MoreProfileView {

    public static View create(final Context context, final PocketPalViewModel viewModel) {
        final DashboardData data = viewModel.getDashboardData();
        final boolean isDark = data.preferences != null && data.preferences.isDarkMode;
        final UserProfile user = data.activeUser;

        ScrollView scroll = new ScrollView(context);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(UiUtils.getBgColor(isDark));

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = UiUtils.dp(context, 16);
        root.setPadding(pad, UiUtils.dp(context, 16), pad, UiUtils.dp(context, 28));

        TextView title = new TextView(context);
        title.setText("Profile & Settings");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        root.addView(title);

        root.addView(UiUtils.makeSpace(context, 1, 14));

        // Active Profile Hero Card
        LinearLayout profileCard = new LinearLayout(context);
        profileCard.setOrientation(LinearLayout.VERTICAL);
        int cPad = UiUtils.dp(context, 18);
        profileCard.setPadding(cPad, cPad, cPad, cPad);
        profileCard.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 20f));
        profileCard.setElevation(UiUtils.dp(context, 2));

        LinearLayout pRow = new LinearLayout(context);
        pRow.setOrientation(LinearLayout.HORIZONTAL);
        pRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView av = new TextView(context);
        av.setText(user != null ? user.avatarEmoji : "🎓");
        av.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        av.setGravity(Gravity.CENTER);
        int avSz = UiUtils.dp(context, 56);
        av.setLayoutParams(new LinearLayout.LayoutParams(avSz, avSz));
        av.setBackground(UiUtils.makeCircle(user != null ? (int) user.avatarColorHex : UiUtils.COLOR_PRIMARY_TEAL));
        pRow.addView(av);

        pRow.addView(UiUtils.makeSpace(context, 14, 1));

        LinearLayout pInfo = new LinearLayout(context);
        pInfo.setOrientation(LinearLayout.VERTICAL);
        pInfo.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView pName = new TextView(context);
        pName.setText(user != null ? user.name : "Student");
        pName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        pName.setTypeface(Typeface.DEFAULT_BOLD);
        pName.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        pInfo.addView(pName);

        TextView pRole = new TextView(context);
        pRole.setText((user != null ? user.roleTag : "Student") + " • Monthly Budget: ₹" + (user != null ? (int) user.monthlyBudget : 5000));
        pRole.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        pRole.setTextColor(UiUtils.getTextSecondaryColor(isDark));
        pInfo.addView(pRole);

        pRow.addView(pInfo);
        profileCard.addView(pRow);

        profileCard.addView(UiUtils.makeSpace(context, 1, 14));

        LinearLayout pBtns = new LinearLayout(context);
        pBtns.setOrientation(LinearLayout.HORIZONTAL);

        TextView editProfileBtn = new TextView(context);
        editProfileBtn.setText("✏️ Edit Profile");
        editProfileBtn.setGravity(Gravity.CENTER);
        editProfileBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        editProfileBtn.setTypeface(Typeface.DEFAULT_BOLD);
        editProfileBtn.setTextColor(UiUtils.COLOR_PRIMARY_TEAL);
        editProfileBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 10), UiUtils.dp(context, 12), UiUtils.dp(context, 10));
        editProfileBtn.setBackground(UiUtils.makeRoundedRect(
                context,
                isDark ? 0xFF04382E : UiUtils.COLOR_PRIMARY_TEAL_CONTAINER,
                12f
        ));
        editProfileBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        UiUtils.applyBounceClick(editProfileBtn, new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    showEditProfileDialog(context, viewModel, user);
                }
            }
        });
        pBtns.addView(editProfileBtn);

        pBtns.addView(UiUtils.makeSpace(context, 10, 1));

        TextView switchBtn = new TextView(context);
        switchBtn.setText("👥 Switch User");
        switchBtn.setGravity(Gravity.CENTER);
        switchBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        switchBtn.setTypeface(Typeface.DEFAULT_BOLD);
        switchBtn.setTextColor(Color.WHITE);
        switchBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 10), UiUtils.dp(context, 12), UiUtils.dp(context, 10));
        switchBtn.setBackground(UiUtils.makeRoundedRect(context, UiUtils.COLOR_PRIMARY_TEAL, 12f));
        switchBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        UiUtils.applyBounceClick(switchBtn, new Runnable() {
            @Override
            public void run() {
                viewModel.setShowUserSwitcher(true);
            }
        });
        pBtns.addView(switchBtn);

        profileCard.addView(pBtns);
        root.addView(profileCard);

        root.addView(UiUtils.makeSpace(context, 1, 16));

        // Quick Actions Card
        LinearLayout actionsCard = new LinearLayout(context);
        actionsCard.setOrientation(LinearLayout.VERTICAL);
        actionsCard.setPadding(cPad, cPad, cPad, cPad);
        actionsCard.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 20f));

        actionsCard.addView(makeActionRow(context, "📊", "Spending Breakdown & Reports", "Inspect category percentages & charts", isDark, new Runnable() {
            @Override
            public void run() {
                viewModel.navigateTo(AppScreen.SPENDING_BREAKDOWN);
            }
        }));
        actionsCard.addView(UiUtils.makeSpace(context, 1, 10));
        actionsCard.addView(makeActionRow(context, "📤", "Export Monthly Report", "Share formatted text summary with parents or friends", isDark, new Runnable() {
            @Override
            public void run() {
                String report = viewModel.buildExportSummaryText();
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "PocketPal Monthly Report");
                sendIntent.putExtra(Intent.EXTRA_TEXT, report);
                context.startActivity(Intent.createChooser(sendIntent, "Export Report"));
            }
        }));

        root.addView(actionsCard);
        root.addView(UiUtils.makeSpace(context, 1, 16));

        // Preferences Card (Dark Mode & Alerts)
        LinearLayout prefsCard = new LinearLayout(context);
        prefsCard.setOrientation(LinearLayout.VERTICAL);
        prefsCard.setPadding(cPad, cPad, cPad, cPad);
        prefsCard.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getCardColor(isDark), 20f));

        LinearLayout darkRow = new LinearLayout(context);
        darkRow.setOrientation(LinearLayout.HORIZONTAL);
        darkRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView darkLbl = new TextView(context);
        darkLbl.setText("🌙 Dark Theme");
        darkLbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        darkLbl.setTypeface(Typeface.DEFAULT_BOLD);
        darkLbl.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        darkLbl.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        darkRow.addView(darkLbl);

        Switch darkSwitch = new Switch(context);
        darkSwitch.setChecked(isDark);
        darkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.toggleDarkMode(isChecked);
            }
        });
        darkRow.addView(darkSwitch);
        prefsCard.addView(darkRow);

        root.addView(prefsCard);
        root.addView(UiUtils.makeSpace(context, 1, 16));

        // Danger Zone (Reset Wallet / Delete Profile)
        LinearLayout dangerRow = new LinearLayout(context);
        dangerRow.setOrientation(LinearLayout.HORIZONTAL);

        TextView resetBtn = new TextView(context);
        resetBtn.setText("🔄 Reset Wallet Data");
        resetBtn.setGravity(Gravity.CENTER);
        resetBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        resetBtn.setTypeface(Typeface.DEFAULT_BOLD);
        resetBtn.setTextColor(UiUtils.COLOR_EXPENSE_RED);
        resetBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12));
        resetBtn.setBackground(UiUtils.makeRoundedRect(context, UiUtils.COLOR_EXPENSE_RED_BG, 14f));
        resetBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        UiUtils.applyBounceClick(resetBtn, new Runnable() {
            @Override
            public void run() {
                viewModel.resetCurrentUserData();
            }
        });
        dangerRow.addView(resetBtn);

        dangerRow.addView(UiUtils.makeSpace(context, 10, 1));

        TextView delUserBtn = new TextView(context);
        delUserBtn.setText("🗑 Delete Profile");
        delUserBtn.setGravity(Gravity.CENTER);
        delUserBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        delUserBtn.setTypeface(Typeface.DEFAULT_BOLD);
        delUserBtn.setTextColor(Color.WHITE);
        delUserBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12));
        delUserBtn.setBackground(UiUtils.makeRoundedRect(context, UiUtils.COLOR_EXPENSE_RED, 14f));
        delUserBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        UiUtils.applyBounceClick(delUserBtn, new Runnable() {
            @Override
            public void run() {
                viewModel.deleteCurrentUserProfile();
            }
        });
        dangerRow.addView(delUserBtn);

        root.addView(dangerRow);

        scroll.addView(root);
        return scroll;
    }

    private static View makeActionRow(
            Context context,
            String emoji,
            String title,
            String subtitle,
            boolean isDark,
            Runnable onClick
    ) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(UiUtils.dp(context, 10), UiUtils.dp(context, 10), UiUtils.dp(context, 10), UiUtils.dp(context, 10));
        row.setBackground(UiUtils.makeRoundedRect(context, UiUtils.getBgColor(isDark), 14f));

        TextView em = new TextView(context);
        em.setText(emoji);
        em.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        row.addView(em);

        row.addView(UiUtils.makeSpace(context, 12, 1));

        LinearLayout col = new LinearLayout(context);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView t = new TextView(context);
        t.setText(title);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setTextColor(UiUtils.getTextPrimaryColor(isDark));
        col.addView(t);

        TextView s = new TextView(context);
        s.setText(subtitle);
        s.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        s.setTextColor(UiUtils.getTextSecondaryColor(isDark));
        col.addView(s);

        row.addView(col);

        TextView arrow = new TextView(context);
        arrow.setText("→");
        arrow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        arrow.setTypeface(Typeface.DEFAULT_BOLD);
        arrow.setTextColor(UiUtils.COLOR_PRIMARY_TEAL);
        row.addView(arrow);

        UiUtils.applyBounceClick(row, onClick);
        return row;
    }

    private static void showEditProfileDialog(
            final Context context,
            final PocketPalViewModel viewModel,
            final UserProfile user
    ) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final String[] selectedRole = {user.roleTag};

        LinearLayout box = new LinearLayout(context);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = UiUtils.dp(context, 20);
        box.setPadding(pad, pad, pad, pad);
        box.setBackground(UiUtils.makeRoundedRect(context, Color.WHITE, 22f));

        TextView title = new TextView(context);
        title.setText("Edit Profile");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.BLACK);
        box.addView(title);
        box.addView(UiUtils.makeSpace(context, 1, 12));

        final EditText nameEt = new EditText(context);
        nameEt.setText(user.name);
        nameEt.setHint("Your Name");
        nameEt.setHintTextColor(0xFF64748B);
        nameEt.setTextColor(Color.BLACK);
        nameEt.setSingleLine(true);
        nameEt.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 10), UiUtils.dp(context, 12), UiUtils.dp(context, 10));
        nameEt.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 12f, 0xFF94A3B8, 1.5f));
        box.addView(nameEt);

        box.addView(UiUtils.makeSpace(context, 1, 10));

        final EditText budgetEt = new EditText(context);
        budgetEt.setText(String.valueOf((int) user.monthlyBudget));
        budgetEt.setHint("Monthly Budget (₹)");
        budgetEt.setHintTextColor(0xFF64748B);
        budgetEt.setTextColor(Color.BLACK);
        budgetEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        budgetEt.setSingleLine(true);
        budgetEt.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 10), UiUtils.dp(context, 12), UiUtils.dp(context, 10));
        budgetEt.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 12f, 0xFF94A3B8, 1.5f));
        box.addView(budgetEt);

        box.addView(UiUtils.makeSpace(context, 1, 12));

        final LinearLayout rolesRow = new LinearLayout(context);
        rolesRow.setOrientation(LinearLayout.HORIZONTAL);
        final String[] roles = {"Student", "College", "School", "Teenager"};
        final Runnable[] refreshRoles = new Runnable[1];
        refreshRoles[0] = new Runnable() {
            @Override
            public void run() {
                rolesRow.removeAllViews();
                for (int i = 0; i < roles.length; i++) {
                    final String r = roles[i];
                    TextView chip = UiUtils.createHighlightedChip(
                            context,
                            r,
                            r.equals(selectedRole[0]),
                            new Runnable() {
                                @Override
                                public void run() {
                                    selectedRole[0] = r;
                                    refreshRoles[0].run();
                                }
                            }
                    );
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

        HorizontalScrollView scrollRoles = new HorizontalScrollView(context);
        scrollRoles.setHorizontalScrollBarEnabled(false);
        scrollRoles.addView(rolesRow);
        box.addView(scrollRoles);

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
                double b = user.monthlyBudget;
                try {
                    b = Double.parseDouble(budgetEt.getText().toString().trim());
                } catch (Exception ignored) {
                }
                viewModel.updateActiveUserDetails(n, selectedRole[0], user.avatarEmoji, b);
                dialog.dismiss();
            }
        });
        actions.addView(saveBtn);
        box.addView(actions);

        dialog.setContentView(box, new ViewGroup.LayoutParams(UiUtils.dp(context, 340), ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();
    }
}
