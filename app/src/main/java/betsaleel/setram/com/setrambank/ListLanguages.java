package betsaleel.setram.com.setrambank;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import betsaleel.setram.com.setrambank.Customizer.ListViewAdapter;
import betsaleel.setram.com.setrambank.pojos.AccountInfo;
import betsaleel.setram.com.setrambank.pojos.AdapterDB;
import betsaleel.setram.com.setrambank.pojos.AsyncCallback;
import betsaleel.setram.com.setrambank.pojos.Country;
import betsaleel.setram.com.setrambank.pojos.networker;

public class ListLanguages extends AppCompatActivity {

    public static String langSelected="";
    @Override
    public void onBackPressed() {
        //Toast.makeText(dashboard.this,"retrun goBack",Toast.LENGTH_LONG)
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_languages);


        final List<Country>lst=new LinkedList<>();
        lst.add(new Country("Angola","Portugais",R.drawable.angola_flag_icon));
        lst.add(new Country("Congo RD","Francais",R.drawable.drc_icon));
        ListViewAdapter listViewAdapter=new ListViewAdapter(ListLanguages.this,lst);
        ListView listView=(ListView)findViewById(R.id.lst_lang);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Country country=lst.get(i);
                if(country.getLanguage().equals("Francais")){
                   langSelected="fr";
                }else if(country.getLanguage().equals("Portugais")){
                 langSelected="por";
                }
            }
        });

    }

}
