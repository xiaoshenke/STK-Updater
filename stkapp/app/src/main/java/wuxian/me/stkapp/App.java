package wuxian.me.stkapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by wuxian on 27/10/2018.
 */

public class App extends Application {

    private static Context contextInstance;
    @Override
    public void onCreate() {
        super.onCreate();

        contextInstance = this.getApplicationContext();

        Thread.setDefaultUncaughtExceptionHandler(new CocoExceptionHandler(contextInstance));
    }


    public static Context getContext() {
        return contextInstance;
    }
}
