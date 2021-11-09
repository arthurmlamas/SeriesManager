package br.edu.ifsp.scl.ads.pdm.seriesmanager.controller

import br.edu.ifsp.scl.ads.pdm.seriesmanager.SeasonActivity
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.SeasonDAO
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.DAO.SqliteSeasonDAO

class SeasonController(seasonActivity: SeasonActivity) {
    private val seasonDAO: SeasonDAO = SqliteSeasonDAO(seasonActivity)

    fun insertSeason(season: Season) = seasonDAO.create(season)
    fun findOneSeason(seasonId: Long) = seasonDAO.findOne(seasonId)
    fun findAllSeasons() = seasonDAO.findAll()
    fun updateSeason(season: Season) = seasonDAO.update(season)
    fun deleteSeason(seasonId: Long) = seasonDAO.delete(seasonId)
}