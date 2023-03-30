import manager.*;
import tasks.*;

import java.io.*;
import java.time.*;

public class Main {
    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        HistoryManager m = Managers.getDefaultHistory();

        Epic cookSoup = new Epic("Приготовить суп", "Суп c лапшой и грибами");
        manager.saveTaskAndEpic(cookSoup);
        Subtask cookBroth = new Subtask(1, "cookBroth", "descr SUBTASK", 20, LocalDateTime.of(2023, 01, 01, 02, 50));
        Subtask addNoodles = new Subtask(cookSoup.getId(), "Добавить лапшу", "Добавить 50 г. домашней лапши. Варить на среднем огне 10 мин.");
        Subtask addDill = new Subtask(cookSoup.getId(), "Добавить укроп", "Добавить зелень и довести до кипения.");
        Epic toHealCat = new Epic("Лечить кота", "Лечить кота Бaрсика от насморка");
        manager.saveTaskAndEpic(toHealCat);
        Task walkingDog = new Task("Выгулять собаку", "Выгуливать собаку на улице 30-40 мин.");

        manager.saveSubtask(cookBroth, cookSoup);
        manager.deleteSubtaskById(cookBroth.getId());
        manager.saveSubtask(addNoodles, cookSoup);
        manager.saveSubtask(addDill, cookSoup);
        manager.saveTaskAndEpic(walkingDog);


        System.out.println(manager.getPrioritizedTasks());
        manager.deleteAllSubtasks();
        manager.deleteAllTasks();
        System.out.println(manager.getPrioritizedTasks());


    }
}