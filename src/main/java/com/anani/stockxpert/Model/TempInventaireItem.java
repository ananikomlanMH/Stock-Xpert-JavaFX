package com.anani.stockxpert.Model;

public class TempInventaireItem {

    private final Integer id;
    private final String article;
    private Integer qte;

    public TempInventaireItem(Integer id, String article, Integer qte) {
        this.id = id;
        this.article = article;
        this.qte = qte;
    }

    public Integer getId() {
        return id;
    }

    public String getArticle() {
        return article;
    }

    public Integer getQte() {
        return qte;
    }

    public void setQte(Integer qte) {
        this.qte = qte;
    }

    @Override
    public String toString() {
        return "TempInventaireItem{" +
                "id=" + id +
                ", article='" + article + '\'' +
                ", qte=" + qte +
                '}';
    }
}
