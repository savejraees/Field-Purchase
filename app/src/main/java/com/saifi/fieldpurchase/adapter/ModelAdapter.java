package com.saifi.fieldpurchase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;


import com.saifi.fieldpurchase.R;
import com.saifi.fieldpurchase.model.Model_Model;

import java.util.ArrayList;
import java.util.List;

public class ModelAdapter extends ArrayAdapter<Model_Model> {

    Context context;
    int resource, textViewResourceId;
    List<Model_Model> items, tempItems, suggestions;

    public ModelAdapter(Context context, int resource, int textViewResourceId, List<Model_Model> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<Model_Model>(items); // this makes the difference.
        suggestions = new ArrayList<Model_Model>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_autocomplete, parent, false);
        }
        Model_Model people = items.get(position);
        if (people != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
            if (lblName != null)
                lblName.setText(people.getModelName());
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Model_Model) resultValue).getModelName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Model_Model people : tempItems) {
                    if (people.getModelName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Model_Model> filterList = (ArrayList<Model_Model>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Model_Model people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };
}


