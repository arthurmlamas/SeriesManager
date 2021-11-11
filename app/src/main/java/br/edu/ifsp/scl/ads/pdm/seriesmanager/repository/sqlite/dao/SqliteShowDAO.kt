package br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.ShowDAO
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.utils.DatabaseBuilder

class SqliteShowDAO(context: Context): ShowDAO {
    private val db: SQLiteDatabase = DatabaseBuilder(context).getDB()

    override fun createShow(show: Show) = db.insert(DatabaseBuilder.TABLE_SHOW, null, convertShowToContentValues(show))

    override fun findOneShow(title: String): Show {
        val showCursor = db.query(
            true,
            DatabaseBuilder.TABLE_SHOW,
            null,
            "${DatabaseBuilder.SHOW_COLUMN_NAME} = ?",
            arrayOf(title),
            null,
            null,
            null,
            null
        )

        return if (showCursor.moveToFirst()) {
            with(showCursor) {
                Show(
                    getString(getColumnIndexOrThrow(DatabaseBuilder.SHOW_COLUMN_NAME)),
                    getInt(getColumnIndexOrThrow(DatabaseBuilder.SHOW_COLUMN_RELEASED_YEAR)),
                    getString(getColumnIndexOrThrow(DatabaseBuilder.SHOW_COLUMN_BROADCASTER)),
                    getString(getColumnIndexOrThrow(DatabaseBuilder.SHOW_COLUMN_GENRE))
                )
            }
        }
        else { Show() }
    }

    override fun findAllSeries(): MutableList<Show> {
        val seriesList: MutableList<Show> = mutableListOf()

        val showCursor = db.query(
            true,
            DatabaseBuilder.TABLE_SHOW,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )

        while (showCursor.moveToNext()) {
            with(showCursor) {
                seriesList.add(
                    Show(
                    getString(getColumnIndexOrThrow(DatabaseBuilder.SHOW_COLUMN_NAME)),
                    getInt(getColumnIndexOrThrow(DatabaseBuilder.SHOW_COLUMN_RELEASED_YEAR)),
                    getString(getColumnIndexOrThrow(DatabaseBuilder.SHOW_COLUMN_BROADCASTER)),
                    getString(getColumnIndexOrThrow(DatabaseBuilder.SHOW_COLUMN_GENRE))
                )
                )
            }
        }

        return seriesList
    }

    override fun updateShow(show: Show): Int {
        val showCv = convertShowToContentValues(show)
        return db.update(DatabaseBuilder.TABLE_SHOW, showCv,"${DatabaseBuilder.SHOW_COLUMN_NAME} = ?", arrayOf(show.title))
    }

    override fun deleteShow(title: String): Int {
        db.execSQL(DatabaseBuilder.BD_PRAGMA_FK_ON)
        return db.delete(DatabaseBuilder.TABLE_SHOW,"${DatabaseBuilder.SHOW_COLUMN_NAME} = ?", arrayOf(title))
    }

    private fun convertShowToContentValues(show: Show) = ContentValues().also {
        with(it) {
            put(DatabaseBuilder.SHOW_COLUMN_NAME, show.title)
            put(DatabaseBuilder.SHOW_COLUMN_RELEASED_YEAR, show.releasedYear)
            put(DatabaseBuilder.SHOW_COLUMN_BROADCASTER, show.broadcaster)
            put(DatabaseBuilder.SHOW_COLUMN_GENRE, show.genre)
        }
    }
}