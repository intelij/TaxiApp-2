package com.cabily.cabilydriver.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cabily.cabilydriver.Pojo.UserPojo;
import com.cabily.cabilydriver.R;
import com.cabily.cabilydriver.Utils.RoundedImageView;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by user88 on 7/10/2017.
 */

public class UserListAdapter extends BaseAdapter {

    Activity context;
    String[] mTitle;
    int[] mIcon;
    LayoutInflater inflater;

    public SessionManager session;
    ArrayList<UserPojo> list;


    public class ViewHolder {
        TextView user_name;
        RoundedImageView user_icon;
        private LinearLayout Ll_user;
        private RelativeLayout rl;
    }

    public UserListAdapter(Activity context, ArrayList<UserPojo> d) {
        this.context = context;
        this.list = d;

    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        session = new SessionManager(context);
        View itemView;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.user_list_single, parent, false);

            holder.Ll_user=(LinearLayout) itemView.findViewById(R.id.ll);
            holder.rl=(RelativeLayout) itemView.findViewById(R.id.user_list_item_normal_layout);
            holder.user_name = (TextView) itemView.findViewById(R.id.title);
            holder.user_icon = (RoundedImageView) itemView.findViewById(R.id.icon);
            itemView.setTag(holder);
        } else {
            itemView = convertView;
            holder = (ViewHolder) itemView.getTag();
        }



        Picasso.with(context).load(String.valueOf(list.get(position).getUser_image())).placeholder(R.drawable.placeholder_icon).into(holder.user_icon);
        holder.user_name.setText(list.get(position).getUser_name());

        System.out.println("------------getActive_ride_id----------------"+list.get(position).getActive_ride_id());
        System.out.println("------------getRide_id----------------"+list.get(position).getRide_id());

        if(list.get(position).getActive_ride_id().equalsIgnoreCase(list.get(position).getRide_id()))
        {
            holder.rl.setBackgroundResource(R.drawable.background_red_border);
            holder.user_name.setTextColor(context.getResources().getColor(R.color.app_color));
        }
        else {
            holder.rl.setBackgroundResource(R.drawable.backgound_border_grey);
            holder.user_name.setTextColor(context.getResources().getColor(R.color.grey_color));
        }

        Display display = context.getWindowManager().getDefaultDisplay();
        if (list.size() == 1) {
            System.out.println("--------------jai-----------category size" + list.size());
            ViewGroup.LayoutParams params = holder.Ll_user.getLayoutParams();

            params.width = (display.getWidth())-8;
            holder.Ll_user.setLayoutParams(params);
        } else if (list.size() == 2) {
            System.out.println("--------------jai-----------category size" + list.size());

            ViewGroup.LayoutParams params = holder.Ll_user.getLayoutParams();
            params.width = (display.getWidth() / 2) - 8;
            holder.Ll_user.setLayoutParams(params);
        } else if (list.size() == 3) {
            System.out.println("--------------jai-----------category size" + list.size());
            ViewGroup.LayoutParams params = holder.Ll_user.getLayoutParams();
            params.width = (display.getWidth() / 3) - 8;
            holder.Ll_user.setLayoutParams(params);
        }





        return itemView;
    }
}
