package com.example.data

import com.example.model.Comment
import com.example.model.NotificationItem
import com.example.model.Video

object SampleData {

    val categories = listOf(
        "All",
        "React",
        "Tailwind CSS",
        "Node.js",
        "Web Dev",
        "Gaming",
        "Music",
        "Podcasts",
        "Android",
        "AI & Tech",
        "Science"
    )

    val sampleVideos = listOf(
        Video(
            id = "vid_1",
            title = "Build a Full-Stack YouTube Clone with React, Tailwind & Node.js (2026)",
            description = "Welcome to the ultimate guide to building a modern, full-stack video sharing platform! In this comprehensive masterclass, we will construct a production-ready YouTube clone from scratch.\n\nKey Highlights:\n- Modern React 19 Architecture\n- Responsive Tailwind CSS Design System\n- Real-time Video Streaming with HTML5 & HLS\n- Full RESTful Node.js + Express backend\n- Search, Likes, Subscriptions & Comments\n\nTimestamps:\n00:00 - Introduction & Project Demo\n04:15 - Architecture & Directory Setup\n12:30 - Building the YouTube Header & Sidebar\n25:40 - Video Card Grid & Feed\n48:10 - Custom Video Player with Scrubbing & Fullscreen\n01:15:00 - Comment System & Live Interactions\n\nDon't forget to Like, Share, and Subscribe for more high-level development tutorials!",
            thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&q=80",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            channelName = "CodeCraft Academy",
            channelAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&q=80",
            subscribersCount = "1.85M",
            viewsCount = "1.4M views",
            uploadTime = "2 days ago",
            uploadDate = "Sep 9, 2026",
            duration = "1:24:18",
            durationSeconds = 5058,
            category = "React",
            likesCount = 94200,
            tags = listOf("#react", "#tailwindcss", "#nodejs", "#fullstack", "#webdev")
        ),
        Video(
            id = "vid_2",
            title = "10 Tailwind CSS Tips You Probably Don't Know (Pro Workflow)",
            description = "Supercharge your styling speed with these 10 game-changing Tailwind CSS tricks. From dynamic arbitrary variants to container queries and custom fluid typography plugins!\n\n1. Arbitrary child selectors\n2. Container queries in Tailwind v4\n3. CSS variable interoperability\n4. Dark mode classes with data attributes\n5. Fluid text scaling without media queries\n\nCheck out our GitHub repository for the full source code snippet library.",
            thumbnailUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800&q=80",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            channelName = "Frontend Prodigy",
            channelAvatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150&q=80",
            subscribersCount = "720K",
            viewsCount = "480K views",
            uploadTime = "5 days ago",
            uploadDate = "Sep 6, 2026",
            duration = "14:52",
            durationSeconds = 892,
            category = "Tailwind CSS",
            likesCount = 38400,
            tags = listOf("#tailwindcss", "#css", "#webdesign", "#frontend")
        ),
        Video(
            id = "vid_3",
            title = "Node.js Performance Optimization: Handling 100K Requests per Second",
            description = "Dive deep into the Node.js V8 engine, event loop clustering, Libuv thread pool tuning, and zero-copy streaming buffers. Learn how leading tech organizations scale their microservice architectures gracefully.",
            thumbnailUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800&q=80",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            channelName = "Backend Masterclass",
            channelAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&q=80",
            subscribersCount = "950K",
            viewsCount = "620K views",
            uploadTime = "1 week ago",
            uploadDate = "Sep 4, 2026",
            duration = "28:10",
            durationSeconds = 1690,
            category = "Node.js",
            likesCount = 51200,
            tags = listOf("#nodejs", "#backend", "#performance", "#javascript")
        ),
        Video(
            id = "vid_4",
            title = "Cyberpunk 2077 Next-Gen RTX 5090 Ultra Benchmark Gameplay [4K 120FPS]",
            description = "Experience Night City pushed to the absolute graphical limit with Full Path Tracing and DLSS 4.0 frame generation enabled. Pure immersive walkthrough of the city streets and combat encounters.",
            thumbnailUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&q=80",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            channelName = "NextGen Gaming",
            channelAvatarUrl = "https://images.unsplash.com/photo-1566492031773-4f4e44671857?w=150&q=80",
            subscribersCount = "3.2M",
            viewsCount = "2.1M views",
            uploadTime = "3 weeks ago",
            uploadDate = "Aug 21, 2026",
            duration = "34:25",
            durationSeconds = 2065,
            category = "Gaming",
            likesCount = 118000,
            tags = listOf("#gaming", "#cyberpunk", "#4k", "#rtx")
        ),
        Video(
            id = "vid_5",
            title = "Chill Lofi Beats to Relax / Study / Code to ☕ [24/7 Radio Session]",
            description = "Relaxing lofi hip hop beats curated for deep work, coding sessions, late-night studying, and meditation. Featuring soothing rhodes pianos, vintage vinyl crackles, and gentle mellow drums.",
            thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&q=80",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            channelName = "Lofi Café",
            channelAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&q=80",
            subscribersCount = "5.8M",
            viewsCount = "8.9M views",
            uploadTime = "1 month ago",
            uploadDate = "Aug 12, 2026",
            duration = "45:00",
            durationSeconds = 2700,
            category = "Music",
            likesCount = 420000,
            tags = listOf("#lofi", "#chill", "#studybeats", "#codingmusic")
        ),
        Video(
            id = "vid_6",
            title = "The Future of AI Agents: Autonomous Coding & Software Engineering in 2026",
            description = "An in-depth technical discussion on agentic loops, autonomous multi-turn coding assistants, AST manipulation, self-healing software test runners, and the evolving role of software engineers.",
            thumbnailUrl = "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=800&q=80",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
            channelName = "AI Frontier Podcast",
            channelAvatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150&q=80",
            subscribersCount = "1.1M",
            viewsCount = "750K views",
            uploadTime = "3 days ago",
            uploadDate = "Sep 8, 2026",
            duration = "52:18",
            durationSeconds = 3138,
            category = "AI & Tech",
            likesCount = 67000,
            tags = listOf("#ai", "#agents", "#techpodcast", "#coding")
        ),
        Video(
            id = "vid_7",
            title = "Modern Android App Architecture with Jetpack Compose & Material 3",
            description = "Learn modern best practices for Android development: MVVM pattern, Kotlin Flows, state management, adaptive foldables, window size classes, and smooth edge-to-edge UI composition.",
            thumbnailUrl = "https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?w=800&q=80",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
            channelName = "Android Dev Pro",
            channelAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&q=80",
            subscribersCount = "890K",
            viewsCount = "310K views",
            uploadTime = "6 days ago",
            uploadDate = "Sep 5, 2026",
            duration = "22:40",
            durationSeconds = 1360,
            category = "Android",
            likesCount = 28500,
            tags = listOf("#android", "#jetpackcompose", "#kotlin", "#mobiledev")
        ),
        Video(
            id = "vid_8",
            title = "James Webb Telescope Unveils Cosmic Dawn Mysteries at the Edge of Time",
            description = "Stunning discoveries from JWST reveal massive early galaxies that challenge our cosmological models. Explore the spectroscopic analysis and high-definition infrared visualizations of deep cosmic structures.",
            thumbnailUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800&q=80",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackSeeTheWorld.mp4",
            channelName = "Cosmos Explored",
            channelAvatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150&q=80",
            subscribersCount = "4.2M",
            viewsCount = "3.8M views",
            uploadTime = "2 weeks ago",
            uploadDate = "Aug 27, 2026",
            duration = "19:15",
            durationSeconds = 1155,
            category = "Science",
            likesCount = 230000,
            tags = listOf("#space", "#astronomy", "#jwst", "#science")
        )
    )

