import Service.WellServImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws SQLException {
        WellServImpl wdj = new WellServImpl();
        Scanner scanner = new Scanner(System.in);
        int number = 0;
        
        do {
            try {
                System.out.println("1 - Создание N кол-ва оборудования на скважине.");
                System.out.println("2 - Вывод общей информации об оборудовании на скважинах.");
                System.out.println("3 - Экспорт всех данных в XML файл.");
                System.out.println("4 - Выход.");

                System.out.println("Введите команду:");
                number = scanner.nextInt();

                switch (number) {
                    case (1):
                        System.out.println("Введите имя скважины:");
                        String wellName = scanner.next();
                        System.out.println("Введите количество оборудования:");
                        int equipmentAmount = scanner.nextInt();
                        wdj.fillTable(wellName, equipmentAmount);
                        break;
                    case (2):
                        System.out.println("Введите имена скважин, разделяя их пробелами или запятыми:");
                        scanner.nextLine();
                        String[] wellsName = scanner.nextLine().split("[,]");
                        wdj.searchWell(wellsName);
                        break;
                    case (3):
                        System.out.println("Укажите имя XML файла:");
                        String xmlFileName = scanner.next();
                        wdj.allWellXml(xmlFileName);
                        break;
                    case (4):
                        break;
                    default:
                        System.out.println("Такой команды нет.");
                        break;
                }
            }
            catch (InputMismatchException ex) {
                System.out.println("Такой команды нет.");
                scanner.nextLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (number != 4);
        
    }
}
