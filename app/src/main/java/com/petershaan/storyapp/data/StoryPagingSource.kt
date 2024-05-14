package com.petershaan.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.petershaan.storyapp.data.pref.UserPreference
import com.petershaan.storyapp.data.remote.response.StoryItem
import com.petershaan.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first
import java.lang.Exception

class StoryPagingSource(private val apiService: ApiService, private val userPreference: UserPreference): PagingSource<Int, StoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
        return try  {
            val token = userPreference.getToken().first()
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getAllStory("Bearer $token", position, params.loadSize, 1)

            LoadResult.Page(
                data = response.listStory.map {
                    StoryItem(it.id, it.name, it.photoUrl, it.createdAt, it.description, it.lat, it.lon)
                },
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position -1,
                nextKey = if (response.listStory.isEmpty()) null else position +1
            )
        } catch (exc: Exception) {
            return LoadResult.Error(exc)
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }


}