package com.danteyu.studio.moodietrail.network

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import com.danteyu.studio.moodietrail.util.Logger

/**
 * Created by George Yu on 2020/1/31.
 */

/**
 * Implementation of ConnectivityManager.NetworkCallback,
 * it stores every change of connectivity into NetworkState
 * @see NetworkState
 */
internal class NetworkCallbackImp(private val holder: NetworkStateImp) :
    ConnectivityManager.NetworkCallback() {

    private fun updateConnectivity(isAvailable: Boolean, network: Network) {
        holder.network = network
        holder.isConnected = isAvailable
    }

    //in case of a new network ( wifi enabled ) this is called first
    override fun onAvailable(network: Network) {
        Logger.i("[$network] - new network")
        updateConnectivity(true, network)
    }

    //this is called several times in a row, as capabilities are added step by step
    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        Logger.i("[$network] - network capability changed: $networkCapabilities")
        holder.networkCapabilities = networkCapabilities
    }

    //this is called after
    override fun onLost(network: Network) {
        Logger.i("[$network] - network lost")
        updateConnectivity(false, network)
    }

    override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
        holder.linkProperties = linkProperties
        Logger.i("[$network] - link changed: ${linkProperties.interfaceName}")
    }

    override fun onUnavailable() {
        Logger.i("Unavailable")
    }

    override fun onLosing(network: Network, maxMsToLive: Int) {
        Logger.i("[$network] - Losing with $maxMsToLive")
    }

    override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
        Logger.i("[$network] - Blocked status changed: $blocked")
    }

    companion object {
        private const val TAG = "NetworkCallbackImp"
    }
}