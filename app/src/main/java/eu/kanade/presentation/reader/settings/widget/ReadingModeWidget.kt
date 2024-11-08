package eu.kanade.presentation.reader.settings.widget

import eu.kanade.presentation.more.settings.widget.BasePreferenceWidget
import eu.kanade.presentation.more.settings.widget.PrefsHorizontalPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eu.kanade.domain.ui.model.ThemeMode
//import eu.kanade.presentation.reader.settings.updateOrientation
import eu.kanade.tachiyomi.ui.reader.setting.ReaderOrientation
import eu.kanade.tachiyomi.ui.reader.setting.ReadingMode
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.SwitchItem
import tachiyomi.presentation.core.i18n.stringResource
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

private val options = mapOf(
    ReaderOrientation.PORTRAIT to ReaderOrientation.PORTRAIT.stringRes,
    ReaderOrientation.LANDSCAPE to ReaderOrientation.LANDSCAPE.stringRes,
    //ReaderOrientation.LOCKED_PORTRAIT to ReaderOrientation.LANDSCAPE.stringRes,
    //ReaderOrientation.LOCKED_LANDSCAPE to ReaderOrientation.LANDSCAPE.stringRes,
    //ReaderOrientation.REVERSE_PORTRAIT to ReaderOrientation.LANDSCAPE.stringRes,
    //ReaderOrientation.FREE to ReaderOrientation.LANDSCAPE.stringRes,
)

@Composable
internal fun ReadingModeWidget(
    value: ReaderOrientation,
    onChange: (ReaderOrientation) -> Unit,
) {
    val isLockOrientationEnabled = remember { mutableStateOf(value == ReaderOrientation.LOCKED_PORTRAIT || value == ReaderOrientation.LOCKED_LANDSCAPE) }
    val isReversePortraitEnabled = remember { mutableStateOf(value == ReaderOrientation.REVERSE_PORTRAIT) }

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
                            (mode == ReaderOrientation.PORTRAIT && value == ReaderOrientation.LOCKED_PORTRAIT) ||
                            (mode == ReaderOrientation.LANDSCAPE && value == ReaderOrientation.LOCKED_LANDSCAPE),
                        onCheckedChange = {
                            val newValue = when {
                                isLockOrientationEnabled.value && mode == ReaderOrientation.PORTRAIT -> ReaderOrientation.LOCKED_PORTRAIT
                                isLockOrientationEnabled.value && mode == ReaderOrientation.LANDSCAPE -> ReaderOrientation.LOCKED_LANDSCAPE
                                isReversePortraitEnabled.value && mode == ReaderOrientation.PORTRAIT -> ReaderOrientation.REVERSE_PORTRAIT
                                else -> mode
                            }
                            onChange(newValue)
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index,
                            options.size,
                        ),
                    ) {
                        Text(stringResource(labelRes))
                    }
                }
            }
        },
    )

    // Show "Reverse Portrait" switch only if Portrait or Locked Portrait is selected
    if (value == ReaderOrientation.PORTRAIT || value == ReaderOrientation.LOCKED_PORTRAIT || value == ReaderOrientation.REVERSE_PORTRAIT) {
        SwitchItem(
            label = stringResource(MR.strings.rotation_reverse_portrait),
            isChecked = isReversePortraitEnabled.value,
            onCheckedChange = { isChecked ->
                isReversePortraitEnabled.value = isChecked
                val newOrientation = if (isChecked) ReaderOrientation.REVERSE_PORTRAIT else ReaderOrientation.PORTRAIT
                onChange(
                    if (isLockOrientationEnabled.value) ReaderOrientation.LOCKED_PORTRAIT else newOrientation
                )
            }
        )
    }

    // Lock Orientation switch
    SwitchItem(
        label = "Lock Orientation",
        isChecked = isLockOrientationEnabled.value,
        onCheckedChange = { isChecked ->
            isLockOrientationEnabled.value = isChecked
            val newOrientation = when {
                isChecked && value == ReaderOrientation.PORTRAIT -> ReaderOrientation.LOCKED_PORTRAIT
                isChecked && value == ReaderOrientation.LANDSCAPE -> ReaderOrientation.LOCKED_LANDSCAPE
                isChecked && value == ReaderOrientation.REVERSE_PORTRAIT -> ReaderOrientation.LOCKED_PORTRAIT
                else -> if (value == ReaderOrientation.LOCKED_PORTRAIT || value == ReaderOrientation.LOCKED_LANDSCAPE) {
                    // Reset back to regular Portrait or Landscape when unlocking
                    if (value == ReaderOrientation.LOCKED_PORTRAIT) ReaderOrientation.PORTRAIT else ReaderOrientation.LANDSCAPE
                } else value
            }
            onChange(newOrientation)
        }
    )

    fun valueChanged()
    {

    }
}

