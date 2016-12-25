package eu.marcocattaneo.rememberhere.business;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RememberApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .name("remember-the-realm")
                .build();

        Realm.setDefaultConfiguration(config);
    }
}
