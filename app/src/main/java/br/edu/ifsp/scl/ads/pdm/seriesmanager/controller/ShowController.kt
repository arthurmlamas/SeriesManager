package br.edu.ifsp.scl.ads.pdm.seriesmanager.controller

import br.edu.ifsp.scl.ads.pdm.seriesmanager.MainActivity
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.ShowDAO
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.dao.SqliteShowDAO

class ShowController(mainActivity: MainActivity) {
    private val showDAO: ShowDAO = SqliteShowDAO(mainActivity)

    fun insertShow(show: Show) = showDAO.create(show)
    fun findOneShow(title: String) = showDAO.findOne(title)
    fun findAllSeries() = showDAO.findAll()
    fun updateShow(show: Show) = showDAO.update(show)
    fun deleteShow(title: String) = showDAO.delete(title)
}