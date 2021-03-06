package com.sabdroidex.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.sabdroidex.R;
import com.sabdroidex.data.sickbeard.Show;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageWorker.ImageType;

import java.util.List;

public class ShowsAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private List<Show> mItems;
    private boolean showOverlay;

    public ShowsAdapter(Context context, List<Show> items) {
        this.mItems = items;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setDataSet(List<Show> showElements) {
        this.mItems = showElements;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShowsListItem showItem = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_show, null);
            showItem = new ShowsListItem();
            showItem.banner = (ImageView) convertView.findViewById(R.id.showBanner);
            showItem.overlay = (ImageView) convertView.findViewById(R.id.showOverlay);
        }
        else {
            showItem = (ShowsListItem) convertView.getTag();
        }

        if (showOverlay) {
            if (((GridView) parent).getCheckedItemPosition() == position) {
                showItem.overlay.setImageResource(R.drawable.list_arrow_selected_holo);
                showItem.overlay.setVisibility(View.VISIBLE);
            }
            else {
                showItem.overlay.setVisibility(View.INVISIBLE);
            }
        }

        Show show = (Show) getItem(position);
        String imageKey = ImageType.SHOW_BANNER.name() + show.getTvdbId();
        ImageUtils.getImageWorker().loadImage(showItem.banner, ImageType.SHOW_BANNER, imageKey, show.getTvdbId(), show.getShowName());

        convertView.setId(position);
        convertView.setTag(showItem);
        return (convertView);
    }

    /**
     * This inner class is used to represent the content of a list item.
     */
    class ShowsListItem {

        ImageView banner;
        ImageView overlay;
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void showOverlay(boolean showOverlay) {
        this.showOverlay = showOverlay;
    }
}