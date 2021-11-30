package br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.firebase.dao

import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.SeasonDAO
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class FirebaseSeasonDAO : SeasonDAO {
    companion object {
        private const val BD_SERIES_MANAGER = "seasons"
    }

    private val seriesManagerRtDb = Firebase.database.getReference(BD_SERIES_MANAGER)

    private val seasonsList: MutableList<Season> = mutableListOf()
    init {
        seriesManagerRtDb.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newSeason: Season? = snapshot.value as? Season
                newSeason?.apply {
                    if (seasonsList.find { it.seasonId == this.seasonId } == null) {
                        seasonsList.add(this)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedSeason: Season? = snapshot.value as? Season
                updatedSeason?.apply {
                    seasonsList[seasonsList.indexOfFirst { it.seasonId.toString() == this.seasonId.toString()}] = this
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val removedSeason: Season? = snapshot.value as? Season
                removedSeason?.apply {
                    seasonsList.remove(this)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // No needed
            }

            override fun onCancelled(error: DatabaseError) {
                // No needed
            }
        })

        seriesManagerRtDb.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                seasonsList.clear()
                snapshot.children.forEach {
                    val season: Season = it.getValue<Season>()?: Season(0, 0, 0,0, Show())
                    seasonsList.add(season)
                }
                seasonsList.sortBy { season -> season.seasonId }
            }

            override fun onCancelled(error: DatabaseError) {
                // No needed
            }
        })
    }

    override fun createSeason(season: Season): Long {
        createOrUpdateSeason(autoGenerateSeasonId(season))
        return 0L
    }

    override fun findOneSeason(seasonId: Long): Season = seasonsList.firstOrNull { it.seasonId.toString() == seasonId.toString() } ?: Season(0,0,0,0, Show())

    override fun findAllSeasonsOfShow(showTitle: String): MutableList<Season> {
        val seasonsOfShowList: MutableList<Season> = mutableListOf()
        seasonsList.forEach {
            if (it.show.title == showTitle) {
                seasonsOfShowList.add(it)
            }
        }
        seasonsOfShowList.sortBy { season -> season.seasonNumber }
        return seasonsOfShowList
    }

    override fun updateSeason(season: Season): Int {
        createOrUpdateSeason(season)
        return 1
    }

    override fun deleteSeason(seasonId: Long): Int {
        seriesManagerRtDb.child(seasonId.toString()).removeValue()
        return 1
    }

    private fun createOrUpdateSeason(season: Season) {
        seriesManagerRtDb.child(season.seasonId.toString()).setValue(season)
    }

    private fun autoGenerateSeasonId(season: Season): Season {
        var i = 0
        while (i < seasonsList.size) {
            if (seasonsList[i].seasonId!!.toInt() != i + 1) {
                season.seasonId = i.toLong() + 1
                break
            }
            else {
                i++
            }
        }
        if (season.seasonId?.toInt() == null) {
            season.seasonId = seasonsList.size.toLong() + 1
        }

        return season
    }
}