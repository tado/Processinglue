package simulation.p5;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import SimpleOpenNI.SimpleOpenNI;

public class AlternativeViewpoint3d extends PApplet {

/* --------------------------------------------------------------------------
 * SimpleOpenNI AlternativeViewpoint3d Test
 * --------------------------------------------------------------------------
 * Processing Wrapper for the OpenNI/Kinect library
 * http://code.google.com/p/simple-openni
 * --------------------------------------------------------------------------
 * prog:  Max Rheiner / Interaction Design / zhdk / http://iad.zhdk.ch/
 * date:  06/11/2011 (m/d/y)
 * ----------------------------------------------------------------------------
 */



SimpleOpenNI context;
float        zoomF =0.3f;
float        rotX = radians(180);  // by default rotate the hole scene 180deg around the x-axis, 
                                   // the data from openni comes upside down
float        rotY = radians(0);

public void setup()
{
  size(1024,768,P3D);

  //context = new SimpleOpenNI(this,SimpleOpenNI.RUN_MODE_SINGLE_THREADED);
  context = new SimpleOpenNI(this);

  // disable mirror
  context.setMirror(false);

  // enable depthMap generation 
  if(context.enableDepth() == false)
  {
     println("Can't open the depthMap, maybe the camera is not connected!"); 
     exit();
     return;
  }

  if(context.enableRGB() == false)
  {
     println("Can't open the rgbMap, maybe the camera is not connected or there is no rgbSensor!"); 
     exit();
     return;
  }
  
  // align depth data to image data
  context.alternativeViewPointDepthToImage();

  stroke(255,255,255);
  smooth();
  perspective(radians(45),
              PApplet.parseFloat(width)/PApplet.parseFloat(height), 
              10,150000);
}

public void draw(){
	  
	background(0,0,0);


	// update the kinect cam
	context.update();
	translate(width/2, height/2, 0);
	rotateX(rotX);
	rotateY(rotY);
	scale(zoomF);

	PImage  rgbImage = context.rgbImage();
	int[]   depthMap = context.depthMap();
	int     steps   = 3;  // to speed up the drawing, draw every third point
	int     index;
	PVector realWorldPoint;
	int   pixelColor;
 
	strokeWeight(steps);

	translate(0,0,-1000);  // set the rotation center of the scene 1000 infront of the camera

	PVector[] realWorldMap = context.depthMapRealWorld();
	for(int y=0;y < context.depthHeight();y+=steps){
		for(int x=0;x < context.depthWidth();x+=steps){
			index = x + y * context.depthWidth();
			if(depthMap[index] > 0){ 
				// get the color of the point
				pixelColor = rgbImage.pixels[index];
				stroke(pixelColor);        
				// draw the projected point
				realWorldPoint = realWorldMap[index];
				point(realWorldPoint.x,realWorldPoint.y,realWorldPoint.z);  // make realworld z negative, in the 3d drawing coordsystem +z points in the direction of the eye
			}
		}
	} 
	// draw the kinect cam
	context.drawCamFrustum();
}


public void keyPressed(){
  switch(key){
  case ' ':
    context.setMirror(!context.mirror());
    break;
  }

  switch(keyCode){
  	case LEFT:
  		rotY += 0.1f;
  		break;
  	case RIGHT:
  		// zoom out
  		rotY -= 0.1f;
  		break;
  	case UP:
  		if(keyEvent.isShiftDown())
  			zoomF += 0.02f;
  		else
  			rotX += 0.1f;
  		break;
  	case DOWN:
  		if(keyEvent.isShiftDown()){
  			zoomF -= 0.02f;
  			if(zoomF < 0.01f)
  				zoomF = 0.01f;
  		}else
  			rotX -= 0.1f;
  		break;
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "AlternativeViewpoint3d" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
