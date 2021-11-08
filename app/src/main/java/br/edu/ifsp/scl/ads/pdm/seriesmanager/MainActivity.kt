package br.edu.ifsp.scl.ads.pdm.seriesmanager

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.ads.pdm.seriesmanager.adapter.SeriesRvAdapter
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.ActivityMainBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.Show
import br.edu.ifsp.scl.ads.pdm.seriesmanager.onClickListeners.OnShowClickListener

class MainActivity : AppCompatActivity(), OnShowClickListener {

    companion object Extras {
        const val EXTRA_SHOW = "EXTRA_SHOW"
        const val EXTRA_POSITION = "EXTRA_POSITION"
    }

    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var showActivityResultLauncher: ActivityResultLauncher<Intent>

    private val seriesList: MutableList<Show> = mutableListOf()
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

        initializeSeriesList()

        showActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getParcelableExtra<Show>(EXTRA_SHOW)?.apply {
                    seriesList.add(this)
                    seriesRvAdapter.notifyDataSetChanged()
                }
            }
        }

    }

    private fun initializeSeriesList() {
        for (index in 1..10) {
            seriesList.add(
                Show(
                    "Série $index",
                    "200${index}",
                    "Emissora $index",
                    "Gênero $index"
                )
            )
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return true
    }

    override fun onShowClick(position: Int) {
        val show = seriesList[position]
        val displayShowIntent = Intent(this, SeasonActivity::class.java)
        displayShowIntent.putExtra(EXTRA_SHOW, show)
        showActivityResultLauncher.launch(displayShowIntent)
    }
}
