package com.example.linphoneapp.BottomNav.bottomScreens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.linphoneapp.Screen.OutboundCall
import com.example.linphoneapp.data.RecentCall
import com.example.linphoneapp.viewmodel.RecentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentScreen(onEndCall: () -> Unit,vm: RecentViewModel = hiltViewModel()) {
    val grouped by vm.groupedCalls.collectAsState()
    val navController = rememberNavController()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Beautiful Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Calls",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Enhanced LazyColumn with your existing structure
        if (grouped.isEmpty()) {
            EmptyRecentCallsState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(grouped) { call ->
                    RecentCallItem(call,vm,navController,onEndCall)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentCallItem(
    call: RecentCall,
    vm: RecentViewModel,
    navController: NavHostController,
    onEndCall: () -> Unit
) {
    Log.d("TAG>>", "RecentCallItem: " + call.number)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {  },
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Enhanced Avatar
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = getAvatarColor(call.number.toString())
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getContactInitials(call.number.toString()),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Enhanced Content Column (your existing structure but beautified)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Enhanced Row for number and count
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = call.number.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Enhanced call count badge
                    if (call.count > 1) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "${call.count}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Enhanced last call time with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Last: ${formatLastCallTime(call.lastTime)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Enhanced action button
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 2.dp
            ) {
                IconButton(
                    onClick = { callo(navController,call.number,onEndCall) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call ${call.number}",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}


private fun callo(navController: NavHostController, number: String?, onEndCall: () -> Unit) {
    navController.navigate("outgoing/$number")
}

// Beautiful empty state
@Composable
fun EmptyRecentCallsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "No Recent Calls",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your call history will appear here when you make or receive calls",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Helper functions for enhanced UI
fun getAvatarColor(phoneNumber: String): Color {
    val colors = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFF8B5CF6), // Purple
        Color(0xFFEC4899), // Pink
        Color(0xFFF59E0B), // Amber
        Color(0xFF10B981), // Emerald
        Color(0xFF3B82F6), // Blue
        Color(0xFFEF4444), // Red
        Color(0xFF84CC16), // Lime
        Color(0xFF06B6D4), // Cyan
        Color(0xFF8B5A2B)  // Brown
    )
    return colors[phoneNumber.hashCode().mod(colors.size)]
}

fun getContactInitials(phoneNumber: String): String {
    // Extract meaningful initials from phone number
    return if (phoneNumber.length >= 2) {
        phoneNumber.take(2)
    } else if (phoneNumber.isNotEmpty()) {
        phoneNumber
    } else {
        "?"
    }
}



fun formatLastCallTime(lastTime: Long): String {
    val currentTime = System.currentTimeMillis() / 1000
    val timeDiff = currentTime - lastTime

    return when {
        timeDiff < 60 -> "Just now"
        timeDiff < 3600 -> "${timeDiff / 60}m ago"
        timeDiff < 86400 -> "${timeDiff / 3600}h ago"
        timeDiff < 604800 -> "${timeDiff / 86400}d ago"
        else -> {
            // Use your existing DateFormat but make it more readable
            val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
            dateFormat.format(Date(lastTime * 1000))
        }
    }
}

