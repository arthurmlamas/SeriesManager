package br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode

interface EpisodeDAO {
    fun createEpisode(episode: Episode): Long
    fun findOneEpisode(episodeId: Long): Episode
    fun findAllEpisodesOfSeason(seasonId: Long): MutableList<Episode>
    fun updateEpisode(episode: Episode): Int
    fun deleteEpisode(episodeId: Long): Int
}