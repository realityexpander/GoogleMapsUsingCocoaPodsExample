package presentation.app

import BottomSheetScreen
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kAppNameStr
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import presentation.maps.Marker

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun AppDrawerContent(
    finalMarkers: List<Marker>,
    onSetBottomSheetActiveScreen: (BottomSheetScreen) -> Unit = {},
    onShowOnboarding: () -> Unit = {},
    onShowAboutBox: () -> Unit = {},
    onExpandBottomSheet: () -> Unit = {},
    onCloseDrawer: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    val entries: SnapshotStateList<Marker> = remember { mutableStateListOf() }
    val searchMarkers = remember { mutableStateListOf<Marker>() }
    LaunchedEffect(finalMarkers) {
        // Log.d("📌📌📌AppDrawerContent: LaunchedEffect(finalMarkers) calculating entries...")
        entries.clear()
        entries.addAll(finalMarkers.reversed())

        searchMarkers.clear()
        searchMarkers.addAll(finalMarkers.reversed())
    }

    // App title & close button
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            kAppNameStr,
            fontSize = MaterialTheme.typography.h5.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(3f)
        )
        IconButton(
            modifier = Modifier
                .offset(16.dp, (-16).dp),
            onClick = {
                coroutineScope.launch {
                    onCloseDrawer()
                }
            }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close"
            )
        }

    }
    Spacer(modifier = Modifier.height(16.dp))

    // Show onboarding button
    Button(
        onClick = {
            coroutineScope.launch {
                onShowOnboarding()
                yield()
                onCloseDrawer()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp),
    ) {
        Text(
            "Show Onboarding",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            fontStyle = FontStyle.Normal,
            fontSize = MaterialTheme.typography.body1.fontSize,
            textAlign = TextAlign.Center,
        )
    }
    Spacer(modifier = Modifier.height(16.dp))


    // Show about box
    Button(
        onClick = {
            coroutineScope.launch {
                onShowAboutBox()
                yield()
                onCloseDrawer()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp),
    ) {
        Text(
            "About this app",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            fontStyle = FontStyle.Normal,
            fontSize = MaterialTheme.typography.body1.fontSize,
            textAlign = TextAlign.Center,
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    // Search box - rounded text field
    var searchQuery by remember { mutableStateOf("") }
    var isSearchDialogVisible by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colors.onBackground.copy(alpha = 0.1f))
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        isSearchDialogVisible = true
                    }
                }
                .focusProperties {
                    if(isSearchDialogVisible) {
                        canFocus = false
                    }
                }
            ,
            placeholder = { Text(text = "Search Marker Title...") },
            value = searchQuery,
            onValueChange = { },
            readOnly = true,
            singleLine = true,
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                placeholderColor = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                leadingIconColor = MaterialTheme.colors.onBackground,
                textColor = MaterialTheme.colors.onBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colors.onBackground,
            ),
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            searchQuery = ""
                            searchMarkers.clear()
                            searchMarkers.addAll(finalMarkers.reversed())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
        )
    }
    Spacer(modifier = Modifier.height(8.dp))

    // Header for list of loaded markers
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp),
    ) {
        Text(
            "Loaded Markers",
            modifier = Modifier.weight(2.5f),
            fontStyle = FontStyle.Italic,
            fontSize = MaterialTheme.typography.body2.fontSize,
            fontWeight = FontWeight.Bold,
        )
        Icon(
            imageVector = Icons.Default.Visibility,
            contentDescription = "Seen",
            modifier = Modifier
                .weight(.3f)
                .height(18.dp)
                .offset((-6).dp, 2.dp)
        )
        Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = "Spoken",
            modifier = Modifier
                .weight(.3f)
                .height(18.dp)
                .offset((-4).dp, 2.dp)
        )
        Text(
            "ID",
            modifier = Modifier
                .padding(start = 0.dp, end = 8.dp)
                .weight(1.2f)
                .offset((-4).dp, 0.dp),
            fontStyle = FontStyle.Italic,
            fontSize = MaterialTheme.typography.body2.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
        )
    }

    if (finalMarkers.isEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "No markers loaded yet, drive around to load some!",
            modifier = Modifier.padding(start = 8.dp),
            fontSize = MaterialTheme.typography.h6.fontSize,
            fontWeight = FontWeight.Normal,
        )
    }

    if(isSearchDialogVisible) {
        SearchMarkerDialog(
            finalMarkers = finalMarkers,
            onSearchResult = { query, searchMarkersResult ->
                searchQuery = query
                searchMarkers.clear()
                searchMarkers.addAll(searchMarkersResult)
                isSearchDialogVisible = false
            },
            onClickMarker = { marker ->
                println("Clicked on marker: ${marker.id}")
            },
            onLocateMarker = { marker ->
                println("Locate marker: ${marker.id}")
            },
            onNavToMarker = { marker ->
                println("Nav to marker: ${marker.id}")
            },
            onDismiss = {
                isSearchDialogVisible = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(),
        state = rememberLazyListState(),
        userScrollEnabled = true,
    ) {
        // Header

//        items(entries.size) { markerIdx ->
        items(searchMarkers.size) { markerIdx ->
//            val marker = entries[markerIdx]
            val marker = searchMarkers[markerIdx]

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .animateItemPlacement(animationSpec = tween(250))
                    .clickable {
                        coroutineScope.launch {
                            println("Clicked on marker: ${marker.id}")
                            onExpandBottomSheet()
                            onSetBottomSheetActiveScreen(
                                BottomSheetScreen.MarkerDetailsScreen(id=marker.id)
                            )
                            onCloseDrawer()
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = marker.title,
                    modifier = Modifier.weight(2.5f),
                    softWrap = false,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontStyle = FontStyle.Normal,
                    fontSize = MaterialTheme.typography.body1.fontSize,
                )

                // isSeen
                if (marker.isSeen) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Seen",
                        modifier = Modifier
                            .weight(.3f)
                            .height(16.dp)
                    )
                } else {
                    // "leave blank"
                    Spacer(
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .weight(.3f)
                            .height(16.dp)
                    )
                }

                // isSpoken
                if (marker.isSpoken) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Spoken",
                        modifier = Modifier
                            .weight(.3f)
                            .height(16.dp)
                    )
                } else {
                    // "leave blank"
                    Spacer(
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .weight(.3f)
                            .height(16.dp)
                    )
                }

                Text(
                    text = marker.id,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1.2f),
                    fontStyle = FontStyle.Normal,
                    fontSize = MaterialTheme.typography.body1.fontSize,
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun SearchMarkerDialog(
    finalMarkers: List<Marker>,
    onSearchResult: (searchQuery: String, searchResult: List<Marker>) -> Unit =
        { _, _ -> },
    onClickMarker: (Marker) -> Unit = {},
    onLocateMarker: (Marker) -> Unit = {},
    onNavToMarker: (Marker) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
        onDismissRequest = {
            onDismiss()
        },
    ) {
        var searchQuery by remember { mutableStateOf("") }
        val searchEntries = remember { mutableStateListOf<Marker>().also {
                it.addAll(finalMarkers.reversed())
            } }
        val keyboardController = LocalSoftwareKeyboardController.current

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.onBackground.copy(alpha = 0.1f))
                .clickable {
                    keyboardController?.show()
                }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Close button
                IconButton(
                    onClick = {
                        onSearchResult(searchQuery, searchEntries)
                        onDismiss()
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.onBackground.copy(alpha = 0.1f))
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        placeholder = { Text(text = "Search Marker Title...") },
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it

                            if (searchQuery.isEmpty()) {
                                searchEntries.clear()
                                searchEntries.addAll(finalMarkers.reversed())
                            } else {
                                searchEntries.clear()
                                searchEntries.addAll(
                                    finalMarkers.filter { marker ->
                                        marker.title.contains(searchQuery, ignoreCase = true)
                                    }.sortedBy { marker ->
                                        marker.title
                                    }
                                )
                            }
                        },
                        singleLine = true,
                        maxLines = 1,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = MaterialTheme.colors.background,
                            placeholderColor = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                            leadingIconColor = MaterialTheme.colors.onBackground,
                            textColor = MaterialTheme.colors.onBackground,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colors.onBackground,
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = ""
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchQuery = ""
                                        searchEntries.clear()
                                        searchEntries.addAll(finalMarkers.reversed())
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search"
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions {
                            keyboardController?.hide()
                        }
                    )
                }
            }

            // List of markers
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 112.dp),
                state = rememberLazyListState(),
                userScrollEnabled = true,
            ) {

                items(searchEntries.size) { markerIdx ->
                    val marker = searchEntries[markerIdx]

                    if(searchEntries.size == 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No markers found",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = MaterialTheme.typography.h6.fontSize,
                            fontWeight = FontWeight.Normal,
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .animateItemPlacement(animationSpec = tween(250))
                            .clickable {
                                coroutineScope.launch {
                                    println("Clicked on marker: ${marker.id}")
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = marker.title,
                            modifier = Modifier.weight(2.5f),
                            softWrap = false,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontStyle = FontStyle.Normal,
                            fontSize = MaterialTheme.typography.body1.fontSize,
                        )

                        // isSeen
                        if (marker.isSeen) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Seen",
                                modifier = Modifier
                                    .weight(.3f)
                                    .height(16.dp)
                            )
                        } else {
                            // "leave blank"
                            Spacer(
                                modifier = Modifier
                                    .padding(end = 2.dp)
                                    .weight(.3f)
                                    .height(16.dp)
                            )
                        }

                        // isSpoken
                        if (marker.isSpoken) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Spoken",
                                modifier = Modifier
                                    .weight(.3f)
                                    .height(16.dp)
                            )
                        } else {
                            // "leave blank"
                            Spacer(
                                modifier = Modifier
                                    .padding(end = 2.dp)
                                    .weight(.3f)
                                    .height(16.dp)
                            )
                        }

                        Text(
                            text = marker.id,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1.2f),
                            fontStyle = FontStyle.Normal,
                            fontSize = MaterialTheme.typography.body1.fontSize,
                        )
                    }
                }
            }
        }
    }
}
