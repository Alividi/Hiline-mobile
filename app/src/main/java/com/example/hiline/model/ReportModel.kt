package com.example.hiline.model

class ReportModel {
    var id: String? = null
    var comment_id: String? = null
    var pId: String? = null
    var pNama: String? = null
    var pUsername: String? = null
    var pEmail: String? = null
    var pRole: String? = null
    var pTanggal_lahir: String? = null
    var pProfile_image: String? = null
    var tId: String? = null
    var tNama: String? = null
    var tUsername: String? = null
    var tEmail: String? = null
    var tRole: String? = null
    var tTanggal_lahir: String? = null
    var tProfile_image: String? = null
    var message: String? = null
    var terproses: Boolean? = null
    var jam: String? = null
    var tanggal: String? = null

    constructor()
    constructor(
        id: String?,
        comment_id: String?,
        pId: String?,
        pNama: String?,
        pUsername: String?,
        pEmail: String?,
        pRole: String?,
        pTanggal_lahir: String?,
        pProfile_image: String?,
        tId: String?,
        tNama: String?,
        tUsername: String?,
        tEmail: String?,
        tRole: String?,
        tTanggal_lahir: String?,
        tProfile_image: String?,
        message: String?,
        terproses: Boolean?,
        jam: String?,
        tanggal: String?
    ) {
        this.id = id
        this.comment_id = comment_id
        this.pId = pId
        this.pNama = pNama
        this.pUsername = pUsername
        this.pEmail = pEmail
        this.pRole = pRole
        this.pTanggal_lahir = pTanggal_lahir
        this.pProfile_image = pProfile_image
        this.tId = tId
        this.tNama = tNama
        this.tUsername = tUsername
        this.tEmail = tEmail
        this.tRole = tRole
        this.tTanggal_lahir = tTanggal_lahir
        this.tProfile_image = tProfile_image
        this.message = message
        this.terproses = terproses
        this.jam = jam
        this.tanggal = tanggal
    }


}

