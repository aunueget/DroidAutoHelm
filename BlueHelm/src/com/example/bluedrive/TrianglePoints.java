package com.example.bluedrive;
import android.graphics.Point;
public class TrianglePoints {
	private Point bigTriangle;
	private Point medTriangle;
	private Point smallTriangle;
	private Point circleEdgeNeg;
	private Point circleEdgePos;
	private Point circle;
	public TrianglePoints(Point big,Point med,Point small,Point circ,Point neg,Point pos){
		bigTriangle=big;
		this.medTriangle=med;
		smallTriangle=small;
		circleEdgeNeg=neg;
		circleEdgePos=pos;
		circle=circ;
	}
	public Point getBigTriangle() {
		return bigTriangle;
	}
	public void setBigTriangle(Point bigTriangle) {
		this.bigTriangle = bigTriangle;
	}
	public Point getSmallTriangle() {
		return smallTriangle;
	}
	public void setSmallTriangle(Point smallTriangle) {
		this.smallTriangle = smallTriangle;
	}
	public Point getCircleEdgeNeg() {
		return circleEdgeNeg;
	}
	public void setCircleEdgeNeg(Point circleEdgeNeg) {
		this.circleEdgeNeg = circleEdgeNeg;
	}
	public Point getCircleEdgePos() {
		return circleEdgePos;
	}
	public void setCircleEdgePos(Point circleEdgePos) {
		this.circleEdgePos = circleEdgePos;
	}
	public Point getMedTriangle() {
		return medTriangle;
	}
	public void setMedTriangle(Point medTriangle) {
		this.medTriangle = medTriangle;
	}
	public Point getCircle() {
		return circle;
	}
	public void setCircle(Point circle) {
		this.circle = circle;
	}

}
