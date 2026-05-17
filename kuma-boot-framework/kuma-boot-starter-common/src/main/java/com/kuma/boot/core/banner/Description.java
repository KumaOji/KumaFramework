package com.kuma.boot.core.banner;

public class Description {

    private String name;
    private String description;
    private int leftCellPadding = 0;
    private int rightCellPadding = 1;

    public Description() {}

    public Description(String name, String description, int leftCellPadding, int rightCellPadding) {
        this.name = name;
        this.description = description;
        this.leftCellPadding = leftCellPadding;
        this.rightCellPadding = rightCellPadding;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getLeftCellPadding() { return leftCellPadding; }
    public void setLeftCellPadding(int leftCellPadding) { this.leftCellPadding = leftCellPadding; }

    public int getRightCellPadding() { return rightCellPadding; }
    public void setRightCellPadding(int rightCellPadding) { this.rightCellPadding = rightCellPadding; }
}
