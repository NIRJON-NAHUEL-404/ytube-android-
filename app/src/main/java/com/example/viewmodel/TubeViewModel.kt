package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TubeLiteDatabase
import com.example.data.TubeRepository
import com.example.model.CommentEntity
import com.example.model.DataSaverSettingsEntity
import com.example.model.DownloadedVideoEntity
import com.example.model.SubscriptionEntity
import com.example.model.Video
import com.example.model.VideoQuality
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TubeTab {
    HOME,
    SUBSCRIPTIONS,
    DOWNLOADS,
    PROFILE
}

class TubeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TubeLiteDatabase.getInstance(application)
    val repository = TubeRepository(application, database)

    private val _selectedTab = MutableStateFlow(TubeTab.HOME)
    val selectedTab: StateFlow<TubeTab> = _selectedTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow("YouTube")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions.asStateFlow()

    private val _isSearchLoading = MutableStateFlow(false)
    val isSearchLoading: StateFlow<Boolean> = _isSearchLoading.asStateFlow()

    private val _currentPlayingVideo = MutableStateFlow<Video?>(null)
    val currentPlayingVideo: StateFlow<Video?> = _currentPlayingVideo.asStateFlow()

    private val _selectedQuality = MutableStateFlow(VideoQuality.AUTO)
    val selectedQuality: StateFlow<VideoQuality> = _selectedQuality.asStateFlow()

    private val _isPlayerFullScreen = MutableStateFlow(false)
    val isPlayerFullScreen: StateFlow<Boolean> = _isPlayerFullScreen.asStateFlow()

    private val _showCommentsSheet = MutableStateFlow(false)
    val showCommentsSheet: StateFlow<Boolean> = _showCommentsSheet.asStateFlow()

    private val _sessionDataUsedMb = MutableStateFlow(0.0f)
    val sessionDataUsedMb: StateFlow<Float> = _sessionDataUsedMb.asStateFlow()

    val dataSaverSettings: StateFlow<DataSaverSettingsEntity> = repository.getDataSaverSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DataSaverSettingsEntity()
        )

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var currentContinuationToken: String? = null
    private var currentResolvedQuery: String = ""
    private var currentDiscoveryOffset: Int = 0

    private val discoveryQueries = listOf(
        "Bangla Hit Songs 2026",
        "Arijit Singh Best Bangla Songs",
        "Bengali New Natok 2026",
        "Best Relaxing Lofi Beats Asia",
        "Tech Tips & Smartphone Bangla",
        "Bangladesh Beautiful Travel 4K",
        "World Gaming Highlights esports",
        "Bangla Islamic Ghazal Nasheed",
        "Viral Funny Animation Clips",
        "Top Bengali Movie Songs Remix"
    )

    init {
        viewModelScope.launch {
            combine(_selectedCategory, _searchQuery) { cat, q -> Pair(cat, q) }
                .collectLatest { (cat, q) ->
                    fetchInitialVideos(cat, q)
                }
        }
    }

    private suspend fun fetchInitialVideos(category: String, query: String) {
        _isSearchLoading.value = true
        currentContinuationToken = null
        currentDiscoveryOffset = 0
        currentResolvedQuery = repository.resolveYouTubeQuery(category, query)

        try {
            val result = repository.searchVideosWithPagination(category, query)
            _videos.value = result.videos
            currentContinuationToken = result.continuationToken
        } catch (e: Exception) {
            android.util.Log.e("TubeViewModel", "Error fetching initial videos", e)
        } finally {
            _isSearchLoading.value = false
        }
    }

    /**
     * Infinite Scrolling Loader: Automatically triggered when the user scrolls near the end
     * of the feed, fetching next pages seamlessly just like the official YouTube app.
     */
    fun loadMoreVideos() {
        if (_isLoadingMore.value || _isSearchLoading.value) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            try {
                val token = currentContinuationToken
                if (!token.isNullOrBlank()) {
                    val nextResult = repository.loadMoreYouTubeVideos(token, currentResolvedQuery)
                    if (nextResult.videos.isNotEmpty()) {
                        val existingIds = _videos.value.map { it.youtubeId }.toSet()
                        val newUnique = nextResult.videos.filter { it.youtubeId !in existingIds }
                        if (newUnique.isNotEmpty()) {
                            _videos.value = _videos.value + newUnique
                        }
                        currentContinuationToken = nextResult.continuationToken
                    } else {
                        // Fallback to Infinite Discovery so scrolling NEVER halts
                        fetchInfiniteDiscoveryBatch()
                    }
                } else {
                    // Seamless Infinite Discovery
                    fetchInfiniteDiscoveryBatch()
                }
            } catch (e: Exception) {
                android.util.Log.e("TubeViewModel", "Error loading more videos", e)
            } finally {
                _isLoadingMore.value = false
            }
        }
    }

    private suspend fun fetchInfiniteDiscoveryBatch() {
        try {
            val nextQuery = discoveryQueries[currentDiscoveryOffset % discoveryQueries.size]
            currentDiscoveryOffset++
            val result = repository.searchVideosWithPagination("YouTube", nextQuery)
            if (result.videos.isNotEmpty()) {
                val existingIds = _videos.value.map { it.youtubeId }.toSet()
                val newUnique = result.videos.filter { it.youtubeId !in existingIds }
                if (newUnique.isNotEmpty()) {
                    _videos.value = _videos.value + newUnique
                }
                currentContinuationToken = result.continuationToken
            }
        } catch (e: Exception) {
            android.util.Log.e("TubeViewModel", "Error in discovery batch", e)
        }
    }

    val subscriptions: StateFlow<List<SubscriptionEntity>> = repository.getAllSubscriptions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val downloads: StateFlow<List<DownloadedVideoEntity>> = repository.getAllDownloads()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val watchHistory: StateFlow<List<Video>> = repository.getWatchHistoryFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val likedVideos: StateFlow<List<Video>> = repository.getLikedVideosFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentComments: StateFlow<List<CommentEntity>> = _currentPlayingVideo
        .flatMapLatest { video ->
            if (video != null) {
                repository.getCommentsForVideo(video.id)
            } else {
                kotlinx.coroutines.flow.flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectTab(tab: TubeTab) {
        _selectedTab.value = tab
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.trim().length >= 2) {
                try {
                    val suggs = com.example.data.YouTubeSearchService.getSearchSuggestions(query.trim())
                    _searchSuggestions.value = suggs
                } catch (_: Exception) {
                    _searchSuggestions.value = emptyList()
                }
            } else {
                _searchSuggestions.value = emptyList()
            }
        }
    }

    fun playVideo(video: Video) {
        _currentPlayingVideo.value = video
        viewModelScope.launch {
            repository.recordWatchProgress(video.id, 0L)
        }
    }

    fun closePlayer() {
        _currentPlayingVideo.value = null
        _isPlayerFullScreen.value = false
    }

    fun setPlayerFullScreen(full: Boolean) {
        _isPlayerFullScreen.value = full
    }

    fun setSelectedQuality(quality: VideoQuality) {
        _selectedQuality.value = quality
    }

    fun setShowCommentsSheet(show: Boolean) {
        _showCommentsSheet.value = show
    }

    fun toggleLike(video: Video) {
        viewModelScope.launch {
            repository.toggleLike(video.id)
            val updated = _videos.value.map {
                if (it.id == video.id) {
                    val newLiked = !it.isLiked
                    it.copy(
                        isLiked = newLiked,
                        isDisliked = false,
                        likesCount = it.likesCount + if (newLiked) 1 else -1
                    )
                } else it
            }
            _videos.value = updated
            // Update current playing video like state if matches
            if (_currentPlayingVideo.value?.id == video.id) {
                val newLiked = !_currentPlayingVideo.value!!.isLiked
                _currentPlayingVideo.value = _currentPlayingVideo.value!!.copy(
                    isLiked = newLiked,
                    isDisliked = false,
                    likesCount = _currentPlayingVideo.value!!.likesCount + if (newLiked) 1 else -1
                )
            }
        }
    }

    fun toggleDislike(video: Video) {
        viewModelScope.launch {
            repository.toggleDislike(video.id)
            val updated = _videos.value.map {
                if (it.id == video.id) {
                    val newDisliked = !it.isDisliked
                    it.copy(
                        isDisliked = newDisliked,
                        isLiked = false
                    )
                } else it
            }
            _videos.value = updated
            if (_currentPlayingVideo.value?.id == video.id) {
                val newDisliked = !_currentPlayingVideo.value!!.isDisliked
                _currentPlayingVideo.value = _currentPlayingVideo.value!!.copy(
                    isDisliked = newDisliked,
                    isLiked = false
                )
            }
        }
    }

    fun toggleSubscribe(channelName: String, avatarUrl: String, subscriberCount: String) {
        viewModelScope.launch {
            repository.toggleSubscription(channelName, avatarUrl, subscriberCount)
            val updated = _videos.value.map {
                if (it.channelName == channelName) {
                    it.copy(isSubscribed = !it.isSubscribed)
                } else it
            }
            _videos.value = updated
            if (_currentPlayingVideo.value?.channelName == channelName) {
                val newSub = !_currentPlayingVideo.value!!.isSubscribed
                _currentPlayingVideo.value = _currentPlayingVideo.value!!.copy(isSubscribed = newSub)
            }
        }
    }

    fun addComment(videoId: String, text: String) {
        viewModelScope.launch {
            repository.addComment(videoId, text)
        }
    }

    fun toggleCommentLike(comment: CommentEntity) {
        viewModelScope.launch {
            repository.toggleCommentLike(comment)
        }
    }

    fun startFastDownload(video: Video, quality: VideoQuality = VideoQuality.Q240P) {
        viewModelScope.launch {
            repository.startFastDownload(video, quality)
        }
    }

    fun deleteDownload(downloadId: String) {
        viewModelScope.launch {
            repository.deleteDownload(downloadId)
        }
    }

    fun recordPlaybackTick(secondsWatched: Float) {
        val q = _selectedQuality.value
        val mbPerMin = if (dataSaverSettings.value.dataSaverEnabled && q == VideoQuality.AUTO) {
            0.8f // Ultra data saver rate: 100MB gives ~125 minutes of video!
        } else {
            q.estimatedMbPerMin
        }
        val mbSpent = (secondsWatched / 60.0f) * mbPerMin
        _sessionDataUsedMb.value += mbSpent

        viewModelScope.launch {
            repository.recordDataConsumption(mbSpent)
        }
    }

    fun setDataSaverEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDataSaverEnabled(enabled)
            if (enabled && _selectedQuality.value == VideoQuality.Q720P) {
                _selectedQuality.value = VideoQuality.Q240P
            }
        }
    }

    fun setDailyBudget(budgetMb: Int) {
        viewModelScope.launch {
            repository.updateDailyBudget(budgetMb)
        }
    }

    fun clearWatchHistory() {
        viewModelScope.launch {
            repository.clearWatchHistory()
        }
    }
}
