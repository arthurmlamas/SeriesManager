package br.edu.ifsp.scl.ads.pdm.seriesmanager.adapter

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.scl.ads.pdm.seriesmanager.R
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.LayoutShowBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import br.edu.ifsp.scl.ads.pdm.seriesmanager.onClickListeners.OnShowClickListener

class SeriesRvAdapter(
    private val onShowClickListener: OnShowClickListener,
    private val showsList: MutableList<Show>
): RecyclerView.Adapter<SeriesRvAdapter.ShowLayoutHolder>() {
    var showPosition: Int = -1

    inner class ShowLayoutHolder(layoutShowBinding: LayoutShowBinding): RecyclerView.ViewHolder(layoutShowBinding.root), View.OnCreateContextMenuListener {
        val showNameTv: TextView = layoutShowBinding.showNameTv
        val releasedYearTv: TextView = layoutShowBinding.releasedYearTv
        val broadcasterTv: TextView = layoutShowBinding.broadcasterTv
        val genreTv: TextView = layoutShowBinding.genreTv
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowLayoutHolder {
        val layoutShowBinding = LayoutShowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowLayoutHolder(layoutShowBinding)
    }

    override fun onBindViewHolder(holder: ShowLayoutHolder, position: Int) {
        val show = showsList[position]

        with(holder) {
            showNameTv.text = show.title
            releasedYearTv.text = show.releasedYear.toString()
            broadcasterTv.text = show.broadcaster
            genreTv.text = show.genre
            itemView.setOnClickListener {
                onShowClickListener.onShowClick(position)
            }
            itemView.setOnLongClickListener {
                showPosition = position
                false
            }
        }
    }

    override fun getItemCount(): Int = showsList.size
}