//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.core.definition.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.io.Serializable;

public class Coordinate implements Serializable {
    private int x;
    private int y;

    public Coordinate() {
    }

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Coordinate that = (Coordinate)o;
            return this.x == that.x && this.y == that.y;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.x, this.y});
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("x", this.x).add("y", this.y).toString();
    }
}
