package com.anani.stockxpert.Model;

public class TempFactureItem {

    private final Integer id;
    private final String article;
    private final Integer pu;
    private Integer qte;
    private Integer total;

    public TempFactureItem(Integer id, String article, Integer pu, Integer qte, Integer total) {
        this.id = id;
        this.article = article;
        this.pu = pu;
        this.qte = qte;
        this.total = total;
    }

    public Integer getId() {
        return id;
    }

    public String getArticle() {
        return article;
    }

    public Integer getPu() {
        return pu;
    }

    public Integer getQte() {
        return qte;
    }

    public void setQte(Integer qte) {
        this.qte = qte;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "TempFactureItem{" +
                "id=" + id +
                ", article='" + article + '\'' +
                ", pu=" + pu +
                ", qte=" + qte +
                ", total=" + total +
                '}';
    }
}
