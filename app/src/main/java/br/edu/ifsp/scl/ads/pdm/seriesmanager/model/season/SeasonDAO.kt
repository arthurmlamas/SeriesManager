package br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season

interface SeasonDAO {
    fun create(season: Season): Long
    fun findOne(seasonId: Long): Season
    fun findAll(): MutableList<Season>
    fun update(season: Season): Int
    fun delete(seasonId: Long): Int
}