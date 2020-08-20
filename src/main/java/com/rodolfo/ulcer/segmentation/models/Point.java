package com.rodolfo.ulcer.segmentation.models;

import lombok.Data;

@Data
public class Point {
    
    private final Integer row;
    private final Integer col;

    public Point(Integer row, Integer col) {

        this.row = row;
        this.col = col;
    }
}