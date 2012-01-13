package com.sabdroidex.fragments;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sabdroidex.R;
import com.sabdroidex.activity.SABDroidEx;
import com.sabdroidex.activity.adapters.HistoryListRowAdapter;
import com.sabdroidex.sabnzbd.SABnzbdController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDFragment;
import com.sabdroidex.utils.SABDroidConstants;

public class HistoryFragment extends SABDFragment {

    private static JSONObject backupJsonObject;

    private static ArrayList<String> rows;
    final static int DIALOG_SETUP_PROMPT = 999;
    private ListView listView;

    // Instantiating the Handler associated with the main thread.
    private Handler messageHandler = new Handler() {

        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SABnzbdController.MESSAGE_UPDATE_HISTORY:

                    Object result[] = (Object[]) msg.obj;
                    // Updating rows
                    rows.clear();
                    rows.addAll((ArrayList<String>) result[1]);

                    ArrayAdapter<String> adapter = extracted(listView);
                    adapter.notifyDataSetChanged();

                    // Updating the header
                    JSONObject jsonObject = (JSONObject) result[0];
                    backupJsonObject = jsonObject;

                    ((SABDroidEx) mParent).updateLabels(jsonObject);
                    ((SABDroidEx) mParent).updateStatus("");
                    break;

                case SABnzbdController.MESSAGE_STATUS_UPDATE:
                    ((SABDroidEx) mParent).updateStatus(msg.obj.toString());
                    break;

                default:
                    break;
            }
        }
    };

    @SuppressWarnings("unchecked")
    private ArrayAdapter<String> extracted(ListView listView) {
        return (ArrayAdapter<String>) listView.getAdapter();
    }

    @SuppressWarnings("unchecked")
    ArrayList<String> extracted(Object[] data, int position) {
        return (ArrayList<String>) data[position];
    }

    private FragmentActivity mParent;

    public HistoryFragment(FragmentActivity fragmentActivity) {
        mParent = fragmentActivity;
    }

    public HistoryFragment(SABDroidEx sabDroidEx, ArrayList<String> historyRows) {
        this(sabDroidEx);
        rows = historyRows;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        SharedPreferences preferences = mParent.getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, 0);
        Preferences.update(preferences);

        LinearLayout downloadView = (LinearLayout) inflater.inflate(R.layout.list, null);

        listView = (ListView) downloadView.findViewById(R.id.queueList);
        downloadView.removeAllViews();

        listView.setAdapter(new HistoryListRowAdapter(mParent, rows));

        // Tries to fetch recoverable data
        Object data[] = (Object[]) mParent.getLastCustomNonConfigurationInstance();
        if (data != null && extracted(data, 1) != null) {
            rows = extracted(data, 1);
            backupJsonObject = (JSONObject) data[2];
            ((SABDroidEx) mParent).updateLabels(backupJsonObject);
        }

        if (rows.size() > 0) {
            ArrayAdapter<String> adapter = extracted(listView);
            adapter.notifyDataSetChanged();
        }
        else {
            manualRefreshHistory();
        }

        return listView;
    }

    /**
     * Refreshing the queue during startup or on user request. Asks to configure if still not done
     */
    public void manualRefreshHistory() {
        // First run setup
        if (!Preferences.isSet("server_url")) {
            mParent.showDialog(DIALOG_SETUP_PROMPT);
            return;
        }

        SABnzbdController.refreshHistory(messageHandler);
    }

    @Override
    public String getTitle() {
        return mParent.getString(R.string.tab_history);
    }

    @Override
    public void onFragmentActivated() {
        manualRefreshHistory();
    }
}
