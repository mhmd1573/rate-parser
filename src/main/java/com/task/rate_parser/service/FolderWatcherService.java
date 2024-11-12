package com.task.rate_parser.service;

import com.task.rate_parser.model.RateRecord;
import com.task.rate_parser.repository.RateRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;


//The FolderWatcherService listens for new files in a specified folder
// and processes them by choosing the appropriate parser via the RateSheetParserFactory.

@Service
public class FolderWatcherService {

    private static final Logger logger = LoggerFactory.getLogger(FolderWatcherService.class);

    @Autowired
    private RateRecordRepository rateRecordRepository;
    private final RateSheetParserFactory parserFactory;

    @Value("${rate.parser.folder-path}")
    private String folderPath;


    @Autowired
    public FolderWatcherService(RateSheetParserFactory parserFactory) {
        this.parserFactory = parserFactory;
    }

    @PostConstruct
    public void startWatchingFolder() throws IOException {

        if (folderPath == null || folderPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Folder path must be specified in the application properties.");
        }

        logger.info("Starting to watch folder: {}", folderPath);

        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(folderPath);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Folder does not exist: " + folderPath);
        }

        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        new Thread(() -> {
            while (true) {
                try {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            File newFile = path.resolve((Path) event.context()).toFile();
                            logger.info("New file detected: {}", newFile.getName());
                            processFile(newFile);
                        }
                    }
                    key.reset();
                } catch (InterruptedException e) {
                    logger.error("Folder watcher interrupted: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }



    private void processFile(File file) {
        logger.info("Processing file: {}", file.getName());
        RateSheetParser parser = parserFactory.getParser(file);
        if (parser != null) {
            try {
                List<RateRecord> records = parser.parse(file);
                // Save records to the database
                logger.info("Saving {} records to the database", records.size());
                rateRecordRepository.saveAll(records);

                // Move file to "processed" folder
                File processedFolder = new File(file.getParentFile(), "processed");
                if (!processedFolder.exists()) {
                    processedFolder.mkdirs();
                }
                File processedFile = new File(processedFolder, file.getName());
                boolean moved = file.renameTo(processedFile);
                if (moved) {
                    logger.info("Moved file {} to processed folder", file.getName());
                }

            } catch (Exception e) {
                // On error, move file to "failed" folder
                logger.error("Error processing file {}: {}", file.getName(), e.getMessage());
                File failedFolder = new File(file.getParentFile(), "failed");
                if (!failedFolder.exists()) {
                    failedFolder.mkdirs();
                }
                File failedFile = new File(failedFolder, file.getName());
                boolean movedToFailed = file.renameTo(failedFile);
                if (movedToFailed) {
                    logger.info("Moved file {} to failed folder", file.getName());
                } else {
                    logger.warn("Failed to move file {} to failed folder", file.getName());
                }
            }
        } else {
            logger.warn("No parser available for file type: {}", file.getName());
        }
    }

}
