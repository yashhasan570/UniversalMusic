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

package org.universalmusic.universalmusic.core.di

import org.koin.core.module.dsl.onClose
import org.universalmusic.universalmusic.core.network.HttpClientFactory
import co.touchlab.kermit.Logger
import org.universalmusic.universalmusic.core.audioplayer.AudioPlayerQueue
import org.universalmusic.universalmusic.core.audioplayer.AudioPlayerQueueRepository
import org.universalmusic.universalmusic.core.audioplayer.DeviceAudioPlayerQueue
import org.universalmusic.universalmusic.core.audioplayer.QueueStateRepository
import org.universalmusic.universalmusic.core.db.Database
import org.universalmusic.universalmusic.core.discovery.DeviceDiscoveryService
import org.universalmusic.universalmusic.core.discord.DiscordRpcService
import org.universalmusic.universalmusic.core.jam.JamRoomClient
import org.universalmusic.universalmusic.core.jam.JamRoomService
import org.universalmusic.universalmusic.core.navigation.navigationModule
import org.universalmusic.universalmusic.core.remote.RemoteControlClient
import org.universalmusic.universalmusic.core.remote.RemoteControlHandler
import org.universalmusic.universalmusic.core.remote.RemoteControlService
import org.universalmusic.universalmusic.core.remote.RemotePlaybackController
import org.universalmusic.universalmusic.core.scrobble.ScrobbleManager
import org.universalmusic.universalmusic.core.playback.CollectionPlaybackHelper
import org.universalmusic.universalmusic.core.server.AlternativeTracksRepository
import org.universalmusic.universalmusic.core.server.CacheManager
import org.universalmusic.universalmusic.core.server.LocalServer
import org.universalmusic.universalmusic.core.server.MatchedTracksRepository
import org.universalmusic.universalmusic.core.server.TrackSourceRepository
import org.universalmusic.universalmusic.core.server.StreamingUrlRepository
import org.universalmusic.universalmusic.core.webview.WebViewController
import org.universalmusic.universalmusic.modules.album.AlbumRepository
import org.universalmusic.universalmusic.modules.album.AlbumViewModel
import org.universalmusic.universalmusic.modules.artist.ArtistRepository
import org.universalmusic.universalmusic.modules.artist.ArtistViewModel
import org.universalmusic.universalmusic.modules.blacklist.BlacklistRepository
import org.universalmusic.universalmusic.modules.blacklist.BlacklistViewModel
import org.universalmusic.universalmusic.modules.devices.DevicesViewModel
import org.universalmusic.universalmusic.modules.devices.RemoteControlViewModel
import org.universalmusic.universalmusic.modules.shell.ConnectStatusViewModel
import org.universalmusic.universalmusic.modules.jam.JamViewModel
import org.universalmusic.universalmusic.modules.downloads.DownloadManager
import org.universalmusic.universalmusic.modules.downloads.DownloadsViewModel
import org.universalmusic.universalmusic.modules.home.HomeScreenRepository
import org.universalmusic.universalmusic.modules.home.HomeScreenViewModel
import org.universalmusic.universalmusic.modules.plugin.MetadataAuthViewModel
import org.universalmusic.universalmusic.modules.library.LibraryRepository
import org.universalmusic.universalmusic.modules.library.LibraryState
import org.universalmusic.universalmusic.modules.library.album.LibraryAlbumsViewModel
import org.universalmusic.universalmusic.modules.library.artist.LibraryArtistsViewModel
import org.universalmusic.universalmusic.modules.library.local_tracks.LibraryLocalTracksViewModel
import org.universalmusic.universalmusic.modules.library.local_tracks.LocalFolderViewModel
import org.universalmusic.universalmusic.modules.library.local_tracks.media.LocalMediaCacheRepository
import org.universalmusic.universalmusic.modules.library.local_tracks.media.LocalMediaFoldersConfig
import org.universalmusic.universalmusic.modules.library.local_tracks.media.LocalMediaLibraryCoordinator
import org.universalmusic.universalmusic.modules.library.playlist.LibraryPlaylistsViewModel
import org.universalmusic.universalmusic.modules.lyrics.LyricsViewModel
import org.universalmusic.universalmusic.modules.playlist.AddTracksToPlaylistViewModel
import org.universalmusic.universalmusic.modules.playlist.PlaylistRepository
import org.universalmusic.universalmusic.modules.playlist.PlaylistViewModel
import org.universalmusic.universalmusic.modules.plugin.AudioPluginSource
import org.universalmusic.universalmusic.modules.plugin.PluginManager
import org.universalmusic.universalmusic.modules.plugin.PluginProvider
import org.universalmusic.universalmusic.modules.plugin.PluginViewModel
import org.universalmusic.universalmusic.modules.plugin.guard.MetadataPluginGuardViewModel
import org.universalmusic.universalmusic.modules.plugin.ScrobblePluginSource
import org.universalmusic.universalmusic.modules.saved_tracks.SavedTracksRepository
import org.universalmusic.universalmusic.modules.saved_tracks.SavedTracksViewModel
import org.universalmusic.universalmusic.modules.search.SearchRepository
import org.universalmusic.universalmusic.modules.search.SearchScreenViewModel
import org.universalmusic.universalmusic.modules.update.AppUpdateService
import org.universalmusic.universalmusic.modules.update.PluginUpdateService
import org.universalmusic.universalmusic.modules.settings.SettingsProvider
import org.universalmusic.universalmusic.modules.settings.SettingsRepository
import org.universalmusic.universalmusic.modules.settings.UserSettingsStore
import org.universalmusic.universalmusic.modules.settings.UserSettingsSource
import org.universalmusic.universalmusic.modules.settings.SettingsViewModel
import org.universalmusic.universalmusic.modules.settings.JamSettingsViewModel
import org.universalmusic.universalmusic.modules.settings.LocalMediaFoldersViewModel
import org.universalmusic.universalmusic.modules.shell.AppShellViewModel
import org.universalmusic.universalmusic.modules.shell.PlayerOptionsViewModel
import org.universalmusic.universalmusic.modules.shell.alternative_track.AlternativeTrackContentViewModel
import org.universalmusic.universalmusic.modules.shell.player_queue.PlayerQueueContentViewModel
import org.universalmusic.universalmusic.modules.welcome.WelcomeRepository
import org.universalmusic.universalmusic.modules.welcome.WelcomeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import org.universalmusic.universalmusic.core.zipline.permissions.PublicSuffixListRepository

