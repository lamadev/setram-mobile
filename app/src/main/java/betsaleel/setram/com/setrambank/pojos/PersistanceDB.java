package betsaleel.setram.com.setrambank.pojos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hornellama on 06/04/2018.
 */

public class PersistanceDB extends SQLiteOpenHelper {
    private static String TABLE="t_account";
    private static String COLUMN_id="idaccount";
    private static String COLUMN_name="accountname";
    private static String COLUMN_pin="pin";
    private static String COLUMN_lang="langs";
    private static String TABLE_Logs="t_logs";

    private static final String QueryBase = "create table "
            + TABLE + " (" + COLUMN_id
            + " integer primary key , " + COLUMN_name
            + " text not null, "+ COLUMN_pin
            + " text not null, " + COLUMN_lang + " text not null);";
    private static  final String QueryLogs="create table "+TABLE_Logs+" (id integer primary key, content text not null);";
    private static final String  QueryExistDB="create table t_appstatus (id integer primary key,langs text not null) ;";
    private static String QueryJournal="create table t_current(id integer primary key,idaccount text not null) ;";
    public PersistanceDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(QueryLogs);
        sqLiteDatabase.execSQL(QueryBase);
        sqLiteDatabase.execSQL(QueryExistDB);
        sqLiteDatabase.execSQL(QueryJournal);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+TABLE+";");
        onCreate(sqLiteDatabase);
    }

}
