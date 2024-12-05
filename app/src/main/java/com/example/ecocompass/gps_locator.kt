package com.example.ecocompass


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ecocompass.BuildConfig.TOMTOM_API_KEY
import com.tomtom.sdk.datamanagement.navigationtile.NavigationTileStore
import com.tomtom.sdk.datamanagement.navigationtile.NavigationTileStoreConfiguration
import com.tomtom.sdk.location.DefaultLocationProviderFactory
import com.tomtom.sdk.location.GeoLocation
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.OnLocationUpdateListener
import com.tomtom.sdk.location.mapmatched.MapMatchedLocationProviderFactory
import com.tomtom.sdk.location.simulation.SimulationLocationProvider
import com.tomtom.sdk.location.simulation.strategy.InterpolationStrategy
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraTrackingMode
import com.tomtom.sdk.map.display.camera.CameraChangeListener
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.common.screen.Padding
import com.tomtom.sdk.map.display.gesture.MapLongClickListener
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import com.tomtom.sdk.map.display.route.Instruction
import com.tomtom.sdk.map.display.route.RouteClickListener
import com.tomtom.sdk.map.display.route.RouteOptions
import com.tomtom.sdk.map.display.ui.MapFragment
import com.tomtom.sdk.map.display.ui.currentlocation.CurrentLocationButton.VisibilityPolicy
import com.tomtom.sdk.navigation.ActiveRouteChangedListener
import com.tomtom.sdk.navigation.ProgressUpdatedListener
import com.tomtom.sdk.navigation.RoutePlan
import com.tomtom.sdk.navigation.TomTomNavigation
import com.tomtom.sdk.navigation.online.Configuration
import com.tomtom.sdk.navigation.online.OnlineTomTomNavigationFactory
import com.tomtom.sdk.navigation.ui.NavigationFragment
import com.tomtom.sdk.navigation.ui.NavigationUiOptions
import com.tomtom.sdk.routing.RoutePlanner
import com.tomtom.sdk.routing.RoutePlanningCallback
import com.tomtom.sdk.routing.RoutePlanningResponse
import com.tomtom.sdk.routing.RoutingFailure
import com.tomtom.sdk.routing.options.Itinerary
import com.tomtom.sdk.routing.options.RoutePlanningOptions
import com.tomtom.sdk.routing.options.guidance.ExtendedSections
import com.tomtom.sdk.routing.options.guidance.GuidanceOptions
import com.tomtom.sdk.routing.options.guidance.InstructionPhoneticsType
import com.tomtom.sdk.routing.online.OnlineRoutePlanner
import com.tomtom.sdk.routing.route.Route
import com.tomtom.sdk.vehicle.Vehicle
import com.tomtom.sdk.vehicle.VehicleProviderFactory


class gps_locator : AppCompatActivity() {
    private lateinit var mapFragment: MapFragment
    private lateinit var tomTomMap: TomTomMap
    private lateinit var navigationTileStore: NavigationTileStore
    private lateinit var locationProvider: LocationProvider
    private lateinit var onLocationUpdateListener: OnLocationUpdateListener
    private lateinit var routePlanner: RoutePlanner
    private var route: Route? = null
    private lateinit var routePlanningOptions: RoutePlanningOptions
    private lateinit var tomTomNavigation: TomTomNavigation
    private lateinit var navigationFragment: NavigationFragment

    private val apiKey = TOMTOM_API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLocationProvider()

        ensureLocationPermissions {
            // Enables the location provider to start receiving location updates.
            locationProvider.enable()
        }

        initMap {

            tomTomMap.setLocationProvider(locationProvider)

            showUserLocation()
            setUpMapListeners()
        }

