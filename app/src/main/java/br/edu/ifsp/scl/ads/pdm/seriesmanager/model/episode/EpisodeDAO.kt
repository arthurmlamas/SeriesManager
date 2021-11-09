package br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode

interface EpisodeDAO {
    fun create(episode: Episode): Long
    fun findOne(episodeId: Long): Episode
    fun findAll(): MutableList<Episode>
    fun update(episode: Episode): Int
    fun delete(episodeId: Long): Int
}