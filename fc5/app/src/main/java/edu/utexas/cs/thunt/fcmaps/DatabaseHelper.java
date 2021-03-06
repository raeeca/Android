package edu.utexas.cs.thunt.fcmaps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhitingz on 9/9/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "restaurants.db";
    private static String DB_PATH;
    private SQLiteDatabase db;
    private final Context context;
    private static int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void copyDatabase() throws IOException {
        InputStream input = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream output = new FileOutputStream(outFileName);

        byte[] buf = new byte[4096];
        int len;
        while ((len = input.read(buf)) > 0) {
            output.write(buf, 0, len);
        }
        output.flush();
        output.close();
        input.close();
    }

    private boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        boolean exist = false;
        try {
            String path = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.e("DB", "Database doesn't exist");
        }

        if (checkDB != null) {
            checkDB.close();
            exist = true;
        }
        return exist;
    }

    public void createDatabase() throws IOException {
        boolean exist = checkDatabase();

        //if (!exist) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new IOException("Fail to copy database");
            }
        //}
    }

    @Override
    public synchronized void close() {
        if (db != null)
            db.close();
        super.close();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }
}
