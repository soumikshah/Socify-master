package extinguishers.com.socify;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.AsyncTask;


public class Home extends ActionBarActivity{

    LoginButton loginButton;
    TwitterLoginButton twitterLoginButton;
    CallbackManager callbackManager;


    private List<Post> posts;
    private List<Post> fbPosts;
    private List<Post> twPosts;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.socify);
        posts = new ArrayList<Post>();
        fbPosts = new ArrayList<Post>();
        twPosts = new ArrayList<Post>();
        getFacebook();
        getTwitter();
    }

    private void getTwitter() {
        if (twPosts != null) {
            twPosts.removeAll(twPosts);
        }
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.login_button_twitter);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {

                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                StatusesService statusesService = twitterApiClient.getStatusesService();
                statusesService.homeTimeline(10, null, null, null, null, null, null, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        if (twPosts != null) {
                            twPosts.removeAll(twPosts);
                        }
                        for (Tweet t : listResult.data) {
                            String uname = t.user.name + " @" + t.user.screenName;
                            String text = t.text;
                            String date = dateConvertTwitter(t.createdAt);
                            String proPic = t.user.profileImageUrl;
                            twPosts.add(new Post(proPic, null, uname, null, text, "Twitter", date));
                        }
                        showPosts();
                    }

                    @Override
                    public void failure(TwitterException e) {

                    }
                });
            }

            @Override
            public void failure(TwitterException e) {

            }
        });

    }

    private void getFacebook() {
        if (fbPosts != null) {
            fbPosts.removeAll(fbPosts);
        }
        loginButton = (LoginButton) findViewById(R.id.login_button);
        List<String> permissionNeeds = Arrays.asList("user_posts");
        //Toast.makeText(Home.this, "after success", Toast.LENGTH_LONG).show();
        loginButton.setReadPermissions(permissionNeeds);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Bundle parameters = new Bundle();
                        parameters.putString("limit", "8");
                        GraphRequest request = new GraphRequest(
                                AccessToken.getCurrentAccessToken(),
                                "/me/feed",
                                parameters,
                                HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse response) {
                                        if (fbPosts != null) {
                                            fbPosts.removeAll(fbPosts);
                                        }
                                        try {
                                            JSONObject jsonObject = response.getJSONObject();
                                            JSONArray array = jsonObject.optJSONArray("data");
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject graph = array.getJSONObject(i);
                                                String username = graph.optJSONObject("from").optString("name").toString();
                                                String posterId = graph.optJSONObject("from").optString("id").toString();
                                                String picUrl = "https://graph.facebook.com/" + posterId + "/picture?type=small";
                                                //String picUrl = "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xat1/v/t1.0-1/p50x50/11150477_1124026697614602_979241696682880295_n.jpg?oh=988eb3df585b3cbb54c6005105a38699&oe=55E693A1&__gda__=1441265637_c2702595fc5c789db5700db13e8ed13d";
                                                String story = graph.optString("story").toString();
                                                if (story.equals("")) {
                                                    story = graph.optString("message").toString();
                                                }
                                                String imageUrl = graph.optString("picture").toString();


                                                String time = dateConvertFacebook(graph.optString("created_time").toString());
                                                //Post post = new Post(R.drawable.anand,R.drawable.post, null, null, story, "Facebook", null);
                                                //Toast.makeText(Home.this,story,Toast.LENGTH_LONG).show();
                                                //String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                                                fbPosts.add(new Post(picUrl, imageUrl, username, null, story, "Facebook", time));

                                            }

                                            //v1.setText(msg);
                                        } catch (Exception e) {
                                            //v1.setText(e.getMessage());
                                        }
                                        showPosts();
                                        AccessToken.setCurrentAccessToken(null);
                                    }


                                });
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        //Log.i("Cancel_Tag", "login has been cancelled");
                        Toast.makeText(Home.this, "cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        //Log.i("Error_Tag", "It has an error in login");
                        Toast.makeText(Home.this, "error", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private String dateConvertFacebook(String time) {
        //String times = "155555T1515555+000";      example
        String[] parts1 = time.split("T");
        String d = parts1[0]; // 155555
        String part2 = parts1[1]; // 1515555+000

        String[] parts2 = part2.split("\\+");
        String t = parts2[0]; // 1515555

        String CreateTime = d + "\n" + t;
        return CreateTime;
    }

    private String dateConvertTwitter(String time) {
        //String times = "1555551515555+000";      example
        String[] parts1 = time.split("\\+");
        String d = parts1[0]; // 155555
        String part2 = parts1[1]; //
        part2 = part2.substring(5);


        String CreateTime = d.substring(4,9) + " " + part2 + "\n" + d.substring(10);
        return CreateTime;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode,
                data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        // Connect to account

        switch (item.getItemId()) {

            case R.id.action_FacebookSignIn:
                //Sign in Facebook

                loginButton.callOnClick();

                //showPosts();
                //Toast.makeText(getApplicationContext(), "Sign in Facebook", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_TwitterSignIn:
                //Sign in Twitter

                twitterLoginButton.callOnClick();

                //Toast.makeText(getApplicationContext(), "Sign in Twitter", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_refresh:
                loginButton.callOnClick();
                twitterLoginButton.callOnClick();
                //Refresh posts
                //Retrieve new posts
                //Toast.makeText(getApplicationContext(), "Refreshing posts", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_Close:
                //Close app
                //Retrieve new posts
                Toast.makeText(getApplicationContext(), "Closing App", Toast.LENGTH_SHORT).show();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*
        private void initializePosts(){
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            posts.add(new Post(R.drawable.me,R.drawable.post,"Wael","Title","Body","Facebook", timeStamp));
            posts.add(new Post(R.drawable.anand,R.drawable.post,"Anand","Title","Body","Twitter", timeStamp));
            posts.add(new Post(R.drawable.soumik,R.drawable.post,"Soumik","Title","Body","Flickr", timeStamp));
            posts.add(new Post(R.drawable.chen,R.drawable.post,"Weicheng","Title","Body","Facebook", timeStamp));
            posts.add(new Post(R.drawable.tw,R.drawable.post,"Anand","Title","Body","Twitter", timeStamp));
            posts.add(new Post(R.drawable.tw,R.drawable.post,"Anand","Title","Body","Twitter", timeStamp));
            posts.add(new Post(R.drawable.fl,R.drawable.post,"Soumik","Title","Body","Flickr", timeStamp));
            posts.add(new Post(R.drawable.fb,R.drawable.post,"Weicheng","Title","Body","Facebook", timeStamp));
            posts.add(new Post(R.drawable.fl,R.drawable.post,"Soumik","Title","Body","Flickr", timeStamp));
            posts.add(new Post(R.drawable.fb,R.drawable.post,"Weicheng","Title","Body","Facebook", timeStamp));
        }
    */
    private void sortPosts() {


    }

    private void showPosts() {
        if (posts != null) {
            posts.removeAll(posts);
        }
        if (twPosts != null) {
            for (int j = 0; j < twPosts.size(); j++) {
                posts.add(twPosts.get(j));
            }
        }
        if (fbPosts != null) {
            for (int j = 0; j < fbPosts.size(); j++) {
                posts.add(fbPosts.get(j));
            }

        }
        sortPosts();
        ArrayAdapter<Post> adapter = new MyArrayAdapter();
        final ListView list = (ListView) findViewById(R.id.posts_listView);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            /*PhotoFragment pf = new PhotoFragment();
            Bundle args = new Bundle();
            args.putString("URL", posts.get(position).getPostPhoto());
            pf.setArguments(args);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.photo_frame, new PhotoFragment());
            ft.commit();*/

           /* Intent fragIntent = new Intent(getApplicationContext(), PhotoFragment.class);
            fragIntent.putExtra("URL",posts.get(position).getPostPhoto());
            startActivity(fragIntent);
*/
                //Toast.makeText(getApplicationContext(), "Post Photo Pressed", Toast.LENGTH_SHORT).show();
                //String main = list.getSelectedItem().toString();
            }
        });
    }


    private class MyArrayAdapter extends ArrayAdapter<Post> {
        public MyArrayAdapter() {
            super(Home.this, R.layout.post_item, posts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemview = convertView;
            if (convertView == null) {
                itemview = getLayoutInflater().inflate(R.layout.post_item, parent, false);
            }

            Post curpost;
            if (posts.get(position).isVisible()) {
                curpost = posts.get(position);
            } else {
                return super.getView(position, convertView, parent);
            }

            //user profile photo
            ImageView iv1 = (ImageView) itemview.findViewById(R.id.userProfileImage);
            if (curpost.getUserPhoto() != null && (!curpost.getUserPhoto().equals(""))) {
                new DownloadImageTask(iv1).execute(curpost.getUserPhoto());
            } else {
                iv1.setImageResource(R.drawable.userna);
            }
            //Post photo
            ImageView iv2 = (ImageView) itemview.findViewById(R.id.PostImage);
            if (curpost.getPostPhoto() != null && (!curpost.getPostPhoto().equals(""))) {
                new DownloadImageTask(iv2).execute(curpost.getPostPhoto());
            } else {
                iv2.setVisibility(View.GONE);
            }


            //Post desc
            TextView postDesc = (TextView) itemview.findViewById(R.id.PostDesctextView);

            postDesc.setText(curpost.getPostBody());

            //Post time
            TextView postTime = (TextView) itemview.findViewById(R.id.PostTimetextView);
            postTime.setText(curpost.getPostDatetime());

            //Post user
            TextView postUser = (TextView) itemview.findViewById(R.id.PostInfotextView);
            postUser.setText(curpost.getPostUser());

            TextView postSrc;
            switch (curpost.getPostSource()) {
                case "Facebook":
                    //Post source
                    postSrc = (TextView) itemview.findViewById(R.id.ColortextView);
                    postSrc.setBackgroundResource(R.color.FbColor);
                    break;
                case "Twitter":
                    //Post source
                    postSrc = (TextView) itemview.findViewById(R.id.ColortextView);
                    postSrc.setBackgroundResource(R.color.TwColor);
                    break;
            }

            return itemview;
            // return super.getView(position, convertView, parent);
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                //Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}