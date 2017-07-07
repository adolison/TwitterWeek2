package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import fragments.UserTimelineFragment;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;
    TextView tvFollowers;
    TextView tvFollowing;
    TextView tvUserName;
    TextView tvTagLine;
    ImageView ivIconImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        client = TwitterApp.getRestClient();

        String screenName = getIntent().getStringExtra("screen_name");
        Long userID = getIntent().getLongExtra("user_ID",0);

        //Toast.makeText(ProfileActivity.this,screenName, Toast.LENGTH_SHORT).show();
        // create the user fragment
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
        // display the user timeline fragment inside the container (dynamic)
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        //make change
        ft.replace(R.id.flContainer,userTimelineFragment);
        //commit
        ft.commit();

        if (screenName == null || userID == 0){
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    User user = null;
                    try {
                        user = User.fromJSON(response);
                        getSupportActionBar().setTitle(user.screenName);
                        populateUserHeader(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("debug", responseString);
                }
            });
        } else{
            client.getOtherUserInfo(screenName, userID, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    User user = null;
                    try {
                        user = User.fromJSON(response);
                        getSupportActionBar().setTitle(user.screenName);
                        populateUserHeader(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });
        }
    }

    public void populateUserHeader (User user) {
       tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvTagLine = (TextView) findViewById(R.id.tvTagLine);
        tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        tvFollowing = (TextView) findViewById(R.id.tvFollowing);


        ivIconImage = (ImageView) findViewById(R.id.ivIconImage);
        tvFollowers.setText("Followers: " + String.valueOf(user.followersCount));
        tvFollowing.setText("Following: " + String.valueOf(user.followingCount));
        tvTagLine.setText(user.tagLine);
        tvUserName.setText(user.name);
        Glide.with(this).load(user.profileImageUrl).into( ivIconImage);
        //});

    }
}
