package ru.mpei.fqw.dto;

import lombok.Data;


@Data
public class FaultCurrentDto {
    private String name;
    private Double value;
    private Integer time;
    private Integer indexOfValues;

    public FaultCurrentDto(String name, Double value, Integer indexOfValues) {
        this.name = name;
        this.value = value;
        this.indexOfValues = indexOfValues;
    }
}
