package com.example.hiline.response
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommentResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: DataResponse? = null

    class DataResponse {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("author")
        @Expose
        var author: AuthorResponse? = null

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
        var comments: List<CommentItemResponse>? = null

        @SerializedName("is_favorite")
        @Expose
        var isFavorite: Boolean? = null
    }

    class AuthorResponse {
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

        @SerializedName("point")
        @Expose
        var point: PointResponse? = null
    }

    class PointResponse {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("user_id")
        @Expose
        var userId: String? = null

        @SerializedName("point")
        @Expose
        var point: Int? = null

        @SerializedName("grade")
        @Expose
        var grade: String? = null
    }

    class CommentItemResponse {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("user")
        @Expose
        var user: AuthorResponse? = null

        @SerializedName("forum_id")
        @Expose
        var forumId: String? = null

        @SerializedName("comment")
        @Expose
        var comment: String? = null

        @SerializedName("like_count")
        @Expose
        var likeCount: Int? = null

        @SerializedName("is_like")
        @Expose
        var isLike: Boolean? = null

        @SerializedName("is_me")
        @Expose
        var isMe: Boolean? = null
    }
}
