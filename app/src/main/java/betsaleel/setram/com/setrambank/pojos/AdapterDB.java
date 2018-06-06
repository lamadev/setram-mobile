package betsaleel.setram.com.setrambank.pojos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by hornellama on 06/04/2018.
 */

public class AdapterDB {

    Context ctx;
    PersistanceDB database;
    public AdapterDB(Context context){
        this.ctx=context;
        database=new PersistanceDB(this.ctx,"setram.db",null,5);
    }
    SQLiteDatabase dbClient=null;
    public boolean OpenDB(){
        this.dbClient=database.getWritableDatabase();
        return true;
    }
    public boolean CloseDB(){
        dbClient.close();
        return true;
    }

    public List<Map<String,String>>getListAccount(){
        Cursor c=dbClient.rawQuery("SELECT * FROM t_account",null);
        List<Map<String,String>>lst=new LinkedList<>();
        while (c.moveToNext()){
            Map<String,String> mapper=new HashMap<>();
            mapper.put("id",Integer.toString(c.getInt(0)));
            mapper.put("name",c.getString(1));
            mapper.put("pin",c.getString(2));
            mapper.put("lang",c.getString(3));

            lst.add(mapper);
        }
        return lst;
    }

    public long AddAccount(int id,String numaccount,String pin,String lang){

        ContentValues contents=new ContentValues();
        contents.put("idaccount",id);
        contents.put("accountname",numaccount);
        contents.put("pin",pin);
        contents.put("langs",lang);
        return dbClient.insert("t_account",null,contents);
    }
    public long addAppStatus(String lang){
        ContentValues cv=new ContentValues();
        cv.put("id",1);
        cv.put("langs",lang);
        return dbClient.insert("t_appstatus",null,cv);
    }
    public long UpdateAccount(int id,String pin){
        ContentValues contents=new ContentValues();
        contents.put("idaccount",id);
        contents.put("pin",pin);
        return  dbClient.update("t_account",contents,"id=?",new String[]{Integer.toString(id)});
    }
    public long AddContentTransaction(String logs,int id){
       // Toast.makeText(ctx, "Logs data: "+logs, Toast.LENGTH_SHORT).show();
        ContentValues contents=new ContentValues();
        contents.put("id",id);
        contents.put("content",logs);
        return dbClient.insert("t_logs",null,contents);
    }
    public long addCurrentUser(String id){
        ContentValues contents=new ContentValues();
        contents.put("id",1);
        contents.put("idaccount",id);
        return dbClient.insert("t_current",null,contents);
    }
    public String getLogs(String id){
        String data="";
        String query="SELECT * FROM t_logs WHERE id=?";
        Cursor c=dbClient.rawQuery(query,new String[]{id});
        List<String>lst=new LinkedList<>();
        while (c.moveToNext()){
           data=c.getString(1);
        }
        //c.close();
        return data;
    }
    public String getCurrentUser(){
        String data="";
        Cursor c=dbClient.rawQuery("SELECT * FROM t_current",null);
        while (c.moveToNext()){
            data=c.getString(1);
            //Toast.makeText(ctx, "ICI:"+c.getString(1), Toast.LENGTH_SHORT).show();
        }
        //c.close();
        return data;
    }
    public boolean UpdateKeyPin(String oldpin,String newpin){
        ContentValues cv = new ContentValues();
        cv.put("pin",newpin);
        dbClient.update("t_account",cv,"pin="+oldpin,null);
        return true;
    }


    public boolean UpdateCurrentUser(String newid){
        ContentValues cv = new ContentValues();
        cv.put("idaccount",newid);
        dbClient.update("t_current",cv,"id="+"1",null);
        return true;
    }
    public String getCurrentLanguage(String id){
        String data="";
        if (!id.equals("")){

            Cursor c=dbClient.rawQuery("SELECT * FROM t_account WHERE idaccount=?",new String[]{id});
            List<String>lst=new LinkedList<>();
            while (c.moveToNext()){
                data=c.getString(3);
            }
        }else{
            Cursor c=dbClient.rawQuery("SELECT * FROM t_account",null);
            List<String>lst=new LinkedList<>();
            while (c.moveToNext()){
                data=c.getString(3);
            }
        }

        //c.close();
        return data;
    }
    public Map getAppStatus(){
        Map<String,String>map=new HashMap<>();
        Cursor c=dbClient.rawQuery("SELECT * FROM t_appstatus",null);
        List<String>lst=new LinkedList<>();
        while (c.moveToNext()){
            map.put("id",c.getString(0));
            map.put("langs",c.getString(1));
        }
        //c.close();
        return map;
    }

    public boolean UpdateLanguage(String id,String language){
        ContentValues cv = new ContentValues();
        cv.put("langs",language);
        dbClient.update("t_account",cv,"idaccount="+id,null);
        dbClient.update("t_appstatus",cv,"id="+"1",null);
        return true;
    }

    public boolean UpdateLogs(String content,String id){
        ContentValues cv = new ContentValues();
        cv.put("content",content);
        dbClient.update("t_logs",cv,"id="+id,null);

        return true;
    }
    public boolean RemoveLogs(String id){
        dbClient.delete("t_logs","id=?",new String[]{id});
        return true;
    }
    public boolean deleteAccount(String id){
        dbClient.delete("t_account","idaccount=?",new String[]{id});
        return true;
    }

}
