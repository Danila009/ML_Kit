package com.example.machinelearningkit.common.connect

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build

private const val WIFICIPHER_NOPASS = 0
private const val WIFICIPHER_WEP = 1
private const val WIFICIPHER_WPA = 2

@SuppressLint("WifiManagerPotentialLeak", "MissingPermission")
fun wifiConnect(
    context: Context,
    ssid: String,
    password: String,
) {
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    val wifiConfig = createWifiCfg(
        ssid = ssid,
        password = password,
        type = WIFICIPHER_WPA
    )

    val netId = wifiManager.addNetwork(wifiConfig)

    wifiManager.disconnect()
    wifiManager.enableNetwork(netId, true)
    wifiManager.reconnect()

//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        val wifiConfig = WifiNetworkSuggestion.Builder()
//            .setSsid(ssid ?: "")
//            .setWpa2Passphrase(password ?: "")
//            .build()
//
//        val netId = wifiManager.addNetworkSuggestions(listOf(wifiConfig))
//
//        wifiManager.disconnect()
//        wifiManager.enableNetwork(netId, true)
//        wifiManager.reconnect()
//    } else {
//        val wifiConfig = WifiConfiguration()
//        wifiConfig.SSID = "\"" + ssid + "\""
//        wifiConfig.wepKeys[0] = "\"" + password + "\""
//        wifiConfig.wepTxKeyIndex = 0
//        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
//        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
//        wifiConfig.preSharedKey = "\""+ password +"\""
//
//        val netId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            wifiManager.addNetworkPrivileged(wifiConfig).networkId
//        } else {
//            wifiManager.addNetwork(wifiConfig)
//        }
//
//        wifiManager.disconnect()
//        wifiManager.enableNetwork(netId, true)
//        wifiManager.reconnect()
//    }
}

fun createWifiCfg(ssid: String, password: String, type: Int): WifiConfiguration {
    val config = WifiConfiguration()
    config.allowedAuthAlgorithms.clear()
    config.allowedGroupCiphers.clear()
    config.allowedKeyManagement.clear()
    config.allowedPairwiseCiphers.clear()
    config.allowedProtocols.clear()
    config.SSID = "\"" + ssid + "\""
    when (type) {
        WIFICIPHER_NOPASS -> {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        }
        WIFICIPHER_WEP -> {
            config.hiddenSSID = true
            config.wepKeys[0] = "\"" + password + "\""
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            config.wepTxKeyIndex = 0
        }
        WIFICIPHER_WPA -> {
            config.preSharedKey = "\"" + password + "\""
            config.hiddenSSID = true
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            config.status = WifiConfiguration.Status.ENABLED
        }
    }
    return config
}