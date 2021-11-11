package br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.Episode
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.EpisodeDAO
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.sqlite.utils.DatabaseBuilder

class SqliteEpisodeDAO(context: Context): EpisodeDAO {
    private val db: SQLiteDatabase = DatabaseBuilder(context).getDB()

    override fun createEpisode(episode: Episode): Long {
        TODO("Not yet implemented")
    }

    override fun findOneEpisode(episodeId: Long): Episode {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun deleteEpisode(episodeId: Long): Int {
        TODO("Not yet implemented")
    }

    private fun Int.toBoolean() :Boolean = this == 1

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
            val numOfEpisodes = findAllEpisodesOfSeason(getLong(getColumnIndexOrThrow(DatabaseBuilder.SEASON_COLUMN_ID))).count()
            val show = findOneShow(getString(getColumnIndexOrThrow(DatabaseBuilder.SEASON_COLUMN_SHOW_ID)))
            return if (seasonCursor.moveToFirst()) {
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