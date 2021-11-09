package br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Show(
    val title: String = "",
    val releasedYear: Int = 0,
    val broadcaster: String = "",
    val genre: String = ""
): Parcelable