//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.core.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.kuma.boot.captcha.support.core.definition.domain.Coordinate;
import java.util.List;

public class Verification extends Captcha {
    private Coordinate coordinate;
    private List<Coordinate> coordinates;
    private String characters;

    public Coordinate getCoordinate() {
        return this.coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public List<Coordinate> getCoordinates() {
        return this.coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public String getCharacters() {
        return this.characters;
    }

    public void setCharacters(String characters) {
        this.characters = characters;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Verification that = (Verification)o;
            return Objects.equal(this.characters, that.characters);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.characters});
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("characters", this.characters).toString();
    }
}
