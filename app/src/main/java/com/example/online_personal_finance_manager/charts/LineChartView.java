package com.example.online_personal_finance_manager.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineChartView extends View {

    private Paint linePaint, axisPaint, textPaint, pointPaint;
    private Path incomePath, expensePath;

    private List<Float> incomeData = new ArrayList<>();
    private List<Float> expenseData = new ArrayList<>();
    private List<String> labels = new ArrayList<>();

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Paint for the lines
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(6f);

        // Paint for axis lines
        axisPaint = new Paint();
        axisPaint.setColor(Color.LTGRAY);
        axisPaint.setStrokeWidth(2f);

        // Paint for text labels
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Paint for data points
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStyle(Paint.Style.FILL);

        incomePath = new Path();
        expensePath = new Path();
    }

    public void setData(List<Float> income, List<Float> expense, List<String> labels) {
        this.incomeData = income;
        this.expenseData = expense;
        this.labels = labels;
        invalidate(); // Redraw the view with new data
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (labels.isEmpty() || (incomeData.isEmpty() && expenseData.isEmpty())) {
            canvas.drawText("No data to display", getWidth() / 2f, getHeight() / 2f, textPaint);
            return;
        }

        float padding = 80f;
        float chartWidth = getWidth() - (2 * padding);
        float chartHeight = getHeight() - (2 * padding);

        // Determine max value for scaling
        float maxIncome = incomeData.isEmpty() ? 0 : Collections.max(incomeData);
        float maxExpense = expenseData.isEmpty() ? 0 : Collections.max(expenseData);
        float maxValue = Math.max(maxIncome, maxExpense);
        if (maxValue == 0) maxValue = 10000; // Default max if no data

        // Draw horizontal axis lines (Y-axis grid)
        for (int i = 0; i <= 4; i++) {
            float y = padding + (chartHeight * i / 4f);
            canvas.drawLine(padding, y, padding + chartWidth, y, axisPaint);
            canvas.drawText(String.format("%.0f", maxValue * (1 - (float)i / 4)), padding - 40, y + 10, textPaint);
        }

        // Draw X-axis labels
        float xStep = chartWidth / (labels.size() - 1);
        for (int i = 0; i < labels.size(); i++) {
            float x = padding + (i * xStep);
            canvas.drawText(labels.get(i), x, chartHeight + padding + 40, textPaint);
        }

        // Plot Income data
        if (!incomeData.isEmpty()) {
            incomePath.reset();
            linePaint.setColor(Color.parseColor("#2ECC71")); // Green
            pointPaint.setColor(Color.parseColor("#2ECC71"));
            for (int i = 0; i < incomeData.size(); i++) {
                float x = padding + (i * xStep);
                float y = padding + chartHeight * (1 - (incomeData.get(i) / maxValue));
                if (i == 0) {
                    incomePath.moveTo(x, y);
                } else {
                    incomePath.lineTo(x, y);
                }
                canvas.drawCircle(x, y, 10f, pointPaint);
            }
            canvas.drawPath(incomePath, linePaint);
        }

        // Plot Expense data
        if (!expenseData.isEmpty()) {
            expensePath.reset();
            linePaint.setColor(Color.parseColor("#E74C3C")); // Red
            pointPaint.setColor(Color.parseColor("#E74C3C"));
            for (int i = 0; i < expenseData.size(); i++) {
                float x = padding + (i * xStep);
                float y = padding + chartHeight * (1 - (expenseData.get(i) / maxValue));
                if (i == 0) {
                    expensePath.moveTo(x, y);
                } else {
                    expensePath.lineTo(x, y);
                }
                canvas.drawCircle(x, y, 10f, pointPaint);
            }
            canvas.drawPath(expensePath, linePaint);
        }
    }
}
