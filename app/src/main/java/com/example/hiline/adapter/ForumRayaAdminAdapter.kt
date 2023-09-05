package com.example.hiline.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.admin.ForumRayaEditActivity
import com.example.hiline.admin.ForumRayaInfoActivity
import com.example.hiline.api.ForumApi
import com.example.hiline.interfaces.ForumRayaInterface
import com.example.hiline.model.ForumModel
import com.example.hiline.model.ForumResponse
import com.example.hiline.model.HospitalModel
import com.example.hiline.user.ForumRayaKomentarActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumRayaAdminAdapter(
    private var activity: Activity,
    private var context: Context,
    private var forumModels: ArrayList<ForumModel>,
    private var listener: ForumRayaInterface
): RecyclerView.Adapter<ForumRayaAdminAdapter.MyViewHolder>() {

    private var prefManager: PrefManager = PrefManager(context)

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

        val tvJudulForum: TextView = view.findViewById(R.id.tvJudulForum)
        val tvIsiForum: TextView = view.findViewById(R.id.tvIsiForum)
        val checkboxFav: CheckBox = view.findViewById(R.id.checkboxFav)
        val tvFav: TextView = view.findViewById(R.id.tvFav)
        val ivKomentar: ImageView = view.findViewById(R.id.ivKomentar)
        val tvKomentar: TextView = view.findViewById(R.id.tvKomentar)
        val btnHapus: TextView = view.findViewById(R.id.btnHapus)
        val tvLine: TextView = view.findViewById(R.id.tvLine)
        val btnEdit: TextView = view.findViewById(R.id.btnEdit)

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

    fun setFilteredModels(filteredModels: ArrayList<ForumModel>) {
        forumModels = filteredModels
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflatedView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleritem_forum_raya_admin, parent, false)
        return MyViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return forumModels.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position < forumModels.size) {
            val forumModel = forumModels[position]
            holder.tvJudulForum.text = forumModel.title
            holder.tvIsiForum.text = forumModel.description
            holder.checkboxFav.isChecked = forumModel.is_favorite == true
            holder.tvFav.text = forumModel.favorite_count.toString()
            holder.tvKomentar.text = forumModel.comment_count.toString()

            holder.btnHapus.setOnClickListener {
                showDeleteDialog(position)
            }

            holder.btnEdit.setOnClickListener {
                val intent = Intent(context, ForumRayaEditActivity::class.java)
                intent.putExtra("id", forumModel.id)
                intent.putExtra("title", forumModel.title)
                intent.putExtra("description", forumModel.description)
                activity.startActivity(intent)
                activity.finish()
            }
        }
    }

    fun showDeleteDialog(position: Int){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_delete_komentar)
        val btnHapus: AppCompatButton = dialog.findViewById(R.id.btnHapus)
        val btnKembali: TextView = dialog.findViewById(R.id.btnKembali)
        val tvTitle: TextView = dialog.findViewById(R.id.tvTitle)
        val tvKet: TextView = dialog.findViewById(R.id.tvKet)
        dialog.setTitle("Hapus Forum")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        tvTitle.text = "Hapus Topik?"
        tvKet.text = "Aksi ini tidak dapat dibatalkan dan akan dihilangkan dari topik."

        btnHapus.setOnClickListener {
            deleteForum(position)
            dialog.dismiss()
        }
        btnKembali.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun deleteForum(position: Int){
        val id = forumModels[position].id.toString()
        val token = "Bearer ${prefManager.getToken()}"
        val forumApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = forumApi.deleteForum(id,token)
        call.enqueue(object : Callback<ForumResponse> {
            override fun onResponse(call: Call<ForumResponse>, response: Response<ForumResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    forumModels.removeAt(position)
                    notifyItemRemoved(position)
                    notifyDataSetChanged()
                }else{
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                }
            }
            override fun onFailure(call: Call<ForumResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
            }
        })
    }
}