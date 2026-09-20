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

    @OptIn(ExperimentalCoroutinesApi::class)
    val videos: StateFlow<List<Video>> = combine(
        _selectedCategory,
        _searchQuery
    ) { category, query -> Pair(category, query) }
        .flatMapLatest { (category, query) ->
            repository.getVideosFlow(category, query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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
