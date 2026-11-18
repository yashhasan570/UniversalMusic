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


import org.spotube.millennium.composables.MillenniumIcon
import org.spotube.millennium.composables.MillenniumIconButton
import org.spotube.millennium.composables.MillenniumIconButtonStyle
import androidx.compose.foundation.shape.CircleShape

import org.universalmusic.universalmusic.core.ui.theming.AppTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.universalmusic.universalmusic.core.audioplayer.AudioPlayerQueue
import org.universalmusic.universalmusic.core.audioplayer.QueueEntry
import org.universalmusic.universalmusic.modules.saved_tracks.SavedState
import org.universalmusic.universalmusic.modules.saved_tracks.SavedTracksViewModel
import org.universalmusic.universalmusic.modules.saved_tracks.rememberIsSavedTracks
import org.universalmusic.universalmusic.resources.iconsax.Iconsax
import org.universalmusic.universalmusic.resources.iconsax.IconsaxHeart
import org.universalmusic.universalmusic.resources.iconsax.IconsaxHeart2
import org.jetbrains.compose.resources.stringResource
import universalmusic.composeapp.generated.resources.Res
import universalmusic.composeapp.generated.resources.player_like_content_description
import universalmusic.composeapp.generated.resources.player_unlike_content_description

@Composable
fun PlayerHeartButton(
    audioPlayerQueue: AudioPlayerQueue,
    savedTracksViewModel: SavedTracksViewModel) {
    val currentQueueEntry by audioPlayerQueue.currentQueueEntryFlow.collectAsStateWithLifecycle()
    val currentTrackId = (currentQueueEntry as? QueueEntry.StreamingTrack)?.track?.id
    val isSavedTrackState =
        rememberIsSavedTracks(trackIds = currentTrackId?.let { listOf(it) } ?: emptyList())
    val savedTrackIds by savedTracksViewModel.savedTrackIdsFlow.collectAsStateWithLifecycle()
    val pendingSavedTrackIds by savedTracksViewModel.pendingSavedTrackIds.collectAsStateWithLifecycle()

    val isInSavedIds = if (currentTrackId != null) currentTrackId in savedTrackIds else false
    val isLiked = if (isSavedTrackState is SavedState.Success) {
        isSavedTrackState.data.firstOrNull() == true || isInSavedIds
    } else {
        isInSavedIds
    }

    MillenniumIconButton(
        onClick = { currentTrackId?.let { savedTracksViewModel.toggleSavedTrack(it, isLiked) } },
        enabled = isSavedTrackState is SavedState.Success,
        loading = currentTrackId != null && currentTrackId in pendingSavedTrackIds,
        style = MillenniumIconButtonStyle.Secondary) {
        MillenniumIcon(
            imageVector = if (isLiked) Iconsax.IconsaxHeart2 else Iconsax.IconsaxHeart,
            contentDescription = stringResource(if (isLiked) Res.string.player_unlike_content_description else Res.string.player_like_content_description),
            tint = if (isLiked) AppTheme.colors.primary else AppTheme.colors.onSurfaceVariant)
    }
}
