package sandeep.com.collect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by sandeepshabd on 1/19/15.
 */
public class CollectionAlarmReceiver extends BroadcastReceiver {

    private static final String TAG="CollectionAlarmReceiver";
    private static PowerManager.WakeLock wakeLock;

    @Override
    public void onReceive(Context ctx, Intent arg1) {
        Log.e(TAG,"Alarm received");
        acquire(ctx);
        // For our recurring task, we'll just display a message
        //CollectionConstant.TEST_TIME_END = 36;
        Toast.makeText(ctx, ctx.getString(R.string.collectToastMsg), Toast.LENGTH_SHORT).show();
        try {
            ctx.startService(new Intent(ctx, CollectionService.class));
        }catch(Exception e){
            Log.e(TAG,"error in starting service");
        }

    }



    public static void acquire(Context ctx) {
        try {
            if (wakeLock != null) {
                release();
            }

            PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE, "Collect");
            wakeLock.acquire();
        }catch(Exception e){
            Log.e(TAG,"error in acquiring wake lock.");
        }
    }

    public static void release() {
        try {
            if (wakeLock != null) {
                wakeLock.release();
                wakeLock = null;
            }
        }catch(Exception e){
            Log.e(TAG,"error in releasing wake lock.");
        }
    }
}
