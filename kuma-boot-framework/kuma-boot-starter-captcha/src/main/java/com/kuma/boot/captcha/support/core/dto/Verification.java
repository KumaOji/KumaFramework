/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 */
package com.kuma.boot.captcha.support.core.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.kuma.boot.captcha.support.core.definition.domain.Coordinate;

import java.util.List;

public class Verification
extends Captcha {
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
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Verification that = (Verification)o;
        return Objects.equal((Object)this.characters, (Object)that.characters);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.characters});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("characters", (Object)this.characters).toString();
    }
}

