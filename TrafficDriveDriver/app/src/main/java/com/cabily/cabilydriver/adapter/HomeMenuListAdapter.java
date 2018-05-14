package com.cabily.cabilydriver.adapter;

/**
 * Created by Prem Kumar on 10/1/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cabily.cabilydriver.R;
import com.cabily.cabilydriver.Utils.RoundedImageView;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


/**
 * Created by Prem Kumar on 9/21/2015.
 */
public class HomeMenuListAdapter extends BaseAdapter
{
    Context context;
    String[] mTitle;
    int[] mIcon;
    LayoutInflater inflater;
    View itemView;
    public SessionManager session;

    public HomeMenuListAdapter(Context context, String[] title, int[] icon)
    {
        this.context = context;
        this.mTitle = title;
        this.mIcon = icon;
    }

    @Override
    public int getCount()
    {
        return mTitle.length;
    }

    @Override
    public Object getItem(int position)
    {
        return mTitle[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Declare Variables
        TextView txtTitle,profile_name,profile_mobile;
        RoundedImageView profile_icon;
        ImageView imgIcon;
        RelativeLayout general_layout,profile_layout;
        View drawer_view;

        session = new SessionManager(context);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemView = inflater.inflate(R.layout.drawer_list_item, parent, false);

        txtTitle = (TextView) itemView.findViewById(R.id.title);
        profile_mobile= (TextView) itemView.findViewById(R.id.profile_mobile_number);
        imgIcon = (ImageView) itemView.findViewById(R.id.icon);
        profile_name = (TextView) itemView.findViewById(R.id.profile_name);
        profile_icon = (RoundedImageView) itemView.findViewById(R.id.profile_icon);
        general_layout = (RelativeLayout) itemView.findViewById(R.id.drawer_list_item_normal_layout);
        profile_layout = (RelativeLayout) itemView.findViewById(R.id.drawer_list_item_profile_layout);
        drawer_view=(View)itemView.findViewById(R.id.drawer_list_view);


        if(position==0)
        {

            HashMap<String, String> user = session.getUserDetails();
            String User_vehicle_model=session.getUserVehicle();
            String User_fullname=user.get(SessionManager.KEY_DRIVER_NAME);
            String driver_image=user.get(SessionManager.KEY_DRIVER_IMAGE);

            profile_layout.setVisibility(View.VISIBLE);
            general_layout.setVisibility(View.GONE);
            drawer_view.setVisibility(View.GONE);

            Picasso.with(context).load(String.valueOf(driver_image)).placeholder(R.drawable.placeholder_icon).into(profile_icon);
            profile_name.setText(User_fullname);
            profile_mobile.setText(User_vehicle_model);
        }
        else
        {

            if(position==3)
            {
                drawer_view.setVisibility(View.VISIBLE);
            }
            else
            {
                drawer_view.setVisibility(View.GONE);
            }

            profile_layout.setVisibility(View.GONE);
            general_layout.setVisibility(View.VISIBLE);


            imgIcon.setImageResource(mIcon[position]);
            txtTitle.setText(mTitle[position]);
        }

        return itemView;
    }
}

