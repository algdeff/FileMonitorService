package XmlMonitor.Logic;

import XmlMonitor.Logic.db.DatabaseManager;
import XmlMonitor.ServerStarter;
import XmlMonitor.Logic.Workers.FileProcessingThread;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.concurrent.*;

public class FileSystemMonitor {

    private Path _monitoringPath;

    public FileSystemMonitor() {
    }

    public void start() {

        _monitoringPath = ConfigManager.getInstance().getInputFolder();

        int pollingInterval = ConfigManager.getInstance().getDirectoryPollingInterval();

        if (pollingInterval > 0) {

            Thread directoryWalkingThread = new Thread(new DirecroryWalkingThread());
            directoryWalkingThread.start();

            ThreadPoolManager.getInstance().sheduledTask(new Runnable() {
                @Override
                public void run() {
                    System.err.println(pollingInterval + _monitoringPath.toString() + directoryWalkingThread.isAlive());

                    //directoryWalking(monitoringPath);
                }
            }, pollingInterval);
        } else {
            Thread directoryWatcherThread = new Thread(new DirectoryWatcherThread());
            //directoryWatcherThread.setDaemon(true);
            directoryWatcherThread.start();

            Thread directoryWalkingThread = new Thread(new DirecroryWalkingThread());
            directoryWalkingThread.start();
        }

    }

    private boolean isCorrectFile(Path pathname) {
        System.out.println(Files.isSymbolicLink(pathname)
                + " " + Files.isWritable(pathname)
                + " " + Files.isDirectory(pathname));

        if (Files.isSymbolicLink(pathname)
                || !Files.isWritable(pathname)
                || Files.isDirectory(pathname)) return false;

        PathMatcher pathMatcher = FileSystems.getDefault()
                .getPathMatcher("glob:" + ConfigManager
                        .getInstance().getTargetFileTypeGlob());

        return pathMatcher.matches(pathname.getFileName());
    }


    private class DirecroryWalkingThread implements Runnable {

        @Override
        public void run() {
            try {
                directoryWalking();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        private void directoryWalking() throws Exception {

            try {
                //Path filename = Files.walkFileTree(pathName, new FindFileVisitor(pattern));
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(_monitoringPath, ConfigManager
                        .getInstance().getTargetFileTypeGlob());
                for (Path file : directoryStream) {
                    if (Files.isDirectory(file)) continue;
                    ThreadPoolManager.getInstance().executeFutureTask(new FileProcessingThread(file));
                }

            } catch (IOException ioe) {
                System.err.println("directoryWalking: ioe");
            }

        }

    }


    private class DirectoryWatcherThread implements Runnable {

        @Override
        public void run() {
            try {
                startWatcher();
            } catch (Exception e) {
                e.printStackTrace();
                ServerStarter.stopAndExit(1);
            }
        }

        private void startWatcher() throws NullPointerException {
            Path watchDirectory = _monitoringPath;
            WatchService watchService = null;

            try {
                watchService = watchDirectory.getFileSystem().newWatchService();
                watchDirectory.register(watchService,
                        StandardWatchEventKinds.ENTRY_CREATE);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            while (true) {
                WatchKey key = null;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (WatchEvent event : key.pollEvents()) {
                    if (event.context() == null) {
                        System.err.println("ERROR______________________________________");

                        continue;
                    }
                    fileProcessing(Paths.get(watchDirectory.toString() , event.context().toString()));
                }
                key.reset();
            }

        }

        private void fileProcessing(Path filePath) {
            if (!isCorrectFile(filePath)) return;

            System.out.println("Add new file: " + filePath);

//            FileProcessingThread callable = new FileProcessingThread(filePath);
//            FutureTask task = new FutureTask(callable);
//            Thread t = new Thread(task);
//            t.start();
//
//            try {
//                System.out.println(task.isDone());
//                System.out.println(task.get());
//            } catch(ExecutionException ee) {
//                ee.printStackTrace();
//            } catch (InterruptedException ie) {
//                ie.printStackTrace();
//            }

//            String result = "";
//            try {
//                Future task = ThreadPoolManager.getInstance().getFutureTaskFromQueue();
//                result = (String) task.get();
//
//            } catch(ExecutionException ee) {
//                ee.printStackTrace();
//            } catch (InterruptedException ie) {
//                ie.printStackTrace();
//            }
            Future<ArrayList> future = ThreadPoolManager.getInstance().getCompletionFutureTask();
            ArrayList<String> result = new ArrayList<>();
            try {
                result = future.get();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            } catch (ExecutionException ee) {
                ee.printStackTrace();
            }

            for (String entry : result) {
                System.err.println(entry + " /---/ " + filePath);
            }

            //DatabaseManager.getInstance().teste();
            DatabaseManager.getInstance().test2();

            //ThreadPoolManager.getInstance().executeFutureTask(new FileProcessingThread(filePath));
        }

    }

}

