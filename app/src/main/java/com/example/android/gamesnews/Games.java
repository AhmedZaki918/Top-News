package com.example.android.gamesnews;


/**
 * An {@link Games} object contains information related to a game.
 */
public class Games {

    /**
     * Name of the section
     */
    private String mSection;

    /**
     * Name of the author
     */
    private String mAuthor;

    /**
     * web title of the section
     */
    private String mTitle;

    /**
     * Time and date
     */
    private String mTimeAndDate;

    /**
     * Website URL of the game
     */
    private String mUrl;

    /**
     * Constructs a new {@link Games} object.
     *
     * @param section     is the name of the section
     * @param author      is the name of the author
     * @param title       is the title of the website URL
     * @param timeAndDate is current time and date for that event
     * @param url         is the website URL to find more details about that game
     */
    public Games(String section, String author, String timeAndDate, String title, String url) {
        mSection = section;
        mAuthor = author;
        mTitle = title;
        mTimeAndDate = timeAndDate;
        mUrl = url;
    }

    /**
     * Returns the name of that section.
     */
    public String getSection() {
        return mSection;
    }

    /**
     * Returns the author name.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the title of the website.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the time and date related for the newest news.
     */
    public String getTimeAndDate() {
        return mTimeAndDate;
    }

    /**
     * Returns the website URL to find more information about that game.
     */
    public String getUrl() {
        return mUrl;
    }
}