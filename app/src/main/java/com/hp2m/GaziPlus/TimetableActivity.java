package com.hp2m.GaziPlus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;

public class TimetableActivity extends AppCompatActivity {

    final EventBus bus = EventBus.getDefault();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<TimetableInformation> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        bus.register(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new TimetableAdapter(this, getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Button startButton = (Button) findViewById(R.id.timetable_download);


        // hey sadr, below code is to login ogrenci.gazi.edu.tr
        final String username = "sadr";
        final String password = "at";
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on here we start our task
                // you should go ahead and check the TimetableTask.class before clicking
                new TimetableTask(getApplicationContext(), username, password).execute();
                // so basically, once this task fires, result will be sent to our onEvent method
            }
        });


    }

    private List<TimetableInformation> getData() {
        // this just returns empty list
        // recyclerView needs it
        return Collections.emptyList();
    }


    // on here you will get the results of the TimetableTask
    public void onEvent(TimetableTaskCompleted event) {


        // get the list from event
        TimetableInformation current = new TimetableInformation();
        for (int i = 0; i < event.list.size(); i++) {
            current.text = event.list.get(i);
            data.add(current);
        }


        // below code is to update the list, don't care this
        Runnable r = new Runnable() {
            @Override
            public void run() {
                adapter = new TimetableAdapter(getApplicationContext(), data);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }
        };
        runOnUiThread(r);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // we should unregister from bus listener list to avoid memory leaks
        // (writes in EventBus docs)
        bus.unregister(this);
    }
}
