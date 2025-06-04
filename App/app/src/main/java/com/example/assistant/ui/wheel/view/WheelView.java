package com.example.assistant.ui.wheel.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


public class WheelView extends View {
    public static class WheelSector {
        public String name;
        public float points;
        public int color;

        public WheelSector(String name, float points, int color) {
            this.name = name;
            this.points = points;
            this.color = color;
        }
    }

    private List<WheelSector> wheelData = new ArrayList<>();
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public WheelView(Context context) {
        super(context);
        init();
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStrokeWidth(2);

        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(2);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
    }

    public void setWheelData(List<WheelSector> data) {
        this.wheelData = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (wheelData == null || wheelData.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        float centerX = width / 2f;
        float centerY = height / 2f;
        float radius = Math.min(centerX, centerY) - 40;

        float maxPoint = 0;
        for (WheelSector ws : wheelData) {
            if (ws.points > maxPoint) maxPoint = ws.points;
        }

        // Нарисовать внешний круг
        canvas.drawCircle(centerX, centerY, radius, circlePaint);

        // Нарисовать внутренние круги (10 уровней)
        for (int i = 1; i < 10; i++) {
            canvas.drawCircle(centerX, centerY, radius * (1 - i / 10f), circlePaint);
        }

        // Нарисовать точку в центре
        canvas.drawCircle(centerX, centerY, 4, linePaint);

        int n = wheelData.size();
        float anglePerSector = 360f / n;
        float startAngle = -90;  // Начало с верхнего центра (т.е. -90°)

        for (int i = 0; i < n; i++) {
            WheelSector sector = wheelData.get(i);

            // Рисуем линию от центра до края
            float angleRad = (float) Math.toRadians(startAngle + i * anglePerSector);
            float endX = centerX + (float) Math.cos(angleRad) * radius;
            float endY = centerY + (float) Math.sin(angleRad) * radius;
            canvas.drawLine(centerX, centerY, endX, endY, linePaint);

            // Рисуем сектор
            float sectorRadius = radius * (sector.points / maxPoint);
            fillPaint.setColor(sector.color);
            fillPaint.setStyle(Paint.Style.FILL);

            Path sectorPath = new Path();
            sectorPath.moveTo(centerX, centerY);
            sectorPath.arcTo(centerX - sectorRadius, centerY - sectorRadius,
                    centerX + sectorRadius, centerY + sectorRadius,
                    startAngle + i * anglePerSector,
                    anglePerSector, false);
            sectorPath.close();
            canvas.drawPath(sectorPath, fillPaint);

            // Расчет угла для текста
            float textAngle = startAngle + i * anglePerSector + anglePerSector / 2;
            float textRadius = radius + 30;  // Увеличиваем радиус для отступа
            float textX = centerX + (float) Math.cos(Math.toRadians(textAngle)) * textRadius;
            float textY = centerY + (float) Math.sin(Math.toRadians(textAngle)) * textRadius;

            // Измеряем ширину текста для точного выравнивания
            float textWidth = textPaint.measureText(sector.name);
            float halfTextWidth = textWidth / 2f;

            canvas.save();
            canvas.translate(textX, textY);

            // Если угол от 0 до 180° (верхняя половина круга), поворачиваем текст
            if (textAngle >= 0 && textAngle <= 180) {
                canvas.rotate(180);  // Переворачиваем текст для нижней половины
            }

            // Рисуем текст с учетом выравнивания
            canvas.rotate(textAngle + 90);  // Поворачиваем текст по дуге
            canvas.drawText(sector.name, -halfTextWidth, 0, textPaint);  // Рисуем текст с учетом его ширины
            canvas.restore();
        }

        // Последняя линия для закрытия круга
        float lastAngleRad = (float) Math.toRadians(startAngle + n * anglePerSector);
        float lastX = centerX + (float) Math.cos(lastAngleRad) * radius;
        float lastY = centerY + (float) Math.sin(lastAngleRad) * radius;
        canvas.drawLine(centerX, centerY, lastX, lastY, linePaint);
    }


}