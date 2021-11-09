package br.edu.ifsp.scl.ads.pdm.seriesmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.ads.pdm.seriesmanager.adapter.SeasonsRvAdapter
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.ActivitySeasonBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import br.edu.ifsp.scl.ads.pdm.seriesmanager.onClickListeners.OnSeasonClickListener

class SeasonActivity : AppCompatActivity(), OnSeasonClickListener {

    private lateinit var show: Show

    private val activitySeasonBinding: ActivitySeasonBinding by lazy {
        ActivitySeasonBinding.inflate(layoutInflater)
    }

    private val seasonsList: MutableList<Season> = mutableListOf()
    private val seasonsRvAdapter: SeasonsRvAdapter by lazy {
        SeasonsRvAdapter(this, seasonsList)
    }
    private val seasonsLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activitySeasonBinding.root)

        activitySeasonBinding.SeasonsRv.adapter = seasonsRvAdapter
        activitySeasonBinding.SeasonsRv.layoutManager = seasonsLayoutManager

        intent.getParcelableExtra<Show>(MainActivity.EXTRA_SHOW)?.run {
            show = this
            activitySeasonBinding.showNameTv.text = this.title.toString()
        }

        initializeSeasonsList()
    }

    private fun initializeSeasonsList() {
        for (index in 1..10) {
            seasonsList.add(
                Season(
                    index.toLong(),
                    index,
                    "200${index}",
                    index,
                    show
                )
            )
        }
    }

    override fun onSeasonClick(position: Int) {
        val season = seasonsList[position]
        Toast.makeText(this, season.seasonNumber.toString(), Toast.LENGTH_SHORT).show()
    }
}