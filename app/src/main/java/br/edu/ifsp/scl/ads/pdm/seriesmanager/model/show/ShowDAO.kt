package br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show

interface ShowDAO {
    fun createShow(show: Show): Long
    fun findOneShow(title: String): Show
    fun findAllSeries(): MutableList<Show>
    fun updateShow(show: Show): Int
    fun deleteShow(title: String): Int
}