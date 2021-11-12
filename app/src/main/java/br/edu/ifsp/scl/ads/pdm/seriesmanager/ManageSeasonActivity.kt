package br.edu.ifsp.scl.ads.pdm.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.ActivityManageSeasonBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show

class ManageSeasonActivity : AppCompatActivity() {
    private var seasonPosition = -1

    private lateinit var season: Season
    private lateinit var show: Show
    private var seasonId: Long? = null

    private val activityManageSeasonBinding: ActivityManageSeasonBinding by lazy {
        ActivityManageSeasonBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityManageSeasonBinding.root)

        intent.getParcelableExtra<Show>(MainActivity.EXTRA_SHOW)?.apply {
            show = this
            activityManageSeasonBinding.showNameTv.text = this.title
        }
        activityManageSeasonBinding.seasonNumberOfEpisodesEt.visibility = View.GONE
        seasonPosition = intent.getIntExtra(SeasonActivity.EXTRA_SEASON_POSITION, -1)
        intent.getParcelableExtra<Season>(SeasonActivity.EXTRA_SEASON)?.apply {
            this@ManageSeasonActivity.seasonId = this.seasonId!!
            with(activityManageSeasonBinding) {
                showNameTv.text = this@apply.show.title
                seasonNumberEt.setText(this@apply.seasonNumber.toString())
                seasonReleasedYearEt.setText(this@apply.releasedYear.toString())
                seasonNumberOfEpisodesEt.visibility = View.VISIBLE
                seasonNumberOfEpisodesEt.text = "${this@apply.numOfEpisodes} episódio(s)"
                if (seasonPosition == -1) {
                    for (i in 0 until (root.childCount - 1)) {
                        root.getChildAt(i).isEnabled = false
                    }
                    saveBt.visibility = View.GONE
                }
            }
            this@ManageSeasonActivity.show = this.show
        }

        activityManageSeasonBinding.saveBt.setOnClickListener {
            with(activityManageSeasonBinding) {
                season = Season(
                    seasonId,
                    this.seasonNumberEt.text.toString().toInt(),
                    this.seasonReleasedYearEt.text.toString().toInt(),
                    0,
                    show
                )
            }

            val resultIntent = Intent().putExtra(SeasonActivity.EXTRA_SEASON, season)

            if (seasonPosition != -1) {
                resultIntent.putExtra(SeasonActivity.EXTRA_SEASON_POSITION, seasonPosition)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

    }
}