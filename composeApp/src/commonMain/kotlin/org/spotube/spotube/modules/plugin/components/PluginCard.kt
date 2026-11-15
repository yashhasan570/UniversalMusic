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

package org.universalmusic.universalmusic.modules.plugin.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.Role
import org.universalmusic.millennium.theme.MillenniumTheme
import org.universalmusic.millennium.theme.millenniumActionPointer
import org.universalmusic.millennium.theme.millenniumSelectedGelBrush
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import compose.icons.FeatherIcons
import compose.icons.feathericons.ChevronRight
import org.universalmusic.millennium.composables.MillenniumIcon
import org.universalmusic.millennium.composables.MillenniumSpokeSpinner
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import org.jetbrains.compose.resources.stringResource
import org.universalmusic.millennium.composables.MillenniumButton
import org.universalmusic.millennium.composables.MillenniumButtonStyle
import org.universalmusic.millennium.composables.MillenniumIconButton
import org.universalmusic.millennium.composables.MillenniumIconButtonStyle
import org.universalmusic.universalmusic.core.ui.component.AdaptiveDropdownBottomSheet
import org.universalmusic.universalmusic.core.ui.component.AdaptiveMenuItem
import org.universalmusic.universalmusic.core.ui.component.HeaderDisplayMode
import org.universalmusic.universalmusic.resources.iconsax.Iconsax3DotsMore
import org.universalmusic.universalmusic.resources.iconsax.IconsaxUserRemove
import universalmusic.composeapp.generated.resources.plugin_action_more
import org.universalmusic.universalmusic.core.ui.theming.AppTheme
import org.universalmusic.universalmusic.modules.plugin.PluginListItem
import org.universalmusic.universalmusic.resources.iconsax.Iconsax
import org.universalmusic.universalmusic.resources.iconsax.IconsaxBox
import org.universalmusic.universalmusic.resources.iconsax.IconsaxTrash
import universalmusic.composeapp.generated.resources.Res
import universalmusic.composeapp.generated.resources.plugin_action_details
import universalmusic.composeapp.generated.resources.plugin_action_login
import universalmusic.composeapp.generated.resources.plugin_action_logout
import universalmusic.composeapp.generated.resources.plugin_action_remove

/**
 * One installed plugin: logo, name, and what it does. The whole row opens its details, which
 * hold everything else (support, account). User-installed plugins also get a remove button
 * on the row; built-in ones can't be removed.
 */
@Composable
internal fun PluginCard(
    item: PluginListItem,
    onInfo: () -> Unit,
    onRemove: () -> Unit) {
    val plugin = item.plugin
    val colors = MillenniumTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val pressed by interaction.collectIsPressedAsState()
    val wash by animateFloatAsState(
        targetValue = when {
            pressed -> 0.24f
            hovered -> 0.12f
            else -> 0f
        },
        animationSpec = tween(140))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (wash > 0f) Modifier.background(millenniumSelectedGelBrush(colors, wash)) else Modifier)
            .hoverable(interaction)
            .millenniumActionPointer()
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClickLabel = stringResource(Res.string.plugin_action_details),
                onClick = onInfo)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        PluginIconWell(size = 40.dp) {
            PluginLogo(
                plugin = plugin,
                logoPath = item.logoPath,
                contentDescription = plugin.name,
                modifier = Modifier.fillMaxSize(),
                fallback = { PluginLogoFallback(Iconsax.IconsaxBox) })
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                plugin.name,
                style = AppTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            PluginRowMeta(item)
        }
        if (item.isRemoving) {
            MillenniumSpokeSpinner(size = 16.dp)
        } else {
            if (!item.isBuiltIn) {
                MillenniumIconButton(
                    onClick = onRemove,
                    style = MillenniumIconButtonStyle.Quiet) {
                    MillenniumIcon(
                        Iconsax.IconsaxTrash,
                        contentDescription = stringResource(Res.string.plugin_action_remove),
                        modifier = Modifier.size(16.dp),
                        tint = AppTheme.colors.error)
                }
            }
            MillenniumIcon(
                FeatherIcons.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = AppTheme.colors.onSurfaceVariant)
        }
    }
}

/** What the plugin does, in its ability colors, then who made it: "Audio · Universal Music Team". */
@Composable
private fun PluginRowMeta(item: PluginListItem) {
    val plugin = item.plugin
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        plugin.abilities.forEach { ability ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                MillenniumIcon(
                    ability.abilityIcon(),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = ability.accentColor())
                Text(
                    ability.displayLabel(),
                    style = AppTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = AppTheme.colors.onSurface,
                    maxLines = 1)
            }
        }
        if (plugin.author.isNotBlank()) {
            Text(
                "· ${plugin.author}",
                modifier = Modifier.weight(1f, fill = false),
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        }
    }
}