    val sampleComments = mapOf(
        "vid_1" to listOf(
            Comment(
                id = "c_1",
                videoId = "vid_1",
                authorName = "Alex Rivera",
                authorAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&q=80",
                timestamp = "1 day ago",
                content = "This is by far the cleanest YouTube clone tutorial ever made. The custom video player and responsive sidebar layout are completely next level!",
                likesCount = 342,
                isLiked = true
            ),
            Comment(
                id = "c_2",
                videoId = "vid_1",
                authorName = "Sophia Chen",
                authorAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&q=80",
                timestamp = "18 hours ago",
                content = "The Tailwind CSS styling tips in the header and the expandable description box saved me hours of debugging. Thank you so much!",
                likesCount = 129,
                isLiked = false
            ),
            Comment(
                id = "c_3",
                videoId = "vid_1",
                authorName = "Dev Marcus",
                authorAvatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=100&q=80",
                timestamp = "12 hours ago",
                content = "Could you do a follow-up on WebRTC live streaming integration? Loving the content quality as always!",
                likesCount = 85,
                isLiked = false
            )
        ),
        "vid_2" to listOf(
            Comment(
                id = "c_4",
                videoId = "vid_2",
                authorName = "Liam Walker",
                authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&q=80",
                timestamp = "3 days ago",
                content = "Tip #4 with dark mode data attributes just changed my entire styling workflow. Awesome video!",
                likesCount = 214,
                isLiked = true
            )
        ),
        "vid_3" to listOf(
            Comment(
                id = "c_5",
                videoId = "vid_3",
                authorName = "Emily Watson",
                authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&q=80",
                timestamp = "4 days ago",
                content = "The explanation of Libuv event loops and memory buffer allocations was crystal clear. Subscribed immediately!",
                likesCount = 98,
                isLiked = false
            )
        )
    )

    val sampleNotifications = listOf(
        NotificationItem(
            id = "notif_1",
            channelName = "CodeCraft Academy",
            channelAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&q=80",
            message = "uploaded: Build a Full-Stack YouTube Clone with React, Tailwind & Node.js",
            timestamp = "2 hours ago",
            thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200&q=80",
            isRead = false
        ),
        NotificationItem(
            id = "notif_2",
            channelName = "Frontend Prodigy",
            channelAvatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=100&q=80",
            message = "uploaded: 10 Tailwind CSS Tips You Probably Don't Know",
            timestamp = "1 day ago",
            thumbnailUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=200&q=80",
            isRead = false
        ),
        NotificationItem(
            id = "notif_3",
            channelName = "AI Frontier Podcast",
            channelAvatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=100&q=80",
            message = "is live: Autonomous Coding & Software Engineering in 2026",
            timestamp = "2 days ago",
            thumbnailUrl = "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=200&q=80",
            isRead = true
        )
    )
}
