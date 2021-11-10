package br.edu.ifsp.scl.ads.pdm.seriesmanager.adapter

import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.scl.ads.pdm.seriesmanager.R
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.LayoutEpisodeBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.episode.Episode
import br.edu.ifsp.scl.ads.pdm.seriesmanager.onClickListeners.OnEpisodeClickListener

class EpisodesRvAdapter(
    private val onEpisodeClickListener: OnEpisodeClickListener,
    private val episodesList: MutableList<Episode>
): RecyclerView.Adapter<EpisodesRvAdapter.EpisodeLayoutHolder>() {
    var episodePosition: Int = -1

    inner class EpisodeLayoutHolder(layoutEpisodeBinding: LayoutEpisodeBinding): RecyclerView.ViewHolder(
        layoutEpisodeBinding.root), View.OnCreateContextMenuListener {
        val episodeNumberTv: TextView = layoutEpisodeBinding.episodeNumberTv
        val episodeTitlteTv: TextView = layoutEpisodeBinding.episodeTitlteTv
        val durationTv: TextView = layoutEpisodeBinding.durationTv
        val watchedEpisodeCb: CheckBox = layoutEpisodeBinding.watchedEpisodeCb
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeLayoutHolder {
        val layoutEpisodeBinding = LayoutEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EpisodeLayoutHolder(layoutEpisodeBinding)
    }

    override fun onBindViewHolder(holder: EpisodeLayoutHolder, position: Int) {
        val episode = episodesList[position]

        with(holder) {
            "Episódio ${episode.episodeNumber}".also { episodeNumberTv.text = it }
            "${episode.duration} min".also { durationTv.text = it }
            episodeTitlteTv.text = episode.title
            watchedEpisodeCb.isChecked = episode.watchedFlag
            watchedEpisodeCb.setOnClickListener { it as CheckBox
                episode.watchedFlag = it.isChecked
            }
            itemView.setOnClickListener {
                onEpisodeClickListener.onEpisodeClick(position)
            }
            itemView.setOnLongClickListener {
                episodePosition = position
                false
            }
        }
    }

    override fun getItemCount(): Int = episodesList.size
}