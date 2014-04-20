public class Universe{
  /** Properties ****************/
  private ArrayList _item;
  private PVector _centerOfMass;
  private float _mass;
  private float _gravConst;
  private float forceScalar = 10;
  
  /** Methods *******************/
  //retrieve center of mass for system 
  public PVector getCenterMass(){
    return _centerOfMass;
  }
  
  // add particle to system
  public void add(Particle i_particle){
    _item.add(i_particle);
    _mass += i_particle.getMass();
  }
  
  public void setForceScalar(float in) {
    forceScalar = in;
  }
  
  public void setGravitationConstant(float in) {
    _gravConst = in;
  }
  
  // apply acceleration to particles within system
  public void applyForce(){
    for(int i = 0, len_i=_item.size(); i<len_i; i++){
      Particle thisparticle = (Particle) _item.get(i);
      
      for(int j = (i+1), len_j=_item.size(); j<len_j; j++){
        Particle thatparticle = (Particle) _item.get(j);
        
        // get vector between two particles (need better name...)
        PVector partVector = PVector.sub(thisparticle.getLocation(), thatparticle.getLocation());
        partVector.normalize();
        
        // determine force between two particles
        float force = -((_gravConst)*thisparticle.getMass()*thatparticle.getMass())/sq(partVector.mag())*forceScalar;
//        force *= map(oscIn[i], 4000, 4500, .5, 1);  
        // apply acceleration
        thisparticle.addAcceleration(PVector.mult(partVector, force/thisparticle.getMass()));
        thatparticle.addAcceleration(PVector.mult(partVector, -force/thatparticle.getMass()));
      }
    }
  }
  
  // move particles within system
  public void update(){
    float cumeMass = 0.0;
    _centerOfMass.set(0,0,0);

    // apply acceleration to particles within system 
    myUniverse.applyForce();
  
    for(int i = 0, len=_item.size(); i<len; i++){
      Particle myParticle = (Particle) _item.get(i);
      myParticle.move();
      myParticle.paint();

      // update center of mass. equation: Xcm = (M1X1+M2X2)/(M1 + M2)
      _centerOfMass.mult(cumeMass);
      _centerOfMass.add(PVector.mult(myParticle.getLocation(), myParticle.getMass()));
      _centerOfMass.div(cumeMass + myParticle.getMass());

      // add particle mass to cMass
      cumeMass += myParticle.getMass();       
    }
  }
  
  /** Constructors  ****************/
  public Universe(int particles){
    // gravitational constant
    _gravConst = .00005;
    
    // initial center of mass is origin
    _centerOfMass = new PVector(0,0,0);
    
    // create collection of random  particles and arrange within a 'fence'
    _item = new ArrayList();
    float initialFence = 50;
    for(int i=0, len=particles; i<len; i++){
      PVector initPos = new PVector(
            random(-initialFence/2, initialFence/2)
          , random(-initialFence/2, initialFence/2)
          , random(-initialFence/2, initialFence/2)
          );

      this.add(new Particle(random(1,100), initPos));
    }
  } 
}

