package eu.marcocattaneo.rememberhere.presentation.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import eu.marcocattaneo.rememberhere.R;
import eu.marcocattaneo.rememberhere.business.Constants;
import eu.marcocattaneo.rememberhere.business.callback.OnListListener;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;
import eu.marcocattaneo.rememberhere.presentation.ui.CoverImageView;

public class PoiAdapter extends RecyclerView.Adapter<PoiAdapter.PoiViewHolder> {
    private List<ProximityPOI> elements;

    private OnListListener onClickListener;

    private Context mContext;

    private int lastPosition = -1;

    public PoiAdapter(Context context, List<ProximityPOI> pois, OnListListener onClickListener) {
        this.elements = pois;
        this.onClickListener = onClickListener;
        this.mContext = context;
    }

    public PoiAdapter(List<ProximityPOI> pois) {
        this.elements = pois;
    }

    @Override
    public PoiViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_poilist, viewGroup, false);

        return new PoiViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PoiViewHolder holder, int position) {

        final ProximityPOI item = getPoi(position);


        if (item != null) {

            if (item.isDone()) {
                holder.icon.setImageResource(R.drawable.ic_check_circle_black_24dp);
                holder.icon.setColorFilter(mContext.getResources().getColor(R.color.task_done_color));
            } else {
                holder.icon.setImageResource(R.drawable.ic_done_black_24dp);
                holder.icon.setColorFilter(mContext.getResources().getColor(item.isExpired() ? R.color.task_expired_color : R.color.task_new_color));
            }
            holder.title.setText(item.getNote());

            String url = String.format(Constants.STATIC_PIC,500,200,item.getLatitude(), item.getLongitude(), 15, mContext.getString(R.string.google_maps_key) );
            holder.streetPic.setCover(url, true);
        }

        if (onClickListener != null) {

            holder.streetPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClickMap(holder.itemView, item);
                }
            });

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClickDelete(holder.itemView, item);
                }
            });
        }

        setAnimation(holder.itemView, position);

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

        public TextView title;

        private CoverImageView streetPic;

        private AppCompatButton deleteButton;

        private ImageView icon;

        public PoiViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.adapter_note);

            icon = (ImageView) itemView.findViewById(R.id.adapter_icon);

            streetPic = (CoverImageView) itemView.findViewById(R.id.adapter_streetpic);

            deleteButton = (AppCompatButton) itemView.findViewById(R.id.adapter_deletebtn);
            deleteButton.setSupportBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.button_color_selector));

        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}

