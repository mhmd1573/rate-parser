//package com.task.rate_parser.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Value;
//import static org.mockito.Mockito.*;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//
//
//@ExtendWith(MockitoExtension.class)  // Mockito support for JUnit5
//public class FolderWatcherServiceTest {
//
//    @Mock
//    private Path mockDirectoryPath;  // Mocking Path class
//
//    @InjectMocks
//    private FolderWatcherService folderWatcherService;  // Inject the service into test
//
//    @Value("${folder.watch.path}")
//    private String folderWatchPath;
//
//    @BeforeEach
//    public void setUp() {
//        // Initialize mocks
//        folderWatchPath = "C:\\Users\\Mohamad Owaiti\\rate";  // Replace with the actual path
//        mockDirectoryPath = Paths.get(folderWatchPath);
//    }
//
//    @Test
//    public void testStartWatchingFolder() throws IOException {
//        // Mock the behavior of the watch service setup
//        // Assuming the FolderWatcherService has a method 'startWatchingFolder' that initiates the watch service
//        doNothing().when(folderWatcherService).startWatchingFolder(mockDirectoryPath);
//
//        // Call the method
//        folderWatcherService.startWatchingFolder(mockDirectoryPath);
//
//        // Verify that the method was called
//        verify(folderWatcherService, times(1)).startWatchingFolder(mockDirectoryPath);
//    }
//
//    @Test
//    public void testHandleFileChangeEvent() {
//        // Here, test the event handling logic, e.g., handling a file creation, modification, or deletion
//        // You can mock events like a file being created in the directory
//
//        // Assuming FolderWatcherService has a method 'handleFileChange' (You can modify according to your actual logic)
//        doNothing().when(folderWatcherService).handleFileChange(any());
//
//        // Simulate the file change
//        folderWatcherService.handleFileChange(mockDirectoryPath);
//
//        // Verify if the method was invoked correctly
//        verify(folderWatcherService, times(1)).handleFileChange(mockDirectoryPath);
//    }
//
//    @Test
//    public void testFolderNotFound() {
//        // Simulate the scenario where the folder path does not exist
//        Path invalidPath = Paths.get("C:\\invalid\\path");
//
//        // Assuming startWatchingFolder throws an exception when the folder does not exist
//        doThrow(new IllegalArgumentException("Folder does not exist")).when(folderWatcherService).startWatchingFolder(invalidPath);
//
//        // Test that the exception is thrown
//        try {
//            folderWatcherService.startWatchingFolder(invalidPath);
//        } catch (IllegalArgumentException e) {
//            assertEquals("Folder does not exist", e.getMessage());
//        }
//
//        // Verify that the method was called with the invalid path
//        verify(folderWatcherService, times(1)).startWatchingFolder(invalidPath);
//    }
//}
