package com.example.machinelearningkit.ui.screens.barcodeGeneratorScreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.text.format.DateFormat.is24HourFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.machinelearningkit.R
import com.example.machinelearningkit.ui.screens.barcodeGeneratorScreen.generateBarCode.generateBarCode
import com.example.machinelearningkit.ui.screens.barcodeGeneratorScreen.enums.EncryptionWifi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.zxing.BarcodeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private enum class BarcodeGenerateType {
    TEXT,
    WIFI,
    GEO,
    EMAIL,
    SMS,
    PHONE,
    EVENT
}

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BarcodeGenerateScreen() {

    var tabSelected by rememberSaveable { mutableStateOf(BarcodeGenerateType.TEXT) }

    var barcodeFormat by rememberSaveable { mutableStateOf(BarcodeFormat.QR_CODE) }

    Scaffold(
        topBar = {
            Column {

                ScrollableTabRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    selectedTabIndex = tabSelected.ordinal
                ){
                    BarcodeGenerateType.values().forEach { item ->
                        Tab(
                            selected = tabSelected == item,
                            onClick = { tabSelected = item },
                            text = { Text(text = item.name) }
                        )
                    }
                }

                LazyRow {
                    item {
                        BarcodeFormat.values().forEach { item ->
                            Chip(
                                modifier = Modifier
                                    .padding(5.dp),
                                onClick = { barcodeFormat = item },
                                leadingIcon = {
                                    if (barcodeFormat == item){
                                        Icon(
                                            imageVector = Icons.Default.Checklist,
                                            contentDescription = null,
                                            tint = Color.Cyan
                                        )
                                    }
                                }
                            ) {
                                Text(text = item.name)
                            }
                        }
                    }
                }
            }
        }, content = {
            LazyColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    when(tabSelected){
                        BarcodeGenerateType.TEXT -> BarcodeGenerateTypeText(barcodeFormat)
                        BarcodeGenerateType.WIFI -> BarcodeGenerateTypeWifi(barcodeFormat)
                        BarcodeGenerateType.GEO -> BarcodeGenerateTypeGeo(barcodeFormat)
                        BarcodeGenerateType.EMAIL -> BarcodeGenerateTypeEmail(barcodeFormat)
                        BarcodeGenerateType.SMS -> BarcodeGenerateTypeSms(barcodeFormat)
                        BarcodeGenerateType.PHONE -> BarcodeGenerateTypePhone(barcodeFormat)
                        BarcodeGenerateType.EVENT -> BarcodeGenerateTypeEvent(barcodeFormat)
                    }
                }
            }
        }
    )
}

