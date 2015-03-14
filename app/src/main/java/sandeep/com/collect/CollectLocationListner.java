package sandeep.com.collect;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by sandeepshabd on 3/7/15.
 */
public class CollectLocationListner implements LocationListener {

    final Context ctx;

    public CollectLocationListner(Context context){
         ctx = context;
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(ctx,
                "Provider enabled: " + provider, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(ctx,
                "Provider disabled: " + provider, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Do work with new location. Implementation of this method will be covered later.
        doWorkWithNewLocation(location);
    }

    private void doWorkWithNewLocation(Location location){
        DataClass.speed = location.getSpeed();
        DataClass.latitude=location.getLatitude();
        DataClass.longitude=location.getLatitude();
        DataClass.altitude=location.getAltitude();
        DataClass.bearing =location.getBearing();
    }

}
