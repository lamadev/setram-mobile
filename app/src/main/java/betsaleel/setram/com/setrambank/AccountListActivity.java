package betsaleel.setram.com.setrambank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.widget.ViewFlipper;

import org.json.JSONException;
import org.json.JSONObject;

import betsaleel.setram.com.setrambank.configs.Configuration;
import betsaleel.setram.com.setrambank.pojos.AccountInfo;
import betsaleel.setram.com.setrambank.pojos.AdapterDB;
import betsaleel.setram.com.setrambank.pojos.AsyncCallback;

public class AccountListActivity extends AppCompatActivity {

    ListView listView=null;


    public static Map<String,String> mapAccount;
    public static String choiceLang="Français",ActualLang="";
    private ViewFlipper viewFlipper=null;
    private boolean isLongTouch=false;
    private ListAdapter adapter =null;
    private  List<Map<String,String>>listingAccount=null;
    private TextView txLabelnotThing=null;
    private TextInputLayout textInputLayout=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);
        txLabelnotThing=(TextView)findViewById(R.id.labelNotThing);

        TextView headerList=(TextView)findViewById(R.id.header);
        TextView Title=(TextView)findViewById(R.id.title_header);
        final Button btn_add_account=(Button)findViewById(R.id.btn_add_account);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Thin.ttf");
        final EditText account=(EditText)findViewById(R.id.tx_cpte_bank);
        final EditText oldPin=(EditText)findViewById(R.id.tx_old_pin);
        final EditText newPin=(EditText)findViewById(R.id.tx_new_pin);

        txLabelnotThing.setTypeface(face);
        final EditText account_por=(EditText)findViewById(R.id.tx_cpte_bank_por);
        final EditText oldPin_por=(EditText)findViewById(R.id.tx_old_pin_por);
        final EditText newPin_por=(EditText)findViewById(R.id.tx_new_pin_por);
        final TextView headerTextView=(TextView)findViewById(R.id.header);
        listView=(ListView)findViewById(R.id.listView_account);
        final View viewInclude=(View)findViewById(R.id.include_pin);
        headerList.setTypeface(face);
        Title.setTypeface(face);
        btn_add_account.setTypeface(face);
        final AdapterDB adapterDB=new AdapterDB(getApplicationContext());
        adapterDB.OpenDB();
        listingAccount=adapterDB.getListAccount();
        final Map mapStatus=adapterDB.getAppStatus();
        //Toast.makeText(AccountListActivity.this, "Status :"+mapStatus.get("langs")+"List accounts: "+adapterDB.getListAccount().size(), Toast.LENGTH_SHORT).show();

          if (mapStatus.get("id")!=null){
            ActualLang=mapStatus.get("langs").toString();
            if (listingAccount.size()>0){
                String lng=adapterDB.getCurrentLanguage("");
                switch (lng){

                    case "por":
                            headerTextView.setText(getResources().getString(R.string.header_list_por));
                            btn_add_account.setText(getResources().getString(R.string.btn_add_account_por));

                            ActualLang="por";
                        break;
                    default:
                            headerTextView.setText(getResources().getString(R.string.header_list_fr));

                            ActualLang="fr";
                        break;


                }
                viewInclude.setVisibility(View.GONE);
                FrameLayout frameLayout=(FrameLayout)findViewById(R.id.containerFrameLayout);
                frameLayout.setVisibility(View.VISIBLE);
            }else{
                viewInclude.setVisibility(View.GONE);
                FrameLayout frameLayout=(FrameLayout)findViewById(R.id.containerFrameLayout);
                frameLayout.setVisibility(View.GONE);
                txLabelnotThing.setText(getResources().getString(R.string.labelNotThing_fr));
                txLabelnotThing.setVisibility(View.VISIBLE);
                txLabelnotThing.setText((mapStatus.get("langs").equals("por")?getResources().getString(R.string.labelNotThing_por):getResources().getString(R.string.labelNotThing_fr)));

            }


            final List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> element;

            int index=1;
            for(Map<String,String> m:listingAccount){
                element = new HashMap<String, String>();
                String account2="xxx-"+m.get("name").substring(3);
                element.put("account",account2);
                element.put("num",(ActualLang.equals("por")?"Número da conta":"Numero compte"));
                liste.add(element);

            }
             adapter = new SimpleAdapter(getApplicationContext(),
                    liste,

                    android.R.layout.simple_list_item_2,

                    new String[] {"num", "account"},

                    new int[] {android.R.id.text1, android.R.id.text2 });

            listView.setAdapter(adapter);

        }else{

            LayoutInflater layout_inflater= LayoutInflater.from(AccountListActivity.this);
            final View layout_view=layout_inflater.inflate(R.layout.layout_langs,null);

            Spinner spin = (Spinner) layout_view.findViewById(R.id.spinLang);
            final List<String> categories = new ArrayList<String>();
            categories.add("Français");
            categories.add("Portugais");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(dataAdapter);
            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    choiceLang=categories.get(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            AlertDialog.Builder builder=new AlertDialog.Builder(AccountListActivity.this);
            builder.setView(layout_view);
            builder.setTitle("Choisir la langue");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(!choiceLang.equals("Français")){
                        textInputLayout=(TextInputLayout)findViewById(R.id.textInputLayoutAccount);
                        textInputLayout.setHint(getResources().getString(R.string.title_compte_bank_por));
                        textInputLayout=textInputLayout=(TextInputLayout)findViewById(R.id.textInputLayoutOldPin);
                        textInputLayout.setHint(getResources().getString(R.string.hint_text_old_pin_por));
                        textInputLayout=textInputLayout=(TextInputLayout)findViewById(R.id.textInputLayoutNewPin);
                        textInputLayout.setHint(getResources().getString(R.string.hint_text_new_pin_por));
                        btn_add_account.setText(getResources().getString(R.string.btn_add_account_por));
                        ActualLang="por";
                    }else{
                        textInputLayout=(TextInputLayout)findViewById(R.id.textInputLayoutAccount);
                        textInputLayout.setHint(getResources().getString(R.string.title_compte_bank_fr));
                        textInputLayout=textInputLayout=(TextInputLayout)findViewById(R.id.textInputLayoutOldPin);
                        textInputLayout.setHint(getResources().getString(R.string.hint_text_old_pin_fr));
                        textInputLayout=textInputLayout=(TextInputLayout)findViewById(R.id.textInputLayoutNewPin);
                        textInputLayout.setHint(getResources().getString(R.string.hint_text_new_pin_fr));
                        btn_add_account.setText(getResources().getString(R.string.btn_create_account));
                        ActualLang="fr";

                    }

                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
        }
        adapterDB.CloseDB();

        class QueryPinAuth extends AsyncTask<Void, Void, View> {
            private Context _ctx;
            private String _url,response;
            private ProgressDialog progressDialog;
            private URL uri;
            private HttpURLConnection httpCnx;
            private URLConnection urlConnection=null;
            private AsyncCallback delegate=null;
            public QueryPinAuth(String url,Context ctx, AsyncCallback callback){
                this._url=url;
                this._ctx=ctx;
                this.delegate=callback;
            }
            @Override
            protected void onPreExecute(){
                progressDialog=new ProgressDialog(_ctx);
                if(!ActualLang.equals("por")){
                    progressDialog.setTitle(_ctx.getResources().getString(R.string.title_progress_auth_fr));
                    progressDialog.setMessage(_ctx.getResources().getString(R.string.title_progress_auth_msg_fr));
                }else{
                    progressDialog.setTitle(_ctx.getResources().getString(R.string.title_progress_auth_por));
                    progressDialog.setMessage(_ctx.getResources().getString(R.string.title_progress_auth_msg_por));
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

                } /*catch (MalformedURLException e) {
                    Toast.makeText(_ctx,"Http ULR:"+e.getMessage(),Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    Toast.makeText(_ctx,e.getMessage(),Toast.LENGTH_LONG).show();
                }*/catch (Exception e){
                    Toast.makeText(_ctx, "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(_ctx, "Response 200:"+response, Toast.LENGTH_SHORT).show();
                        dashboard.jsonObject=object;
                        int status=object.getJSONObject("response").getInt("status");
                        //Toast.makeText(_ctx,"Status :"+Integer.toString(status),Toast.LENGTH_LONG).show();
                        if (status!=200){
                            delegate.queryResult(false);
                            // Toast.makeText(getApplicationContext(),"RESPONSE:"+response,Toast.LENGTH_LONG).show();

                        }else{
                            //Toast.makeText(getApplicationContext(),"RESPONSE:"+response,Toast.LENGTH_LONG).show();
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


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                isLongTouch=true;
                final int index=i;

                AlertDialog.Builder builder=new AlertDialog.Builder(AccountListActivity.this);
                builder.setTitle((ActualLang.equals("por")?"Excluir conta":"Supprimer le compte"));
                builder.setMessage((ActualLang.equals("por")?"Tem certeza de que deseja excluir?":"Êtes-vous sûr de supprimer"));
                builder.setPositiveButton((ActualLang.equals("por") ? "SIM" : "OUI"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Map<String,String>objectAccount=listingAccount.get(index);
                        listingAccount.remove(index);
                        adapterDB.OpenDB();
                        adapterDB.deleteAccount(objectAccount.get("id"));
                        adapterDB.RemoveLogs(objectAccount.get("id"));
                        adapterDB.CloseDB();

                        final List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
                        HashMap<String, String> element;

                        //int index=1;
                        for(Map<String,String> m:listingAccount){
                            element = new HashMap<String, String>();
                            String account2="xxx-"+m.get("name").substring(3);
                            element.put("account",account2);
                            element.put("num",(ActualLang.equals("por")?"Número da conta":"Numero compte"));
                            liste.add(element);

                        }
                        adapter = new SimpleAdapter(getApplicationContext(),
                                liste,

                                android.R.layout.simple_list_item_2,

                                new String[] {"num", "account"},

                                new int[] {android.R.id.text1, android.R.id.text2 });

                        listView.setAdapter(adapter);
                        if (listingAccount.size()==0){
                            viewInclude.setVisibility(View.GONE);
                            FrameLayout frameLayout=(FrameLayout)findViewById(R.id.containerFrameLayout);
                            frameLayout.setVisibility(View.GONE);
                            txLabelnotThing.setText(getResources().getString(R.string.labelNotThing_fr));
                            txLabelnotThing.setVisibility(View.VISIBLE);
                            txLabelnotThing.setText((mapStatus.get("langs").equals("por")?getResources().getString(R.string.labelNotThing_por):getResources().getString(R.string.labelNotThing_fr)));

                        }

                    }

                });
                builder.setNegativeButton((ActualLang.equals("por") ? "NO" : "NON"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setCancelable(true);
                AlertDialog dialog=builder.create();
                dialog.show();
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isLongTouch==false){
                    final Map<String,String>mapper=listingAccount.get(i);
                    final String pin=mapper.get("pin");
                    final String idCompte=mapper.get("id");
                    //Toast.makeText(AccountListActivity.this, "Language:"+mapper.get("id")+"PIN:"+mapper.get("pin"), Toast.LENGTH_SHORT).show();
                    LayoutInflater layout_inflater= LayoutInflater.from(AccountListActivity.this);
                    final View layout_view=layout_inflater.inflate(R.layout.layout_login,null);
                    AlertDialog.Builder builder=new AlertDialog.Builder(AccountListActivity.this);
                    builder.setView(layout_view);
                    builder.setTitle((ActualLang.equals("por")?"Autenticação":"Authentification"));
                    builder.setCancelable(true);
                    builder.setPositiveButton((ActualLang.equals("por")?"Validar":"Valider"), new DialogInterface.OnClickListener() {
                        @Override

                        public void onClick(DialogInterface dialogInterface, int i) {
                            final EditText editPin=(EditText)layout_view.findViewById(R.id.pin_login);


                            final String url="https://setramvip.com/codes/serveur/api/loginController.php?idcompte="+idCompte+"&pin="+editPin.getText().toString().trim();
                            //Toast.makeText(AccountListActivity.this,"RESPONSE:"+url,Toast.LENGTH_LONG).show();
                            Configuration configuration=new Configuration();
                            boolean isConnected=configuration.statusConnectivity(getApplicationContext());
                            if (isConnected){
                                new QueryPinAuth(url, AccountListActivity.this, new AsyncCallback() {
                                    @Override
                                    public void queryResult(Object result) {
                                        try{
                                            if (result instanceof JSONObject){

                                                AccountInfo.AccountNum=((JSONObject)result).getJSONObject("data").getString("accountNum");
                                                AccountInfo.pin=((JSONObject)result).getJSONObject("data").getString("pin");
                                                AccountInfo.idAccount=((JSONObject)result).getJSONObject("data").getString("IdCompte");
                                                HomeActivity.jsonObject=(JSONObject) result;
                                                ActualLang=mapper.get("lang").toString();
                                                AdapterDB db=new AdapterDB(AccountListActivity.this);
                                                db.OpenDB();
                                                db.UpdateLogs(HomeActivity.jsonObject.toString(),idCompte);
                                                //Toast.makeText(AccountListActivity.this,result.toString(),Toast.LENGTH_LONG).show();
                                                Intent iDash=new Intent(AccountListActivity.this,HomeActivity.class);
                                                startActivity(iDash);
                                            }else{
                                                Toast.makeText(AccountListActivity.this,ActualLang.equals("fr")?"Authentfication réjétée":"Autenticação rejeitada",Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        }catch(Exception ex){

                                        }

                                    }
                                }).execute();
                            }else{
                                Toast.makeText(AccountListActivity.this, ActualLang.equals("fr")?"Pas de connexion internet disponible":"Nenhuma conexão com a internet disponível", Toast.LENGTH_SHORT).show();

                            }



                        }
                    });
                    AlertDialog dialog=builder.create();
                    dialog.show();

                }else{
                    isLongTouch=false;
                }

            }
        });

        btn_add_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputLayout til=null;
                //Toast.makeText(AccountListActivity.this, "Sizer:"+listingAccount.size(), Toast.LENGTH_SHORT).show();
                Configuration configuration=new Configuration();
                boolean isConnected=configuration.statusConnectivity(getApplicationContext());
                if (isConnected){
                    if (mapStatus.get("id")!=null){
                        LayoutInflater layout_inflater= LayoutInflater.from(AccountListActivity.this);
                        final View layout_view=layout_inflater.inflate(R.layout.layout_add_account_list,null);
                        AlertDialog.Builder builder=new AlertDialog.Builder(AccountListActivity.this);
                        final EditText accountAddList=(EditText)layout_view.findViewById(R.id.account_added);
                        final EditText pinAddList=(EditText)layout_view.findViewById(R.id.pin_added);
                        til=layout_view.findViewById(R.id.textInputLayoutAccountList);
                        til.setHint(!ActualLang.equals("fr")?"Número da conta":"Numéro de compte");
                        til=layout_view.findViewById(R.id.textInputLayoutPinAdd);
                        til.setHint(ActualLang.equals("fr")?"Votre pin":"Seu código");
                        builder.setView(layout_view);
                        builder.setTitle(ActualLang.equals("fr")?"Ajouter un compte":"Adicione uma conta");
                        builder.setCancelable(true);
                        builder.setPositiveButton(ActualLang.equals("fr")?"CONNEXION":"CONEXÃO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String url="https://setramvip.com/codes/serveur/api/loginController.php?numcompte="+accountAddList.getText().toString().trim()+"&pin="+pinAddList.getText().toString().trim();
                                //Toast.makeText(MainActivity.this,"RESPONSE:"+url,Toast.LENGTH_LONG).show();

                                new QueryPinAuth(url, AccountListActivity.this, new AsyncCallback() {
                                    @Override
                                    public void queryResult(Object result) {
                                        try{
                                            if (result instanceof JSONObject){
                                                boolean isListed=false;
                                                AccountInfo.AccountNum=((JSONObject)result).getJSONObject("data").getString("accountNum");
                                                AccountInfo.pin=((JSONObject)result).getJSONObject("data").getString("pin");
                                                AccountInfo.idAccount=((JSONObject)result).getJSONObject("data").getString("IdCompte");
                                                HomeActivity.jsonObject=(JSONObject) result;
                                                //Toast.makeText(AccountListActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                                                if (listingAccount.size()>0){
                                                    for (Map<String,String> m:listingAccount){
                                                        if(!m.get("name").toString().equals(accountAddList.getText().toString())){
                                                            isListed=true;
                                                        }
                                                        if (isListed)break;
                                                    }
                                                }else{
                                                    isListed=true;
                                                }
                                                if (isListed){
                                                    AdapterDB adapterDB=new AdapterDB(getApplicationContext());
                                                    adapterDB.OpenDB();
                                                    adapterDB.AddAccount(Integer.parseInt(AccountInfo.idAccount),AccountInfo.AccountNum,AccountInfo.pin,mapStatus.get("langs").toString());
                                                    adapterDB.AddContentTransaction(result.toString(),Integer.parseInt(AccountInfo.idAccount));
                                                    adapterDB.addAppStatus(mapStatus.get("langs").toString());
                                                    adapterDB.UpdateCurrentUser(AccountInfo.idAccount);

                                                }

                                                // Toast.makeText(MainActivity.this,result.toString(),Toast.LENGTH_LONG).show();
                                                //List<Map<String,String>> datas=adapterDB.getListAccount();
                                                // Toast.makeText(AccountListActivity.this, "Size :"+adapterDB.getListAccount().size(), Toast.LENGTH_SHORT).show();
                                                Intent iDash=new Intent(AccountListActivity.this,HomeActivity.class);
                                                startActivity(iDash);
                                            }else{
                                                Toast.makeText(AccountListActivity.this,(ActualLang.equals("por")?"Autenticação rejeitada":"Authentification réjetée"),Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        }catch(JSONException ex){

                                        }

                                    }
                                }).execute();
                            }
                        });
                        AlertDialog dialog=builder.create();
                        dialog.show();
                    }else{
                        //Toast.makeText(AccountListActivity.this, "Create!!", Toast.LENGTH_SHORT).show();


                        String url="https://setramvip.com/codes/serveur/api/pinController.php?account="+account.getText().toString()+"&old="+oldPin.getText().toString()+"&new="+newPin.getText().toString();
                        new QueryPinAuth(url, AccountListActivity.this, new AsyncCallback() {
                            @Override
                            public void queryResult(Object result) {
                                try{
                                    if (result instanceof JSONObject){
                                        boolean isListed=false;
                                        AccountInfo.AccountNum=((JSONObject)result).getJSONObject("data").getString("accountNum");
                                        AccountInfo.pin=((JSONObject)result).getJSONObject("response").getString("pin");
                                        AccountInfo.idAccount=((JSONObject)result).getJSONObject("data").getString("IdCompte");
                                        HomeActivity.jsonObject=(JSONObject) result;
                                        // Toast.makeText(AccountListActivity.this, "RESPONSE: "+result.toString(), Toast.LENGTH_SHORT).show();
                                        AdapterDB adapterDB=new AdapterDB(getApplicationContext());
                                        adapterDB.OpenDB();
                                        adapterDB.AddAccount(Integer.parseInt(AccountInfo.idAccount),AccountInfo.AccountNum,AccountInfo.pin,(choiceLang.equals("Français")?"fr":"por"));
                                        adapterDB.AddContentTransaction(result.toString(),Integer.parseInt(AccountInfo.idAccount));
                                        adapterDB.addAppStatus(ActualLang);
                                        if (adapterDB.getCurrentUser()==""){
                                            adapterDB.addCurrentUser(AccountInfo.idAccount);
                                            //Toast.makeText(AccountListActivity.this, "NULLABLE", Toast.LENGTH_SHORT).show();
                                        }else{
                                            adapterDB.UpdateCurrentUser(AccountInfo.idAccount);
                                        }
                                        //Toast.makeText(AccountListActivity.this, "Id current:"+adapterDB.getCurrentUser(), Toast.LENGTH_SHORT).show();
                                        //String logs=adapterDB.getLogs(adapterDB.getCurrentUser());
                                        adapterDB.CloseDB();

                                        // List<Map<String,String>> datas=adapterDB.getListAccount();
                                        //  Toast.makeText(AccountListActivity.this, "Size :"+datas.size(), Toast.LENGTH_SHORT).show();
                                        Intent iDash=new Intent(AccountListActivity.this,HomeActivity.class);
                                        startActivity(iDash);
                                    }else{
                                        Toast.makeText(AccountListActivity.this,ActualLang.equals("fr")?"Authentfication réjétée":"Autenticação rejeitada",Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }catch(JSONException ex){
                                    Toast.makeText(AccountListActivity.this, "Error:"+ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).execute();
                    }
                }else{
                    Toast.makeText(AccountListActivity.this, ActualLang.equals("fr")?"Pas de connexion internet disponible":"Nenhuma conexão com a internet disponível", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
}
