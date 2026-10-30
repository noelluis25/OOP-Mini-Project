package com.example.ui.components;

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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.data.model.UserProfile;
import com.example.ui.viewmodel.PocketPalViewModel;
import java.util.List;

public class UserSwitcherDialogHelper {

    public static void show(final Context context, final PocketPalViewModel viewModel) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final ScrollView scrollRoot = new ScrollView(context);
        final LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = UiUtils.dp(context, 20);
        container.setPadding(pad, pad, pad, pad);
        container.setBackground(UiUtils.makeRoundedRect(context, Color.WHITE, 24f));
        scrollRoot.addView(container);

        final boolean[] isCreatingNew = {false};
        final String[] selectedRole = {"College"};
        final String[] selectedEmoji = {"🎓"};
        final long[] selectedColor = {0xFF0F8A74L};

        final Runnable[] renderRef = new Runnable[1];
        renderRef[0] = new Runnable() {
            @Override
            public void run() {
                container.removeAllViews();

                if (!isCreatingNew[0]) {
                    LinearLayout topRow = new LinearLayout(context);
                    topRow.setOrientation(LinearLayout.HORIZONTAL);
                    topRow.setGravity(Gravity.CENTER_VERTICAL);

                    TextView title = new TextView(context);
                    title.setText("Switch Profile");
                    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    title.setTypeface(Typeface.DEFAULT_BOLD);
                    title.setTextColor(Color.BLACK);
                    title.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    topRow.addView(title);

                    TextView closeBtn = new TextView(context);
                    closeBtn.setText("✕");
                    closeBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    closeBtn.setTextColor(Color.BLACK);
                    closeBtn.setPadding(UiUtils.dp(context, 8), UiUtils.dp(context, 4), UiUtils.dp(context, 8), UiUtils.dp(context, 4));
                    UiUtils.applyBounceClick(closeBtn, new Runnable() {
                        @Override
                        public void run() {
                            viewModel.setShowUserSwitcher(false);
                            dialog.dismiss();
                        }
                    });
                    topRow.addView(closeBtn);
                    container.addView(topRow);

                    TextView sub = new TextView(context);
                    sub.setText("Each profile has its own independent wallet, budget, and savings goals.");
                    sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    sub.setTextColor(0xFF475569);
                    container.addView(sub);
                    container.addView(UiUtils.makeSpace(context, 1, 14));

                    List<UserProfile> users = viewModel.getDashboardData().allUsers;
                    UserProfile active = viewModel.getDashboardData().activeUser;
                    long activeId = active != null ? active.id : -1L;

                    for (final UserProfile user : users) {
                        boolean isCurrent = user.id == activeId;
                        LinearLayout row = new LinearLayout(context);
                        row.setOrientation(LinearLayout.HORIZONTAL);
                        row.setGravity(Gravity.CENTER_VERTICAL);
                        row.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12));
                        row.setBackground(UiUtils.makeRoundedRectWithStroke(
                                context,
                                isCurrent ? UiUtils.COLOR_PRIMARY_TEAL_CONTAINER : 0xFFF8FAFC,
                                16f,
                                isCurrent ? UiUtils.COLOR_PRIMARY_TEAL : 0xFFE2E8F0,
                                isCurrent ? 2f : 1f
                        ));

                        TextView av = new TextView(context);
                        av.setText(user.avatarEmoji);
                        av.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        av.setGravity(Gravity.CENTER);
                        int avSize = UiUtils.dp(context, 44);
                        av.setLayoutParams(new LinearLayout.LayoutParams(avSize, avSize));
                        av.setBackground(UiUtils.makeCircle((int) user.avatarColorHex));
                        row.addView(av);

                        row.addView(UiUtils.makeSpace(context, 12, 1));

                        LinearLayout infoCol = new LinearLayout(context);
                        infoCol.setOrientation(LinearLayout.VERTICAL);
                        infoCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                        TextView uName = new TextView(context);
                        uName.setText(user.name);
                        uName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        uName.setTypeface(Typeface.DEFAULT_BOLD);
                        uName.setTextColor(Color.BLACK);
                        infoCol.addView(uName);

                        TextView uSub = new TextView(context);
                        uSub.setText(user.roleTag + " • Budget ₹" + (int) user.monthlyBudget);
                        uSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        uSub.setTextColor(0xFF475569);
                        infoCol.addView(uSub);

                        row.addView(infoCol);

                        if (isCurrent) {
                            TextView chk = new TextView(context);
                            chk.setText("✓");
                            chk.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                            chk.setTypeface(Typeface.DEFAULT_BOLD);
                            chk.setTextColor(UiUtils.COLOR_PRIMARY_TEAL);
                            row.addView(chk);
                        }

                        UiUtils.applyBounceClick(row, new Runnable() {
                            @Override
                            public void run() {
                                viewModel.switchUser(user.id);
                                dialog.dismiss();
                            }
                        });

                        container.addView(row);
                        container.addView(UiUtils.makeSpace(context, 1, 8));
                    }

                    container.addView(UiUtils.makeSpace(context, 1, 8));

                    TextView addBtn = new TextView(context);
                    addBtn.setText("＋ Add Another Student Profile");
                    addBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    addBtn.setTypeface(Typeface.DEFAULT_BOLD);
                    addBtn.setTextColor(Color.WHITE);
                    addBtn.setGravity(Gravity.CENTER);
                    addBtn.setPadding(UiUtils.dp(context, 16), UiUtils.dp(context, 14), UiUtils.dp(context, 16), UiUtils.dp(context, 14));
                    addBtn.setBackground(UiUtils.makeRoundedRect(context, UiUtils.COLOR_PRIMARY_TEAL, 14f));
                    addBtn.setTag("btn_create_new_profile");
                    UiUtils.applyBounceClick(addBtn, new Runnable() {
                        @Override
                        public void run() {
                            isCreatingNew[0] = true;
                            renderRef[0].run();
                        }
                    });
                    container.addView(addBtn);

                } else {
                    TextView title = new TextView(context);
                    title.setText("New Student Profile");
                    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    title.setTypeface(Typeface.DEFAULT_BOLD);
                    title.setTextColor(Color.BLACK);
                    container.addView(title);
                    container.addView(UiUtils.makeSpace(context, 1, 12));

                    final EditText nameEt = new EditText(context);
                    nameEt.setHint("Profile Name (e.g., Riya, Hostel Mess)");
                    nameEt.setHintTextColor(0xFF64748B);
                    nameEt.setTextColor(Color.BLACK);
                    nameEt.setSingleLine(true);
                    nameEt.setPadding(UiUtils.dp(context, 14), UiUtils.dp(context, 12), UiUtils.dp(context, 14), UiUtils.dp(context, 12));
                    nameEt.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 12f, 0xFF94A3B8, 1.5f));
                    nameEt.setTag("input_new_profile_name");
                    container.addView(nameEt);

                    container.addView(UiUtils.makeSpace(context, 1, 10));

                    final EditText budgetEt = new EditText(context);
                    budgetEt.setText("5000");
                    budgetEt.setHint("Monthly Budget (₹)");
                    budgetEt.setHintTextColor(0xFF64748B);
                    budgetEt.setTextColor(Color.BLACK);
                    budgetEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    budgetEt.setSingleLine(true);
                    budgetEt.setPadding(UiUtils.dp(context, 14), UiUtils.dp(context, 12), UiUtils.dp(context, 14), UiUtils.dp(context, 12));
                    budgetEt.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 12f, 0xFF94A3B8, 1.5f));
                    budgetEt.setTag("input_new_profile_budget");
                    container.addView(budgetEt);

                    container.addView(UiUtils.makeSpace(context, 1, 12));

                    TextView roleLbl = new TextView(context);
                    roleLbl.setText("Role Tag");
                    roleLbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    roleLbl.setTypeface(Typeface.DEFAULT_BOLD);
                    roleLbl.setTextColor(Color.BLACK);
                    container.addView(roleLbl);
                    container.addView(UiUtils.makeSpace(context, 1, 6));

                    final LinearLayout rolesRow = new LinearLayout(context);
                    rolesRow.setOrientation(LinearLayout.HORIZONTAL);
                    final String[] roles = {"Student", "College", "School", "Teenager"};
                    final Runnable[] refreshRoleChips = new Runnable[1];
                    refreshRoleChips[0] = new Runnable() {
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
                                                refreshRoleChips[0].run();
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
                    refreshRoleChips[0].run();

                    HorizontalScrollView roleScroll = new HorizontalScrollView(context);
                    roleScroll.setHorizontalScrollBarEnabled(false);
                    roleScroll.addView(rolesRow);
                    container.addView(roleScroll);

                    container.addView(UiUtils.makeSpace(context, 1, 12));

                    TextView emojiLbl = new TextView(context);
                    emojiLbl.setText("Choose Avatar");
                    emojiLbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    emojiLbl.setTypeface(Typeface.DEFAULT_BOLD);
                    emojiLbl.setTextColor(Color.BLACK);
                    container.addView(emojiLbl);
                    container.addView(UiUtils.makeSpace(context, 1, 6));

                    final LinearLayout emojiRow = new LinearLayout(context);
                    emojiRow.setOrientation(LinearLayout.HORIZONTAL);
                    final String[] emojis = {"🎓", "🚀", "🎨", "⚽", "🎸", "💻"};
                    final long[] colors = {0xFF0F8A74L, 0xFF3B82F6L, 0xFF8B5CF6L, 0xFFF97316L, 0xFFEC4899L, 0xFF10B981L};
                    final Runnable[] refreshEmojis = new Runnable[1];
                    refreshEmojis[0] = new Runnable() {
                        @Override
                        public void run() {
                            emojiRow.removeAllViews();
                            for (int i = 0; i < emojis.length; i++) {
                                final String em = emojis[i];
                                final long col = colors[i];
                                boolean sel = em.equals(selectedEmoji[0]);
                                TextView b = new TextView(context);
                                b.setText(em);
                                b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                b.setGravity(Gravity.CENTER);
                                int sz = UiUtils.dp(context, 44);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sz, sz);
                                lp.rightMargin = UiUtils.dp(context, 8);
                                b.setLayoutParams(lp);
                                b.setBackground(UiUtils.makeCircleWithStroke(
                                        context,
                                        sel ? (int) col : 0xFFF1F5F9,
                                        sel ? UiUtils.COLOR_PRIMARY_TEAL_DARK : Color.TRANSPARENT,
                                        2f
                                ));
                                UiUtils.applyBounceClick(b, new Runnable() {
                                    @Override
                                    public void run() {
                                        selectedEmoji[0] = em;
                                        selectedColor[0] = col;
                                        refreshEmojis[0].run();
                                    }
                                });
                                emojiRow.addView(b);
                            }
                        }
                    };
                    refreshEmojis[0].run();
                    container.addView(emojiRow);

                    container.addView(UiUtils.makeSpace(context, 1, 18));

                    LinearLayout actions = new LinearLayout(context);
                    actions.setOrientation(LinearLayout.HORIZONTAL);

                    TextView backBtn = new TextView(context);
                    backBtn.setText("Back");
                    backBtn.setGravity(Gravity.CENTER);
                    backBtn.setTextColor(Color.BLACK);
                    backBtn.setTypeface(Typeface.DEFAULT_BOLD);
                    backBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12));
                    backBtn.setBackground(UiUtils.makeRoundedRectWithStroke(context, Color.WHITE, 12f, 0xFFCBD5E1, 1f));
                    backBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    UiUtils.applyBounceClick(backBtn, new Runnable() {
                        @Override
                        public void run() {
                            isCreatingNew[0] = false;
                            renderRef[0].run();
                        }
                    });
                    actions.addView(backBtn);

                    actions.addView(UiUtils.makeSpace(context, 10, 1));

                    TextView createBtn = new TextView(context);
                    createBtn.setText("Create Profile");
                    createBtn.setGravity(Gravity.CENTER);
                    createBtn.setTextColor(Color.WHITE);
                    createBtn.setTypeface(Typeface.DEFAULT_BOLD);
                    createBtn.setPadding(UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12), UiUtils.dp(context, 12));
                    createBtn.setBackground(UiUtils.makeRoundedRect(context, UiUtils.COLOR_PRIMARY_TEAL, 12f));
                    createBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    createBtn.setTag("btn_confirm_create_profile");
                    UiUtils.applyBounceClick(createBtn, new Runnable() {
                        @Override
                        public void run() {
                            String n = nameEt.getText().toString().trim();
                            if (n.isEmpty()) return;
                            double b = 5000.0;
                            try {
                                b = Double.parseDouble(budgetEt.getText().toString().trim());
                            } catch (Exception ignored) {
                            }
                            viewModel.createNewProfile(n, selectedRole[0], selectedEmoji[0], selectedColor[0], b);
                            dialog.dismiss();
                        }
                    });
                    actions.addView(createBtn);
                    container.addView(actions);
                }
            }
        };

        renderRef[0].run();
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int dialogWidth = Math.min(UiUtils.dp(context, 340), screenWidth - UiUtils.dp(context, 32));
        dialog.setContentView(scrollRoot, new ViewGroup.LayoutParams(
                dialogWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        dialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(android.content.DialogInterface d) {
                viewModel.setShowUserSwitcher(false);
            }
        });
        dialog.show();
    }
}
