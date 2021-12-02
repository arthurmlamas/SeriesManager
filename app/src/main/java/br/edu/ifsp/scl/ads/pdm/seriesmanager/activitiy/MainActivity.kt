package br.edu.ifsp.scl.ads.pdm.seriesmanager.activitiy

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.ads.pdm.seriesmanager.R
import br.edu.ifsp.scl.ads.pdm.seriesmanager.adapter.SeriesRvAdapter
import br.edu.ifsp.scl.ads.pdm.seriesmanager.controller.ShowController
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.ActivityMainBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import br.edu.ifsp.scl.ads.pdm.seriesmanager.onClickListeners.OnShowClickListener
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.firebase.utils.AuthenticationFirebase
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnShowClickListener {

    companion object Extras {
        const val EXTRA_SHOW = "EXTRA_SHOW"
        const val EXTRA_SHOW_POSITION = "EXTRA_SHOW_POSITION"
    }

    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var manageShowActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var updateShowActivityResultLauncher: ActivityResultLauncher<Intent>

    private val seriesList: MutableList<Show> by lazy {
        showController.findAllSeries()
    }
    private val showController: ShowController by lazy {
        ShowController(this)
    }
    private val seriesRvAdapter: SeriesRvAdapter by lazy {
        SeriesRvAdapter(this, seriesList)
    }
    private val seriesLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        activityMainBinding.SeriesRv.adapter = seriesRvAdapter
        activityMainBinding.SeriesRv.layoutManager = seriesLayoutManager

        manageShowActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getParcelableExtra<Show>(EXTRA_SHOW)?.apply {
                    showController.insertShow(this)
                    seriesList.add(this)
                    seriesRvAdapter.notifyDataSetChanged()
                }
            }
        }

        updateShowActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val position = result.data?.getIntExtra(EXTRA_SHOW_POSITION, -1)
                result.data?.getParcelableExtra<Show>(EXTRA_SHOW)?.apply {
                    if (position != null && position != -1) {
                        showController.updateShow(this)
                        seriesList[position] = this
                        seriesRvAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        activityMainBinding.addShowFab.setOnClickListener {
            manageShowActivityResultLauncher.launch(Intent(this, ManageShowActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.refreshMi -> {
            seriesRvAdapter.notifyDataSetChanged()
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
        val position = seriesRvAdapter.showPosition
        val show = seriesList[position]
        return when (item.itemId) {
            R.id.updateShowMi -> {
                val updateShowIntent = Intent(this, ManageShowActivity::class.java)
                updateShowIntent.putExtra(EXTRA_SHOW, show)
                updateShowIntent.putExtra(EXTRA_SHOW_POSITION, position)
                updateShowActivityResultLauncher.launch(updateShowIntent)
                true
            }
            R.id.removeShowMi -> {
                with(AlertDialog.Builder(this)) {
                    setMessage("Confirma remoção?")
                    setPositiveButton("Sim") { _, _ ->
                        showController.deleteShow(show.title)
                        seriesList.removeAt(position)
                        seriesRvAdapter.notifyDataSetChanged()
                        Snackbar.make(activityMainBinding.root, "Série removida!", Snackbar.LENGTH_SHORT).show()
                    }
                    setNegativeButton("Não") { _, _ ->
                        Snackbar.make(activityMainBinding.root, "Remoção cancelada!", Snackbar.LENGTH_SHORT).show()
                    }
                    create()
                }.show()
                true
            }
            else -> { false }
        }
    }

    override fun onShowClick(position: Int) {
        val show = seriesList[position]
        val displayShowIntent = Intent(this, SeasonActivity::class.java)
        displayShowIntent.putExtra(EXTRA_SHOW, show)
        manageShowActivityResultLauncher.launch(displayShowIntent)
    }

    override fun onStart() {
        super.onStart()
        if (AuthenticationFirebase.firebaseAuth.currentUser == null) {
            finish()
        }
    }
}
