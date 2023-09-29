package com.example.hiline.user.profile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.service.Retro
import com.example.hiline.service.UserService
import com.example.hiline.request.EditProfileRequest
import com.example.hiline.response.CurrentResponse
import com.example.hiline.response.HospitalResponse
import com.example.hiline.response.ProfileResponse
import com.example.hiline.service.AuthService
import com.example.hiline.service.HospitalService
import com.example.hiline.service.TokenAuthenticator
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileUserEditActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private lateinit var etTanggal: EditText
    private lateinit var etNama: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var ivPP: ImageView
    private var name: String = ""
    private var username: String =""
    private var pImg: String = ""
    private var email: String = ""
    private var tglLahir: String = ""
    private var base64Img: String = ""

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
        setContentView(R.layout.activity_profile_user_edit)
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
        etTanggal = findViewById(R.id.etTanggal)

        ViewCompat.setElevation(ivPencil,cvPP.elevation * 2)

        currentUser()

        clEditPP.setOnClickListener {
            openGallery()
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, ProfileUserInfoActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSimpan.setOnClickListener {
            editProfile()
        }

        etTanggal.setOnClickListener{
            val c = Calendar.getInstance()

            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,
                { _, year, monthOfYear, dayOfMonth ->
                    val cal = Calendar.getInstance()
                    cal.set(year, monthOfYear, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                    val formattedDate = dateFormat.format(cal.time)
                    etTanggal.setText(formattedDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    fun currentUser(){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getAuthUrl(okHttpClient)
        val service = retrofit.create(AuthService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.currentUser(aToken)

        call.enqueue(object : Callback<CurrentResponse> {
            override fun onResponse(call: Call<CurrentResponse>, response: Response<CurrentResponse>) {
                if (response.isSuccessful) {
                    //val gson = GsonBuilder().setPrettyPrinting().create()
                    //val responseBody = gson.toJson(response.body())
                    //Log.e("Body: ", responseBody)
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    name = response.body()?.data?.user?.name.toString()
                    username = response.body()?.data?.user?.username.toString()
                    pImg = response.body()?.data?.user?.image.toString()
                    email = response.body()?.data?.user?.email.toString()
                    tglLahir = response.body()?.data?.user?.tanggal_lahir.toString()

                    val nama: String = name
                    etNama.text = nama.toEditable() ?: "".toEditable()
                    val usernames: String = username
                    etUsername.text = usernames.toEditable() ?: "".toEditable()
                    val emails: String = email
                    etEmail.text = emails.toEditable() ?: "".toEditable()
                    val tanggal: String = tglLahir
                    etTanggal.text = tanggal.toEditable() ?: "".toEditable()

                    val imgUri = pImg
                    loadImageAwal(imgUri)
                    if (imgUri.isNullOrEmpty()){

                    }else{
                        Picasso.get().invalidate(imgUri)
                        Picasso.get().load(imgUri).into(ivPP)
                    }

                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                }
            }
            override fun onFailure(call: Call<CurrentResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }

    fun editProfile(){
        val request = EditProfileRequest()
        request.image = "data:image/png;base64,$base64Img"
        request.name = etNama.text.toString().trim()
        request.username = etUsername.text.toString().trim()
        request.email = etEmail.text.toString().trim()
        request.tanggal_lahir = etTanggal.text.toString().trim()

        if (etNama.text.toString() == ""){
            etNama.error = "Nama wajib diisi"
            etNama.requestFocus()
        } else if (etUsername.text.toString() == ""){
            etUsername.error = "Username wajib diisi"
            etUsername.requestFocus()
        } else if(etEmail.text.toString() == ""){
            etEmail.error = "Email Wajib diisi"
            etEmail.requestFocus()
        }else if (etTanggal.text.toString() == ""){
            etTanggal.error = "tanggal lahir wajib diisi"
            etTanggal.requestFocus()
        }else {
            val tokenAuthenticator = TokenAuthenticator(prefManager)
            val okHttpClient = OkHttpClient.Builder()
                .authenticator(tokenAuthenticator)
                .build()
            val retrofit = Retro().getUserUrl(okHttpClient)
            val service = retrofit.create(UserService::class.java)
            val aToken = prefManager.getAccessToken()
            val call = service.editProfile(aToken,request)
            Log.e("Request:", call.request().toString())
            call.enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful) {
                        //val gson = GsonBuilder().setPrettyPrinting().create()
                        //val responseBody = gson.toJson(response.body())
                        //Log.e("Body: ", responseBody)
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        val profile = response.body()

                        if (profile != null){
                            prefManager.setNama(profile.data?.name.toString())
                            prefManager.setUsername(profile.data?.username.toString())
                            prefManager.setEmail(profile.data?.email.toString())
                            prefManager.setTanggal(profile.data?.tanggal_lahir.toString())
                        }
                        val intent = Intent(this@ProfileUserEditActivity, ProfileUserInfoActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        Log.e("Error: ", "unsuccessful response")
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("Network API Error: ", t.message.toString())
                    Log.e("Error: ","network or API call failure")
                }
            })
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun loadImage(imageUri: Uri) {
        val bitmap = try {
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        } catch (e: Exception) {
            null
        }

        if (bitmap != null) {
            val base64Image = bitmapToBase64(bitmap)
            base64Img = base64Image
            //Log.e("Base64: ",base64Img)
            ivPP.setImageBitmap(bitmap)
        }
    }

    private fun loadImageAwal(imageUrl: String?) {
        Picasso.get().load(imageUrl).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap != null) {
                    val base64Image = bitmapToBase64(bitmap)
                    base64Img = base64Image
                    //Log.e("Base64: ", base64Img)
                }
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                Log.e("Image Load Error", e?.message ?: "Unknown error")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        })
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun onBackPressed() {
        val intent = Intent(this, ProfileUserInfoActivity::class.java)
        startActivity(intent)
        finish()
    }
}