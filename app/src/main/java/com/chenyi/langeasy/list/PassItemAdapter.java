package com.chenyi.langeasy.list;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.activity.MainNewActivity;
import com.chenyi.langeasy.listener.FragmentExchangeListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by liyzh on 2016/9/10.
 */
public class PassItemAdapter extends ArrayAdapter<Map<String, Object>> {
    private ArrayList<Map<String, Object>> recordLst;
    public ArrayList<Map<String, Object>> mOriginalValues; // Original Values
    private Context mContext;
    private View listLayout;

    public PassItemAdapter(Context context, View listLayout, ArrayList<Map<String, Object>> recordLst) {
        super(context, 0, recordLst);

        this.mContext = context;
        this.listLayout = listLayout;
        this.recordLst = recordLst;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Map<String, Object> record = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_pass, parent, false);
        }
        final MainNewActivity activity = (MainNewActivity) mContext;
        final Context applicationContext = activity.getApplicationContext();

        // Lookup view for data population
        final Integer wordid = (Integer) record.get("wordid");
        final String word = (String) record.get("word");
        Button bUnpass = (Button) convertView.findViewById(R.id.btn_unpass);
        bUnpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getDBHelper().unpass(wordid);
                Button bRefresh = (Button) listLayout.findViewById(R.id.btn_refresh);
                bRefresh.performClick();
                Toast.makeText(applicationContext, "Unpass [" + word +
                        "] Success!", Toast.LENGTH_SHORT).show();
            }
        });
        String wordText = "";
        String wordUnique = (String) record.get("wordunique");
        if (record.get("index") != null) {
            wordText = ((int) record.get("index") + 1) + "/" + wordUnique;
        } else {
            wordText = (position + 1) + "/" + wordUnique;
        }
        TextView vWord = (TextView) convertView.findViewById(R.id.word);
        vWord.setText(wordText);

        TextView vSentenceCount = (TextView) convertView.findViewById(R.id.sentence_count);
        vSentenceCount.setText((Integer) record.get("scount") + "");
        TextView vBooktype = (TextView) convertView.findViewById(R.id.booktype);
        vBooktype.setText((String) record.get("booktype"));
        TextView vBookname = (TextView) convertView.findViewById(R.id.bookname);
        vBookname.setText((String) record.get("bookname"));

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
//                recordLst = (ArrayList<Map<String, Object>>) results.values; // has the filtered values

                recordLst.clear();
                recordLst.addAll((ArrayList<Map<String, Object>>) results.values);
                notifyDataSetChanged();  // notifies the data with new filtered values


                TextView vSize = (TextView) listLayout.findViewById(R.id.size_val);
                vSize.setText(recordLst.size() + "");
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Map<String, Object>> FilteredArrList = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Map<String, Object>>(recordLst); // saves the original data in mOriginalValues
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

                        if (condition.startsWith("b:")) {// query by record
                            String bid = condition.substring(2);
                            Log.i("condition", condition);
                            Log.i("bid", bid);
                            String bookid = (String) data.get("bookid");
                            Log.i("bookid", bookid);
                            if (bookid.equals(bid)) {
                                data.put("index", count++);
                                FilteredArrList.add(data);
                            }
                        } else if (condition.startsWith("[critical50]")) {// query sentence list that listened-count not less than 50
                            Integer scount = (Integer) data.get("scount");
                            if (scount >= 50) {
                                data.put("index", count++);
                                FilteredArrList.add(data);
                            }
                        } else if (condition.startsWith("[critical30]")) {// query sentence list that listened-count between 30 and 50
                            Integer scount = (Integer) data.get("scount");
                            if (scount >= 30 && scount < 50) {
                                data.put("index", count++);
                                FilteredArrList.add(data);
                            }
                        } else if (condition.startsWith("[critical20]")) {// query sentence list that listened-count between 20 and 30
                            Integer scount = (Integer) data.get("scount");
                            if (scount >= 20 && scount < 30) {
                                data.put("index", count++);
                                FilteredArrList.add(data);
                            }
                        } else if (condition.startsWith("[critical10]")) {// query sentence list that listened-count between 10 and 20
                            Integer scount = (Integer) data.get("scount");
                            if (scount >= 10 && scount < 20) {
                                data.put("index", count++);
                                FilteredArrList.add(data);
                            }
                        } else if (condition.startsWith("[critical0]")) {// query sentence list that listened-count below 10
                            Integer scount = (Integer) data.get("scount");
                            if (scount < 10) {
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
