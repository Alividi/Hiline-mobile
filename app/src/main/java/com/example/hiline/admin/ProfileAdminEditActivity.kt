package com.example.hiline.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.api.UserApi
import com.example.hiline.model.EditProfileAdminRequest
import com.example.hiline.model.ProfileResponse
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class ProfileAdminEditActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private lateinit var etNama: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var ivPP: ImageView

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    loadImage(imageUri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_admin_edit)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val btnSimpan: AppCompatButton = findViewById(R.id.btnSimpan)
        val clEditPP: ConstraintLayout = findViewById(R.id.clEditPP)
        val cvPP: CardView = findViewById(R.id.cvPP)
        val ivPencil: ImageView = findViewById(R.id.ivPencil)
        ivPP = findViewById(R.id.ivPP)
        etNama = findViewById(R.id.etNama)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)

        ViewCompat.setElevation(ivPencil,cvPP.elevation * 2);

        val nama: String? = prefManager.getNama()
        etNama.text = nama?.toEditable() ?: "".toEditable()
        val username: String? = prefManager.getUsername()
        etUsername.text = username?.toEditable() ?: "".toEditable()
        val email: String? = prefManager.getEmail()
        etEmail.text = email?.toEditable() ?: "".toEditable()
        val imgUri = prefManager.getPImg()
        Picasso.get().invalidate(imgUri)
        Picasso.get().load(imgUri).into(ivPP)

        btnBack.setOnClickListener {
            val intent = Intent(this, ProfileAdminInfoActivity::class.java)
            startActivity(intent)
            finish()
        }

        clEditPP.setOnClickListener {
            openGallery()
        }

        btnSimpan.setOnClickListener {
            editProfile()
        }
    }
    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    fun editProfile(){
        val request = EditProfileAdminRequest()

        request.name = etNama.text.toString().trim()
        request.username = etUsername.text.toString().trim()
        request.email = etEmail.text.toString().trim()

        val retro = Retro().getRetroClientInstance().create(UserApi::class.java)
        val tokenAuth = "Bearer ${prefManager.getAccessToken()}"

        if (etNama.text.toString() == ""){
            etNama.error = "Nama wajib diisi"
            etNama.requestFocus()
        } else if (etUsername.text.toString() == ""){
            etUsername.error = "Username wajib diisi"
            etUsername.requestFocus()
        } else if(etEmail.text.toString() == ""){
            etEmail.error = "Email Wajib diisi"
            etEmail.requestFocus()
        }else {
            retro.editProfileAdmin(tokenAuth, request).enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    val profile = response.body()

                    if (profile != null){
                        prefManager.setNama(profile.data?.nama.toString())
                        prefManager.setUsername(profile.data?.username.toString())
                        prefManager.setEmail(profile.data?.email.toString())

                    }
                    uploadImage()
                    val intent = Intent(this@ProfileAdminEditActivity, ProfileAdminInfoActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("Error: ", t.message ?: "Error not found")
                }
            })
        }
    }

    fun uploadImage(){
        val retro = Retro().getRetroClientInstance().create(UserApi::class.java)
        val tokenAuth = "Bearer ${prefManager.getAccessToken()}"

        val drawable = ivPP.drawable
        val bitmap = (drawable as BitmapDrawable).bitmap

        val file = File(applicationContext.cacheDir, "image.png")
        file.createNewFile()

        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

        retro.uploadImage(tokenAuth, imagePart).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    val profileResponse = response.body()
                    if (profileResponse != null) {
                        val imageUrl = profileResponse.data?.profile_image
                        Log.e("Img : ", imageUrl.toString())
                        prefManager.setPImg(imageUrl.toString())
                    }
                } else {
                    Log.e("Error: ", "Error not Successful")
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e("Error: ", t.message ?: "Error not found")
            }
        })
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun loadImage(imageUri: Uri) {
        Picasso.get().invalidate(imageUri)
        Picasso.get().load(imageUri).into(ivPP)
    }

    override fun onBackPressed() {
        val intent = Intent(this, ProfileAdminInfoActivity::class.java)
        startActivity(intent)
        finish()
    }
}