package com.korozhan.app.document;

/**
 * Created by veronika.korozhan on 14.03.2018.
 */
public class SeriesNumber {
    private String number;
    private String numberParam;
    private String series;

    private String seriesParam;

    public void setNumber(String number) {
        this.number = number;
    }
    public String getNumber() {
        return number;
    }
    public void setNumberParam(String numberParam) {
        this.numberParam = numberParam;
    }
    public String getNumberParam() {
        return numberParam;
    }
    public void setSeries(String series) {
        this.series = series;
    }
    public String getSeries() {
        return series;
    }
    public void setSeriesParam(String seriesParam) {
        this.seriesParam = seriesParam;
    }
    public String getSeriesParam() {
        return seriesParam;
    }
}
