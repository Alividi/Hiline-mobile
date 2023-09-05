package com.example.hiline.model

class CommentModel {
    var id: String? = null
    var idU: String? = null
    var nama: String? = null
    var username: String? = null
    var email: String? = null
    var role: String? = null
    var tanggal_lahir: String? = null
    var profile_image: String? = null
    var message: String? = null
    var like_count: Int? = null
    var liked: Boolean? = null
    var is_me: Boolean? = null

    constructor()
    constructor(
        id: String?,
        idU: String?,
        nama: String?,
        username: String?,
        email: String?,
        role: String?,
        tanggal_lahir: String?,
        profile_image: String?,
        message: String?,
        like_count: Int?,
        liked: Boolean?,
        is_me: Boolean?
    ) {
        this.id = id
        this.idU = idU
        this.nama = nama
        this.username = username
        this.email = email
        this.role = role
        this.tanggal_lahir = tanggal_lahir
        this.profile_image = profile_image
        this.message = message
        this.like_count = like_count
        this.liked = liked
        this.is_me = is_me
    }


}