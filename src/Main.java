import manager.*;
import server.*;
import tasks.*;

import java.io.*;
import java.time.*;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        TaskManager manager = Managers.getDefault();
        HistoryManager history = Managers.getDefaultHistory();
        KVServer server = new KVServer();
        server.start();

        Epic cookSoup = new Epic("Приготовить суп", "Суп c лапшой и грибами");
        manager.saveTaskAndEpic(cookSoup);
        Subtask cookBroth = new Subtask(1, "cookBroth", "descr SUBTASK", 20, LocalDateTime.of(2023, 01, 01, 02, 50));
        Subtask addNoodles = new Subtask(cookSoup.getId(), "Добавить лапшу", "Добавить 50 г. домашней лапши. Варить на среднем огне 10 мин.", 10, LocalDateTime.of(2023, 01, 01, 03, 20));
        Subtask addDill = new Subtask(cookSoup.getId(), "Добавить укроп", "Добавить зелень и довести до кипения.");
        Epic toHealCat = new Epic("Лечить кота", "Лечить кота Бaрсика от насморка");
        manager.saveTaskAndEpic(toHealCat);
        Task walkingDog = new Task("Выгулять собаку", "Выгуливать собаку на улице 30-40 мин.", 40, LocalDateTime.of(2023, 01, 02, 13, 00));

        manager.saveSubtask(cookBroth, cookSoup);
        manager.saveSubtask(addNoodles, cookSoup);
        manager.saveSubtask(addDill, cookSoup);
        manager.saveTaskAndEpic(walkingDog);

        manager.getEpicById(1);
        manager.getEpicById(2);
        manager.getSubtaskById(3);
        manager.getSubtaskById(4);
        manager.getSubtaskById(5);
        manager.getTaskById(6);

        System.out.println(history.getHistory());
        System.out.println(manager.getPrioritizedTasks());
        server.stop();
    }
}