package eu.kanade.presentation.reader.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import eu.kanade.domain.manga.model.readerOrientation
import eu.kanade.domain.manga.model.readingMode
import eu.kanade.presentation.more.settings.Preference
import eu.kanade.tachiyomi.ui.reader.setting.ReaderOrientation
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import eu.kanade.tachiyomi.ui.reader.setting.ReaderSettingsScreenModel
import eu.kanade.tachiyomi.ui.reader.setting.ReadingMode
import eu.kanade.tachiyomi.ui.reader.viewer.webtoon.WebtoonViewer
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.CheckboxItem
import tachiyomi.presentation.core.components.HeadingItem
import tachiyomi.presentation.core.components.SettingsChipRow
import tachiyomi.presentation.core.components.SliderItem
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.collectAsState
import java.text.NumberFormat
import androidx.compose.material3.Icon
import eu.kanade.presentation.reader.settings.widget.ReadingOrientationWidget
import tachiyomi.presentation.core.components.SwitchItem
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.PanoramaHorizontal
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.mutableStateOf
import eu.kanade.presentation.more.settings.widget.BasePreferenceWidget
import eu.kanade.presentation.more.settings.widget.PrefsHorizontalPadding
import eu.kanade.presentation.reader.settings.widget.ReadingModeWidget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

@Composable
internal fun ColumnScope.ReadingModePage(screenModel: ReaderSettingsScreenModel) {
    Preference.PreferenceItem.InfoPreference("Reader settings for this series")

    HeadingItem("Layout")
    val manga by screenModel.mangaFlow.collectAsState()

    val readingMode = remember(manga) { ReadingMode.fromPreference(manga?.readingMode?.toInt()) }

    ReadingModeWidget(
        readingMode = readingMode,
        screenModel = screenModel
    )

    HorizontalDivider()

    val orientation = remember(manga) { ReaderOrientation.fromPreference(manga?.readerOrientation?.toInt()) }

    ReadingOrientationWidget(
        value = orientation,
        onChange = { selectedOrientation ->
            screenModel.onChangeOrientation(selectedOrientation)
        }
    )

    HorizontalDivider()

    val viewer by screenModel.viewerFlow.collectAsState()
    if (viewer is WebtoonViewer) {
        WebtoonViewerSettings(screenModel)
    } else {
        PagerViewerSettings(screenModel)
    }
}

@Composable
private fun ColumnScope.PagerViewerSettings(screenModel: ReaderSettingsScreenModel) {
    HeadingItem(MR.strings.pager_viewer)

    val navigationModePager by screenModel.preferences.navigationModePager().collectAsState()
    val pagerNavInverted by screenModel.preferences.pagerNavInverted().collectAsState()

    val imageScaleType by screenModel.preferences.imageScaleType().collectAsState()
    val imageScaleTypeIcons = arrayOf(
        // Add to enum?
        Icons.Filled.FitScreen,
        Icons.Filled.Image,
        Icons.Filled.CropSquare,
        Icons.Filled.FitScreen,
        Icons.Filled.Image,
        Icons.Filled.CropSquare,
    )
    SettingsChipRow(MR.strings.pref_image_scale_type) {
        ReaderPreferences.ImageScaleType.mapIndexed { index, it ->
            FilterChip(
                selected = imageScaleType == index + 1,
                onClick = { screenModel.preferences.imageScaleType().set(index + 1) },
                label = { Text(stringResource(it)) },
                leadingIcon = {
                    Icon(
                        imageVector  = imageScaleTypeIcons[index],
                        contentDescription = null,
                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                    )
                },
            )
        }
    }

    SwitchItem(
        label = stringResource(MR.strings.pref_crop_borders),
        pref = screenModel.preferences.cropBorders(),
        icon = {
            Icon(
                imageVector = Icons.Default.Crop,
                contentDescription = "ScreenLockRotation"
            )
        }
    )

    SwitchItem(
        label = stringResource(MR.strings.pref_navigate_pan),
        pref = screenModel.preferences.navigateToPan(),
        icon = {
            Icon(
                imageVector = Icons.Default.PanoramaHorizontal,
                contentDescription = "ScreenLockRotation"
            )
        }
    )

    HorizontalDivider()

    val zoomStart by screenModel.preferences.zoomStart().collectAsState()

//    SettingsChipRow(MR.strings.pref_zoom_start) {
//        ReaderPreferences.ZoomStart.mapIndexed { index, it ->
//            FilterChip(
//                selected = zoomStart == index + 1,
//                onClick = { screenModel.preferences.zoomStart().set(index + 1) },
//                label = { Text(stringResource(it)) },
//            )
//        }
//    }

    SwitchItem(
        label = "Automatic zoom start position",
        isChecked = zoomStart == 1,
        //onCheckedChange = { screenModel.preferences.zoomStart().set(1) },
        onCheckedChange = {
            if (zoomStart == 1) {
                screenModel.preferences.zoomStart().set(2)
            } else {
                screenModel.preferences.zoomStart().set(1)
            }
        },
    )

    BasePreferenceWidget(
        subcomponent = {
            MultiChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PrefsHorizontalPadding),
            ) {
                ReaderPreferences.ZoomStart.mapIndexed { index, it ->
                    if(it != MR.strings.zoom_start_automatic) {
                        SegmentedButton(
                            checked = zoomStart == index + 1,
                            onCheckedChange = { screenModel.preferences.zoomStart().set(index + 1) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index - 1,
                                ReaderPreferences.ZoomStart.size - 1,
                            ),
                        ) {
                            Text(stringResource(it))
                        }
                    }
                }
            }
        },
    )

    SwitchItem(
        label = stringResource(MR.strings.pref_landscape_zoom),
        pref = screenModel.preferences.landscapeZoom(),
    )

    HorizontalDivider()

    TapZonesItems(
        selected = navigationModePager,
        onSelect = screenModel.preferences.navigationModePager()::set,
        invertMode = pagerNavInverted,
        onSelectInvertMode = screenModel.preferences.pagerNavInverted()::set,
    )

    HorizontalDivider()

    val dualPageSplitPaged by screenModel.preferences.dualPageSplitPaged().collectAsState()
    CheckboxItem(
        label = stringResource(MR.strings.pref_dual_page_split),
        pref = screenModel.preferences.dualPageSplitPaged(),
    )

    if (dualPageSplitPaged) {
        CheckboxItem(
            label = stringResource(MR.strings.pref_dual_page_invert),
            pref = screenModel.preferences.dualPageInvertPaged(),
        )
    }
}

