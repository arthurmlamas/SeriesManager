package br.edu.ifsp.scl.ads.pdm.seriesmanager.controller

import br.edu.ifsp.scl.ads.pdm.seriesmanager.MainActivity
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.ShowDAO
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.dao.SqliteShowDAO

class ShowController(mainActivity: MainActivity) {
    private val showDAO: ShowDAO = SqliteShowDAO(mainActivity)

    fun insertShow(show: Show) = showDAO.createShow(show)
    fun findOneShow(title: String) = showDAO.findOneShow(title)
    fun findAllSeries() = showDAO.findAllSeries()
    fun updateShow(show: Show) = showDAO.updateShow(show)
    fun deleteShow(title: String) = showDAO.deleteShow(title)
}