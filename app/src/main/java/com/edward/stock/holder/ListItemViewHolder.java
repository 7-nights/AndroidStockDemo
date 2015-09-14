package com.edward.stock.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.edward.stock.R;

/**
 * Created by 朱凌峰 on 9-4.
 */
public class ListItemViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName, tvId, tvPrice, tvCent;

    public ListItemViewHolder(View itemView) {
        super(itemView);
        tvName = (TextView) itemView.findViewById(R.id.tv_name);
        tvId = (TextView) itemView.findViewById(R.id.tv_id);
        tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
        tvCent = (TextView) itemView.findViewById(R.id.tv_cent);
    }
}
