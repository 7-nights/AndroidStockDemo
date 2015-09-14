package com.edward.stock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edward.stock.R;
import com.edward.stock.StockApplication;
import com.edward.stock.holder.ListItemViewHolder;
import com.edward.stock.model.ListItemNewestInfo;

import java.util.List;

/**
 * Created by 朱凌峰 on 9-4.
 */
public class ListAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

    private Context context;
    private List<ListItemNewestInfo> data;
    private OnItemClickLitener onItemClickLitener;

    public ListAdapter(Context context, List<ListItemNewestInfo> data) {
        this.context = context;
        this.data = data;
    }

    public void setOnItemClickLitener(OnItemClickLitener onItemClickLitener) {
        this.onItemClickLitener = onItemClickLitener;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemViewHolder holder = new ListItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ListItemViewHolder holder, int position) {
        holder.tvName.setText(data.get(position).getName());
        holder.tvId.setText(data.get(position).getStockId());
        holder.tvPrice.setText(data.get(position).getLastPrice());
        if (data.get(position).isHalting()) {
            holder.tvCent.setBackgroundColor(StockApplication.getContextAnywhere().getResources().getColor(R.color.grey_blue));
            holder.tvCent.setText("停牌");
        } else {
            switch (data.get(position).getStatus()) {
                case -1:
                    holder.tvCent.setBackgroundColor(StockApplication.getContextAnywhere().getResources().getColor(R.color.green_dark));
                    holder.tvCent.setText(data.get(position).getLastDifferenceRate() + "%");
                    break;
                case 1:
                    holder.tvCent.setBackgroundColor(StockApplication.getContextAnywhere().getResources().getColor(R.color.red_dark));
                    holder.tvCent.setText("+" + data.get(position).getLastDifferenceRate() + "%");
                    break;
                default:
                    holder.tvCent.setBackgroundColor(StockApplication.getContextAnywhere().getResources().getColor(R.color.grey_blue));
                    holder.tvCent.setText("0.00%");
            }
        }
        if (onItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickLitener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }
}
