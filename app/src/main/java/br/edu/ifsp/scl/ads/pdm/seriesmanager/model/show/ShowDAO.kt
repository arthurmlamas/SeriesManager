package br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show

interface ShowDAO {
    fun create(show: Show): Long
    fun findOne(title: String): Show
    fun findAll(): MutableList<Show>
    fun update(show: Show): Int
    fun delete(title: String): Int
}