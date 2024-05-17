package com.example.thenewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thenewsapp.R
import com.example.thenewsapp.models.Article

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleImage: ImageView = itemView.findViewById(R.id.articleImage)
        val articleSource: TextView = itemView.findViewById(R.id.articleSource)
        val articleTitle: TextView = itemView.findViewById(R.id.articleTitle)
        val articleDescriptor: TextView = itemView.findViewById(R.id.articleDescription)
        val articleDatetime: TextView = itemView.findViewById(R.id.articleDateTime)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.apply {
            val imageUrl = if (!article.urlToImage.isNullOrEmpty()) {
                article.urlToImage
            } else {
                "https://avatars.mds.yandex.net/i?id=eae10f04989cd397301a18c9c3b748cb_l-7757016-images-thumbs&n=13"
            }

            Glide.with(itemView.context)
                .load(imageUrl)
                .into(articleImage)

            articleSource.text = article.source.name
            articleTitle.text = article.title
            articleDescriptor.text = article.description
            articleDatetime.text = article.publishedAt

            itemView.setOnClickListener {
                onItemClickListener?.invoke(article)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    fun submitList(list: List<Article>) {
        differ.submitList(list)
    }
}
