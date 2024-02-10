package edu.binghamton.cs.csterdroids;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Path;


import java.util.Random;


public class MainActivity extends AppCompatActivity {

    AsteroidView asteroidView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        asteroidView = new AsteroidView(this);
        setContentView(asteroidView);
    }

    class AsteroidView extends SurfaceView implements Runnable {
        private float spaceshipRotation = 0;
        private boolean rotatingLeft = false;
        private boolean rotatingRight = false;
        Thread gameThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playing;
        boolean paused = false;
        Canvas canvas;
        Paint paint;
        int y;
        int posx, posy;
        int dx, dy;
        int height, width;
        boulder[] b;

        private long thisTimeFrame;
        public AsteroidView(Context context) {
            super(context);

            ourHolder = getHolder();
            paint = new Paint();
            paint.setColor(Color.WHITE); // Set the color of the spaceship
            paint.setStyle(Paint.Style.FILL);
        }

        private Path getTrianglePath(int width, int height) {
            Path path = new Path();
            path.moveTo(width / 2, height / 3); // Top vertex
            path.lineTo((width / 2) - (width / 6), (2 * height) / 3); // Bottom left vertex
            path.lineTo((width / 2) + (width / 6), (2 * height) / 3); // Bottom right vertex
            path.close(); // Close the path to form a triangle
            return path;
        }


        @Override
        public void run() {
            Random r = new Random();
            b = new boulder[5];
            posx = 50;
            posy = 50;
            dx = 20;
            dy = 45;
            for (int i = 0; i < 5; ++i) {
                b[i] = new boulder();
                b[i].x = r.nextInt(50);
                b[i].y = r.nextInt(50);
                b[i].dx = r.nextInt(30) - 15;
                b[i].dy = r.nextInt(30) - 15;
                b[i].diameter = 95;
            }


            while (playing)
            {
                if (!paused) {
                    update();
                }
                draw();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {

                }
            }
        }
        public void update() {
            y = y + 5;
            if (y > 200)
                y = 5;

            posx += dx;
            posy += dy;
            if ((posx > width) || (posx < 0))
                dx = -dx;
            if ((posy > height) || (posy < 0))
                dy = -dy;

            for (int i = 0; i < 5; ++i)
                b[i].update();
            if (rotatingLeft) {
                spaceshipRotation -= 5;
                Log.d("Update", "Rotating Left: " + spaceshipRotation);
            }
            if (rotatingRight) {
                spaceshipRotation += 5;
                Log.d("Update", "Rotating Right: " + spaceshipRotation);
            }

        }
        public void draw() {
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                width = canvas.getWidth();
                height = canvas.getHeight();

                // Draw the background color
                canvas.drawColor(Color.argb(255, 26, 128, 182));
                drawSpaceship(canvas, width, height,spaceshipRotation);
//                Log.d("Draw", "Drawing Spaceship with rotation: " + spaceshipRotation);
                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));
                canvas.drawLine(0, 0, 300, y, paint);


                // canvas.drawCircle(posx, posy, 30l, paint);
                for (int i = 0; i < 5; ++i) {
                    b[i].width = width;
                    b[i].height = height;
                    b[i].draw(canvas, paint);
                }

                // canvas.drawCircle(b.x, b.y, 50, paint);

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }


        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    // Get the touch coordinates
                    float touchX = motionEvent.getX();
                    float touchY = motionEvent.getY();

                    // Calculate the angle
                    float deltaX = touchX - (width / 2); // Assuming spaceship is at the center of the screen

                    // Inverting deltaY calculation
                    float deltaY = touchY - (height / 3); // Adjusted to invert the deltaY

                    // Updated angle calculation
                    spaceshipRotation = (float) Math.toDegrees(Math.atan2(deltaY, deltaX)) + 90; // Add 90 to correct the direction

                    break;
                case MotionEvent.ACTION_UP:
                    // Optionally handle touch release
                    break;
            }
            return true;
        }





    }


    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        asteroidView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        asteroidView.pause();
    }
    private void drawSpaceship(Canvas canvas, int width, int height, float rotation) {
        Paint spaceshipPaint = new Paint();
        spaceshipPaint.setColor(Color.GREEN); // Color for the main body of the spaceship
        spaceshipPaint.setStyle(Paint.Style.FILL);

        Paint tipPaint = new Paint();
        tipPaint.setColor(Color.RED); // Color for the tip of the spaceship
        tipPaint.setStyle(Paint.Style.FILL);

        int centerX = width / 2;
        int centerY = height / 3;

        canvas.save();
        canvas.rotate(rotation, centerX, centerY);

        // Draw the main body of the spaceship
        Path bodyPath = new Path();
        bodyPath.moveTo(centerX, centerY - 30); // Move a bit down from the tip to make the tip noticeable
        bodyPath.lineTo(centerX - 50, centerY + 50);
        bodyPath.lineTo(centerX + 50, centerY + 50);
        bodyPath.close();
        canvas.drawPath(bodyPath, spaceshipPaint);

        // Draw the tip of the spaceship
        Path tipPath = new Path();
        tipPath.moveTo(centerX, centerY - 50); // Tip point
        tipPath.lineTo(centerX - 10, centerY - 30); // Connect to the body - left side
        tipPath.lineTo(centerX + 10, centerY - 30); // Connect to the body - right side
        tipPath.close();
        canvas.drawPath(tipPath, tipPaint);

        canvas.restore();
    }



}

