package info.androidhive.materialdesign.adapter;

/**
 * Created by HP ENVY on 5/18/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import info.androidhive.materialdesign.R;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.MyViewHolder> {

    public List<notifications> notifyList;
    private List<notifications> filteredData = null;
    private int myview;
    private int count = 0;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView posted_by, content, date, link;
        public ImageView image; ;
        public RelativeLayout layout;

        public MyViewHolder(View view) {
            super(view);
            posted_by = (TextView) view.findViewById(R.id.posted_by);
            content = (TextView) view.findViewById(R.id.content);
            date = (TextView) view.findViewById(R.id.date);
            link = (TextView) view.findViewById(R.id.link);
            image = (ImageView) view.findViewById(R.id.image);
            layout = (RelativeLayout) view.findViewById(R.id.mylayout);
        }
    }



    public NotifyAdapter(List<notifications> notifyList, int view) {
        this.notifyList = notifyList;
        this.filteredData = notifyList;
        this.myview = view;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(myview, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        notifications nt = notifyList.get(position);

        holder.posted_by.setText(nt.getPostedBy());
        holder.content.setText(nt.getContent());

//        if(nt.getHeight() == 0) {
//            int x = holder.content.getHeight();
//            nt.setHeight(x);
//            holder.content.setHeight(50);
//        }

        Date d = new Date();
        long y = nt.getDate() * 1000L;
        d.setTime(y);
        String dt = getTimeAgo(d, holder.itemView.getContext());
        holder.date.setText(dt);

        if(!nt.getImageLink().isEmpty()){
            holder.image.setVisibility(View.VISIBLE);
            new DownloadImageTask(holder.image)
                    .execute(nt.getImageLink());
        }else{
            holder.image.setVisibility(View.GONE);
        }

        if(!nt.getLink().isEmpty()){
            holder.link.setVisibility(View.VISIBLE);
            holder.link.setText(nt.getLink());
            holder.link.setMovementMethod(LinkMovementMethod.getInstance());
        }else{
            holder.link.setVisibility(View.GONE);
        }





        if(holder.layout == null)
            return;

        if(position%2 == 0)
            holder.layout.setBackgroundResource(R.color.bg1);
        else
            holder.layout.setBackgroundResource(R.color.bg2);


        if(nt.getRead() == 0){
            holder.layout.setBackgroundResource(R.color.unread);
        }
    }


    @Override
    public int getItemCount() {
        return notifyList.size();
    }


    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String getTimeAgo(Date date, Context ctx) {

        if(date == null) {
            return null;
        }

        long time = date.getTime();

        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return date.toString();
        }

        int dim = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        if (dim == 0) {
            timeAgo = ctx.getResources().getString(R.string.date_util_term_less) + " " +  ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim == 1) {
            return "1 " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim >= 2 && dim <= 44) {
            timeAgo = dim + " " + ctx.getResources().getString(R.string.date_util_unit_minutes);
        } else if (dim >= 45 && dim <= 89) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+ctx.getResources().getString(R.string.date_util_term_an)+ " " + ctx.getResources().getString(R.string.date_util_unit_hour);
        } else if (dim >= 90 && dim <= 1439) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 60)) + " " + ctx.getResources().getString(R.string.date_util_unit_hours);
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
        } else if (dim >= 2520 && dim <= 43199) {
            timeAgo = (Math.round(dim / 1440)) + " " + ctx.getResources().getString(R.string.date_util_unit_days);
        } else if (dim >= 43200 && dim <= 86399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+ctx.getResources().getString(R.string.date_util_term_a)+ " " + ctx.getResources().getString(R.string.date_util_unit_month);
        } else if (dim >= 86400 && dim <= 525599) {
            timeAgo = (Math.round(dim / 43200)) + " " + ctx.getResources().getString(R.string.date_util_unit_months);
        } else if (dim >= 525600 && dim <= 655199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+ctx.getResources().getString(R.string.date_util_term_a)+ " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " "+ctx.getResources().getString(R.string.date_util_term_a)+ " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " + ctx.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 525600)) + " " + ctx.getResources().getString(R.string.date_util_unit_years);
        }

        return timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}