package com.hankyjcheng.colorchooser.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hankyjcheng.colorchooser.listener.ColorValueListener;
import com.hankyjcheng.colorchooser.listener.ColorWheelListener;
import com.hankyjcheng.colorchooser.utility.Utils;

import java.util.HashSet;
import java.util.Set;

public class ColorValueView extends View implements ColorWheelListener {

    private Set<ColorValueListener> listeners = new HashSet<>();
    private Bitmap bitmap;
    private Paint cursorPaint, cursorBorderPaint;
    private int padding;
    private float cursorRadius;
    private float xPos;
    private float[] hsv;
    private Rect srcRect, dstRect;
    private int color = -1;

    public ColorValueView(Context context) {
        super(context);
        init();
    }

    public ColorValueView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorValueView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, 1, Bitmap.Config.ARGB_8888);
        srcRect = new Rect(padding, 0, w - padding, 1);
        dstRect = new Rect(padding, 0, w - padding, h);
        xPos = w - padding;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (color != -1 && bitmap != null) {
            canvas.drawBitmap(bitmap, srcRect, dstRect, null);
            drawCursor(canvas);
        }
    }

    private void drawCursor(Canvas canvas) {
        canvas.drawCircle(xPos, getHeight() / 2, cursorRadius, cursorBorderPaint);
        canvas.drawCircle(xPos, getHeight() / 2, cursorRadius, cursorPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event.getX());
                break;
            case MotionEvent.ACTION_UP:
                onColorFinish();
                break;
        }
        return true;
    }

    private void onTouchMove(float x) {
        if (x < padding) {
            x = padding;
        }
        else if (x > getWidth() - padding) {
            x = getWidth() - padding;
        }
        this.xPos = x;

        float value = (x - padding) / (bitmap.getWidth() - padding * 2);
        hsv[2] = value;
        int color = Color.HSVToColor(hsv);
        onColorChange(color);
        invalidate();
    }

    private void onColorChange(int color) {
        for (ColorValueListener listener : listeners) {
            listener.onColorValueChange(color);
        }
    }

    private void onColorFinish() {
        for (ColorValueListener listener : listeners) {
            listener.onColorValueChangeFinish();
        }
    }

    @Override
    public void onColorWheelChange(int color) {
        if (bitmap != null) {
            this.color = color;
            xPos = getWidth() - padding;
            createBitmap();
            invalidate();
        }
    }

    private void createBitmap() {
        int[] pixels = new int[getWidth()];
        hsv = new float[3];
        Color.colorToHSV(color, hsv);
        float value = 0;
        for (int i = 0; i < getWidth(); i++) {
            value += (1f / getWidth());
            hsv[2] = value;
            pixels[i] = Color.HSVToColor(hsv);
        }
        bitmap.setPixels(pixels, 0, getWidth(), 0, 0, getWidth(), 1);
    }

    @Override
    public void onColorWheelChangeFinish() {

    }

    public void setColor(int color) {
        if (bitmap != null) {
            this.color = color;
            createBitmap();
            Color.colorToHSV(color, hsv);
            xPos = hsv[2] * (bitmap.getWidth() - padding * 2) + padding;
            invalidate();
        }
    }

    public void attachListener(ColorValueListener listener) {
        this.listeners.add(listener);
    }

}