package com.example.data

import com.example.model.CommentEntity
import com.example.model.Video

object SampleVideoCatalog {
    // 100% verified working streams from high-speed CDNs (Cloudflare, VideoJS, W3C, Archive.org)
    const val STREAM_BUNNY = "https://media.w3.org/2010/05/bunny/trailer.mp4"
    const val STREAM_SINTEL = "https://media.w3.org/2010/05/sintel/trailer.mp4"
    const val STREAM_MOVIE300 = "https://media.w3.org/2010/05/video/movie_300.mp4"
    const val STREAM_OCEANS = "https://vjs.zencdn.net/v/oceans.mp4"
    const val STREAM_BBB_FAST = "https://www.w3schools.com/html/mov_bbb.mp4"
    const val STREAM_ELEPHANTS = "https://archive.org/download/ElephantsDream/ed_1024_512kb.mp4"
    const val STREAM_BIG_BUCK = "https://archive.org/download/BigBuckBunny_124/Content/big_buck_bunny_720p_surround.mp4"
    const val STREAM_TEARS = "https://archive.org/download/Tears-of-Steel/tears_of_steel_720p.mp4"

    val sampleVideos = listOf(
        // Bangla Songs / Music
        Video(
            id = "vid_song_1",
            title = "বাংলা গান: তুমি যাকে ভালোবাসো | Romantic Bengali Melodious Hits",
            description = "জনপ্রিয় বাংলা আধুনিক গান ও রোমান্টিক সুর। লো ডাটা মোডে কোনো বাফারিং ছাড়াই শুনুন ও উপভোগ করুন।",
            channelName = "Bangla Music Station",
            channelAvatarUrl = "https://picsum.photos/seed/banglamusic/200/200",
            subscriberCount = "2.4M",
            videoUrl = STREAM_OCEANS,
            thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=60",
            durationSeconds = 275,
            viewsCount = 1450000,
            uploadedTimeAgo = "1 day ago",
            category = "Music",
            likesCount = 98000,
            commentsCount = 1120
        ),
        Video(
            id = "vid_song_2",
            title = "চিরদিনই তুমি যে আমার | বাংলা সেরা ক্লাসিক রোমান্টিক গান",
            description = "সর্বকালের সেরা জনপ্রিয় রোমান্টিক বাংলা গান। মাত্র ৩-৪ এমবি ডাটা খরচে ফুল গান শুনুন।",
            channelName = "Sur O Chhondo BD",
            channelAvatarUrl = "https://picsum.photos/seed/surochhondo/200/200",
            subscriberCount = "1.8M",
            videoUrl = STREAM_MOVIE300,
            thumbnailUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=800&auto=format&fit=crop&q=60",
            durationSeconds = 310,
            viewsCount = 2890000,
            uploadedTimeAgo = "3 days ago",
            category = "Music",
            likesCount = 142000,
            commentsCount = 1840
        ),
        Video(
            id = "vid_song_3",
            title = "বাংলা ফোক গান ও লালন গীতি - খাঁচার ভিতর অচিন পাখি | Acoustic Folk",
            description = "মন মাতানো বাউল ও ফোক গান। একতারা ও দোতারার সুমধুর সুর। আল্ট্রা ডাটা সেভার সাপোর্টেড।",
            channelName = "Matir Shur Folk BD",
            channelAvatarUrl = "https://picsum.photos/seed/matirshur/200/200",
            subscriberCount = "950K",
            videoUrl = STREAM_BUNNY,
            thumbnailUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=800&auto=format&fit=crop&q=60",
            durationSeconds = 245,
            viewsCount = 760000,
            uploadedTimeAgo = "1 week ago",
            category = "Music",
            likesCount = 64000,
            commentsCount = 620
        ),
        Video(
            id = "vid_song_4",
            title = "রবীন্দ্র সঙ্গীত: আমার হিয়ার মাঝে লুকিয়ে ছিলে | Rabindra Sangeet Relaxing",
            description = "শান্ত ও স্নিগ্ধ রবীন্দ্র সঙ্গীত কালেকশন। পড়ার সময় বা ক্লান্তি দূর করতে নিখুঁত সুর।",
            channelName = "Rabindra Melody BD",
            channelAvatarUrl = "https://picsum.photos/seed/rabindramelody/200/200",
            subscriberCount = "620K",
            videoUrl = STREAM_SINTEL,
            thumbnailUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800&auto=format&fit=crop&q=60",
            durationSeconds = 280,
            viewsCount = 520000,
            uploadedTimeAgo = "2 weeks ago",
            category = "Music",
            likesCount = 48000,
            commentsCount = 410
        ),
        Video(
            id = "vid_song_5",
            title = "বাংলা ব্যান্ড গান: সেই তুমি কেন এতো অচেনা হলে | Acoustic Rock",
            description = "কিংবদন্তি বাংলা ব্যান্ড গান। ক্রিস্প সাউন্ড ও স্মুথ অডিও প্লেব্যাক।",
            channelName = "Bangla Rock & Band",
            channelAvatarUrl = "https://picsum.photos/seed/banglarock/200/200",
            subscriberCount = "1.5M",
            videoUrl = STREAM_BBB_FAST,
            thumbnailUrl = "https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?w=800&auto=format&fit=crop&q=60",
            durationSeconds = 340,
            viewsCount = 3100000,
            uploadedTimeAgo = "1 month ago",
            category = "Music",
            likesCount = 210000,
            commentsCount = 2950
        ),
        Video(
            id = "vid_song_6",
            title = "Top 10 Relaxing Lo-Fi Beats & Bangla Melodies to Chill / Study",
            description = "Calm lo-fi chill beats. Super lightweight audio stream designed to use minimum MB for all-day continuous playback.",
            channelName = "Lofi Vibes Studio",
            channelAvatarUrl = "https://picsum.photos/seed/lofistudio/200/200",
            subscriberCount = "3.4M",
            videoUrl = STREAM_OCEANS,
            thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=60",
            durationSeconds = 120,
            viewsCount = 2800000,
            uploadedTimeAgo = "5 days ago",
            category = "Music",
            likesCount = 180000,
            commentsCount = 1250
        ),
        Video(
            id = "vid_song_7",
            title = "মন ছুঁয়ে যাওয়া বাংলা সুফি ও ইসলামিক গজল | Heart Touching Bangla Ghazal",
            description = "হৃদয় শীতল করা সুমধুর বাংলা গজল ও নাশীদ কালেকশন। ক্লিয়ার ভোকাল সাউন্ড।",
            channelName = "Islamic Melody BD",
            channelAvatarUrl = "https://picsum.photos/seed/islamicmelody/200/200",
            subscriberCount = "880K",
            videoUrl = STREAM_MOVIE300,
            thumbnailUrl = "https://images.unsplash.com/photo-1542816417-0983c9c9ad53?w=800&auto=format&fit=crop&q=60",
            durationSeconds = 320,
            viewsCount = 1120000,
            uploadedTimeAgo = "4 days ago",
            category = "Music",
            likesCount = 95000,
            commentsCount = 890
        ),

        // Tech & Tips
        Video(
            id = "vid_2",
            title = "100 MB ইন্টারনেটে সারাদিন ভিডিও দেখার গোপন সেটিংস! Ultra Data Saver",
            description = "কিভাবে মাত্র ১০০ এমবি ডাটা দিয়ে সারাদিন ইউটিউব ভিডিও উপভোগ করবেন? অটো রেজুলেশন এবং ব্যান্ডউইডথ ক্যাশিং এর সম্পূর্ণ সমাধান।",
            channelName = "Smart Tips BD",
            channelAvatarUrl = "https://picsum.photos/seed/smarttips/200/200",
            subscriberCount = "890K",
            videoUrl = STREAM_BUNNY,
            thumbnailUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&auto=format&fit=crop&q=60",
            durationSeconds = 653,
            viewsCount = 1205000,
            uploadedTimeAgo = "3 weeks ago",
            category = "Tech",
            likesCount = 92000,
            commentsCount = 850
        ),
        Video(
            id = "vid_1",
            title = "Android 15 & Jetpack Compose 2026 Full Masterclass | Build Fast Apps",
            description = "Learn modern Android architecture, Compose UI, fast reactive state management, and extreme data-saving optimizations.",
            channelName = "Tech Bangla & Code",
            channelAvatarUrl = "https://picsum.photos/seed/techbangla/200/200",
            subscriberCount = "1.25M",
            videoUrl = STREAM_SINTEL,
            thumbnailUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&auto=format&fit=crop&q=60",
            durationSeconds = 596,
            viewsCount = 384500,
            uploadedTimeAgo = "2 days ago",
            category = "Tech",
            likesCount = 28400,
            commentsCount = 420
        ),

        // Travel
        Video(
            id = "vid_3",
            title = "Travel Vlog: সাজেক ভ্যালির মেঘের রাজ্য | Sajek Valley 4K Cinematic",
            description = "মেঘের দেশে দুই দিন! খাগড়াছড়ি থেকে সাজেক, হেলিপ্যাড থেকে সূর্যোদয়। দেখুন রোমাঞ্চকর পাহাড়ি ভ্রমণ।",
            channelName = "Bangla Explorer",
            channelAvatarUrl = "https://picsum.photos/seed/explorer/200/200",
            subscriberCount = "450K",
            videoUrl = STREAM_OCEANS,
            thumbnailUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop&q=60",
            durationSeconds = 180,
            viewsCount = 540000,
            uploadedTimeAgo = "1 month ago",
            category = "Travel",
            likesCount = 45000,
            commentsCount = 310
        ),

        // Gaming
        Video(
            id = "vid_5",
            title = "Pro Gaming Highlights 2026: Epic Clutch Moments & Tactics",
            description = "Insane gameplay reaction, clutch reflexes and world tournament finals summary. Watch at 60fps or data-saver 240p.",
            channelName = "Apex Esports BD",
            channelAvatarUrl = "https://picsum.photos/seed/apexesports/200/200",
            subscriberCount = "620K",
            videoUrl = STREAM_MOVIE300,
            thumbnailUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&auto=format&fit=crop&q=60",
            durationSeconds = 60,
            viewsCount = 780000,
            uploadedTimeAgo = "1 week ago",
            category = "Gaming",
            likesCount = 63000,
            commentsCount = 590
        ),

        // Food
        Video(
            id = "vid_6",
            title = "সহজ উপায়ে মজাদার চিকেন বিরিয়ানি রেসিপি | Easy Homemade Biryani",
            description = "ঘরে থাকা সামান্য উপকরণেই হোটেলের মতো সুস্বাদু বিরিয়ানি বানানোর নিখুঁত সিক্রেট টিপস।",
            channelName = "Rannaghor Recipes",
            channelAvatarUrl = "https://picsum.photos/seed/rannaghor/200/200",
            subscriberCount = "1.8M",
            videoUrl = STREAM_BBB_FAST,
            thumbnailUrl = "https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=800&auto=format&fit=crop&q=60",
            durationSeconds = 120,
            viewsCount = 950000,
            uploadedTimeAgo = "2 weeks ago",
            category = "Food",
            likesCount = 74000,
            commentsCount = 420
        ),

        // Animation
        Video(
            id = "vid_7",
            title = "Sintel Open Source Animated Short Film - 4K Remaster",
            description = "The lonely girl searching for her dragon companion. Masterpiece animated short film optimized for instant playback.",
            channelName = "Blender Animation Guild",
            channelAvatarUrl = "https://picsum.photos/seed/blenderguild/200/200",
            subscriberCount = "5.1M",
            videoUrl = STREAM_SINTEL,
            thumbnailUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=60",
            durationSeconds = 888,
            viewsCount = 4200000,
            uploadedTimeAgo = "3 months ago",
            category = "Animation",
            likesCount = 310000,
            commentsCount = 2400
        )
    )

