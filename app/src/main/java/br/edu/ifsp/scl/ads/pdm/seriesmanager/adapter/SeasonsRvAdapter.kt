package br.edu.ifsp.scl.ads.pdm.seriesmanager.adapter

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.scl.ads.pdm.seriesmanager.R
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.LayoutSeasonBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.season.Season
import br.edu.ifsp.scl.ads.pdm.seriesmanager.onClickListeners.OnSeasonClickListener

class SeasonsRvAdapter(
    private val onSeasonClickListener: OnSeasonClickListener,
    private val seasonsList: MutableList<Season>
): RecyclerView.Adapter<SeasonsRvAdapter.SeasonLayoutHolder>() {
    var seasonPosition: Int = -1

    inner class SeasonLayoutHolder(layoutSeasonBinding: LayoutSeasonBinding): RecyclerView.ViewHolder(
        layoutSeasonBinding.root), View.OnCreateContextMenuListener {
        val seasonNumberTv: TextView = layoutSeasonBinding.seasonNumberTv
        val seasonReleasedYearTv: TextView = layoutSeasonBinding.seasonReleasedYearTv
        val seasonNumOfEpisodesTv: TextView = layoutSeasonBinding.seasonNumOfEpisodesTv
        init {
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            view: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            MenuInflater(view?.context).inflate(R.menu.context_menu_main, menu)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonLayoutHolder {
        val layoutSeasonBinding = LayoutSeasonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeasonLayoutHolder(layoutSeasonBinding)
    }

    override fun onBindViewHolder(holder: SeasonLayoutHolder, position: Int) {
        val season = seasonsList[position]

        with(holder) {
            seasonNumberTv.text = "${season.seasonNumber}ª Temporada"
            seasonReleasedYearTv.text = season.releasedYear.toString()
            seasonNumOfEpisodesTv.text = "${season.numOfEpisodes} episódios"
            itemView.setOnClickListener {
                onSeasonClickListener.onSeasonClick(position)
            }
            itemView.setOnLongClickListener {
                seasonPosition = position
                false
            }
        }
    }

    override fun getItemCount(): Int = seasonsList.size
}