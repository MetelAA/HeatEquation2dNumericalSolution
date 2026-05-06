package org.example.testfx.Constants;

public class Constants {
    public final static int MAX_TEMPERATURE = 2000;
    public final static int MIN_TEMPERATURE = -273;
    public final static int THREAD_COUNT = 16;
    public final static String workMod = "debug"; //prod || debug
    public final static double WRITE_FRAME_PER_SECOND = 0.1;
    public final static String PARAMS_FILE_LOCATION = "params.json";
    public final static String TEMP_MAP_FOR_NUM_METHOD_FILE_LOCATION = "result_num.txt";
    public final static String TEMP_MAP_FOR_ANALYTICAL_METHOD_FILE_LOCATION = "result_analytical.txt";
    public final static String TEMP_MAP_PARAMS_FILE_LOCATION = "result_params.json";
    public final static String COMPARE_PARAMS_FILE_LOCATION = "compare_params.json";
    public final static int MIN_HARMONIC_COUNT = 10;
    public final static int MAX_HARMONIC_COUNT = 100;
    public final static int DEFAULT_FPS = 15;
    public final static int DYNAMIC_GRADIENT_TEMPERATURE_LIMITS_CHECK_INTERVAL = 10; // 1 раз в 10 прочитанных кадров
    public final static int DYNAMIC_GRADIENT_TEMPERATURE_PERCENT_DELTA_TO_REDRAW_GRADIENT = 25;
    public final static double DYNAMIC_GRADIENT_MINIMUM_ABS_TEMPERATURE_DELTA = 1; //минимальная дельта между максимальным и минимальным значением в градиенте температур
    public final static String MAX_TEMPERATURE_DIFFERENCE_GRAPHIC_DATA_FILE_LOCATION = "max_difference_graphic_data.txt";
    public final static String AVERAGE_QUAD_TEMPERATURE_GRAPHIC_DATA_FILE_LOCATION = "avg_quad_difference_graphic_data.txt";

}