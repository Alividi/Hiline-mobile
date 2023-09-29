package com.example.hiline.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EducationsResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data {
        @SerializedName("data")
        @Expose
        var datas: List<Datas>? = null

        @SerializedName("page")
        @Expose
        var page: Int? = null

        @SerializedName("page_size")
        @Expose
        var pageSize: Int? = null

        @SerializedName("total_page")
        @Expose
        var totalPage: Int? = null

        @SerializedName("total_data")
        @Expose
        var totalData: Int? = null
    }
    class Datas{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("category")
        @Expose
        var category: Category? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("color")
        @Expose
        var color: String? = null

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("question")
        @Expose
        var question: String? = null

        @SerializedName("answer")
        @Expose
        var answer: Boolean? = null

        @SerializedName("result")
        @Expose
        var result: Int? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

        @SerializedName("article")
        @Expose
        var article: String? = null

        @SerializedName("sources")
        @Expose
        var sources: List<Sources>? = null
    }

    class Category{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("serial")
        @Expose
        var serial: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null
    }

    class Sources{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("education_id")
        @Expose
        var education_id: String? = null

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("link")
        @Expose
        var link: String? = null
    }

}