package com.example.android.gamesnews.utils;


import android.text.TextUtils;
import android.util.Log;

import com.example.android.gamesnews.Games;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * Helper methods related to requesting and receiving game data from guardians website.
 */
public final class QueryUtils {

    private static final int readTimeout = 10000;
    private static final int connectTimeout = 15000;


    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the the guardians dataset and return a list of {@link Games} objects.
     */
    public static List<Games> fetchGamesData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Games}
        List<Games> game = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Games}
        return game;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(readTimeout /* milliseconds */);
            urlConnection.setConnectTimeout(connectTimeout /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the game JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Games} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Games> extractFeatureFromJson(String gameJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(gameJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding games to
        List<Games> games = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(gameJSON);

            // Extract the JSONObject associated with the key called "response"
            JSONObject gameObject = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results"
            JSONArray gameArray = gameObject.getJSONArray("results");

            // For each game in the gameArray, create an {@link Games} object
            for (int i = 0; i < gameArray.length(); i++) {

                // Get a single game at position i within the list of games
                JSONObject currentGame = gameArray.getJSONObject(i);

                // Extract the value for the key called "sectionName"
                String section = currentGame.optString("sectionName");

                // Extract the value for the key called "webPublicationDate"
                String dateAndTime = currentGame.optString("webPublicationDate");

                // Extract the value for the key called "webTitle"
                String title = currentGame.optString("webTitle");

                // Extract the value for the key called "webUrl"
                String url = currentGame.optString("webUrl");

                // Extract the JSONArray associated with the key called "tags"
                JSONArray tags = currentGame.getJSONArray("tags");
                String webTitle = null;
                // For each game in tags, create an {@link Games} object
                for (int b = 0; b < tags.length(); b++) {

                    // Create JSONobject to loop each array at given position
                    JSONObject currentTag = tags.getJSONObject(b);
                    // Extract the value for the key called "webTitle"
                    webTitle = currentTag.optString("webTitle");
                }

                // Create a new {@link Games} object with the section, article, dateAndTime, title,
                // and url from the JSON response.
                Games game = new Games(section, webTitle, dateAndTime, title, url);

                // Add the new {@link Games} to the list of games.
                games.add(game);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the games JSON results", e);
        }

        // Return the list of games
        return games;
    }
}