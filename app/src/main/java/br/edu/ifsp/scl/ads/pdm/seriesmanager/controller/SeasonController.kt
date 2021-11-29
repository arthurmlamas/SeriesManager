package br.edu.ifsp.scl.ads.pdm.seriesmanager.controller

import android.content.Context
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.SeasonDAO
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.dao.SqliteSeasonDAO

class SeasonController(context: Context) {
    private val seasonDAO: SeasonDAO = SqliteSeasonDAO(context)

    fun insertSeason(season: Season) = seasonDAO.createSeason(season)
    fun findOneSeason(seasonId: Long) = seasonDAO.findOneSeason(seasonId)
    fun findAllSeasonsOfShow(showTitle: String) = seasonDAO.findAllSeasonsOfShow(showTitle)
    fun updateSeason(season: Season) = seasonDAO.updateSeason(season)
    fun deleteSeason(seasonId: Long) = seasonDAO.deleteSeason(seasonId)
}