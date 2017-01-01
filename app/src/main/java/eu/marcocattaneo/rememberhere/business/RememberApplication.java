package eu.marcocattaneo.rememberhere.business;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import java.util.Date;

import io.fabric.sdk.android.Fabric;
import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class RememberApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(2)
                .migration(migration)
                .name("remember-the-realm")
                .build();

        Realm.setDefaultConfiguration(config);
    }

    public final RealmMigration migration = new RealmMigration() {

        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            RealmSchema schema = realm.getSchema();

            if (oldVersion == 1 && newVersion == 2) {
                schema.get("ProximityPOI")
                        .addField("createDate", Date.class)
                        .addField("updateDate", Date.class);

            }

        }
    };
}
