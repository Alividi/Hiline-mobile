package com.example.hiline.model

class LinkModel {
    var educationId: String? = null
    var sourceId: String? = null
    var sourceTitle: String? = null
    var link: String? = null

    constructor()
    constructor(educationId: String?, sourceId: String?, sourceTitle: String?, link: String?) {
        this.educationId = educationId
        this.sourceId = sourceId
        this.sourceTitle = sourceTitle
        this.link = link
    }


}