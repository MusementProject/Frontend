package com.example.musementfrontend.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.musementfrontend.R;
import com.example.musementfrontend.pojo.Ticket;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.VH> {

    public interface Listener {
        void onDelete(Ticket ticket);
        void onPreview(Ticket ticket);
    }

    private final List<Ticket> tickets;
    private final Listener listener;
    private final Context context;
    private final SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

    public TicketsAdapter(Context context, List<Ticket> tickets, Listener listener) {
        this.context = context;
        this.tickets = tickets;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_ticket, parent, false);
        return new VH(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        final Ticket t = tickets.get(pos);
        h.tvName.setText(t.getConcertArtist() + " (" + t.getConcertLocation() + ")");
        h.tvDate.setText(df.format(t.getConcertDate()));
        if ("pdf".equalsIgnoreCase(t.getFileFormat())) {
            h.ivPreview.setImageResource(R.drawable.ic_pdf);
        } else {
            Glide.with(context).load(t.getFileUrl())
                    .centerCrop().into(h.ivPreview);
        }
        h.btnDel.setOnClickListener(v -> listener.onDelete(t));
        h.ivPreview.setOnClickListener(v -> listener.onPreview(t));
    }

    @Override public int getItemCount() { return tickets.size(); }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTickets(List<Ticket> newList) {
        tickets.clear();
        tickets.addAll(newList);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivPreview;
        TextView tvName, tvDate;
        ImageButton btnDel, btnRep;
        VH(@NonNull View v) {
            super(v);
            ivPreview = v.findViewById(R.id.imgPreview);
            tvName    = v.findViewById(R.id.tvEventName);
            tvDate    = v.findViewById(R.id.tvEventDate);
            btnDel    = v.findViewById(R.id.btnDelete);
        }
    }
}
