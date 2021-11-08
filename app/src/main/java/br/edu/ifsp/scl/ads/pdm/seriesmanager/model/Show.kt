package br.edu.ifsp.scl.ads.pdm.seriesmanager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Year

@Parcelize
data class Show (
    val title: String = "",
    val releasedYear: String = "",
    val broadcaster: String = "",
    val genre: String = ""
): Parcelable