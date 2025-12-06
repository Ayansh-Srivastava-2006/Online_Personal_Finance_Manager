package com.example.online_personal_finance_manager.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PieChartView extends View {

    private Paint slicePaint, textPaint, holePaint;
    private RectF chartRect = new RectF();
    private List<Float> data = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        slicePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        slicePaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(28f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        holePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        holePaint.setColor(Color.WHITE); // For Donut chart look
    }

    public void setData(List<Float> data, List<String> labels) {
        this.data = data;
        this.labels = labels;
        generateColors();
        invalidate();
    }

    private void generateColors() {
        colors.clear();
        // Use predefined material colors for better aesthetics
        int[] palette = {
            Color.parseColor("#3498DB"), // Blue
            Color.parseColor("#E74C3C"), // Red
            Color.parseColor("#F1C40F"), // Yellow
            Color.parseColor("#2ECC71"), // Green
            Color.parseColor("#9B59B6"), // Purple
            Color.parseColor("#E67E22"), // Orange
            Color.parseColor("#1ABC9C"), // Teal
            Color.parseColor("#34495E")  // Dark Blue
        };
        
        for (int i = 0; i < data.size(); i++) {
            colors.add(palette[i % palette.length]);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data.isEmpty()) {
            canvas.drawText("No data", getWidth() / 2f, getHeight() / 2f, textPaint);
            return;
        }

        float padding = 40f;
        float size = Math.min(getWidth(), getHeight()) - (2 * padding);
        float left = (getWidth() - size) / 2f;
        float top = (getHeight() - size) / 2f;
        
        chartRect.set(left, top, left + size, top + size);

        float total = 0;
        for (float val : data) total += val;

        float startAngle = -90f; // Start from top
        for (int i = 0; i < data.size(); i++) {
            float sweepAngle = (data.get(i) / total) * 360f;
            slicePaint.setColor(colors.get(i));
            canvas.drawArc(chartRect, startAngle, sweepAngle, true, slicePaint);
            
            // Draw labels if slice is big enough
            if (sweepAngle > 15) {
                float angle = (float) Math.toRadians(startAngle + sweepAngle / 2);
                float radius = size / 3f; // Position text
                float x = chartRect.centerX() + radius * (float) Math.cos(angle);
                float y = chartRect.centerY() + radius * (float) Math.sin(angle);
                
                // Optionally draw percentage
                String pct = String.format("%.0f%%", (data.get(i) / total) * 100);
                canvas.drawText(pct, x, y, textPaint);
            }
            
            startAngle += sweepAngle;
        }

        // Draw center hole for Donut effect
        float holeSize = size * 0.5f;
        float holeLeft = (getWidth() - holeSize) / 2f;
        float holeTop = (getHeight() - holeSize) / 2f;
        canvas.drawOval(holeLeft, holeTop, holeLeft + holeSize, holeTop + holeSize, holePaint);
    }
}