expect val platformModules: Module

val sharedModules = module {
    includes(navigationModule)
    singleOf(::Database)
    single { HttpClientFactory() } withOptions { onClose { it?.close() } }

    singleOf(::WebViewController)

    // Shell
    viewModelOf(::AppShellViewModel)
    viewModelOf(::PlayerOptionsViewModel)
    viewModelOf(::PlayerQueueContentViewModel)
    viewModelOf(::AlternativeTrackContentViewModel)

    // Home
    singleOf(::HomeScreenRepository)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::MetadataAuthViewModel)

    // Search
    singleOf(::SearchRepository)
    viewModelOf(::SearchScreenViewModel)

    // Library
    singleOf(::LibraryState)
    singleOf(::LibraryRepository)
    singleOf(::LocalMediaCacheRepository)
    singleOf(::LocalMediaFoldersConfig)
    singleOf(::LocalMediaLibraryCoordinator)
    viewModelOf(::LibraryPlaylistsViewModel)
    viewModelOf(::LibraryAlbumsViewModel)
    viewModelOf(::LibraryArtistsViewModel)
    viewModelOf(::LibraryLocalTracksViewModel)
    viewModel { (folderPath: String) -> LocalFolderViewModel(folderPath, get(), get()) }

    // Plugin system
    single { PublicSuffixListRepository.create(get(), get()) } withOptions { createdAtStart() }
    singleOf(::PluginManager) {
        bind<PluginProvider>()
        bind<AudioPluginSource>()
        bind<ScrobblePluginSource>()
    }
    viewModelOf(::PluginViewModel)
    viewModelOf(::MetadataPluginGuardViewModel)

    // Settings
    singleOf(::SettingsRepository) {
        bind<UserSettingsSource>()
        bind<UserSettingsStore>()
    }
    viewModelOf(::SettingsViewModel) { bind<SettingsProvider>() }
    viewModelOf(::JamSettingsViewModel)
    viewModel {
        val repository = get<SettingsRepository>()
        LocalMediaFoldersViewModel(repository.userSettings) { folders ->
            val current = repository.getCurrentSettings()
            repository.updateSettings(current.copy(localMediaFolders = folders))
        }
    }

    // Welcome / Onboarding
    singleOf(::WelcomeRepository)
    viewModelOf(::WelcomeViewModel)

    // Updates
    single { AppUpdateService(get(), get()) } withOptions { createdAtStart() }
    single { PluginUpdateService(get(), get()) } withOptions { createdAtStart() }

    // Downloads
    singleOf(::DownloadManager)
    viewModelOf(::DownloadsViewModel)

    // Playlist
    singleOf(::PlaylistRepository)
    viewModel { (playlistId: String) ->
        PlaylistViewModel(
            playlistId = playlistId,
            repository = get(),
            savedTracksRepository = get(),
            libraryRepository = get(),
            playbackHelper = get(),
            audioPlayerQueue = get(),
            blacklistRepository = get(),
            shareService = get(),
            downloadManager = get(),
            remotePlaybackController = get())
    }
    viewModel { (playlistId: String) -> AddTracksToPlaylistViewModel(playlistId, get(), get()) }

    // Saved Tracks
    singleOf(::SavedTracksRepository)
    viewModel {
        SavedTracksViewModel(
            repository = get(),
            playbackHelper = get(),
            audioPlayerQueue = get(),
            blacklistRepository = get(),
            libraryRepository = get(),
            shareService = get(),
            downloadManager = get(),
            remotePlaybackController = get())
    }

    // Artist
    singleOf(::ArtistRepository)
    viewModel { (artistId: String) ->
        ArtistViewModel(
            artistId = artistId,
            repository = get(),
            libraryRepository = get(),
            savedTracksRepository = get(),
            audioPlayerQueue = get(),
            blacklistRepository = get(),
            shareService = get(),
            downloadManager = get(),
            remotePlaybackController = get())
    }

    // Blacklist
    singleOf(::BlacklistRepository)
    viewModelOf(::BlacklistViewModel)
    viewModel { DevicesViewModel(get()) }
    viewModelOf(::RemoteControlViewModel)
    viewModelOf(::ConnectStatusViewModel)
    viewModel {
        JamViewModel(
            jamRoomService = get(),
            shareService = get(),
            settingsProvider = get())
    }

    // Album
    singleOf(::AlbumRepository)
    viewModel { (albumId: String) ->
        AlbumViewModel(
            albumId = albumId,
            repository = get(),
            savedTracksRepository = get(),
            playbackHelper = get(),
            libraryRepository = get(),
            audioPlayerQueue = get(),
            blacklistRepository = get(),
            shareService = get(),
            downloadManager = get(),
            remotePlaybackController = get())
    }

    // Lyrics
    viewModelOf(::LyricsViewModel)

    // Local playback proxy server
    single {
        CollectionPlaybackHelper(
            albumRepository = get(),
            playlistRepository = get(),
            savedTracksRepository = get(),
            artistRepository = get(),
            audioPlayerQueue = get(),
            blacklistRepository = get())
    }
    singleOf(::MatchedTracksRepository) { bind<TrackSourceRepository>() }
    single {
        StreamingUrlRepository(
            pluginManager = get(),
            audioPlayerQueue = get(),
            matchedTracksRepository = get(),
            settingsRepository = get())
    }
    singleOf(::CacheManager)
    singleOf(::AlternativeTracksRepository)
    singleOf(::LocalServer) withOptions {
        createdAtStart()
    }
    single { RemoteControlHandler(get(), get(), get(), get()) }
    single { RemoteControlClient(get()) }
    singleOf(::DeviceDiscoveryService)
    single { RemoteControlService(get(), get(), get()) } withOptions {
        createdAtStart()
    }
    single { RemotePlaybackController(get(), get(), get(), get(), get()) }
    singleOf(::JamRoomClient)
    single {
        JamRoomService(
            get(), get(), get(), get(), get(), get(), get(),
            evictCachedAudio = get<CacheManager>()::invalidateCacheEntry)
    }
    singleOf(::AudioPlayerQueueRepository) { bind<QueueStateRepository>() }
    single<AudioPlayerQueue> {
        DeviceAudioPlayerQueue(get(), get(), get(), get(), get())
    }

    single { DiscordRpcService(get(), get()) } withOptions { createdAtStart() }

    // Scrobbling
    single { ScrobbleManager(get(), get(), get()) } withOptions { createdAtStart() }

    factory { (tag: String?) ->
        if (tag != null) {
            Logger.withTag(tag)
        } else {
            // Default logger if no tag is provided
            Logger
        }
    }
}
