package com.example.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

public class ModuleIconView extends View {

    public static final int TYPE_WALLET = 1;
    public static final int TYPE_ADD_EXPENSE = 2;
    public static final int TYPE_ADD_INCOME = 3;
    public static final int TYPE_SET_GOAL = 4;
    public static final int TYPE_VIEW_REPORTS = 5;

    private final int iconType;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private final Path path = new Path();

    public ModuleIconView(Context context, int iconType) {
        super(context);
        this.iconType = iconType;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        float cx = w / 2f;
        float cy = h / 2f;

        switch (iconType) {
            case TYPE_WALLET:
                drawWallet(canvas, w, h);
                break;
            case TYPE_ADD_EXPENSE:
                drawAddExpense(canvas, cx, cy, Math.min(w, h));
                break;
            case TYPE_ADD_INCOME:
                drawAddIncome(canvas, cx, cy, Math.min(w, h));
                break;
            case TYPE_SET_GOAL:
                drawSetGoal(canvas, cx, cy, Math.min(w, h));
                break;
            case TYPE_VIEW_REPORTS:
                drawViewReports(canvas, cx, cy, Math.min(w, h));
                break;
        }
    }

    private void drawWallet(Canvas canvas, float w, float h) {
        float pad = w * 0.08f;
        // Top tilted bill peeking out of the wallet
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFE2E8F0);
        path.reset();
        path.moveTo(w * 0.24f, h * 0.28f);
        path.lineTo(w * 0.62f, h * 0.14f);
        path.lineTo(w * 0.74f, h * 0.32f);
        path.lineTo(w * 0.24f, h * 0.32f);
        path.close();
        canvas.drawPath(path, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(UiUtils.dp(getContext(), 2f));
        paint.setColor(0xFF0F1E36);
        canvas.drawPath(path, paint);

        // Back flap of wallet
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFF1E3A5F);
        rect.set(pad, h * 0.24f, w - pad * 1.2f, h * 0.86f);
        float r = UiUtils.dp(getContext(), 10f);
        canvas.drawRoundRect(rect, r, r, paint);

        // Main navy body of wallet
        paint.setColor(0xFF15325B);
        rect.set(pad, h * 0.30f, w - pad * 1.2f, h * 0.86f);
        canvas.drawRoundRect(rect, r, r, paint);

        // Navy outline
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(UiUtils.dp(getContext(), 2.2f));
        paint.setColor(0xFF0A1931);
        canvas.drawRoundRect(rect, r, r, paint);

        // Emerald green clasp on right
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFF10B981);
        rect.set(w * 0.60f, h * 0.48f, w - pad * 0.6f, h * 0.68f);
        float cr = UiUtils.dp(getContext(), 7f);
        canvas.drawRoundRect(rect, cr, cr, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(UiUtils.dp(getContext(), 1.8f));
        paint.setColor(0xFF0A1931);
        canvas.drawRoundRect(rect, cr, cr, paint);

        // White clasp button dot
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(w * 0.69f, h * 0.58f, UiUtils.dp(getContext(), 3f), paint);
    }

    private void drawAddExpense(Canvas canvas, float cx, float cy, float size) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFFFE4E6);
        canvas.drawCircle(cx, cy, size / 2f, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFFE11D48);
        paint.setStrokeWidth(UiUtils.dp(getContext(), 2.2f));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        float bw = size * 0.38f;
        float bh = size * 0.42f;
        rect.set(cx - bw / 2f, cy - bh / 2f + size * 0.04f, cx + bw / 2f, cy + bh / 2f + size * 0.04f);
        canvas.drawRoundRect(rect, UiUtils.dp(getContext(), 4f), UiUtils.dp(getContext(), 4f), paint);

        // Top clip / pouch neck
        canvas.drawLine(cx - bw * 0.25f, rect.top - size * 0.06f, cx + bw * 0.25f, rect.top - size * 0.06f, paint);
        canvas.drawLine(cx, rect.top - size * 0.06f, cx, rect.top, paint);

        // Center star/minus details inside pouch
        canvas.drawCircle(cx, cy + size * 0.02f, size * 0.045f, paint);
        canvas.drawLine(cx - bw * 0.24f, cy + size * 0.14f, cx + bw * 0.24f, cy + size * 0.14f, paint);
    }

    private void drawAddIncome(Canvas canvas, float cx, float cy, float size) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFDCFCE7);
        canvas.drawCircle(cx, cy, size / 2f, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFF059669);
        paint.setStrokeWidth(UiUtils.dp(getContext(), 2.2f));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        // Top coin slot
        canvas.drawLine(cx - size * 0.18f, cy - size * 0.18f, cx + size * 0.18f, cy - size * 0.18f, paint);

        // Coin dropping in
        canvas.drawCircle(cx, cy - size * 0.05f, size * 0.09f, paint);

        // Hand / tray receiving below
        path.reset();
        path.moveTo(cx - size * 0.20f, cy + size * 0.12f);
        path.quadTo(cx, cy + size * 0.24f, cx + size * 0.20f, cy + size * 0.10f);
        canvas.drawPath(path, paint);
    }

    private void drawSetGoal(Canvas canvas, float cx, float cy, float size) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFF3E8FF);
        canvas.drawCircle(cx, cy, size / 2f, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFF7C3AED);
        paint.setStrokeWidth(UiUtils.dp(getContext(), 2.2f));
        paint.setStrokeCap(Paint.Cap.ROUND);

        // Outer ring & inner ring
        canvas.drawCircle(cx, cy, size * 0.22f, paint);
        canvas.drawCircle(cx, cy, size * 0.11f, paint);

        // Center bullseye dot
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, size * 0.04f, paint);

        // Arrow dart from top-right into center
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(cx + size * 0.03f, cy - size * 0.03f, cx + size * 0.24f, cy - size * 0.24f, paint);
    }

    private void drawViewReports(Canvas canvas, float cx, float cy, float size) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFDBEAFE);
        canvas.drawCircle(cx, cy, size / 2f, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFF2563EB);
        paint.setStrokeWidth(UiUtils.dp(getContext(), 2.2f));
        paint.setStrokeCap(Paint.Cap.ROUND);

        float baseLineY = cy + size * 0.18f;
        canvas.drawLine(cx - size * 0.22f, baseLineY, cx + size * 0.22f, baseLineY, paint);

        // 3 vertical bars of different heights
        paint.setStyle(Paint.Style.FILL);
        float barW = size * 0.075f;
        float r = UiUtils.dp(getContext(), 2f);

        // Bar 1 (medium)
        rect.set(cx - size * 0.17f, cy - size * 0.04f, cx - size * 0.17f + barW, baseLineY);
        canvas.drawRoundRect(rect, r, r, paint);

        // Bar 2 (tall)
        rect.set(cx - barW / 2f, cy - size * 0.20f, cx + barW / 2f, baseLineY);
        canvas.drawRoundRect(rect, r, r, paint);

        // Bar 3 (medium-tall)
        rect.set(cx + size * 0.17f - barW, cy - size * 0.11f, cx + size * 0.17f, baseLineY);
        canvas.drawRoundRect(rect, r, r, paint);
    }
}
