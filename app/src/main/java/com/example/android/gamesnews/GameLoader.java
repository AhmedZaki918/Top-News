package com.example.android.gamesnews;


import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of games by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class GameLoader extends AsyncTaskLoader<List<Games>> {

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link GameLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public GameLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Games> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of games.
        List<Games> game = QueryUtils.fetchGamesData(mUrl);
        return game;
    }
}