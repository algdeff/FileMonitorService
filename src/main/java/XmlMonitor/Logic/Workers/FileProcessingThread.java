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
import java.nio.file.*;
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
        return processedXMLDocument();
    }

    private ArrayList processedXMLDocument() {
        ArrayList<String> resultset = new ArrayList<>();

//        Path processedFile = Paths.get(ConfigManager.getProcessedFilesPath().toString(),
//                _xmlFileName.getFileName().toString());
//
//        if (Files.exists(processedFile)) {
//            String message = "File: " + _xmlFileName.getFileName().toString()
//                    + " already exist in processed folder!";
//            resultset.add(message);
//            System.err.println(message);
//            return resultset;
//        }

        boolean isProcessed = false;
        Document xmlDoc;
        Timestamp timestamp;

        String entryName = ConfigManager.getEntryName();
        String entityContent = ConfigManager.getEntryContent();
        String entryDate = ConfigManager.getEntryDate();

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

                XmlFilesEntriesEntity databaseRecord = new XmlFilesEntriesEntity();
                databaseRecord.setFilename(_xmlFileName.getFileName().toString());
                databaseRecord.setEntryId(id != null ? Integer.parseInt(id) : null);
                databaseRecord.setEntryContent(content);

                try {
                    timestamp = Timestamp.valueOf(date);
                } catch (IllegalArgumentException iae) {
                    System.err.println("Incorrect DATE format in entry: "
                            + id + ", file: " + _xmlFileName.getFileName().toString());
                    timestamp = null;
                }
                databaseRecord.setEntryCreationDate(timestamp);
                DatabaseManager.getInstance().saveEntity(databaseRecord);
                resultset.add(_xmlFileName.getFileName() + ": " + id + " - "
                        + timestamp + " --- entry add to database");
            }
            isProcessed = elements.size() > 0;

        } catch (JDOMException jde) {
            jde.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("File not read: " + _xmlFileName.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        moveFile(isProcessed);

        System.err.println("file removed: " + _xmlFileName.toString());

//        try {
//            TimeUnit.SECONDS.sleep(20);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        return resultset;
    }

    private void moveFile(boolean isProcessed) {

        Path target = isProcessed ? Paths.get(ConfigManager.getProcessedFilesPath().toString(),
                _xmlFileName.getFileName().toString()) :
                Paths.get(ConfigManager.getIncorrectFilesPath().toString(),
                        _xmlFileName.getFileName().toString());

        try {
            Files.move(_xmlFileName, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (FileSystemException fse) {
            System.out.println("File: " + _xmlFileName.getFileName().
                    toString() + " is already used!");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

}

