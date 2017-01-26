������ ������� � ������ XmlMonitorService

���� ��� maven � JRE, 1-4 ��� ���:
1. ��������� maven: http://maven.apache.org/download.cgi
2. ������������� ����� � maven �� ����
3. ��������� � ���������� %PATH% ���� � maven/bin, �������� c:\apache-maven-3.3.9\bin\
4. ��������� � ������������� JRE ��� ����� �������: http://www.oracle.com/technetwork/java/javase/downloads/index.html
5. ��������� � ���������� XmlMonitorService
6. ��� ������ ������� � ������� maven �����: mvn compile
7. ����� ������ ��������� � target/classes/ � ����������� ��������������� ������� xmlmonitor.conf.xml, hibernate.cfg.xml
8. c������ � ���� ������ ������� 'xml_files_entries', ������ �����
7. ��������� �������: mvn exec:java -Dexec.mainClass="XmlMonitor.ServerStarter"
8. ����� �� ���������: CTRL+C

����� ���-�� ������� ������ � ���� ����������� .jar ����, �� ����� �������� � �������������:
1. ������� ����������� .jar: mvn package
2. ���� �������� � target/XmlMonitorService-......-jar-with-dependencies.jar (����. ��������� ������� 'maven-assembly-plugin' � pom.xml)
3. ���� ����� ����������� � ����� ����� (������� �� "progFolder")
4. � ����� � ������ (progFolder) ����������� ������ ��������� hibernate.cfg.xml c ����������� ������� � ��
5. ���-�� � progFolder ����� ��������� ���� �������� xmlmonitor.conf.xml, ����� ����� ��������� �� ���������
6. � ��������� ������ .jar �� JVM: java -jar XmlMonitorService-..(������)..-jar-with-dependencies.jar

=============================================================================================
���������������� ����: xmlmonitor.conf.xml

������ ���� � ���������� ����������, � ��� maven ������ � target/classes/
���� �����������, ����������� ��������� � ������ �������� � ���.

=============================================================================================
C������� ������� � ���� ������ (SQL):

CREATE TABLE `scheme_name`.`xml_files_entries` (
  `id` INT NOT NULL AUTO_INCREMENT, 
  `filename` VARCHAR(100) NOT NULL,
  `entry_id` INT NULL,
  `entry_content` VARCHAR(1000) NULL,
  `entry_creation_date` DATETIME NULL,
  PRIMARY KEY (`id`));



=============================================================================================
��������� ������� � ��: hibernate.cfg.xml

<property name="hibernate.connection.url">jdbc:mysql://databaseURL:3306/scheme_name</property>
���� ��������� ������ "TimeZone... UTC" �� ����� ����� ��(scheme_name) ��������� "?serverTimezone=UTC"

���� ���� ������ Postgre �� ������ MySQLInnoDBDialect �� PostgreSQL94Dialect � ������:
<property name="hibernate.dialect">org.hibernate.dialect.MySQLInnoDBDialect</property>