package br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.firebase.dao

import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.Show
import br.edu.ifsp.scl.ads.pdm.seriesmanager.model.show.ShowDAO
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class FirebaseShowDAO : ShowDAO {
    companion object {
        private const val BD_SERIES_MANAGER = "series_manager"
    }

    private val seriesManagerRtDb = Firebase.database.getReference(BD_SERIES_MANAGER)

    private val seriesList: MutableList<Show> = mutableListOf()
    init {
        seriesManagerRtDb.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newShow: Show? = snapshot.value as? Show
                newShow?.apply {
                    if (seriesList.find { it.title == this.title } == null) {
                        seriesList.add(this)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedShow: Show? = snapshot.value as? Show
                updatedShow?.apply {
                    seriesList[seriesList.indexOfFirst { it.title == this.title}] = this
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val removedShow: Show? = snapshot as? Show
                removedShow?.apply {
                    seriesList.remove(this)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // No needed
            }

            override fun onCancelled(error: DatabaseError) {
                // No needed
            }
        })

        seriesManagerRtDb.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                seriesList.clear()
                snapshot.children.forEach {
                    val show: Show = it.getValue<Show>()?: Show()
                    seriesList.add(show)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // No needed
            }
        })
    }

    override fun createShow(show: Show): Long {
        createOrUpdateShow(show)
        return 0L
    }

    override fun findOneShow(title: String): Show = seriesList.firstOrNull { it.title == title } ?: Show()

    override fun findAllSeries(): MutableList<Show> = seriesList

    override fun updateShow(show: Show): Int {
        createOrUpdateShow(show)
        return 1
    }

    override fun deleteShow(title: String): Int {
        seriesManagerRtDb.child(title).removeValue()
        return 1
    }

    private fun createOrUpdateShow(show: Show) {
        seriesManagerRtDb.child(show.title).setValue(show)
    }
}