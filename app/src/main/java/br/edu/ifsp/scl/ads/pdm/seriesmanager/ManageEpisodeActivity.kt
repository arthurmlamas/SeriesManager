package br.edu.ifsp.scl.ads.pdm.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.ActivityManageEpisodeBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.Episode
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season

class ManageEpisodeActivity : AppCompatActivity() {
    private var episodePosition = -1

    private lateinit var episode: Episode
    private lateinit var season: Season

    private val activityManageEpisodeBinding: ActivityManageEpisodeBinding by lazy {
        ActivityManageEpisodeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityManageEpisodeBinding.root)

        intent.getParcelableExtra<Season>(SeasonActivity.EXTRA_SEASON)?.apply {
            season = this
            activityManageEpisodeBinding.showNameTv.text = this.show.title
            "${this.seasonNumber}ª Temporada".also { activityManageEpisodeBinding.seasonNumberTv.text = it }
        }
        episodePosition = intent.getIntExtra(EpisodeActivity.EXTRA_EPISODE_POSITION, -1)
        intent.getParcelableExtra<Episode>(EpisodeActivity.EXTRA_EPISODE)?.apply {
            with(activityManageEpisodeBinding) {
                showNameTv.text = this@apply.season.show.title
                "${this@apply.season.seasonNumber}ª Temporada".also { seasonNumberTv.text = it }
                episodeNumberEt.setText(this@apply.episodeNumber.toString())
                episodeTitleEt.setText(this@apply.title)
                durationEt.setText(this@apply.duration.toString())
                watchedEpisodeCb.isChecked = this@apply.watchedFlag
                if (episodePosition == -1) {
                    for (i in 0 until (root.childCount - 1)) {
                        root.getChildAt(i).isEnabled = false
                    }
                    saveBt.visibility = View.GONE
                }
            }
            this@ManageEpisodeActivity.season = this.season
        }

        activityManageEpisodeBinding.saveBt.setOnClickListener {
            with(activityManageEpisodeBinding) {
                episode = Episode(
                    null,
                    this.episodeNumberEt.text.toString().toInt(),
                    this.episodeTitleEt.text.toString(),
                    this.durationEt.text.toString().toInt(),
                    this.watchedEpisodeCb.isChecked,
                    season
                )
            }

            val resultIntent = Intent().putExtra(EpisodeActivity.EXTRA_EPISODE, episode)

            if (episodePosition != -1) {
                resultIntent.putExtra(EpisodeActivity.EXTRA_EPISODE_POSITION, episodePosition)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}