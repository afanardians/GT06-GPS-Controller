package com.gt06.gpscontroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom 3x3 Pattern Lock View untuk Pengamanan Akses Menu Pengaturan.
 * Mendukung deteksi sentuhan halus, haptic feedback, dan indikator error/sukses.
 */
public class PatternLockView extends View {

    public interface OnPatternListener {
        void onPatternDetected(List<Integer> pattern);
        void onPatternCleared();
    }

    private static final int DOT_COUNT_PER_ROW = 3;
    private static final int TOTAL_DOTS = 9;

    private final Paint dotNormalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final List<Integer> selectedDots = new ArrayList<>();
    private final float[][] dotCenters = new float[TOTAL_DOTS][2];

    private float currentX = -1;
    private float currentY = -1;
    private boolean isDrawing = false;
    private boolean isError = false;

    private float dotRadius = 16f;
    private float hitRadius = 45f;
    private float ringRadius = 36f;

    private OnPatternListener listener;

    public PatternLockView(Context context) {
        super(context);
        init();
    }

    public PatternLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PatternLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        dotRadius = 12f * density;
        hitRadius = 36f * density;
        ringRadius = 30f * density;

        dotNormalPaint.setColor(0xFF94A3B8); // Slate 400
        dotNormalPaint.setStyle(Paint.Style.FILL);

        dotSelectedPaint.setColor(0xFF0284C7); // Sky 600
        dotSelectedPaint.setStyle(Paint.Style.FILL);

        dotRingPaint.setColor(0x400284C7); // Translucent Sky
        dotRingPaint.setStyle(Paint.Style.FILL);

        linePaint.setColor(0xFF0284C7);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(6f * density);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void setOnPatternListener(OnPatternListener listener) {
        this.listener = listener;
    }

    public void clearPattern() {
        selectedDots.clear();
        isDrawing = false;
        isError = false;
        currentX = -1;
        currentY = -1;
        resetPaints();
        invalidate();
        if (listener != null) {
            listener.onPatternCleared();
        }
    }

    public void setPatternError(boolean error) {
        this.isError = error;
        if (error) {
            dotSelectedPaint.setColor(0xFFEF4444); // Red 500
            dotRingPaint.setColor(0x40EF4444);
            linePaint.setColor(0xFFEF4444);
            vibrateError();
        } else {
            resetPaints();
        }
        invalidate();
    }

    private void resetPaints() {
        dotSelectedPaint.setColor(0xFF0284C7);
        dotRingPaint.setColor(0x400284C7);
        linePaint.setColor(0xFF0284C7);
    }

    private void vibrateSuccess() {
        try {
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        } catch (Exception ignored) {}
    }

    private void vibrateError() {
        try {
            Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(120);
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDotPositions(w, h);
    }

    private void calculateDotPositions(int width, int height) {
        int side = Math.min(width, height);
        float padX = (width - side) / 2f;
        float padY = (height - side) / 2f;
        float step = side / (DOT_COUNT_PER_ROW + 1f);

        for (int r = 0; r < DOT_COUNT_PER_ROW; r++) {
            for (int c = 0; c < DOT_COUNT_PER_ROW; c++) {
                int index = r * DOT_COUNT_PER_ROW + c;
                dotCenters[index][0] = padX + (c + 1) * step;
                dotCenters[index][1] = padY + (r + 1) * step;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1. Gambar garis penghubung antar titik yang telah dipilih
        if (selectedDots.size() > 1) {
            for (int i = 0; i < selectedDots.size() - 1; i++) {
                int fromIdx = selectedDots.get(i);
                int toIdx = selectedDots.get(i + 1);
                canvas.drawLine(
                        dotCenters[fromIdx][0], dotCenters[fromIdx][1],
                        dotCenters[toIdx][0], dotCenters[toIdx][1],
                        linePaint
                );
            }
        }

        // 2. Gambar garis dari titik terakhir ke posisi sentuhan jari
        if (isDrawing && !selectedDots.isEmpty() && currentX >= 0 && currentY >= 0) {
            int lastIdx = selectedDots.get(selectedDots.size() - 1);
            canvas.drawLine(
                    dotCenters[lastIdx][0], dotCenters[lastIdx][1],
                    currentX, currentY,
                    linePaint
            );
        }

        // 3. Gambar 9 Titik
        for (int i = 0; i < TOTAL_DOTS; i++) {
            float cx = dotCenters[i][0];
            float cy = dotCenters[i][1];

            if (selectedDots.contains(i)) {
                // Outer glowing ring
                canvas.drawCircle(cx, cy, ringRadius, dotRingPaint);
                // Inner filled dot
                canvas.drawCircle(cx, cy, dotRadius, dotSelectedPaint);
            } else {
                canvas.drawCircle(cx, cy, dotRadius * 0.75f, dotNormalPaint);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                clearPattern();
                int hitIdx = getHitDot(x, y);
                if (hitIdx >= 0) {
                    isDrawing = true;
                    selectedDots.add(hitIdx);
                    currentX = x;
                    currentY = y;
                    vibrateSuccess();
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (isDrawing) {
                    currentX = x;
                    currentY = y;
                    int movedHit = getHitDot(x, y);
                    if (movedHit >= 0 && !selectedDots.contains(movedHit)) {
                        selectedDots.add(movedHit);
                        vibrateSuccess();
                    }
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isDrawing) {
                    isDrawing = false;
                    currentX = -1;
                    currentY = -1;
                    invalidate();
                    if (listener != null) {
                        listener.onPatternDetected(new ArrayList<>(selectedDots));
                    }
                }
                return true;
        }

        return super.onTouchEvent(event);
    }

    private int getHitDot(float x, float y) {
        for (int i = 0; i < TOTAL_DOTS; i++) {
            float dx = x - dotCenters[i][0];
            float dy = y - dotCenters[i][1];
            if (dx * dx + dy * dy <= hitRadius * hitRadius) {
                return i;
            }
        }
        return -1;
    }

    public static String patternToString(List<Integer> pattern) {
        if (pattern == null || pattern.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pattern.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(pattern.get(i));
        }
        return sb.toString();
    }

    public static List<Integer> stringToPattern(String patternStr) {
        List<Integer> list = new ArrayList<>();
        if (patternStr == null || patternStr.trim().isEmpty()) return list;
        String[] parts = patternStr.split(",");
        for (String p : parts) {
            try {
                list.add(Integer.parseInt(p.trim()));
            } catch (Exception ignored) {}
        }
        return list;
    }
}
