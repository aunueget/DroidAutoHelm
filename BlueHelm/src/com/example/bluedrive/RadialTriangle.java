package com.example.bluedrive;

import android.graphics.Point;
public class RadialTriangle {
	public enum TriangleType { BIG,MED,SMALL,CIRCLE,NEG,POS};
	private final double CIRC = 2 * Math.PI;
	public TrianglePoints compassTriangles[];
	private Point centerPoint;
	private double theta;
	public RadialTriangle(Point centerPoint) {
		this.centerPoint = centerPoint;
		compassTriangles = new TrianglePoints[362];
		generateVectors();
	}

	private void generateVectors() {
		theta = CIRC / 360;

		Point2D big = new Point2D(0, -160);
		Point2D med = new Point2D(0, -110);
		Point2D small = new Point2D(0, -70);
		Point2D neg = new Point2D(-15, -35);
		Point2D pos = new Point2D(15, -35);
		Point2D circle= new Point2D(0,-200);

		for (int i = 0; i < 362; i++) {
			big = this.rotateVector(big, theta);
			med = this.rotateVector(med, theta);
			small = this.rotateVector(small, theta);
			neg = this.rotateVector(neg, theta);
			pos = this.rotateVector(pos, theta);
			circle= this.rotateVector(circle, theta);
			compassTriangles[i] = (new TrianglePoints(
					getPointAsVector(new Point((int) Math.round(big.x),
							(int) Math.round(big.y))),
					getPointAsVector(new Point((int) Math.round(med.x),
							(int) Math.round(med.y))),
					getPointAsVector(new Point((int) Math.round(small.x),
							(int) Math.round(small.y))),
					getPointAsVector(new Point((int) Math.round(circle.x),
									(int) Math.round(circle.y))),						
					getPointAsVector(new Point((int) Math.round(neg.x),
							(int) Math.round(neg.y))),
					getPointAsVector(new Point((int) Math.round(pos.x),
							(int) Math.round(pos.y)))));

		}
	}

	/**
	 * @return the vectors
	 */
	public TrianglePoints[] getVectors() {
		return this.compassTriangles;
	}

	/**
	 * @return the centerPoint
	 */
	public Point getCenterPoint() {
		return centerPoint;
	}

	/**
	 * Rotates a vector counterclockwise by the angle theta specified in
	 * radians.
	 * 
	 * @param vector
	 *            Vector to rotate.
	 * @param theta
	 *            Angle in radians to rotate by.
	 * @return A new vector rotated accordingly.
	 */
	public Point2D rotateVector(Point2D vector, double theta) {
		double newX = (vector.x * Math.cos(theta))
				- (vector.y * Math.sin(theta));
		double newY = (vector.x * Math.sin(theta))
				+ (vector.y * Math.cos(theta));

		return new Point2D(newX, newY);
	}

	public Point getPointAsVector(Point point) {
		int xComp = point.x + centerPoint.x;
		int yComp = point.y + centerPoint.y;
		return new Point(xComp, yComp);
	}

	public TrianglePoints getTriangles(int index) {
		return this.compassTriangles[index];
	}
	public static double magnitude(Point vector) {
		return Math.sqrt(vector.x * vector.x + vector.y * vector.y);
	}
	
	public static double dotProduct(Point v1, Point v2) {
		return v1.x * v2.x + v1.y * v2.y;
	}

	
	/**
	 * Gets the counter-clockwise angle (clockwise with respect to image)
	 * from startVector to endVector
	 * @param startVector
	 * @param endVector
	 * @return
	 */
	public int getAngleToNorth( Point endVector) {
		Point toAnglePoint=new Point(endVector.x-centerPoint.x,endVector.y-centerPoint.y);
		Point north=new Point(0,-1);
		double magV1 = magnitude(north);
		double magV2 = magnitude(toAnglePoint);
		double dotProd = dotProduct(north, toAnglePoint);
		
		double angle = Math.acos(dotProd/(magV1 * magV2));
		
		if( north.x * toAnglePoint.y < north.y * toAnglePoint.x ) {
			   angle = 2 * Math.PI - angle;
		}
		
		return (int) Math.round(Math.toDegrees(angle));
	}
	public int getOppisiteAngle(int degrees){
		return ((degrees+180)%360);
	}
	public Point getTriangle(int degrees,TriangleType triType){
		switch(triType){
		case BIG:
			return this.compassTriangles[degrees].getBigTriangle();

		case SMALL:
			return this.compassTriangles[degrees].getSmallTriangle();

		case MED:
			return this.compassTriangles[degrees].getMedTriangle();
		case CIRCLE:
			return this.compassTriangles[degrees].getCircle();

		case NEG:
			return this.compassTriangles[degrees].getCircleEdgeNeg();

		case POS:
			return this.compassTriangles[degrees].getCircleEdgePos();

		}
		return this.compassTriangles[degrees].getBigTriangle();
	}
	
	public Point getProjectedTriangle(int degrees,TriangleType triType){
		Point projectedPoint=this.getTriangle(degrees, triType);
		projectedPoint=new Point(projectedPoint.x-this.centerPoint.x,projectedPoint.y-this.centerPoint.y);
		Point2D prjPoint=normalize(projectedPoint);
		return new Point((int)Math.round((prjPoint.x*200)+this.centerPoint.x),(int)Math.round((prjPoint.y*200)+this.centerPoint.y));
	}
	public Point2D normalize(Point vector) {
		double norm = magnitude(vector);

		return new Point2D(vector.x / norm, vector.y / norm);
	}

}
