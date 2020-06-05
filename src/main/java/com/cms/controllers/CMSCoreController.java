package com.cms.controllers;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.cms.entities.File;
import com.cms.entities.Folder;
import com.cms.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/cms/core")
public class CMSCoreController {

    @Autowired
    private S3Service s3Service;

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public ResponseEntity<String> createFile(@RequestParam MultipartFile file, @RequestParam String folderName) {

        s3Service.uploadFile(folderName, file);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public ResponseEntity<String> getFile(@RequestParam String folderName, @RequestParam String fileName) {

        s3Service.getFile(folderName, fileName);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/file", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteFile(@RequestParam String folderName, @RequestParam String fileName) {

        s3Service.deleteFile(folderName, fileName);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/file", method = RequestMethod.PUT)
    public ResponseEntity<String> updateFile(@RequestBody File file) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/folder", method = RequestMethod.POST)
    public ResponseEntity<String> createFolder(@RequestBody Folder folder) {
        System.out.println("Folder for create: " + folder.getName());
        s3Service.createBucket(folder.getName());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/folder", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getFolder(@RequestBody Folder folder) {

        Bucket foundFolder = s3Service.getFolder(folder.getName());

        if(foundFolder != null)
            return new ResponseEntity<>(foundFolder, HttpStatus.OK);
        else
            return new ResponseEntity<>("Folder not found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/folder/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllFolders() {

        List<Bucket> folders = s3Service.getAllFolders();

        return new ResponseEntity<>(folders, HttpStatus.OK);
    }

    @RequestMapping(value = "/folder", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteFolder(@RequestBody Folder folder) {

        s3Service.deleteBucket(folder.getName());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/folder", method = RequestMethod.PUT)
    public ResponseEntity<String> updateFolder(@RequestBody Folder folder) {

        s3Service.deleteBucket(folder.getName());
        s3Service.createBucket(folder.getUpdateName());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/folder/content", method = RequestMethod.GET)
    public List<S3ObjectSummary> getFolderContent(@RequestParam String folderName) {

        return s3Service.getFolderContent(folderName);
    }
}
