package com.example.machinelearningkit.ui.screens.barcodeScanningScreen.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.machinelearningkit.common.connect.wifiConnect
import com.example.machinelearningkit.common.extensions.copyText
import com.example.machinelearningkit.common.intentEmail
import com.example.machinelearningkit.common.intentPhone
import com.example.machinelearningkit.common.intentUrl
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
                Barcode.TYPE_CONTACT_INFO -> BarcodeTypeContactInfo(barcode)
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

@ExperimentalPermissionsApi
@Composable
fun BarcodeTypeContactInfo(barcode: Barcode) {
    val context = LocalContext.current

    val permissionCallPhone = rememberPermissionState(permission = Manifest.permission.CALL_PHONE)

    val title = barcode.contactInfo?.title ?: ""
    val addresses = barcode.contactInfo?.addresses ?: emptyList()
    val emails = barcode.contactInfo?.emails ?: emptyList()
    val name = barcode.contactInfo?.name
    val phones = barcode.contactInfo?.phones ?: emptyList()
    val organization = barcode.contactInfo?.organization ?: ""
    val urls = barcode.contactInfo?.urls ?: emptyList()

    AlertDialogBarcode {
        LazyColumn(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                name?.let {
                    if ((name.first ?: "").isNotEmpty()){
                        Text(
                            text = "First name",
                            fontWeight = FontWeight.W900
                        )

                        BaseOutlinedTextField(text = name.first)
                    }
                    if ((name.last ?: "").isNotEmpty()){
                        Text(
                            text = "Last name",
                            fontWeight = FontWeight.W900
                        )

                        BaseOutlinedTextField(text = name.last)
                    }
                    if ((name.formattedName ?: "").isNotEmpty()){
                        Text(
                            text = "Formatted name",
                            fontWeight = FontWeight.W900
                        )

                        BaseOutlinedTextField(text = name.formattedName)
                    }
                    if ((name.middle ?: "").isNotEmpty()){
                        Text(
                            text = "Middle",
                            fontWeight = FontWeight.W900
                        )

                        BaseOutlinedTextField(text = name.middle)
                    }
                    if ((name.prefix ?: "").isNotEmpty()){
                        Text(
                            text = "Prefix",
                            fontWeight = FontWeight.W900
                        )

                        BaseOutlinedTextField(text = name.prefix)
                    }
                    if ((name.pronunciation ?: "").isNotEmpty()){
                        Text(
                            text = "Pronunciation",
                            fontWeight = FontWeight.W900
                        )

                        BaseOutlinedTextField(text = name.pronunciation)
                    }
                    if ((name.suffix ?: "").isNotEmpty()){
                        Text(
                            text = "Suffix",
                            fontWeight = FontWeight.W900
                        )

                        BaseOutlinedTextField(text = name.suffix)
                    }
                }
                if (title.isNotEmpty()){
                    Text(
                        text = "Title",
                        fontWeight = FontWeight.W900
                    )

                    BaseOutlinedTextField(text = title)
                }
                if (organization.isNotEmpty()){
                    Text(
                        text = "Organization",
                        fontWeight = FontWeight.W900
                    )

                    BaseOutlinedTextField(text = organization)
                }
                if (addresses.isNotEmpty()){
                    Text(
                        text = "Address",
                        fontWeight = FontWeight.W900
                    )
                    addresses.forEach { address ->
                        address.addressLines.forEach { lene ->
                            BaseOutlinedTextField(text = lene)
                        }
                    }
                }
                if (emails.isNotEmpty()){
                    Text(
                        text = "Emails",
                        fontWeight = FontWeight.W900
                    )
                    emails.forEach { email ->
                        val intentEmail = intentEmail(
                            address = email.address,
                            subject = email.subject,
                            body = email.address
                        )

                        BaseOutlinedTextField(text = email.address)

                        OutlinedButton(onClick = {
                            context.startActivity(intentEmail)
                        }) {
                            Text(text = "Open emails")
                        }
                    }
                }

                if (urls.isNotEmpty()){
                    Text(
                        text = "Urls",
                        fontWeight = FontWeight.W900
                    )

                    urls.forEach { url ->
                        val intentUrl = intentUrl(url)

                        BaseOutlinedTextField(text = url)

                        OutlinedButton(onClick = {
                            context.startActivity(intentUrl)
                        }) {
                            Text(text = "Open website")
                        }
                    }
                }

                if (phones.isNotEmpty()){
                    Text(
                        text = "Phones",
                        fontWeight = FontWeight.W900
                    )

                    phones.forEach { phone ->
                        val intentPhone = intentPhone(phone.number)

                        BaseOutlinedTextField(text = phone.number)

                        OutlinedButton(onClick = {
                            permissionCallPhone.launchPermissionRequest()
                            if (permissionCallPhone.hasPermission){
                                context.startActivity(intentPhone)
                            }
                        }) {
                            Text(text = "Call phone")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BarcodeTypeCalendarEvent(barcode: Barcode) {
    val context = LocalContext.current

    val startDate = barcode.calendarEvent?.start
    val endDate = barcode.calendarEvent?.end
    val description = barcode.calendarEvent?.description
    val location = barcode.calendarEvent?.location
    val organizer = barcode.calendarEvent?.organizer
    val status = barcode.calendarEvent?.status
    val summary = barcode.calendarEvent?.summary

    val intent = Intent(Intent.ACTION_INSERT)
    intent.apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate?.rawValue)
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate?.rawValue)
        putExtra(CalendarContract.Events.TITLE, summary)
        putExtra(CalendarContract.Events.DESCRIPTION, description)
        putExtra(CalendarContract.Events.EVENT_LOCATION, location)
        putExtra(CalendarContract.Events.ORGANIZER, organizer)
        putExtra(CalendarContract.Events.STATUS, status)
        putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
    }

    AlertDialogBarcode {
        OutlinedButton(onClick = { context.startActivity(intent) }) {
            Text(text = "Open calendar")
        }
    }
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
private fun BaseOutlinedTextField(
    modifier: Modifier = Modifier,
    text:String?
) {
    val context = LocalContext.current

    OutlinedTextField(
        modifier = modifier
            .padding(5.dp)
            .clickable {
                text?.copyText(context)
                Toast
                    .makeText(context, "Текст скопирован", Toast.LENGTH_SHORT)
                    .show()
            },
        value = text ?: "",
        onValueChange = {  },
        enabled = false,
        readOnly = true
    )
}

@Composable
private fun AlertDialogBarcode(
    barcodeResult: String? = null,
    title: @Composable () -> Unit = {},
    buttons: @Composable () -> Unit = {},
){
    var dialogVisible by rememberSaveable{ mutableStateOf(true) }

    if(dialogVisible){
        AlertDialog(
            shape = AbsoluteRoundedCornerShape(15.dp),
            onDismissRequest = { dialogVisible = false },
            title = title,
            text = { barcodeResult?.let { BaseOutlinedTextField(text = barcodeResult) } },
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
                val intent = intentUrl(url)
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
                val intentPhone = intentPhone(phone)
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

    val intent = intentEmail(address,subject,body)

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