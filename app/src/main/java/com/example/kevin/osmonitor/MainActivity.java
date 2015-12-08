package com.example.kevin.osmonitor;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ListActivity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kevin.osmonitor.adapters.ListAdapter;

import java.util.List;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get running process
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
        if (runningProcesses != null && runningProcesses.size() > 0) {
            // Set data to the list adapter
            setListAdapter(new ListAdapter(this, runningProcesses));
        } else {
            // In case there are no processes running
            Toast.makeText(getApplicationContext(), "No application is running", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        long send       = 0;
        long received    = 0;
        // Get UID of the selected process
        int uid = ((RunningAppProcessInfo)getListAdapter().getItem(position)).uid;

        // Get traffic data
        received = TrafficStats.getUidRxBytes(uid);
        send = TrafficStats.getUidTxBytes(uid);

        // Display data
        Toast.makeText(getApplicationContext(), "UID " + uid + " details...\n send: " + send/1000 + "kB" + " \n recived: " + received/1000 + "kB", Toast.LENGTH_LONG).show();
    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
