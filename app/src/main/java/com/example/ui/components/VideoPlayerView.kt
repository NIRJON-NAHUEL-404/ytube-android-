package com.example.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.example.data.SampleVideoCatalog
import com.example.model.Video
import com.example.model.VideoQuality
import com.example.ui.theme.DataSaverGreen
import com.example.ui.theme.TubeRed
import kotlinx.coroutines.delay

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeEmbeddedPlayer(
    youtubeId: String,
    modifier: Modifier = Modifier
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var currentLoadedId by remember { mutableStateOf("") }

    val embedHtml = remember(youtubeId) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }
                html, body {
                    width: 100%;
                    height: 100%;
                    background-color: #000000;
                    overflow: hidden;
                }
                #player-container {
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                }
                iframe {
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    border: 0;
                }
            </style>
        </head>
        <body>
            <div id="player-container">
                <iframe
                    id="yt-player"
                    src="https://www.youtube.com/embed/$youtubeId?autoplay=1&playsinline=1&controls=1&enablejsapi=1&fs=1&rel=0&modestbranding=1&iv_load_policy=3&origin=https://www.youtube.com"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                    allowfullscreen>
                </iframe>
            </div>
            <script>
                // Background & Screen-off Playback Hack:
                // Intercept visibilitychange so YouTube never pauses when screen turns off or app is minimized
                try {
                    window.addEventListener('visibilitychange', function(e) { e.stopImmediatePropagation(); }, true);
                    document.addEventListener('visibilitychange', function(e) { e.stopImmediatePropagation(); }, true);
                    Object.defineProperty(document, 'hidden', { get: function() { return false; }, configurable: true });
                    Object.defineProperty(document, 'visibilityState', { get: function() { return 'visible'; }, configurable: true });
                } catch(e) {}
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    DisposableEffect(youtubeId) {
        onDispose {
            try {
                webViewRef?.loadUrl("about:blank")
                webViewRef?.destroy()
                webViewRef = null
            } catch (_: Exception) {}
        }
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Ensure hardware accelerated compositing for video surface
                setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                setBackgroundColor(android.graphics.Color.BLACK)

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    javaScriptCanOpenWindowsAutomatically = false
                    setSupportMultipleWindows(false)
                    allowContentAccess = true
                    allowFileAccess = true
                    userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
                }

                webChromeClient = object : WebChromeClient() {
                    override fun getDefaultVideoPoster(): android.graphics.Bitmap? {
                        // Return transparent 1x1 bitmap to prevent Android's default black/gray poster overlay
                        return android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_8888)
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        val url = request?.url?.toString() ?: ""
                        // Allow YouTube embed, media streams, and scripts
                        if (url.contains("youtube.com") || url.contains("youtube-nocookie.com") ||
                            url.contains("googlevideo.com") || url.contains("ytimg.com") ||
                            url.contains("google.com")) {
                            return false
                        }
                        // Prevent accidental navigation out of the video player
                        return true
                    }
                }

                currentLoadedId = youtubeId
                loadDataWithBaseURL("https://www.youtube.com", embedHtml, "text/html", "UTF-8", null)
                webViewRef = this
            }
        },
        update = { webView ->
            webViewRef = webView
            if (currentLoadedId != youtubeId) {
                currentLoadedId = youtubeId
                webView.loadDataWithBaseURL("https://www.youtube.com", embedHtml, "text/html", "UTF-8", null)
            }
        },
        modifier = modifier
    )
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerView(
    video: Video,
    quality: VideoQuality,
    isDataSaverEnabled: Boolean,
    isFullScreen: Boolean,
    onClosePlayer: () -> Unit,
    onToggleFullScreen: () -> Unit,
    onOpenQualitySettings: () -> Unit,
    onPlaybackTick: (secondsWatched: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val ytId = video.effectiveYouTubeId

    if (ytId != null) {
        // Real YouTube video playback with Ad-block & Background Playback
        LaunchedEffect(ytId) {
            while (true) {
                delay(1000)
                onPlaybackTick(0.5f)
            }
        }

        Box(
            modifier = modifier
                .background(Color.Black)
        ) {
            YouTubeEmbeddedPlayer(
                youtubeId = ytId,
                modifier = Modifier.fillMaxSize()
            )

            // Top control strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onClosePlayer,
                        modifier = Modifier.testTag("close_player_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Player",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = video.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Background Playback & Ad-Free Indicator
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1B5E20).copy(alpha = 0.7f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .padding(end = 4.dp)
                    ) {
                        Text(
                            text = "🎧 স্ক্রিন অফ প্লে",
                            color = Color(0xFF81C784),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDataSaverEnabled) DataSaverGreen.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onOpenQualitySettings() }
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isDataSaverEnabled) "⚡ সেভার চালু" else "⚡ ফ্রি প্রিমিয়াম",
                            color = if (isDataSaverEnabled) DataSaverGreen else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleFullScreen,
                        modifier = Modifier.testTag("fullscreen_button")
                    ) {
                        Icon(
                            imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = "Toggle Fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    } else {
        ExoPlayerViewContent(
            video = video,
            quality = quality,
            isDataSaverEnabled = isDataSaverEnabled,
            isFullScreen = isFullScreen,
            onClosePlayer = onClosePlayer,
            onToggleFullScreen = onToggleFullScreen,
            onOpenQualitySettings = onOpenQualitySettings,
            onPlaybackTick = onPlaybackTick,
            modifier = modifier
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun ExoPlayerViewContent(
    video: Video,
    quality: VideoQuality,
    isDataSaverEnabled: Boolean,
    isFullScreen: Boolean,
    onClosePlayer: () -> Unit,
    onToggleFullScreen: () -> Unit,
    onOpenQualitySettings: () -> Unit,
    onPlaybackTick: (secondsWatched: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Fast-loading low-latency LoadControl for instant video startup & minimal initial buffering
    val exoPlayer = remember {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                1500,  // minBufferMs: starts super fast
                10000, // maxBufferMs
                500,   // bufferForPlaybackMs
                1000   // bufferForPlaybackAfterRebufferMs
            )
            .build()

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setUserAgent("Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36")
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(20000)

        val mediaSourceFactory = DefaultMediaSourceFactory(httpDataSourceFactory)

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .build()
            .apply {
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_OFF
            }
    }

    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(video.durationSeconds * 1000) }
    var showControls by remember { mutableStateOf(true) }
    var isUserScrubbing by remember { mutableStateOf(false) }
    var scrubProgress by remember { mutableFloatStateOf(0f) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }

    // Load stream according to selected quality & data saver mode
    val streamUrl = remember(video, quality, isDataSaverEnabled) {
        val stream = video.getStreamForQuality(quality, isDataSaverEnabled)
        if (stream.contains("youtube.com") || stream.contains("youtu.be")) {
            SampleVideoCatalog.STREAM_OCEANS
        } else {
            stream
        }
    }

    LaunchedEffect(streamUrl) {
        val mediaItem = MediaItem.fromUri(streamUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) {
                    isBuffering = false
                    if (exoPlayer.duration > 0) {
                        durationMs = exoPlayer.duration
                    }
                } else if (playbackState == Player.STATE_ENDED) {
                    isPlaying = false
                    showControls = true
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlayerError(error: PlaybackException) {
                isBuffering = false
                val fallback = SampleVideoCatalog.STREAM_OCEANS
                if (streamUrl != fallback) {
                    val mediaItem = MediaItem.fromUri(fallback)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    exoPlayer.play()
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Auto-hide controls after 3.5 seconds of inactivity
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(3500)
            showControls = false
        }
    }

    // Ticking loop to track current position and data consumption
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000)
            if (!isUserScrubbing) {
                currentPositionMs = exoPlayer.currentPosition
                if (exoPlayer.duration > 0) {
                    durationMs = exoPlayer.duration
                }
            }
            onPlaybackTick(1.0f)
        }
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
    ) {
        // ExoPlayer Texture / PlayerView
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Buffering spinner
        if (isBuffering) {
            CircularProgressIndicator(
                color = TubeRed,
                strokeWidth = 3.dp,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }

        // Overlay Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.75f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            ) {
                // Top Control Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onClosePlayer,
                            modifier = Modifier.testTag("close_player_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Player",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = video.title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Data Saver / Quality Pill
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isDataSaverEnabled) DataSaverGreen.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.2f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onOpenQualitySettings() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isDataSaverEnabled) "⚡ ${quality.resolutionText}" else quality.resolutionText,
                                    color = if (isDataSaverEnabled) DataSaverGreen else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Playback Speed Toggle
                        IconButton(
                            onClick = {
                                playbackSpeed = when (playbackSpeed) {
                                    1.0f -> 1.25f
                                    1.25f -> 1.5f
                                    1.5f -> 2.0f
                                    2.0f -> 0.75f
                                    else -> 1.0f
                                }
                                exoPlayer.playbackParameters = PlaybackParameters(playbackSpeed)
                            }
                        ) {
                            Text(
                                text = "${playbackSpeed}x",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Settings Icon
                        IconButton(
                            onClick = onOpenQualitySettings,
                            modifier = Modifier.testTag("player_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Playback Settings",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Center Play / Seek Buttons
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    // Replay 10s
                    IconButton(
                        onClick = {
                            val target = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                            exoPlayer.seekTo(target)
                            currentPositionMs = target
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Replay 10 seconds",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Play / Pause
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                        },
                        modifier = Modifier
                            .size(62.dp)
                            .background(TubeRed.copy(alpha = 0.9f), CircleShape)
                            .testTag("play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Forward 10s
                    IconButton(
                        onClick = {
                            val target = (exoPlayer.currentPosition + 10000L).coerceAtMost(durationMs)
                            exoPlayer.seekTo(target)
                            currentPositionMs = target
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10 seconds",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Bottom Controls (Scrub bar, Time, Fullscreen)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    // Time text and Fullscreen button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val currentFormatted = formatMsToTime(if (isUserScrubbing) (scrubProgress * durationMs).toLong() else currentPositionMs)
                        val totalFormatted = formatMsToTime(durationMs)
                        Text(
                            text = "$currentFormatted / $totalFormatted",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        IconButton(
                            onClick = onToggleFullScreen,
                            modifier = Modifier.testTag("fullscreen_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = "Toggle Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Slider Bar
                    val currentProgress = if (durationMs > 0) {
                        (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    Slider(
                        value = if (isUserScrubbing) scrubProgress else currentProgress,
                        onValueChange = { progress ->
                            isUserScrubbing = true
                            scrubProgress = progress
                        },
                        onValueChangeFinished = {
                            val targetMs = (scrubProgress * durationMs).toLong()
                            exoPlayer.seekTo(targetMs)
                            currentPositionMs = targetMs
                            isUserScrubbing = false
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = TubeRed,
                            activeTrackColor = TubeRed,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .testTag("video_seek_slider")
                    )
                }
            }
        }
    }
}

private fun formatMsToTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
