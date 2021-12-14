/*
 * Name: Xiaohong Deng
 * Student ID: 991517517
 * Assignment: Capstone Project - DermaAider APP
 * Dec 12, 2021
 *
 * Description of ViewPagerIntroAdapter class:
 * This is to create a ViewModel to provide a string information to UI and
 * create ViewPagerIntroAdapter processes implementation for the recyclerview.
 *
 * @author dengxiao
* */

package project.capstone6.acne_diagnosis.Intro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.intro_item_page.view.*
import project.capstone6.acne_diagnosis.R

class ViewPagerIntroAdapter(introViews: List<IntroView>) :
    RecyclerView.Adapter<ViewPagerIntroAdapter.IntroViewHolder>() {

    private val list = introViews

    class IntroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
        return IntroViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.intro_item_page, parent, false)
        )
    }

    override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {

        val currentView = list[position]
        holder.itemView.iv_image_intro.setImageResource(currentView.image)
        holder.itemView.tv_description_intro.text = currentView.description
    }

    override fun getItemCount(): Int {
        return list.size
    }
}