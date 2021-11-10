package br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import br.edu.ifsp.scl.ads.pdm.seriesmanager.R

class DatabaseBuilder(context: Context) {
    companion object {
        const val BD_SERIES_MANAGER = "series_manager"

        const val TABLE_SHOW = "serie"
        const val SHOW_COLUMN_NAME = "nome"
        const val SHOW_COLUMN_RELEASED_YEAR = "ano_lancamento"
        const val SHOW_COLUMN_BROADCASTER = "emissora"
        const val SHOW_COLUMN_GENRE = "genre"

        const val CREATE_TABLE_SHOW_STMT = "CREATE TABLE IF NOT EXISTS $TABLE_SHOW (" +
                "$SHOW_COLUMN_NAME TEXT NOT NULL PRIMARY KEY, " +
                "$SHOW_COLUMN_RELEASED_YEAR INTEGER NOT NULL, " +
                "$SHOW_COLUMN_BROADCASTER TEXT NOT NULL, " +
                "$SHOW_COLUMN_GENRE TEXT NOT NULL, " +
                ");"

        const val TABLE_SEASON = "temporada"
        const val SEASON_COLUMN_ID = "id"
        const val SEASON_COLUMN_SEASON_NUMBER = "numero_sequencial"
        const val SEASON_COLUMN_RELEASED_YEAR = "ano_lancamento"
        const val SEASON_COLUMN_SHOW_ID = "serie_id"

        const val CREATE_TABLE_TEMPORADA_STMT = "CREATE TABLE IF NOT EXISTS $TABLE_SEASON (" +
                "$SEASON_COLUMN_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "$SEASON_COLUMN_SEASON_NUMBER INTEGER NOT NULL, " +
                "$SEASON_COLUMN_RELEASED_YEAR INTEGER NOT NULL, " +
                "$SEASON_COLUMN_SHOW_ID TEXT NOT NULL, " +
                "FOREIGN KEY ($SEASON_COLUMN_SHOW_ID) REFERENCES $TABLE_SHOW($SHOW_COLUMN_NAME) ON DELETE CASCADE" +
                ");"

        const val TABLE_EPISODE = "episodio"
        const val EPISODE_COLUMN_ID = "id"
        const val EPISODE_COLUMN_EPISODE_NUMBER = "numero_sequencial"
        const val EPISODE_COLUMN_NAME = "nome"
        const val EPISODE_COLUMN_DURATION = "duracao"
        const val EPISODE_COLUMN_WATCHED = "assitido"
        const val EPISODE_COLUMN_SEASON_ID = "temporada_id"

        const val CREATE_TABLE_EPISODIO_STMT = "CREATE TABLE IF NOT EXISTS $TABLE_EPISODE (" +
                "$EPISODE_COLUMN_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "$EPISODE_COLUMN_EPISODE_NUMBER INTEGER NOT NULL, " +
                "$EPISODE_COLUMN_NAME TEXT NOT NULL, " +
                "$EPISODE_COLUMN_DURATION INTEGER NOT NULL, " +
                "$EPISODE_COLUMN_WATCHED INTEGER NOT NULL DEFAULT 0, " +
                "$EPISODE_COLUMN_SEASON_ID INTEGER NOT NULL, " +
                "FOREIGN KEY ($EPISODE_COLUMN_SEASON_ID) REFERENCES $TABLE_SEASON($EPISODE_COLUMN_ID) ON DELETE CASCADE" +
                ");"
    }

    private val seriesManagerDB: SQLiteDatabase =
        context.openOrCreateDatabase(BD_SERIES_MANAGER, MODE_PRIVATE, null)

    init {
        try {
            with(seriesManagerDB) {
                execSQL(CREATE_TABLE_SHOW_STMT)
                execSQL(CREATE_TABLE_TEMPORADA_STMT)
                execSQL(CREATE_TABLE_EPISODIO_STMT)
            }
        } catch (se: SQLException) {
            Log.e(context.getString(R.string.app_name), se.toString())
        }
    }

    fun getDB(): SQLiteDatabase {
        return seriesManagerDB
    }
}