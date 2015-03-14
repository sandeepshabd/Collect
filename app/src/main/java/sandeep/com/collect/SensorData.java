package sandeep.com.collect;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by sandeepshabd on 12/10/14.
 */
public class SensorData  implements SensorEventListener {
    private static final String TAG= "SensorData";
    float[] mGravity= new float[3];
    //boolean gravityFlag = false;
    float[] mGeomagnetic= new float[3];
    // boolean magneticFlag = false;
    float R[] = new float[9];
    float I[] = new float[9];
    float[] orientation = new float[3];

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    public void onSensorChanged(SensorEvent event) {



        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
           // gravityFlag= true;
            for(int i =0; i < 3; i++){
                 mGravity[i] = event.values[i];
            }
            DataClass.accelerometerData ="\"accelerometer_x\":"+mGravity[0]+", \"accelerometer_y\":"+mGravity[1]+", \"accelerometer_z\":"+mGravity[2]+"";
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
           // magneticFlag= true;
            for(int i =0; i < 3; i++){
                mGeomagnetic[i] = event.values[i];
            }

        }

       // if (gravityFlag && magneticFlag) {
        if (true) {
            try {
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    SensorManager.getOrientation(R, orientation);
                    DataClass.orientationData = "\"orientation_azimut\":"+orientation[0]+",\"orientation_pitch\":"+orientation[1]+",\"orientation_roll\":"+orientation[2]; // orientation contains: azimut, pitch and roll
                }
            }catch (Exception e){
                Log.e(TAG,e.getMessage());
            }
        }
    }


}
