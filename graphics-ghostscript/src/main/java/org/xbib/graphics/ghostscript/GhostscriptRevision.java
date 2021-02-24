package org.xbib.graphics.ghostscript;

import java.time.LocalDate;

public class GhostscriptRevision {

    private String product;

    private String copyright;

    private String number;

    private LocalDate revisionDate;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public LocalDate getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(LocalDate revisionDate) {
        this.revisionDate = revisionDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String toString() {
        return "Product " + product + " copyright " + copyright + " revisiondate " + revisionDate + " number " + number;
    }
}
