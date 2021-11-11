package br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season

interface SeasonDAO {
    fun createSeason(season: Season): Long
    fun findOneSeason(seasonId: Long): Season
    fun findAllSeasonsOfShow(showTitle: String): MutableList<Season>
    fun updateSeason(season: Season): Int
    fun deleteSeason(seasonId: Long): Int
}