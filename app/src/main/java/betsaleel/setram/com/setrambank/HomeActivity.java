package betsaleel.setram.com.setrambank;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Map;

import betsaleel.setram.com.setrambank.configs.Configuration;
import betsaleel.setram.com.setrambank.pojos.AccountInfo;
import betsaleel.setram.com.setrambank.pojos.AdapterDB;
import betsaleel.setram.com.setrambank.pojos.AsyncCallback;
import betsaleel.setram.com.setrambank.pojos.networker;

public class HomeActivity extends AppCompatActivity {
    TabHost tabHost=null;
    ListView list_transact=null;
    ListView list_agency=null;
    ListView list_param=null;
    ListView list_tarif=null;
    TextView tv=null;
    private String choiceLang="";
    public static JSONObject jsonObject=null;
    private static int SIZE_TAB=0;
    private Configuration configuration=null;
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
    class QueryAddTransaction extends AsyncTask<Void, Void, View> {
        private Context _ctx;
        private String _url,response;
        private ProgressDialog progressDialog;
        private URL uri;
        private HttpURLConnection httpCnx;
        private URLConnection urlConnection=null;
        private AsyncCallback delegate=null;
        private String hd;
        public QueryAddTransaction(String url,Context ctx,String header, AsyncCallback callback){
            this._url=url;
            this._ctx=ctx;
            this.delegate=callback;
            this.hd=header;
        }
        @Override
        protected void onPreExecute(){
            progressDialog=new ProgressDialog(_ctx);

            if(this.hd.equals("Transaction")){
                progressDialog.setTitle(AccountListActivity.ActualLang.equals("por")?"Transação":this.hd);
                progressDialog.setMessage(AccountListActivity.ActualLang.equals("por")?"Transação em andamento":"Transaction en cours");
            }
            if (this.hd.equals("Actualiser")){
                progressDialog.setTitle(AccountListActivity.ActualLang.equals("por")?"Atualize":this.hd);
                progressDialog.setMessage(AccountListActivity.ActualLang.equals("por")?"Atualize a lista de transações":"Actualiser la liste des transactions");
            }
            if(this.hd.equals("Sécurité")){
                progressDialog.setTitle(AccountListActivity.ActualLang.equals("por")?"Segurança":this.hd);
                progressDialog.setMessage(AccountListActivity.ActualLang.equals("por")?"Atualização em andamento":"Mise à jour en cours....");
            }
            if (this.hd.equals("solde")){
                progressDialog.setTitle(AccountListActivity.ActualLang.equals("por")?"Saldo de carregamento":"Chargement solde");
                progressDialog.setMessage(AccountListActivity.ActualLang.equals("por")?"Carregando":"Chargement en cours");
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
                response="";
                while((line=reader.readLine())!=null){
                    response+=reader.readLine();
                }
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
               // Toast.makeText(_ctx, "ANSWER:"+response, Toast.LENGTH_SHORT).show();

                try {

                    JSONObject object=new JSONObject(response);
                    HomeActivity.jsonObject=object;
                    //Toast.makeText(_ctx, "JSON Datas:"+HomeActivity.jsonObject.toString(), Toast.LENGTH_SHORT).show();
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

            if(this.hd.equals("Transaction")){
                progressDialog.setTitle(AccountListActivity.ActualLang.equals("por")?"Transação":this.hd);
                progressDialog.setMessage(AccountListActivity.ActualLang.equals("por")?"Transação em andamento":"Transaction en cours");
            }
            if (this.hd.equals("Actualiser")){
                progressDialog.setTitle(AccountListActivity.ActualLang.equals("por")?"Atualize":this.hd);
                progressDialog.setMessage(AccountListActivity.ActualLang.equals("por")?"Atualize a lista de transações":"Actualiser la liste des transactions");
            }
            if(this.hd.equals("Sécurité")){
                progressDialog.setTitle(AccountListActivity.ActualLang.equals("por")?"Segurança":this.hd);
                progressDialog.setMessage(AccountListActivity.ActualLang.equals("por")?"Atualização em andamento":"Mise à jour en cours....");
            }
            if (this.hd.equals("solde")){
                progressDialog.setTitle(AccountListActivity.ActualLang.equals("por")?"Saldo de carregamento":"Chargement solde");
                progressDialog.setMessage(AccountListActivity.ActualLang.equals("por")?"Carregando":"Chargement en cours");
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
                response=reader.readLine();


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
                    HomeActivity.jsonObject=object;
                    //Toast.makeText(_ctx, "JSON Datas:"+HomeActivity.jsonObject.toString(), Toast.LENGTH_SHORT).show();
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
                   // Toast.makeText(_ctx, "Other Exception:"+e.getMessage(), Toast.LENGTH_SHORT).show();

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
        MenuItem itemActu=menu.getItem(0);
        itemActu.setTitle((AccountListActivity.ActualLang.equals("por")?"Enviar dinheiro":getResources().getString(R.string.action_settings_add)));
        MenuItem itemMoney=menu.getItem(1);
        itemMoney.setTitle((AccountListActivity.ActualLang.equals("por")?"Atualização":getResources().getString(R.string.action_settings)));

        return true;
    }
    private void RefreshAccount(String url){
        new QueryPinAuth(url, HomeActivity.this, "Actualiser", new AsyncCallback() {
            @Override
            public void queryResult(Object result) {
                try{
                    if (result instanceof JSONObject){

                        AccountInfo.AccountNum=((JSONObject)result).getJSONObject("data").getString("accountNum");
                        AccountInfo.pin=((JSONObject)result).getJSONObject("data").getString("pin");
                        AccountInfo.idAccount=((JSONObject)result).getJSONObject("data").getString("IdCompte");
                        HomeActivity.jsonObject=(JSONObject) result;
                        AdapterDB adb=new AdapterDB(getApplicationContext());
                        adb.OpenDB();
                        adb.UpdateLogs(result.toString(),AccountInfo.idAccount);
                        getTransact();
                        //Toast.makeText(AccountListActivity.this,result.toString(),Toast.LENGTH_LONG).show();
                        // Intent iDash=new Intent(AccountListActivity.this,HomeActivity.class);
                        //startActivity(iDash);
                    }else{
                        //Toast.makeText(AccountListActivity.this,"Authentfication réjetée",Toast.LENGTH_LONG)
                        //      .show();
                    }
                }catch(Exception ex){

                }
            }
        }).execute();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
           //Toast.makeText(HomeActivity.this, "ACtualiser Id:"+item.getOrder(), Toast.LENGTH_LONG).show();
            final String url="http://www.agriprombtc.com/svptest/codes/serveur/api/loginController.php?idcompte="+AccountInfo.idAccount+"&pin="+AccountInfo.pin;
            //Toast.makeText(HomeActivity.this,"RESPONSE:"+url,Toast.LENGTH_LONG).show();
            configuration=new Configuration();
            boolean isConnected=configuration.statusConnectivity(getApplicationContext());
            if (isConnected){
                new QueryPinAuth(url, HomeActivity.this, "Actualiser", new AsyncCallback() {
                    @Override
                    public void queryResult(Object result) {
                        try{
                            if (result instanceof JSONObject){

                                AccountInfo.AccountNum=((JSONObject)result).getJSONObject("data").getString("accountNum");
                                AccountInfo.pin=((JSONObject)result).getJSONObject("data").getString("pin");
                                AccountInfo.idAccount=((JSONObject)result).getJSONObject("data").getString("IdCompte");
                                HomeActivity.jsonObject=(JSONObject) result;
                                AdapterDB adb=new AdapterDB(getApplicationContext());
                                adb.OpenDB();
                                adb.UpdateLogs(result.toString(),AccountInfo.idAccount);
                                getTransact();
                                //Toast.makeText(AccountListActivity.this,result.toString(),Toast.LENGTH_LONG).show();
                                // Intent iDash=new Intent(AccountListActivity.this,HomeActivity.class);
                                //startActivity(iDash);
                            }else{
                                //Toast.makeText(AccountListActivity.this,"Authentfication réjetée",Toast.LENGTH_LONG)
                                //      .show();
                            }
                        }catch(Exception ex){

                        }
                    }
                }).execute();
            }else{
                Toast.makeText(HomeActivity.this, AccountListActivity.ActualLang.equals("fr")?"Pas de connexion internet disponible":"Nenhuma conexão com a internet disponível", Toast.LENGTH_SHORT).show();
            }


            return true;
        }else{
            try{

                LayoutInflater layout_inflater=LayoutInflater.from(HomeActivity.this);
                final View layout_view=layout_inflater.inflate(R.layout.layout_add_transact,null);
                AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
                final String NumAccountSender=jsonObject.getJSONObject("data").getString("accountNum");
                final  String devise=jsonObject.getJSONObject("data").getString("codeCurrency");
                final String idAccount=jsonObject.getJSONObject("data").getString("IdCompte");
                final double amountClient=Double.parseDouble(jsonObject.getJSONObject("data").getString("balance"));
                AccountInfo.pin=jsonObject.getJSONObject("response").getString("pin");
               // Toast.makeText(getApplicationContext(), "PIN IS :"+AccountInfo.pin, Toast.LENGTH_SHORT).show();
                builder.setView(layout_view);
                builder.setTitle(AccountListActivity.ActualLang.equals("fr")?"Envoyer de l'argent":"Enviar dinheiro");
                builder.setCancelable(true);
                final EditText editReceiver=((EditText)layout_view.findViewById(R.id.txBenef));
               // editReceiver.setImeActionLabel(AccountListActivity.ActualLang.equals("fr")?"Numero Compte beneficiaire":"Número da conta do beneficiário",6);
                //editReceiver.setHint(AccountListActivity.ActualLang.equals("fr")?"Numero Compte beneficiaire":"Número da conta do beneficiário");
                final EditText editPin=((EditText)layout_view.findViewById(R.id.txPin));
                //editPin.setHint(AccountListActivity.ActualLang.equals("fr")?"":"Código PIN");
                final EditText editAmount=((EditText)layout_view.findViewById(R.id.txAmount));
                //editAmount.setHint(AccountListActivity.ActualLang.equals("fr")?"":"Quantidade para enviar");
                TextInputLayout layoutTextInput=layout_view.findViewById(R.id.textInputLayoutReceiver);
                layoutTextInput.setHint(AccountListActivity.ActualLang.equals("fr")?"Numero Compte beneficiaire":"Número da conta do beneficiário");
                layoutTextInput=layout_view.findViewById(R.id.textInputLayoutPin);
                layoutTextInput.setHint(AccountListActivity.ActualLang.equals("fr")?"Code Pin":"Código PIN");
                layoutTextInput=layout_view.findViewById(R.id.textInputLayoutAmount);
                layoutTextInput.setHint(AccountListActivity.ActualLang.equals("fr")?"Montant à envoyer":"Quantidade para enviar");
                builder.setPositiveButton(AccountListActivity.ActualLang.equals("fr")?"ENVOYER":"ENVIAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final String NumAccountReceiver=editReceiver.getText().toString();
                        final String pinSender=editPin.getText().toString();
                        final String Amount=((EditText)layout_view.findViewById(R.id.txAmount)).getText().toString();

                        if (!NumAccountReceiver.equals("") && !Amount.equals("")){
                            if (AccountInfo.pin.equals(pinSender.trim())){
                                if (Double.parseDouble(Amount)<amountClient  ){
                                   // String http_url="http://www.agriprombtc.com/svptest/codes/serveur/ServeurTransfert_Mobile.php?IdCompteE="+idAccount+"&IdCompteB="+NumAccountReceiver+"&MontantTransfert="+Amount+"&CodeMonnaietrans="+devise+"&Moyen=MOBILE&idagent=0&IdAgence=0&PIN="+AccountInfo.pin;
                                    String http_url="https://setramvip.com/codes/serveur/ServeurTransfert_Mobile.php?IdCompteE="+idAccount+"&IdCompteB="+NumAccountReceiver+"&MontantTransfert="+Amount+"&CodeMonnaietrans="+devise+"&Moyen=MOBILE&idagent=0&IdAgence=0&PIN="+AccountInfo.pin+"&CodeTypeCompte=Standard";
                                   // Toast.makeText(HomeActivity.this, "URL:"+http_url, Toast.LENGTH_SHORT).show();
                                    configuration=new Configuration();
                                    boolean isConnected=configuration.statusConnectivity(getApplicationContext());
                                    if (isConnected){
                                        if (NumAccountSender.equals(NumAccountReceiver)){
                                            String message=AccountListActivity.ActualLang.equals("fr")?"Impossible d'effectuer cette opération":"Não pode executar esta operação";
                                            Toast.makeText(HomeActivity.this,message, Toast.LENGTH_SHORT).show();
                                        }else{
                                            if (amountClient<0 ){
                                                String message=AccountListActivity.ActualLang.equals("fr")?"Les valeurs négatives ne sont pas permises":"Valores negativos não são permitidos";
                                            }
                                            new QueryAddTransaction(http_url, HomeActivity.this,"Transaction", new AsyncCallback() {
                                                @Override
                                                public void queryResult(Object result) {
                                                    if (result instanceof JSONObject){

                                                        HomeActivity.jsonObject=(JSONObject)result;
                                                        AdapterDB db=new AdapterDB(HomeActivity.this);
                                                        db.OpenDB();

                                                        try{
                                                            String id=HomeActivity.jsonObject.getJSONObject("data").getString("IdCompte");
                                                            db.UpdateLogs(HomeActivity.jsonObject.toString(),id);
                                                            getTransact();
                                                            //HomeActivity.jsonObject=new JSONObject(db.getLogs(db.getCurrentUser()));
                                                           Toast.makeText(HomeActivity.this, HomeActivity.jsonObject.toString(), Toast.LENGTH_LONG).show();

                                                        }catch(Exception e){

                                                        }
                                                        //JSONObject test=(JSONObject)result;
                                                       // Toast.makeText(HomeActivity.this, "Response:"+.toString(), Toast.LENGTH_SHORT).show();
                                                        Snackbar.make(HomeActivity.this.getCurrentFocus(),AccountListActivity.ActualLang.equals("fr")?"Transaction effectuée avec succes":"Transação concluída com sucesso",Snackbar.LENGTH_LONG).show();
                                                       // getTransact();

                                                    }
                                                }
                                            }).execute();
                                        }

                                    }else {
                                        Toast.makeText(HomeActivity.this, AccountListActivity.ActualLang.equals("fr")?"Pas de connexion internet disponible":"Nenhuma conexão com a internet disponível", Toast.LENGTH_SHORT).show();
                                    }

                                }else{
                                    Snackbar.make(HomeActivity.this.getCurrentFocus(),AccountListActivity.ActualLang.equals("fr")?"Le montant est superieur à votre balance":"O valor é maior que o seu saldo",Snackbar.LENGTH_LONG).show();

                                }

                            }else{
                                Snackbar.make(HomeActivity.this.getCurrentFocus(),AccountListActivity.ActualLang.equals("fr")?"Votre ancien pin est invalide, réessayez svp":"O seu pin antigo é inválido, por favor tente novamente",Snackbar.LENGTH_LONG).show();

                            }

                        }else {
                            Snackbar.make(HomeActivity.this.getCurrentFocus(),AccountListActivity.ActualLang.equals("fr")?"Certaines informations sont requises":"Alguma informação é necessária",Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
                Typeface face = Typeface.createFromAsset(getAssets(),
                        "fonts/Roboto-Thin.ttf");
                TextView txDevise=(TextView)layout_view.findViewById(R.id.txDevise);
                TextView tLabelDevise=layout_view.findViewById(R.id.textViewCount);
                tLabelDevise.setText(AccountListActivity.ActualLang.equals("fr")?"Votre Solde : ":"Seu saldo:");
                txDevise.setTypeface(face);
                txDevise.setText(jsonObject.getJSONObject("data").getString("currency"));
                TextView txAccount=(TextView)layout_view.findViewById(R.id.textViewNum);
                txAccount.setTypeface(face);
                txAccount.setText(jsonObject.getJSONObject("data").getString("balance"));
                AlertDialog dialog=builder.create();
                dialog.show();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Erreur :"+e.getMessage(), Toast.LENGTH_LONG).show();
            }
           // Toast.makeText(HomeActivity.this, " Not Action clicked", Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        //toolbar.setNavigationIcon(R.drawable.iconnav);
        AdapterDB db=new AdapterDB(getApplicationContext());
        db.OpenDB();
        try {
            HomeActivity.jsonObject=new JSONObject(db.getLogs(db.getCurrentUser()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(), "Language actual :"+db.getCurrentLanguage(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "Data:"+db.getLogs(), Toast.LENGTH_LONG).show();
        AccountListActivity.ActualLang=db.getCurrentLanguage(db.getCurrentUser());
        db.CloseDB();
        ListView listView_transact=(ListView)findViewById(R.id.listView_transact);
        tv=(TextView)findViewById(R.id.tvHeaderTransacts);
        tv.setText((AccountListActivity.ActualLang.equals("por")?"Diário transações":"Journal transactions"));
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Thin.ttf");
        tv.setTypeface(face);

        tv=(TextView)findViewById(R.id.tvHeaderAgencies);
        tv.setText((AccountListActivity.ActualLang.equals("por")?"Agências":"Les agences"));
        tv.setTypeface(face);


        tv=(TextView)findViewById(R.id.tvHeaderTarifs);
        tv.setText((AccountListActivity.ActualLang.equals("por")?"Custos de transferência":"Coûts transferts"));
        tv.setTypeface(face);


        tv=(TextView)findViewById(R.id.tvHeaderParam);
        tv.setText((AccountListActivity.ActualLang.equals("por")?"configurações":"Paramétrage"));
        tv.setTypeface(face);
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
        list_param=(ListView)findViewById(R.id.listView_param);
        list_param.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    if (i==2){
                        String id=HomeActivity.jsonObject.getJSONObject("data").getString("IdCompte");
                        String pin=HomeActivity.jsonObject.getJSONObject("data").getString("pin");
                        String urlServer="https://setramvip.com/codes/serveur/api/loginController.php?idcompte="+id+"&pin="+pin;
                      //  Toast.makeText(HomeActivity.this, urlServer, Toast.LENGTH_LONG).show();
                         new QueryPinAuth(urlServer, HomeActivity.this, "solde", new AsyncCallback() {
                            @Override
                            public void queryResult(Object result) {
                                HomeActivity.jsonObject=(JSONObject)result;
                                getTransact();
                                String solde= null;
                                try {
                                    solde = HomeActivity.jsonObject.getJSONObject("data").getString("balance")+" "+jsonObject.getJSONObject("data").getString("codeCurrency").toUpperCase();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
                                builder.setMessage(AccountListActivity.ActualLang.equals("por")?"Seu saldo bancário é : "+solde+"":"Votre solde est de : "+solde+"");
                                builder.setTitle(AccountListActivity.ActualLang.equals("por")?"SEU BALANÇO DO BANCO":"VOTRE SOLDE");
                                builder.setCancelable(true);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override

                                    public void onClick(DialogInterface dialogInterface, int i) {


                                    }
                                });
                                AlertDialog dialog=builder.create();
                                dialog.show();
                            }
                        }).execute();

                    }else if(i==0){
                        final LayoutInflater layout_inflater=LayoutInflater.from(HomeActivity.this);
                        final View layout_view=layout_inflater.inflate(R.layout.layout_change_pin,null);
                        AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
                        builder.setTitle(AccountListActivity.ActualLang.equals("por")?"Segurança: Mude seu pin":"Securité: Changer votre pin");
                        builder.setView(layout_view);
                        builder.setCancelable(true);
                        tv=(TextView)layout_view.findViewById(R.id.toldPin);
                        tv.setText(AccountListActivity.ActualLang.equals("por")?"Digite seu código antigo":"Entrez votre ancien pin");
                        tv=(TextView)layout_view.findViewById(R.id.tnewPin);
                        tv.setText(AccountListActivity.ActualLang.equals("por")?"Digite seu novo código":"Entrez votre ancien pin");
                        builder.setPositiveButton(AccountListActivity.ActualLang.equals("por")?"CONFIRMAR":"VALIDER", new DialogInterface.OnClickListener() {
                            @Override

                            public void onClick(DialogInterface dialogInterface, int i) {

                                final EditText old_pin=(EditText)layout_view.findViewById(R.id.tx_old_pin_change);
                                final EditText new_pin=(EditText)layout_view.findViewById(R.id.tx_nwe_pin_change);

                                if (old_pin.getText().toString().equals(AccountInfo.pin)){
                                    //Toast.makeText(HomeActivity.this, "OK", Toast.LENGTH_SHORT).show();
                                    //String http_url="http://"+ networker.ipServer+"setram_vip/codes/serveur/api/pinController.php?account="+ AccountInfo.AccountNum+"&old="+old_pin.getText().toString()+"&new="+new_pin.getText().toString()+"";
                                    String http_url="https://setramvip.com/codes/serveur/api/pinController.php?account="+AccountInfo.AccountNum+"&old="+old_pin.getText().toString()+"&new="+new_pin.getText().toString()+"";
                                    //Toast.makeText(HomeActivity.this, "URL:"+http_url, Toast.LENGTH_LONG).show();

                                  HomeActivity.QueryPinAuth queryPinAuth=new HomeActivity.QueryPinAuth(http_url, HomeActivity.this,"Sécurité", new AsyncCallback() {
                                        @Override
                                        public void queryResult(Object result) {
                                            if (result instanceof JSONObject){
                                                HomeActivity.jsonObject=(JSONObject) result;
                                                Snackbar.make(HomeActivity.this.getCurrentFocus(),AccountListActivity.ActualLang.equals("fr")?"Votre pin a été modifié":"Seu pin foi modificado",Snackbar.LENGTH_LONG).show();
                                                AdapterDB db=new AdapterDB(HomeActivity.this);
                                                db.OpenDB();
                                                db.UpdateKeyPin(old_pin.getText().toString(),new_pin.getText().toString());
                                                AccountInfo.pin=new_pin.getText().toString();
                                                //Toast.makeText(dashboard.this, "Response:"+result.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    configuration=new Configuration();
                                    boolean isConnected=configuration.statusConnectivity(getApplicationContext());
                                    if (isConnected){
                                        queryPinAuth.execute();
                                    }else{
                                        Toast.makeText(HomeActivity.this, AccountListActivity.ActualLang.equals("fr")?"Pas de connexion internet disponible":"Nenhuma conexão com a internet disponível", Toast.LENGTH_SHORT).show();
                                    }




                                }else {
                                    Snackbar.make(HomeActivity.this.getCurrentFocus(),AccountListActivity.ActualLang.equals("fr")?"Votre ancien pin est invalide, réessayez svp":"O seu pin antigo é inválido, por favor tente novamente",Snackbar.LENGTH_LONG).show();
                                }

                            }
                        });

                        AlertDialog dialog=builder.create();
                        dialog.show();
                    }else{
                        LayoutInflater layout_inflater= LayoutInflater.from(HomeActivity.this);
                        final View layout_view=layout_inflater.inflate(R.layout.layout_langs,null);
                        Spinner spin = (Spinner) layout_view.findViewById(R.id.spinLang);
                        final List<String> categories = new ArrayList<String>();
                        categories.add("Français");
                        categories.add("Portugais");
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_spinner_item, categories);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spin.setAdapter(dataAdapter);

                        if(AccountListActivity.ActualLang.equals("fr")){
                            spin.setSelection(0);
                        }else{
                            spin.setSelection(1);
                        }
                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                choiceLang=categories.get(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        final AdapterDB adb=new AdapterDB(getApplicationContext());
                        AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
                        builder.setView(layout_view);
                        builder.setTitle(AccountListActivity.ActualLang.equals("fr")?"Choisir la langue":"Escolher idioma");
                        builder.setCancelable(true);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(choiceLang.equals("Français")){
                                    AccountListActivity.ActualLang="fr";
                                    adb.OpenDB();
                                    adb.UpdateLanguage(AccountInfo.idAccount,AccountListActivity.ActualLang);
                                    tv=(TextView)findViewById(R.id.tvHeaderTransacts);
                                    tv.setText((AccountListActivity.ActualLang.equals("por")?"Diário transações":"Journal transactions"));
                                    Typeface face = Typeface.createFromAsset(getAssets(),
                                            "fonts/Roboto-Thin.ttf");
                                    tv.setTypeface(face);

                                    tv=(TextView)findViewById(R.id.tvHeaderAgencies);
                                    tv.setText((AccountListActivity.ActualLang.equals("por")?"Agências":"Les agences"));
                                    tv.setTypeface(face);


                                    tv=(TextView)findViewById(R.id.tvHeaderTarifs);
                                    tv.setText((AccountListActivity.ActualLang.equals("por")?"Custos de transferência":"Coûts transferts"));
                                    tv.setTypeface(face);


                                    tv=(TextView)findViewById(R.id.tvHeaderParam);
                                    tv.setText((AccountListActivity.ActualLang.equals("por")?"configurações":"Paramétrage"));
                                    tv.setTypeface(face);
                                    getParam();

                                }else{
                                    AccountListActivity.ActualLang="por";
                                    adb.OpenDB();
                                    adb.UpdateLanguage(AccountInfo.idAccount,AccountListActivity.ActualLang);
                                    tv=(TextView)findViewById(R.id.tvHeaderTransacts);
                                    tv.setText((AccountListActivity.ActualLang.equals("por")?"Diário transações":"Journal transactions"));
                                    Typeface face = Typeface.createFromAsset(getAssets(),
                                            "fonts/Roboto-Thin.ttf");
                                    tv.setTypeface(face);

                                    tv=(TextView)findViewById(R.id.tvHeaderAgencies);
                                    tv.setText((AccountListActivity.ActualLang.equals("por")?"Agências":"Les agences"));
                                    tv.setTypeface(face);


                                    tv=(TextView)findViewById(R.id.tvHeaderTarifs);
                                    tv.setText((AccountListActivity.ActualLang.equals("por")?"Custos de transferência":"Coûts transferts"));
                                    tv.setTypeface(face);


                                    tv=(TextView)findViewById(R.id.tvHeaderParam);
                                    tv.setText((AccountListActivity.ActualLang.equals("por")?"configurações":"Paramétrage"));
                                    tv.setTypeface(face);
                                    getParam();
                                }
                                adb.CloseDB();

                            }
                        });
                        AlertDialog dialog=builder.create();
                        dialog.show();
                    }
                }catch(Exception e){

                }

            }
        });
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void onTabChanged(String s) {
                //  Toast.makeText(dashboard.this, s, Toast.LENGTH_SHORT).show();




                HomeActivity.this.getTarifs();

                switch (s){
                    case "tab_list_transact":
                        HomeActivity.this.getTransact();
                        break;

                    case "tab_list_agence":
                        HomeActivity.this.getListAgency();
                        break;

                    case "tab_list_params":
                        HomeActivity.this.getParam();
                        break;
                }




            }
        });

        getTransact();
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
            String label_transactOn=AccountListActivity.ActualLang.equals("por")?"Depósito :":"Depot :";
            List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> element;

            for(int i = 0 ; i < transactOut.length() ; i++) {
                element = new HashMap<String, String>();
                try {
                    String name="";
                    if (transactOut.getJSONObject(i).getString("CodeTypeTransact").equals("Transfert")){
                        if(!transactOut.getJSONObject(i).isNull("beneficiairy")){
                            //Toast.makeText(HomeActivity.this,"Id JSON: "+i,Toast.LENGTH_LONG).show();
                            JSONObject json_transOut=transactOut.getJSONObject(i).getJSONObject("beneficiairy");
                            label_TransOut=AccountListActivity.ActualLang.equals("por")?"Enviado :":"Envoyé :";
                            name=json_transOut.getString("Nom")+" "+json_transOut.getString("Postnom")+" "+
                                    json_transOut.getString("Prenom");
                            String currency=json_transOut.getString("CodeMonnaie");
                            String date_transact=json_transOut.getString("DateTransact").split(" ")[0];
                            String time_transact=json_transOut.getString("DateTransact").split(" ")[1];
                            String sdate="";
                            if (AccountListActivity.ActualLang.equals("por")){
                                sdate=label_TransOut+" "+transactOut.getJSONObject(i).getString("MontantTransact")
                                        +transactOut.getJSONObject(i).getString("CodeMonnaie")+"\nData :"+
                                        getFormatDate(transactOut.getJSONObject(i).getString("DateTransact").split(" ")[0])+" "+
                                        transactOut.getJSONObject(i).getString("DateTransact").split(" ")[1];
                            }else{
                                sdate=label_TransOut+" "+transactOut.getJSONObject(i).getString("MontantTransact")
                                        +transactOut.getJSONObject(i).getString("CodeMonnaie")+"\nDate :"+
                                        getFormatDate(transactOut.getJSONObject(i).getString("DateTransact").split(" ")[0])+" "+
                                        transactOut.getJSONObject(i).getString("DateTransact").split(" ")[1];
                            }
                            element.put("cpte", name);
                            element.put("date",sdate);
                            liste.add(element);
                        }

                    }else if(transactOut.getJSONObject(i).getString("CodeTypeTransact").equals("Retrait")){
                        label_TransOut=AccountListActivity.ActualLang.equals("por")?"Retirada :":"Retrait :";
                        JSONObject obj=jsonObject.getJSONObject("data");
                        name=obj.getString("name")+" "+obj.getString("lastname")+" "+obj.getString("firstname");
                        String sdate="";
                        if (AccountListActivity.ActualLang.equals("por")){
                            sdate=label_TransOut+" "+transactOut.getJSONObject(i).getString("MontantTransact")
                                    +transactOut.getJSONObject(i).getString("CodeMonnaie")+"\nData :"+
                                    getFormatDate(transactOut.getJSONObject(i).getString("DateTransact").split(" ")[0])+" "+
                                    transactOut.getJSONObject(i).getString("DateTransact").split(" ")[1];
                        }else{
                            sdate=label_TransOut+" "+transactOut.getJSONObject(i).getString("MontantTransact")
                                    +transactOut.getJSONObject(i).getString("CodeMonnaie")+"\nDate :"+
                                    getFormatDate(transactOut.getJSONObject(i).getString("DateTransact").split(" ")[0])+" "+
                                    transactOut.getJSONObject(i).getString("DateTransact").split(" ")[1];
                        }
                        element.put("cpte", name);
                        element.put("date",sdate);
                        liste.add(element);

                    }



                    ListAdapter adapter = new SimpleAdapter(HomeActivity.this,
                            liste,

                            android.R.layout.simple_list_item_2,

                            new String[] {"cpte", "date"},

                            new int[] {android.R.id.text1, android.R.id.text2 });

                    list_transact.setAdapter(adapter);

                } catch (JSONException e) {
                    Toast.makeText(HomeActivity.this, "Exception id :"+i+":"+e.getMessage(), Toast.LENGTH_LONG).show();
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
                            label_transactOn=AccountListActivity.ActualLang.equals("por")?"Recebido":"Reçu :";
                            break;
                        case "Depot":
                            label_transactOn=AccountListActivity.ActualLang.equals("por")?"Depósito":"Depot :";

                            break;
                    }
                    // JSONObject beneficiairy=transactIn.getJSONObject(i).getJSONObject("beneficiary");

                    element.put("cpte", names);
                    element.put("date",
                            label_transactOn+" "+json_transIn.getString("MontantTransact")+currency+"\nDate :"+getFormatDate(date_transact)+" "+time_transact
                    );
                    liste.add(element);
                } catch (JSONException e) {
                    Toast.makeText(HomeActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                ListAdapter adapter = new SimpleAdapter(HomeActivity.this,
                        liste,

                        android.R.layout.simple_list_item_2,

                        new String[] {"cpte", "date"},

                        new int[] {android.R.id.text1, android.R.id.text2 });

                list_transact.setAdapter(adapter);
            }

        } catch (JSONException e) {
            Toast.makeText(HomeActivity.this, "Exception 1:"+e.getMessage(), Toast.LENGTH_LONG).show();
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

            ListAdapter adapter = new SimpleAdapter(HomeActivity.this,
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
        String[][] repertoire =null;
        if (!AccountListActivity.ActualLang.equals("por")){
            repertoire=new String[][]{
                    {"Modifier Pin", "Votre pin de securité"},
                    {"Changer Langue", "Modifier la langue de l'application"},
                    {"Solde", "verifier votre solde"}
            };
        }else {
            repertoire=new String[][]{
                    {"Modificar Código", "Seu pino de segurança"},
                    {"Alterar idioma", "Alterar o idioma do aplicativo"},
                    {"Saldo bancário", "verifique seu saldo bancário"}
            };
        }
        List<HashMap<String, String>> liste = new

                ArrayList<HashMap<String, String>>();
        HashMap<String, String> element;
        for(int i = 0 ; i < repertoire.length ; i++) {
            element = new HashMap<String, String>();
            element.put("text1", repertoire[i][0]);

            element.put("text2", repertoire[i][1]);
            liste.add(element);
        }
        ListAdapter adapter = new SimpleAdapter(HomeActivity.this,
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
                        " "+object_tarif.getString("CodeMonnaie");
                String typePlage=object_tarif.getString("TypePlage").toUpperCase().equals("F")?"Forfait":"Pourcentage";
                String gain=typePlage+":"+object_tarif.getString("MontantPlage");
                element = new HashMap<String, String>();
                element.put("text1", borne);
                element.put("text2", gain);
                liste.add(element);
            }
            // Toast.makeText(this, "Count Tarif Sizer: "+liste.size(), Toast.LENGTH_SHORT).show();
            ListAdapter adapter = new SimpleAdapter(HomeActivity.this,
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
