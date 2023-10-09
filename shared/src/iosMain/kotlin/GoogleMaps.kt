import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCameraUpdate
import cocoapods.GoogleMaps.GMSCircle
import cocoapods.GoogleMaps.GMSCoordinateBounds
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.GMSMarker.Companion.markerImageWithColor
import cocoapods.GoogleMaps.GMSMutablePath
import cocoapods.GoogleMaps.GMSPolyline
import cocoapods.GoogleMaps.animateWithCameraUpdate
import cocoapods.GoogleMaps.kGMSTypeNormal
import cocoapods.GoogleMaps.kGMSTypeSatellite
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.UIKit.UIColor


@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun GoogleMaps(
    modifier: Modifier,
    isControlsVisible: Boolean,
    onMarkerClick: ((MapMarker) -> Unit)?,
    onMapClick: ((LatLong) -> Unit)?,
    onMapLongClick: ((LatLong) -> Unit)?,
    markers: List<MapMarker>?,
    cameraLocationLatLong: LatLong?,  // best for tracking user location
    cameraLocationBounds: CameraLocationBounds?,  // best for showing a bunch of markers
    cameraPosition: CameraPosition?, // usually only used for initial camera position bc zoom level is forced
    polyLine: List<LatLong>?,
    myLocation: LatLong?  // shows the user's location with a 100m radius circle
) {
    val googleMapView = remember { GMSMapView() }

    var isMapSetupCompleted by remember { mutableStateOf(false) }

    var isTrackingEnabled by remember { mutableStateOf(false) }
    var isMyLocationButtonUpdated by remember { mutableStateOf(false) }

    var gsmMapViewType by remember { mutableStateOf(kGMSTypeNormal) }
    var isMapTypeUpdated by remember { mutableStateOf(false) }

    var isCameraPositionLatLongBoundsUpdated by remember { mutableStateOf(false) }
    var isCameraPositionUpdated by remember { mutableStateOf(false) }
    var isCameraLocationLatLongUpdated by remember { mutableStateOf(false) }
    var isRedrawMapTriggered by remember { mutableStateOf(false) }

    var showSomething = remember { false } // leave for testing purposes

    LaunchedEffect(myLocation, markers) {
        if (myLocation != null) {
            isRedrawMapTriggered = true
        }
        if (markers != null) {
            isRedrawMapTriggered = true
        }
    }

    LaunchedEffect(cameraLocationBounds) {
        if (cameraLocationBounds != null) {
            isCameraPositionLatLongBoundsUpdated = true
        }
    }

    LaunchedEffect(cameraPosition) {
        if (cameraPosition != null) {
            isCameraPositionUpdated = true
        }
    }

    LaunchedEffect(cameraLocationLatLong) {
        if (cameraLocationLatLong != null) {
            isCameraLocationLatLongUpdated = true
        }
    }

    // Note: `GoogleMaps` using UIKit is a bit of a hack, it's not a real Composable, so we have to
    //       trigger independent updates of the map parts, and sometimes re-render the
    //       map elements. That's why theres all these `is` variables, and the `isRedrawMapTriggered`
    //       variable.
    //       If its not done like this, the UI for the map will not allow the user to move around.
    Box(Modifier.fillMaxSize()) {
        // Google Maps
        UIKitView(
            modifier = modifier.fillMaxSize(),
            interactive = true,
            factory = {
                // Does not work yet... :(
                //            googleMapView.delegate = object : NSObject(), GMSMapViewDelegateProtocol {
                //                override fun mapView(mapView: GMSMapView, willMove: Boolean) {
                //                    showSomething = true
                //                    //myLocation = "lat: ${location.useContents { this.latitude }} long: ${location.useContents { this.longitude }}"
                //                }
                //
                //                override fun didTapMyLocationButtonForMapView(mapView: GMSMapView): Boolean {
                //                    showSomething = true
                //
                //                    return true
                //                }
                //
                //                override fun mapView(mapView: GMSMapView, didTapMarker: GMSMarker): Boolean {
                //                    showSomething = true
                //
                //                    return true
                //                }
                //            }

                googleMapView
            },
            update = { view ->
                if(isTrackingEnabled) {
                    myLocation?.let { myLocation ->
                        view.animateWithCameraUpdate(
                            GMSCameraUpdate.setTarget(
                                CLLocationCoordinate2DMake(
                                    latitude = myLocation.latitude,
                                    longitude = myLocation.longitude
                                )
                            )
                        )
                    }
                } else {
                    if(!isMapSetupCompleted) { // Sets the camera once during setup, this allows the user to move the map around
                        cameraPosition?.let { cameraPosition ->
                            view.animateWithCameraUpdate(
                                GMSCameraUpdate.setTarget(
                                    CLLocationCoordinate2DMake(
                                        latitude = cameraPosition.target.latitude,
                                        longitude = cameraPosition.target.longitude
                                    )
                                )
                            )
                        }
                    }
                }

                // set the map up only once, this allows the user to move the map around
                if(!isMapSetupCompleted) {
                    view.settings.setAllGesturesEnabled(true)
                    view.settings.setScrollGestures(true)
                    view.settings.setZoomGestures(true)
                    view.settings.setCompassButton(true)

                    view.myLocationEnabled = true
                    view.settings.myLocationButton = true
                    isMapSetupCompleted = true
                }

                if(isMapTypeUpdated) {
                    isMapTypeUpdated = false
                    view.mapType = gsmMapViewType
                }

                if(isMyLocationButtonUpdated) {
                    isMyLocationButtonUpdated = false
                    view.settings.myLocationButton = !isTrackingEnabled
                }

                if(isCameraPositionUpdated) {
                    isCameraPositionUpdated = false
                    cameraPosition?.let { cameraPosition ->
                        view.setCamera(
                            GMSCameraPosition.cameraWithLatitude(
                                cameraPosition.target.latitude,
                                cameraPosition.target.longitude,
                                cameraPosition.zoom // Note Zoom level is forced here, which changes user's zoom level
                            )
                        )
                    }
                }

                if(isCameraLocationLatLongUpdated) {
                    isCameraLocationLatLongUpdated = false
                    cameraLocationLatLong?.let { cameraLocation ->
                        view.animateWithCameraUpdate(
                            GMSCameraUpdate.setTarget(
                                CLLocationCoordinate2DMake(
                                    latitude = cameraLocation.latitude,
                                    longitude = cameraLocation.longitude
                                )
                            )
                        )
                    }
                }

                if (isCameraPositionLatLongBoundsUpdated) {
                    isCameraPositionLatLongBoundsUpdated = false
                    cameraLocationBounds?.let { cameraPositionLatLongBounds ->
                        var bounds = GMSCoordinateBounds()

                        cameraPositionLatLongBounds.coordinates.forEach { latLong ->
                            bounds = bounds.includingCoordinate(
                                CLLocationCoordinate2DMake(
                                    latitude = latLong.latitude,
                                    longitude = latLong.longitude
                                )
                            )
                        }
                        view.animateWithCameraUpdate(
                            GMSCameraUpdate.fitBounds(
                                bounds,
                                cameraPositionLatLongBounds.padding.toDouble()
                            )
                        )
                    }
                }

                if(isRedrawMapTriggered) {
                    // reset the markers & polylines, selected marker, etc.
                    val oldSelectedMarker = view.selectedMarker
                    var curSelectedMarker: GMSMarker? = null
                    val curSelectedMarkerId = view.selectedMarker?.userData as? String
                    view.clear()

                    // render the user's location "talk" circle
                    myLocation?.let {
                        GMSCircle().apply {
                            position = CLLocationCoordinate2DMake(
                                myLocation.latitude,
                                myLocation.longitude
                            )
                            radius = 1000.0
                            fillColor = UIColor.blueColor().colorWithAlphaComponent(0.2)
                            strokeWidth = 2.0
                            strokeColor = UIColor.blueColor().colorWithAlphaComponent(0.5)
                            map = view
                        }
                    }

                    // render the markers
                    markers?.forEach { marker ->
                        val tempMarker = GMSMarker().apply {
                            position = CLLocationCoordinate2DMake(
                                marker.position.latitude,
                                marker.position.longitude
                            )
                            title = marker.title
                            userData = marker.key
                            map = view
                            icon = markerImageWithColor(UIColor.blueColor())
                        }

                        if(tempMarker.userData as String == curSelectedMarkerId) {
                            curSelectedMarker = tempMarker
                        }
                    }

                    // render the polyline
                    polyLine?.let { polyLine ->
                        val points = polyLine.map {
                            CLLocationCoordinate2DMake(it.latitude, it.longitude)
                        }
                        val path = GMSMutablePath().apply {
                            points.forEach { point ->
                                addCoordinate(point)
                            }
                        }

                        GMSPolyline().apply {
                            this.path = path
                            this.map = view
                        }
                    }

                    // re-select the marker (if it was selected before)
                    oldSelectedMarker?.let { _ ->
                        view.selectedMarker = curSelectedMarker
                    }

                    isRedrawMapTriggered = false
                }
            },
        )

        // Map Controls
        if (isControlsVisible) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart),
                horizontalAlignment = Alignment.Start
            ) {
                if (showSomething) {  // leave for testing purposes
                    Text(
                        "SOMETHING",
                        modifier = Modifier
                            .background(color = Color.Red)
                    )
                }

                SwitchWithLabel(
                    label = "Track My location",
                    state = isTrackingEnabled,
                    darkOnLightTextColor = gsmMapViewType == kGMSTypeSatellite
                ) {
                    isTrackingEnabled = !isTrackingEnabled
                    isMyLocationButtonUpdated = true
                }
                SwitchWithLabel(
                    label = "Satellite",
                    state = gsmMapViewType == kGMSTypeSatellite,
                    darkOnLightTextColor = gsmMapViewType == kGMSTypeSatellite
                ) { shouldUseSatellite ->
                    isMapTypeUpdated = true
                    gsmMapViewType = if (shouldUseSatellite) kGMSTypeSatellite else kGMSTypeNormal
                }
            }
        }
    }
}