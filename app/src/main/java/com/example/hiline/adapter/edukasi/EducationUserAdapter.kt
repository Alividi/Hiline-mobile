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
import com.example.hiline.service.PrefManager
import com.squareup.picasso.Picasso

class EducationUserAdapter(
    private var context: Context,
    private var edukasiModels: ArrayList<EducationModel>,
    private var listener: EducationInterface
): RecyclerView.Adapter<EducationUserAdapter.MyViewHolder>() {

    private var prefManager: PrefManager = PrefManager(context)

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        val tvKategori: TextView = view.findViewById(R.id.tvKategori)
        val tvJudul: TextView = view.findViewById(R.id.tvJudul)
        val ivEdukasi: ImageView = view.findViewById(R.id.ivEdukasi)
        val clEdukasi: ConstraintLayout = view.findViewById(R.id.clEdukasi)

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
    ): EducationUserAdapter.MyViewHolder {
        val inflatedView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleritem_edukasi_user, parent, false)
        return MyViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: EducationUserAdapter.MyViewHolder, position: Int) {
        holder.tvJudul.text = edukasiModels[position].title
        holder.tvKategori.text = edukasiModels[position].name

        val imgUri = edukasiModels[position].image

        if (!imgUri.isNullOrEmpty()) {
            Picasso.get().invalidate(imgUri)
            Picasso.get().load(imgUri).into(holder.ivEdukasi)
        } else {
            holder.ivEdukasi.setImageResource(R.drawable.edukasi_def_mof)
        }

        if (edukasiModels[position].result!! > 0){
            holder.clEdukasi.setBackgroundResource(R.drawable.edukasi_bg_done)
            holder.tvKategori.setTextColor(context.resources.getColor(R.color.cool_grey))
        }else{
            if (edukasiModels[position].color == "Apricot"){
                holder.clEdukasi.setBackgroundResource(R.drawable.edukasi_bg)
                holder.tvKategori.setTextColor(context.resources.getColor(R.color.apricot))
            }
            if (edukasiModels[position].color == "Geraldine"){
                holder.clEdukasi.setBackgroundResource(R.drawable.edukasi_bg_geraldine)
                holder.tvKategori.setTextColor(context.resources.getColor(R.color.geraldine))
            }
            if (edukasiModels[position].color == "Periwinkle Blue"){
                holder.clEdukasi.setBackgroundResource(R.drawable.edukasi_bg_periwinkle_blue)
                holder.tvKategori.setTextColor(context.resources.getColor(R.color.periwinkle_blue))
            }
            if (edukasiModels[position].color == "Tufts Blue"){
                holder.clEdukasi.setBackgroundResource(R.drawable.edukasi_bg_tufts_blue)
                holder.tvKategori.setTextColor(context.resources.getColor(R.color.tufts_blue))
            }
            if (edukasiModels[position].color == "Purple"){
                holder.clEdukasi.setBackgroundResource(R.drawable.edukasi_bg_purple)
                holder.tvKategori.setTextColor(context.resources.getColor(R.color.purple))
            }
        }
    }

    override fun getItemCount(): Int {
        return edukasiModels.size
    }

}