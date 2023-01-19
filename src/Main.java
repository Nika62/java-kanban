import model.Epic;
import model.Subtask;
public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        Epic cookSoup = new Epic("Приготовить суп", "Суп куриный с лапшой", "NEW");
        Subtask cookBroth = new Subtask( "Сварить бульон", "Бульон варить 40 мин.", "DONE");
        Subtask addNoodles = new Subtask( "Добавить лапшу", "Добавить 50 г. домашней лапши. Варить на среднем огне 10 мин.", "NEW");
        Epic toHealCat = new Epic("Лечить кота", "Лечить кота Бaрсика от насморка", "NEW");
        Subtask givPill = new Subtask("Дать таблетку", "Положить таблетку коту в рот, убедиться что проглотил.","NEW");

        manager.saveTaskAndEpic(cookSoup);
        manager.saveSubtask(cookBroth, cookSoup);
        manager.saveSubtask(addNoodles, cookSoup);
        manager.saveTaskAndEpic(toHealCat);
        manager.saveSubtask (givPill, toHealCat);
        System.out.println(manager.listEpics + "\n" + manager.listSubtasks + "\n" + manager.listTasks );
        manager.updateEpic(cookSoup);
        manager.deleteSubtaskById(5);
        manager.deleteEpicById(4);
        System.out.println(manager.listEpics + "\n" + manager.listSubtasks + "\n" + manager.listTasks );
    }
}
