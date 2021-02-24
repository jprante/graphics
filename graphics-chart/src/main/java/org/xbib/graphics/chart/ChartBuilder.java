package org.xbib.graphics.chart;

import org.xbib.graphics.chart.theme.DefaultTheme;
import org.xbib.graphics.chart.theme.Theme;

public abstract class ChartBuilder<T extends ChartBuilder<?, ?>, C extends Chart<?, ?>> {

    private int width;

    private int height;

    private String title;

    private Theme theme;

    public ChartBuilder() {
        this.width = 800;
        this.height = 600;
        this.title = "";
        this.theme = new DefaultTheme();
    }

    @SuppressWarnings("unchecked")
    public T width(int width) {
        this.width = width;
        return (T) this;
    }

    public int getWidth() {
        return width;
    }

    @SuppressWarnings("unchecked")
    public T height(int height) {
        this.height = height;
        return (T) this;
    }

    public int getHeight() {
        return height;
    }

    @SuppressWarnings("unchecked")
    public T title(String title) {
        this.title = title;
        return (T) this;
    }

    public String getTitle() {
        return title;
    }

    @SuppressWarnings("unchecked")
    public T theme(Theme theme) {
        if (theme != null) {
            this.theme = theme;
        }
        return (T) this;
    }

    public Theme getTheme() {
        return theme;
    }

    public abstract C build();
}
