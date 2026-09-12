package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.NavigationDestination

@Composable
fun YouTubeSidebar(
    currentDestination: NavigationDestination,
    onDestinationSelect: (NavigationDestination) -> Unit,
    subscribedChannels: Set<String>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier
            .width(240.dp)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 8.dp, horizontal = 10.dp)
                .verticalScroll(scrollState)
        ) {
            // Main Top Section
            SidebarNavItem(
                label = "Home",
                icon = if (currentDestination == NavigationDestination.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                isSelected = currentDestination == NavigationDestination.HOME,
                onClick = { onDestinationSelect(NavigationDestination.HOME) },
                testTag = "nav_item_home"
            )

            SidebarNavItem(
                label = "Shorts",
                icon = if (currentDestination == NavigationDestination.SHORTS) Icons.Filled.PlayCircleOutline else Icons.Outlined.PlayCircleOutline,
                isSelected = currentDestination == NavigationDestination.SHORTS,
                onClick = { onDestinationSelect(NavigationDestination.SHORTS) },
                testTag = "nav_item_shorts"
            )

            SidebarNavItem(
                label = "Subscriptions",
                icon = if (currentDestination == NavigationDestination.SUBSCRIPTIONS) Icons.Filled.Subscriptions else Icons.Outlined.Subscriptions,
                isSelected = currentDestination == NavigationDestination.SUBSCRIPTIONS,
                onClick = { onDestinationSelect(NavigationDestination.SUBSCRIPTIONS) },
                testTag = "nav_item_subscriptions"
            )

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // You / Library Section
            Text(
                text = "You",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )

            SidebarNavItem(
                label = "Library",
                icon = if (currentDestination == NavigationDestination.LIBRARY) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
                isSelected = currentDestination == NavigationDestination.LIBRARY,
                onClick = { onDestinationSelect(NavigationDestination.LIBRARY) },
                testTag = "nav_item_library"
            )

            SidebarNavItem(
                label = "History",
                icon = if (currentDestination == NavigationDestination.HISTORY) Icons.Filled.History else Icons.Outlined.History,
                isSelected = currentDestination == NavigationDestination.HISTORY,
                onClick = { onDestinationSelect(NavigationDestination.HISTORY) },
                testTag = "nav_item_history"
            )

            SidebarNavItem(
                label = "Your Videos",
                icon = if (currentDestination == NavigationDestination.YOUR_VIDEOS) Icons.Filled.PlayCircleOutline else Icons.Outlined.PlayCircleOutline,
                isSelected = currentDestination == NavigationDestination.YOUR_VIDEOS,
                onClick = { onDestinationSelect(NavigationDestination.YOUR_VIDEOS) },
                testTag = "nav_item_your_videos"
            )

            SidebarNavItem(
                label = "Watch Later",
                icon = if (currentDestination == NavigationDestination.WATCH_LATER) Icons.Filled.WatchLater else Icons.Outlined.WatchLater,
                isSelected = currentDestination == NavigationDestination.WATCH_LATER,
                onClick = { onDestinationSelect(NavigationDestination.WATCH_LATER) },
                testTag = "nav_item_watch_later"
            )

            SidebarNavItem(
                label = "Liked Videos",
                icon = if (currentDestination == NavigationDestination.LIKED_VIDEOS) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                isSelected = currentDestination == NavigationDestination.LIKED_VIDEOS,
                onClick = { onDestinationSelect(NavigationDestination.LIKED_VIDEOS) },
                testTag = "nav_item_liked_videos"
            )

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Subscriptions list
            Text(
                text = "Subscriptions",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )

            if (subscribedChannels.isEmpty()) {
                Text(
                    text = "No channels subscribed yet",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            } else {
                subscribedChannels.forEach { channel ->
                    ChannelSubscriptionRow(channelName = channel)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SidebarNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ChannelSubscriptionRow(channelName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { /* Channel view */ }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&q=80",
                contentDescription = channelName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = channelName,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1
        )
    }
}
