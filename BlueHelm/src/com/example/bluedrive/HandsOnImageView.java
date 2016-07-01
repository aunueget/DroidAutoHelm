package com.example.bluedrive;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class HandsOnImageView extends ImageView {
	public Paint paint;
	private int desiredHeading;
	public RadialTriangle triangles;
	public TrianglePoints currPoints;
	public int triangleLocation[];
	private boolean autoDrive;
	private boolean autoOn;
	private Point centerPoint;
	private boolean touchClicked;
	public Path pathTriangles;

	/**
	 * @param context
	 */

	public HandsOnImageView(Context context) {
		super(context);
        setBackgroundColor(0xFFFFFF);
		paint = new Paint(Paint.LINEAR_TEXT_FLAG);
		desiredHeading = 0;
		triangles = new RadialTriangle(new Point(215, 215));
		currPoints = triangles.getTriangles(270);
		triangleLocation = new int[6];
		triangleLocation[0] = 45;
		triangleLocation[1] = 225;
		triangleLocation[2] = 90;
		triangleLocation[3] = 270;
		triangleLocation[4] = 0;
		triangleLocation[5] = 180;
		desiredHeading = triangleLocation[2];
		autoDrive = false;
		centerPoint = new Point(214, 214);
		touchClicked = false;
		autoOn = false;
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public HandsOnImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
        paint = new Paint(Paint.LINEAR_TEXT_FLAG);
        triangles = new RadialTriangle(new Point(215, 215));
		currPoints = triangles.getTriangles(270);
		desiredHeading = 0;
		triangleLocation = new int[6];
		triangleLocation[0] = 45;
		triangleLocation[1] = 225;
		triangleLocation[2] = 90;
		triangleLocation[3] = 270;
		triangleLocation[4] = 0;
		triangleLocation[5] = 180;
		desiredHeading = triangleLocation[2];
		autoDrive = false;
		centerPoint = new Point(214, 214);
		touchClicked = false;
		autoOn = false;
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public HandsOnImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		paint = new Paint(Paint.LINEAR_TEXT_FLAG);
        triangles = new RadialTriangle(new Point(215, 215));
		currPoints = triangles.getTriangles(270);
		desiredHeading = 0;
		triangleLocation = new int[6];
		triangleLocation[0] = 45;
		triangleLocation[1] = 225;
		triangleLocation[2] = 90;
		triangleLocation[3] = 270;
		triangleLocation[4] = 0;
		triangleLocation[5] = 180;
		desiredHeading = triangleLocation[2];
		autoDrive = false;
		centerPoint = new Point(214, 214);
		touchClicked = false;
		autoOn = false;
	}
	@Override
	protected void onDraw(Canvas canvas) {

		// TODO Auto-generated method stub
		super.onDraw(canvas);
		paintTriangles(canvas);
	}
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		Point touchedPoint = new Point((int) event.getX(), (int) event.getY());
		if (!autoOn
				&& distance(touchedPoint, this.triangles.getTriangle(
						this.triangleLocation[2],
						RadialTriangle.TriangleType.CIRCLE)) < 100) {
			this.triangleLocation[2] = triangles.getAngleToNorth(touchedPoint);
			this.desiredHeading = this.triangleLocation[2];
            this.triangleLocation[3] = triangles
					.getOppisiteAngle(this.triangleLocation[2]);
			this.invalidate();

			return true;
			// Log.d("TouchTest", "Touch down");
		}
		if (action == MotionEvent.ACTION_DOWN) {
			touchClicked = true;
		} else if (action == MotionEvent.ACTION_MOVE) {

		} else if (action == MotionEvent.ACTION_UP) {
			if (touchClicked) {
				// touch press complete, show toast
				if (distance(touchedPoint, centerPoint) < 50) {
					if (autoDrive) {
						autoDrive = false;
					} else {
						autoDrive = true;
					}
					this.invalidate();
				}
				touchClicked = false;
			}
			return true;
		}
		// TODO Auto-generated method stub
		Log.d("Hello Android", "Got a touch event: " + event.getAction());
		// return super.onTouchEvent(event);
		return false;

	}

	public int distance(Point one, Point two) {
		return (int) Math.round(Math.sqrt(((one.x - two.x) * (one.x - two.x))
				+ ((one.y - two.y) * (one.y - two.y))));
	}

	public void paintTriangles(Canvas canvas) {
		paint.setTextSize(46);
		paint.setAntiAlias(true);
		if (autoOn) {
			paint.setColor(android.graphics.Color.argb(150, 240, 225, 17));
			canvas.drawText("A", 203, 232, paint);
		} else {
			paint.setColor(android.graphics.Color.argb(215, 230, 0, 0));
			canvas.drawText("P", 203, 232, paint);
		}
        //set text size for gps/curr/desired number display
        paint.setTextSize(22);
        //set color for borders
        paint.setColor(android.graphics.Color.argb(255,180,90,8));
        canvas.drawRect(0,0,48,78,paint);
        //draw box for gps/curr/desired display area
        paint.setColor(android.graphics.Color.argb(255, 255, 250, 180));
        paint.setStrokeWidth(0);
        canvas.drawRect(2,2,46,26,paint);
        canvas.drawRect(2,28,46,50,paint);
        canvas.drawRect(2,52,46,76,paint);

		paint.setFlags(Paint.ANTI_ALIAS_FLAG);

		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setAntiAlias(true);

		// draw big blue triangle and tail for DIGITAL COMPASS HEADING
		paint.setColor(android.graphics.Color.argb(255, 0, 0, 150));
        canvas.drawText(triangleLocation[0]+"",5,23,paint);

		currPoints = triangles.getTriangles(triangleLocation[0]);
		pathTriangles = new Path();
		pathTriangles.setFillType(Path.FillType.EVEN_ODD);

		pathTriangles.moveTo(currPoints.getCircleEdgeNeg().x,
				currPoints.getCircleEdgeNeg().y);
		pathTriangles.lineTo(currPoints.getCircleEdgePos().x,
				currPoints.getCircleEdgePos().y);
		pathTriangles.lineTo(currPoints.getBigTriangle().x,
				currPoints.getBigTriangle().y);
		pathTriangles.lineTo(currPoints.getCircleEdgeNeg().x,
				currPoints.getCircleEdgeNeg().y);
		pathTriangles.close();
		canvas.drawPath(pathTriangles, paint);

		// tail
		currPoints = triangles.getTriangles(triangleLocation[1]);
		pathTriangles = new Path();
		pathTriangles.setFillType(Path.FillType.EVEN_ODD);
		pathTriangles.moveTo(currPoints.getCircleEdgeNeg().x,
				currPoints.getCircleEdgeNeg().y);
		pathTriangles.lineTo(currPoints.getCircleEdgePos().x,
				currPoints.getCircleEdgePos().y);
		pathTriangles.lineTo(currPoints.getSmallTriangle().x,
				currPoints.getSmallTriangle().y);
		pathTriangles.lineTo(currPoints.getCircleEdgeNeg().x,
				currPoints.getCircleEdgeNeg().y);
		pathTriangles.close();
		canvas.drawPath(pathTriangles, paint);

		// draw medium green triangle and tail for DESIRED HEADING
		paint.setColor(android.graphics.Color.argb(255, 0, 120, 0));
        canvas.drawText(triangleLocation[2]+"",5,48,paint);
		currPoints = triangles.getTriangles(triangleLocation[2]);
		canvas.drawCircle(currPoints.getCircle().x, currPoints.getCircle().y,
				15, paint);
		pathTriangles = new Path();
		pathTriangles.setFillType(Path.FillType.EVEN_ODD);

		pathTriangles.moveTo(currPoints.getCircleEdgeNeg().x,
				currPoints.getCircleEdgeNeg().y);
		pathTriangles.lineTo(currPoints.getCircleEdgePos().x,
				currPoints.getCircleEdgePos().y);
		pathTriangles.lineTo(currPoints.getMedTriangle().x,
				currPoints.getMedTriangle().y);
		pathTriangles.lineTo(currPoints.getCircleEdgeNeg().x,
				currPoints.getCircleEdgeNeg().y);
		pathTriangles.close();
		canvas.drawPath(pathTriangles, paint);
		// tail
		currPoints = triangles.getTriangles(triangleLocation[3]);
		pathTriangles = new Path();
		pathTriangles.setFillType(Path.FillType.EVEN_ODD);

		pathTriangles.moveTo(currPoints.getCircleEdgeNeg().x,
				currPoints.getCircleEdgeNeg().y);
		pathTriangles.lineTo(currPoints.getCircleEdgePos().x,
				currPoints.getCircleEdgePos().y);
		pathTriangles.lineTo(currPoints.getSmallTriangle().x,
				currPoints.getSmallTriangle().y);
		pathTriangles.lineTo(currPoints.getCircleEdgeNeg().x,
				currPoints.getCircleEdgeNeg().y);
		pathTriangles.close();
		canvas.drawPath(pathTriangles, paint);

		// draw small black triangle and tail for GPS
		paint.setColor(android.graphics.Color.BLACK);
        canvas.drawText(triangleLocation[4]+"",5,73,paint);
		currPoints = triangles.getTriangles(triangleLocation[4]);
		pathTriangles = new Path();
		pathTriangles.setFillType(Path.FillType.EVEN_ODD);

		pathTriangles.moveTo(currPoints.getCircleEdgeNeg().x,
				currPoints.getCircleEdgeNeg().y);
		pathTriangles.lineTo(currPoints.getCircleEdgePos().x,
				currPoints.getCircleEdgePos().y);
		pathTriangles.lineTo(currPoints.getSmallTriangle().x,
				currPoints.getSmallTriangle().y);
		pathTriangles.lineTo(currPoints.getCircleEdgeNeg().x,
				currPoints.getCircleEdgeNeg().y);
		pathTriangles.close();
		canvas.drawPath(pathTriangles, paint);

		// tail
		// currPoints = triangles.getTriangles(triangleLocation[5]);
		// pathTriangles = new Path();
		// pathTriangles.setFillType(Path.FillType.EVEN_ODD);
		//
		// pathTriangles.moveTo(currPoints.getCircleEdgeNeg().x,
		// currPoints.getCircleEdgeNeg().y);
		// pathTriangles.lineTo(currPoints.getCircleEdgePos().x,
		// currPoints.getCircleEdgePos().y);
		// pathTriangles.lineTo(currPoints.getSmallTriangle().x,
		// currPoints.getSmallTriangle().y);
		// pathTriangles.lineTo(currPoints.getCircleEdgeNeg().x,
		// currPoints.getCircleEdgeNeg().y);
		// pathTriangles.close();
		// canvas.drawPath(pathTriangles, paint);

	}

	public int getTriangleLocation(int location) {
		return triangleLocation[location];
	}

	public void setTriangleLocation(int location, int degrees) {
		this.triangleLocation[location] = degrees;
	}

	public void setDigitalCompass(int degrees) {
		setTriangleLocation(0, degrees);
		setTriangleLocation(1, this.triangles.getOppisiteAngle(degrees));
    }

	public int getDesiredHeading() {
		return desiredHeading;
	}

	public void setDesiredHeading(int desiredHeading) {
		setTriangleLocation(2, desiredHeading);
		setTriangleLocation(3, desiredHeading);
		this.desiredHeading = desiredHeading;
    }
    public int getGPSCorrectedHeading(){
        int difference=degreeDifferance(false,desiredHeading,getGPSBearing());
        return handleLoop(desiredHeading+difference);
    }
	public boolean isAutoDrive() {
		return autoDrive;
	}

	public void setAutoDrive(boolean autoDrive) {
		this.autoDrive = autoDrive;
	}

	public void setGPSBearing(int bearing) {
		setTriangleLocation(4, bearing);
		// setTriangleLocation(5, this.triangles.getOppisiteAngle(bearing));
	}
    public int getGPSBearing(){
        return triangleLocation[4];
    }

	public boolean isAutoOn() {
		return autoOn;
	}

	public void setAutoOn(boolean autoOn) {
		this.autoOn = autoOn;
	}
    int degreeDifferance(boolean absoluteValue,int value,int minus){
        int compDiff=0;
        boolean accrossN=false;
        compDiff=Math.abs(value-minus);
        if(compDiff>180){
            compDiff=Math.abs(compDiff-360);
            accrossN=true;
        }
        if(absoluteValue || ((value>minus && !accrossN) || (accrossN && value<minus))){
            return compDiff;
        }
        else{
            return (-1*compDiff);
        }
    }
    private int handleLoop(int degrees) {
        if (degrees > 359) {
            degrees -= 360;
        } else if (degrees < 0) {
            degrees += 360;
        }
        return degrees;
    }
}