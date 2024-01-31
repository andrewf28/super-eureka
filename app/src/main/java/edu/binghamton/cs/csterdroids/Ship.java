package edu.binghamton.cs.csterdroids;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ship {
    float x, y, width, height;

    public Ship(float initialX, float initialY, float shipWidth, float shipHeight) {
        this.x = initialX;
        this.y = initialY;
        this.width = shipWidth;
        this.height = shipHeight;
    }

    public void update() {
        // Add any specific update logic for the ship if needed
    }

    public void draw(Canvas canvas, Paint paint) {
        // Customize the draw method to represent a house-like ship
        // For example, you can draw a rectangle with a triangle on top to resemble a house
        float roofHeight = height / 2;
        float roofWidth = width / 2;

        // Draw the house-like ship
        canvas.drawRect(x - roofWidth / 2, y - roofHeight, x + roofWidth / 2, y, paint);  // Rectangle as the house
        canvas.drawLine(x - roofWidth / 2, y - roofHeight, x + roofWidth / 2, y - roofHeight, paint);  // Line as the roof

        // You can customize this further based on your desired appearance
    }
}
