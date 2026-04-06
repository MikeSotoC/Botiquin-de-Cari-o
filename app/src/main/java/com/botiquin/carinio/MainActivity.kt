package com.botiquin.carinio

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.botiquin.carinio.model.Remedy
import com.botiquin.carinio.ui.RemedyAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val breathHandler = Handler(Looper.getMainLooper())
    private var breathRunnable: Runnable? = null
    private var breathTick = 0
    private var breathPhase = 0

    private val affirmations = listOf(
        "🌟 Eres suficiente tal como eres, incluso en tus días grises.",
        "💛 No tienes que poder con todo hoy; avanzar un poquito también vale.",
        "🫶 Tu valor no depende de un error ni de una mala racha.",
        "🤍 Pedir apoyo es valentía: también mereces que te cuiden.",
        "🌿 Respira despacio, corazón: este momento difícil también va a pasar.",
        "✨ Mereces descanso, paz y palabras amables para ti mismo.",
        "🚶‍♂️ Paso a paso: la constancia suave vence a la perfección.",
        "🔥 Tu sensibilidad y tu forma de amar son una fuerza, no una debilidad."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.remediesRecycler)
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = RemedyAdapter(buildRemedies(), ::showRemedyDialog)
    }

    private fun buildRemedies(): List<Remedy> = listOf(
        Remedy(
            "😢", "Te sientes triste", "Para cuando", "Abrir aquí",
            """😢 Está bien estar triste. No tienes que apurarte a sentirte mejor.

💧 Toma agua, abrígate y respira profundo.

🫂 Recuerda: a veces no estar bien también es parte de sanar.""",
            Color.parseColor("#7AB0D8"), false, false, false
        ),
        Remedy(
            "💊", "Fue un mal día", "Para cuando", "1 dosis",
            """💊 ¿Hoy fue un día pesado? Tranquilo, amor: solo fue un día, no tu destino.

🌅 Mañana puedes volver a empezar con más calma.

💖 Sigue, poquito a poquito. Yo creo en ti.""",
            Color.parseColor("#E8829A"), false, false, false
        ),
        Remedy(
            "🫁", "La ansiedad aprieta", "Para cuando", "Respirar",
            """🫁 Cuando la ansiedad apriete, detente un momento.

🌬️ Inhala 4, sostén 4, exhala 6.

🛟 Este ejercicio le recuerda a tu cuerpo que ahora estás a salvo.""",
            Color.parseColor("#7AC4A8"), true, false, false
        ),
        Remedy(
            "🫙", "Olvidaste lo bonito", "Para cuando", "Guardar",
            """🫙 Guarda aquí tus momentos bonitos: una risa, una canción, un mensaje lindo.

✨ Cuando te sientas apagado, vuelve a leerlos.

🌈 Te ayudarán a recordar que también pasan cosas hermosas.""",
            Color.parseColor("#F0A07A"), false, false, true
        ),
        Remedy(
            "🤍", "Crees que no vales", "Para cuando", "Leer",
            """🤍 Si hoy dudas de ti, respira: tu valor no desaparece.

🏅 Eres valioso por quien eres, no por estar perfecto.

💫 Sigues siendo una persona única, capaz y profundamente querible.""",
            Color.parseColor("#A890D8"), false, false, false
        ),
        Remedy(
            "✨", "Necesitas afirmación", "Para cuando", "Tocar",
            """✨ A veces solo necesitas una frase correcta en el momento exacto.

🪞 Toca para recibir una afirmación y hablarte con más amor.

💜 Tu mente también merece ternura.""",
            Color.parseColor("#8C74D7"), false, true, false
        ),
        Remedy(
            "🌿", "Tu mente se va lejos", "Para cuando", "5-4-3-2-1",
            """🌿 Si tu mente se va muy lejos, vuelve al presente con 5-4-3-2-1.

👀 5 cosas que ves · ✋ 4 que tocas · 👂 3 que escuchas · 👃 2 que hueles · 👅 1 que saboreas.

🧭 Este anclaje te regresa al aquí y ahora.""",
            Color.parseColor("#56B88B"), false, false, false
        ),
        Remedy(
            "🙃", "Finges que estás bien", "Para cuando", "Solo tú",
            """🙃 No tienes que fingir estar bien para ser fuerte.

😭 También tienes derecho a llorar, cansarte y pedir abrazo.

🤝 Tu dolor importa, y tú también importas.""",
            Color.parseColor("#F0A07A"), false, false, false
        ),
        Remedy(
            "💌", "Me necesitas a mí", "Para cuando", "Mensaje",
            """💌 Si me necesitas, aquí estoy.

🩷 No estás solo: hay personas que te quieren y te sostienen.

📩 Hablar puede aliviar más de lo que imaginas.""",
            Color.parseColor("#E8829A"), false, false, false
        ),
        Remedy(
            "⭐", "No puedes dormir", "Para cuando", "Noche",
            """⭐ Si no puedes dormir, no te castigues.

🌙 Baja la luz, respira suave y deja que la noche te calme.

🛌 Tu cuerpo sabe descansar; solo necesita un poquito de paz.""",
            Color.parseColor("#7AB0D8"), false, false, false
        )
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
