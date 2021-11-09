package br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode

import android.os.Parcelable
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import kotlinx.parcelize.Parcelize

@Parcelize
data class Episode (
    val episodeId: Long = 0,
    val episodeNumber: Int = 0,
    val title: String = "",
    val duration: Int = 0,
    val seenFlag: Boolean = false,
    val season: Season
): Parcelable