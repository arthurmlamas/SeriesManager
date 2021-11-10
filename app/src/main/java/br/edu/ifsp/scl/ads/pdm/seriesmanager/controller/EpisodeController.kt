package br.edu.ifsp.scl.ads.pdm.seriesmanager.controller

import br.edu.ifsp.scl.ads.pdm.seriesmanager.EpisodeActivity
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.Episode
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.EpisodeDAO
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.dao.SqliteEpisodeDAO

class EpisodeController(episodeActivity: EpisodeActivity) {
    private val episodeDAO: EpisodeDAO = SqliteEpisodeDAO(episodeActivity)

    fun insertEpisode(episode: Episode) = episodeDAO.create(episode)
    fun findOneEpisode(episodeId: Long) = episodeDAO.findOne(episodeId)
    fun findAllEpisodes() = episodeDAO.findAll()
    fun updateEpisode(episode: Episode) = episodeDAO.update(episode)
    fun deleteEpisode(episodeId: Long) = episodeDAO.delete(episodeId)
}