package com.example.hiline.model

class ForumModel {
    var id: String? = null
    var idU: String? = null
    var nama: String? = null
    var username: String? = null
    var email: String? = null
    var role: String? = null
    var tanggal_lahir: String? = null
    var profile_image: String? = null
    var title: String? = null
    var description: String? = null
    var favorite_count: Int? = null
    var comment_count: Int? = null
    var is_favorite: Boolean? = null

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
        title: String?,
        description: String?,
        favorite_count: Int?,
        comment_count: Int?,
        is_favorite: Boolean?
    ) {
        this.id = id
        this.idU = idU
        this.nama = nama
        this.username = username
        this.email = email
        this.role = role
        this.tanggal_lahir = tanggal_lahir
        this.profile_image = profile_image
        this.title = title
        this.description = description
        this.favorite_count = favorite_count
        this.comment_count = comment_count
        this.is_favorite = is_favorite
    }


}