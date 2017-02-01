package bhouse.travellist_starterproject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseRetriever extends Activity {

    private SQLiteDatabase db;
    private Cursor cursor;
    private Context context;

    public DatabaseRetriever(Context context) {
        this.context = context;
    }

    public String[] getRouteDetails(String routeName) {

        try {
            SQLiteOpenHelper DatabaseHelper = new DatabaseHelper(context);
            db = DatabaseHelper.getReadableDatabase();
            String[] thisCompanyName = {routeName};
            cursor = db.query("DRINK",
                    new String[]{"_id", "NAME"},
                    "NAME = ?", thisCompanyName, null, null, null);


            /*
            CursorAdapter listAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[]{"NAME"},
                    new int[]{android.R.id.text1},
                    0);

            mList.setAdapter(listAdapter);
            mList.setOnItemClickListener(new DetailActivity.ListItemClickListener());
            */
        } catch (Exception e) {
            System.out.println("DatabaseRetriever BROKE");
            System.out.println(e);
        }


        //The test return statement
        String[] routeDetails = {routeName, "Company", "Route_number"};
        return routeDetails;
    }
}
