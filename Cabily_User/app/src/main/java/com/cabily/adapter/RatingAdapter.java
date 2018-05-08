package com.cabily.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cabily.pojo.RatingPojo;
import com.casperon.app.cabily.R;

import java.util.ArrayList;

/**
 * Created by Prem Kumar and Anitha on 11/4/2015.
 */
public class RatingAdapter extends BaseAdapter
{

    private ArrayList<RatingPojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public RatingAdapter(Context c,ArrayList<RatingPojo> d)
    {
        context=c;
        mInflater = LayoutInflater.from(context);
        data = d;
    }

    @Override
    public int getCount() {
        return data.size();
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
    public int getViewTypeCount()
    {
        return 1;
    }


    public class ViewHolder
    {
        private TextView title;
        private RatingBar rating;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View view;
        ViewHolder holder;
        if(convertView == null)
        {
            view = mInflater.inflate(R.layout.myride_rating_single, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.myride_rating_single_title);
            holder.rating = (RatingBar) view.findViewById(R.id.myride_rating_single_ratingbar);
            view.setTag(holder);
        }
        else
        {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }

        holder.title.setText(data.get(position).getRatingName());

        holder.rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                data.get(position).setRatingcount(String.valueOf(rating));
                System.out.println("rating position jai "+position);

            }
        });

        return view;
    }
}


