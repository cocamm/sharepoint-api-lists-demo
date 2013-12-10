package com.example.sharepoint.client.ui;

import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sharepoint.client.R;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.BaseOperation;
import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;
import com.example.sharepoint.client.network.ListCreateEntityTask;
import com.example.sharepoint.client.network.ListReadTask;
import com.example.sharepoint.client.network.ListUpdateTask;
import com.example.sharepoint.client.network.ListsOperation;
import com.example.sharepoint.client.network.ListsReceiveTask;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataValue;

/**
 * Sample activity displaying request results.
 */
public class MainActivity extends Activity implements OnOperaionExecutionListener {

    /**
     * Maps list names and guids
     */
    private HashMap<String, String> guids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ListsReceiveTask(this, this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onExecutionComplete(final BaseOperation operation, final boolean executionResult) {
        runOnUiThread(new Runnable() {
            public void run() {
                displayListsToView(operation, executionResult);
            }
        });
    }

    /**
     * Displays lists to ListView
     * 
     * @param operation
     * @param executionResult
     */
    private void displayListsToView(final BaseOperation operation, final boolean executionResult) {
        try {
            if (executionResult != true) {
                ((TextView) MainActivity.this.findViewById(R.id.pending_request_text_stub)).setText("Error");
                return;
            }

            ODataCollectionValue result = ((ListsOperation) operation).getResult();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
            Iterator<ODataValue> iter = result.iterator();
            guids = new HashMap<String, String>();
            while (iter.hasNext()) {
                ODataComplexValue value = (ODataComplexValue) iter.next().asComplex();
                String title = value.get("Title").getPrimitiveValue().toString();
                String guid = value.get("Id").getPrimitiveValue().toString();
                guids.put(title, guid);
                adapter.add(title);
            }

            ListView lists = (ListView) MainActivity.this.findViewById(R.id.available_lists);
            lists.setAdapter(adapter);
            lists.setVisibility(View.VISIBLE);
            MainActivity.this.findViewById(R.id.pending_request_text_stub).setVisibility(View.GONE);

            lists.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        ODataEntity list = new ListReadTask(new ListReadOperationExecutionListener(MainActivity.this), MainActivity.this).execute(
                                guids.get(((TextView) view).getText())).get();
                        new ListCreateEntityTask(null, MainActivity.this).execute(list);
                    } catch (Exception e) {
                        Logger.logApplicationException(e, getClass().getSimpleName() + ".onItemClick(): Error.");
                    }
                }
            });

        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".run(): Error.");
            ((TextView) MainActivity.this.findViewById(R.id.pending_request_text_stub)).setText("Error");
        }
    }
}