@Composable
fun BarcodeGenerateTypeEvent(barcodeFormat: BarcodeFormat) {
    val context = LocalContext.current

    val withScreen = LocalConfiguration.current.screenWidthDp.dp

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var startDate by rememberSaveable { mutableStateOf("") }
    var startTime by rememberSaveable { mutableStateOf("") }
    var endDate by rememberSaveable { mutableStateOf("") }
    var endTime by rememberSaveable { mutableStateOf("") }

    val isSystem24Hour = is24HourFormat(context)

    val calendar = Calendar.getInstance()

    val startTimePicker = TimePickerDialog.OnTimeSetListener { _, hours, minutes ->
        calendar[Calendar.MINUTE] = minutes
        calendar[Calendar.HOUR_OF_DAY] = hours
        startTime = "$hours$minutes"
    }

    val endTimePicker = TimePickerDialog.OnTimeSetListener { _, hours, minutes ->
        calendar[Calendar.MINUTE] = minutes
        calendar[Calendar.HOUR_OF_DAY] = hours
        endTime = "$hours$minutes"
    }

    val startTimeDialog = TimePickerDialog(
        context,
        startTimePicker,
        Calendar.HOUR_OF_DAY,
        Calendar.MINUTE,
        isSystem24Hour
    )

    val endTimeDialog = TimePickerDialog(
        context,
        endTimePicker,
        Calendar.HOUR_OF_DAY,
        Calendar.MINUTE,
        isSystem24Hour
    )

    val startDatePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        startDate = "$year$month$dayOfMonth"
    }

    val endDatePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        endDate = "$year$month$dayOfMonth"
    }

    val startDateDialog = DatePickerDialog(
        context,
        startDatePicker,
        Calendar.YEAR,
        Calendar.MONTH,
        Calendar.DAY_OF_MONTH
    )

    val endDateDialog = DatePickerDialog(
        context,
        endDatePicker,
        Calendar.YEAR,
        Calendar.MONTH,
        Calendar.DAY_OF_MONTH
    )

    startTimeDialog.setTitle(R.string.app_name)
    endTimeDialog.setTitle(R.string.app_name)
    
    startDateDialog.setTitle(R.string.app_name)
    endDateDialog.setTitle(R.string.app_name)

    var barCodeBitmap:Bitmap? by rememberSaveable{ mutableStateOf(null) }

    LaunchedEffect(
        title,description,location,startDate,endDate,startTime,endTime,barcodeFormat
    ){
        barCodeBitmap = generateBarCode("" +
                "BEGIN:VEVENT" +
                "SUMMARY:$title" +
                "DESCRIPTION:$description" +
                "LOCATION:$location" +
                "DTSTART:${startDate}T${startTime}Z" +
                "DTEND:${endDate}T${endTime}Z" +
                "END:VEVENT",
            barcodeFormat
        )
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = title,
        onValueChange = { title = it },
        label = { Text(text = "Title") }
    )

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = description,
        onValueChange = { description = it },
        label = { Text(text = "Description") }
    )

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = location,
        onValueChange = { location = it },
        label = { Text(text = "Location") }
    )

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            modifier = Modifier
                .width(withScreen / 2)
                .padding(5.dp)
                .clickable {
                    startDateDialog.show()
                    startTimeDialog.show()
                },
            value = "${startDate}T${startTime}Z",
            onValueChange = {},
            enabled = false,
            readOnly = true,
            label = { Text(text = "Start date and time") }
        )

        OutlinedTextField(
            modifier = Modifier
                .width(withScreen / 2)
                .padding(5.dp)
                .clickable {
                    endDateDialog.show()
                    endTimeDialog.show()
                },
            value = "${endDate}T${endTime}Z",
            onValueChange = {},
            enabled = false,
            readOnly = true,
            label = { Text(text = "End date and time") }
        )
    }

    barCodeBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .padding(15.dp)
                .size(300.dp)
        )
    }
}

@Composable
fun BarcodeGenerateTypePhone(barcodeFormat: BarcodeFormat) {

    var phoneNumber by rememberSaveable { mutableStateOf("") }

    var barCodeBitmap:Bitmap? by rememberSaveable{ mutableStateOf(null) }

    LaunchedEffect(
        phoneNumber, barcodeFormat
    ){
        barCodeBitmap = generateBarCode("tel:$phoneNumber", barcodeFormat)
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = phoneNumber,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone
        ),
        onValueChange = { phoneNumber = it },
        label = { Text(text = "Phone Number") }
    )

    barCodeBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .padding(15.dp)
                .size(300.dp)
        )
    }
}

@Composable
fun BarcodeGenerateTypeSms(barcodeFormat: BarcodeFormat) {

    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }

    var barCodeBitmap:Bitmap? by rememberSaveable{ mutableStateOf(null) }

    LaunchedEffect(
        phoneNumber,message,barcodeFormat
    ){
        barCodeBitmap = generateBarCode("SMSTO:$phoneNumber:$message", barcodeFormat)
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = phoneNumber,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone
        ),
        onValueChange = { phoneNumber = it },
        label = { Text(text = "Номер") }
    )

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = message,
        onValueChange = { message = it },
        label = { Text(text = "Сообщение") }
    )

    barCodeBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .padding(15.dp)
                .size(300.dp)
        )
    }
}

