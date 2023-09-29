package com.example.hiline.adapter.layanan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.R
import com.example.hiline.interfaces.LayananRayaInterface
import com.example.hiline.model.HospitalModel
import com.squareup.picasso.Picasso

class LayananRayaAdminAdapter(
    private var hospitalModel: ArrayList<HospitalModel>,
    private val listener: LayananRayaInterface
) : RecyclerView.Adapter<LayananRayaAdminAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        val nama: TextView = view.findViewById(R.id.tvNamaLayanan)
        val kota: TextView = view.findViewById(R.id.tvKotaLayanan)
        val provinsi: TextView = view.findViewById(R.id.tvProvinsiLayanan)
        val img: ImageView = view.findViewById(R.id.ivLayananRaya)


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

    fun setFilteredModels(filteredModels: ArrayList<HospitalModel>) {
        //hospitalModel = filteredModels
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflatedView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleritem_layanan_raya_admin, parent, false)
        return MyViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return hospitalModel.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nama.text = hospitalModel[position].nama
        holder.kota.text = hospitalModel[position].kota
        holder.provinsi.text = hospitalModel[position].provinsi
        val imgUri = hospitalModel[position].image

        if (!imgUri.isNullOrEmpty()) {
            Picasso.get().invalidate(imgUri)
            Picasso.get().load(imgUri).into(holder.img)
        } else {
            holder.img.setImageResource(R.drawable.def_hospital_img)
        }
    }
}