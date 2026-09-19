package com.example.data

import com.example.model.CommentEntity
import com.example.model.Video

object SampleVideoCatalog {
    // 100% verified working streams from high-speed CDNs (fallback for offline or non-YouTube players)
    const val STREAM_BUNNY = "https://media.w3.org/2010/05/bunny/trailer.mp4"
    const val STREAM_SINTEL = "https://media.w3.org/2010/05/sintel/trailer.mp4"
    const val STREAM_MOVIE300 = "https://media.w3.org/2010/05/video/movie_300.mp4"
    const val STREAM_OCEANS = "https://vjs.zencdn.net/v/oceans.mp4"
    const val STREAM_BBB_FAST = "https://www.w3schools.com/html/mov_bbb.mp4"

    val sampleVideos = listOf(
        // Bangla Songs / Music (Real YouTube IDs)
        Video(
            id = "vid_song_2",
            title = "চিরদিনই তুমি যে আমার | বাংলা সেরা ক্লাসিক রোমান্টিক গান",
            description = "সর্বকালের সেরা জনপ্রিয় রোমান্টিক বাংলা গান। মাত্র ৩-৪ এমবি ডাটা খরচে ফুল গান শুনুন।",
            channelName = "Sur O Chhondo BD",
            channelAvatarUrl = "https://picsum.photos/seed/surochhondo/200/200",
            subscriberCount = "1.8M",
            videoUrl = "https://www.youtube.com/watch?v=t33M1Rj83nU",
            thumbnailUrl = "https://i.ytimg.com/vi/t33M1Rj83nU/hqdefault.jpg",
            durationSeconds = 320,
            viewsCount = 129800000,
            uploadedTimeAgo = "3 days ago",
            category = "Music",
            youtubeId = "t33M1Rj83nU",
            likesCount = 142000,
            commentsCount = 1840
        ),
        Video(
            id = "vid_song_1",
            title = "Mon Majhi Khobordar | মন মাঝি খবরদার | Best Viral Song",
            description = "জনপ্রিয় বাংলা আধুনিক গান ও রোমান্টিক সুর। লো ডাটা মোডে কোনো বাফারিং ছাড়াই শুনুন ও উপভোগ করুন।",
            channelName = "Vidya Bhushan Sarkar",
            channelAvatarUrl = "https://picsum.photos/seed/banglamusic/200/200",
            subscriberCount = "2.4M",
            videoUrl = "https://www.youtube.com/watch?v=YGnctmSC3z8",
            thumbnailUrl = "https://i.ytimg.com/vi/YGnctmSC3z8/hqdefault.jpg",
            durationSeconds = 290,
            viewsCount = 17620000,
            uploadedTimeAgo = "1 day ago",
            category = "Music",
            youtubeId = "YGnctmSC3z8",
            likesCount = 98000,
            commentsCount = 1120
        ),
        Video(
            id = "vid_song_3",
            title = "একটা হাওয়ার গাড়ি | Ekta Hawar Gari | Bangla Folk Song",
            description = "মন মাতানো লোকগীতি ও ফোক গান। একতারা ও দোতারার সুমধুর সুর। আল্ট্রা ডাটা সেভার সাপোর্টেড।",
            channelName = "Sham Gaan Official",
            channelAvatarUrl = "https://picsum.photos/seed/matirshur/200/200",
            subscriberCount = "950K",
            videoUrl = "https://www.youtube.com/watch?v=mvUNTnHk07k",
            thumbnailUrl = "https://i.ytimg.com/vi/mvUNTnHk07k/hqdefault.jpg",
            durationSeconds = 314,
            viewsCount = 760000,
            uploadedTimeAgo = "1 week ago",
            category = "Music",
            youtubeId = "mvUNTnHk07k",
            likesCount = 64000,
            commentsCount = 620
        ),
        Video(
            id = "vid_song_4",
            title = "Du Haatey Mutho Bhore | Bengali Sad Romantic Song",
            description = "শান্ত ও স্নিগ্ধ বাংলা গান কালেকশন। পড়ার সময় বা ক্লান্তি দূর করতে নিখুঁত সুর।",
            channelName = "Rudra Kanya Oishwarja",
            channelAvatarUrl = "https://picsum.photos/seed/rabindramelody/200/200",
            subscriberCount = "620K",
            videoUrl = "https://www.youtube.com/watch?v=O7WnA8AU4O8",
            thumbnailUrl = "https://i.ytimg.com/vi/O7WnA8AU4O8/hqdefault.jpg",
            durationSeconds = 183,
            viewsCount = 10250000,
            uploadedTimeAgo = "2 weeks ago",
            category = "Music",
            youtubeId = "O7WnA8AU4O8",
            likesCount = 48000,
            commentsCount = 410
        ),
        Video(
            id = "vid_song_5",
            title = "জীবনের হিসাব মিলে না | Jiboner Hisab Mile Na | New Folk Song",
            description = "কিংবদন্তি ফোক গান। ক্রিস্প সাউন্ড ও স্মুথ অডিও প্লেব্যাক।",
            channelName = "Nupur Music",
            channelAvatarUrl = "https://picsum.photos/seed/banglarock/200/200",
            subscriberCount = "1.5M",
            videoUrl = "https://www.youtube.com/watch?v=6y2pKyG7KSc",
            thumbnailUrl = "https://i.ytimg.com/vi/6y2pKyG7KSc/hqdefault.jpg",
            durationSeconds = 322,
            viewsCount = 3100000,
            uploadedTimeAgo = "1 month ago",
            category = "Music",
            youtubeId = "6y2pKyG7KSc",
            likesCount = 210000,
            commentsCount = 2950
        ),
        Video(
            id = "vid_song_6",
            title = "Top Relaxing Lo-Fi Beats & Chill Melodies to Study",
            description = "Calm lo-fi chill beats. Super lightweight stream designed to use minimum MB for all-day continuous playback.",
            channelName = "Lofi Girl Studio",
            channelAvatarUrl = "https://picsum.photos/seed/lofistudio/200/200",
            subscriberCount = "14.4M",
            videoUrl = "https://www.youtube.com/watch?v=jfKfPfyJRdk",
            thumbnailUrl = "https://i.ytimg.com/vi/jfKfPfyJRdk/hqdefault.jpg",
            durationSeconds = 600,
            viewsCount = 45000000,
            uploadedTimeAgo = "5 days ago",
            category = "Music",
            youtubeId = "jfKfPfyJRdk",
            likesCount = 180000,
            commentsCount = 1250
        ),

        // Ghazal / Islamic
        Video(
            id = "vid_gazal_1",
            title = "তোমরা যদি যাওগো মদিনায় । সালাতু সালাম গো আমার । Salatu Salam Go Amar",
            description = "হৃদয় শীতল করা সুমধুর বাংলা গজল ও নাশীদ কালেকশন। ক্লিয়ার ভোকাল সাউন্ড।",
            channelName = "Nasheed Studio",
            channelAvatarUrl = "https://picsum.photos/seed/islamicmelody/200/200",
            subscriberCount = "880K",
            videoUrl = "https://www.youtube.com/watch?v=anzhRV7Qq2M",
            thumbnailUrl = "https://i.ytimg.com/vi/anzhRV7Qq2M/hqdefault.jpg",
            durationSeconds = 142,
            viewsCount = 71950000,
            uploadedTimeAgo = "4 days ago",
            category = "Music",
            youtubeId = "anzhRV7Qq2M",
            likesCount = 95000,
            commentsCount = 890
        ),
        Video(
            id = "vid_gazal_2",
            title = "মায়াবী যাদু মাখা কণ্ঠে হৃদয়স্পর্শী গজল | ঐ খুঁটিহীন নীল আকাশ | Khutihin Nil Akash",
            description = "মন জুড়ানো বাংলা সুফি ও ইসলামিক সুর। আল্ট্রা ডাটা সেভার সাপোর্টেড।",
            channelName = "Islam and Life",
            channelAvatarUrl = "https://picsum.photos/seed/islamandlife/200/200",
            subscriberCount = "1.2M",
            videoUrl = "https://www.youtube.com/watch?v=dHoZ6Vd6HcE",
            thumbnailUrl = "https://i.ytimg.com/vi/dHoZ6Vd6HcE/hqdefault.jpg",
            durationSeconds = 239,
            viewsCount = 79100000,
            uploadedTimeAgo = "1 month ago",
            category = "Music",
            youtubeId = "dHoZ6Vd6HcE",
            likesCount = 125000,
            commentsCount = 1420
        ),

        // Natok / Drama
        Video(
            id = "vid_natok_1",
            title = "আপনপর | Aponpor | Full Natok | Tawsif Mahbub | New Bangla Natok",
            description = "২০২৬ সালের অন্যতম জনপ্রিয় বাংলা নাটক। দারুণ গল্প ও রোমান্টিক কমেডি।",
            channelName = "CMV Drama",
            channelAvatarUrl = "https://picsum.photos/seed/cmvdrama/200/200",
            subscriberCount = "4.2M",
            videoUrl = "https://www.youtube.com/watch?v=tWatFr--zGY",
            thumbnailUrl = "https://i.ytimg.com/vi/tWatFr--zGY/hqdefault.jpg",
            durationSeconds = 3966,
            viewsCount = 8760000,
            uploadedTimeAgo = "2 weeks ago",
            category = "Entertainment",
            youtubeId = "tWatFr--zGY",
            likesCount = 180000,
            commentsCount = 2300
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

        // Animation
        Video(
            id = "vid_7",
            title = "Sintel Open Source Animated Short Film - 4K Remaster",
            description = "The lonely girl searching for her dragon companion. Masterpiece animated short film optimized for instant playback.",
            channelName = "Blender Animation Guild",
            channelAvatarUrl = "https://picsum.photos/seed/blenderguild/200/200",
            subscriberCount = "5.1M",
            videoUrl = "https://www.youtube.com/watch?v=eRsGyueVLvQ",
            thumbnailUrl = "https://i.ytimg.com/vi/eRsGyueVLvQ/hqdefault.jpg",
            durationSeconds = 888,
            viewsCount = 4200000,
            uploadedTimeAgo = "3 months ago",
            category = "Animation",
            youtubeId = "eRsGyueVLvQ",
            likesCount = 310000,
            commentsCount = 2400
        )
    )

    val initialComments = mapOf(
        "vid_song_2" to listOf(
            CommentEntity(
                id = "c_s201",
                videoId = "vid_song_2",
                authorName = "Tahmina Akter",
                authorAvatarUrl = "https://picsum.photos/seed/tahmina/100/100",
                commentText = "কত বছর আগের গান, আজও শুনলে হৃদয় জুড়িয়ে যায়!",
                timestamp = System.currentTimeMillis() - 3600000L * 3,
                likesCount = 142,
                isLikedByMe = true
            )
        ),
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
            )
        ),
        "vid_gazal_1" to listOf(
            CommentEntity(
                id = "c_g101",
                videoId = "vid_gazal_1",
                authorName = "Mahbub Alam",
                authorAvatarUrl = "https://picsum.photos/seed/mahbub/100/100",
                commentText = "সুবহানাল্লাহ! মন শীতল করা সুন্দর গজল।",
                timestamp = System.currentTimeMillis() - 3600000L * 5,
                likesCount = 310,
                isLikedByMe = true
            )
        )
    )
}
