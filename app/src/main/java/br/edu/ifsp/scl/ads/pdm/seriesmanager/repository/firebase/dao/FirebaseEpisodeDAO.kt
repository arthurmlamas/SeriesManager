package br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.firebase.dao

import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.Episode
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.EpisodeDAO
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class FirebaseEpisodeDAO : EpisodeDAO {
    companion object {
        private const val BD_SERIES_MANAGER = "episodes"
    }

    private val seriesManagerRtDb = Firebase.database.getReference(BD_SERIES_MANAGER)
    private var generatedEpisodeId: Long = 0

    val episodesList: MutableList<Episode> = mutableListOf()
    init {
        seriesManagerRtDb.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newEpisode: Episode? = snapshot.value as? Episode
                newEpisode?.apply {
                    if (episodesList.find { it.episodeId == this.episodeId } == null) {
                        episodesList.add(this)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedEpisode: Episode? = snapshot.value as? Episode
                updatedEpisode?.apply {
                    episodesList[episodesList.indexOfFirst { it.episodeId.toString() == this.episodeId.toString()}] = this
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val removedEpisode: Episode? = snapshot.value as? Episode
                removedEpisode?.apply {
                    episodesList.remove(this)
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
                episodesList.clear()
                snapshot.children.forEach {
                    val episode: Episode = it.getValue<Episode>()?: Episode()
                    episodesList.add(episode)

                }
                generatedEpisodeId = autoGenerateEpisodeId()

            }

            override fun onCancelled(error: DatabaseError) {
                // No needed
            }
        })
    }

    override fun createEpisode(episode: Episode): Long {
        episode.episodeId = generatedEpisodeId
        var numberOfEpisodesOfSeason = 0
        episodesList.forEach { episodeOfList ->
            if (episodeOfList.season.seasonId!! == episode.season.seasonId!!) {
                numberOfEpisodesOfSeason += 1
            }
        }

        episodesList.forEach { episodeOfList ->
            if (episodeOfList.season.seasonId!! == episode.season.seasonId!!) {
                episodeOfList.season.numOfEpisodes = numberOfEpisodesOfSeason + 1
                updateEpisode(episodeOfList)
            }
        }
        episode.season.numOfEpisodes = numberOfEpisodesOfSeason + 1
        createOrUpdateEpisode(episode)
        return 0L
    }

    override fun findOneEpisode(episodeId: Long): Episode = episodesList.firstOrNull { it.episodeId.toString() == episodeId.toString() } ?: Episode()

    override fun findAllEpisodesOfSeason(seasonId: Long): MutableList<Episode> {
        seriesManagerRtDb.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                episodesList.clear()
                snapshot.children.forEach {
                    val episode: Episode = it.getValue<Episode>()?: Episode()
                    if (episode.season.seasonId == seasonId) {
                        episodesList.add(episode)
                    }
                }
                val numberOfEpisodesOfSeason = episodesList.count()
                episodesList.forEach { episode ->
                    episode.season.numOfEpisodes = numberOfEpisodesOfSeason
                    updateEpisode(episode)
                }
                episodesList.sortBy { episode -> episode.episodeNumber }
            }

            override fun onCancelled(error: DatabaseError) {
                // No needed
            }
        })

        return episodesList
    }

    override fun updateEpisode(episode: Episode): Int {
        createOrUpdateEpisode(episode)
        return 1
    }

    override fun deleteEpisode(episodeId: Long): Int {
        var numberOfEpisodesOfSeason = 0
        val seasonId = findOneEpisode(episodeId).season.seasonId
        episodesList.forEach { episodeOfList ->
            if (episodeOfList.season.seasonId!! == seasonId) {
                numberOfEpisodesOfSeason += 1
            }
        }
        episodesList.forEach { episode ->
            if (episode.season.seasonId!! == seasonId) {
                episode.season.numOfEpisodes = numberOfEpisodesOfSeason - 1
                updateEpisode(episode)
            }
        }

        seriesManagerRtDb.child(episodeId.toString()).removeValue()
        return 1
    }

    private fun createOrUpdateEpisode(episode: Episode) {
        seriesManagerRtDb.child(episode.episodeId.toString()).setValue(episode)
    }

    private fun autoGenerateEpisodeId(): Long {
        var i = 0
        while (i < episodesList.size) {
            if (episodesList[i].episodeId!!.toInt() != i + 1) {
                return i.toLong() + 1
            }
            else {
                i++
            }
        }
        return episodesList.size.toLong() + 1
    }

    fun deleteAllEpisodesOfSeason(seasonId: Long) {
        episodesList.forEach { episode ->
            if (episode.season.seasonId == seasonId) {
                seriesManagerRtDb.child(episode.episodeId.toString()).removeValue()
            }
        }
    }
}