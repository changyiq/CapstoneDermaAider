package project.capstone6.acne_diagnosis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyRecyclerView(private val adsList: List<HomePageModel>) :
    RecyclerView.Adapter<MyRecyclerView.MyViewHolder>() {


    class MyViewHolder(newView: View) : RecyclerView.ViewHolder(newView) {

        val imageView: ImageView = itemView.findViewById(R.id.img)
        val adsTitle: TextView = itemView.findViewById(R.id.adsTitle)
        val adsView: TextView = itemView.findViewById(R.id.adsView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val infoView = LayoutInflater.from(parent.context).inflate(
            R.layout.activity_ads,
            parent, false
        )
        return MyViewHolder(infoView)
    }

    override fun getItemCount() = adsList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = adsList[position]

        holder.imageView.setImageResource(currentItem.imageResource)
        holder.adsTitle.text = currentItem.adsTitle
        holder.adsView.text = currentItem.adsContent
    }


}
