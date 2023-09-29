package com.example.hiline.adapter.forum

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
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.service.Retro
import com.example.hiline.admin.forum.ForumRayaEditActivity
import com.example.hiline.service.ForumService
import com.example.hiline.interfaces.ForumRayaInterface
import com.example.hiline.model.ForumModel
import com.example.hiline.response.FavResponse
import com.example.hiline.response.ForumResponse
import com.example.hiline.service.TokenAuthenticator
import okhttp3.OkHttpClient
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
        //forumModels = filteredModels
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
            holder.checkboxFav.setOnCheckedChangeListener(null)
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
            holder.checkboxFav.setOnCheckedChangeListener { _, isChecked ->
                forumModel.is_favorite = isChecked
                if (isChecked) {
                    favForum(position)
                    val newLikeCount = forumModels[position].favorite_count?.plus(1)
                    forumModels[position].favorite_count = newLikeCount
                    holder.tvFav.text = newLikeCount.toString()
                } else {
                    favForum(position)
                    val newLikeCount = forumModels[position].favorite_count?.minus(1)
                    forumModels[position].favorite_count = newLikeCount
                    holder.tvFav.text = newLikeCount.toString()
                }
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
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.deleteForum(id,aToken)
        call.enqueue(object : Callback<FavResponse> {
            override fun onResponse(call: Call<FavResponse>, response: Response<FavResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    forumModels.removeAt(position)
                    notifyItemRemoved(position)
                    notifyDataSetChanged()
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                }
            }
            override fun onFailure(call: Call<FavResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }

    fun favForum(position: Int) {
        val id = forumModels[position].id.toString()
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.favForum(id,aToken)
        call.enqueue(object : Callback<FavResponse> {
            override fun onResponse(call: Call<FavResponse>, response: Response<FavResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                }
            }
            override fun onFailure(call: Call<FavResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }
}