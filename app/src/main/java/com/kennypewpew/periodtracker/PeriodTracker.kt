package com.kennypewpew.periodtracker

import android.content.Context
import android.graphics.Typeface
import android.widget.Toast
import com.applandeo.materialcalendarview.CalendarUtils
import com.applandeo.materialcalendarview.EventDay
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PeriodInstances {
    private var completePeriods: MutableList<Pair<Calendar, Calendar>> = mutableListOf()
    private var incompletePeriods: MutableList<Calendar> = mutableListOf()
    private val fmt = SimpleDateFormat("dd/MM/yyyy")
    private var cycleLength = 28
    private var periodLength = 7
    private var activePeriod = false
    private var activePeriodStart = Calendar.getInstance()
    private var initialized = false

    fun init(context: Context?) {
        if ( !initialized ) {
            load(context)
        }
    }

    fun isPeriodActive(): Boolean {
        return activePeriod
    }

    fun startPeriod(c: Calendar) {
        activePeriodStart = c.clone() as Calendar
        activePeriod = true
    }

    fun endPeriod(c: Calendar) {
        addPeriod(activePeriodStart, c)
        activePeriod = false
    }

    fun getPeriodHistory(): List<Pair<Calendar,Calendar>> {
        return completePeriods as List<Pair<Calendar,Calendar>>
    }

    fun getActivePeriodDate(): Calendar {
        return activePeriodStart.clone() as Calendar
    }

    fun getAllPeriodDays(): List<Calendar> {
        var res: MutableList<Calendar> = mutableListOf()
        completePeriods.forEach{
            val s = it.first
            val e = it.second
            var c = s.clone() as Calendar
            while ( c <= e ) {
                res.add(c.clone() as Calendar)
                c.add(Calendar.DATE, 1)
            }
        }
        return res as List<Calendar>
    }

    private fun getHistoryPlusPredictions(): List<Pair<Calendar,Calendar>> {
        if ( completePeriods.isEmpty() ) {
            return mutableListOf()
        }

        val res = completePeriods.toMutableList()

        for ( i in 1..12 ) {
            val s = res.last().first.clone() as Calendar
            s.add(Calendar.DATE, cycleLength)
            val e = s.clone() as Calendar
            e.add(Calendar.DATE, periodLength-1)
            res.add(Pair(s,e))
        }

        return res
    }

    fun getAllPeriodEvents(context: Context?): List<EventDay> {
        val days = getAllPeriodDays()
        var res: MutableList<EventDay> = mutableListOf()

        val historyPlusPredict = getHistoryPlusPredictions()
        if ( historyPlusPredict.isEmpty() )
            return listOf()

        val lastStart = completePeriods.last().second.clone() as Calendar

        for ( i in 1 until historyPlusPredict.size) {
            val s = historyPlusPredict[i-1].first
            val e = historyPlusPredict[i-1].second
            val n = historyPlusPredict[i].first
            val c = s.clone() as Calendar
            var cnt = 1;
            while ( c < n ) {
                var color = android.R.color.holo_red_dark
                if ( c > e ) {
                    color = android.R.color.black
                    if ( c > lastStart ) {
                        c.add(Calendar.DATE,1)
                        cnt += 1
                        continue
                    }
                }
                if ( c > lastStart ) {
                    color = android.R.color.holo_red_light
                }
                res.add(
                    EventDay(
                        c.clone() as Calendar,
                        CalendarUtils.getDrawableText(
                            context,
                            cnt.toString(),
                            Typeface.DEFAULT,
                            color,
                            10
                        )
                    )
                )
                c.add(Calendar.DATE,1)
                cnt += 1
            }
        }

        return res as List<EventDay>
    }

    fun addPeriodFrag(start: Calendar) {
        incompletePeriods.add(start)
    }

    fun delPeriodFrag(start: Calendar) {
        incompletePeriods = incompletePeriods.filterNot { c -> c == start } as MutableList<Calendar>
        incompletePeriods.add(start)
    }

    fun addPeriod(start: Calendar, end: Calendar) {
        if ( start <= end ) completePeriods.add(Pair<Calendar, Calendar>(start,end))
        else                completePeriods.add(Pair<Calendar, Calendar>(end,start))
        completePeriods.sortBy{ it.first }

    }

    fun delPeriod(start: Calendar, end: Calendar) {
        completePeriods = completePeriods.filterNot { c -> c.first == start && c.second == end } as MutableList<Pair<Calendar, Calendar>>
    }

    fun delPeriod(start: Calendar) {
        completePeriods = completePeriods.filterNot { c -> c.first == start } as MutableList<Pair<Calendar, Calendar>>
    }

    private fun saveActivePeriod(context: Context?) {
        val flName = "activefile"
        var output: String
        if ( activePeriod ) {
            output = fmt.format(activePeriodStart.timeInMillis)
        }
        else {
            output = fmt.format(0)
        }
        context?.openFileOutput(flName, Context.MODE_PRIVATE).use {
            it?.write(output.toByteArray())
        }
    }

    private fun readActivePeriod(context: Context?) {
        val flCheck = File(context?.getFilesDir(),"activefile")
        if ( flCheck.exists() ) {
            val rd = context?.openFileInput(
                "activefile"
            )?.bufferedReader()
            val line = rd?.readLine()

            if (null == line) return
            else {
                val raw = fmt.parse(line) as Date
                val date = Calendar.getInstance() as Calendar
                date.setTime(raw)
                if (0.toLong() != date.timeInMillis) {
                    activePeriod = true
                    activePeriodStart = date
                }
            }
        }
    }

    private fun saveInstances(context: Context?) {
        val flName = "datafile"
        val output = dataToText()
        context?.openFileOutput(flName, Context.MODE_PRIVATE).use {
            it?.write(output.toByteArray())
        }
    }

    private fun readInstances(context: Context?) {
        val flCheck = File(context?.getFilesDir(),"datafile")
        if ( flCheck.exists() ) {
            val rd = context?.openFileInput(
                "datafile"
            )?.bufferedReader()

            var line = rd?.readLine()
            var newData: MutableList<Pair<Calendar, Calendar>> = mutableListOf()
            while (null != line) {
                val dates = line.split(",")
                val s = fmt.parse(dates[0])
                val e = fmt.parse(dates[1])
                var sc: Calendar = Calendar.getInstance()
                var ec: Calendar = Calendar.getInstance()
                sc.setTime(s)
                ec.setTime(e)
                newData.add(Pair<Calendar, Calendar>(sc, ec))
                line = rd?.readLine()
            }
            completePeriods = newData.toMutableList()
        }
    }

    fun save(context: Context?) {
        saveInstances(context)
        saveActivePeriod(context)
    }

    fun load(context: Context?) {
        readInstances(context)
        readActivePeriod(context)
    }

    private fun dataToText() : String {
        var res = ""
        completePeriods.forEach{
            res += fmt.format(it.first.timeInMillis)
            res += ","
            res += fmt.format(it.second.timeInMillis)
            res += "\n"
        }
        return res
    }

    fun showInstances(context: Context?) {
        val toast = Toast.makeText(context, dataToText(), Toast.LENGTH_SHORT)
        toast.show()
    }
}
