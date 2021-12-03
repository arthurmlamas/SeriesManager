package br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.firebase.dao

import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.SeasonDAO
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
    private var episodeDAO: FirebaseEpisodeDAO = FirebaseEpisodeDAO()
    private var generatedSeasonId: Long = 0

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
                generatedSeasonId = autoGenerateSeasonId()
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
                    val season: Season = it.getValue<Season>()?: Season()
                    seasonsList.add(season)
                }
                generatedSeasonId = autoGenerateSeasonId()
                seasonsList.forEach { season ->
                    var numberOfEpisodesOfSeason = 0
                    episodeDAO.episodesList.forEach { episode ->
                        val comparedSeasonId = episode.season.seasonId!!
                        if (season.seasonId!! == comparedSeasonId)  {
                            numberOfEpisodesOfSeason = episode.season.numOfEpisodes
                        }
                        season.numOfEpisodes = numberOfEpisodesOfSeason
                        updateSeason(season)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // No needed
            }
        })
    }

    override fun createSeason(season: Season): Long {
        season.seasonId = generatedSeasonId
        createOrUpdateSeason(season)
        return 0L
    }

    override fun findOneSeason(seasonId: Long): Season = seasonsList.firstOrNull { it.seasonId.toString() == seasonId.toString() } ?: Season()

    override fun findAllSeasonsOfShow(showTitle: String): MutableList<Season> {
        seriesManagerRtDb.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                seasonsList.clear()
                snapshot.children.forEach {
                    val season: Season = it.getValue<Season>()?: Season()
                    if (season.show.title == showTitle) {
                        seasonsList.add(season)
                    }
                }
                seasonsList.forEach { season ->
                    var numberOfEpisodesOfSeason = 0
                    episodeDAO.episodesList.forEach { episode ->
                        val comparedSeasonId = episode.season.seasonId!!
                        if (season.seasonId!! == comparedSeasonId)  {
                            numberOfEpisodesOfSeason = episode.season.numOfEpisodes
                        }
                        season.numOfEpisodes = numberOfEpisodesOfSeason
                        updateSeason(season)
                    }
                }
                seasonsList.sortBy { season -> season.seasonNumber }
            }

            override fun onCancelled(error: DatabaseError) {
                // No needed
            }
        })

        return seasonsList
    }

    override fun updateSeason(season: Season): Int {
        createOrUpdateSeason(season)
        return 1
    }

    override fun deleteSeason(seasonId: Long): Int {
        deleteSeasonOnCascade(seasonId)
        seriesManagerRtDb.child(seasonId.toString()).removeValue()
        generatedSeasonId = autoGenerateSeasonId()
        return 1
    }

    private fun createOrUpdateSeason(season: Season) {
        seriesManagerRtDb.child(season.seasonId.toString()).setValue(season)
    }

    private fun autoGenerateSeasonId(): Long = (seasonsList.lastOrNull { it.seasonId != null} ?: Season()).seasonId!! + 1

    private fun deleteSeasonOnCascade(seasonId: Long) {
        episodeDAO.deleteAllEpisodesOfSeason(seasonId)
    }

    fun deleteAllSeasonsOfShow(showTitle: String) {
        seasonsList.forEach { season ->
            if (season.show.title == showTitle) {
                deleteSeasonOnCascade(season.seasonId!!)
                seriesManagerRtDb.child(season.seasonId.toString()).removeValue()
            }
        }
    }
}