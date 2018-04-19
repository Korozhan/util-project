package com.korozhan.app.report;

import java.math.BigDecimal;

/**
 * Created by veronika.korozhan on 23.11.2017.
 */
public class Loan {
    private String date;
    private BigDecimal principal;
    private BigDecimal interest;

    public Loan(String date, BigDecimal principal, BigDecimal interest) {
        this.date = date;
        this.principal = principal;
        this.interest = interest;
    }

    public String getDate() {
        return date;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public BigDecimal getInterest() {
        return interest;
    }
}
