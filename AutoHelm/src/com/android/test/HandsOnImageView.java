package com.android.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class HandsOnImageView extends ImageView {
	public Paint paint;
	public int desiredHeading;
	public RadialTriangle triangles;
	public TrianglePoints currPoints;
	public int triangleLocation[];

	public Path pathTriangles;

	/**
	 * @param context
	 */

	
	public HandsOnImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
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

	}

	/**
	 * @param context
	 * @param attrs
	 */
	public HandsOnImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
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

	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public HandsOnImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
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

	}

	@Override
	protected void onDraw(Canvas canvas) {

		// TODO Auto-generated method stub
		super.onDraw(canvas);

		paintTriangles(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Point touchedPoint = new Point((int) event.getX(), (int) event.getY());
		if (distance(touchedPoint,
				this.triangles.getTriangle(this.triangleLocation[2],RadialTriangle.TriangleType.CIRCLE)) < 100) {
			this.triangleLocation[2]=triangles.getAngleToNorth(touchedPoint);
			this.triangleLocation[3]=triangles.getOppisiteAngle(this.triangleLocation[2]);
			this.invalidate();
			return true;
			//Log.d("TouchTest", "Touch down");
		}
		// TODO Auto-generated method stub
		Log.d("Hello Android", "Got a touch event: " + event.getAction());
		//return super.onTouchEvent(event);
		return false;

	}

	public int distance(Point one, Point two) {
		return (int) Math.round(Math.sqrt(((one.x - two.x) * (one.x - two.x))
				+ ((one.y - two.y) * (one.y - two.y))));
	}

	public void paintTriangles(Canvas canvas) {

		paint.setFlags(Paint.ANTI_ALIAS_FLAG);

		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setAntiAlias(true);

		// draw big blue triangle and tail for DIGITAL COMPASS HEADING
		paint.setColor(android.graphics.Color.argb(255, 0, 0, 150));
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
		currPoints = triangles.getTriangles(triangleLocation[2]);
		canvas.drawCircle(currPoints.getCircle().x, currPoints.getCircle().y, 15, paint);
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
		currPoints = triangles.getTriangles(triangleLocation[5]);
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

	}

	public int getTriangleLocation(int location) {
		return triangleLocation[location];
	}

	public void setTriangleLocation(int location, int degrees) {
		this.triangleLocation[location] = degrees;
	}

}