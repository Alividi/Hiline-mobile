package com.example.hiline.model

class EducationModel {
    var id: String? = null
    var image: String? = null
    var color: String? = null
    var title: String? = null
    var question: String? = null
    var answer: Boolean? = null
    var result: Int? = null
    var description: String? = null
    var article: String? = null
    var CategoryId: String? = null
    var serial: String? = null
    var name: String? = null
    var sourceId: String? = null
    var sourceTitle: String? = null
    var link: String? = null

    constructor()
    constructor(
        id: String?,
        image: String?,
        color: String?,
        title: String?,
        question: String?,
        answer: Boolean?,
        result: Int?,
        description: String?,
        article: String?,
        CategoryId: String?,
        serial: String?,
        name: String?,
    ) {
        this.id = id
        this.image = image
        this.color = color
        this.title = title
        this.question = question
        this.answer = answer
        this.result = result
        this.description = description
        this.article = article
        this.CategoryId = CategoryId
        this.serial = serial
        this.name = name
    }


}