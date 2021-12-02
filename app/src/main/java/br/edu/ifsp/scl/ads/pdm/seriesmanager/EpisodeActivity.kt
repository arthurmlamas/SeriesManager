package br.edu.ifsp.scl.ads.pdm.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.ads.pdm.seriesmanager.adapter.EpisodesRvAdapter
import br.edu.ifsp.scl.ads.pdm.seriesmanager.controller.EpisodeController
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.ActivityEpisodeBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.Episode
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import br.edu.ifsp.scl.ads.pdm.seriesmanager.onClickListeners.OnEpisodeClickListener
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.firebase.utils.AuthenticationFirebase
import com.google.android.material.snackbar.Snackbar

class EpisodeActivity : AppCompatActivity(), OnEpisodeClickListener {

    companion object Extras {
        const val EXTRA_EPISODE = "EXTRA_EPISODE"
        const val EXTRA_EPISODE_POSITION = "EXTRA_EPISODE_POSITION"
    }

    private lateinit var season: Season

    private val activityEpisodeBinding: ActivityEpisodeBinding by lazy {
        ActivityEpisodeBinding.inflate(layoutInflater)
    }

    private lateinit var manageEpisodeActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var updateEpisodeActivityResultLauncher: ActivityResultLauncher<Intent>

    private val episodesList: MutableList<Episode> by lazy {
        episodeController.findAllEpisodesOfSeason(season.seasonId!!)
    }
    private val episodeController: EpisodeController by lazy {
        EpisodeController(this)
    }
    private val episodesRvAdapter: EpisodesRvAdapter by lazy {
        EpisodesRvAdapter(this, episodesList)
    }
    private val episodesLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityEpisodeBinding.root)

        intent.getParcelableExtra<Season>(SeasonActivity.EXTRA_SEASON)?.run {
            season = this
            activityEpisodeBinding.showNameTv.text = this.show.title
            "${this.seasonNumber}ª Temporada".also { activityEpisodeBinding.seasonNumberTv.text = it }
        }

        activityEpisodeBinding.episodesRv.adapter = episodesRvAdapter
        activityEpisodeBinding.episodesRv.layoutManager = episodesLayoutManager

        manageEpisodeActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getParcelableExtra<Episode>(EXTRA_EPISODE)?.apply {
                    episodeController.insertEpisode(this)
                    episodesList.add(this)
                    episodesRvAdapter.notifyDataSetChanged()
                }
            }
        }

        updateEpisodeActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val position = result.data?.getIntExtra(EXTRA_EPISODE_POSITION, -1)
                result.data?.getParcelableExtra<Episode>(EXTRA_EPISODE)?.apply {
                    if (position != null && position != -1) {
                        episodeController.updateEpisode(this)
                        episodesList[position] = this
                        episodesRvAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        activityEpisodeBinding.addEpisodeFab.setOnClickListener {
            val addEpisodeIntent = Intent(this, ManageEpisodeActivity::class.java)
            addEpisodeIntent.putExtra(SeasonActivity.EXTRA_SEASON, season)
            manageEpisodeActivityResultLauncher.launch(addEpisodeIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.refreshMi -> {
            episodesRvAdapter.notifyDataSetChanged()
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
        val position = episodesRvAdapter.episodePosition
        val episode = episodesList[position]
        return when (item.itemId) {
            R.id.updateShowMi -> {
                val updateEpisodeIntent = Intent(this, ManageEpisodeActivity::class.java)
                updateEpisodeIntent.putExtra(EXTRA_EPISODE, episode)
                updateEpisodeIntent.putExtra(EXTRA_EPISODE_POSITION, position)
                updateEpisodeActivityResultLauncher.launch(updateEpisodeIntent)
                true
            }
            R.id.removeShowMi -> {
                with(AlertDialog.Builder(this)) {
                    setMessage("Confirma remoção?")
                    setPositiveButton("Sim") { _, _ ->
                        episodeController.deleteEpisode(episode.episodeId!!)
                        episodesList.removeAt(position)
                        episodesRvAdapter.notifyDataSetChanged()
                        Snackbar.make(activityEpisodeBinding.root, "Episódio removido!", Snackbar.LENGTH_SHORT).show()
                    }
                    setNegativeButton("Não") { _, _ ->
                        Snackbar.make(activityEpisodeBinding.root, "Remoção cancelada!", Snackbar.LENGTH_SHORT).show()
                    }
                    create()
                }.show()
                true
            }
            else -> { false }
        }
    }

    override fun onEpisodeClick(position: Int) {
        val episode = episodesList[position]
        val displayEpisodeIntent = Intent(this, ManageEpisodeActivity::class.java)
        displayEpisodeIntent.putExtra(EXTRA_EPISODE, episode)
        manageEpisodeActivityResultLauncher.launch(displayEpisodeIntent)
    }

    override fun onEpisodeCheckBoxClick(position: Int) {
        val episode = episodesList[position]
        episodeController.updateEpisode(episode)
    }

    override fun onStart() {
        super.onStart()
        if (AuthenticationFirebase.firebaseAuth.currentUser == null) {
            finish()
        }
    }
}