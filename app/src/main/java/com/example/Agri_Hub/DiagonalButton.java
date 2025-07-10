package com.example.Agri_Hub;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

public class DiagonalButton extends AppCompatButton {

    private Paint flashPaint;
    private float lineProgress = -0.2f; // Start before visible area
    private Handler handler = new Handler();
    private Runnable animationRunnable;

    public DiagonalButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Paint for the moving flashlight effect
        flashPaint = new Paint();
        flashPaint.setStrokeWidth(30f); // Increased thickness
        flashPaint.setStyle(Paint.Style.STROKE);
        flashPaint.setAntiAlias(true);

        // Animation logic
        animationRunnable = new Runnable() {
            @Override
            public void run() {
                lineProgress += 0.03f; // Smooth movement
                if (lineProgress > 1.5f) { // Increased range for more visibility
                    lineProgress = -0.5f; // Start earlier for a smoother transition
                }
                invalidate(); // Redraw the button
                handler.postDelayed(this, 35); // Fast smooth animation
            }
        };
        handler.post(animationRunnable); // Start animation
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Increase the diagonal height
        float startX = lineProgress * width;
        float startY = -height * 0.5f; // Extend above the button
        float endX = startX - (height * 2.0f); // Make the line longer diagonally
        float endY = height * 1.5f; // Extend below the button

        // Create a gradient to simulate the flashlight effect
        LinearGradient gradient = new LinearGradient(
                startX, startY, endX, endY,
                new int[]{0x00FFFFFF, 0xFFFFFFFF, 0x00FFFFFF}, // Fading white glow
                new float[]{0.2f, 0.5f, 0.8f}, // Gradient stops
                Shader.TileMode.CLAMP
        );
        flashPaint.setShader(gradient);

        // Draw the moving diagonal flashlight effect
        canvas.drawLine(startX, startY, endX, endY, flashPaint);
    }
}
