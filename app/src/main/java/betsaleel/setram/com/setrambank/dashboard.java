package betsaleel.setram.com.setrambank;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import betsaleel.setram.com.setrambank.pojos.AccountInfo;
import betsaleel.setram.com.setrambank.pojos.AsyncCallback;
import betsaleel.setram.com.setrambank.pojos.networker;

public class dashboard extends AppCompatActivity {

    TabHost tabHost=null;
    ListView list_transact=null;
    ListView list_agency=null;
    ListView list_param=null;
    ListView list_tarif=null;
    TextView tv=null;
    public static JSONObject jsonObject=null;
    private static int SIZE_TAB=0;
    //private String host=

    private String getFormatDate(String strDate){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date=simpleDateFormat.parse(strDate);
            simpleDateFormat.applyPattern("dd-MM-yyyy");
            return simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    class QueryPinAuth extends AsyncTask<Void, Void, View> {
        private Context _ctx;
        private String _url,response;
        private ProgressDialog progressDialog;
        private URL uri;
        private HttpURLConnection httpCnx;
        private URLConnection urlConnection=null;
        private AsyncCallback delegate=null;
        private String hd;
        public QueryPinAuth(String url,Context ctx,String header, AsyncCallback callback){
            this._url=url;
            this._ctx=ctx;
            this.delegate=callback;
            this.hd=header;
        }
        @Override
        protected void onPreExecute(){
            progressDialog=new ProgressDialog(_ctx);
            progressDialog.setTitle(this.hd);
            if(this.hd.equals("Transaction")){
                progressDialog.setMessage("Transaction en cours");
            }else {
                progressDialog.setMessage(_ctx.getResources().getString(R.string.title_progress_auth_msg_fr));
            }

            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected View doInBackground(Void... voids) {

            try {
                String line="";
                uri=new URL(this._url);
                urlConnection=uri.openConnection();
                httpCnx=(HttpURLConnection)urlConnection;
                httpCnx.setRequestMethod("GET");
                httpCnx.setDoInput(true);
                //httpCnx.connect();
                InputStream is=httpCnx.getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                while((line=reader.readLine())!=null){
                    response=reader.readLine();
                }
                reader.close();
                is.close();
                httpCnx.disconnect();

            } catch (MalformedURLException e) {
                Toast.makeText(_ctx,"Http ULR:"+e.getMessage(),Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                Toast.makeText(_ctx,e.getMessage(),Toast.LENGTH_LONG).show();
            }


            return null;
        }

        @Override
        protected void onPostExecute(View view){
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
                // Toast.makeText(_ctx, "Response:"+response, Toast.LENGTH_SHORT).show();
                try {

                    JSONObject object=new JSONObject(response);
                    dashboard.jsonObject=object;
                    Toast.makeText(_ctx, "JSON Datas:"+dashboard.jsonObject.toString(), Toast.LENGTH_SHORT).show();
                    int status=object.getJSONObject("response").getInt("status");
                    //Toast.makeText(_ctx,"Status :"+Integer.toString(status),Toast.LENGTH_LONG).show();
                    if (status!=200){
                        delegate.queryResult(false);
                    }else{
                        delegate.queryResult(object);
                    }
                } catch (JSONException e) {
                    Toast.makeText(_ctx, "JSON Exception:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    // e.printStackTrace();
                }catch(Exception e){
                    Toast.makeText(_ctx, "Other Exception:"+e.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(dashboard.this,"retrun goBack",Toast.LENGTH_LONG)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_dash, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(dashboard.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }else{
            Toast.makeText(dashboard.this, " Not Action clicked", Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        ListView listView_transact=(ListView)findViewById(R.id.listView_transact);
        tv=(TextView)findViewById(R.id.tvHeaderTransacts);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Thin.ttf");
        tv.setTypeface(face);

        tv=(TextView)findViewById(R.id.tvHeaderAgencies);
        tv.setTypeface(face);


        tv=(TextView)findViewById(R.id.tvHeaderTarifs);
        tv.setTypeface(face);


        tv=(TextView)findViewById(R.id.tvHeaderParam);
        tv.setTypeface(face);


        list_param=(ListView)findViewById(R.id.listView_param);
        list_param.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    String solde=jsonObject.getJSONObject("data").getString("balance")+" "+jsonObject.getJSONObject("data").getString("codeCurrency").toUpperCase();
                    if (i==2){
                        AlertDialog.Builder builder=new AlertDialog.Builder(dashboard.this);
                        builder.setMessage("Votre solde est de : "+solde);
                        builder.setTitle("VOTRE SOLDE");
                        builder.setCancelable(true);
                        builder.setPositiveButton("QUITTER", new DialogInterface.OnClickListener() {
                            @Override

                            public void onClick(DialogInterface dialogInterface, int i) {


                            }
                        });
                        AlertDialog dialog=builder.create();
                        dialog.show();
                    }else if(i==0){
                        final LayoutInflater layout_inflater=LayoutInflater.from(dashboard.this);
                        final View layout_view=layout_inflater.inflate(R.layout.layout_change_pin,null);
                        AlertDialog.Builder builder=new AlertDialog.Builder(dashboard.this);
                        builder.setTitle("Securité: Changer votre pin");
                        builder.setView(layout_view);
                        builder.setCancelable(true);
                        builder.setPositiveButton("VALIDER", new DialogInterface.OnClickListener() {
                            @Override

                            public void onClick(DialogInterface dialogInterface, int i) {

                                EditText old_pin=(EditText)layout_view.findViewById(R.id.tx_old_pin_change);
                                EditText new_pin=(EditText)layout_view.findViewById(R.id.tx_nwe_pin_change);
                                if (old_pin.getText().toString().equals(AccountInfo.pin)){
                                    Toast.makeText(dashboard.this, "OK", Toast.LENGTH_SHORT).show();
                                    String http_url="http://"+networker.ipServer+"/setram_vip/codes/serveur/api/pinlog/"+ AccountInfo.AccountNum+"/"+old_pin.getText().toString()+"/"+new_pin.getText().toString()+"";
                                    Toast.makeText(dashboard.this, "URL:"+http_url, Toast.LENGTH_SHORT).show();

                                    QueryPinAuth queryPinAuth=new QueryPinAuth(http_url, dashboard.this,"Securité", new AsyncCallback() {
                                        @Override
                                        public void queryResult(Object result) {
                                            if (result instanceof JSONObject){
                                               dashboard.jsonObject=(JSONObject) result;
                                               Snackbar.make(dashboard.this.getCurrentFocus(),"Votre pin a été modifié",Snackbar.LENGTH_LONG).show();

                                                //Toast.makeText(dashboard.this, "Response:"+result.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    queryPinAuth.execute();


                                }else {
                                    Snackbar.make(dashboard.this.getCurrentFocus(),"Votre ancien pin est invalide, reessayez svp",Snackbar.LENGTH_LONG).show();
                                }

                            }
                        });

                        AlertDialog dialog=builder.create();
                        dialog.show();
                    }
                }catch(Exception e){

                }

            }
        });


        tabHost=(TabHost)findViewById(R.id.TabHost02);
        tabHost.setup();


        TabHost.TabSpec spec=tabHost.newTabSpec("tab_list_transact");
        spec.setIndicator("",getResources().getDrawable(R.drawable.transfer_selector));
        spec.setContent(R.id.tab_list_transact);

        tabHost.addTab(spec);


        //tabHost=(TabHost)findViewById(R.id.TabHost01);
       // tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab_list_agence").setIndicator("",getResources().getDrawable(R.drawable.agency_selector)).setContent(R.id.tab_list_agence));
        tabHost.addTab(tabHost.newTabSpec("tab_list_tarif").setIndicator("",getResources().getDrawable(R.drawable.tarif_selector)).setContent(R.id.tab_list_tarif));
        tabHost.addTab(tabHost.newTabSpec("tab_list_params").setIndicator("",getResources().getDrawable(R.drawable.param_selector)).setContent(R.id.tab_list_params));


        try {
            JSONObject response=jsonObject.getJSONObject("response");
           // Toast.makeText(dashboard.this, "RESPONSE:"+response.getInt("status"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {   tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void onTabChanged(String s) {
                //  Toast.makeText(dashboard.this, s, Toast.LENGTH_SHORT).show();




                dashboard.this.getTarifs();

                switch (s){
                    case "tab_list_transact":
                        dashboard.this.getTransact();
                        break;

                    case "tab_list_agence":
                        dashboard.this.getListAgency();
                        break;

                    case "tab_list_params":
                        dashboard.this.getParam();
                        break;
                }




            }
        });

            e.printStackTrace();
        }

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void onTabChanged(String s) {
               //  Toast.makeText(dashboard.this, s, Toast.LENGTH_SHORT).show();




                dashboard.this.getTarifs();

                switch (s){
                    case "tab_list_transact":
                        dashboard.this.getTransact();
                        break;

                    case "tab_list_agence":
                        dashboard.this.getListAgency();
                        break;

                    case "tab_list_params":
                        dashboard.this.getParam();
                        break;
                }




            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               try{
                   LayoutInflater layout_inflater=LayoutInflater.from(dashboard.this);
                   final View layout_view=layout_inflater.inflate(R.layout.layout_add_transact,null);
                   AlertDialog.Builder builder=new AlertDialog.Builder(dashboard.this);
                   final String NumAccountSender=jsonObject.getJSONObject("data").getString("accountNum");
                   final  String devise=jsonObject.getJSONObject("data").getString("codeCurrency");
                   final String idAccount=jsonObject.getJSONObject("data").getString("IdCompte");
                   final double amountClient=Double.parseDouble(jsonObject.getJSONObject("data").getString("balance"));

                   builder.setView(layout_view);
                   builder.setTitle("Envoyer de l'argent");
                   builder.setCancelable(true);
                   builder.setPositiveButton("ENVOYER", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           final String NumAccountReceiver=((EditText)layout_view.findViewById(R.id.txBenef)).getText().toString();
                           final String pinSender=((EditText)layout_view.findViewById(R.id.txPin)).getText().toString();
                           final String Amount=((EditText)layout_view.findViewById(R.id.txAmount)).getText().toString();

                           if (!NumAccountReceiver.equals("") && !Amount.equals("")){

                               if (AccountInfo.pin.equals(pinSender.trim())){
                                  if (Double.parseDouble(Amount)<amountClient){
                                      String http_url="http://www.agriprombtc.com/svptest/codes/serveur/ServeurTransfert_Mobile.php?IdCompteE="+idAccount+"&IdCompteB="+NumAccountReceiver+"&MontantTransfert="+Amount+"&CodeMonnaietrans="+devise+"&Moyen=MOBILE&idagent=0&IdAgence=0&PIN="+AccountInfo.pin;
                                      Toast.makeText(dashboard.this, "URL:"+http_url, Toast.LENGTH_SHORT).show();

                                      new QueryPinAuth(http_url, dashboard.this,"Transaction", new AsyncCallback() {
                                          @Override
                                          public void queryResult(Object result) {
                                              if (result instanceof JSONObject){
                                                  dashboard.jsonObject=null;
                                                  dashboard.jsonObject=(JSONObject)result;
                                                  //JSONObject test=(JSONObject)result;
                                                  //oast.makeText(dashboard.this, "Response:"+((JSONObject)result).toString(), Toast.LENGTH_SHORT).show();
                                                  Snackbar.make(dashboard.this.getCurrentFocus(),"Transaction effectuée avec succes",Snackbar.LENGTH_LONG).show();
                                                  getTransact();

                                              }
                                          }
                                      }).execute();
                                  }else{
                                      Snackbar.make(dashboard.this.getCurrentFocus(),"Le montant est superieur a votre balance",Snackbar.LENGTH_LONG).show();

                                  }

                               }else{
                                   Snackbar.make(dashboard.this.getCurrentFocus(),"Votre ancien pin est invalide, reessayez svp",Snackbar.LENGTH_LONG).show();

                               }

                           }else {
                               Snackbar.make(dashboard.this.getCurrentFocus(),"Certaines informations sont requises",Snackbar.LENGTH_LONG).show();
                           }
                       }
                   });
                   Typeface face = Typeface.createFromAsset(getAssets(),
                           "fonts/Roboto-Thin.ttf");
                   TextView txDevise=(TextView)layout_view.findViewById(R.id.txDevise);
                   txDevise.setTypeface(face);
                   txDevise.setText(jsonObject.getJSONObject("data").getString("currency"));
                   TextView txAccount=(TextView)layout_view.findViewById(R.id.textViewNum);
                   txAccount.setTypeface(face);
                   txAccount.setText(jsonObject.getJSONObject("data").getString("balance"));
                   AlertDialog dialog=builder.create();
                   dialog.show();
               }catch (Exception e){

               }
               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                       // .setAction("Action", null).show();
            }
        });
           // getTransact();

    }

    private void getTransact() {
        //Toast.makeText(dashboard.this, "GET TRANSACT:"+jsonObject.toString(), Toast.LENGTH_SHORT).show();
        list_transact=(ListView)findViewById(R.id.listView_transact);
        JSONArray transactOut=null;
        JSONArray transactIn=null;
        JSONObject data=null;
        try {
            data=jsonObject.getJSONObject("data");
            transactOut= jsonObject.getJSONArray("transactionsOut");
            transactIn=jsonObject.getJSONArray("transactionsIn");
            //Toast.makeText(dashboard.this,"lenght out:"+transactOut.length(),Toast.LENGTH_LONG).show();
            String label_TransOut=getResources().getString(R.string.transact_out_label_fr);
            String label_transactOn="Depot:";
            List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> element;

            for(int i = 0 ; i < transactOut.length() ; i++) {
                element = new HashMap<String, String>();
                try {
                    String name="";
                    if (transactOut.getJSONObject(i).getString("CodeTypeTransact").equals("Transfert")){
                        if(transactOut.getJSONObject(i).getJSONObject("beneficiairy")!=null){
                            JSONObject json_transOut=transactOut.getJSONObject(i).getJSONObject("beneficiairy");
                            label_TransOut="Envoyé :";
                            name=json_transOut.getString("Nom")+" "+json_transOut.getString("Postnom")+" "+
                                    json_transOut.getString("Prenom");
                            String currency=json_transOut.getString("CodeMonnaie");
                            String date_transact=json_transOut.getString("DateTransact").split(" ")[0];
                            String time_transact=json_transOut.getString("DateTransact").split(" ")[1];
                            element.put("cpte", name);
                            element.put("date",
                                    label_TransOut+" "+json_transOut.getString("MontantTransact")+currency+"\nDate :"+
                                            getFormatDate(date_transact)+" "+time_transact
                            );
                            liste.add(element);
                        }

                    }else if(transactOut.getJSONObject(i).getString("CodeTypeTransact").equals("Retrait")){
                        label_TransOut="Retrait :";
                        JSONObject obj=jsonObject.getJSONObject("data");
                        name=obj.getString("name")+" "+obj.getString("lastname")+" "+obj.getString("firstname");

                        element.put("cpte", name);
                        element.put("date",
                                label_TransOut+" "+transactOut.getJSONObject(i).getString("MontantTransact")
                                        +transactOut.getJSONObject(i).getString("CodeMonnaie")+"\nDate :"+
                                        getFormatDate(transactOut.getJSONObject(i).getString("DateTransact").split(" ")[0])+" "+
                                        transactOut.getJSONObject(i).getString("DateTransact").split(" ")[1]);
                        liste.add(element);

                    }



                    ListAdapter adapter = new SimpleAdapter(dashboard.this,
                            liste,

                            android.R.layout.simple_list_item_2,

                            new String[] {"cpte", "date"},

                            new int[] {android.R.id.text1, android.R.id.text2 });

                    list_transact.setAdapter(adapter);

                } catch (JSONException e) {
                    Toast.makeText(dashboard.this, "Exception 2:"+e.getMessage(), Toast.LENGTH_LONG).show();
                    //e.printStackTrace();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
            for (int j=0;j<transactIn.length();j++){
                element = new HashMap<String, String>();
                try {
                    JSONObject json_transIn=transactIn.getJSONObject(j);
                    String currency=json_transIn.getString("CodeMonnaie");
                    JSONObject client=jsonObject.getJSONObject("data");

                    String names=client.getString("name")+" "+client.getString("lastname");
                    String date_transact=json_transIn.getString("DateTransact").split(" ")[0];
                    String time_transact=json_transIn.getString("DateTransact").split(" ")[1];
                    switch (json_transIn.getString("CodeTypeTransact")){
                        case "Transfert":
                            label_transactOn="Reçu :";
                            break;
                        case "Depot":
                            label_transactOn="Depot :";

                            break;
                    }
                    // JSONObject beneficiairy=transactIn.getJSONObject(i).getJSONObject("beneficiary");

                    element.put("cpte", names);
                    element.put("date",
                            label_transactOn+" "+json_transIn.getString("MontantTransact")+currency+"\nDate :"+getFormatDate(date_transact)+" "+time_transact
                    );
                    liste.add(element);
                } catch (JSONException e) {
                    Toast.makeText(dashboard.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                ListAdapter adapter = new SimpleAdapter(dashboard.this,
                        liste,

                        android.R.layout.simple_list_item_2,

                        new String[] {"cpte", "date"},

                        new int[] {android.R.id.text1, android.R.id.text2 });

                list_transact.setAdapter(adapter);
            }

        } catch (JSONException e) {
            Toast.makeText(dashboard.this, "Exception 1:"+e.getMessage(), Toast.LENGTH_LONG).show();
           // e.printStackTrace();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }




    }
    private void getListAgency(){
        JSONArray arrayJSON=null;
        try {
            HashMap<String, String> element;
            List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
            arrayJSON=jsonObject.getJSONArray("agencies");
            for (int i=0;i<arrayJSON.length();i++){
                JSONObject json_agency=arrayJSON.getJSONObject(i);
                element=new HashMap<>();
                element.put("text1",json_agency.getString("NomAgence"));
                element.put("text2",json_agency.getString("AdresseAgence")+"\n"+json_agency.getString("PhoneAgence"));
                liste.add(element);
            }

            list_agency=(ListView)findViewById(R.id.listView_agency);

            ListAdapter adapter = new SimpleAdapter(dashboard.this,
                    liste,

                    android.R.layout.simple_list_item_2,

                    new String[] {"text1", "text2"},

                    new int[] {android.R.id.text1, android.R.id.text2 });

            list_agency.setAdapter(adapter);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error JSON:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private  void getParam(){
        list_param=(ListView)findViewById(R.id.listView_param);
        String[][] repertoire = new String[][]{
                {"Modifier Pin", "Votre pin de securité"},
                {"Changer Langue", "Modifier la langue de l'application"},
                {"Solde", "verifier votre solde"}
                };
        List<HashMap<String, String>> liste = new

                ArrayList<HashMap<String, String>>();
        HashMap<String, String> element;
        for(int i = 0 ; i < repertoire.length ; i++) {
            element = new HashMap<String, String>();
            element.put("text1", repertoire[i][0]);

            element.put("text2", repertoire[i][1]);
            liste.add(element);
        }
        ListAdapter adapter = new SimpleAdapter(dashboard.this,
                liste,

                android.R.layout.simple_list_item_2,

                new String[] {"text1", "text2"},

                new int[] {android.R.id.text1, android.R.id.text2 });

        list_param.setAdapter(adapter);
    }

    private void getTarifs(){
        list_tarif=(ListView)findViewById(R.id.listView_tarifs);
        List<HashMap<String,String>>liste=new ArrayList<>();
        HashMap<String,String>element;
        try {
            JSONArray tabTarif=jsonObject.getJSONArray("tarif");
            for (int i=0;i<tabTarif.length();i++){
                JSONObject object_tarif=tabTarif.getJSONObject(i);
                String borne="De "+object_tarif.getString("BorneInf")+" à "+object_tarif.getString("BornSuper")+
                        " "+object_tarif.getString("Libmonnaie");
                String gain=object_tarif.getString("TypePlage")+":"+object_tarif.getString("MontantPlage");
                element = new HashMap<String, String>();
                element.put("text1", borne);
                element.put("text2", gain);
                liste.add(element);
            }
           // Toast.makeText(this, "Count Tarif Sizer: "+liste.size(), Toast.LENGTH_SHORT).show();
            ListAdapter adapter = new SimpleAdapter(dashboard.this,
                    liste,

                    android.R.layout.simple_list_item_2,

                    new String[] {"text1", "text2"},

                    new int[] {android.R.id.text1, android.R.id.text2 });

            list_tarif.setAdapter(adapter);
        } catch (JSONException e) {
            Toast.makeText(this, "Exception JSON:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
