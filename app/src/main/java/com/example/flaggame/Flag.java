package com.example.flaggame;

import android.annotation.SuppressLint;
import android.content.res.Resources;


import java.util.Objects;

public class Flag {
    private final int drawableResourceId;
    private final String pathToPng;
    private final String countryName;
    private final int levelOfFamiliarity;


    @SuppressLint("DiscouragedApi")
    public Flag(String pathToPng, String countryName, int levelOfFamiliarity, Resources resources) {
        this.pathToPng = pathToPng;
        this.countryName = countryName;
        this.levelOfFamiliarity = levelOfFamiliarity;
        this.drawableResourceId = resources.getIdentifier(pathToPng, "drawable",
                MainActivity.getPackageNameStatic());
    }

    public int getDrawableResourceId() {
        return drawableResourceId;
    }

    public String getCountryName() {
        return countryName;
    }

    public int getLevelOfFamiliarity() {
        return levelOfFamiliarity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flag flag = (Flag) o;
        return levelOfFamiliarity == flag.levelOfFamiliarity && Objects.equals(pathToPng, flag.pathToPng) && Objects.equals(countryName, flag.countryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathToPng, countryName, levelOfFamiliarity);
    }
}
