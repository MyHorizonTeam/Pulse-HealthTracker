package com.example.pulse_healthtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ArticlesActivity : AppCompatActivity() {

    private lateinit var adapter: ArticleAdapter

    private val articleList = mutableListOf(
        Article("How to improve your mood daily", "09:00"),
        Article("5 tips for better mental health", "09:00"),
        Article("Why meditation changes your brain", "09:00")
    )

    private val moreArticles = listOf(
        Article("The power of positive thinking", "09:00"),
        Article("How sleep affects your mood", "09:00"),
        Article("Exercise and mental wellness", "09:00")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articles)

        adapter = ArticleAdapter(articleList)

        val rv = findViewById<RecyclerView>(R.id.rvArticles)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        findViewById<android.widget.Button>(R.id.btnBack).setOnClickListener {
            val intent = android.content.Intent(this, home::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<android.widget.Button>(R.id.btnMore).setOnClickListener {
            adapter.addMoreArticles(moreArticles)
            openWebPage("https://www.mentalhealth.org.uk/explore-mental-health/articles")
        }

        findViewById<android.widget.ImageView>(R.id.imageView).setOnClickListener {
            openWebPage("https://www.health.harvard.edu/womens-health/treating-premenstrual-dysphoric-disorder")
        }

        findViewById<android.widget.ImageView>(R.id.imageView2).setOnClickListener {
            openWebPage("https://www.health.harvard.edu/blog/3-ways-to-create-community-and-counter-loneliness-202303082900")
        }
    }

    private fun openWebPage(url: String) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
        startActivity(intent)
    }
}