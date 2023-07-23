package com.dicoding.storyappdicoding

import com.dicoding.storyappdicoding.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-1684844868317_ua2_GfCz.jpg",
                "2023-05-23T12:27:48.318Z",
                "name $i",
                "description $i",
                112.55306,
                "$i",
                -7.896508


            )
           items.add(story)
        }
        return items
    }
}