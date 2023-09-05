package com.example.hiline.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.api.ForumApi
import com.example.hiline.model.CommentModel
import com.example.hiline.model.CommentResponse
import com.example.hiline.model.ForumResponse
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumCommentAdminAdapter(
    private var activity: Activity,
    private var context: Context,
    private var commentModels: ArrayList<CommentModel>
): RecyclerView.Adapter<ForumCommentAdminAdapter.MyViewHolder>() {

    private var prefManager: PrefManager = PrefManager(context)

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val cvPPForum: CardView = view.findViewById(R.id.cvPPForum)
        val ivPPForum: ImageView = view.findViewById(R.id.ivPPForum)
        val ivMedal: ImageView = view.findViewById(R.id.ivMedal)
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvUsername: TextView = view.findViewById(R.id.tvUsername)
        val tvIsi: TextView = view.findViewById(R.id.tvIsi)
        val checkboxLike: CheckBox = view.findViewById(R.id.checkboxLike)
        val tvLiked: TextView = view.findViewById(R.id.tvLiked)
        val btnHapus: TextView = view.findViewById(R.id.btnHapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflatedView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleritem_forum_komentar_admin, parent, false)
        return MyViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return commentModels.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position < commentModels.size) {
            val imgUri = commentModels[position].profile_image

            if (!imgUri.isNullOrEmpty()) {
                Picasso.get().invalidate(imgUri)
                Picasso.get().load(imgUri).into(holder.ivPPForum)
            } else {
                holder.ivPPForum.setImageResource(R.drawable.pp_admin)
            }

            holder.tvNama.text = commentModels[position].nama
            holder.tvUsername.text ="@" + commentModels[position].username
            holder.tvIsi.text = commentModels[position].message
            holder.tvLiked.text = commentModels[position].like_count.toString()

            holder.checkboxLike.isChecked = commentModels[position].liked == true

            ViewCompat.setElevation(holder.ivMedal,holder.cvPPForum.elevation * 2)

            holder.checkboxLike.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    likeComment(position)
                    val newLikeCount = commentModels[position].like_count?.plus(1)
                    commentModels[position].like_count = newLikeCount
                    holder.tvLiked.text = newLikeCount.toString()
                } else {
                    likeComment(position)
                    val newLikeCount = commentModels[position].like_count?.minus(1)
                    commentModels[position].like_count = newLikeCount
                    holder.tvLiked.text = newLikeCount.toString()
                }
            }


            holder.btnHapus.setOnClickListener {
                showDeleteDialog(position)
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
        dialog.setTitle("Hapus Balasan")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        tvTitle.text = "Hapus Balasan?"
        tvKet.text = "Aksi ini tidak dapat dibatalkan dan akan dihilangkan dari balasan."

        btnHapus.setOnClickListener {
            deleteComment(position)
            dialog.dismiss()
        }
        btnKembali.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun deleteComment(position: Int){
        val id = commentModels[position].id.toString()
        val token = "Bearer ${prefManager.getToken()}"
        val forumApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = forumApi.deleteComment(id,token)
        call.enqueue(object : Callback<CommentResponse> {
            override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    commentModels.removeAt(position)
                    notifyDataSetChanged()
                }else{
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response unsuccessful : ", rawResponse)
                    Log.e("Code: ", response.code().toString()+response.message().toString())
                }
            }
            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
            }
        })
    }

    fun likeComment(position: Int){
        val id = commentModels[position].id.toString()
        val token = "Bearer ${prefManager.getToken()}"
        val forumApi = Retro().getRetroClientInstance().create(ForumApi::class.java)
        val call = forumApi.likeComment(id,token)

        call.enqueue(object : Callback<CommentResponse>{
            override fun onResponse(
                call: Call<CommentResponse>,
                response: Response<CommentResponse>
            ) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Code: ", response.code().toString()+response.message().toString())
                }else{
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response unsuccessful : ", rawResponse)
                    Log.e("Code: ", response.code().toString()+response.message().toString())
                }
            }
            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
            }
        })
    }
}