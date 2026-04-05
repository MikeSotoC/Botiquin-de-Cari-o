package com.botiquin.carinio;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.botiquin.carinio.model.Remedy;
import com.botiquin.carinio.ui.RemedyAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final Handler breathHandler = new Handler(Looper.getMainLooper());
    private Runnable breathRunnable;
    private int breathTick = 0;
    private int breathPhase = 0;

    private final List<String> affirmations = Arrays.asList(
            "Eres suficiente, incluso en tus días difíciles.",
            "No tienes que cargar todo tú solo.",
            "Tu valor no depende de un mal día.",
            "Pedir ayuda también es valentía.",
            "Respira: ya has superado momentos complicados antes.",
            "Mereces descanso, cuidado y amor.",
            "Paso a paso, sigues avanzando.",
            "Tu sensibilidad también es fortaleza."
    );

    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recycler = findViewById(R.id.remediesRecycler);
        recycler.setLayoutManager(new GridLayoutManager(this, 2));

        RemedyAdapter adapter = new RemedyAdapter(buildRemedies(), this::showRemedySheet);
        recycler.setAdapter(adapter);
    }

    private List<Remedy> buildRemedies() {
        List<Remedy> items = new ArrayList<>();

        items.add(new Remedy("😢", "Te sientes triste", "Para cuando", "Abrir aquí",
                "Está bien no estar bien. Date permiso de sentir y respirar sin prisa.",
                Color.parseColor("#7AB0D8"), false, false, false));

        items.add(new Remedy("💊", "Fue un mal día", "Para cuando", "1 dosis",
                "Un mal día no define tu vida. Mañana puedes volver a empezar.",
                Color.parseColor("#E8829A"), false, false, false));

        items.add(new Remedy("🫁", "La ansiedad aprieta", "Para cuando", "Respirar",
                "Usa el ejercicio guiado 4-4-6 para calmar cuerpo y mente.",
                Color.parseColor("#7AC4A8"), true, false, false));

        items.add(new Remedy("🫙", "Olvidaste lo bonito", "Para cuando", "Guardar",
                "Guarda pequeñas cosas buenas de hoy para releerlas cuando lo necesites.",
                Color.parseColor("#F0A07A"), false, false, true));

        items.add(new Remedy("🤍", "Crees que no vales", "Para cuando", "Leer",
                "Tu valor no cambia por cómo te sientes hoy. Sigues siendo una persona valiosa.",
                Color.parseColor("#A890D8"), false, false, false));

        items.add(new Remedy("✨", "Necesitas afirmación", "Para cuando", "Tocar",
                "Aquí tienes recordatorios amables para acompañarte.",
                Color.parseColor("#8C74D7"), false, true, false));

        items.add(new Remedy("🌿", "Tu mente se va lejos", "Para cuando", "5-4-3-2-1",
                "Mira 5 cosas, toca 4, escucha 3, huele 2 y saborea 1 para volver al presente.",
                Color.parseColor("#56B88B"), false, false, false));

        items.add(new Remedy("🙃", "Finges que estás bien", "Para cuando", "Solo tú",
                "No tienes que fingir. También mereces un lugar seguro para sentir.",
                Color.parseColor("#F0A07A"), false, false, false));

        items.add(new Remedy("💌", "Me necesitas a mí", "Para cuando", "Mensaje",
                "No estás solo. Hay personas que te quieren y se preocupan por ti.",
                Color.parseColor("#E8829A"), false, false, false));

        items.add(new Remedy("⭐", "No puedes dormir", "Para cuando", "Noche",
                "Si no llega el sueño, baja el ritmo: respiración suave, poca luz y calma.",
                Color.parseColor("#7AB0D8"), false, false, false));

        return items;
    }

    private void showRemedySheet(Remedy remedy) {
        stopBreathing();

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        LinearLayout sheet = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.bottom_sheet_remedy, null, false);

        TextView icon = sheet.findViewById(R.id.sheetIcon);
        TextView subtitle = sheet.findViewById(R.id.sheetSubtitle);
        TextView title = sheet.findViewById(R.id.sheetTitle);
        TextView detail = sheet.findViewById(R.id.sheetDetail);

        icon.setText(remedy.icon);
        subtitle.setText(remedy.subtitle);
        subtitle.setTextColor(remedy.color);
        title.setText(remedy.title);
        detail.setText(remedy.detail);

        LinearLayout breathSection = sheet.findViewById(R.id.breathSection);
        LinearLayout affirmSection = sheet.findViewById(R.id.affirmationSection);
        LinearLayout gratitudeSection = sheet.findViewById(R.id.gratitudeSection);

        breathSection.setVisibility(remedy.hasBreathTool ? LinearLayout.VISIBLE : LinearLayout.GONE);
        affirmSection.setVisibility(remedy.hasAffirmations ? LinearLayout.VISIBLE : LinearLayout.GONE);
        gratitudeSection.setVisibility(remedy.hasGratitudeJar ? LinearLayout.VISIBLE : LinearLayout.GONE);

        configureBreathing(sheet);
        configureAffirmations(sheet);
        configureGratitude(sheet);

        dialog.setOnDismissListener(d -> stopBreathing());
        dialog.setContentView(sheet);
        dialog.show();
    }

    private void configureBreathing(LinearLayout sheet) {
        TextView phaseText = sheet.findViewById(R.id.breathPhase);
        ProgressBar progress = sheet.findViewById(R.id.breathProgress);
        Button start = sheet.findViewById(R.id.startBreathButton);
        Button stop = sheet.findViewById(R.id.stopBreathButton);

        String[] phases = {"Inhala", "Sostén", "Exhala"};
        int[] duration = {4, 4, 6};
        final int cycleTicks = 140;

        start.setOnClickListener(v -> {
            stopBreathing();
            breathTick = 0;
            breathPhase = 0;

            breathRunnable = new Runnable() {
                @Override
                public void run() {
                    int totalTicksPhase = duration[breathPhase] * 10;
                    int inPhaseTick = breathTick % totalTicksPhase;
                    int percent = (int) ((inPhaseTick / (float) totalTicksPhase) * 100);

                    phaseText.setText(phases[breathPhase]);
                    progress.setProgress(percent);

                    breathTick++;
                    if (breathTick % totalTicksPhase == 0) {
                        breathPhase = (breathPhase + 1) % phases.length;
                    }

                    if (breathTick < cycleTicks) {
                        breathHandler.postDelayed(this, 100);
                    } else {
                        phaseText.setText("Ciclo completo. Muy bien.");
                        progress.setProgress(100);
                    }
                }
            };

            breathHandler.post(breathRunnable);
        });

        stop.setOnClickListener(v -> stopBreathing());
    }

    private void configureAffirmations(LinearLayout sheet) {
        Button nextAffirmation = sheet.findViewById(R.id.nextAffirmationButton);
        TextView affirmationText = sheet.findViewById(R.id.affirmationText);

        nextAffirmation.setOnClickListener(v -> {
            String affirmation = affirmations.get(random.nextInt(affirmations.size()));
            affirmationText.setText(affirmation);
        });
    }

    private void configureGratitude(LinearLayout sheet) {
        EditText input = sheet.findViewById(R.id.gratitudeInput);
        Button add = sheet.findViewById(R.id.addGratitudeButton);
        ChipGroup chips = sheet.findViewById(R.id.gratitudeChips);

        add.setOnClickListener(v -> {
            String text = input.getText() == null ? "" : input.getText().toString().trim();
            if (TextUtils.isEmpty(text)) return;

            Chip chip = new Chip(this);
            chip.setText("✦ " + text);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(view -> chips.removeView(chip));
            chips.addView(chip);
            input.setText("");
        });
    }

    private void stopBreathing() {
        if (breathRunnable != null) {
            breathHandler.removeCallbacks(breathRunnable);
            breathRunnable = null;
        }
    }

    @Override
    protected void onDestroy() {
        stopBreathing();
        super.onDestroy();
    }
}
