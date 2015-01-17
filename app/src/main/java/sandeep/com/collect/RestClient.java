package sandeep.com.collect;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by sandeepshabd on 1/17/15.
 */
public class RestClient {

    private static IApi REST_CLIENT;
    private final static String ROOT =   "http://sandeepshabd/data/2.5";
    private final static long timeoutSeconds = 10;

    static {
        setupRestClient();
    }

    private RestClient() {}

    public static IApi get() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {

        OkHttpClient httpClient = new OkHttpClient();
        HttpErrorHandler httpErrorHandler = new HttpErrorHandler();

        httpClient.setConnectTimeout(timeoutSeconds, TimeUnit.SECONDS);
        httpClient.setReadTimeout(timeoutSeconds, TimeUnit.SECONDS);
        httpClient.setWriteTimeout(timeoutSeconds, TimeUnit.SECONDS);

        OkClient okClient =  new OkClient(httpClient);


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(okClient)
                .setErrorHandler(httpErrorHandler)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        REST_CLIENT = restAdapter.create(IApi.class);
    }
}
