package com.example.gallery.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.flow.cachedIn
import com.example.gallery.data.repository.MediaRepository
import com.example.gallery.domain.model.MediaItem
import com.example.gallery.ui.home.HomeTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
) : ViewModel() {

    // Current tab selection
    private val _currentTab = MutableStateFlow(HomeTab.PHOTOS)
    val currentTab = _currentTab.asStateFlow()

    fun setTab(tab: HomeTab) {
        _currentTab.value = tab
    }

    // Paging source for current tab
    val mediaPagingData = combine(currentTab) { tab ->
        val pagingSourceFactory = when (tab) {
            HomeTab.PHOTOS -> { MediaPagingSource(mediaRepository, MediaPagingSource.Type.ALL_IMAGES) }
            HomeTab.FAVORITES -> { MediaPagingSource(mediaRepository, MediaPagingSource.Type.FAVORITES) }
            HomeTab.VIDEOS -> { MediaPagingSource(mediaRepository, MediaPagingSource.Type.VIDEOS) }
            HomeTab.SCREENSHOTS -> { MediaPagingSource(mediaRepository, MediaPagingSource.Type.SCREENSHOTS) }
            HomeTab.DOCUMENTS -> { MediaPagingSource(mediaRepository, MediaPagingSource.Type.DOCUMENTS) }
            HomeTab.FOLDERS -> { MediaPagingSource(mediaRepository, MediaPagingSource.Type.FOLDERS) }
        }

        Pager(
            config = PagingConfig(
                pageSize = 50,
                prefetchDistance = 20,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.cachedIn(viewModelScope)
    }.flattenMerge()
}

class MediaPagingSource(
    private val mediaRepository: MediaRepository,
    private val type: Type,
) : PagingSource<Int, MediaItem>() {

    enum class Type {
        ALL_IMAGES, FAVORITES, VIDEOS, SCREENSHOTS, DOCUMENTS, FOLDERS
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaItem> {
        val page = params.key ?: 1
        val limit = params.loadSize
        val offset = (page - 1) * limit

        return try {
            val items: List<MediaItem> = when (type) {
                Type.ALL_IMAGES -> mediaRepository.getMediaItemsPaged(limit, offset).first()
                Type.FAVORITES -> mediaRepository.getFavoriteMediaItems().first()
                    .drop(offset).take(limit)
                Type.VIDEOS -> mediaRepository.getVideoMediaItems().first()
                    .drop(offset).take(limit)
                Type.SCREENSHOTS -> mediaRepository.getMediaItemsByLabel("screenshot")
                    .first().drop(offset).take(limit)
                Type.DOCUMENTS -> mediaRepository.getMediaItemsByLabel("document")
                    .first().drop(offset).take(limit)
                Type.FOLDERS -> mediaRepository.getMediaItemsPaged(limit, offset).first()
            }

            LoadResult.Page(
                data = items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (items.size < limit) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MediaItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}