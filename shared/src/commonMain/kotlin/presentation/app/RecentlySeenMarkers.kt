package presentation.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import data.MarkersRepo
import data.appSettings
import kotlinx.coroutines.launch
import presentation.maps.MarkerIdStr
import presentation.maps.RecentlySeenMarker
import presentation.uiComponents.lightenBy

@Composable
fun RecentlySeenMarkers(
    recentlySeenMarkersForUiList: SnapshotStateList<RecentlySeenMarker>,
    onClickRecentlySeenMarkerItem: ((MarkerIdStr) -> Unit) = {},
    currentlySpeakingMarker: RecentlySeenMarker? = null,
    isTextToSpeechCurrentlySpeaking: Boolean = false,
    onClickStartSpeakingMarker: (RecentlySeenMarker, shouldSpeakDetails: Boolean) -> Unit = { _, _ -> Unit},
    onClickStopSpeakingMarker: () -> Unit = {},
    markersRepo: MarkersRepo
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    var curTopItemKey by remember { mutableStateOf("") }

    // Scroll to top when first item key changes
    LaunchedEffect(
        recentlySeenMarkersForUiList.isNotEmpty()
        && recentlySeenMarkersForUiList.first().id != curTopItemKey
    ) {
        if(recentlySeenMarkersForUiList.isEmpty()) return@LaunchedEffect

        curTopItemKey = recentlySeenMarkersForUiList.first().id
        coroutineScope.launch {
            scrollState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Current spoken marker
        AnimatedVisibility(
            currentlySpeakingMarker != null,
            enter = expandVertically(tween(1500)),
            exit = fadeOut()
        ) {
            val speakingMarker = currentlySpeakingMarker!!
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp, 4.dp, 8.dp, 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colors.primary.lightenBy(.2f).copy(alpha = 0.75f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .heightIn(min = 48.dp)
                            .padding(8.dp, 0.dp, 8.dp, 4.dp)
                            .clickable {
                                onClickRecentlySeenMarkerItem(speakingMarker.id)
                            }
                            .weight(3f)
                    ) {
                        Text(
                            text = speakingMarker.title,
                            color = MaterialTheme.colors.onPrimary,
                            fontStyle = FontStyle.Normal,
                            fontSize = MaterialTheme.typography.h6.fontSize,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = speakingMarker.id + " "
                                    + if(isTextToSpeechCurrentlySpeaking) "speaking" else "spoken last",
                            color = MaterialTheme.colors.onPrimary.copy(alpha = 0.50f),
                            fontStyle = FontStyle.Normal,
                            fontSize = MaterialTheme.typography.body1.fontSize,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    if (isTextToSpeechCurrentlySpeaking) {
                        IconButton(
                            onClick = {
                                onClickStopSpeakingMarker()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop Speaking Marker",
                            )
                        }
                    } else {
                        IconButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f),
                            onClick = {
                                markersRepo.updateMarkerIsSpoken(id=speakingMarker.id, isSpoken=true)
                                onClickStartSpeakingMarker(
                                    speakingMarker,
                                    appSettings.shouldSpeakDetailsWhenUnseenMarkerFound
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VolumeUp,
                                contentDescription = "Speak Marker",
                                tint = MaterialTheme.colors.onBackground
                            )
                        }
                    }
                }
        }

        LazyColumn(
            userScrollEnabled = true,
            state = scrollState,
            modifier = Modifier
                .background(MaterialTheme.colors.surface)

        ) {

            if (recentlySeenMarkersForUiList.isEmpty()) {
                // Show "empty" placeholder if no markers
                item {
                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = "No recently seen markers, drive around to see some!",
                        color = MaterialTheme.colors.onBackground,
                        fontStyle = FontStyle.Normal,
                        fontSize = MaterialTheme.typography.h6.fontSize,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    )
                }
            } else {
                // Header
                item(
                    "header"
                ) {
                    Text(
                        text = "TOP 5 RECENTLY SEEN MARKERS",
                        color = MaterialTheme.colors.onSurface,
                        fontStyle = FontStyle.Normal,
                        fontSize = MaterialTheme.typography.subtitle2.fontSize,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, bottom = 4.dp)
                    )
                }
            }

            items(
                recentlySeenMarkersForUiList.size,
                key = { index -> recentlySeenMarkersForUiList[index].id }
            ) {
                val recentMarker = recentlySeenMarkersForUiList.elementAt(it)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp, 4.dp, 8.dp, 8.dp)
                        .background(
                            color = MaterialTheme.colors.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .padding(8.dp, 0.dp, 8.dp, 4.dp)
                            .clickable {
                                onClickRecentlySeenMarkerItem(recentMarker.id)
                            }
                            .weight(3f)
                    ) {
                        Text(
                            text = recentMarker.title,
                            color = MaterialTheme.colors.onPrimary,
                            fontStyle = FontStyle.Normal,
                            fontSize = MaterialTheme.typography.h6.fontSize,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = recentMarker.id,
                            color = MaterialTheme.colors.onPrimary.copy(alpha = 0.50f),
                            fontStyle = FontStyle.Normal,
                            fontSize = MaterialTheme.typography.body1.fontSize,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f)
                    ) {
                        if (markersRepo.marker(recentMarker.id)?.isSpoken == true) {
                            IconButton(
                                onClick = {
                                    onClickStartSpeakingMarker(recentMarker, true)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeMute,
                                    contentDescription = "Speak a spoken marker again",
                                    tint = MaterialTheme.colors.onBackground.copy(alpha =.5f)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    onClickStartSpeakingMarker(recentMarker, true)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VolumeUp,
                                    contentDescription = "Speak Marker",
                                    tint = MaterialTheme.colors.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}