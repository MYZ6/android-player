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
import com.chenyi.langeasy.fragment.PlayListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liyzh on 2016/9/10.
 */
public class SentenceAdapter extends ArrayAdapter<Map<String, Object>> {
    private ArrayList<Map<String, Object>> sentenceLst;
    private ArrayList<Map<String, Object>> mOriginalValues; // Original Values
    private PlayListFragment.AdapterCallback mAdapterCallback;

    public SentenceAdapter(Context context, ArrayList<Map<String, Object>> sentenceLst) {
        super(context, 0, sentenceLst);

        this.sentenceLst = sentenceLst;
    }

    public SentenceAdapter(Context context, PlayListFragment.AdapterCallback adapterCallback, ArrayList<Map<String, Object>> sentenceLst) {
        super(context, 0, sentenceLst);
        this.mAdapterCallback = adapterCallback;

        this.sentenceLst = sentenceLst;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Map<String, Object> sentence = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.playlist_item, parent, false);
        }
        // Lookup view for data population
        TextView songTitle = (TextView) convertView.findViewById(R.id.songTitle);
//        TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);

        // Populate the data into the template view using the data object
        String text = (String) sentence.get("wordunique");

        if (sentence.get("index") != null) {
            text = ((int) sentence.get("index") + 1) + "/" + text;
        } else {
            text = (position + 1) + "/" + text;
        }
        songTitle.setText(text);

        TextView vBooktype = (TextView) convertView.findViewById(R.id.booktype);
        vBooktype.setText((String) sentence.get("booktype"));
        TextView vBookname = (TextView) convertView.findViewById(R.id.bookname);
        vBookname.setText((String) sentence.get("bookname"));
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
                Log.i("sentenceLst size", sentenceLst.size() + "");
                notifyDataSetChanged();  // notifies the data with new filtered values
                if (mAdapterCallback != null) {
                    mAdapterCallback.filterFinished();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Map<String, Object>> FilteredArrList = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Map<String, Object>>(sentenceLst); // saves the original data in mOriginalValues
//                    mOriginalValues = new ArrayList<Map<String, Object>>(); // deep copy list
//                    for(Map<String, Object> sentence: sentenceLst){
//                        Map<String, Object> shallowCopy = new HashMap<String, Object>();
//                        shallowCopy.putAll(sentence);// shallow copy map
//                        mOriginalValues.add(shallowCopy);
//                    }
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

                        if (condition.startsWith("bt:")) {// query by book
                            String bt = condition.substring(3);
//                            Log.i("condition", condition);
//                            Log.i("bt", bt);
                            String booktype = (String) data.get("booktype");
                            booktype = booktype.toLowerCase().trim();
//                            Log.i("booktype", booktype);
                            if (booktype.equals(bt)) {
                                data.put("index", count++);
                                FilteredArrList.add(data);
                            }
                        } else if (condition.startsWith("b:")) {// query by book
                            String bid = condition.substring(2);
//                            Log.i("condition", condition);
//                            Log.i("bid", bid);
                            String bookid = (String) data.get("bookid");
//                            Log.i("bookid", bookid);
                            if (bookid.equals(bid)) {
                                data.put("index", count++);
                                FilteredArrList.add(data);
                            }
                        } else if (condition.startsWith("c:")) {// query by book
                            String cid = condition.substring(2);
//                            Log.i("condition", condition);
//                            Log.i("cid", cid);
                            String courseid = (String) data.get("courseid");
//                            Log.i("courseid", courseid);
                            if (courseid.equals(cid)) {
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
