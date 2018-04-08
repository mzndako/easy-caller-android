package info.androidhive.materialdesign.db;

/**
 * Created by HP ENVY on 5/20/2017.
 */


        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import java.util.ArrayList;
        import java.util.HashMap;

public class database {
    private DBHelper dbHelper;
    private ContentValues values;
    SQLiteDatabase db;
    public database(Context context) {
        dbHelper = new DBHelper(context);
        values = new ContentValues();
        db = dbHelper.getWritableDatabase();
    }

    public void put(String key, String value){
        values.put(key, value);
    }


    public ContentValues getValues(){
        return values;
    }

    public int insert(String table) {
        long student_Id = db.insert(table, null, values);
        values.clear();
        return (int) student_Id;
    }

    public int insert(String table, boolean clear) {
        long student_Id = db.insert(table, null, values);
        if(clear)
            values.clear();
        return (int) student_Id;
    }

    public void clear(){
        values.clear();
    }

    public void close(){
        db.close();
    }

    public void delete(String table, String where, String[] args) {

        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(table, where, args);
        values.clear();
    }

    public void update(String table, String where, String[] args) {

        db.update(table, values, where, args);
        values.clear();
    }

    public ArrayList<HashMap<String, String>>  getRows(String selectQuery, String[] args) {
        //Open connection to read only
        ArrayList<HashMap<String, String>> rows = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, args  );
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> x = new HashMap<String, String>();
                int maxCol = cursor.getColumnCount();
                for(int i = 0; i< maxCol; i++){
                    x.put(cursor.getColumnName(i), cursor.getString(i));
                }
                rows.add(x);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return rows;

    }

    public String getLastRowVersion(String table) {

        Cursor cursor = db.rawQuery("SELECT row_version FROM "+ table+ " ORDER BY row_version DESC LIMIT 1", null  );

        if (cursor.moveToFirst()) {
            String x = cursor.getString(0);
            cursor.close();
            return x;
        }

        cursor.close();
        return "0";
    }

    public Boolean idExit(String id, String table) {

        Cursor cursor = db.rawQuery("SELECT id FROM "+ table+ " WHERE id = "+id+" LIMIT 1", null  );

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }

}
