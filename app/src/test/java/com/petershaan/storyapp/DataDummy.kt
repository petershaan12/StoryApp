package com.petershaan.storyapp

import com.petershaan.storyapp.data.remote.response.StoryItem


object DataDummy {
    fun generateDummyStoryResponse(): List<StoryItem> {
        val items: MutableList<StoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryItem(
                i.toString(),
                "name $i",
                "photoUrl $i",
                "createdAt + $i",
                "description $i",
                0.0,
                0.0,
            )
            items.add(quote)
        }
        return items
    }
}