package com.example.pulse_healthtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Article(
    val title: String,
    val time: String
)

class ArticleAdapter(
    private val articles: MutableList<Article>
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvArticleTitle)
        val tvTime: TextView = view.findViewById(R.id.tvArticleTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(v)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.tvTitle.text = article.title
        holder.tvTime.text = article.time
    }

    override fun getItemCount() = articles.size

    fun addMoreArticles(newArticles: List<Article>) {
        val startPos = articles.size
        articles.addAll(newArticles)
        notifyItemRangeInserted(startPos, newArticles.size)
    }
}