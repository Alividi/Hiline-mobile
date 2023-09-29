package com.example.hiline.admin.forum

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.model.CommentModel
import com.example.hiline.service.Retro
import com.example.hiline.service.ForumService
import com.example.hiline.response.CommentResponse
import com.example.hiline.response.FavResponse
import com.example.hiline.response.ReportResponse
import com.example.hiline.service.TokenAuthenticator
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengaduanKomentarActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private lateinit var idComment: String
    private lateinit var ivPPForum: ImageView
    private lateinit var ivMedal: ImageView
    private lateinit var tvNama: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvIsi: TextView
    private lateinit var checkboxLike: CheckBox
    private lateinit var tvLiked: TextView
    private lateinit var cvPPForum: CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengaduan_komentar)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val btnHapus: AppCompatButton = findViewById(R.id.btnHapus)
        cvPPForum = findViewById(R.id.cvPPForum)
        ivPPForum = findViewById(R.id.ivPPForum)
        ivMedal = findViewById(R.id.ivMedal)
        tvNama = findViewById(R.id.tvNama)
        tvUsername = findViewById(R.id.tvUsername)
        tvIsi = findViewById(R.id.tvIsi)
        checkboxLike = findViewById(R.id.checkboxLike)
        tvLiked = findViewById(R.id.tvLiked)
        idComment = intent.getStringExtra("idComment").toString()

        ViewCompat.setElevation(ivMedal,cvPPForum.elevation * 2)

        getComment()

        btnBack.setOnClickListener {
            val intent = Intent(this, PengaduanActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnHapus.setOnClickListener{
            showDeleteDialog()
        }
    }

    fun getComment(){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()
        val call = service.getReport(idComment,aToken)
        call.enqueue(object : Callback<ReportResponse> {
            override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
                if (response.isSuccessful) {
                    val reportsResponse = response.body()
                    val report = reportsResponse?.data
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    idComment = report?.comment?.id.toString()

                    val imgUri = report?.comment?.user?.image

                    if (!imgUri.isNullOrEmpty()) {
                        Picasso.get().invalidate(imgUri)
                        Picasso.get().load(imgUri).into(ivPPForum)
                    } else {
                        ivPPForum.setImageResource(R.drawable.pp_comment)
                    }

                    if(report?.comment?.user?.point?.grade == "BRONZE" || report?.comment?.user?.point?.grade == ""){
                        ivMedal.setImageResource(R.drawable.bronze_medal)
                    }
                    if(report?.comment?.user?.point?.grade == "SILVER"){
                        ivMedal.setImageResource(R.drawable.silver_medal)
                    }
                    if(report?.comment?.user?.point?.grade == "GOLD"){
                        ivMedal.setImageResource(R.drawable.gold_medal)
                    }

                    tvNama.text = report?.comment?.user?.name
                    tvUsername.text = "@"+report?.comment?.user?.username
                    tvIsi.text = report?.comment?.comment
                    tvLiked.text = report?.comment?.likeCount.toString()
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                    Log.e("Status: ", response.body()?.status.toString())
                }
            }
            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }
    fun showDeleteDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_delete_komentar)
        val btnHapus: AppCompatButton = dialog.findViewById(R.id.btnHapus)
        val btnKembali: TextView = dialog.findViewById(R.id.btnKembali)
        dialog.setTitle("Hapus Balasan")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        btnHapus.setOnClickListener {
            dialog.dismiss()
            deleteComment()
        }
        btnKembali.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun deleteComment(){
        val id = idComment
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.deleteComment(id,aToken)
        call.enqueue(object : Callback<FavResponse> {
            override fun onResponse(call: Call<FavResponse>, response: Response<FavResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    val intent = Intent(this@PengaduanKomentarActivity, PengaduanActivity::class.java)
                    startActivity(intent)
                    finish()
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
    override fun onBackPressed() {
        val intent = Intent(this, PengaduanActivity::class.java)
        startActivity(intent)
        finish()
    }
}