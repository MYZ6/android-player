package com.chenyi.langeasy.list;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.fragment.BookTypeListFragment;
import com.chenyi.langeasy.listener.FragmentExchangeListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by liyzh on 2016/9/10.
 */
public class BooktypeAdapter extends ArrayAdapter<Map<String, Object>> {
    private ArrayList<Map<String, Object>> booktypeLst;
    private ArrayList<Map<String, Object>> mOriginalValues; // Original Values
    private Context mContext;

    public BooktypeAdapter(Context context, ArrayList<Map<String, Object>> booktypeLst) {
        super(context, 0, booktypeLst);

        this.mContext = context;
        this.booktypeLst = booktypeLst;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Map<String, Object> book = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_booktype, parent, false);
        }

        final String booktype = (String) book.get("booktype");
        // Lookup view for data population
        TextView booktypeView = (TextView) convertView.findViewById(R.id.booktype);
        booktypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof FragmentExchangeListener) {
                    ((FragmentExchangeListener) mContext).query("bt:" + booktype);
                }
            }
        });
        TextView vSentenceCount = (TextView) convertView.findViewById(R.id.sentence_count);

        Button bCourses = (Button) convertView.findViewById(R.id.btn_books);
        bCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof BookTypeListFragment.OnItemSelectedListener) {
                    ((BookTypeListFragment.OnItemSelectedListener) mContext).onBooktypeSelected(booktype);
                }
            }
        });
        String booktypeText = "";
        if (book.get("index") != null) {
            booktypeText = ((int) book.get("index") + 1) + "/" + booktype;
        } else {
            booktypeText = (position + 1) + "/" + booktype;
        }
        booktypeView.setText(booktypeText);
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
//                booktypeLst = (ArrayList<Map<String, Object>>) results.values; // has the filtered values

                booktypeLst.clear();
                booktypeLst.addAll((ArrayList<Map<String, Object>>) results.values);
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Map<String, Object>> FilteredArrList = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Map<String, Object>>(booktypeLst); // saves the original data in mOriginalValues
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
