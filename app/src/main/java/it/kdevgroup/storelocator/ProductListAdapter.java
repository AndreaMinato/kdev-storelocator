package it.kdevgroup.storelocator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Michele on 04/05/2016.
 */
public class ProductListAdapter extends ArrayAdapter {

    private Context context;
    private boolean useList = true;

    public ProductListAdapter(Context context, List products) {
        super(context, android.R.layout.simple_list_item_1, products);
        this.context = context;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder {
        TextView titleText;
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Product item = (Product) getItem(position);
        View viewToUse = null;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            viewToUse = mInflater.inflate(R.layout.fragment_product, null);
            holder = new ViewHolder();
            holder.titleText = (TextView) viewToUse.findViewById(R.id.titleText);
            viewToUse.setTag(holder);
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }

        holder.titleText.setText(item.getName());
        return viewToUse;
    }
}
