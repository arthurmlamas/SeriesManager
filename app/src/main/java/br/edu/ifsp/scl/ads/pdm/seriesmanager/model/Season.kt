package br.edu.ifsp.scl.ads.pdm.seriesmanager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Season (
    val seasonId: Long = 0,
    val seasonNumber: Int = 0,
    val releasedYear: String = "",
    val numOfEpisodes: Int = 0,
    val show: Show
): Parcelable