package com.chenyi.langeasy.list;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.chenyi.langeasy.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by liyzh on 2016/9/10.
 */
public class BookAdapter extends ArrayAdapter<Map<String, Object>> {
    private ArrayList<Map<String, Object>> sentenceLst;
    private ArrayList<Map<String, Object>> mOriginalValues; // Original Values

    public BookAdapter(Context context, ArrayList<Map<String, Object>> sentenceLst) {
        super(context, 0, sentenceLst);

        this.sentenceLst = sentenceLst;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Map<String, Object> book = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.booklist_item, parent, false);
        }
        // Lookup view for data population
        TextView booknameView = (TextView) convertView.findViewById(R.id.bookname);
        TextView booktypeView = (TextView) convertView.findViewById(R.id.booktype);
        TextView vSentenceCount = (TextView) convertView.findViewById(R.id.sentence_count);

        // Populate the data into the template view using the data object
        String bookname = (String) book.get("bookname");

        if (book.get("index") != null) {
            bookname = ((int) book.get("index") + 1) + "/" + bookname;
        } else {
            bookname = (position + 1) + "/" + bookname;
        }
        booknameView.setText(bookname);
        booktypeView.setText((String) book.get("booktype"));
        vSentenceCount.setText((Integer) book.get("scount") + "");
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
//                sentenceLst = (ArrayList<Map<String, Object>>) results.values; // has the filtered values

                sentenceLst.clear();
                sentenceLst.addAll((ArrayList<Map<String, Object>>) results.values);
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Map<String, Object>> FilteredArrList = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Map<String, Object>>(sentenceLst); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    String condition = constraint.toString().toLowerCase();
                    int count = 0;
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        Map<String, Object> data = mOriginalValues.get(i);

                        if (condition.startsWith("b:")) {// query by book
                            String bid = condition.substring(2);
                            Log.i("condition", condition);
                            Log.i("bid", bid);
                            String bookid = (String) data.get("bookid");
                            Log.i("bookid", bookid);
                            if (bookid.equals(bid)) {
                                data.put("index", count++);
                                FilteredArrList.add(data);
                            }
                        } else {
                            String wordunique = (String) data.get("wordunique");
                            if (wordunique.indexOf(condition) > -1) {
                                data.put("index", count++);
                                FilteredArrList.add(data);
                            }
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;

                }
                return results;
            }
        };
        return filter;
    }
}
