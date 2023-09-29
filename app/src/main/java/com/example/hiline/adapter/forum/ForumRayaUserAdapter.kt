package com.example.hiline.adapter.forum

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.service.Retro
import com.example.hiline.service.ForumService
import com.example.hiline.interfaces.ForumRayaInterface
import com.example.hiline.model.ForumModel
import com.example.hiline.response.FavResponse
import com.example.hiline.response.ForumsResponse
import com.example.hiline.service.TokenAuthenticator
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumRayaUserAdapter(
    private var context: Context,
    private var forumModels: ArrayList<ForumModel>,
    private var listener: ForumRayaInterface
) : RecyclerView.Adapter<ForumRayaUserAdapter.MyViewHolder>() {

    private var prefManager: PrefManager = PrefManager(context)

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        val tvJudulForum: TextView = view.findViewById(R.id.tvJudulForum)
        val tvNamaForum: TextView = view.findViewById(R.id.tvNamaForum)
        val tvUsernameForum: TextView = view.findViewById(R.id.tvUsernameForum)
        val ivPPForum: ImageView = view.findViewById(R.id.ivPPForum)
        val tvIsiForum: TextView = view.findViewById(R.id.tvIsiForum)
        val checkboxFav: CheckBox = view.findViewById(R.id.checkboxFav)
        val tvFavCount: TextView = view.findViewById(R.id.tvFavCount)
        val ivKomentar: ImageView = view.findViewById(R.id.ivKomentar)
        val tvKomentarCount: TextView = view.findViewById(R.id.tvKomentarCount)

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
            .inflate(R.layout.recycleritem_forum_raya_user, parent, false)
        return MyViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return forumModels.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvJudulForum.text = forumModels[position].title
        holder.tvNamaForum.text = forumModels[position].nama
        holder.tvUsernameForum.text = "@"+forumModels[position].username
        holder.tvIsiForum.text = forumModels[position].description
        holder.tvFavCount.text = forumModels[position].favorite_count.toString()
        holder.tvKomentarCount.text = forumModels[position].comment_count.toString()
        holder.checkboxFav.setOnCheckedChangeListener(null)
        holder.checkboxFav.isChecked = forumModels[position].is_favorite == true

        val imgUri = forumModels[position].profile_image

        if (!imgUri.isNullOrEmpty()) {
            Picasso.get().invalidate(imgUri)
            Picasso.get().load(imgUri).into(holder.ivPPForum)
        } else {
            holder.ivPPForum.setImageResource(R.drawable.pp_admin)
        }

        holder.checkboxFav.setOnCheckedChangeListener { _, isChecked ->
            forumModels[position].is_favorite = isChecked
            if (isChecked) {
                favForum(position)
                val newLikeCount = forumModels[position].favorite_count?.plus(1)
                forumModels[position].favorite_count = newLikeCount
                holder.tvFavCount.text = newLikeCount.toString()
            } else {
                favForum(position)
                val newLikeCount = forumModels[position].favorite_count?.minus(1)
                forumModels[position].favorite_count = newLikeCount
                holder.tvFavCount.text = newLikeCount.toString()
            }
        }
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