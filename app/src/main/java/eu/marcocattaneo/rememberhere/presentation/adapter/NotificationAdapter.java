package eu.marcocattaneo.rememberhere.presentation.adapter;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import eu.marcocattaneo.rememberhere.R;
import eu.marcocattaneo.rememberhere.business.models.ProximityPOI;

public class NotificationAdapter extends BaseAdapter {

    private List<ProximityPOI> mNotifications;

    public NotificationAdapter(List<ProximityPOI> notifications) {
        this.mNotifications = notifications;
    }

    @Override
    public int getCount() {
        return mNotifications != null ? mNotifications.size() : 0;
    }

    @Override
    public ProximityPOI getItem(int i) {
        return mNotifications != null ? mNotifications.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_notification, null);
            viewHolder.note = (TextView) view.findViewById(R.id.notify_note);
            viewHolder.status = (TextView) view.findViewById(R.id.notify_status);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.note.setText(getItem(i).getNote());
        viewHolder.status.setTextColor(ContextCompat.getColor(viewGroup.getContext(), getItem(i).isDone() ? R.color.colorGreen : R.color.darkGrey));
        viewHolder.status.setText(getItem(i).isDone() ? viewGroup.getContext().getString(R.string.string_done) : viewGroup.getContext().getString(R.string.string_undone));

        return view;
    }

    private static class ViewHolder {

        private TextView note, status;

    }
}
