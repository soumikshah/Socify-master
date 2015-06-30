package extinguishers.com.socify;

import android.app.Application;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by anandsuresh on 6/11/15.
 */
public class App extends Application {

    private static final String TWITTER_KEY = "7Gvysc3OBlCClOx3RFF2RvD9l";
    private static final String TWITTER_SECRET = "FY2ZhuxmbSEDMeAVl60ZhC4ua1WRSL7NgsvF3ELIlT9wpKkECT";

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
    }
}
