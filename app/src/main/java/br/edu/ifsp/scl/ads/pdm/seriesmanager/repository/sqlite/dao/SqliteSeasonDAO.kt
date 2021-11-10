package br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.dao

import android.content.Context
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.SeasonDAO

class SqliteSeasonDAO(context: Context): SeasonDAO {
    override fun create(season: Season): Long {
        TODO("Not yet implemented")
    }

    override fun findOne(seasonId: Long): Season {
        TODO("Not yet implemented")
    }

    override fun findAll(): MutableList<Season> {
        TODO("Not yet implemented")
    }

    override fun update(season: Season): Int {
        TODO("Not yet implemented")
    }

    override fun delete(seasonId: Long): Int {
        TODO("Not yet implemented")
    }
}