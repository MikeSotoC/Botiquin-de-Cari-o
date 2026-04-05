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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.botiquin.carinio.model.Remedy
import com.botiquin.carinio.ui.RemedyAdapter
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val breathHandler = Handler(Looper.getMainLooper())
    private var breathRunnable: Runnable? = null
    private var breathTick = 0
    private var breathPhase = 0

    private val affirmations = listOf(
        "Eres suficiente incluso cuando te sientes cansado o confundido.",
        "No tienes que resolverlo todo hoy: avanzar un poco ya es avanzar.",
        "Tu valor no depende de tu productividad ni de un mal momento.",
        "Pedir ayuda es una forma madura de cuidarte, no una debilidad.",
        "Respira con calma: ya superaste días difíciles antes y volverás a hacerlo.",
        "Mereces descanso, cariño y paciencia en tu propio proceso.",
        "Cada paso pequeño cuenta; ser constante vale más que ser perfecto.",
        "Tu sensibilidad, tu forma de querer y tu esfuerzo también son fortaleza."
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.remediesRecycler)
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = RemedyAdapter(buildRemedies(), ::showRemedyDialog)
    }

    private fun buildRemedies(): List<Remedy> = listOf(
        Remedy("😢", "Te sientes triste", "Para cuando", "Abrir aquí",
            "Está bien no estar bien. No te exijas sonreír de inmediato: respira, hidrátate y date permiso de sentir. Hoy tu tarea principal es cuidarte con suavidad.",
            Color.parseColor("#7AB0D8"), false, false, false),
        Remedy("💊", "Fue un mal día", "Para cuando", "1 dosis",
            "Un mal día no define tu historia. Lo que salió mal hoy puede enseñarte algo para mañana. Descansa, reorganiza y vuelve a intentarlo paso a paso.",
            Color.parseColor("#E8829A"), false, false, false),
        Remedy("🫁", "La ansiedad aprieta", "Para cuando", "Respirar",
            "Cuando notes el pecho apretado o la mente acelerada, usa esta respiración 4-4-6. Ayuda a bajar la activación y a recuperar claridad antes de tomar decisiones.",
            Color.parseColor("#7AC4A8"), true, false, false),
        Remedy("🫙", "Olvidaste lo bonito", "Para cuando", "Guardar",
            "Anota momentos buenos, por pequeños que sean: una conversación, una risa, una canción, algo que sí salió bien. Este frasco te recuerda que no todo fue oscuro.",
            Color.parseColor("#F0A07A"), false, false, true),
        Remedy("🤍", "Crees que no vales", "Para cuando", "Leer",
            "Tu valor no cambia por una caída emocional. Sigues siendo una persona valiosa, con talentos reales y una forma única de querer y construir vínculos.",
            Color.parseColor("#A890D8"), false, false, false),
        Remedy("✨", "Necesitas afirmación", "Para cuando", "Tocar",
            "Si tu diálogo interno se vuelve duro, usa estas afirmaciones como una voz de apoyo. No buscan magia: buscan darte piso emocional y perspectiva.",
            Color.parseColor("#8C74D7"), false, true, false),
        Remedy("🌿", "Tu mente se va lejos", "Para cuando", "5-4-3-2-1",
            "Usa la técnica 5-4-3-2-1 para volver al presente: 5 cosas que ves, 4 que tocas, 3 que escuchas, 2 que hueles y 1 que saboreas. Te ayuda a salir del bucle mental.",
            Color.parseColor("#56B88B"), false, false, false),
        Remedy("🙃", "Finges que estás bien", "Para cuando", "Solo tú",
            "No necesitas fingir fortaleza todo el tiempo. También mereces un espacio seguro para admitir que te duele, pedir contención y descansar de las máscaras.",
            Color.parseColor("#F0A07A"), false, false, false),
        Remedy("💌", "Me necesitas a mí", "Para cuando", "Mensaje",
            "No estás solo, aunque a veces lo parezca. Hay personas que te quieren de verdad y desean verte en paz. Hablar con alguien puede aliviar mucho más de lo que imaginas.",
            Color.parseColor("#E8829A"), false, false, false),
        Remedy("⭐", "No puedes dormir", "Para cuando", "Noche",
            "Si no puedes dormir, no pelees contra la noche. Baja estímulos: luz tenue, respiración suave, menos pantalla y pensamientos más lentos. Tu cuerpo sí sabe volver a calmarse.",
            Color.parseColor("#7AB0D8"), false, false, false)
    )

    private fun showRemedyDialog(remedy: Remedy) {
        stopBreathing()

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val sheet = LayoutInflater.from(this).inflate(R.layout.dialog_remedy_floating, null, false)

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

        sheet.findViewById<View>(R.id.dialogOverlay)?.setOnClickListener { dialog.dismiss() }
        sheet.findViewById<View>(R.id.dialogCard)?.setOnClickListener { /* consume */ }
        sheet.findViewById<View>(R.id.closeButton)?.setOnClickListener { dialog.dismiss() }

        dialog.setOnDismissListener { stopBreathing() }
        dialog.setContentView(sheet)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
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
