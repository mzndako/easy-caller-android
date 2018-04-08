package info.androidhive.materialdesign.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import info.androidhive.materialdesign.R;

/**
 * Created by HP ENVY on 5/24/2017.
 */

public class MyDialogDialer {

    public static int YES_BUTTON;
    public static int NO_BUTTON;
    public static int OK_BUTTON;

    Activity activity;
    public Dialog dialog;
    boolean yesno = false;
    TextView text, text2;
    public MyDialogDialer(Activity a)
    {
        activity = a;
    }



    public void showDialog(String phone1, String phone2) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_dial);

        text = (TextView) dialog.findViewById(R.id.text_phone1);
        text.setText(phone1);
        text.setOnClickListener(new onClickMe());

        text2 = (TextView) dialog.findViewById(R.id.text_phone2);
        text2.setText(phone2);
        text2.setOnClickListener(new onClickMe());

        dialog.show();

    }

    class onClickMe implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            onButtonClick(v);
        }
    };

    protected void onButtonClick(View v){
        TextView view = (TextView) v;
        try {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:+"+view.getText()));
            activity.startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            Log.e("Calling a Phone Number", "Call failed", activityException);
        }
        dialog.dismiss();
    }
}
