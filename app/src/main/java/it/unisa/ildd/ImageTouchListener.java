package it.unisa.ildd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ImageTouchListener implements View.OnTouchListener {

    private Bitmap image;
    private List<Position> positions;
    private ImageView imgView;
    private int imgHeight, imgWidth;
    private float eventX,eventY;
    private ArrayList<ILDDData> dataArray;
    private LinearAcceleration linearAcceleration;
    private OrientationAPR orientationAPR;
    private ArrayList<NetworkRecord> networkRecordArrayList;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;

    private float mPositionX;
    private float mPositionY;
    private float mLastTouchX;
    private float mLastTouchY;

    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerID = INVALID_POINTER_ID;



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        imgView = (ImageView) v;
        int originalImageWidth = image.getWidth();
        int originalImageHeight = image.getHeight();

        imgWidth = v.getWidth();
        imgHeight = v.getHeight();

        if(MainActivity.zoomMode){
            mScaleGestureDetector.onTouchEvent(event);
            final int action = event.getAction();

            switch (action & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN: {

                    //get x and y cords of where we touch the screen
                    final float x = event.getX();
                    final float y = event.getY();

                    //remember where touch event started
                    mLastTouchX = x;
                    mLastTouchY = y;

                    //save the ID of this pointer
                    mActivePointerID = event.getPointerId(0);

                    break;
                }
                case MotionEvent.ACTION_MOVE: {

                    //find the index of the active pointer and fetch its position
                    final int pointerIndex = event.findPointerIndex(mActivePointerID);
                    final float x = event.getX(pointerIndex);
                    final float y = event.getY(pointerIndex);

                    if (!mScaleGestureDetector.isInProgress()) {

                        //calculate the distance in x and y directions
                        final float distanceX = x - mLastTouchX;
                        final float distanceY = y - mLastTouchY;

                        mPositionX += distanceX;
                        mPositionY += distanceY;



                    }
                    //remember this touch position for next move event
                    mLastTouchX = x;
                    mLastTouchY = y;


                    break;
                }

                case MotionEvent.ACTION_UP: {
                    mActivePointerID = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    mActivePointerID = INVALID_POINTER_ID;
                    break;

                }

                case MotionEvent.ACTION_POINTER_UP: {
                    //Extract the index of the pointer that left the screen
                    final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerID) {
                        //Our active pointer is going up Choose another active pointer and adjust
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mLastTouchX = event.getX(newPointerIndex);
                        mLastTouchY = event.getY(newPointerIndex);
                        mActivePointerID = event.getPointerId(newPointerIndex);
                    }
                    break;
                }
            }
            if ((mPositionX * -1) > imgWidth * mScaleFactor - originalImageWidth) {
                mPositionX = (imgWidth * mScaleFactor - originalImageWidth * -1);
            }
            if ((mPositionY * -1) < 0) {
                mPositionY = 0;
            } else if ((mPositionY * -1) > imgHeight * mScaleFactor - originalImageHeight) {
                mPositionY = (imgHeight * mScaleFactor - originalImageHeight) * -1;
            }

            if ((imgHeight * mScaleFactor) < originalImageHeight) {
                mPositionY = 0;
            }

            imgView.setTranslationX(mPositionX);
            imgView.setTranslationY(mPositionY);

            return true;
        }
        else {


            long now = System.currentTimeMillis();

            ILDDData data = new ILDDData();
            data.setCurrentMs(now);


            float aspectRatioView = (float) imgWidth / (float) imgHeight;
            float aspectRatioOriginal = (float) originalImageWidth / (float) originalImageHeight;


            float ratio;

            if (aspectRatioView > aspectRatioOriginal) {
                ratio = (float) imgHeight / (float) originalImageHeight;
                int banda = (int) (imgWidth - originalImageWidth * ratio) / 2;
                eventX = (event.getX() - banda) / ratio;
                eventY = event.getY() / ratio;


            } else {
                ratio = (float) imgWidth / (float) originalImageWidth;
                int banda = (int) (imgHeight - originalImageHeight * ratio) / 2;
                eventY = (event.getY() - banda) / ratio;
                eventX = event.getX() / ratio;
            }

            if (eventX < originalImageWidth && eventX >= 0 && eventY < originalImageHeight && eventY >= 0) {
                if (positions.size() == 0 || (positions.get(positions.size() - 1).getX() != eventX && positions.get(positions.size() - 1).getY() != eventY)) {
                    Position p = new Position();
                    p.setX(eventX);
                    p.setY(eventY);
                    positions.add(p);
                    BitmapFactory.Options myOptions = new BitmapFactory.Options();
                    myOptions.inDither = true;
                    myOptions.inScaled = false;
                    myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important
                    myOptions.inPurgeable = true;
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    data.setPosition(p);
                    LinearAcceleration la = new LinearAcceleration();
                    la.setX(linearAcceleration.getX());
                    la.setY(linearAcceleration.getY());
                    la.setZ(linearAcceleration.getZ());
                    OrientationAPR oAPR = new OrientationAPR();
                    oAPR.setAzimuth(orientationAPR.getAzimuth());
                    oAPR.setRoll(orientationAPR.getRoll());
                    oAPR.setPitch(orientationAPR.getPitch());
                    data.setLinearAcceleration(la);
                    data.setOrientationAPR(oAPR);
                    data.setNetworkRecordArrayList(new ArrayList<>(networkRecordArrayList));
                    dataArray.add(data);

                    Bitmap workingBitmap = Bitmap.createBitmap(image);
                    Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

                    Canvas canvas = new Canvas(mutableBitmap);
                    paint.setColor(Color.BLACK);
                    for (int i = 0; i < positions.size(); i++) {
                        if (i == positions.size() - 1) {
                            paint.setColor(Color.RED);
                        }
                        canvas.drawCircle(positions.get(i).getX(), positions.get(i).getY(), 5, paint);

                    }


                    imgView.setAdjustViewBounds(true);
                    imgView.setImageBitmap(mutableBitmap);
                }
            }

            return true;
        }

    }

    public ImageTouchListener(Context context, Bitmap image, ArrayList<ILDDData> detectedData, LinearAcceleration la, OrientationAPR oAPR, ArrayList<NetworkRecord> nral){
        this.image = image;
        this.dataArray = detectedData;
        this.linearAcceleration = la;
        this.orientationAPR = oAPR;
        this.networkRecordArrayList = nral;
        positions= new ArrayList<Position>();
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inDither = true;
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important
        myOptions.inPurgeable = true;
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){

            mScaleFactor *= scaleGestureDetector.getScaleFactor();

            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            imgView.setScaleX(mScaleFactor);

            imgView.setScaleY(mScaleFactor);

            return true;

        }
    }
}
