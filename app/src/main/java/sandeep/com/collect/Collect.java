package sandeep.com.collect;

import android.app.Application;

/**
 * Created by sandeepshabd on 1/23/15.
 */
public class Collect extends Application {
    // uncaught exception handler variable
    private ErrorReporter errorReporter;



    public Collect() {

        // setup handler for uncaught exception
        errorReporter= ErrorReporter.getInstance();
        errorReporter.Init(this);

        Thread.setDefaultUncaughtExceptionHandler(errorReporter);
    }
}
