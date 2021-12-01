package project.capstone6.acne_diagnosis

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import me.relex.circleindicator.CircleIndicator3
import project.capstone6.acne_diagnosis.data.IntroView
import project.capstone6.acne_diagnosis.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    lateinit var introView: List<IntroView>
    private lateinit var binding: ActivityIntroBinding
    private lateinit var viewPager2: ViewPager2
    private lateinit var btn_start_app: Button
    private lateinit var circleIndicator: CircleIndicator3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_intro)
        binding = ActivityIntroBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        btn_start_app = binding.btnStartApp
        viewPager2 = binding.viewPager2
        circleIndicator = binding.circleIndicator

        addToIntroView()

        viewPager2.adapter = ViewPagerIntroAdapter(introView)
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        circleIndicator.setViewPager(viewPager2)

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (position == 3) {
                    animationButton()
                }
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        })
    }

    private fun animationButton() {
        btn_start_app.visibility = View.VISIBLE

        btn_start_app.animate().apply {
            duration = 1600
            alpha(1f)

            btn_start_app.setOnClickListener {
                btn_start_app.visibility = View.GONE
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }

    private fun addToIntroView() {

        //Create some items that you want to add to your viewpager
        introView = listOf(
            IntroView(getString(R.string.intro1), R.drawable.logo08),
            IntroView(getString(R.string.intro2), R.drawable.ic_compare),
            IntroView(getString(R.string.intro3), R.drawable.ic_camera),
            IntroView(getString(R.string.intro4), R.drawable.ic_app),
        )
    }
}
