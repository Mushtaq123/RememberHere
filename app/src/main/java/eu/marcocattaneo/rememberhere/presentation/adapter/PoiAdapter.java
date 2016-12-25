package eu.marcocattaneo.rememberhere.presentation.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.marcocattaneo.rememberhere.business.callback.OnPoiListListener;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;

public class PoiAdapter extends RecyclerView.Adapter<PoiAdapter.PoiViewHolder> {
    private List<ProximityPOI> elements;

    private OnPoiListListener onClickListener;

    public PoiAdapter(List<ProximityPOI> pois, OnPoiListListener onClickListener) {
        this.elements = pois;
        this.onClickListener = onClickListener;
    }

    public PoiAdapter(List<ProximityPOI> pois) {
        this.elements = pois;
    }

    @Override
    public PoiViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(android.R.layout.simple_list_item_1, viewGroup, false);

        return new PoiViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PoiViewHolder holder, int position) {

        final ProximityPOI item = getPoi(position);

        if (item != null) {
            holder.title.setText(item.getNote());
        }

        if (onClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(holder.itemView, item);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onClickListener.onLongPress(holder.itemView, item);
                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return elements != null ? elements.size() : 0;
    }

    /**
     * Return post by position
     *
     * @param position
     * @return
     */
    @Nullable
    public ProximityPOI getPoi(int position) {

        return position < getItemCount() ? elements.get(position) : null;
    }

    public void swapItems(List<ProximityPOI> data) {
        elements = data;
        notifyDataSetChanged();
    }

    public static class PoiViewHolder extends RecyclerView.ViewHolder {

        public TextView title, meta;

        public PoiViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(android.R.id.text1);

        }
    }

}

