package com.petershaan.storyapp


object DataDummy {
    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryEntity(
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