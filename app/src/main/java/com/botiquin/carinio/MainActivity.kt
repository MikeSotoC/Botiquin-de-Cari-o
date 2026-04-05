package com.botiquin.carinio

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.botiquin.carinio.model.Remedy
import com.botiquin.carinio.ui.RemedyAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val breathHandler = Handler(Looper.getMainLooper())
    private var breathRunnable: Runnable? = null
    private var breathTick = 0
    private var breathPhase = 0

    private val affirmations = listOf(
        "Eres suficiente, incluso en tus días difíciles.",
        "No tienes que cargar todo tú solo.",
        "Tu valor no depende de un mal día.",
        "Pedir ayuda también es valentía.",
        "Respira: ya has superado momentos complicados antes.",
        "Mereces descanso, cuidado y amor.",
        "Paso a paso, sigues avanzando.",
        "Tu sensibilidad también es fortaleza."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.remediesRecycler)
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = RemedyAdapter(buildRemedies(), ::showRemedySheet)
    }

    private fun buildRemedies(): List<Remedy> = listOf(
        Remedy("😢", "Te sientes triste", "Para cuando", "Abrir aquí",
            "Está bien no estar bien. Date permiso de sentir y respirar sin prisa.",
            Color.parseColor("#7AB0D8"), false, false, false),
        Remedy("💊", "Fue un mal día", "Para cuando", "1 dosis",
            "Un mal día no define tu vida. Mañana puedes volver a empezar.",
            Color.parseColor("#E8829A"), false, false, false),
        Remedy("🫁", "La ansiedad aprieta", "Para cuando", "Respirar",
            "Usa el ejercicio guiado 4-4-6 para calmar cuerpo y mente.",
            Color.parseColor("#7AC4A8"), true, false, false),
        Remedy("🫙", "Olvidaste lo bonito", "Para cuando", "Guardar",
            "Guarda pequeñas cosas buenas de hoy para releerlas cuando lo necesites.",
            Color.parseColor("#F0A07A"), false, false, true),
        Remedy("🤍", "Crees que no vales", "Para cuando", "Leer",
            "Tu valor no cambia por cómo te sientes hoy. Sigues siendo una persona valiosa.",
            Color.parseColor("#A890D8"), false, false, false),
        Remedy("✨", "Necesitas afirmación", "Para cuando", "Tocar",
            "Aquí tienes recordatorios amables para acompañarte.",
            Color.parseColor("#8C74D7"), false, true, false),
        Remedy("🌿", "Tu mente se va lejos", "Para cuando", "5-4-3-2-1",
            "Mira 5 cosas, toca 4, escucha 3, huele 2 y saborea 1 para volver al presente.",
            Color.parseColor("#56B88B"), false, false, false),
        Remedy("🙃", "Finges que estás bien", "Para cuando", "Solo tú",
            "No tienes que fingir. También mereces un lugar seguro para sentir.",
            Color.parseColor("#F0A07A"), false, false, false),
        Remedy("💌", "Me necesitas a mí", "Para cuando", "Mensaje",
            "No estás solo. Hay personas que te quieren y se preocupan por ti.",
            Color.parseColor("#E8829A"), false, false, false),
        Remedy("⭐", "No puedes dormir", "Para cuando", "Noche",
            "Si no llega el sueño, baja el ritmo: respiración suave, poca luz y calma.",
            Color.parseColor("#7AB0D8"), false, false, false)
    )

    private fun showRemedySheet(remedy: Remedy) {
        stopBreathing()

        val dialog = BottomSheetDialog(this)
        val sheet = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_remedy, null, false)

        sheet.findViewById<TextView>(R.id.sheetIcon).text = remedy.icon
        sheet.findViewById<TextView>(R.id.sheetSubtitle).apply {
            text = remedy.subtitle
            setTextColor(remedy.color)
        }
        sheet.findViewById<TextView>(R.id.sheetTitle).text = remedy.title
        sheet.findViewById<TextView>(R.id.sheetDetail).text = remedy.detail

        val breathSection = sheet.findViewById<View>(R.id.breathSection)
        val affirmationSection = sheet.findViewById<View>(R.id.affirmationSection)
        val gratitudeSection = sheet.findViewById<View>(R.id.gratitudeSection)

        breathSection?.visibility = if (remedy.hasBreathTool) View.VISIBLE else View.GONE
        affirmationSection?.visibility = if (remedy.hasAffirmations) View.VISIBLE else View.GONE
        gratitudeSection?.visibility = if (remedy.hasGratitudeJar) View.VISIBLE else View.GONE

        if (remedy.hasBreathTool) configureBreathing(sheet)
        if (remedy.hasAffirmations) configureAffirmations(sheet)
        if (remedy.hasGratitudeJar) configureGratitude(sheet)

        dialog.setOnDismissListener { stopBreathing() }
        dialog.setContentView(sheet)
        dialog.show()
    }

    private fun configureBreathing(sheet: View) {
        val phaseText = sheet.findViewById<TextView>(R.id.breathPhase) ?: return
        val progress = sheet.findViewById<ProgressBar>(R.id.breathProgress) ?: return
        val start = sheet.findViewById<Button>(R.id.startBreathButton) ?: return
        val stop = sheet.findViewById<Button>(R.id.stopBreathButton) ?: return

        val phases = arrayOf("Inhala", "Sostén", "Exhala")
        val duration = intArrayOf(4, 4, 6)
        val cycleTicks = 140

        start.setOnClickListener {
            stopBreathing()
            breathTick = 0
            breathPhase = 0

            breathRunnable = object : Runnable {
                override fun run() {
                    val totalTicksPhase = duration[breathPhase] * 10
                    val inPhaseTick = breathTick % totalTicksPhase
                    val percent = ((inPhaseTick / totalTicksPhase.toFloat()) * 100).toInt()

                    phaseText.text = phases[breathPhase]
                    progress.progress = percent

                    breathTick++
                    if (breathTick % totalTicksPhase == 0) {
                        breathPhase = (breathPhase + 1) % phases.size
                    }

                    if (breathTick < cycleTicks) {
                        breathHandler.postDelayed(this, 100)
                    } else {
                        phaseText.text = "Ciclo completo. Muy bien."
                        progress.progress = 100
                    }
                }
            }
            breathRunnable?.let { breathHandler.post(it) }
        }

        stop.setOnClickListener { stopBreathing() }
    }

    private fun configureAffirmations(sheet: View) {
        val nextAffirmation = sheet.findViewById<Button>(R.id.nextAffirmationButton) ?: return
        val affirmationText = sheet.findViewById<TextView>(R.id.affirmationText) ?: return

        nextAffirmation.setOnClickListener {
            affirmationText.text = affirmations[Random.nextInt(affirmations.size)]
        }
    }

    private fun configureGratitude(sheet: View) {
        val input = sheet.findViewById<EditText>(R.id.gratitudeInput) ?: return
        val add = sheet.findViewById<Button>(R.id.addGratitudeButton) ?: return
        val chips = sheet.findViewById<ChipGroup>(R.id.gratitudeChips) ?: return

        add.setOnClickListener {
            val text = input.text?.toString()?.trim().orEmpty()
            if (TextUtils.isEmpty(text)) return@setOnClickListener

            val chip = Chip(this).apply {
                this.text = "✦ $text"
                isCloseIconVisible = true
                setOnCloseIconClickListener { chips.removeView(this) }
            }

            chips.addView(chip)
            input.setText("")
        }
    }

    private fun stopBreathing() {
        breathRunnable?.let { breathHandler.removeCallbacks(it) }
        breathRunnable = null
    }

    override fun onDestroy() {
        stopBreathing()
        super.onDestroy()
    }
}
