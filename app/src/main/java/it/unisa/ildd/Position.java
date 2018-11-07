package it.unisa.ildd;

public class Position {
private float x;
private float y ;
private long timestamp;

public Position() {
	super();
	this.x = 0;
	this.y = 0;
	this.timestamp=0;
}
public long getTimestamp() {
	return timestamp;
}
public void setTimestamp(long timestamp) {
	this.timestamp = timestamp;
}
public float getX() {
	return x;
}
public void setX(float x) {
	this.x = x;
}
public float getY() {
	return y;
}
public void setY(float y) {
	this.y = y;
}



}
