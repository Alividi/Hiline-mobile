package com.example.hiline.adapter.edukasi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.R
import com.example.hiline.interfaces.EducationInterface
import com.example.hiline.model.EducationModel
import com.example.hiline.model.LinkModel

class LinkUserAdapter(
    private var context: Context,
    private var linkModels: ArrayList<LinkModel>,
    private var listener: EducationInterface
):RecyclerView.Adapter<LinkUserAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvLink: TextView = view.findViewById(R.id.tvLink)
        init {
            view.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LinkUserAdapter.MyViewHolder {
        val inflatedView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleritem_edukasi_link, parent, false)
        return MyViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: LinkUserAdapter.MyViewHolder, position: Int) {
        holder.tvTitle.text = linkModels[position].sourceTitle
        holder.tvLink.text = linkModels[position].link
    }

    override fun getItemCount(): Int {
        return linkModels.size
    }
}