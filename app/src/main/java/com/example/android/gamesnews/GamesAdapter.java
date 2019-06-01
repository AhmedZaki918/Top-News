package com.example.android.gamesnews;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * An {@link GamesAdapter} knows how to create a list item layout for each game
 * in the data source (a list of {@link Games} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class GamesAdapter extends ArrayAdapter<Games> {


    private static final String DATE_SEPARATOR = "T";

    /**
     * Constructs a new {@link GamesAdapter}.
     *
     * @param context of the app
     * @param game    is the list of games, which is the data source of the adapter
     */
    public GamesAdapter(Activity context, ArrayList<Games> game) {
        super(context, 0, game);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link Games} object located at this position in the list
        Games currentItem = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID section
        TextView section = convertView.findViewById(R.id.section);
        // Get the section from the current Data object and
        // set this text on that view
        section.setText(currentItem.getSection());

        // Find the TextView in the list_item.xml layout with the ID author
        TextView author = convertView.findViewById(R.id.author);
        // Get the author from the current Data object and
        // set this text on that view
        author.setText(currentItem.getAuthor());

        // Get the original date&time string from the Games object,
        // which can be in the format of "28-3-2018T08.00.15Z".
        String currentDate = currentItem.getTimeAndDate();

        // Split the string into different parts (as an array of Strings)
        // based on the " T " text. We expect an array of 2 Strings, where
        // the first String will be "28-3-2018" and the second String will be "08.00.15".
        String[] dateWithoutTime = currentDate.split(DATE_SEPARATOR);
        // publication Date should be "28-3-2018"
        String publicationDate = dateWithoutTime[0];
        // publication Time should be "08.00.15"
        String publicationTime = dateWithoutTime[1];


        // Find the TextView in the list_item.xml layout with the ID date
        TextView date = convertView.findViewById(R.id.date);
        // Get the date from the current Data object and
        // set this text on that view
        date.setText(publicationDate);

        // Find the TextView in the list_item.xml layout with the ID time
        TextView time = convertView.findViewById(R.id.time);
        // Get the time from the current Data object and
        // set this text on that view and remove last character form time
        time.setText(String.format(publicationTime.substring
                (0, publicationTime.length() - 4)));

        // Find the TextView in the list_item.xml layout with the ID title
        TextView title = convertView.findViewById(R.id.title);
        // Get the title from the current Data object and
        // set this text on that view
        title.setText(currentItem.getTitle());

        // Return the whole list item layout (containing 4 views)
        // so that it can be shown in the ListView
        return convertView;
    }
}