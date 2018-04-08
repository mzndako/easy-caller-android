package info.androidhive.materialdesign.helper;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Paint;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import info.androidhive.materialdesign.R;

/**
 * Created by HP ENVY on 5/24/2017.
 */

public class MyDialog {

    public static int YES_BUTTON;
    public static int NO_BUTTON;
    public static int OK_BUTTON;

    Activity activity;
    public Dialog dialog;
    boolean yesno = false;
    TextView text;
    public MyDialog(Activity a)
    {

        activity = a;
    }

    public void enableYesAndNo(){
        yesno = true;
    }


    public void showDialog(String msg) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_ok);
        text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);


        MyDialog.OK_BUTTON = R.id.btn_dialogok;
        Button ok = (Button) dialog.findViewById(MyDialog.OK_BUTTON);
        ok.setOnClickListener(new onClickMe());

        MyDialog.YES_BUTTON = R.id.btn_dialog_yes;
        Button yes = (Button) dialog.findViewById(R.id.btn_dialog_yes);
        yes.setOnClickListener(new onClickMe());

        MyDialog.NO_BUTTON = R.id.btn_dialog_no;
        Button no = (Button) dialog.findViewById(R.id.btn_dialog_no);
        no.setOnClickListener(new onClickMe());

        if(yesno){
            ok.setVisibility(View.INVISIBLE);

        }else{
            yes.setVisibility(View.INVISIBLE);
            no.setVisibility(View.INVISIBLE);
        }

        dialog.show();

    }

    class onClickMe implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            onButtonClick(v);
        }
    };

    protected void onButtonClick(View v){
        dialog.dismiss();
    }
}
