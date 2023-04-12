package com.kennypewpew.periodtracker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kennypewpew.periodtracker.databinding.FragmentFirstBinding
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */


class MySeekBar : androidx.appcompat.widget.AppCompatSeekBar {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        val thumb_x = this.width.toDouble() * 0.90;//(this.progress.toDouble() / this.max * this.width.toDouble()).toInt()
        val middle = this.height.toFloat()
        val paint = Paint()
        paint.setColor(Color.BLACK)
        paint.setTextSize(30F)
        c.drawText("" + this.progress, thumb_x.toFloat(), middle, paint)
    }
}

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    fun daysBetween(a: Calendar, b: Calendar): Long {
        val s = a.timeInMillis as Long
        val e = b.timeInMillis as Long
        val d = e - s
        return 1 + TimeUnit.MILLISECONDS.toDays(d)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        periodData.init(context)

        val periods = periodData.getPeriodHistory()

        val scrollArea = view.findViewById(R.id.scrollViewLayout) as LinearLayout
        for ( i in periods.indices) {
            val bar = BarThingy(context as Context)
            //style="?android:attr/progressBarStyleHorizontal"
            //bar.setMin(10)
            var g = 28;
            if ( i > 0 ) {
                g = daysBetween(periods[i-1].first,periods[i].first).toInt()
            }

            bar.setMax(40)
            bar.setGap(g)
            bar.setRange(daysBetween(periods[i].first, periods[i].second).toInt())
            bar.setPadding(8,8,8,8)
            //txt.setText(i.toString())
            scrollArea.addView(bar)
        }

            binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}