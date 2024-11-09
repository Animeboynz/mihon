package eu.kanade.presentation.reader.settings.widget

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OnDeviceTraining
import androidx.compose.material.icons.outlined.VerticalAlignCenter
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import eu.kanade.tachiyomi.ui.reader.setting.ReaderSettingsScreenModel
import eu.kanade.tachiyomi.ui.reader.setting.ReadingMode
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.SettingsChipRow
import tachiyomi.presentation.core.components.SwitchItem
import tachiyomi.presentation.core.i18n.stringResource

@Composable
internal fun ReadingModeWidget (
    readingMode: ReadingMode,
    screenModel: ReaderSettingsScreenModel
){
    SettingsChipRow(MR.strings.pref_category_reading_mode) {
        ReadingMode.entries.map {
            if (it != ReadingMode.CONTINUOUS_VERTICAL) {
                FilterChip(
                    selected = it == readingMode ||
                        (it == ReadingMode.WEBTOON && readingMode == ReadingMode.CONTINUOUS_VERTICAL),
                    onClick = { screenModel.onChangeReadingMode(it) },
                    label = { Text(stringResource(it.stringRes)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = it.iconRes),
                            contentDescription = "Localized description",
                            Modifier.size(AssistChipDefaults.IconSize)
                        )
                    }
                )
            }
        }
    }
    if (readingMode == ReadingMode.WEBTOON || readingMode == ReadingMode.CONTINUOUS_VERTICAL) {
        SwitchItem(
            label = stringResource(ReadingMode.CONTINUOUS_VERTICAL.stringRes),
            isChecked = (readingMode == ReadingMode.CONTINUOUS_VERTICAL),
            onCheckedChange = { isChecked ->
                val newMode = if (isChecked) ReadingMode.CONTINUOUS_VERTICAL else ReadingMode.WEBTOON
                screenModel.onChangeReadingMode(newMode)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.VerticalAlignCenter,
                    contentDescription = "OnDeviceTraining"
                )
            }
        )
    }
}
