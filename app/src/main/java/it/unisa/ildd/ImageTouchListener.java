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

import static android.view.MotionEvent.INVALID_POINTER_ID;

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
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;

    private float mLastTouchX;
    private float mLastTouchY;
    private float mLastGestureX;
    private float mLastGestureY;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mPosX;
    private float mPosY;



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        imgView = (ImageView) v;
        if(MainActivity.zoomMode){
            scaleGestureDetector.onTouchEvent(event);

            final int action = event.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    if (!scaleGestureDetector.isInProgress()) {
                        final float x = event.getX();
                        final float y = event.getY();

                        mLastTouchX = x;
                        mLastTouchY = y;

                        mActivePointerId = event.getPointerId(0);
                    }
                    break;
                }

                case MotionEvent.ACTION_POINTER_DOWN: {
                    if (!scaleGestureDetector.isInProgress()) {
                        final float gx = scaleGestureDetector.getFocusX();
                        final float gy = scaleGestureDetector.getFocusY();

                        mLastGestureX = gx;
                        mLastGestureY = gy;
                    }
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    if (!scaleGestureDetector.isInProgress()) {
                        final int pointerIndex = event.findPointerIndex(mActivePointerId);
                        final float x = event.getX(pointerIndex);
                        final float y = event.getY(pointerIndex);

                        final float dx = x - mLastTouchX;
                        final float dy = y - mLastTouchY;

                        mPosX += dx;
                        mPosY += dy;
                        imgView.setTranslationX(mPosX);
                        imgView.setTranslationY(mPosY);
                        mLastTouchX = x;
                        mLastTouchY = y;
                    } else {
                        final float gx = scaleGestureDetector.getFocusX();
                        final float gy = scaleGestureDetector.getFocusY();

                        final float gdx = gx - mLastGestureX;
                        final float gdy = gy - mLastGestureY;

                        mPosX += gdx;
                        mPosY += gdy;
                        imgView.setTranslationX(mPosX);
                        imgView.setTranslationY(mPosY);
                        mLastGestureX = gx;
                        mLastGestureY = gy;
                    }

                    break;
                }

                case MotionEvent.ACTION_POINTER_UP: {

                    final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;

                        mLastTouchX = event.getX(newPointerIndex);
                        mLastTouchY = event.getY(newPointerIndex);

                        mActivePointerId = event.getPointerId(newPointerIndex);
                    } else {
                        final int tempPointerIndex = event.findPointerIndex(mActivePointerId);

                        mLastTouchX = event.getX(tempPointerIndex);
                        mLastTouchY = event.getY(tempPointerIndex);
                    }

                    break;
                }
            }

        }
        else {



            int originalImageWidth = image.getWidth();
            int originalImageHeight = image.getHeight();

            imgWidth = v.getWidth();
            imgHeight = v.getHeight();

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
        }
        return true;

    }

    private class ScaleListener extends ScaleGestureDetector.
            SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 3.0f));
            imgView.setScaleX(scaleFactor);
            imgView.setScaleY(scaleFactor);
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
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

}
