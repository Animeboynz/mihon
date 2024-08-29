package eu.kanade.presentation.more.settings.widget

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import eu.kanade.domain.ui.model.AppTheme
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource
import kotlin.math.roundToInt

@Composable
internal fun ThemeColorPickerWidget(
    initialColor: Color,
    controller: ColorPickerController,
    onItemClick: (Color, AppTheme) -> Unit,
) {
    var selectedColor by remember { mutableStateOf(initialColor) }
    var showConfirmButton by remember { mutableStateOf(false) }
    var hexInput by remember { mutableStateOf(TextFieldValue(selectedColor.toHex())) }

    val wheelSize = with(LocalDensity.current) { 20.dp.toPx().roundToInt() }
    val wheelStrokeWidth = with(LocalDensity.current) { 2.dp.toPx() }

    // Remember a wheel bitmap
    val wheelBitmap = remember(wheelSize, wheelStrokeWidth) {
        val bitmap = Bitmap.createBitmap(wheelSize, wheelSize, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = wheelStrokeWidth
            isAntiAlias = true
        }

        // Draw the circle for wheel indicator
        canvas.drawCircle(
            wheelSize / 2f,
            wheelSize / 2f,
            wheelSize / 2f - wheelStrokeWidth,
            paint,
        )
        bitmap.asImageBitmap()
    }

    BasePreferenceWidget(
        subcomponent = {
            Column(
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.padding.large)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .padding(
                            vertical = MaterialTheme.padding.medium,
                        ),
                ) {
                    HsvColorPicker(
                        modifier = Modifier
                            .size(300.dp),
                        controller = controller,
                        wheelImageBitmap = wheelBitmap,
                        initialColor = initialColor,
                        onColorChanged = { colorEnvelope: ColorEnvelope ->
                            selectedColor = colorEnvelope.color
                            hexInput = TextFieldValue(selectedColor.toHex())
                            showConfirmButton = true
                        },
                    )
                }

//                CustomBrightnessSlider(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    controller = controller,
//                    initialColor = initialColor,
//                )
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(35.dp),
                    controller = controller,
                    initialColor = initialColor,
                    borderRadius = 24.dp,
                )

                // Hex display and input section
//                Text(
//                    text = "Selected Color: ${selectedColor.toHex()}",
//                    style = MaterialTheme.typography.bodyLarge,
//                    modifier = Modifier.padding(top = 16.dp)
//                )

                OutlinedTextField(
                    value = hexInput,
                    onValueChange = {
                        hexInput = it
                        val color = it.text.toColorOrNull()
                        if (color != null) {
                            selectedColor = color
                            //controller.wheelColor = color
                        }
                    },
                    label = { Text("Enter Hex Value") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                )

                AnimatedVisibility(
                    visible = showConfirmButton,
                    enter = fadeIn() + expandVertically(),
                    modifier = Modifier
                        .padding(top = MaterialTheme.padding.large),
                ) {
                    Button(
                        onClick = {
                            onItemClick(selectedColor, AppTheme.CUSTOM)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        content = {
                            Text(text = stringResource(MR.strings.action_confirm))
                        },
                    )
                }
            }
        },
    )
}

// Extension function to convert Color to Hex
private fun Color.toHex(): String {
    return String.format("#%02X%02X%02X", (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())
}

// Extension function to convert Hex String to Color
private fun String.toColorOrNull(): Color? {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: IllegalArgumentException) {
        null
    }
}
