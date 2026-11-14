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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowRight
import org.universalmusic.universalmusic.core.ui.component.AdaptiveDialogBottomSheet
import org.universalmusic.universalmusic.core.ui.theming.AppTheme
import org.universalmusic.universalmusic.modules.plugin.PluginCapability
import org.universalmusic.universalmusic.modules.plugin.PluginEntry
import org.universalmusic.universalmusic.modules.plugin.CookieDomainPermission
import org.universalmusic.universalmusic.modules.plugin.PluginPermissionsSummary
import org.universalmusic.universalmusic.resources.iconsax.Iconsax
import org.universalmusic.universalmusic.resources.iconsax.IconsaxCheckCircle
import org.universalmusic.universalmusic.resources.iconsax.IconsaxEye
import org.universalmusic.universalmusic.resources.iconsax.IconsaxWifiSquare
import org.universalmusic.universalmusic.resources.iconsax.PhosphorDatabaseDuotone
import org.universalmusic.universalmusic.resources.iconsax.User
import okio.Path
import org.jetbrains.compose.resources.stringResource
import org.universalmusic.millennium.composables.MillenniumIcon
import org.universalmusic.millennium.composables.MillenniumSeparator
import universalmusic.composeapp.generated.resources.Res
import universalmusic.composeapp.generated.resources.common_cancel
import universalmusic.composeapp.generated.resources.common_close
import universalmusic.composeapp.generated.resources.plugin_permissions_api_diff
import universalmusic.composeapp.generated.resources.plugin_permissions_capability_network_desc
import universalmusic.composeapp.generated.resources.plugin_permissions_capability_network_title
import universalmusic.composeapp.generated.resources.plugin_permissions_capability_storage_desc
import universalmusic.composeapp.generated.resources.plugin_permissions_capability_storage_title
import universalmusic.composeapp.generated.resources.plugin_permissions_capability_webview_desc
import universalmusic.composeapp.generated.resources.plugin_permissions_capability_webview_title
import universalmusic.composeapp.generated.resources.plugin_permissions_compare_title
import universalmusic.composeapp.generated.resources.plugin_permissions_cookie_domain_subdomains
import universalmusic.composeapp.generated.resources.plugin_permissions_cookie_domains_none
import universalmusic.composeapp.generated.resources.plugin_permissions_cookie_domains_title
import universalmusic.composeapp.generated.resources.plugin_permissions_installed_label
import universalmusic.composeapp.generated.resources.plugin_permissions_new_badge
import universalmusic.composeapp.generated.resources.plugin_permissions_none
import universalmusic.composeapp.generated.resources.plugin_permissions_requested_title
import universalmusic.composeapp.generated.resources.plugin_permissions_supplied_label
import universalmusic.composeapp.generated.resources.plugin_version_label

