package ca.team2706.scouting.mcmergemanager.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;

/**
 * This is a class defining a custom canvas view that will draw a time-bar graph of when cycles
 * started and finished.
 *
 * Created by mike on 22/01/17.
 */

public class CyclesDisplayView extends SurfaceView {
    private SurfaceHolder holder;


    private static final int PURPLE = Color.rgb(102,51,153);

    private class CycleBar {
        public double startTime;
        public double endTime;
        public int colour;

        public CycleBar(double startTime, double endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }


    private ArrayList<CycleBar> cycleBars = new ArrayList<>();

    public static final double MATCH_DURATION = 135.0;  // s

    /** Constructor **/
    public CyclesDisplayView(Context context) {
        super(context);
        localInit();
    }

    /** Constructor **/
    public CyclesDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        localInit();
    }

    private void localInit() {
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Canvas c = holder.lockCanvas(null);
                onDraw(c);
                holder.unlockCanvasAndPost(c);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }
        });
    }

    /**
     * Add a cycle bar to the bar graph.
     *
     * @param startTime the time-stamp of when the cycle started [0, MATCH_DURATION].
     * @param endTime the time-stamp of when the cycle ended [0, MATCH_DURATION]. If endTime < startTime, then nothing gets drawn.
     * @param success if true, we draw the cycle bar in 2706 purple, if false, we draw the cycle bar in red.
     *                Intended for cycles that were incomplete at the end of the match, but I'm sure we
     *                can find other uses.
     */
    public void addCycle(double startTime, double endTime, boolean success) {
        if (endTime < startTime) return;

        // just so they don't draw weird, clamp them to [0, MATCH_DURATION].
        if (startTime < 0) startTime = 0;
        if (startTime > MATCH_DURATION) startTime = MATCH_DURATION;
        if (endTime < 0) endTime = 0;
        if (endTime > MATCH_DURATION) endTime = MATCH_DURATION;


        // size the bar to the length of the cycle
        CycleBar cycleBar = new CycleBar(startTime, endTime);

        // set colour
        if(success) {
            cycleBar.colour = PURPLE;
        } else {
            cycleBar.colour = Color.RED;
        }

        cycleBars.add(cycleBar);

        // force a re-draw of this view.
        invalidate();
    }

    /**
     * All Views must have this, it gets called by the OS every time the screen redraws.
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Set the background colour
        canvas.drawColor(Color.WHITE);

        for(CycleBar cycleBar : cycleBars) {
            double scalingFactor = this.getWidth() / MATCH_DURATION;

            int startx = (int) (cycleBar.startTime * scalingFactor);
            int endx = (int) (cycleBar.endTime * scalingFactor);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(cycleBar.colour);
            canvas.drawRect(new Rect(startx, 0, endx, this.getHeight()), paint);

            // draw an outline to the box
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            paint.setColor(Color.BLACK);
            canvas.drawRect(new Rect(startx, 0, endx, this.getHeight()), paint);
        }
    }
}
