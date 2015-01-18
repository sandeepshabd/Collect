package sandeep.com.collect;

import android.content.Context;

import android.location.Location;
import android.location.LocationManager;

/**
 * Created by sandeepshabd on 12/9/14.
 */
public class GPSGatherData  {



    public static void getLocationData(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if(lastKnownLocation!=null){
            DataClass.gps="\"gps_latitude\":\""+lastKnownLocation.getLongitude()+"\",\"gps_longitude\":\""+lastKnownLocation.getLatitude()+"\",\"gps_altitude\":\""+lastKnownLocation.getAltitude()+
                    "\",\"gps_speed\":\""+lastKnownLocation.getSpeed()+"\",\"gps_bearing\":\""+lastKnownLocation.getBearing()+"\"";
        }



    }
}
