package com.example.android.gamesnews;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GamesActivity extends AppCompatActivity implements LoaderCallbacks<List<Games>> {


    // a static inner class inside our Activity to use it in GamesAdapter.
    static class ViewHolder {
        public TextView sectionTextView;
        public TextView authorTextView;
        public TextView dateTextView;
        public TextView timeTextView;
        public TextView titleTextView;
    }


    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = GamesActivity.class.getSimpleName();

    private static final String apiKey = BuildConfig.THE_GUARDIAN_API_KEY;
    /**
     * URL for games data from the guardians dataset
     */
    private static final String Guardian_REQUEST_URL =
            "https://content.guardianapis.com/search?&api-key=" + apiKey;


    /**
     * Constant value for the game loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int GAME_LOADER_ID = 1;

    /**
     * Adapter for the list of games
     */
    private GamesAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        // Find a reference to the {@link ListView} in the layout
        ListView gameListView = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        gameListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of games as input
        mAdapter = new GamesAdapter(this, new ArrayList<Games>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        gameListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected game.
        gameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current game that was clicked on
                Games currentGame = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri gameUri = Uri.parse(currentGame.getUrl());

                // Create a new intent to view the game URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, gameUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(GAME_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Games>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        String pageSize = sharedPrefs.getString(
                getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default));

        String sections = sharedPrefs.getString(
                getString(R.string.settings_sections_key),
                getString(R.string.settings_sections_default));


        Uri baseUri = Uri.parse(Guardian_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("section", sections);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", pageSize);

        // Create a new loader for the given URI
        return new GameLoader(this, uriBuilder.toString());


    }

    @Override
    public void onLoadFinished(Loader<List<Games>> loader, List<Games> games) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No games found."
        mEmptyStateTextView.setText(R.string.no_games);

        // Clear the adapter of previous game data
        mAdapter.clear();

        // If there is a valid list of {@link Games}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (games != null && !games.isEmpty()) {
            mAdapter.addAll(games);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Games>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}