    val initialComments = mapOf(
        "vid_song_1" to listOf(
            CommentEntity(
                id = "c_s101",
                videoId = "vid_song_1",
                authorName = "Anisur Rahman",
                authorAvatarUrl = "https://picsum.photos/seed/anis/100/100",
                commentText = "কী মিষ্টি গান! টিউবলাইটে একদম স্মুথ চলছে কোনো বাফারিং ছাড়াই ❤️",
                timestamp = System.currentTimeMillis() - 3600000L * 2,
                likesCount = 124,
                isLikedByMe = true
            ),
            CommentEntity(
                id = "c_s102",
                videoId = "vid_song_1",
                authorName = "Farhana Islam",
                authorAvatarUrl = "https://picsum.photos/seed/farhana/100/100",
                commentText = "অল্প এমবিতে গান শোনার জন্য এই অ্যাপ সেরা!",
                timestamp = System.currentTimeMillis() - 3600000L * 6,
                likesCount = 58,
                isLikedByMe = false
            )
        ),
        "vid_2" to listOf(
            CommentEntity(
                id = "c_201",
                videoId = "vid_2",
                authorName = "Sumon Chandra",
                authorAvatarUrl = "https://picsum.photos/seed/sumon/100/100",
                commentText = "আমি গ্রামে থাকি, ডাটা প্যাক খুব দামি। এই অ্যাপ দিয়ে ১০০ এমবি তেই সারাদিন চলতেছে!",
                timestamp = System.currentTimeMillis() - 3600000L * 2,
                likesCount = 89,
                isLikedByMe = true
            ),
            CommentEntity(
                id = "c_202",
                videoId = "vid_2",
                authorName = "Nadia Sultana",
                authorAvatarUrl = "https://picsum.photos/seed/nadia/100/100",
                commentText = "অটো রেজুলেশন ফিচারটা দারুণ। স্পিড কমলে নিজে থেকেই অপটিমাইজ করে নেয়।",
                timestamp = System.currentTimeMillis() - 3600000L * 8,
                likesCount = 27,
                isLikedByMe = false
            )
        )
    )
}
