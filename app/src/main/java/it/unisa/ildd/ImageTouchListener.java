package it.unisa.ildd;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
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


    @Override
    public boolean onTouch(View v, MotionEvent event) {


            long now = System.currentTimeMillis();
            int originalImageWidth = image.getWidth();
            int originalImageHeight = image.getHeight();
            imgView = (ImageView) v;
            imgWidth = v.getWidth();
            imgHeight = v.getHeight();
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
                if(positions.size() == 0 || (positions.get(positions.size()-1).getX() != eventX && positions.get(positions.size()-1).getY()!=eventY)) {
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

    public ImageTouchListener(Bitmap image, ArrayList<ILDDData> detectedData, LinearAcceleration la, OrientationAPR oAPR, ArrayList<NetworkRecord> nral){
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
    }
}
