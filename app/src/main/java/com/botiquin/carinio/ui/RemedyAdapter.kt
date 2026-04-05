package com.botiquin.carinio.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.botiquin.carinio.R
import com.botiquin.carinio.model.Remedy
import com.google.android.material.card.MaterialCardView

class RemedyAdapter(
    private val remedies: List<Remedy>,
    private val onRemedyClick: (Remedy) -> Unit
) : RecyclerView.Adapter<RemedyAdapter.RemedyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemedyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_remedy_card, parent, false)
        return RemedyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RemedyViewHolder, position: Int) {
        val remedy = remedies[position]
        holder.icon.text = remedy.icon
        holder.subtitle.text = remedy.subtitle
        holder.title.text = remedy.title
        holder.badge.text = remedy.badge
        holder.badge.setTextColor(remedy.color)
        holder.card.strokeColor = remedy.color
        holder.topStripe.setBackgroundColor(remedy.color)
        holder.card.setCardBackgroundColor(ColorUtils.blendARGB(Color.WHITE, remedy.color, 0.04f))

        val badgeDrawable = holder.badge.background
        if (badgeDrawable is GradientDrawable) {
            badgeDrawable.mutate()
            badgeDrawable.setColor(ColorUtils.blendARGB(Color.WHITE, remedy.color, 0.15f))
        }

        holder.itemView.setOnClickListener { onRemedyClick(remedy) }
    }

    override fun getItemCount(): Int = remedies.size

    class RemedyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView.findViewById(R.id.card)
        val topStripe: View = itemView.findViewById(R.id.topStripe)
        val icon: TextView = itemView.findViewById(R.id.cardIcon)
        val subtitle: TextView = itemView.findViewById(R.id.cardSubtitle)
        val title: TextView = itemView.findViewById(R.id.cardTitle)
        val badge: TextView = itemView.findViewById(R.id.cardBadge)
    }
}
