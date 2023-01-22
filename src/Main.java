import model.Epic;
import model.Subtask;

import static model.Task.statusList.DONE;
import static model.Task.statusList.IN_PROGRESS;
public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        Epic cookSoup = new Epic("Приготовить суп", "Суп куриный с лапшой");
        manager.saveTaskAndEpic(cookSoup);
        Subtask cookBroth = new Subtask(cookSoup.getId(), "Сварить бульон", "Бульон варить 40 мин.", DONE);
        Subtask addNoodles = new Subtask(cookSoup.getId(), "Добавить лапшу", "Добавить 50 г. домашней лапши. Варить на среднем огне 10 мин.");
        Epic toHealCat = new Epic("Лечить кота", "Лечить кота Бaрсика от насморка");
        manager.saveTaskAndEpic(toHealCat);
        Subtask givePill = new Subtask(toHealCat.getId(), "Дать таблетку", "Положить таблетку коту в рот, убедиться что проглотил.");

        manager.saveSubtask(cookBroth, cookSoup);
        manager.saveSubtask(addNoodles, cookSoup);
        manager.saveSubtask(givePill, toHealCat);
        givePill.setStatus(IN_PROGRESS);
        manager.updateSubtask(givePill);
        manager.deleteEpicById(2);
        manager.deleteSubtaskById(3);
        System.out.println(manager.getListEpics() + "\n" + manager.getListSubtasks());


    }
}
