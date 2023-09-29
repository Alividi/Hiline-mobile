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

class LayananRayaUserAdapter(
    private var hospitalModel: ArrayList<HospitalModel>,
    private val listener:LayananRayaInterface
) : RecyclerView.Adapter<LayananRayaUserAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        val image: ImageView = view.findViewById(R.id.ivLayanan)
        val nama: TextView = view.findViewById(R.id.tvNamaLayanan)
        val jarak: TextView = view.findViewById(R.id.tvJarak)

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
            .inflate(R.layout.recycleritem_layanan_raya_user, parent, false)
        return MyViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return hospitalModel.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nama.text = hospitalModel[position].nama
        holder.jarak.text = hospitalModel[position].jarak.toString() + " Km"

        val imgUri = hospitalModel[position].image

        if (!imgUri.isNullOrEmpty()) {
            Picasso.get().invalidate(imgUri)
            Picasso.get().load(imgUri).into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.def_hospital_img)
        }
    }
}