package no.safety.backend.utility.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
    private Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value("$file.storage.root")
    private String rootPathString;

    public String probeContentType(Resource resource) {
        try {
            return Files.probeContentType(resource.getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Handles saving of files to physical locations on the drive.
     * @param file a MultipartFile containing the data for the file to be saved
     * @return a Path to the newly created file
     */
    public Path save(MultipartFile file, String storageKey) {
        try {
            if(file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
                throw new  IllegalArgumentException("File name is empty");
            }
            Path destination = Paths.get(rootPathString).resolve(storageKey);
            if(exists(destination)) {
                throw new FileAlreadyExistsException(storageKey);
            }
            file.transferTo(destination);
            return destination;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Simple method for checking if a file already exists with a given name.
     * @param path a Java Path directing towards a specific file.
     * @return a boolean flag of true or false
     */
    public boolean exists(Path path) {
        return Files.exists(path);
    }

    /**
     * Creates a directory at a given path if it doesn't already exist.
     * @param path a Java Path object
     */
    public void createDirectories(Path path) {
        if(!Files.exists(path)) {
            logger.info("Creating directory {}", path);
            try {
                Files.createDirectories(path);
            }catch (IOException e) {
                logger.error(e.getMessage());
            }

        }
    }

    /**
     * Handles deletion of an item in physical storage
     * @param path a Java Path object
     */
    public void delete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a file from physical storage
     * @param path a Java path to the desired object.
     * @return a Spring Resource object containing the file
     */
    public Resource load(Path path){
        try{
            return new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
}
