package br.edu.ifsp.scl.ads.pdm.seriesmanager.activitiy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.ads.pdm.seriesmanager.R
import br.edu.ifsp.scl.ads.pdm.seriesmanager.adapter.SeasonsRvAdapter
import br.edu.ifsp.scl.ads.pdm.seriesmanager.controller.SeasonController
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.ActivitySeasonBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import br.edu.ifsp.scl.ads.pdm.seriesmanager.onClickListeners.OnSeasonClickListener
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.firebase.utils.AuthenticationFirebase
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

    private val seasonsList: MutableList<Season> by lazy {
        seasonController.findAllSeasonsOfShow(show.title)
    }
    private val seasonController: SeasonController by lazy {
        SeasonController(this)
    }
    private val seasonsRvAdapter: SeasonsRvAdapter by lazy {
        SeasonsRvAdapter(this, seasonsList)
    }
    private val seasonsLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activitySeasonBinding.root)

        intent.getParcelableExtra<Show>(MainActivity.EXTRA_SHOW)?.run {
            show = this
            activitySeasonBinding.showNameTv.text = this.title
        }

        activitySeasonBinding.seasonsRv.adapter = seasonsRvAdapter
        activitySeasonBinding.seasonsRv.layoutManager = seasonsLayoutManager

        manageSeasonActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getParcelableExtra<Season>(EXTRA_SEASON)?.apply {
                    seasonController.insertSeason(this)
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
                        seasonController.updateSeason(this)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.refreshMi -> {
            seasonsRvAdapter.notifyDataSetChanged()
            true
        }
        R.id.logoutMi -> {
            AuthenticationFirebase.firebaseAuth.signOut()
            finish()
            true
        }
        else -> { false }
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
                    setMessage("Confirma remo????o?")
                    setPositiveButton("Sim") { _, _ ->
                        seasonController.deleteSeason(season.seasonId!!)
                        seasonsList.removeAt(position)
                        seasonsRvAdapter.notifyDataSetChanged()
                        Snackbar.make(activitySeasonBinding.root, "Temporada removida!", Snackbar.LENGTH_SHORT).show()
                    }
                    setNegativeButton("N??o") { _, _ ->
                        Snackbar.make(activitySeasonBinding.root, "Remo????o cancelada!", Snackbar.LENGTH_SHORT).show()
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
        val displaySeasonIntent = Intent(this, EpisodeActivity::class.java)
        displaySeasonIntent.putExtra(EXTRA_SEASON, season)
        manageSeasonActivityResultLauncher.launch(displaySeasonIntent)
    }

    override fun onStart() {
        super.onStart()
        if (AuthenticationFirebase.firebaseAuth.currentUser == null) {
            finish()
        }
    }
}