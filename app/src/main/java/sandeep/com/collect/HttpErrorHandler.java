package sandeep.com.collect;

import android.util.Log;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created by sandeepshabd on 1/17/15.
 */
public class HttpErrorHandler implements ErrorHandler {

    private final String TAG = "TimeoutErrorHandler";

    @Override
    public Throwable handleError(RetrofitError failureReason) {
        Log.e(TAG, "Error occurred while making call :-( ");
        if(RetrofitError.Kind.NETWORK == failureReason.getKind()) {
            Log.e(TAG, "An link IOException occurred while communicating to the server.");
        }else if(RetrofitError.Kind.HTTP == failureReason.getKind()) {
            Log.e(TAG, "A non-200 HTTP status code was received from the server.");
        }else if(RetrofitError.Kind.HTTP == failureReason.getKind()) {
            Log.e(TAG, "An unexpected error has occurred");
        }
        return failureReason;
    }
}
