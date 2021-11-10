package br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.ShowDAO
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.utils.DatabaseBuilder

class SqliteShowDAO(context: Context): ShowDAO {
    private val seriesManagerDB: SQLiteDatabase = DatabaseBuilder(context).getDB()

    override fun create(show: Show): Long {
        TODO("Not yet implemented")
    }

    override fun findOne(title: String): Show {
        TODO("Not yet implemented")
    }

    override fun findAll(): MutableList<Show> {
        TODO("Not yet implemented")
    }

    override fun update(show: Show): Int {
        TODO("Not yet implemented")
    }

    override fun delete(title: String): Int {
        TODO("Not yet implemented")
    }
}