package fsu.mobile.group1.geohashing;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class MyUser {
    public static final String USER_NAME = "name";
    public static final String LAST_KNOWN_LOCATION = "lastKnownLocation";
    public static final String USER_SCORE = "userScore";                    //Times Won

    private String mUserName;
    private String mLastKnownLocation;
    private int mUserScore;

    public MyUser(){
    }

    public MyUser(String username, String lastKnownLocation, int userScore)
    {
        mUserName = username;
        mLastKnownLocation = lastKnownLocation;
        mUserScore = userScore;
    }

    public MyUser(Map<String, Object> map) {
        mUserName = (String) map.get(USER_NAME);
        mLastKnownLocation = (String) map.get(LAST_KNOWN_LOCATION);
        mUserScore = 0;
    }

    //Setters and getters for our username
    public void setUserName(String username) {
        mUserName = username;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setLastKnownLocation(String lastKnown) {
        mLastKnownLocation = lastKnown;
    }

    public String getLastKnownLocation() {
        return mLastKnownLocation;
    }

    public int getUserScore() {
        return mUserScore;
    }

    public void incrementUserScore()
    {
        mUserScore = mUserScore++;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(USER_NAME, mUserName);
        result.put(LAST_KNOWN_LOCATION, mLastKnownLocation);
        result.put(USER_SCORE, mUserScore);

        return result;
    }

    public static MyUser fromDataSnapshot(DataSnapshot userSnapshot) {
        String username = (String) userSnapshot.child(USER_NAME).getValue();
        String lastKnown = (String) userSnapshot.child(LAST_KNOWN_LOCATION).getValue();
        int score = (int) userSnapshot.child(USER_SCORE).getValue();

        return new MyUser(username, lastKnown, score);
    }
}
