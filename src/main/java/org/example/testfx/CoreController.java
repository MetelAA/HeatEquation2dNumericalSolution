package org.example.testfx;

import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.DTO.SimulationParameters;
import org.example.testfx.HeatEquation.HeatEquationCore;
import org.example.testfx.utils.ReadWriteTMap;

import java.io.IOException;

public class CoreController {
    private final HeatEquationCore heatEquation;
    private final PlateParameters plateParameters;
    private final SimulationParameters simulationParameters;

    public CoreController(PlateParameters plateParameters, SimulationParameters simulationParameters) {
        this.plateParameters = plateParameters;
        this.simulationParameters = simulationParameters;
        System.out.println("Приступаю к инициализации HeatEquationCore");
        heatEquation = new HeatEquationCore(plateParameters, simulationParameters.getDx(), simulationParameters.getDy(), simulationParameters.getDt());
    }

    void run(){
        System.out.println("core controller run");
        int nt = (int) (simulationParameters.getTime() / simulationParameters.getDt());

        System.out.println("Всего предстоит " + nt + " шагов по времени, " + heatEquation.getNx() + " шагов по x, " + heatEquation.getNy() + "шагов по y" );
        System.out.println("Т.е. всего O("+ (((long)nt)*heatEquation.getNy()* heatEquation.getNx()) + ") шагов");

        // не знаю как пока сделать, но будем исходить из 60 кадров в сек

        int dtPerSec = (int) (1 / simulationParameters.getDt());
        ReadWriteTMap mapIO = new ReadWriteTMap("test.txt");

        try {
            mapIO.initWriter();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка инициализации писателя данных, с сообщением: " + e);
        }

        for (int i = 0; i < nt; i++) {
            heatEquation.step();
            if(i % dtPerSec == 0){
                try {
                    mapIO.writeMap(heatEquation.gettMap(), i / dtPerSec);
                } catch (IOException e) {
                    throw new RuntimeException("Ошибка записи очередного шага, с сообщением: " + e);
                }
            }
        }

        System.out.println("Всё успешно выполнено!");
    }
}
