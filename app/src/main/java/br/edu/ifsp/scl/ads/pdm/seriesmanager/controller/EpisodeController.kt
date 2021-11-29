package br.edu.ifsp.scl.ads.pdm.seriesmanager.controller

import android.content.Context
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.Episode
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.EpisodeDAO
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.dao.SqliteEpisodeDAO

class EpisodeController(context: Context) {
    private val episodeDAO: EpisodeDAO = SqliteEpisodeDAO(context)

    fun insertEpisode(episode: Episode) = episodeDAO.createEpisode(episode)
    fun findOneEpisode(episodeId: Long) = episodeDAO.findOneEpisode(episodeId)
    fun findAllEpisodesOfSeason(seasonId: Long) = episodeDAO.findAllEpisodesOfSeason(seasonId)
    fun updateEpisode(episode: Episode) = episodeDAO.updateEpisode(episode)
    fun deleteEpisode(episodeId: Long) = episodeDAO.deleteEpisode(episodeId)
}