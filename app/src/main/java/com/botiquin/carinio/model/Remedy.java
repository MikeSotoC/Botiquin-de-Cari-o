package com.botiquin.carinio.model;

public class Remedy {
    public final String icon;
    public final String title;
    public final String subtitle;
    public final String badge;
    public final String detail;
    public final int color;
    public final boolean hasBreathTool;
    public final boolean hasAffirmations;
    public final boolean hasGratitudeJar;

    public Remedy(
            String icon,
            String title,
            String subtitle,
            String badge,
            String detail,
            int color,
            boolean hasBreathTool,
            boolean hasAffirmations,
            boolean hasGratitudeJar
    ) {
        this.icon = icon;
        this.title = title;
        this.subtitle = subtitle;
        this.badge = badge;
        this.detail = detail;
        this.color = color;
        this.hasBreathTool = hasBreathTool;
        this.hasAffirmations = hasAffirmations;
        this.hasGratitudeJar = hasGratitudeJar;
    }
}
