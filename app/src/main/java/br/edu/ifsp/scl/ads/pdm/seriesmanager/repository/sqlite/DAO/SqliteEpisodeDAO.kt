package br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.DAO

import android.content.Context
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.Episode
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.EpisodeDAO

class SqliteEpisodeDAO(context: Context): EpisodeDAO {
    override fun create(episode: Episode): Long {
        TODO("Not yet implemented")
    }

    override fun findOne(episodeId: Long): Episode {
        TODO("Not yet implemented")
    }

    override fun findAll(): MutableList<Episode> {
        TODO("Not yet implemented")
    }

    override fun update(episode: Episode): Int {
        TODO("Not yet implemented")
    }

    override fun delete(episodeId: Long): Int {
        TODO("Not yet implemented")
    }
}