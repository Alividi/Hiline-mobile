package com.example.hiline.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ForumsResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: DataResponse? = null

    class DataResponse{
        @SerializedName("data")
        @Expose
        var forumData: List<ForumData>? = null

        @SerializedName("page")
        @Expose
        var page: Int = 0

        @SerializedName("page_size")
        @Expose
        var pageSize: Int = 0

        @SerializedName("total_page")
        @Expose
        var totalPage: Int = 0

        @SerializedName("total_data")
        @Expose
        var totalData: Int = 0
    }

    class ForumData{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("author")
        @Expose
        var author: Author? = null

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

        @SerializedName("favorite_count")
        @Expose
        var favoriteCount: Int? = null

        @SerializedName("comment_count")
        @Expose
        var commentCount: Int? = null

        @SerializedName("comment")
        @Expose
        var comment: List<Comment>? = null

        @SerializedName("is_favorite")
        @Expose
        var isFavorite: Boolean? = null
    }

    class Author{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("username")
        @Expose
        var username: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("tanggal_lahir")
        @Expose
        var tanggalLahir: String? = null

        @SerializedName("role")
        @Expose
        var role: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null
    }

    class Comment{

    }

}
