package eu.kanade.presentation.browse.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun BaseBrowseItem(
    isIncognito: Boolean = false,
    modifier: Modifier = Modifier,
    onClickItem: () -> Unit = {},
    onLongClickItem: () -> Unit = {},
    icon: @Composable RowScope.() -> Unit = {},
    action: @Composable RowScope.() -> Unit = {},
    content: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .combinedClickable(
                onClick = onClickItem,
                onLongClick = onLongClickItem,
            )
            .padding(horizontal = MaterialTheme.padding.medium, vertical = MaterialTheme.padding.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BadgedBox(
            badge = {
                if (isIncognito) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.absoluteOffset(x = (-5).dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VisibilityOff,
                            contentDescription = stringResource(MR.strings.tracked_privately),
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            },
        ) {
            this@Row.icon()
        }
        //icon()
        content()
        action()
    }
}
