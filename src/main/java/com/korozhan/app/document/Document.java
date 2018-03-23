package com.korozhan.app.document;

import java.util.List;

/**
 * Created by veronika.korozhan on 14.03.2018.
 */
public class Document {
    private String contractName;
    private SeriesNumber seriesNumber;
    private List<Header> header;
    private List<Description> description;
    private List<Conditions> conditions;

    public Document() {
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractName() {
        return contractName;
    }

    public void setSeriesNumber(SeriesNumber seriesNumber) {
        this.seriesNumber = seriesNumber;
    }

    public SeriesNumber getSeriesNumber() {
        return seriesNumber;
    }

    public void setHeader(List<Header> header) {
        this.header = header;
    }

    public List<Header> getHeader() {
        return header;
    }

    public void setDescription(List<Description> description) {
        this.description = description;
    }

    public List<Description> getDescription() {
        return description;
    }

    public void setConditions(List<Conditions> conditions) {
        this.conditions = conditions;
    }

    public List<Conditions> getConditions() {
        return conditions;
    }
}
