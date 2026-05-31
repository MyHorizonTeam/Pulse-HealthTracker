package com.example.pulse_healthtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ArticlesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articles)

        findViewById<android.widget.Button>(R.id.btnBack).setOnClickListener {
            //val intent = android.content.Intent(this, ProfilePg::class.java)
            //startActivity(intent)
            finish()
        }

        findViewById<android.widget.Button>(R.id.btnMore).setOnClickListener {
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