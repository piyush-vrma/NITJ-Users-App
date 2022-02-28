package com.nitj.nitj.models.gallery
import java.util.ArrayList

data class GalleryEvent(
    var eventName: String = "",
    var arrayList: ArrayList<GalleryData>
)