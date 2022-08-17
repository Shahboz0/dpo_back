package ru.mpei.fqw.utils;


import lombok.Data;

import java.util.List;

@Data
public class ComtradeToJson {
    private String name;
    private String type;
    private List<?> values;

//    private String measurementUnit;
//    private Float maxValue;
//    private Float minValue;
}
