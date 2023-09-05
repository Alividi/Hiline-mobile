package com.example.hiline.model

class HospitalModel {
    var id: String? = null
    var nama: String? = null
    var kota: String? = null
    var provinsi : String? = null
    var alamat : String? = null
    var telepon : String? = null
    var image: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var jarak: Double? = null

    constructor()
    constructor(
        id: String?,
        nama: String?,
        kota: String?,
        provinsi: String?,
        alamat: String?,
        telepon: String?,
        image: String?,
        latitude: Double?,
        longitude: Double?,
        jarak: Double?
    ) {
        this.id = id
        this.nama = nama
        this.kota = kota
        this.provinsi = provinsi
        this.alamat = alamat
        this.telepon = telepon
        this.image = image
        this.latitude = latitude
        this.longitude = longitude
        this.jarak = jarak
    }


}