package br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season

import android.os.Parcelable
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import kotlinx.parcelize.Parcelize

@Parcelize
data class Season(
    var seasonId: Long? = 0,
    var seasonNumber: Int = 0,
    val releasedYear: Int = 0,
    var numOfEpisodes: Int = 0,
    val show: Show = Show()
): Parcelable