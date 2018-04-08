package info.androidhive.materialdesign.adapter;

/**
 * Created by HP ENVY on 5/18/2017.
 */

        import android.content.Context;
        import android.graphics.Color;
        import android.graphics.drawable.Drawable;
        import android.provider.SyncStateContract;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Filter;
        import android.widget.Filterable;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import info.androidhive.materialdesign.R;

        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    public List<contacts> contactsList;
    private List<contacts> filteredData = null;
    private int myview;
    private int count = 0;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, rank, command, position, department, phone_count, phone, time;
        public RelativeLayout layout;

        public MyViewHolder(View view) {
            super(view);
            rank = (TextView) view.findViewById(R.id.rank);
            name = (TextView) view.findViewById(R.id.name);
            position = (TextView) view.findViewById(R.id.position);
            phone_count = (TextView) view.findViewById(R.id.phone_count);
            phone = (TextView) view.findViewById(R.id.phone);
            time = (TextView) view.findViewById(R.id.time);
            layout = (RelativeLayout) view.findViewById(R.id.mylayout);
        }
    }



    public ContactAdapter(List<contacts> contactsList, int view) {
        this.contactsList = contactsList;
        this.filteredData = contactsList;
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
        contacts movie = contactsList.get(position);
        holder.rank.setText(movie.getRank());
        holder.name.setText(movie.getName());
        holder.position.setText(movie.getPosition()+ " ("+movie.getDepartment()+")");
        if(R.layout.contact_list_row == myview){
            holder.phone_count.setText(movie.getPhoneCount());

        }

        if(holder.time != null){
            Date d = new Date();
            d.setTime(movie.getStartTime());
            String dt = getTimeAgo(d, holder.itemView.getContext());
            holder.time.setText(dt);
        }

        if(holder.phone != null){
            holder.phone.setText(movie.getPhone());
        }

        if(holder.layout == null)
            return;

        if(position%2 == 0)
            holder.layout.setBackgroundResource(R.color.bg1);
        else
            holder.layout.setBackgroundResource(R.color.bg2);

    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public void filter(String s){
        List<contacts> newlist = new ArrayList<>();
        for(int i =0; i < filteredData.size(); i++){
            contacts mycontact = (contacts) filteredData.get(i);
            if(mycontact.toString().toLowerCase().indexOf(s.toLowerCase().trim()) > -1){
                newlist.add(mycontact);
            }
        }

        contactsList = newlist;
        notifyDataSetChanged();
        this.notifyDataSetChanged();
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
            return null;
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
}

