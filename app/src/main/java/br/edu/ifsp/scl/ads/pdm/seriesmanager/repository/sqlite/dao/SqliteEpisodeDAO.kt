package br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.Episode
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.EpisodeDAO
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.utils.DatabaseBuilder

class SqliteEpisodeDAO(context: Context): EpisodeDAO {
    private val db: SQLiteDatabase = DatabaseBuilder(context).getDB()

    override fun createEpisode(episode: Episode): Long = db.insert(DatabaseBuilder.TABLE_EPISODE, null, convertEpisodeToContentValues(episode))

    override fun findOneEpisode(episodeId: Long): Episode {
        val episodeCursor = db.query(
            true,
            DatabaseBuilder.TABLE_EPISODE,
            null,
            "${DatabaseBuilder.EPISODE_COLUMN_SEASON_ID} = ?",
            arrayOf(episodeId.toString()),
            null,
            null,
            null,
            null,
        )

        with(episodeCursor) {
            var season = Season(0, 0,0, 0 , Show())
            return if (episodeCursor.moveToFirst()) {
                season = findOneSeason(getLong(getColumnIndexOrThrow(DatabaseBuilder.EPISODE_COLUMN_ID)))
                Episode(
                    getLong(getColumnIndexOrThrow(DatabaseBuilder.EPISODE_COLUMN_ID)),
                    getInt(getColumnIndexOrThrow(DatabaseBuilder.EPISODE_COLUMN_EPISODE_NUMBER)),
                    getString(getColumnIndexOrThrow(DatabaseBuilder.EPISODE_COLUMN_NAME)),
                    getInt(getColumnIndexOrThrow(DatabaseBuilder.EPISODE_COLUMN_DURATION)),
                    getInt(getColumnIndexOrThrow(DatabaseBuilder.EPISODE_COLUMN_WATCHED)).toBoolean(),
                    season
                    )
            }
            else { Episode(0, 0, "", 0, false, season)
            }
        }
    }

    override fun findAllEpisodesOfSeason(seasonId: Long): MutableList<Episode> {
        val episodesList: MutableList<Episode> = mutableListOf()

        val episodeCursor = db.query(
            true,
            DatabaseBuilder.TABLE_EPISODE,
            null,
            "${DatabaseBuilder.EPISODE_COLUMN_SEASON_ID} = ?",
            arrayOf(seasonId.toString()),
            null,
            null,
            null,
            null
        )

        while (episodeCursor.moveToNext()) {
            with(episodeCursor) {
                episodesList.add(
                    Episode(
                        getLong(getColumnIndexOrThrow(DatabaseBuilder.EPISODE_COLUMN_ID)),
                        getInt(getColumnIndexOrThrow(DatabaseBuilder.EPISODE_COLUMN_EPISODE_NUMBER)),
                        getString(getColumnIndexOrThrow(DatabaseBuilder.EPISODE_COLUMN_NAME)),
                        getInt(getColumnIndexOrThrow(DatabaseBuilder.EPISODE_COLUMN_DURATION)),
                        getInt(getColumnIndexOrThrow(DatabaseBuilder.EPISODE_COLUMN_WATCHED)).toBoolean(),
                        findOneSeason(seasonId)
                    )
                )
            }
        }

        return episodesList
    }

    override fun updateEpisode(episode: Episode): Int {
        val episodeCv = convertEpisodeToContentValues(episode)
        return db.update(DatabaseBuilder.TABLE_EPISODE, episodeCv, "${DatabaseBuilder.EPISODE_COLUMN_ID} = ?", arrayOf(episode.episodeId.toString()))
    }

    override fun deleteEpisode(episodeId: Long): Int {
        return db.delete(DatabaseBuilder.TABLE_EPISODE, "${DatabaseBuilder.EPISODE_COLUMN_ID} = ?", arrayOf(episodeId.toString()))
    }

    private fun convertEpisodeToContentValues(episode: Episode) = ContentValues().also {
        with(it) {
            put(DatabaseBuilder.EPISODE_COLUMN_EPISODE_NUMBER, episode.episodeNumber)
            put(DatabaseBuilder.EPISODE_COLUMN_NAME, episode.title)
            put(DatabaseBuilder.EPISODE_COLUMN_DURATION, episode.duration)
            put(DatabaseBuilder.EPISODE_COLUMN_WATCHED, episode.watchedFlag.toInt())
            put(DatabaseBuilder.EPISODE_COLUMN_SEASON_ID, episode.season.seasonId)
        }
    }

    private fun Boolean.toInt(): Int {
        return if (this) { 1 }
        else { 0 }
    }

    private fun Int.toBoolean(): Boolean = this == 1

    private fun findOneSeason(seasonId: Long): Season {
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
            val numOfEpisodes = db.rawQuery("SELECT count(*) FROM ${DatabaseBuilder.TABLE_EPISODE} WHERE ${DatabaseBuilder.EPISODE_COLUMN_SEASON_ID} = ?;", arrayOf(seasonId.toString())).count
            return if (seasonCursor.moveToFirst()) {
                val show = findOneShow(getString(getColumnIndexOrThrow(DatabaseBuilder.SEASON_COLUMN_SHOW_ID)))
                Season(
                    getLong(getColumnIndexOrThrow(DatabaseBuilder.SEASON_COLUMN_ID)),
                    getInt(getColumnIndexOrThrow(DatabaseBuilder.SEASON_COLUMN_SEASON_NUMBER)),
                    getInt(getColumnIndexOrThrow(DatabaseBuilder.SEASON_COLUMN_RELEASED_YEAR)),
                    numOfEpisodes,
                    show
                )
            }
            else { Season(0, 0, 0, 0, Show())
            }
        }
    }

    private fun findOneShow(title: String): Show {
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
}