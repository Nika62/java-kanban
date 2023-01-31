import ru.javakanban.*;
import ru.javakanban.interfaces.*;
import ru.javakanban.model.*;

import static ru.javakanban.model.Task.StatusList.DONE;
import static ru.javakanban.model.Task.StatusList.IN_PROGRESS;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Epic cookSoup = new Epic("Приготовить суп", "Суп куриный с лапшой");
        manager.saveTaskAndEpic(cookSoup);
        Subtask cookBroth = new Subtask(cookSoup.getId(), "Сварить бульон", "Бульон варить 40 мин.", DONE);
        Subtask addNoodles = new Subtask(cookSoup.getId(), "Добавить лапшу", "Добавить 50 г. домашней лапши. Варить на среднем огне 10 мин.");
        Epic toHealCat = new Epic("Лечить кота", "Лечить кота Бaрсика от насморка");
        manager.saveTaskAndEpic(toHealCat);
        Subtask givePill = new Subtask(toHealCat.getId(), "Дать таблетку", "Положить таблетку коту в рот, убедиться что проглотил.");
        Task walkingDog = new Task("Выгулять собаку", "Выгуливать собаку на улице 30-40 мин.");

        manager.saveSubtask(cookBroth, cookSoup);
        manager.saveSubtask(addNoodles, cookSoup);
        manager.saveSubtask(givePill, toHealCat);
        manager.saveTaskAndEpic(walkingDog);
        givePill.setStatus(IN_PROGRESS);
        manager.updateSubtask(givePill);
        manager.getEpicById(1);
        manager.getSubtasksById(3);
        manager.getTaskById(6);
        manager.getSubtasksById(4);
        manager.getTaskById(6);
        manager.getEpicById(2);
        manager.getEpicById(2);
        System.out.println(Managers.getDefaultHistory().getHistory());
        manager.deleteEpicById(2);
        manager.deleteSubtaskById(3);
        System.out.println(manager.getListAllEpic() + "\n" + manager.getListAllSubtasks() + "\n" + manager.getListAllTasks() + "\n" + manager.getListSubtasksOfEpic(cookSoup));

    }
}
