package ru.mpei.fqw.client;

import lombok.Getter;
import lombok.Setter;
import java.util.stream.IntStream;

/**
 * @author Александр Холодов
 * @created 08.2020
 * @project VIED
 * @description Дискретное преобразование Фурье (полное)
 */
public class FourierImpl {

    /*
     * Настройка
     */

    /** Количество выборок за период */
    private static final int size = 400;

    /*
     * Переменные
     */

    /** Коэффициент Фурье */
    protected double k = Math.sqrt(2) /size;

    /** Буфер Sin Cos */
    private final double[] sin = new double[size];
    private final double[] cos = new double[size];

    /** Номер выборки */
    private int count = 0;

    /** Сумматор ортогональных составляющих */
    private double intX = 0, intY = 0;

    /** Буфер ортогональных составляющих */
    private final double[] xb = new double[size], yb = new double[size];

    /**
     * Фильтр Фурье
     * @param harm - номер гармоники
     */
    public FourierImpl(int harm){
        /* Вычисление sin и cos */
        IntStream.range(0, size).forEach(v -> {
            sin[v] = Math.sin(((double) harm * 2*Math.PI * ((double) v/size)));
            cos[v] = Math.cos(((double) harm * 2*Math.PI * ((double) v/size)));
        });
    }

    /**
     * Произвести шаг расчета окна (для токов)
     * @param instMag - Входная мгновенная величина
     * @param vector - Выходной вектор
     */
    public void process(double instMag, VectorF vector) {

        /* Вычисление xy составляющих */
        double x = instMag * sin[count];
        double y = instMag * cos[count];

        /* Интегрирование периода */
        intX += (x - xb[count]); xb[count] = x;
        intY += (y - yb[count]); yb[count] = y;

        /* Передача ортогональных составляющих */
        vector.setOrtPair(k * intX, k * intY);

        /* Сдвиг плавающего окна */
        if(++count > size - 1) count = 0;
    }

    /** Сброс фильтра */
    public void reset() {
        intX = 0; intY = 0; count = 0;
        IntStream.range(0, size).forEach(i -> { xb[i] = 0.0; yb[i] = 0.0; });
    }



}
