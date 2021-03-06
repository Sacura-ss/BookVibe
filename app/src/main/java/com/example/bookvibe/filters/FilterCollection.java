package com.example.bookvibe.filters;

import android.widget.Filter;

import com.example.bookvibe.adapters.AdapterCollection;
import com.example.bookvibe.models.ModelCollection;

import java.util.ArrayList;
import java.util.List;

public class FilterCollection extends Filter {
    //arraylist in which we want to search
    private List<ModelCollection> filterList;
    //adapter in which filter need to be implemented
    private AdapterCollection adapterCollection;

    //constructor
    public FilterCollection(List<ModelCollection> filterList, AdapterCollection adapterCollection) {
        this.filterList = filterList;
        this.adapterCollection = adapterCollection;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        //value should not be null and empty
        if (charSequence != null && charSequence.length() > 0) {
            //change to upper case, or lower case to avoid sensitivity
            charSequence = charSequence.toString().toUpperCase();
            List<ModelCollection> filteredModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                //validate
                if(filterList.get(i).getCollection().toUpperCase().contains(charSequence)) {
                    //add to filtered list
                    filteredModels.add(filterList.get(i));
                }

            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        //apply filter changes
        adapterCollection.collectionArrayList = (ArrayList<ModelCollection>)filterResults.values;

        //notify changes
        adapterCollection.notifyDataSetChanged();
    }
}
