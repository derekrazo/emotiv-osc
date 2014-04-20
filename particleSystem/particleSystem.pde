/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/8213*@* */
/* !do not delete the line above, required for linking your tweak if you upload again */
/* 3D Particle Cloud by tgr8Haus
** Inputs:
**   + zooms in
**   - zooms out
**   arrow keys rotate view              
*/

import oscP5.*;
//import netP5.*;

OscP5 oscP5;
//NetAddress myRemoteLocation;

// number of particles that make up system
int numberParticles = 16;

// Universe object manages collection of particles
Universe myUniverse;

// camera position and target manage your view of the particles
PVector cameraPos, cameraTarget;

private float counter = 0.0;
private float oscIn[] = new float[16];
private float oscInOld[] = new float[16];
private float scalar;

float simulSpeed = 1.0;

/* initial processing state */
void setup() {
  
  
  size(1200, 800, P3D);
  frameRate(60);
  //myRemoteLocation = new NetAddress("192.168.1.253",12000);
  oscP5 = new OscP5(this, 57110);
  

  
  /* particles */
  myUniverse = new Universe(numberParticles);

  // initial position and target for camera
  cameraPos = new PVector(100.0, 0.0, 0.0);
  cameraTarget = myUniverse.getCenterMass();
}

/* changes to processing state */
void draw(){
  // paint background white
  
  /* myRemoteLocation is a NetAddress. a NetAddress takes 2 parameters,
   * an ip address and a port number. myRemoteLocation is used as parameter in
   * oscP5.send() when sending osc packets to another computer, device, 
   * application. usage see below. for testing purposes the listening port
   * and the port of the remote location address are the same, hence you will
   * send messages back to this sketch.
   */
   
  background(255);
  

  // update camera position and target
  camera(cameraPos.x, cameraPos.y, cameraPos.z
    , cameraTarget.x, cameraTarget.y, cameraTarget.z
    , 0.0, 1.0, 0.0);
  
  //simulSpeed = map(oscIn[3], 4150, 4300, .1, 1);
  simulSpeed = map(scalar, .5, 1., .1, 1);
  //println("3: " + oscIn[3]+", speed " + simulSpeed);
  // update universe 
  myUniverse.update();
  //counter+= .01;
}

/* Update camera position
** '+' zooms in
** '-' zooms out  
** arrow keys rotate view
*/
void iterate(){
  // set increments to initial state
  float incrementHorz = 0.0;
  float incrementVert = 0.0;
  
  float addAngle = PI/180;
  float zoomPct = 1.05;
  
  // vector between camera and camera target
  PVector vectCC = PVector.sub(cameraPos, cameraTarget);
  float distance = vectCC.mag();
  float horzAngle = atan2(vectCC.x, vectCC.z);
  float vertAngle = asin(vectCC.y/distance);
  
  // stage changes to camera
  if (keyCode == LEFT) incrementHorz   =  addAngle;
  if (keyCode == RIGHT) incrementHorz  = -addAngle;
  if (keyCode == UP   && vertAngle <  HALF_PI - addAngle) incrementVert =  addAngle;
  if (keyCode == DOWN && vertAngle > -HALF_PI + addAngle) incrementVert = -addAngle;
  if (key == '-' || key == '_') distance *= zoomPct;
  if (key == '+' || key == '=') distance /= zoomPct;
  
  // reposition vector between camera and center of mass
  vectCC.x = distance * cos(vertAngle + incrementVert) * sin(horzAngle + incrementHorz);
  vectCC.y = distance * sin(vertAngle + incrementVert);
  vectCC.z = distance * cos(vertAngle + incrementVert) * cos(horzAngle + incrementHorz);

  // reposition camera to point to center of mass
  cameraPos = PVector.add(cameraTarget, vectCC);
}

void oscEvent(OscMessage m) {
  //println(m.addrPattern());
  //println(m.arguments().length);
  synchronized(this) {
    
    if(m.checkAddrPattern("/eeg/raw/")) {
      for (int i=0;i < m.arguments().length;i++){
         oscInOld[i] = oscIn[i];
         oscIn[i] = new Float(m.arguments()[i].toString());
      }  
    }
    
    
    if(m.checkAddrPattern("/eeg/frustration")) {
      scalar= new Float (m.arguments()[0].toString());
      println(scalar);
    }
    
  }
}




