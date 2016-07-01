package test;


import junit.framework.TestCase;

public class AutoHelmTest extends TestCase {

	
	
	public void testHandleLoop(){
		assertEquals(359,handleLoop(-1));
		assertEquals(0,handleLoop(360));
		assertEquals(1,handleLoop(361));
		assertEquals(2,handleLoop(362));
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
