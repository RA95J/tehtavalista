package com.example.application.data;

import jakarta.persistence.Entity;

@Entity
public class Tehtavalista extends AbstractEntity {

    private String kategoria;
    private String nimi;
    private String tehtava;
    private String kommentti;

    public String getKategoria() {
        return kategoria;
    }
    public void setKategoria(String kategoria) {
        this.kategoria = kategoria;
    }
    public String getNimi() {
        return nimi;
    }
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    public String getTehtava() {
        return tehtava;
    }
    public void setTehtava(String tehtava) {
        this.tehtava = tehtava;
    }
    public String getKommentti() {
        return kommentti;
    }
    public void setKommentti(String kommentti) {
        this.kommentti = kommentti;
    }

}
