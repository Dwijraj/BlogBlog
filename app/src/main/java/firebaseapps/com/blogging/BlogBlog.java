package firebaseapps.com.blogging;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by 1405214 on 12-09-2016.
 */
public class BlogBlog extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if(!FirebaseApp.getApps(this).isEmpty()) {

            //This line is writtern so that the data is stored locally and data in fetched only when there is a change if there are no changes
            //the data needn't be fetched from the net it is retrived from locally stored cache
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }


        /*  This is used so that images are stored locally one needn't use their data everytime to fetch old images
            old images are stored locally in the cache this functionality is provided by Picasso
         */
        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

    }
}