        initNavigationTileStore()
        initRouting()
        initNavigation()
    }


    private fun initMap(onMapReady: () -> Unit) {
        val mapOptions = MapOptions(mapKey = apiKey)
        mapFragment = MapFragment.newInstance(mapOptions)

        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()

        mapFragment.getMapAsync { map ->
            tomTomMap = map
            onMapReady()
        }
    }


    private fun initNavigationTileStore() {
        navigationTileStore = NavigationTileStore.create(
            context = applicationContext,
            navigationTileStoreConfig = NavigationTileStoreConfiguration(
                apiKey = apiKey
            )
        )
    }

    private fun initLocationProvider() {
        locationProvider = DefaultLocationProviderFactory.create(context = applicationContext)
    }

    /**
     * Plans the route by initializing by using the online route planner and default route replanner.
     */
    private fun initRouting() {
        routePlanner = OnlineRoutePlanner.create(context = applicationContext, apiKey = apiKey)
    }

    /**
     * To use navigation in the application, start by by initialising the navigation configuration.
     */
    private fun initNavigation() {
        val configuration = Configuration(
            context = applicationContext,
            navigationTileStore = navigationTileStore,
            locationProvider = locationProvider,
            routePlanner = routePlanner,
            vehicleProvider = VehicleProviderFactory.create(vehicle = Vehicle.Car())
        )
        tomTomNavigation = OnlineTomTomNavigationFactory.create(configuration)
    }

    private fun ensureLocationPermissions(onLocationPermissionsGranted: () -> Unit) {
        if (areLocationPermissionsGranted()) {
            onLocationPermissionsGranted()
        } else {
            requestLocationPermission(onLocationPermissionsGranted)
        }
    }

    private fun showUserLocation() {
        // zoom to current location at city level
        onLocationUpdateListener = OnLocationUpdateListener { location ->
            val locationMarker = LocationMarkerOptions(type = LocationMarkerOptions.Type.Pointer)
            tomTomMap.enableLocationMarker(locationMarker)
            tomTomMap.moveCamera(CameraOptions(location.position, zoom = 8.0))
            locationProvider.removeOnLocationUpdateListener(onLocationUpdateListener)
        }
        locationProvider.addOnLocationUpdateListener(onLocationUpdateListener)
    }


    private fun setUpMapListeners() {
        tomTomMap.addMapLongClickListener(mapLongClickListener)
        tomTomMap.addRouteClickListener(routeClickListener)
    }


    private val mapLongClickListener = MapLongClickListener { geoPoint ->
        clearMap()
        calculateRouteTo(geoPoint)
        true
    }


    private fun isNavigationRunning(): Boolean = tomTomNavigation.navigationSnapshot != null



    private val routeClickListener = RouteClickListener {
        if (!isNavigationRunning()) {
            route?.let { route ->
                mapFragment.currentLocationButton.visibilityPolicy = VisibilityPolicy.Invisible
                startNavigation(route)
            }
        }
    }


    private fun calculateRouteTo(destination: GeoPoint) {
        val userLocation =
            tomTomMap.currentLocation?.position ?: return
        val itinerary = Itinerary(origin = userLocation, destination = destination)
        routePlanningOptions = RoutePlanningOptions(
            itinerary = itinerary,
            guidanceOptions = GuidanceOptions(
                phoneticsType = InstructionPhoneticsType.Ipa,
                extendedSections = ExtendedSections.All
            ),
            vehicle = Vehicle.Car()
        )
        routePlanner.planRoute(routePlanningOptions, routePlanningCallback)
    }


    private val routePlanningCallback = object : RoutePlanningCallback {
        override fun onSuccess(result: RoutePlanningResponse) {
            route = result.routes.first()
            drawRoute(route!!)
            tomTomMap.zoomToRoutes(ZOOM_TO_ROUTE_PADDING)
        }

        override fun onFailure(failure: RoutingFailure) {
            Toast.makeText(this@gps_locator, failure.message, Toast.LENGTH_SHORT).show()
        }

        override fun onRoutePlanned(route: Route) = Unit
    }

    private fun drawRoute(route: Route) {
        val instructions = route.mapInstructions()
        val routeOptions = RouteOptions(
            geometry = route.geometry,
            destinationMarkerVisible = true,
            departureMarkerVisible = true,
            instructions = instructions,
            routeOffset = route.routePoints.map { it.routeOffset }
        )
        tomTomMap.addRoute(routeOptions)
    }


    private fun Route.mapInstructions(): List<Instruction> {
        val routeInstructions = legs.flatMap { routeLeg -> routeLeg.instructions }
        return routeInstructions.map {
            Instruction(
                routeOffset = it.routeOffset
            )
        }
    }


    private fun startNavigation(route: Route) {
        initNavigationFragment()
        navigationFragment.setTomTomNavigation(tomTomNavigation)
        val routePlan = RoutePlan(route, routePlanningOptions)
        navigationFragment.startNavigation(routePlan)
        navigationFragment.addNavigationListener(navigationListener)
        tomTomNavigation.addProgressUpdatedListener(progressUpdatedListener)
        tomTomNavigation.addActiveRouteChangedListener(activeRouteChangedListener)
    }


    private val navigationListener = object : NavigationFragment.NavigationListener {
        override fun onStarted() {
            tomTomMap.addCameraChangeListener(cameraChangeListener)
            tomTomMap.cameraTrackingMode = CameraTrackingMode.FollowRouteDirection
            tomTomMap.enableLocationMarker(LocationMarkerOptions(LocationMarkerOptions.Type.Chevron))
            setMapMatchedLocationProvider()
            setSimulationLocationProviderToNavigation(route!!)
            setMapNavigationPadding()
        }

        override fun onStopped() {
            stopNavigation()
        }
    }


    private fun initNavigationFragment() {
        if (!::navigationFragment.isInitialized) {
            navigationFragment = NavigationFragment.newInstance(
                NavigationUiOptions(
                    keepInBackground = true
                )
            )
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.navigation_fragment_container, navigationFragment)
            .commitNow()
    }

    private val progressUpdatedListener = ProgressUpdatedListener {
        tomTomMap.routes.first().progress = it.distanceAlongRoute
    }

    private val activeRouteChangedListener = ActiveRouteChangedListener { route ->
        tomTomMap.removeRoutes()
        drawRoute(route)
    }

    private fun setSimulationLocationProviderToNavigation(route: Route) {
        val routeGeoLocations = route.geometry.map { GeoLocation(it) }
        val simulationStrategy = InterpolationStrategy(routeGeoLocations)
        locationProvider = SimulationLocationProvider.create(strategy = simulationStrategy)
        tomTomNavigation.locationProvider = locationProvider
        locationProvider.enable()
    }


    private fun stopNavigation() {
        navigationFragment.stopNavigation()
        mapFragment.currentLocationButton.visibilityPolicy =
            VisibilityPolicy.InvisibleWhenRecentered
        tomTomMap.removeCameraChangeListener(cameraChangeListener)
        tomTomMap.cameraTrackingMode = CameraTrackingMode.None
        tomTomMap.enableLocationMarker(LocationMarkerOptions(LocationMarkerOptions.Type.Pointer))
        resetMapPadding()
        navigationFragment.removeNavigationListener(navigationListener)
        tomTomNavigation.removeProgressUpdatedListener(progressUpdatedListener)
        tomTomNavigation.removeActiveRouteChangedListener(activeRouteChangedListener)
        clearMap()
        initLocationProvider()
        tomTomMap.setLocationProvider(locationProvider)
        locationProvider.enable()
        showUserLocation()
    }

    private fun setMapNavigationPadding() {
        val paddingBottom = resources.getDimensionPixelOffset(R.dimen.map_padding_bottom)
        val padding = Padding(0, 0, 0, paddingBottom)
        tomTomMap.setPadding(padding)
    }

    private fun resetMapPadding() {
        tomTomMap.setPadding(Padding(0, 0, 0, 0))
    }


    private fun setMapMatchedLocationProvider() {
        val mapMatchedLocationProvider = MapMatchedLocationProviderFactory.create(tomTomNavigation)
        tomTomMap.setLocationProvider(mapMatchedLocationProvider)
        mapMatchedLocationProvider.enable()
    }


    private fun clearMap() {
        tomTomMap.clear()
    }

    private val cameraChangeListener = CameraChangeListener {
        val cameraTrackingMode = tomTomMap.cameraTrackingMode
        if (cameraTrackingMode == CameraTrackingMode.FollowRouteDirection) {
            navigationFragment.navigationView.showSpeedView()
        } else {
            navigationFragment.navigationView.hideSpeedView()
        }
    }


    private fun areLocationPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    private fun requestLocationPermission(onLocationPermissionsGranted: () -> Unit) =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                onLocationPermissionsGranted()
            } else {
                Toast.makeText(
                    this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT
                ).show()
            }
        }.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

    override fun onDestroy() {
        tomTomMap.setLocationProvider(null)
        if (::navigationFragment.isInitialized) {
            supportFragmentManager.beginTransaction().remove(navigationFragment).commitNowAllowingStateLoss()
        }
        super.onDestroy()
        tomTomNavigation.close()
        navigationTileStore.close()
        locationProvider.close()
    }

    companion object {
        private const val ZOOM_TO_ROUTE_PADDING = 100
    }
}
