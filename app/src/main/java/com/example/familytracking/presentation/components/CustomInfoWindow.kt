package com.example.familytracking.presentation.components

import android.view.View
import android.widget.TextView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow
import com.example.familytracking.R

class CustomInfoWindow(layoutResId: Int, mapView: MapView) : InfoWindow(layoutResId, mapView) {

    override fun onOpen(item: Any?) {
        val marker = item as? org.osmdroid.views.overlay.Marker ?: return
        
        val titleView = mView.findViewById<TextView>(R.id.bubble_title)
        val snippetView = mView.findViewById<TextView>(R.id.bubble_description)
        
        titleView.text = marker.title
        snippetView.text = marker.snippet
        
        // Hide snippet if empty
        snippetView.visibility = if (marker.snippet.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun onClose() {
        // Nothing to do
    }
}
