package com.example.hiline.adapter.forum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.R
import com.example.hiline.interfaces.ReportInterface
import com.example.hiline.model.ReportModel

class ReportAdminAdapter(
    private var reportModels: ArrayList<ReportModel>,
    private var listener: ReportInterface
): RecyclerView.Adapter<ReportAdminAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        val dotPengaduan: View = view.findViewById(R.id.dotPengaduan)
        val tvNamaPengadu: TextView = view.findViewById(R.id.tvNamaPengadu)
        val tvKet: TextView = view.findViewById(R.id.tvKet)

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
            .inflate(R.layout.recycleritem_pengaduan_admin, parent, false)
        return MyViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return reportModels.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvNamaPengadu.text = reportModels[position].pNama
        holder.tvKet.text = "${holder.tvKet.text}${reportModels[position].tUsername}"

        if (reportModels[position].terproses == true){
            holder.dotPengaduan.setBackgroundResource(R.drawable.dot_pengaduan_grey_bg)
        } else{
            holder.dotPengaduan.setBackgroundResource(R.drawable.dot_pengaduan_bg)
        }
    }
}