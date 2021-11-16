package br.edu.ifsp.scl.ads.pdm.seriesmanager

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.ActivityManageShowBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show

class ManageShowActivity : AppCompatActivity() {
    private var showPosition = -1
    private lateinit var show: Show

    private val activityManageShowBinding: ActivityManageShowBinding by lazy {
        ActivityManageShowBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityManageShowBinding.root)

        val genresList: MutableList<String> = mutableListOf()
        for (i in 0 until activityManageShowBinding.genreSp.count) {
            genresList.add(activityManageShowBinding.genreSp.getItemAtPosition(i).toString())
        }
        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(this, R.layout.simple_spinner_item, genresList)
        activityManageShowBinding.genreSp.adapter = spinnerAdapter

        showPosition = intent.getIntExtra(MainActivity.EXTRA_SHOW_POSITION, -1)
        intent.getParcelableExtra<Show>(MainActivity.EXTRA_SHOW)?.apply {
            with(activityManageShowBinding) {
                showNameEt.isEnabled = false
                showNameEt.setText(this@apply.title)
                releasedYearEt.setText(this@apply.releasedYear.toString())
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

        activityManageShowBinding.saveBt.setOnClickListener {
            with(activityManageShowBinding) {
                show = Show(
                    this.showNameEt.text.toString(),
                    this.releasedYearEt.text.toString().toInt(),
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