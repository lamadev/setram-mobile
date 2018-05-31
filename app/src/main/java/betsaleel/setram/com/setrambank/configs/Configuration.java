package betsaleel.setram.com.setrambank.configs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import betsaleel.setram.com.setrambank.HomeActivity;

/**
 * Created by hornellama on 22/12/2017.
 */

public class Configuration {
    private static  HashMap<String,String> responseMap=new HashMap<>();

    public  static HashMap<String,String> statusPin(Context ctx){

        try {
            InputStream is=ctx.getAssets().open("config_files/config.json");
            byte[] buffer=new byte[is.available()];
            is.read(buffer);
            String data=new String(buffer,"UTF-8");
            Toast.makeText(ctx, "Config content:"+data, Toast.LENGTH_SHORT).show();
            JSONObject jsonConfig=new JSONObject(data);
            responseMap.put("status",jsonConfig.getString("pin"));
            responseMap.put("language",jsonConfig.getString("language"));
          //  Toast.makeText(ctx, "Pin code is :"+statusPin, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(ctx,"Error IO:"+e.getMessage(),Toast.LENGTH_LONG).show();
            return null;
        } catch (JSONException e) {
          //  Toast.makeText(ctx,"Error JSON:"+e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return  null;
        }
        return responseMap;
    }
    public boolean statusConnectivity(Context ctx){
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}
