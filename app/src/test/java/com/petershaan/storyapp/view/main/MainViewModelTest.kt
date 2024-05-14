package com.petershaan.storyapp.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.petershaan.storyapp.DataDummy
import com.petershaan.storyapp.MainDispatcherRule
import com.petershaan.storyapp.data.remote.response.StoryItem
import com.petershaan.storyapp.data.repository.AuthRepository
import com.petershaan.storyapp.data.repository.StoryRepository
import com.petershaan.storyapp.getOrAwaitValue
import com.petershaan.storyapp.view.adapter.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var authRepository: AuthRepository

    @Test
    fun `when Get story Should Not Null and Return Data`() = runTest {
        val dummystory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<StoryItem> = storyPagingSource.snapshot(dummystory)
        val expectedstory = MutableLiveData<PagingData<StoryItem>>()

        expectedstory.value = data
        Mockito.`when`(storyRepository.getAllStory()).thenReturn(expectedstory)

        val mainViewModel = MainViewModel(storyRepository, authRepository)
        val actualstory: PagingData<StoryItem> = mainViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualstory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummystory.size, differ.snapshot().size)
        Assert.assertEquals(dummystory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get story Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryItem> = PagingData.from(emptyList())
        val expectedstory = MutableLiveData<PagingData<StoryItem>>()
        expectedstory.value = data
        Mockito.`when`(storyRepository.getAllStory()).thenReturn(expectedstory)

        val mainViewModel = MainViewModel(storyRepository, authRepository)
        val actualstory: PagingData<StoryItem> = mainViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualstory)

        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class storyPagingSource : PagingSource<Int, LiveData<List<StoryItem>>>() {
    companion object {
        fun snapshot(items: List<StoryItem>): PagingData<StoryItem> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryItem>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}