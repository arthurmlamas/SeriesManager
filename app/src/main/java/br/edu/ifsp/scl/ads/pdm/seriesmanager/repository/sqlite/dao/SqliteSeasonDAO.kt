package br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.SeasonDAO
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.utils.DatabaseBuilder

class SqliteSeasonDAO(context: Context): SeasonDAO {
    private val db: SQLiteDatabase = DatabaseBuilder(context).getDB()
    private val sqliteShowDAO: SqliteShowDAO = SqliteShowDAO(context)

    override fun createSeason(season: Season) = db.insert(DatabaseBuilder.TABLE_SEASON, null, convertSeasonToContentValues(season))

    override fun findOneSeason(seasonId: Long): Season {
        val numOfEpisodes = db.rawQuery("SELECT count(*) FROM ${DatabaseBuilder.TABLE_EPISODE} WHERE ${DatabaseBuilder.EPISODE_COLUMN_SEASON_ID} = ?;", arrayOf(seasonId.toString())).count

        val seasonCursor = db.query(
            true,
            DatabaseBuilder.TABLE_SEASON,
            null,
            "${DatabaseBuilder.SEASON_COLUMN_ID} = ?",
            arrayOf(seasonId.toString()),
            null,
            null,
            null,
            null
        )

        with(seasonCursor) {
            var show = Show()
            return if (seasonCursor.moveToFirst()) {
                show = sqliteShowDAO.findOneShow(getString(getColumnIndexOrThrow(DatabaseBuilder.SEASON_COLUMN_SHOW_ID)))
                Season(
                    getLong(getColumnIndexOrThrow(DatabaseBuilder.SEASON_COLUMN_ID)),
                    getInt(getColumnIndexOrThrow(DatabaseBuilder.SEASON_COLUMN_SEASON_NUMBER)),
                    getInt(getColumnIndexOrThrow(DatabaseBuilder.SEASON_COLUMN_RELEASED_YEAR)),
                    numOfEpisodes,
                    show
                )
            }
            else { Season(0, 0, 0, 0, show)
            }
        }
    }

    override fun findAllSeasonsOfShow(showTitle: String): MutableList<Season> {
        val seasonsList: MutableList<Season> = mutableListOf()

        val seasonCursor = db.query(
            true,
            DatabaseBuilder.TABLE_SEASON,
            null,
            "${DatabaseBuilder.SEASON_COLUMN_SHOW_ID} = ?",
            arrayOf(showTitle),
            null,
            null,
            null,
            null
        )

        with(seasonCursor) {
            while (this.moveToNext()) {
                val seasonId = getLong(getColumnIndexOrThrow(DatabaseBuilder.SEASON_COLUMN_ID))
                val numOfEpisodes = db.rawQuery("SELECT * FROM ${DatabaseBuilder.TABLE_EPISODE} WHERE ${DatabaseBuilder.EPISODE_COLUMN_SEASON_ID} = ?;", arrayOf(seasonId.toString())).count
                val show = sqliteShowDAO.findOneShow(getString(getColumnIndexOrThrow(DatabaseBuilder.SEASON_COLUMN_SHOW_ID)))
                seasonsList.add(
                    Season(
                        seasonId,
                        getInt(getColumnIndexOrThrow(DatabaseBuilder.SEASON_COLUMN_SEASON_NUMBER)),
                        getInt(getColumnIndexOrThrow(DatabaseBuilder.SEASON_COLUMN_RELEASED_YEAR)),
                        numOfEpisodes,
                        show
                    )
                )
            }
        }

        return seasonsList
    }

    override fun updateSeason(season: Season): Int {
        val seasonCv = convertSeasonToContentValues(season)
        return db.update(DatabaseBuilder.TABLE_SEASON, seasonCv, "${DatabaseBuilder.SEASON_COLUMN_ID} = ?", arrayOf(season.seasonId.toString()))
    }

    override fun deleteSeason(seasonId: Long): Int {
        db.execSQL(DatabaseBuilder.BD_PRAGMA_FK_ON)
        return  db.delete(DatabaseBuilder.TABLE_SEASON, "${DatabaseBuilder.SEASON_COLUMN_ID} = ?", arrayOf(seasonId.toString()))
    }

    private fun convertSeasonToContentValues(season: Season) = ContentValues().also {
        with(it) {
            put(DatabaseBuilder.SEASON_COLUMN_SEASON_NUMBER, season.seasonNumber)
            put(DatabaseBuilder.SEASON_COLUMN_RELEASED_YEAR, season.releasedYear)
            put(DatabaseBuilder.SEASON_COLUMN_SHOW_ID, season.show.title)
        }
    }
}