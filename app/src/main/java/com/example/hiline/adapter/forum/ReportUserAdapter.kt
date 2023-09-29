package com.example.hiline.adapter.forum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.R
import com.example.hiline.interfaces.ReportInterface
import com.example.hiline.model.ReportModel

class ReportUserAdapter(
    private var reportModels: ArrayList<ReportModel>,
    private var listener: ReportInterface
): RecyclerView.Adapter<ReportUserAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        val tvAlasan:TextView = view.findViewById(R.id.tvAlasan)
        val tvTanggal:TextView = view.findViewById(R.id.tvTanggal)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflatedView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleritem_pengaduan_user, parent, false)
        return MyViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return reportModels.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvAlasan.text = reportModels[position].message
        holder.tvTanggal.text = reportModels[position].tanggal
    }
}