@Composable
fun BarcodeGenerateTypeEmail(barcodeFormat: BarcodeFormat) {

    var address by rememberSaveable { mutableStateOf("") }
    var subject by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }

    var barCodeBitmap:Bitmap? by rememberSaveable{ mutableStateOf(null) }

    LaunchedEffect(
        address,subject,body,barcodeFormat
    ){
        barCodeBitmap = generateBarCode("MATMSG:TO:$address;SUB:$subject;BODY:$body;;", barcodeFormat)
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = address,
        onValueChange = { address = it },
        label = { Text(text = "Электронная почта") }
    )

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = subject,
        onValueChange = { subject = it },
        label = { Text(text = "Тема") }
    )

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = body,
        onValueChange = { body = it },
        label = { Text(text = "Сообщение") }
    )

    barCodeBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .padding(15.dp)
                .size(300.dp)
        )
    }
}

@ExperimentalPermissionsApi
@Composable
fun BarcodeGenerateTypeGeo(barcodeFormat: BarcodeFormat) {

    var lat by rememberSaveable { mutableStateOf(0.1) }
    var lng by rememberSaveable { mutableStateOf(0.1) }

    var barCodeBitmap:Bitmap? by rememberSaveable{ mutableStateOf(null) }

    val geoPermission = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(key1 = Unit, block = {
        geoPermission.launchPermissionRequest()
    })

    LaunchedEffect(
        lat,lng,barcodeFormat
    ){
        launch(Dispatchers.IO){
            barCodeBitmap = generateBarCode("geo:$lat,$lng", barcodeFormat)
        }
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        properties = MapProperties(
            isMyLocationEnabled = geoPermission.hasPermission
        ),
        onMapClick = {
            lat = it.latitude
            lng = it.longitude
        },
        content = {
            Marker(position = LatLng(lat,lng))
        }
    )

    barCodeBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .padding(15.dp)
                .size(300.dp)
        )
    }

}

@ExperimentalMaterialApi
@Composable
private fun BarcodeGenerateTypeWifi(barcodeFormat: BarcodeFormat) {

    var ssid by rememberSaveable{ mutableStateOf("") }
    var password by rememberSaveable{ mutableStateOf("") }
    var hidden by rememberSaveable{ mutableStateOf(false) }
    var encryption by rememberSaveable{ mutableStateOf(EncryptionWifi.WPA) }

    var barCodeBitmap:Bitmap? by rememberSaveable{ mutableStateOf(null) }

    LaunchedEffect(
        ssid,password,hidden,encryption,barcodeFormat
    ) {
        try {
            if (ssid.isNotEmpty() && password.isNotEmpty()){
                barCodeBitmap = generateBarCode(
                    "WIFI:T:$encryption;P:$password;S:$ssid;H:$hidden;",barcodeFormat
                )
            }
        }catch (e:Exception){}
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = ssid,
        onValueChange = { ssid = it },
        label = { Text(text = "Ssid") }
    )

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = password,
        onValueChange = { password = it },
        label = { Text(text = "Password") }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = hidden,
                onClick = { hidden = !hidden }
            )

            Text(
                text = "Hidden",
                color = if (hidden) Color.Cyan else Color.Unspecified
            )
        }

        Row {
            EncryptionWifi.values().forEach { item ->
                Chip(
                    modifier = Modifier
                        .padding(5.dp),
                    onClick = { encryption = item },
                    leadingIcon = {
                        if (encryption == item){
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = null,
                                tint = Color.Cyan
                            )
                        }
                    }
                ) {
                    Text(text = item.title)
                }
            }
        }
    }

    barCodeBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .padding(15.dp)
                .size(300.dp)
        )
    }
}

@Composable
private fun BarcodeGenerateTypeText(barcodeFormat:BarcodeFormat) {

    var barCodeText by rememberSaveable{ mutableStateOf("") }

    var barCodeBitmap:Bitmap? by rememberSaveable{ mutableStateOf(null) }

    LaunchedEffect(
        barCodeText, barcodeFormat,
        block = {
            launch(Dispatchers.IO){
                if (barCodeText.isNotEmpty()){
                    barCodeBitmap = generateBarCode(barCodeText,barcodeFormat)
                }
            }
        }
    )

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = barCodeText,
        onValueChange = { barCodeText = it },
        label = { Text(text = "Text") }
    )

    barCodeBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .padding(15.dp)
                .size(300.dp)
        )
    }
}