@Composable
fun PluginPermissionDialog(
    pluginInfo: PluginEntry,
    title: String,
    message: String,
    confirmLabel: String?,
    existingPlugin: PluginEntry? = null,
    /** [pluginInfo]'s permissions, with what's new since [existingPlugin] flagged. */
    permissions: PluginPermissionsSummary,
    logoPath: Path? = null,
    onConfirm: (() -> Unit)? = null,
    isConfirming: Boolean = false,
    onDismiss: () -> Unit) {
    AdaptiveDialogBottomSheet(
        onDismiss = onDismiss,
        title = { PluginDialogTitle(title) },
        dialogTitle = title,
        actions = {
            PluginDialogActions(
                secondaryLabel = stringResource(
                    if (confirmLabel == null) Res.string.common_close else Res.string.common_cancel),
                onSecondary = onDismiss,
                primaryLabel = confirmLabel,
                onPrimary = onConfirm,
                primaryLoading = isConfirming)
        }) {
        PluginDialogBody {
            PluginPanel {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    PluginLogoTile(plugin = pluginInfo, logoPath = logoPath)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            pluginInfo.name,
                            style = AppTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            PluginPill(pluginInfo.author, icon = Iconsax.User)
                            PluginPill(
                                stringResource(Res.string.plugin_version_label, pluginInfo.version),
                                tint = AppTheme.colors.primary)
                        }
                    }
                }
                if (pluginInfo.description.isNotBlank()) {
                    Text(
                        pluginInfo.description,
                        modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis)
                }
            }

            PluginCallout(text = message, isWarning = confirmLabel == null)

            existingPlugin?.let {
                InstalledComparisonPanel(installedPlugin = it, incomingPlugin = pluginInfo)
            }

            PluginSectionLabel(
                stringResource(Res.string.plugin_permissions_requested_title),
                badge = permissions.capabilities.size.takeIf { it > 0 }?.toString())

            PluginPanel {
                if (permissions.capabilities.isEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PluginIconTile(Iconsax.IconsaxCheckCircle)
                        Text(
                            stringResource(Res.string.plugin_permissions_none),
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colors.onSurfaceVariant)
                    }
                } else {
                    permissions.capabilities.forEachIndexed { index, item ->
                        if (index > 0) MillenniumSeparator()
                        CapabilityRow(capability = item.capability, isNew = item.isNew)
                        if (item.capability == PluginCapability.WEBVIEW) {
                            CookieDomainsList(permissions.cookieDomains)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InstalledComparisonPanel(
    installedPlugin: PluginEntry,
    incomingPlugin: PluginEntry) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PluginSectionLabel(stringResource(Res.string.plugin_permissions_compare_title))
        PluginPanel {
            Column(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    VersionStat(
                        label = stringResource(Res.string.plugin_permissions_installed_label),
                        value = installedPlugin.version)
                    MillenniumIcon(
                        imageVector = FeatherIcons.ArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = AppTheme.colors.onSurfaceVariant)
                    VersionStat(
                        label = stringResource(Res.string.plugin_permissions_supplied_label),
                        value = incomingPlugin.version,
                        highlighted = true)
                }
                Text(
                    stringResource(
                        Res.string.plugin_permissions_api_diff,
                        installedPlugin.apiVersion,
                        incomingPlugin.apiVersion),
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun VersionStat(label: String, value: String, highlighted: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            label,
            style = AppTheme.typography.labelSmall,
            color = AppTheme.colors.onSurfaceVariant)
        Text(
            stringResource(Res.string.plugin_version_label, value),
            style = AppTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (highlighted) AppTheme.colors.primary else AppTheme.colors.onSurface)
    }
}

@Composable
private fun CapabilityRow(capability: PluginCapability, isNew: Boolean) {
    val (icon, label, description) = when (capability) {
        PluginCapability.PERSISTENT_STORAGE -> Triple(
            Iconsax.PhosphorDatabaseDuotone,
            stringResource(Res.string.plugin_permissions_capability_storage_title),
            stringResource(Res.string.plugin_permissions_capability_storage_desc))
        PluginCapability.NETWORK_REQUESTS -> Triple(
            Iconsax.IconsaxWifiSquare,
            stringResource(Res.string.plugin_permissions_capability_network_title),
            stringResource(Res.string.plugin_permissions_capability_network_desc))
        PluginCapability.WEBVIEW -> Triple(
            Iconsax.IconsaxEye,
            stringResource(Res.string.plugin_permissions_capability_webview_title),
            stringResource(Res.string.plugin_permissions_capability_webview_desc))
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PluginIconTile(icon)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                label,
                style = AppTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium)
            Text(
                description,
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.onSurfaceVariant)
        }
        if (isNew) NewPermissionPill()
    }
}

/** The hosts a WebView plugin may read cookies from, listed under its WebView capability. */
@Composable
private fun CookieDomainsList(domains: List<CookieDomainPermission>) {
    Column(
        // Indented to line up with the capability text, past the 14dp inset, 38dp icon tile and 12dp gap.
        modifier = Modifier.fillMaxWidth().padding(start = 64.dp, end = 14.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            stringResource(Res.string.plugin_permissions_cookie_domains_title),
            style = AppTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colors.onSurfaceVariant)
        if (domains.isEmpty()) {
            Text(
                stringResource(Res.string.plugin_permissions_cookie_domains_none),
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.onSurfaceVariant)
        }
        domains.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.domain,
                        style = AppTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                    item.subdomainsOf?.let { parent ->
                        Text(
                            stringResource(Res.string.plugin_permissions_cookie_domain_subdomains, parent),
                            style = AppTheme.typography.labelSmall,
                            color = AppTheme.colors.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                    }
                }
                if (item.isNew) NewPermissionPill()
            }
        }
    }
}

@Composable
private fun NewPermissionPill() {
    PluginPill(
        stringResource(Res.string.plugin_permissions_new_badge),
        tint = AppTheme.colors.error)
}
