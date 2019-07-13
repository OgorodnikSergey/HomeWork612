package ru.ogorodnik.homework612;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private List<Map<String,String>> text;
    private ListView listView;
    SharedPreferences sharPref;
    private SharedPreferences.Editor editor;
    private ArrayList<Integer> saveChange = new ArrayList<Integer>();
    private String [] arrayList;
    private SwipeRefreshLayout swiperefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swiperefresh.setOnRefreshListener(this);
        preferencesChack(savedInstanceState);

    }
    public void onRefresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                preferencesChack(null);
                swiperefresh.setRefreshing(false);
            }
        }, 100);
    }

    private void preferencesChack(Bundle bundle){
        sharPref = getSharedPreferences("Satting", Context.MODE_PRIVATE);
        editor = sharPref.edit();
        if(sharPref.contains("TEXT")){
            arrayList = sharPref.getString("TEXT", "Текст отсутствует").split("\n\n");
        } else {
            editor.putString("TEXT", getResources().getString(R.string.large_text));
            editor.apply();
            arrayList = sharPref.getString("TEXT", "Текст отсутствует").split("\n\n");
        }
        listView = findViewById(R.id.list);
        final List<Map<String,String>> values = prepareContent();
        final BaseAdapter listContentAdapter = createAdapter(values);
        listView.setAdapter(listContentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                saveChange.add(position);
                values.remove(position);
                listContentAdapter.notifyDataSetChanged();
            }
        });
        if(bundle != null) {
            saveChange.addAll(bundle.getIntegerArrayList("saveList"));
            for (int index : saveChange) {
                values.remove(index);
            }
        }
    }

    @NonNull
    private BaseAdapter createAdapter(List<Map<String,String>> values) {
        SimpleAdapter simpleAdapter= new SimpleAdapter(this, values, R.layout.list_items,
                new String[]{"nameText", "ourText"}, new int[]{R.id.first_text, R.id.second_text});
        return simpleAdapter; }

    @NonNull
    private List<Map<String,String>> prepareContent() {
        text= new ArrayList<>();
        for (int i = 0; i < arrayList.length; i++) {
            Map<String,String> mapString = new HashMap<>();
            mapString.put("nameText", arrayList[i]);
            mapString.put("ourText", String.valueOf(arrayList[i].length()));
            text.add(mapString);
        }
        return text;
    }

    protected void onSaveInstanceState(Bundle outState){
        outState.putIntegerArrayList("saveList", saveChange);
        super.onSaveInstanceState(outState);
    }

}