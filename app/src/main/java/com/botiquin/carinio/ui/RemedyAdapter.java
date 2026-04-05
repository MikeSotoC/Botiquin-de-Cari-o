package com.botiquin.carinio.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.botiquin.carinio.R;
import com.botiquin.carinio.model.Remedy;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class RemedyAdapter extends RecyclerView.Adapter<RemedyAdapter.RemedyViewHolder> {

    public interface OnRemedyClick {
        void onClick(Remedy remedy);
    }

    private final List<Remedy> remedies;
    private final OnRemedyClick onRemedyClick;

    public RemedyAdapter(List<Remedy> remedies, OnRemedyClick onRemedyClick) {
        this.remedies = remedies;
        this.onRemedyClick = onRemedyClick;
    }

    @NonNull
    @Override
    public RemedyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remedy_card, parent, false);
        return new RemedyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RemedyViewHolder holder, int position) {
        Remedy remedy = remedies.get(position);
        holder.icon.setText(remedy.icon);
        holder.subtitle.setText(remedy.subtitle);
        holder.title.setText(remedy.title);
        holder.badge.setText(remedy.badge);
        holder.badge.setTextColor(remedy.color);
        holder.card.setStrokeColor(remedy.color);
        holder.topStripe.setBackgroundColor(remedy.color);
        holder.card.setCardBackgroundColor(ColorUtils.blendARGB(Color.WHITE, remedy.color, 0.04f));

        GradientDrawable badgeBg = (GradientDrawable) holder.badge.getBackground().mutate();
        badgeBg.setColor(ColorUtils.blendARGB(Color.WHITE, remedy.color, 0.15f));

        holder.itemView.setOnClickListener(v -> onRemedyClick.onClick(remedy));
    }

    @Override
    public int getItemCount() {
        return remedies.size();
    }

    static class RemedyViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        View topStripe;
        TextView icon;
        TextView subtitle;
        TextView title;
        TextView badge;

        public RemedyViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            topStripe = itemView.findViewById(R.id.topStripe);
            icon = itemView.findViewById(R.id.cardIcon);
            subtitle = itemView.findViewById(R.id.cardSubtitle);
            title = itemView.findViewById(R.id.cardTitle);
            badge = itemView.findViewById(R.id.cardBadge);
        }
    }
}
