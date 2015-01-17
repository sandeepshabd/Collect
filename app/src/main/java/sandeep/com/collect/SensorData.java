package sandeep.com.collect;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by sandeepshabd on 12/10/14.
 */
public class SensorData  implements SensorEventListener {

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    public void onSensorChanged(SensorEvent event) {

        float[] mGravity= new float[3];
        boolean gravityFlag = false;
        float[] mGeomagnetic= new float[3];
        boolean magneticFlag = false;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            gravityFlag= true;
            mGravity = event.values;
            DataClass.accelerometerData ="{\"x\":"+mGravity[0]+", \"y\":"+mGravity[1]+", \"z\":"+mGravity[2]+"}";
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            magneticFlag= true;
            mGeomagnetic = event.values;
        }

        if (gravityFlag && magneticFlag) {
            float R[] = new float[9];
            float I[] = new float[9];
            try {
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    DataClass.orientationData = "{\"azimut\":"+orientation[0]+",\"pitch\":"+orientation[1]+",\"roll\":"+orientation[2]+"}"; // orientation contains: azimut, pitch and roll
                }
            }catch (Exception e){

            }
        }
    }


}
