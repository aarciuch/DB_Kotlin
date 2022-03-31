package art.tm.db_kotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ArtAdapter(var lista: List<Person>) : RecyclerView.Adapter<ArtAdapter.ArtViewHolder> () {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtAdapter.ArtViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item,
            parent,
            false
        )
        return ArtViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArtAdapter.ArtViewHolder, position: Int) {
        val item = lista[position]
        holder.name.text = item.name
        holder.age.text = item.age.toString()
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    class ArtViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.nameItem)
        var age: TextView = view.findViewById(R.id.ageItem)
    }

}
