package XmlMonitor.Logic.Workers;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ResultFileWorker {

    private static ConcurrentLinkedQueue<String> _queue;

    private int counter;

    private static boolean _inited = false;

    private static class SingletonInstance {
        private static final ResultFileWorker INSTANCE = new ResultFileWorker();
    }

    public ResultFileWorker() {
    }

    public static ResultFileWorker getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public void init() {
        if (_inited) {
            return;
        }
//        _threadsNumber = threadsNumber;
//        _queue = new LinkedList();
//        _threads = new ThreadPoolManager.PoolWorker[threadsNumber];
//
//        for (int i=0; i<threadsNumber; i++) {
//            _threads[i] = new ThreadPoolManager.PoolWorker();
//            _threads[i].start();
//        }

        _queue = new ConcurrentLinkedQueue<>();
        //processed();

        _inited = true;
    }

//    private void processd() {
//        int tasksQueueSize = ThreadPoolManager.getInstance().getTasksQueueSize();
//        String result = "";
//
//        if (tasksQueueSize > 0) {
//
//            try {
//                Future task = ThreadPoolManager.getInstance().getFutureTaskFromQueue();
//                result = (String) task.get();
//
//            } catch(ExecutionException ee) {
//                ee.printStackTrace();
//            } catch (InterruptedException ie) {
//                ie.printStackTrace();
//            }
//
//        }
//
//        System.err.println(result + " / " + tasksQueueSize);
//
//    }

    public void addRecord(String record) {
        counter++;
        //processd();
        //System.err.println("ADD: " + counter + " /// " + record);
        System.out.println("ADD Record: " + counter + " /// " + record + " of " + _queue.size());

        _queue.add(record);
    }


    private class PoolWorker extends Thread {
        @Override
        public void run() {

            String record;

            while (true) {
                synchronized(_queue) {
                    while (_queue.isEmpty()) {
                        try {
                            _queue.wait();
                        } catch (InterruptedException ignored) {
                        }
                    }

                    System.out.println(_queue.size());

                    record = _queue.poll();
                }

                //record ti file

            }
        }
    }





    public List<Path> getDirectoryFileList(Path pathName) {

//        String pattern = "*.{xml}"; //filemask
//        List<Path> directoryFileList = new ArrayList<>();
//        try {
//            //Path filename = Files.walkFileTree(pathName, new FindFileVisitor(pattern));
//            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(pathName, pattern);
//            for (Path file : directoryStream) {
//                directoryFileList.add(file);
//            }
//
////            Iterator<Path> iterator = directoryFiles.iterator();
////            while(iterator.hasNext()) {
////                Path file = iterator.next();
////                System.out.println(file);
////            }
//        } catch (IOException e) {
//            System.err.println("getServerFileList error");
//        }
//        return directoryFileList;
        return null;
    }

    private class FindFileVisitor extends SimpleFileVisitor<Path> {
        private PathMatcher matcher;

        public FindFileVisitor(String pattern) {
            try {
                matcher = FileSystems.getDefault().getPathMatcher(pattern);
            } catch (IllegalArgumentException iae) {
                System.err.println("Invalid pattern; did you forget to prefix \"glob:\" or \"regex:\"?");
                //ServerStarter.stopAndExit(1);
            }
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes fileAttributes) {
            find(path);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes fileAttributes) {
            find(path);
            return FileVisitResult.CONTINUE;
        }

        private void find(Path path) {
            Path name = path.getFileName();
            if (matcher.matches(name)) {
                System.out.println("Matching file:" + path.getFileName());
            }

        }
    }

}

