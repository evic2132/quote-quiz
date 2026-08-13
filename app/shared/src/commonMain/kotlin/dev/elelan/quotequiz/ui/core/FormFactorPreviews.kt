package dev.elelan.quotequiz.ui.core

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Phone", device = Devices.PHONE, showBackground = true, showSystemUi = true)
@Preview(name = "Phone Landscape", device = "spec:width=891dp,height=411dp,dpi=420", showBackground = true)
@Preview(name = "Tablet", device = Devices.TABLET, showBackground = true)
@Preview(name = "Desktop", device = Devices.DESKTOP, showBackground = true)
annotation class FormFactorPreviews
