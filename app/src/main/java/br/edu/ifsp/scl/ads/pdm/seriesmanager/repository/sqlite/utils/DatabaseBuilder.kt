package br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import br.edu.ifsp.scl.ads.pdm.seriesmanager.R

class DatabaseBuilder(context: Context) {
    companion object {
        private const val BD_SERIES_MANAGER = "series_manager"

        private const val CREATE_TABLE_SERIE_STMT = "CREATE TABLE IF NOT EXISTS serie (" +
                "nome TEXT NOT NULL PRIMARY KEY, " +
                "ano_lancamento INTEGER NOT NULL, " +
                "emissora TEXT NOT NULL, " +
                "genero TEXT NOT NULL, " +
                ");"

        private const val CREATE_TABLE_TEMPORADA_STMT = "CREATE TABLE IF NOT EXISTS temporada (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "numero_sequencial INTEGER NOT NULL, " +
                "ano_lancamento INTEGER NOT NULL, " +
                "serie_id TEXT NOT NULL, " +
                "FOREIGN KEY (serie_id) REFERENCES serie(nome) ON DELETE CASCADE" +
                ");"

        private const val CREATE_TABLE_EPISODIO_STMT = "CREATE TABLE IF NOT EXISTS episodio (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "numero_sequencial INTEGER NOT NULL, " +
                "nome TEXT NOT NULL, " +
                "duracao INTEGER NOT NULL, " +
                "assistido INTEGER NOT NULL DEFAULT 0, " +
                "temporada_id INTEGER NOT NULL, " +
                "FOREIGN KEY (temporada_id) REFERENCES temporada(id) ON DELETE CASCADE" +
                ");"
    }

    private val seriesManagerDB: SQLiteDatabase =
        context.openOrCreateDatabase(BD_SERIES_MANAGER, MODE_PRIVATE, null)

    init {
        try {
            with(seriesManagerDB) {
                execSQL(CREATE_TABLE_SERIE_STMT)
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