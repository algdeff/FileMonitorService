package XmlMonitor.Utils;

import XmlMonitor.Logic.db.DatabaseManager;
import org.jdom2.*;
import org.jdom2.filter.ContentFilter;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class XmlUtil {

    //private static volatile xmlCreate instance;
    //private static volatile SAXBuilder _parser;

    public XmlUtil() {
        //_parser = new SAXBuilder();

    }

//    public static synchronized xmlCreate getInstance() {
//        if (instance == null) {
//            instance = new xmlCreate();
//        }
//        return instance;
//    }




    public void createXMLDocument(String filename){

        double numEntries = Math.random() * 10 + 1;

        Document xmlDoc = new Document();
        Element root = new Element("Entries");
        xmlDoc.setRootElement(root);

        for (int entryNumber = 1; entryNumber < numEntries; entryNumber++) {
            Element entry = new Element("Entry");
            entry.setAttribute("id", String.valueOf(entryNumber));

            entry.addContent(new Comment("max string lenght 1024 symbols, " + String.valueOf(entryNumber)));
            Element content = new Element("content");
            content.addContent("DATA: " + generateRandomString());
            entry.addContent(content);

            Date date = new Date();
            //System.out.println(date.getTime() + " / " + date.toString() + " / " + date.getHours());

            entry.addContent(new Comment("date of record creation, " + String.valueOf(entryNumber)));
            Element creationDate = new Element("creationDate");

            LocalDateTime datetime = LocalDateTime.of(LocalDate.now(), LocalTime.of(13, new Random().nextInt(59), 22));
            Timestamp timestamp = Timestamp.valueOf(datetime);  //"2007-12-23 09:01:06.000000003");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
            creationDate.addContent(datetime.format(formatter));
            entry.addContent(creationDate);

            root.addContent(entry);

//            root.addContent(new Element("head").setAttribute("id","2")
//                    .addContent(new Comment("Head of Sales"))
//                    .addContent(new Element("name").addContent("Sidorov S."))
//                    .addContent(new Element("department").addContent("Sales"))
//            );
        }

        try {
            // Получаем "красивый" формат для вывода XML
            // с переводами на новую строку и отступами
            Format fmt = Format.getPrettyFormat();

            // Выводим созданный XML как поток байт на стандартный
            // вывод и в файл, используя подготовленный формат
            XMLOutputter serializer = new XMLOutputter(fmt);
            serializer.output(xmlDoc, System.out);
            serializer.output(xmlDoc, new FileOutputStream(new File(filename)));
        }
        catch (IOException e) {
            System.err.println(e);
        }
    }

    private String generateRandomString() {
        //String result = "__";
        //int result = (int) Math.random() * len;
//        for (int i = 1; i < len; i++) {
//            result += "Data";
//        }
        return String.valueOf(Math.random() * 999999999);
    }

    public static void listChildren(Element element) {
        System.out.println(element.getName());
        List children = element.getChildren();
        Iterator iterator = children.iterator();
        while (iterator.hasNext()) {
            Element child = (Element) iterator.next();
            listChildren(child);
        }
    }

    public void readXMLDocument(String filename) {
        SAXBuilder parser = new SAXBuilder();

        //String filename = ConfigManager.getInstance().getInputFolder()+file;

        Document xmlDoc;

        System.out.println("readXMLDocument - " + filename);

//        try {
//            xmlDoc = parser.build(new File(filename));
//
//            // Получаем список всех элементов head, которые
//            // содержит корневой элемент
//            List elements = xmlDoc.getRootElement()
//                    .getContent(new ElementFilter("Entry"));
//
//            // Для каждого элемента head получаем значение атрибута
//            // id и текст вложенных элементов name и department
//            Iterator iterator = elements.iterator();
//            while(iterator.hasNext()){
//                Element head = (Element)iterator.next();
//                String id = head.getAttributeValue("id");
//                String name = head.getChildText("content");
//                String department = head.getChildText("creationDate");
//
//                System.out.println(filename + ": " + id+" - "+name+" - "+department);
//            }
//
////            System.out.println("Comments:");
////
////            // Получаем все комментарии в документе и выводим для
////            // каждого его значение и имя элемента, который содержит
////            // этот комментарий
////            iterator = xmlDoc.getDescendants(new ContentFilter(ContentFilter.COMMENT));
////            while(iterator.hasNext()){
////                Content comment = (Content)iterator.next();
////                System.out.println(comment.getParentElement().getName()+": "+ comment.getValue());
////            }
//        } catch (JDOMException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


}
