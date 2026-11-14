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

package org.universalmusic.universalmusic.core.navigation

import androidx.navigation3.runtime.NavKey
import org.universalmusic.universalmusic.modules.album.AlbumScreen
import org.universalmusic.universalmusic.modules.artist.ArtistScreen
import org.universalmusic.universalmusic.modules.blacklist.BlacklistScreen
import org.universalmusic.universalmusic.modules.devices.DevicesScreen
import org.universalmusic.universalmusic.modules.devices.RemoteControlScreen
import org.universalmusic.universalmusic.modules.jam.JamScreen
import org.universalmusic.universalmusic.modules.home.HomeScreen
import org.universalmusic.universalmusic.modules.library.LibraryScreen
import org.universalmusic.universalmusic.modules.library.local_tracks.LocalFolderScreen
import org.universalmusic.universalmusic.modules.library.local_tracks.LocalFolderViewModel
import org.universalmusic.universalmusic.modules.lyrics.LyricsScreen
import org.universalmusic.universalmusic.modules.playlist.PlaylistScreen
import org.universalmusic.universalmusic.modules.plugin.PluginScreen
import org.universalmusic.universalmusic.modules.plugin.guard.MetadataPluginGuard
import org.universalmusic.universalmusic.modules.saved_tracks.SAVED_TRACKS_COLLECTION_ID
import org.universalmusic.universalmusic.modules.saved_tracks.SavedTracksScreen
import org.universalmusic.universalmusic.modules.search.SearchScreen
import org.universalmusic.universalmusic.modules.settings.SettingsScreen
import org.universalmusic.universalmusic.modules.settings.JamSettingsScreen
import org.universalmusic.universalmusic.modules.welcome.WelcomeScreen
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@Serializable
sealed interface Routes : NavKey {
    @Serializable
    data object Home : Routes

    @Serializable
    data object Library : Routes

    @Serializable
    data object Search : Routes

    @Serializable
    data object Settings : Routes

    @Serializable
    data object Plugins : Routes

    @Serializable
    data object WebView : Routes

    @Serializable
    data object Lyrics : Routes

    @Serializable
    data class Playlist(val playlistId: String) : Routes

    @Serializable
    data class Artist(val artistId: String) : Routes

    @Serializable
    data class Album(val albumId: String) : Routes

    @Serializable
    data object SavedTracks : Routes

    @Serializable
    data object Blacklist : Routes

    @Serializable
    data object RemoteControl : Routes

    @Serializable
    data object Devices : Routes

    @Serializable
    data object Jam : Routes

    @Serializable
    data object JamSettings : Routes

    @Serializable
    data object Welcome : Routes

    @Serializable
    data class LocalFolder(val folderPath: String) : Routes
}

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {
    singleOf(::NavigationCommands)
    navigation<Routes.Home> {
        MetadataPluginGuard {
            HomeScreen(viewModel = koinViewModel())
        }
    }
    navigation<Routes.Search> {
        MetadataPluginGuard {
            SearchScreen()
        }
    }
    navigation<Routes.Library> {
        // Not guarded as a whole: Local tracks and Downloads work without a plugin.
        LibraryScreen()
    }
    navigation<Routes.LocalFolder> {
        LocalFolderScreen(
            viewModel = koinViewModel(
                key = it.folderPath,
                parameters = { parametersOf(it.folderPath) }
            ),
            onBack = { get<NavigationCommands>().pop() })
    }
    navigation<Routes.Settings> {
        SettingsScreen(settingsViewModel = koinViewModel())
    }
    navigation<Routes.Plugins> {
        PluginScreen(viewModel = koinViewModel())
    }
    navigation<Routes.WebView> {}
    navigation<Routes.Playlist> {
        MetadataPluginGuard(backButton = true) {
            PlaylistScreen(
                playlistId = it.playlistId,
                viewModel = koinViewModel(
                    key = it.playlistId,
                    parameters = { parametersOf(it.playlistId) }
                ),
                audioPlayerQueue = get(),
                audioPlayer = get(),
                navigationCommands = get())
        }
    }
    navigation<Routes.Artist> {
        MetadataPluginGuard(backButton = true) {
            ArtistScreen(
                viewModel = koinViewModel(
                    key = it.artistId,
                    parameters = { parametersOf(it.artistId) }
                ),
                audioPlayerQueue = get(),
                audioPlayer = get(),
                navigationCommands = get())
        }
    }
    navigation<Routes.Album> {
        MetadataPluginGuard(backButton = true) {
            AlbumScreen(
                albumId = it.albumId,
                viewModel = koinViewModel(
                    key = it.albumId,
                    parameters = { parametersOf(it.albumId) }
                ),
                audioPlayerQueue = get(),
                audioPlayer = get(),
                navigationCommands = get())
        }
    }
    navigation<Routes.Lyrics> {
        LyricsScreen(viewModel = koinViewModel())
    }
    navigation<Routes.SavedTracks> {
        MetadataPluginGuard(backButton = true) {
            SavedTracksScreen(
                viewModel = koinViewModel(
                    key = SAVED_TRACKS_COLLECTION_ID,
                    parameters = { parametersOf(SAVED_TRACKS_COLLECTION_ID) }
                ),
                audioPlayerQueue = get(),
                audioPlayer = get(),
                navigationCommands = get())
        }
    }
    navigation<Routes.Blacklist> {
        BlacklistScreen()
    }
    navigation<Routes.Devices> {
        DevicesScreen()
    }
    navigation<Routes.RemoteControl> {
        RemoteControlScreen(
            onDisconnect = { get<NavigationCommands>().pop() }
        )
    }
    navigation<Routes.Jam> {
        JamScreen(navigationCommands = get())
    }
    navigation<Routes.JamSettings> {
        JamSettingsScreen(viewModel = koinViewModel())
    }
    navigation<Routes.Welcome> {
        WelcomeScreen(viewModel = koinViewModel())
    }
}
