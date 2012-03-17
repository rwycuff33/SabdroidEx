package com.sabdroidex.adapters;

import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.sickbeard.SickBeardController;
import com.sabdroidex.utils.AsyncImage;

public class ComingListRowAdapter extends ArrayAdapter<Object[]> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final ArrayList<Object[]> rows;
    private final Vector<Bitmap> mListBanners;
    private final Vector<AsyncImage> mAsyncImages;
    private final Bitmap mEmptyBanner;
    private ShowsListItem mQueueListItem;

    public ComingListRowAdapter(Context context, ArrayList<Object[]> rows) {
        super(context, R.layout.coming_item, rows);
        this.mContext = context;
        this.rows = rows;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mListBanners = new Vector<Bitmap>();
        this.mAsyncImages = new Vector<AsyncImage>();
        this.mEmptyBanner = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.temp_banner);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.coming_item, null);
            mQueueListItem = new ShowsListItem();
            mQueueListItem.title = (TextView) convertView.findViewById(R.id.coming_show_name);
            mQueueListItem.banner = (ImageView) convertView.findViewById(R.id.coming_show_banner);
            mQueueListItem.next_ = (TextView) convertView.findViewById(R.id.coming_next_episode_);
            mQueueListItem.next = (TextView) convertView.findViewById(R.id.coming_next_episode);
            mQueueListItem.airs_ = (TextView) convertView.findViewById(R.id.coming_airs_);
            mQueueListItem.airs = (TextView) convertView.findViewById(R.id.coming_airs);
        }
        else {
            mQueueListItem = (ShowsListItem) convertView.getTag();
        }

        this.mListBanners.setSize(rows.size());
        this.mAsyncImages.setSize(rows.size());

        /**
         * If the size is 1, this means this is a time descriptor
         */
        if (rows.get(position).length == 1) {
            convertView.setPadding(1, 0, 1, 0);
            mQueueListItem.banner.setVisibility(View.GONE);
            mQueueListItem.next_.setVisibility(View.GONE);
            mQueueListItem.next.setVisibility(View.GONE);
            mQueueListItem.airs_.setVisibility(View.GONE);
            mQueueListItem.airs.setVisibility(View.GONE);

            mQueueListItem.title.setTextColor(Color.BLACK);
            mQueueListItem.title.setBackgroundColor(Color.rgb(156, 181, 207));
            mQueueListItem.title.setGravity(Gravity.CENTER);
            mQueueListItem.title.setText(rows.get(position)[0] + " ");
        }
        else {
            convertView.setPadding(10, 4, 10, 0);
            mQueueListItem.banner.setVisibility(View.VISIBLE);
            mQueueListItem.next_.setVisibility(View.VISIBLE);
            mQueueListItem.next.setVisibility(View.VISIBLE);
            mQueueListItem.airs_.setVisibility(View.VISIBLE);
            mQueueListItem.airs.setVisibility(View.VISIBLE);

            if (rows.size() != 0 && mAsyncImages.size() != rows.size()) {
                this.mAsyncImages.clear();
                this.mAsyncImages.setSize(rows.size());
            }

            if (mListBanners.get(position) == null) {
                mQueueListItem.banner.setImageBitmap(mEmptyBanner);

                if (mAsyncImages.get(position) == null) {
                    mAsyncImages.add(position, new AsyncImage());
                }

                if (mAsyncImages.get(position).getStatus() != Status.FINISHED && mAsyncImages.get(position).getStatus() != Status.RUNNING) {
                    mAsyncImages.get(position).execute(mContext, handler, rows.get(position)[1], rows.get(position)[2],
                            SickBeardController.MESSAGE.SHOW_GETBANNER, position);
                }
            }
            else {
                mQueueListItem.banner.setImageBitmap(mListBanners.get(position));
            }

            mQueueListItem.title.setTextColor(Color.WHITE);
            mQueueListItem.title.setBackgroundColor(Color.rgb(128, 128, 128));
            mQueueListItem.title.setGravity(Gravity.LEFT);
            mQueueListItem.title.setText(rows.get(position)[2] + " ");

            String next = rows.get(position)[3] + "x" + rows.get(position)[4] + " - " + rows.get(position)[5] + " (" + rows.get(position)[6] + ")";
            String airs = rows.get(position)[7] + " " + rows.get(position)[8] + " [" + rows.get(position)[9] + "]";

            mQueueListItem.next.setText(next);
            mQueueListItem.airs.setText(airs);
        }
        convertView.setId(position);
        convertView.setTag(mQueueListItem);
        return (convertView);
    }

    /**
     * This inner class is used to represent the content of a list item.
     */
    class ShowsListItem {

        TextView title;
        ImageView banner;
        TextView next_;
        TextView next;
        TextView airs_;
        TextView airs;
    }

    /**
     * This handler will receive the messages from the background worker
     */
    private final Handler handler = new Handler() {

        /**
         * This method will handle the messages sent to this handler by the background worker
         */
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            if (bitmap != null && mListBanners != null && mListBanners.size() > msg.what) {
                mListBanners.set(msg.what, bitmap);
                notifyDataSetChanged();
            }
            else {
                Toast.makeText(getContext(), R.string.no_poster + " : " + rows.get(msg.what)[0], Toast.LENGTH_LONG);
            }
        }
    };

    /**
     * Clearing the {@link Bitmap} list
     */
    public void clearBitmaps() {
        if (mListBanners != null) {
            mListBanners.clear();
        }
        System.gc();
    }
}