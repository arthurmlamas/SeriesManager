package br.edu.ifsp.scl.ads.pdm.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.ActivityShowBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show

class ShowActivity : AppCompatActivity() {
    private var showPosition = -1
    private lateinit var show: Show

    private val activityShowBinding: ActivityShowBinding by lazy {
        ActivityShowBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityShowBinding.root)

        val genresList: MutableList<String> = mutableListOf()
        for (i in 0 until activityShowBinding.genreSp.count) {
            genresList.add(activityShowBinding.genreSp.getItemAtPosition(i).toString())
        }
        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, genresList)
        activityShowBinding.genreSp.adapter = spinnerAdapter

        showPosition = intent.getIntExtra(MainActivity.EXTRA_SHOW_POSITION, -1)
        intent.getParcelableExtra<Show>(MainActivity.EXTRA_SHOW)?.apply {
            with(activityShowBinding) {
                showNameEt.isEnabled = false
                showNameEt.setText(this@apply.title)
                releasedYearEt.setText(this@apply.releasedYear)
                broadcasterEt.setText(this@apply.broadcaster)
                genreSp.setSelection(spinnerAdapter.getPosition(this@apply.genre))
                if (showPosition == -1) {
                    for (i in 0 until (root.childCount - 1)) {
                        root.getChildAt(i).isEnabled = false
                    }
                    saveBt.visibility = View.GONE
                }
            }
        }

        activityShowBinding.saveBt.setOnClickListener {
            with(activityShowBinding) {
                show = Show(
                    this.showNameEt.text.toString(),
                    this.releasedYearEt.text.toString(),
                    this.broadcasterEt.text.toString(),
                    (this.genreSp.selectedView as TextView).text.toString()
                )
            }

            val resultIntent = Intent().putExtra(MainActivity.EXTRA_SHOW, show)

            if (showPosition != -1) {
                resultIntent.putExtra(MainActivity.EXTRA_SHOW_POSITION, showPosition)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

    }
}