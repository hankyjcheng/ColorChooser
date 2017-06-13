package com.hankyjcheng.colorchooser.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hankyjcheng.colorchooser.listener.ColorWheelListener;
import com.hankyjcheng.colorchooser.utility.Utils;

import java.util.HashSet;
import java.util.Set;

public class ColorWheelView extends View {

    private Set<ColorWheelListener> colorWheelListeners = new HashSet<>();
    private Bitmap bitmap;
    private Paint cursorPaint, cursorBorderPaint;
    private int padding;
    private float cursorRadius;
    private float xPos, yPos;

    public ColorWheelView(Context context) {
        super(context);
        init();
    }

    public ColorWheelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        cursorPaint = new Paint();
        cursorPaint.setStyle(Paint.Style.STROKE);
        cursorPaint.setStrokeWidth(Utils.dpToPx(getContext(), 2));
        cursorPaint.setColor(Color.WHITE);
        cursorPaint.setAntiAlias(true);
        cursorBorderPaint = new Paint();
        cursorBorderPaint.setStyle(Paint.Style.STROKE);
        cursorBorderPaint.setAntiAlias(true);
        cursorBorderPaint.setStrokeWidth(Utils.dpToPx(getContext(), 8));
        cursorBorderPaint.setColor(Color.BLACK);
        cursorRadius = Utils.dpToPx(getContext(), 12);
        padding = Utils.dpToPx(getContext(), 16);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, padding, padding, null);
            drawCursor(canvas);
        }
    }

    private void drawCursor(Canvas canvas) {
        canvas.drawCircle(xPos, yPos, cursorRadius, cursorBorderPaint);
        canvas.drawCircle(xPos, yPos, cursorRadius, cursorPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = w - padding * 2;
        int height = h - padding * 2;
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        xPos = getWidth() / 2;
        yPos = getHeight() / 2;
        createBitmap(width, height);
    }

    private void createBitmap(int width, int height) {
        int[] pixels = new int[width * height];
        int circleRadius = Math.min(width, height) / 2;
        float[] hsv = new float[]{0f, 0f, 1f};
        int alpha = 255;
        int x = -circleRadius;
        int y = -circleRadius;
        for (int i = 0; i < pixels.length; i++) {
            if (i % width == 0) {
                x = -circleRadius;
                y++;
            }
            else {
                x++;
            }
            double distance = Math.sqrt(x * x + y * y);
            if (distance <= circleRadius) {
                hsv[0] = (float) (Math.atan2(y, x) / Math.PI * 180f) + 180;
                hsv[1] = (float) (distance / circleRadius);
                pixels[i] = Color.HSVToColor(alpha, hsv);
            }
            else {
                pixels[i] = 0x00000000;
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                onColorFinish();
                break;
        }
        return true;
    }

    private void onTouchMove(float x, float y) {
        int radius = Math.min(getWidth(), getHeight()) / 2;
        double distanceCenter = Math.sqrt(Math.pow(x - getWidth() / 2, 2) + Math.pow(y - getHeight() / 2, 2));
        if (distanceCenter > (radius - padding)) {
            double angle = Math.atan2(y - getHeight() / 2, x - getWidth() / 2);
            x = (float) (Math.cos(angle) * (radius - padding)) + getWidth() / 2;
            y = (float) (Math.sin(angle) * (radius - padding)) + getHeight() / 2;
        }
        this.xPos = x;
        this.yPos = y;

        x -= radius;
        y -= radius;

        double distance = Math.sqrt(x * x + y * y);
        float[] hsv = new float[]{0f, 0f, 1f};
        hsv[0] = (float) (Math.atan2(y, x) / Math.PI * 180f) + 180;
        hsv[1] = Math.max(0f, Math.min(1f, (float) distance / (radius - padding * 2)));
        int color = Color.HSVToColor(hsv);
        onColorChange(color);
        invalidate();
    }

    private void onColorChange(int color) {
        for (ColorWheelListener listener : colorWheelListeners) {
            listener.onColorWheelChange(color);
        }
    }

    private void onColorFinish() {
        for (ColorWheelListener listener : colorWheelListeners) {
            listener.onColorWheelChangeFinish();
        }
    }

    public void setColor(int color) {
        int radius = Math.min(getWidth(), getHeight()) / 2 - padding;
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        double angle = hsv[0] * Math.PI / 180;
        double distance = hsv[1] * radius;
        float x = (float) (-Math.cos(angle) * distance + getWidth() / 2);
        float y = (float) (-Math.sin(angle) * distance + getHeight() / 2);
        this.xPos = x;
        this.yPos = y;
        invalidate();
    }

    public void attachListener(ColorWheelListener listener) {
        this.colorWheelListeners.add(listener);
    }

}