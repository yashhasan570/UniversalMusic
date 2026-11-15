package org.universalmusic.universalmusic.modules.welcome.pages

import androidx.compose.runtime.Composable
import org.universalmusic.universalmusic.modules.plugin.PluginScreen

/** The same installation, discovery, permissions and sign-in flow as Settings, in-place. */
@Composable
fun PluginsPage() {
    PluginScreen(onboarding = true)
}
