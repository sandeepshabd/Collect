package sandeep.com.collect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by sandeepshabd on 1/19/15.
 */
public class CollectionAlarmReceiver extends BroadcastReceiver {

    private static final String TAG="CollectionAlarmReceiver";

    @Override
    public void onReceive(Context ctx, Intent arg1) {
        Log.e(TAG,"Alarm received");
        // For our recurring task, we'll just display a message
        //CollectionConstant.TEST_TIME_END = 36;
        Toast.makeText(ctx, ctx.getString(R.string.collectToastMsg), Toast.LENGTH_SHORT).show();
        ctx.startService(new Intent(ctx, CollectionService.class));

    }
}
