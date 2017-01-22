package XmlMonitor.Logic.Workers;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class FileProcessingThread implements Callable {

    private static int _counter;

    private Path _xmlFileName;

    public FileProcessingThread(Path xmlFileName) {
        _xmlFileName = xmlFileName;
    }

    @Override
    public ArrayList call() {
        //String resultat = _xmlFileName.toString(); //readXMLDocument(_xmlFileName);
        //return resultat;

        return readXMLDocument();
    }

//    private boolean isCorrectFile(Path pathname) {
//        if (Files.isSymbolicLink(pathname)
//                || !Files.isWritable(pathname)
//                || Files.isDirectory(pathname)) return false;
//
//        PathMatcher pathMatcher = FileSystems.getDefault()
//                .getPathMatcher("glob:" + ConfigManager
//                        .getInstance().getTargetFileTypeGlob());
//
//        return pathMatcher.matches(pathname.getFileName());
//    }

    public ArrayList readXMLDocument() {
//        if (!isCorrectFile(filename)) return null;
        ArrayList<String> resultset = new ArrayList<>();

        SAXBuilder parser = new SAXBuilder();
        Document xmlDoc;

        System.out.println("readXMLDocument - " + _xmlFileName);

        try {
            _counter++;
            System.out.println("readXMLDocument - " + _xmlFileName + Files.isReadable(_xmlFileName) + _counter + " /");
            xmlDoc = parser.build(new File(_xmlFileName.toString())); //!!!!!!!!!!!!!!!!new Java API - File -> Path..
            List elements = xmlDoc.getRootElement().getContent(new ElementFilter("Entry"));
            Iterator iterator = elements.iterator();
            while (iterator.hasNext()) {
                Element head = (Element)iterator.next();
                String id = head.getAttributeValue("id");
                String name = head.getChildText("content");
                String department = head.getChildText("creationDate");
                //System.out.println(filename + ": " + id+" - "+name+" - "+department);
                resultset.add(_xmlFileName + ": " + id+" - "+name+" - "+department);
            }

            //ResultFileWorker.getInstance().addRecord(_xmlFileName.getFileName().toString() + " - completed");


        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ResultFileWorker.getInstance().addRecord(_xmlFileName.getFileName().toString() + " - completed");


        //String result = _xmlFileName.getFileName().toString() + " - XXXXXX"; //resultset;
        return resultset;
    }

}

