package br.edu.ifsp.scl.ads.pdm.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.ads.pdm.seriesmanager.adapter.SeasonsRvAdapter
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.ActivitySeasonBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import br.edu.ifsp.scl.ads.pdm.seriesmanager.onClickListeners.OnSeasonClickListener
import com.google.android.material.snackbar.Snackbar

class SeasonActivity : AppCompatActivity(), OnSeasonClickListener {

    companion object Extras {
        const val EXTRA_SEASON = "EXTRA_SEASON"
        const val EXTRA_SEASON_POSITION = "EXTRA_SEASON_POSITION"
    }

    private lateinit var show: Show

    private val activitySeasonBinding: ActivitySeasonBinding by lazy {
        ActivitySeasonBinding.inflate(layoutInflater)
    }

    private lateinit var manageSeasonActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var updateSeasonActivityResultLauncher: ActivityResultLauncher<Intent>

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
            activitySeasonBinding.showNameTv.text = this.title
        }

        initializeSeasonsList()

        println("Nome da série de cada temporada" + seasonsList.iterator().next().show.title)

        manageSeasonActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getParcelableExtra<Season>(EXTRA_SEASON)?.apply {
                    seasonsList.add(this)
                    seasonsRvAdapter.notifyDataSetChanged()
                }
            }
        }

        updateSeasonActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val position = result.data?.getIntExtra(EXTRA_SEASON_POSITION, -1)
                result.data?.getParcelableExtra<Season>(EXTRA_SEASON)?.apply {
                    if (position != null && position != -1) {
                        seasonsList[position] = this
                        seasonsRvAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        activitySeasonBinding.addSeasonFab.setOnClickListener {
            val addSeasonIntent = Intent(this, ManageSeasonActivity::class.java)
            addSeasonIntent.putExtra(MainActivity.EXTRA_SHOW, show)
            manageSeasonActivityResultLauncher.launch(addSeasonIntent)
        }
    }

    private fun initializeSeasonsList() {
        for (index in 1..10) {
            seasonsList.add(
                Season(
                    index.toLong(),
                    index,
                    2000 + index,
                    index,
                    show
                )
            )
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = seasonsRvAdapter.seasonPosition
        val season = seasonsList[position]
        return when (item.itemId) {
            R.id.updateShowMi -> {
                val updateSeasonIntent = Intent(this, ManageSeasonActivity::class.java)
                updateSeasonIntent.putExtra(EXTRA_SEASON, season)
                updateSeasonIntent.putExtra(EXTRA_SEASON_POSITION, position)
                updateSeasonActivityResultLauncher.launch(updateSeasonIntent)
                true
            }
            R.id.removeShowMi -> {
                with(AlertDialog.Builder(this)) {
                    setMessage("Confirma remoção?")
                    setPositiveButton("Sim") { _, _ ->
                        seasonsList.removeAt(position)
                        seasonsRvAdapter.notifyDataSetChanged()
                        Snackbar.make(activitySeasonBinding.root, "Temporada removída!", Snackbar.LENGTH_SHORT).show()
                    }
                    setNegativeButton("Não") { _, _ ->
                        Snackbar.make(activitySeasonBinding.root, "Remoção cancelada!", Snackbar.LENGTH_SHORT).show()
                    }
                    create()
                }.show()
                true
            }
            else -> { false }
        }
    }

    override fun onSeasonClick(position: Int) {
        val season = seasonsList[position]
        Toast.makeText(this, season.seasonNumber.toString(), Toast.LENGTH_SHORT).show()
    }
}