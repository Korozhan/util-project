package com.korozhan.app.document;

import java.util.List;

/**
 * Created by veronika.korozhan on 16.03.2018.
 */
public class DocumentCard extends Document{
    private List<PayCard> paycard;
    private List<Conditions> conditionsCompletion;

    public DocumentCard () {
        super();
    }

    public List<PayCard> getPaycard() {
        return paycard;
    }

    public void setPaycard(List<PayCard> paycard) {
        this.paycard = paycard;
    }

    public List<Conditions> getConditionsCompletion() {
        return conditionsCompletion;
    }

    public void setConditionsCompletion(List<Conditions> conditionsCompletion) {
        this.conditionsCompletion = conditionsCompletion;
    }
}
