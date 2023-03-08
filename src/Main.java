import manager.*;
import tasks.*;

import java.io.*;

import static tasks.Task.StatusList.*;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        HistoryManager m = Managers.getDefaultHistory();

        Epic cookSoup = new Epic("Приготовить суп", "Суп куриный с лапшой");
        manager.saveTaskAndEpic(cookSoup);
        Subtask cookBroth = new Subtask(cookSoup.getId(), "Сварить бульон", "Бульон варить 40 мин.", DONE);
        Subtask addNoodles = new Subtask(cookSoup.getId(), "Добавить лапшу", "Добавить 50 г. домашней лапши. Варить на среднем огне 10 мин.");
        Subtask addDill = new Subtask(cookSoup.getId(), "Добавить укроп", "Добавить зелень и довести до кипения.");
        Epic toHealCat = new Epic("Лечить кота", "Лечить кота Бaрсика от насморка");
        manager.saveTaskAndEpic(toHealCat);
        Task walkingDog = new Task("Выгулять собаку", "Выгуливать собаку на улице 30-40 мин.");

        manager.saveSubtask(cookBroth, cookSoup);
        manager.saveSubtask(addNoodles, cookSoup);
        manager.saveSubtask(addDill, cookSoup);
        manager.saveTaskAndEpic(walkingDog);

        manager.getEpicById(1);
        System.out.println(m.getHistory() + " (◕‿◕) \n");

        manager.getSubtasksById(3);
        manager.getTaskById(6);
        System.out.println(m.getHistory() + " ╰(▔∀▔)╯ \n");

        manager.getSubtasksById(4);
        manager.getSubtasksById(5);
        manager.getTaskById(6);
        System.out.println(m.getHistory() + " (⌒▽⌒)☆ \n");

        manager.getEpicById(2);
        manager.getEpicById(1);
        manager.getSubtasksById(3);
        manager.getSubtasksById(4);
        manager.getEpicById(2);
        manager.getSubtasksById(5);
        manager.getTaskById(6);
        manager.getSubtasksById(4);
        manager.getEpicById(1);
        manager.getSubtasksById(3);
        System.out.println(m.getHistory() + " (╯✧▽✧)╯ \n");

        manager.deleteTaskById(6);
        System.out.println(m.getHistory() + " (´｡• ω •｡`) \n");

        manager.deleteSubtaskById(3);
        System.out.println(m.getHistory() + " ଲ(ⓛ ω ⓛ)ଲ  \n");

        manager.deleteEpicById(1);
        System.out.println(m.getHistory() + " (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ \n");

        FileBackedTasksManager f = new FileBackedTasksManager(new File("./fileBackedTasksManager.csv"));

        f.saveTaskAndEpic(cookSoup);

        f.saveTaskAndEpic(toHealCat);


        f.saveSubtask(cookBroth, cookSoup);
        f.saveSubtask(addNoodles, cookSoup);
        f.saveSubtask(addDill, cookSoup);
        f.saveTaskAndEpic(walkingDog);
        f.getEpicById(2);
        f.getEpicById(1);
        f.getSubtasksById(3);
        f.getSubtasksById(4);
        f.getEpicById(2);
        f.getSubtasksById(5);
        f.getTaskById(6);
        f.getSubtasksById(4);
        f.getEpicById(1);
        f.getSubtasksById(3);


        f.save();


    }
}
