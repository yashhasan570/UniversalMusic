/*
 * Copyright (C) 2026 Kingkor Roy Tirtho and UniversalMusic Contributors
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

import org.universalmusic.universalmusic.core.audioplayer.AudioPlayer
import org.universalmusic.universalmusic.core.audioplayer.AudioPlayerInterface
import org.universalmusic.universalmusic.core.mediacontrol.SystemMediaControlService
import org.universalmusic.universalmusic.core.paths.Paths
import org.universalmusic.universalmusic.core.share.JvmShareService
import org.universalmusic.universalmusic.core.share.ShareService
import org.universalmusic.universalmusic.core.systemtray.SystemTrayService
import org.universalmusic.universalmusic.modules.library.local_tracks.media.JvmLocalMediaDiscoveryService
import org.universalmusic.universalmusic.modules.library.local_tracks.media.LocalMediaDiscoveryService
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import org.universalmusic.universalmusic.core.db.Database
import org.universalmusic.universalmusic.core.window.WindowStateRepository

actual val platformModules = module {
    singleOf(::Paths)
    single { WindowStateRepository(get<Database>()) }
    single<AudioPlayerInterface> { AudioPlayer(Unit) }
    single<LocalMediaDiscoveryService> { JvmLocalMediaDiscoveryService() }
    single<ShareService> { JvmShareService() }
    single { SystemTrayService(get(), get(), get()) }
    single { SystemMediaControlService(get()) } withOptions { createdAtStart() }
}