@Composable
private fun ColumnScope.WebtoonViewerSettings(screenModel: ReaderSettingsScreenModel) {
    val numberFormat = remember { NumberFormat.getPercentInstance() }

    HeadingItem(MR.strings.webtoon_viewer)

    val navigationModeWebtoon by screenModel.preferences.navigationModeWebtoon().collectAsState()
    val webtoonNavInverted by screenModel.preferences.webtoonNavInverted().collectAsState()
    TapZonesItems(
        selected = navigationModeWebtoon,
        onSelect = screenModel.preferences.navigationModeWebtoon()::set,
        invertMode = webtoonNavInverted,
        onSelectInvertMode = screenModel.preferences.webtoonNavInverted()::set,
    )

    CheckboxItem(
        label = stringResource(MR.strings.pref_crop_borders),
        pref = screenModel.preferences.cropBordersWebtoon(),
    )

    val dualPageSplitWebtoon by screenModel.preferences.dualPageSplitWebtoon().collectAsState()
    CheckboxItem(
        label = stringResource(MR.strings.pref_dual_page_split),
        pref = screenModel.preferences.dualPageSplitWebtoon(),
    )

    if (dualPageSplitWebtoon) {
        CheckboxItem(
            label = stringResource(MR.strings.pref_dual_page_invert),
            pref = screenModel.preferences.dualPageInvertWebtoon(),
        )
    }

    val dualPageRotateToFitWebtoon by screenModel.preferences.dualPageRotateToFitWebtoon().collectAsState()
    CheckboxItem(
        label = stringResource(MR.strings.pref_page_rotate),
        pref = screenModel.preferences.dualPageRotateToFitWebtoon(),
    )

    if (dualPageRotateToFitWebtoon) {
        CheckboxItem(
            label = stringResource(MR.strings.pref_page_rotate_invert),
            pref = screenModel.preferences.dualPageRotateToFitInvertWebtoon(),
        )
    }

    CheckboxItem(
        label = stringResource(MR.strings.pref_double_tap_zoom),
        pref = screenModel.preferences.webtoonDoubleTapZoomEnabled(),
    )
    CheckboxItem(
        label = stringResource(MR.strings.pref_webtoon_disable_zoom_out),
        pref = screenModel.preferences.webtoonDisableZoomOut(),
    )
}

@Composable
private fun ColumnScope.TapZonesItems(
    selected: Int,
    onSelect: (Int) -> Unit,
    invertMode: ReaderPreferences.TappingInvertMode,
    onSelectInvertMode: (ReaderPreferences.TappingInvertMode) -> Unit,
) {

    SwitchItem(
        label = "Enable Tap Zones",
        isChecked = selected != 5,
        onCheckedChange = {
            if (selected != 5) {
                onSelect(5)
            } else {
                onSelect(0)
            }
        },

    )

    if (selected != 5) {
        SettingsChipRow(MR.strings.pref_viewer_nav) {
            ReaderPreferences.TapZones.mapIndexed { index, it ->
                if (index != 5) {
                    FilterChip(
                        selected = selected == index,
                        onClick = { onSelect(index) },
                        label = { Text(stringResource(it)) },
                    )
                }
            }
        }

        var shouldShow by remember { mutableStateOf(invertMode != ReaderPreferences.TappingInvertMode.NONE) }

        SwitchItem(
            label = stringResource(MR.strings.pref_read_with_tapping_inverted),
            isChecked = shouldShow,
            onCheckedChange = {
                if (shouldShow) onSelectInvertMode(ReaderPreferences.TappingInvertMode.NONE)
                shouldShow = !shouldShow
            },
        )

        if (shouldShow) {
            val options = mapOf(
                ReaderPreferences.TappingInvertMode.BOTH to ReaderPreferences.TappingInvertMode.BOTH.titleRes,
                ReaderPreferences.TappingInvertMode.HORIZONTAL to ReaderPreferences.TappingInvertMode.HORIZONTAL.titleRes,
                ReaderPreferences.TappingInvertMode.VERTICAL to ReaderPreferences.TappingInvertMode.VERTICAL.titleRes,
            )
            BasePreferenceWidget(
                subcomponent = {
                    MultiChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = PrefsHorizontalPadding),
                    ) {
                        options.onEachIndexed { index, (mode, labelRes) ->
                            SegmentedButton(
                                checked = mode == invertMode,
                                onCheckedChange = { onSelectInvertMode(mode) },
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
        }
    }
}
