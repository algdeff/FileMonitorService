сборка проекта и запуск XmlMonitorService

Если нет maven и JRE, 1-4 для Вас:
1. скачиваем maven: http://maven.apache.org/download.cgi
2. распаковываем архив с maven на диск
3. добавляем в переменные %PATH% путь к maven/bin, например c:\apache-maven-3.3.9\bin\
4. скачиваем и устанавливаем JRE для Вашей системы: http://www.oracle.com/technetwork/java/javase/downloads/index.html
5. переходим в директорию XmlMonitorService
6. для сборки проекта с помошью maven пишем: mvn compile
7. после сборки переходим в target/classes/ и настраиваем нижеприведенные конфиги xmlmonitor.conf.xml, hibernate.cfg.xml
8. cоздаем в базе данных таблицу 'xml_files_entries', пример внизу
7. запускаем монитор: mvn exec:java -Dexec.mainClass="XmlMonitor.ServerStarter"
8. выход из программы: CTRL+C

Можно так-же собрать проект в один исполняемый .jar файл, со всеми классами и зависимостями:
1. Создаем исполняемый .jar: mvn package
2. Файл появится в target/XmlMonitorService-......-jar-with-dependencies.jar (прим. настройки плагина 'maven-assembly-plugin' в pom.xml)
3. Файл можно переместить в любую папку (назовем ее "progFolder")
4. В папке с файлом (progFolder) обязательно должен нажодится hibernate.cfg.xml c настройками доступа к БД
5. Так-же в progFolder можно поместить файл настроек xmlmonitor.conf.xml, иначе будут настройки по умолчанию
6. И посдеднее запуск .jar на JVM: java -jar XmlMonitorService-..(версия)..-jar-with-dependencies.jar

=============================================================================================
Конфигурационный файл: xmlmonitor.conf.xml

должен быть в директории приложения, а для maven сборки в target/classes/
Путь мониторинга, результатов обработки и других настроек в нем.

=============================================================================================
Cоздание таблицы в базе данных (SQL):

CREATE TABLE `scheme_name`.`xml_files_entries` (
  `id` INT NOT NULL AUTO_INCREMENT, 
  `filename` VARCHAR(100) NOT NULL,
  `entry_id` INT NULL,
  `entry_content` VARCHAR(1000) NULL,
  `entry_creation_date` DATETIME NULL,
  PRIMARY KEY (`id`));



=============================================================================================
Настройка доступа к БД: hibernate.cfg.xml

<property name="hibernate.connection.url">jdbc:mysql://databaseURL:3306/scheme_name</property>
если возникает ошибка "TimeZone... UTC" то после имени БД(scheme_name) добавляем "?serverTimezone=UTC"

Если база данных Postgre то меняем MySQLInnoDBDialect на PostgreSQL94Dialect в записи:
<property name="hibernate.dialect">org.hibernate.dialect.MySQLInnoDBDialect</property>