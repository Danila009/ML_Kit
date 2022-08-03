package com.example.machinelearningkit.ui.screens.barcodeScanningScreen.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.machinelearningkit.common.connect.wifiConnect
import com.example.machinelearningkit.common.extensions.copyText
import com.example.machinelearningkit.ui.view.camera.model.SourceInfo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.mlkit.vision.barcode.common.Barcode

@ExperimentalPermissionsApi
@SuppressLint("WifiManagerLeak", "MissingPermission")
@Composable
fun DetectedBarcode(barcodes: List<Barcode>?, sourceInfo: SourceInfo) {
    barcodes?.let {

        val needToMirror = sourceInfo.isImageFlipped

        for (barcode in barcodes){
            val rawValue = barcode.rawValue.toString()

            when(barcode.valueType){
                Barcode.TYPE_URL  -> BarcodeTypeUrl(barcode)
                Barcode.TYPE_WIFI -> BarcodeTypeWifi(barcode)
                Barcode.TYPE_GEO -> BarcodeTypeGeo(barcode)
                Barcode.TYPE_PHONE -> BarcodeTypePhone(barcode)
                Barcode.TYPE_EMAIL -> BarcodeTypeEmail(barcode)
                Barcode.TYPE_SMS -> BarcodeTypeSms(barcode)
                Barcode.TYPE_CALENDAR_EVENT -> BarcodeTypeCalendarEvent(barcode)
                else -> {
                    AlertDialogBarcode(
                        barcodeResult = rawValue
                    )
                }
            }
        }

        Canvas(
            modifier = Modifier.fillMaxSize()
        ){
            for (barcode in barcodes){
                val corners = barcode.cornerPoints

                corners?.let {
                    drawPath(
                        path = Path().apply {
                            corners.forEachIndexed { index, point ->
                                if (index == 0) {
                                    if (needToMirror){
                                        moveTo(size.width - point.x, point.y.toFloat())
                                    }else {
                                        moveTo(point.x.toFloat(), point.y.toFloat())
                                    }
                                } else {
                                    if (needToMirror){
                                        lineTo(size.width - point.x, point.y.toFloat())
                                    }else{
                                        lineTo(point.x.toFloat(), point.y.toFloat())
                                    }
                                }
                            }
                        },
                        color = Color.Red,
                        style = Stroke(5f)
                    )
                }
            }
        }
    }
}

@Composable
fun BarcodeTypeCalendarEvent(barcode: Barcode) {
    
}

@Composable
fun BarcodeTypeSms(barcode: Barcode) {
    val context = LocalContext.current

    val phoneNumber = barcode.sms?.phoneNumber
    val message = barcode.sms?.message

    val smsIntent = Intent(Intent.ACTION_VIEW)
    smsIntent.type = "vnd.android-dir/mms-sms"
    smsIntent.putExtra("address", phoneNumber)
    smsIntent.putExtra("sms_body", message)

    AlertDialogBarcode(
        barcodeResult = "Number $phoneNumber \n Message $message"
    ) {
        OutlinedButton(onClick = {
            context.startActivity(smsIntent)
        }) {
            Text(text = "Open SMS")
        }
    }
}

@Composable
private fun AlertDialogBarcode(
    barcodeResult: String? = null,
    title: @Composable () -> Unit = {},
    buttons: @Composable () -> Unit = {},
){
    val context = LocalContext.current

    var dialogVisible by rememberSaveable{ mutableStateOf(true) }

    if(dialogVisible){
        AlertDialog(
            shape = AbsoluteRoundedCornerShape(15.dp),
            onDismissRequest = { dialogVisible = false },
            title = title,
            text = {
                barcodeResult?.let {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(5.dp)
                            .clickable {
                                barcodeResult.copyText(context)
                                Toast
                                    .makeText(context, "Текст скопирован", Toast.LENGTH_SHORT)
                                    .show()
                            },
                        value = barcodeResult,
                        onValueChange = {  },
                        enabled = false,
                        readOnly = true
                    )   
                }
            },
            buttons = buttons
        )
    }
}

@Composable
private fun BarcodeTypeUrl(barcode: Barcode) {
    val context = LocalContext.current

    AlertDialogBarcode(
        barcodeResult = barcode.url!!.url
    ) {
        OutlinedButton(onClick = {
            val title = barcode.url!!.title
            val url = barcode.url!!.url

            url?.let {
                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
                context.startActivity(intent)
            }
            title?.let {
                if (title.isNotEmpty()){
                    Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text(text = "Open website")
        }
    }
}

@ExperimentalPermissionsApi
@Composable
private fun BarcodeTypeGeo(barcode: Barcode) {

    val permissionLocation = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    val lat = barcode.geoPoint?.lat ?: 0.1
    val lng = barcode.geoPoint?.lng ?: 0.1

    LaunchedEffect(key1 = Unit, block = {
        permissionLocation.launchPermissionRequest()
    })

    AlertDialogBarcode(
        barcodeResult = barcode.rawValue,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GoogleMap(
                    modifier = Modifier
                        .padding(20.dp)
                        .clip(AbsoluteRoundedCornerShape(10.dp))
                        .fillMaxWidth()
                        .height(400.dp),
                    cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(LatLng(lat,lng), 17f)
                    },
                    properties = MapProperties(
                        isMyLocationEnabled = permissionLocation.hasPermission
                    )
                ) {
                    Marker(position = LatLng(lat,lng))
                }
            }
        }
    )

}

@Composable
private fun BarcodeTypeWifi(barcode: Barcode) {
    val context = LocalContext.current

    val ssid = barcode.wifi!!.ssid ?: ""
    val password = barcode.wifi!!.password ?: ""

    AlertDialogBarcode {
        OutlinedButton(onClick = {
            wifiConnect(
                context = context,
                ssid = ssid,
                password = password
            )
        }) {
            Text(text = "Connect Wife $ssid")
        }
    }
}

@ExperimentalPermissionsApi
@Composable
private fun BarcodeTypePhone(barcode: Barcode) {
    val context = LocalContext.current

    val permissionCallPhone = rememberPermissionState(permission = Manifest.permission.CALL_PHONE)

    val phone = barcode.phone?.number

    AlertDialogBarcode(
        barcodeResult = phone
    ){
        OutlinedButton(onClick = {
            permissionCallPhone.launchPermissionRequest()
            if (permissionCallPhone.hasPermission){
                val intentPhone = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone"))
                context.startActivity(intentPhone)
            }
        }) {
            Text(text = "Call phone")
        }
    }
}

@Composable
private fun BarcodeTypeEmail(barcode:Barcode){
    val context = LocalContext.current

    val address = barcode.email?.address
    val subject = barcode.email?.subject
    val body = barcode.email?.body

    val intent = Intent(Intent.ACTION_SENDTO)
        .apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, address)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

    AlertDialogBarcode(
        barcodeResult = "Address $address \n Subject $subject \n Body $body"
    ){
        OutlinedButton(onClick = {
            context.startActivity(intent)
        }) {
            Text(text = "Open email")
        }
    }
}