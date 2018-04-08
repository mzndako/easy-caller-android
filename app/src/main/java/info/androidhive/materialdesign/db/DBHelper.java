package info.androidhive.materialdesign.db;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper  extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "easycaller.db";

    public DBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here

        String CREATE_TABLE_MEMBERS = "CREATE TABLE IF NOT EXISTS `members` (\n" +
                "`id` int(11) NOT NULL,\n" +
                "  `fname` varchar(50) NOT NULL,\n" +
                "  `surname` varchar(50) NOT NULL,\n" +
                "  `mname` varchar(50) NOT NULL DEFAULT '',\n" +
                "  `phone_1` varchar(20) NOT NULL,\n" +
                "  `phone_2` varchar(20) NOT NULL DEFAULT '',\n" +
                "  `command` int(11) NOT NULL DEFAULT '0',\n" +
                "  `rank` int(11) NOT NULL DEFAULT '0',\n" +
                "  `department` int(11) NOT NULL DEFAULT '0',\n" +
                "  `position` int(11) NOT NULL DEFAULT '0',\n" +
                "  `row_version` int DEFAULT 0\n" +
                ");\n";

        db.execSQL(CREATE_TABLE_MEMBERS);

        String CREATE_TABLE_COMMAND = "CREATE TABLE IF NOT EXISTS `command` (\n" +
                "`id` int(11) NOT NULL,\n" +
                "  `name` varchar(50) NOT NULL,\n" +
                "  `address` varchar(500) NOT NULL,\n" +
                "  `state` int(11) DEFAULT '0',\n" +
                "  `lga` int(11) DEFAULT '0',\n" +
                "  `row_version` int default '0'\n" +
                ")";

        db.execSQL(CREATE_TABLE_COMMAND);

        String SQL = "\n" +
                "CREATE TABLE IF NOT EXISTS `rank` (\n" +
                "`id` int(11) NOT NULL,\n" +
                "  `name` varchar(50) NOT NULL,\n" +
                "  `abbr` varchar(10) NOT NULL,\n" +
                " `row_version` int default '0')";

        db.execSQL(SQL);

        SQL = "\n" +
                "CREATE TABLE IF NOT EXISTS `department` (\n" +
                "`id` int(11) NOT NULL,\n" +
                "  `name` varchar(50) NOT NULL,\n" +
                "  `abbr` varchar(10) NOT NULL,\n" +
                "`row_version` int default '0')";

        db.execSQL(SQL);

        SQL = "\n" +
                "CREATE TABLE IF NOT EXISTS `position` (\n" +
                "`id` int(11) NOT NULL,\n" +
                "  `name` varchar(50) NOT NULL,\n" +
                "  `abbr` varchar(10) NOT NULL,\n" +
                "`row_version` int default '0')";

        db.execSQL(SQL);

        SQL = "\n" +
                "CREATE TABLE IF NOT EXISTS `position` (\n" +
                "`id` int(11) NOT NULL,\n" +
                "  `name` varchar(50) NOT NULL,\n" +
                "  `abbr` varchar(10) NOT NULL,\n" +
                "`row_version` int default '0')";

        db.execSQL(SQL);

        SQL = "CREATE TABLE `notification` (\n" +
                "  `id` int(11) NOT NULL,\n" +
                "  `content` varchar(800) NOT NULL,\n" +
                "  `category` tinyint(4) NOT NULL DEFAULT '0',\n" +
                "  `link` varchar(200) NOT NULL DEFAULT '',\n" +
                "  `image_link` varchar(300) NOT NULL DEFAULT '',\n" +
                "  `urgent` tinyint(4) NOT NULL DEFAULT '0',\n" +
                "  `target` varchar(500) DEFAULT '',\n" +
                "  `date` int(11) NOT NULL,\n" +
                "  `read` int(11) NOT NULL DEFAULT 0,\n" +
                "  `posted_by` varchar(100) NOT NULL DEFAULT '',\n" +
                "  `row_version` int(11) NOT NULL DEFAULT '0',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ")";

        db.execSQL(SQL);

        SQL = "CREATE TABLE `recent` (\n" +
                "  `id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  `member` int(11) NOT NULL,\n" +
                "  `rank` int(11) NOT NULL DEFAULT '0',\n" +
                "  `command` int(11) NOT NULL DEFAULT '0',\n" +
                "  `department` int(11) NOT NULL DEFAULT '0',\n" +
                "  `position` int(11) NOT NULL DEFAULT '0',\n" +
                "  `type` int(11) NOT NULL DEFAULT '0',\n" +
                "  `start` int(11) NOT NULL,\n" +
                "  `end` int(11) NOT NULL,\n" +
                "  `phone` varchar(20) NOT NULL\n" +
                ")";

        db.execSQL(SQL);

        SQL = "INSERT INTO `members` (`id`, `fname`, `surname`, `mname`,  `phone_1`, `phone_2`, `command`, `rank`, `department`, `position`) VALUES\n" +
                "(1, 'Demo', 'Mr. Admin', '',   '234703000000', '', 1, 1, 3, 1),\n" +
                "(2, 'MM', 'Ndako', '', '07034634717', '09038781252', 3, 3, 4, 1),\n" +
                "(3, 'MMMMMM', 'Ndakoss', 'Middle',  '09038781253', '07034634718', 3, 3, 3, 1);\n";

//        db.execSQL(SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS members");
        db.execSQL("DROP TABLE IF EXISTS command");
        db.execSQL("DROP TABLE IF EXISTS rank");
        db.execSQL("DROP TABLE IF EXISTS department");
        db.execSQL("DROP TABLE IF EXISTS position");
        db.execSQL("DROP TABLE IF EXISTS recent");
        db.execSQL("DROP TABLE IF EXISTS notification");

        // Create tables again
        onCreate(db);

    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }

}