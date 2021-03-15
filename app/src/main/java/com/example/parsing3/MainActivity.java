package com.example.parsing3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.parsing3.DatabaseHelper.KEY_ID;
import static com.example.parsing3.DatabaseHelper.TABLE_Users;

public class MainActivity extends AppCompatActivity {
    private static long set;
    DatabaseHelper databaseHelper;
     Button knopka;
     Button knopka2;
    ArrayList<String> aray;
    ArrayAdapter adapter;
    Runnable runnable;
    Thread secThread;
     ListView list;
     Document doc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
aray = new ArrayList<>();
        knopka = findViewById(R.id.kjpka);
        knopka2 = findViewById(R.id.kjpka2);


        list = findViewById(R.id.listwie);
        viewData();




        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                int k =2000;
                if(set + k > System.currentTimeMillis()){

                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    Cursor cursor = db.query(DatabaseHelper.TABLE_Users, null, null, null, null, null, null);
                    if(cursor.moveToPosition(i)) {
                        String  rowId = cursor.getString(cursor.getColumnIndex(KEY_ID));
                        db.delete(TABLE_Users, KEY_ID + "=?",  new String[]{rowId});
                        //  Toast.makeText(MainActivity.this, ""+i, Toast.LENGTH_SHORT).show();

                        aray.clear();
                        viewData();
                        db.close();
                    }


                }else{
                    
                }
                set = System.currentTimeMillis();

            }
        });


        knopka2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           //  deleteFirstRow();

        }
        });



        knopka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                init();

            }
        });

    }


    private void init()
    {

        aray.clear();
        viewData();

        runnable = new Runnable() {
            @Override
            public void run() {
                getWeb();

            }
        };

        secThread = new Thread(runnable);
        secThread.start();


    }
    private void getWeb()
    {
        try {
            String sss = "";
            String str = "";
            String str2 = "";
            String str3 = "";


            doc = Jsoup.connect("https://select.by/kurs/").get();

            Elements elements = doc.getElementsByTag("tbody");
            Element outable = elements.get(1);
            Elements elem = outable.children();
            Element dolor = elem.get(2);
            Element dolor2 = elem.get(1);
            Element dolor3 = elem.get(0);
            Elements dolor_elem = dolor.children();
            Elements dolor_elem2 = dolor2.children();
            Elements dolor_elem3 = dolor3.children();

            Calendar cal = Calendar.getInstance();
            int mes = cal.get(Calendar.DAY_OF_MONTH);
            int chas = cal.get(Calendar.HOUR);
            int minute = cal.get(Calendar.MINUTE);

            str += dolor_elem.text();
            str2 += dolor_elem2.text();
            str3 += dolor_elem3.text();
            sss +=str+str2+str3;

databaseHelper.insertData(sss + " " + mes + " день " + "  " + chas + " час " + minute + " минута");

         //     Log.d("Mylog", "" +sss + " " + mes + " день " + "  " + chas + " час " + minute + " минута" );

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




    private void viewData() {
        Cursor cursor = databaseHelper.viewData();
        if(cursor.getCount()==0){
            Toast.makeText(this, "нету базы", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                aray.add(cursor.getString(1));
            }
          //  Log.d("Mylog", "" +aray );
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, aray);
            list.setAdapter(adapter);
        }

    }




}