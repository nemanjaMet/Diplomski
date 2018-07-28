package elfak.diplomski;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Neca on 14.12.2017..
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

}
