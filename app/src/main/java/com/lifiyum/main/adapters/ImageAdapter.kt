package com.lifiyum.main.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.lifiyum.main.R

class ImageAdapter (private val mDataset: ArrayList<Uri?>, private val mContext: Context) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_user_: ImageView = itemView.findViewById<View>(R.id.img_user_) as ImageView
        var img_photoPicker: ImageView = itemView.findViewById<View>(R.id.img_photoPicker) as ImageView
    }
    override fun getItemCount(): Int {
        return mDataset.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      //  holder.mTextView.text = mDataset[position]
        holder.img_user_.setImageURI(mDataset.get(position))
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.item_images, parent, false
        )
        return ViewHolder(view)
    }
}