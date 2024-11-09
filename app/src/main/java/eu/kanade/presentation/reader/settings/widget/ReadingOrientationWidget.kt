package eu.kanade.presentation.reader.settings.widget

import eu.kanade.presentation.more.settings.widget.BasePreferenceWidget
import eu.kanade.presentation.more.settings.widget.PrefsHorizontalPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropLandscape
import androidx.compose.material.icons.filled.CropPortrait
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.OnDeviceTraining
import androidx.compose.material.icons.filled.ScreenLockRotation
import androidx.compose.material3.Icon
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eu.kanade.tachiyomi.ui.reader.setting.ReaderOrientation
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.SwitchItem
import tachiyomi.presentation.core.i18n.stringResource
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import logcat.logcat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

private val options = mapOf(
    ReaderOrientation.PORTRAIT to ReaderOrientation.PORTRAIT.stringRes,
    ReaderOrientation.LANDSCAPE to ReaderOrientation.LANDSCAPE.stringRes,
)

private val orientationIcons = mapOf(
    ReaderOrientation.PORTRAIT to Icons.Default.CropPortrait,
    ReaderOrientation.LANDSCAPE to Icons.Default.CropLandscape,
    ReaderOrientation.LOCKED_PORTRAIT to Icons.Default.CropPortrait,
    ReaderOrientation.LOCKED_LANDSCAPE to Icons.Default.CropLandscape,
    ReaderOrientation.REVERSE_PORTRAIT to Icons.Default.CropPortrait,
)

@Composable
internal fun ReadingOrientationWidget(
    value: ReaderOrientation,
    onChange: (ReaderOrientation) -> Unit,
) {
//    val isLockOrientationEnabled = remember { mutableStateOf(false) }
//    val isReversePortraitEnabled = remember { mutableStateOf(false) }
//    val segmentedButtonChoice = remember { mutableStateOf(value) }

    var isLockOrientationEnabled by remember {
        mutableStateOf(value == ReaderOrientation.LOCKED_PORTRAIT || value == ReaderOrientation.LOCKED_LANDSCAPE)
    }
    var isReversePortraitEnabled by remember {
        mutableStateOf(value == ReaderOrientation.REVERSE_PORTRAIT)
    }
    var segmentedButtonChoice by remember {
        mutableStateOf(
            when (value) {
                ReaderOrientation.PORTRAIT, ReaderOrientation.LOCKED_PORTRAIT, ReaderOrientation.REVERSE_PORTRAIT -> ReaderOrientation.PORTRAIT
                ReaderOrientation.LANDSCAPE, ReaderOrientation.LOCKED_LANDSCAPE -> ReaderOrientation.LANDSCAPE
                else -> value
            }
        )
    }

    fun valueChanged()
    {
        val newValue = when {
            isLockOrientationEnabled && segmentedButtonChoice == ReaderOrientation.PORTRAIT -> ReaderOrientation.LOCKED_PORTRAIT
            isLockOrientationEnabled && segmentedButtonChoice == ReaderOrientation.LANDSCAPE -> ReaderOrientation.LOCKED_LANDSCAPE
            isReversePortraitEnabled -> ReaderOrientation.REVERSE_PORTRAIT
            else -> segmentedButtonChoice
        }
        logcat("Roshan") { "Segment: $segmentedButtonChoice Lock: $isLockOrientationEnabled Rev: $isReversePortraitEnabled Orientation: $newValue" }
        onChange(newValue)
    }

    BasePreferenceWidget(
        subcomponent = {
            MultiChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PrefsHorizontalPadding),
            ) {
                options.onEachIndexed { index, (mode, labelRes) ->
                    SegmentedButton(
                        checked = mode == value ||
                            (mode == ReaderOrientation.PORTRAIT && (value == ReaderOrientation.LOCKED_PORTRAIT || value == ReaderOrientation.REVERSE_PORTRAIT)) ||
                            (mode == ReaderOrientation.LANDSCAPE && value == ReaderOrientation.LOCKED_LANDSCAPE),
                        onCheckedChange = {
                            segmentedButtonChoice = mode
                            valueChanged()
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index,
                            options.size,
                        ),
                        icon = {
                            if (mode == value ||
                                (mode == ReaderOrientation.PORTRAIT && (value == ReaderOrientation.LOCKED_PORTRAIT || value == ReaderOrientation.REVERSE_PORTRAIT)) ||
                                (mode == ReaderOrientation.LANDSCAPE && value == ReaderOrientation.LOCKED_LANDSCAPE)) {
                                Icon(
                                    imageVector = orientationIcons[mode] ?: Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                                )
                            }
                        }
                    ) {
                        Text(stringResource(labelRes))
                    }
                }
            }
        },
    )

    if (value == ReaderOrientation.PORTRAIT || value == ReaderOrientation.LOCKED_PORTRAIT || value == ReaderOrientation.REVERSE_PORTRAIT) {
        SwitchItem(
            label = stringResource(MR.strings.rotation_reverse_portrait),
            isChecked = isReversePortraitEnabled,
            onCheckedChange = { isChecked ->
                isReversePortraitEnabled = isChecked
                valueChanged()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.OnDeviceTraining,
                    contentDescription = "OnDeviceTraining"
                )
            }
        )
    }

    if (value != ReaderOrientation.REVERSE_PORTRAIT)
    {
        SwitchItem(
            label = "Lock Orientation",
            isChecked = isLockOrientationEnabled,
            onCheckedChange = { isChecked ->
                isLockOrientationEnabled = isChecked
                valueChanged()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.ScreenLockRotation,
                    contentDescription = "ScreenLockRotation"
                )
            }
        )
    }

}

