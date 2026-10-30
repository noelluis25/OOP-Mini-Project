package com.example.ui.components;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.data.model.CategoryItem;
import com.example.data.model.ExpenseCategory;
import com.example.data.model.IncomeCategory;
import com.example.data.model.ParsedVoiceTransaction;
import com.example.data.model.TransactionType;
import com.example.ui.viewmodel.PocketPalViewModel;
import java.util.ArrayList;
import java.util.List;

public class VoiceConfirmationDialogHelper {

    public static final int REQUEST_RECORD_AUDIO = 1001;

    public static void startVoiceEntry(final Activity activity, final PocketPalViewModel viewModel) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO
            );
            return;
        }

        showVoiceListeningDialog(activity, viewModel);
    }

    private static void showVoiceListeningDialog(final Activity activity, final PocketPalViewModel viewModel) {
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

        // Header
        LinearLayout topRow = new LinearLayout(activity);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = new TextView(activity);
        title.setText("🎙️ Voice Entry");
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
        topRow.addView(closeBtn);
        container.addView(topRow);

        container.addView(UiUtils.makeSpace(activity, 1, 16));

        // Animated Mic Visualizer Circle
        final View micPulseBg = new View(activity);
        int micCircleSize = UiUtils.dp(activity, 72);
        LinearLayout.LayoutParams micParams = new LinearLayout.LayoutParams(micCircleSize, micCircleSize);
        micParams.gravity = Gravity.CENTER_HORIZONTAL;
        micPulseBg.setLayoutParams(micParams);
        micPulseBg.setBackground(UiUtils.makeCircle(0xFFD0F4EA));
        container.addView(micPulseBg);

        container.addView(UiUtils.makeSpace(activity, 1, 12));

        // Status text
        final TextView statusTv = new TextView(activity);
        statusTv.setText("Initializing microphone...");
        statusTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        statusTv.setTypeface(Typeface.DEFAULT_BOLD);
        statusTv.setTextColor(UiUtils.COLOR_PRIMARY_TEAL);
        statusTv.setGravity(Gravity.CENTER);
        container.addView(statusTv);

        container.addView(UiUtils.makeSpace(activity, 1, 14));

        // Sample prompts list for student guidance
        LinearLayout promptBox = new LinearLayout(activity);
        promptBox.setOrientation(LinearLayout.VERTICAL);
        promptBox.setPadding(UiUtils.dp(activity, 14), UiUtils.dp(activity, 12), UiUtils.dp(activity, 14), UiUtils.dp(activity, 12));
        promptBox.setBackground(UiUtils.makeRoundedRect(activity, 0xFFF8FAFC, 16f));

        TextView promptTitle = new TextView(activity);
        promptTitle.setText("💡 Try saying:");
        promptTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        promptTitle.setTypeface(Typeface.DEFAULT_BOLD);
        promptTitle.setTextColor(0xFF475569);
        promptBox.addView(promptTitle);

        promptBox.addView(UiUtils.makeSpace(activity, 1, 4));

        String[] samplePrompts = {
                "• \"Spent 50 rupees on snacks\"",
                "• \"Paid 100 for petrol\"",

        };
        for (String p : samplePrompts) {
            TextView pTv = new TextView(activity);
            pTv.setText(p);
            pTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            pTv.setTextColor(0xFF334155);
            promptBox.addView(pTv);
        }
        container.addView(promptBox);

        container.addView(UiUtils.makeSpace(activity, 1, 18));

        // Retry / Re-listen Button
        final TextView speakBtn = new TextView(activity);
        speakBtn.setText("🎙️ Tap to Speak Again");
        speakBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        speakBtn.setTypeface(Typeface.DEFAULT_BOLD);
        speakBtn.setTextColor(Color.WHITE);
        speakBtn.setGravity(Gravity.CENTER);
        speakBtn.setPadding(UiUtils.dp(activity, 14), UiUtils.dp(activity, 12), UiUtils.dp(activity, 14), UiUtils.dp(activity, 12));
        speakBtn.setBackground(UiUtils.makeRoundedRect(activity, UiUtils.COLOR_PRIMARY_TEAL, 14f));

        container.addView(speakBtn);

        final Handler mainHandler = new Handler(Looper.getMainLooper());
        final VoiceInputManager[] voiceManagerRef = new VoiceInputManager[1];

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voiceManagerRef[0] != null) {
                    voiceManagerRef[0].stopListening();
                }
                dialog.dismiss();
            }
        });

        dialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(android.content.DialogInterface dialogInterface) {
                if (voiceManagerRef[0] != null) {
                    voiceManagerRef[0].stopListening();
                }
            }
        });

        final VoiceInputManager.VoiceInputListener voiceListener = new VoiceInputManager.VoiceInputListener() {
            @Override
            public void onStatusChanged(final String statusMessage) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        statusTv.setText(statusMessage);
                    }
                });
            }

            @Override
            public void onVolumeChanged(final float rmsdB) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        float scale = Math.max(1.0f, Math.min(1.5f, 1.0f + (rmsdB / 15f)));
                        micPulseBg.animate().scaleX(scale).scaleY(scale).setDuration(80).start();
                    }
                });
            }

            @Override
            public void onSpeechResult(final String recognizedText) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (voiceManagerRef[0] != null) {
                            voiceManagerRef[0].stopListening();
                        }
                        dialog.dismiss();
                        List<ParsedVoiceTransaction> parsedList = VoiceTransactionParser.parse(recognizedText);
                        showConfirmationDialog(activity, viewModel, recognizedText, parsedList);
                    }
                });
            }

            @Override
            public void onError(final String errorMessage) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        statusTv.setText("⚠️ " + errorMessage);
                        statusTv.setTextColor(UiUtils.COLOR_EXPENSE_RED);
                        micPulseBg.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                    }
                });
            }
        };

        voiceManagerRef[0] = new VoiceInputManager(activity, voiceListener);

        UiUtils.applyBounceClick(speakBtn, new Runnable() {
            @Override
            public void run() {
                statusTv.setTextColor(UiUtils.COLOR_PRIMARY_TEAL);
                voiceManagerRef[0].startListening();
            }
        });

        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        int dialogWidth = Math.min(UiUtils.dp(activity, 340), screenWidth - UiUtils.dp(activity, 32));
        dialog.setContentView(scrollRoot, new ViewGroup.LayoutParams(
                dialogWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        dialog.show();

        // Start listening automatically when dialog appears
        voiceManagerRef[0].startListening();
    }

    private static void showConfirmationDialog(
            final Activity activity,
            final PocketPalViewModel viewModel,
            final String spokenText,
            List<ParsedVoiceTransaction> initialParsedList
    ) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final ScrollView scrollRoot = new ScrollView(activity);
        final LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = UiUtils.dp(activity, 18);
        container.setPadding(pad, pad, pad, pad);
        container.setBackground(UiUtils.makeRoundedRect(activity, Color.WHITE, 24f));
        scrollRoot.addView(container);

        // Header
        LinearLayout topRow = new LinearLayout(activity);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = new TextView(activity);
        title.setText("Confirm Voice Entry");
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

        container.addView(UiUtils.makeSpace(activity, 1, 10));

        // Spoken Text Speech Bubble Card
        LinearLayout speechBubble = new LinearLayout(activity);
        speechBubble.setOrientation(LinearLayout.VERTICAL);
        speechBubble.setPadding(UiUtils.dp(activity, 12), UiUtils.dp(activity, 10), UiUtils.dp(activity, 12), UiUtils.dp(activity, 10));
        speechBubble.setBackground(UiUtils.makeRoundedRect(activity, UiUtils.COLOR_PRIMARY_TEAL_CONTAINER, 16f));

        TextView spokenLbl = new TextView(activity);
        spokenLbl.setText("🗣️ You said:");
        spokenLbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        spokenLbl.setTypeface(Typeface.DEFAULT_BOLD);
        spokenLbl.setTextColor(UiUtils.COLOR_ON_PRIMARY_TEAL_CONTAINER);
        speechBubble.addView(spokenLbl);

        speechBubble.addView(UiUtils.makeSpace(activity, 1, 2));

        TextView spokenTv = new TextView(activity);
        spokenTv.setText("\"" + spokenText + "\"");
        spokenTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        spokenTv.setTypeface(Typeface.DEFAULT_BOLD);
        spokenTv.setTextColor(UiUtils.COLOR_ON_PRIMARY_TEAL_CONTAINER);
        speechBubble.addView(spokenTv);

        container.addView(speechBubble);

        container.addView(UiUtils.makeSpace(activity, 1, 14));

        // Mutable list of items being edited
        final List<ParsedVoiceTransaction> itemsToSave = new ArrayList<>(
                initialParsedList != null && !initialParsedList.isEmpty()
                        ? initialParsedList
                        : List.of(new ParsedVoiceTransaction(
                                0.0,
                                TransactionType.EXPENSE,
                                ExpenseCategory.OTHERS.toItem(),
                                spokenText,
                                spokenText,
                                "UPI",
                                System.currentTimeMillis()
                        ))
        );

        final LinearLayout cardsContainer = new LinearLayout(activity);
        cardsContainer.setOrientation(LinearLayout.VERTICAL);
        container.addView(cardsContainer);

        // Render card helpers
        final Runnable[] renderCardsRef = new Runnable[1];
        renderCardsRef[0] = new Runnable() {
            @Override
            public void run() {
                cardsContainer.removeAllViews();
                for (int i = 0; i < itemsToSave.size(); i++) {
                    final int idx = i;
                    final ParsedVoiceTransaction tx = itemsToSave.get(i);

                    LinearLayout card = new LinearLayout(activity);
                    card.setOrientation(LinearLayout.VERTICAL);
                    int cPad = UiUtils.dp(activity, 14);
                    card.setPadding(cPad, cPad, cPad, cPad);
                    card.setBackground(UiUtils.makeRoundedRectWithStroke(activity, 0xFFF8FAFC, 18f, 0xFFE2E8F0, 1.5f));

                    // Card header title
                    TextView cardHeader = new TextView(activity);
                    cardHeader.setText(itemsToSave.size() > 1 ? "Transaction #" + (idx + 1) : "Detected Transaction");
                    cardHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    cardHeader.setTypeface(Typeface.DEFAULT_BOLD);
                    cardHeader.setTextColor(0xFF475569);
                    card.addView(cardHeader);

                    card.addView(UiUtils.makeSpace(activity, 1, 8));

                    // Type Toggle (Expense / Income)
                    final LinearLayout typeRow = new LinearLayout(activity);
                    typeRow.setOrientation(LinearLayout.HORIZONTAL);
                    typeRow.setPadding(UiUtils.dp(activity, 3), UiUtils.dp(activity, 3), UiUtils.dp(activity, 3), UiUtils.dp(activity, 3));
                    typeRow.setBackground(UiUtils.makeRoundedRect(activity, 0xFFE2E8F0, 12f));

                    final TextView expBtn = new TextView(activity);
                    expBtn.setText("💸 Expense");
                    expBtn.setGravity(Gravity.CENTER);
                    expBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    expBtn.setTypeface(Typeface.DEFAULT_BOLD);
                    expBtn.setPadding(UiUtils.dp(activity, 8), UiUtils.dp(activity, 8), UiUtils.dp(activity, 8), UiUtils.dp(activity, 8));
                    expBtn.setTextColor(tx.type == TransactionType.EXPENSE ? Color.WHITE : 0xFF64748B);
                    expBtn.setBackground(tx.type == TransactionType.EXPENSE ? UiUtils.makeRoundedRect(activity, UiUtils.COLOR_EXPENSE_RED, 10f) : null);
                    expBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                    final TextView incBtn = new TextView(activity);
                    incBtn.setText("💰 Income");
                    incBtn.setGravity(Gravity.CENTER);
                    incBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    incBtn.setTypeface(Typeface.DEFAULT_BOLD);
                    incBtn.setPadding(UiUtils.dp(activity, 8), UiUtils.dp(activity, 8), UiUtils.dp(activity, 8), UiUtils.dp(activity, 8));
                    incBtn.setTextColor(tx.type == TransactionType.INCOME ? Color.WHITE : 0xFF64748B);
                    incBtn.setBackground(tx.type == TransactionType.INCOME ? UiUtils.makeRoundedRect(activity, UiUtils.COLOR_INCOME_GREEN, 10f) : null);
                    incBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                    typeRow.addView(expBtn);
                    typeRow.addView(incBtn);
                    card.addView(typeRow);

                    card.addView(UiUtils.makeSpace(activity, 1, 10));

                    // Title / Description Field
                    TextView titleLbl = new TextView(activity);
                    titleLbl.setText("Description");
                    titleLbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                    titleLbl.setTypeface(Typeface.DEFAULT_BOLD);
                    titleLbl.setTextColor(Color.BLACK);
                    card.addView(titleLbl);

                    final EditText titleEt = new EditText(activity);
                    titleEt.setText(tx.title);
                    titleEt.setHint("e.g. Snacks, Petrol, Allowance");
                    titleEt.setHintTextColor(0xFF64748B);
                    titleEt.setTextColor(Color.BLACK);
                    titleEt.setSingleLine(true);
                    titleEt.setPadding(UiUtils.dp(activity, 10), UiUtils.dp(activity, 8), UiUtils.dp(activity, 10), UiUtils.dp(activity, 8));
                    titleEt.setBackground(UiUtils.makeRoundedRectWithStroke(activity, Color.WHITE, 10f, 0xFFCBD5E1, 1f));
                    card.addView(titleEt);

                    card.addView(UiUtils.makeSpace(activity, 1, 8));

                    // Amount Field
                    TextView amtLbl = new TextView(activity);
                    amtLbl.setText("Amount (₹)");
                    amtLbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                    amtLbl.setTypeface(Typeface.DEFAULT_BOLD);
                    amtLbl.setTextColor(Color.BLACK);
                    card.addView(amtLbl);

                    final EditText amountEt = new EditText(activity);
                    amountEt.setText(tx.amount > 0 ? String.valueOf((int) tx.amount) : "");
                    amountEt.setHint("0");
                    amountEt.setHintTextColor(0xFF64748B);
                    amountEt.setTextColor(Color.BLACK);
                    amountEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    amountEt.setSingleLine(true);
                    amountEt.setPadding(UiUtils.dp(activity, 10), UiUtils.dp(activity, 8), UiUtils.dp(activity, 10), UiUtils.dp(activity, 8));
                    amountEt.setBackground(UiUtils.makeRoundedRectWithStroke(activity, Color.WHITE, 10f, 0xFFCBD5E1, 1f));
                    card.addView(amountEt);

                    card.addView(UiUtils.makeSpace(activity, 1, 10));

                    // Category Chips
                    TextView catLbl = new TextView(activity);
                    catLbl.setText("Category");
                    catLbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                    catLbl.setTypeface(Typeface.DEFAULT_BOLD);
                    catLbl.setTextColor(Color.BLACK);
                    card.addView(catLbl);

                    final LinearLayout catRow = new LinearLayout(activity);
                    catRow.setOrientation(LinearLayout.HORIZONTAL);

                    List<CategoryItem> categories = CategoryItem.getCategoriesForType(tx.type);
                    for (final CategoryItem cat : categories) {
                        boolean sel = cat.name.equals(tx.category.name);
                        TextView chip = UiUtils.createHighlightedChip(
                                activity,
                                cat.emoji + " " + cat.label,
                                sel,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        tx.category = cat;
                                        // Update local text fields into tx first
                                        tx.title = titleEt.getText().toString();
                                        try {
                                            tx.amount = Double.parseDouble(amountEt.getText().toString().trim());
                                        } catch (Exception ignored) {
                                        }
                                        renderCardsRef[0].run();
                                    }
                                }
                        );
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        lp.rightMargin = UiUtils.dp(activity, 6);
                        chip.setLayoutParams(lp);
                        catRow.addView(chip);
                    }

                    HorizontalScrollView catScroll = new HorizontalScrollView(activity);
                    catScroll.setHorizontalScrollBarEnabled(false);
                    catScroll.addView(catRow);
                    card.addView(catScroll);

                    // Type toggle callbacks
                    expBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tx.type != TransactionType.EXPENSE) {
                                tx.type = TransactionType.EXPENSE;
                                tx.category = ExpenseCategory.FOOD.toItem();
                                tx.title = titleEt.getText().toString();
                                try {
                                    tx.amount = Double.parseDouble(amountEt.getText().toString().trim());
                                } catch (Exception ignored) {
                                }
                                renderCardsRef[0].run();
                            }
                        }
                    });

                    incBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tx.type != TransactionType.INCOME) {
                                tx.type = TransactionType.INCOME;
                                tx.category = IncomeCategory.ALLOWANCE.toItem();
                                tx.title = titleEt.getText().toString();
                                try {
                                    tx.amount = Double.parseDouble(amountEt.getText().toString().trim());
                                } catch (Exception ignored) {
                                }
                                renderCardsRef[0].run();
                            }
                        }
                    });

                    cardsContainer.addView(card);
                    if (i < itemsToSave.size() - 1) {
                        cardsContainer.addView(UiUtils.makeSpace(activity, 1, 10));
                    }
                }
            }
        };

        renderCardsRef[0].run();

        container.addView(UiUtils.makeSpace(activity, 1, 16));

        // Action Buttons (Re-speak | Save)
        LinearLayout actionsRow = new LinearLayout(activity);
        actionsRow.setOrientation(LinearLayout.HORIZONTAL);

        TextView reSpeakBtn = new TextView(activity);
        reSpeakBtn.setText("🎙️ Speak Again");
        reSpeakBtn.setGravity(Gravity.CENTER);
        reSpeakBtn.setTextColor(Color.BLACK);
        reSpeakBtn.setTypeface(Typeface.DEFAULT_BOLD);
        reSpeakBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        reSpeakBtn.setPadding(UiUtils.dp(activity, 10), UiUtils.dp(activity, 12), UiUtils.dp(activity, 10), UiUtils.dp(activity, 12));
        reSpeakBtn.setBackground(UiUtils.makeRoundedRectWithStroke(activity, Color.WHITE, 12f, 0xFFCBD5E1, 1f));
        reSpeakBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        UiUtils.applyBounceClick(reSpeakBtn, new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                showVoiceListeningDialog(activity, viewModel);
            }
        });
        actionsRow.addView(reSpeakBtn);

        actionsRow.addView(UiUtils.makeSpace(activity, 10, 1));

        TextView saveBtn = new TextView(activity);
        saveBtn.setText("Save Transaction");
        saveBtn.setGravity(Gravity.CENTER);
        saveBtn.setTextColor(Color.WHITE);
        saveBtn.setTypeface(Typeface.DEFAULT_BOLD);
        saveBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        saveBtn.setPadding(UiUtils.dp(activity, 10), UiUtils.dp(activity, 12), UiUtils.dp(activity, 10), UiUtils.dp(activity, 12));
        saveBtn.setBackground(UiUtils.makeRoundedRect(activity, UiUtils.COLOR_PRIMARY_TEAL, 12f));
        saveBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        UiUtils.applyBounceClick(saveBtn, new Runnable() {
            @Override
            public void run() {
                int savedCount = 0;
                for (int i = 0; i < cardsContainer.getChildCount(); i++) {
                    View child = cardsContainer.getChildAt(i);
                    if (child instanceof LinearLayout) {
                        LinearLayout card = (LinearLayout) child;
                        // Retrieve Title & Amount EditTexts
                        EditText titleEt = null;
                        EditText amountEt = null;
                        for (int j = 0; j < card.getChildCount(); j++) {
                            View sub = card.getChildAt(j);
                            if (sub instanceof EditText) {
                                if (titleEt == null) {
                                    titleEt = (EditText) sub;
                                } else if (amountEt == null) {
                                    amountEt = (EditText) sub;
                                }
                            }
                        }

                        if (i < itemsToSave.size()) {
                            ParsedVoiceTransaction tx = itemsToSave.get(i);
                            String finalTitle = titleEt != null ? titleEt.getText().toString().trim() : tx.title;
                            double finalAmount = 0.0;
                            try {
                                if (amountEt != null) {
                                    finalAmount = Double.parseDouble(amountEt.getText().toString().trim());
                                }
                            } catch (Exception ignored) {
                            }

                            if (finalAmount > 0) {
                                viewModel.saveTransaction(
                                        finalTitle.isEmpty() ? tx.category.label : finalTitle,
                                        finalAmount,
                                        tx.type,
                                        tx.category,
                                        "",
                                        tx.paymentMethod,
                                        tx.timestamp
                                );
                                savedCount++;
                            }
                        }
                    }
                }

                if (savedCount > 0) {
                    Toast.makeText(activity, "Saved " + savedCount + " transaction(s) via Voice! 🎙️", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(activity, "Please enter a valid amount to save", Toast.LENGTH_SHORT).show();
                }
            }
        });
        actionsRow.addView(saveBtn);
        container.addView(actionsRow);

        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        int dialogWidth = Math.min(UiUtils.dp(activity, 340), screenWidth - UiUtils.dp(activity, 32));
        dialog.setContentView(scrollRoot, new ViewGroup.LayoutParams(
                dialogWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        dialog.show();
    }
}
