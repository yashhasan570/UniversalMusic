/*
 * Copyright (C) 2026 Kingkor Roy Tirtho and Universal Music Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.universalmusic.universalmusic.modules.shell

import com.composeunstyled.LocalTextStyle
import com.composeunstyled.Text
import org.spotube.millennium.composables.MillenniumBottomNavigationBar
import org.spotube.millennium.composables.MillenniumBottomNavigationItem
import org.spotube.millennium.composables.MillenniumIcon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.universalmusic.universalmusic.NavigationItem
import org.jetbrains.compose.resources.stringResource
import org.universalmusic.universalmusic.bottombarTabs
import org.universalmusic.universalmusic.core.navigation.NavigationState
import org.universalmusic.universalmusic.core.navigation.Navigator
import org.universalmusic.universalmusic.core.navigation.Routes
import org.universalmusic.universalmusic.modules.downloads.DownloadBadgeIndicator

@Composable
fun AppBottombar(
    navigator: Navigator,
    navigationState: NavigationState,
    modifier: Modifier = Modifier,
    libraryBadge: @Composable () -> Unit = { DownloadBadgeIndicator() }
) {
    MillenniumBottomNavigationBar(modifier = modifier) {
        bottombarTabs.filterIsInstance<NavigationItem.Tab<*>>()
            .forEach { tab ->
                val selected = navigationState.topLevelRoute == tab.route
                val title = stringResource(tab.title)
                MillenniumBottomNavigationItem(
                    selected = selected,
                    onClick = { navigator.navigate(tab.route) },
                    contentDescription = title,
                    icon = {
                        MillenniumIcon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            tint = LocalTextStyle.current.color
                        )
                    },
                    label = { Text(title) },
                    badge = if (tab.route == Routes.Library) {
                        libraryBadge
                    } else {
                        null
                    }
                )
            }
    }
}
