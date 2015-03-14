package sandeep.com.collect;

import android.content.Context;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by sandeepshabd on 12/9/14.
 */
public class GPSGatherData  {



    public static void getLocationData(){
        DataClass.gps="\"gps_latitude\":\""+DataClass.latitude+"\",\"gps_longitude\":\""+DataClass.longitude+"\",\"gps_altitude\":\""+DataClass.altitude+
                "\",\"gps_speed\":\""+DataClass.speed+"\",\"gps_bearing\":\""+DataClass.bearing+"\"";

    }


}
