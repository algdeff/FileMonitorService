package XmlMonitor.Logic.Workers;

import XmlMonitor.HibernateEntities.XmlFilesEntriesEntity;
import XmlMonitor.Logic.ConfigManager;
import XmlMonitor.Logic.db.DatabaseManager;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public class FileProcessingThread implements Callable {

    private Path _xmlFileName;


    public FileProcessingThread(Path xmlFileName) {
        _xmlFileName = xmlFileName;
    }

    @Override
    public ArrayList call() {
        return readXMLDocument();
    }

    private ArrayList readXMLDocument() {
        boolean isProcessed = false;
        ArrayList<String> resultset = new ArrayList<>();
        Document xmlDoc;

        String entryName = ConfigManager.getInstance().getEntryName();
        String entityContent = ConfigManager.getInstance().getEntryContent();
        String entryDate = ConfigManager.getInstance().getEntryDate();

        SAXBuilder parser = new SAXBuilder();
        try {

            xmlDoc = parser.build(new File(_xmlFileName.toString()));
            List elements = xmlDoc.getRootElement().getContent(new ElementFilter(entryName));
            Iterator iterator = elements.iterator();

            while (iterator.hasNext()) {

                Element entry = (Element)iterator.next();
                String id = entry.getAttributeValue("id");
                String content = entry.getChildText(entityContent);
                String date = entry.getChildText(entryDate);
                resultset.add(_xmlFileName + ": " + id+" - "+content+" - "+date);

                XmlFilesEntriesEntity databaseRecord = new XmlFilesEntriesEntity();
                databaseRecord.setFilename(_xmlFileName.getFileName().toString());
                databaseRecord.setEntryId(Integer.parseInt(id));
                databaseRecord.setEntryContent(content);

                LocalDateTime datetime = LocalDateTime.of(LocalDate.now(), LocalTime.now());

//                System.out.println(datetime.toString() + " / 2007-12-23 09:01:06.000000003 "
//                        + LocalDate.now() + " " + LocalTime.now());

                Timestamp timestamp = Timestamp.valueOf(date);  //"2007-12-23 09:01:06.000000003");

                System.out.println(timestamp.toString());

                databaseRecord.setEntryCreationDate(timestamp);
                DatabaseManager.getInstance().saveEntity(databaseRecord);
            }
            isProcessed = elements.size() > 0;

        } catch (JDOMException jde) {
            jde.printStackTrace();
        } catch (IllegalArgumentException iae) {
            System.err.println("Wrong DATE format in: " + _xmlFileName.toString());
        } catch (IOException ioe) {
            System.err.println("File not read: " + _xmlFileName.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        moveFile(isProcessed);

//        try {
//            TimeUnit.SECONDS.sleep(20);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //ResultFileWorker.getInstance().addRecord(_xmlFileName.getFileName().toString() + " - completed");
        return resultset;
    }

    private void moveFile(boolean isProcessed) {

        Path target = isProcessed ? Paths.get(ConfigManager.getInstance().getProcessedFilesPath().toString(),
                _xmlFileName.getFileName().toString()) :
                Paths.get(ConfigManager.getInstance().getIncorrectFilesPath().toString(),
                        _xmlFileName.getFileName().toString());

        try {
            Files.move(_xmlFileName, target);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

