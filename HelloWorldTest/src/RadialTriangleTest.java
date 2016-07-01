import android.graphics.Point;

import com.android.test.RadialTriangle;

import junit.framework.TestCase;

public class RadialTriangleTest extends TestCase {

	public void testGetTriangle() {
		RadialTriangle triangles = new RadialTriangle(new Point(0, 0));
		for (int i = 0; i < 360; i++) {
			System.out.println("At degree " + i + " big triangle point: "
					+ triangles.getTriangles(i).getBigTriangle().toString());
		}
		for (int i = 0; i < 360; i++) {
			System.out.println("At degree " + i + " med triangle point: "
					+ triangles.getTriangles(i).getMedTriangle().toString());
		}

		for (int i = 0; i < 360; i++) {
			System.out.println("At degree " + i + " small triangle point: "
					+ triangles.getTriangles(i).getSmallTriangle().toString());
		}

	}
	public void testGetAngleToNorth(){
		RadialTriangle triangles = new RadialTriangle(new Point(0, 0));
		assertEquals(0,triangles.getAngleToNorth(new Point(0,-2)));
		assertEquals(180,triangles.getAngleToNorth(new Point(0,2)));
		assertEquals(135,triangles.getAngleToNorth(new Point(10,10)));
		assertEquals(270,triangles.getAngleToNorth(new Point(-2,0)));
		assertEquals(90,triangles.getAngleToNorth(new Point(2,0)));

	}

}
