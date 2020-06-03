package com.cms.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

@Component
public class S3Service {

    private String accessKey = "AKIAZ4BW3XD5SNUD3YFV";
    private String secretKey = "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";

    public S3Service() {

    }

    public List<Bucket> getAllFolders() {

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withRegion(Regions.DEFAULT_REGION)
                .build();

        List<Bucket> folders = s3.listBuckets();

        return folders;
    }

    public Bucket getFolder(String folderName) {

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withRegion(Regions.DEFAULT_REGION)
                .build();
        Bucket folder = null;

        List<Bucket> buckets = s3.listBuckets();

        for (Bucket b : buckets) {
            if (b.getName().equals(folderName)) {
                folder = b;
            }
        }

        return folder;
    }

    public Bucket createBucket(String folderName) {

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withRegion(Regions.DEFAULT_REGION)
                .build();
        Bucket folder = null;

        if (s3.doesBucketExistV2(folderName)) {
            System.out.format("Bucket %s already exists.\n", folderName);
            folder = getFolder(folderName);
        } else {
            try {
                folder = s3.createBucket(folderName);
            } catch (AmazonS3Exception e) {
                System.err.println(e.getErrorMessage());
            }
        }

        return folder;
    }

    public void deleteBucket(String folderName) {

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withRegion(Regions.DEFAULT_REGION)
                .build();

        ObjectListing object_listing = s3.listObjects(folderName);
        while (true) {
            for (Iterator<?> iterator =
                 object_listing.getObjectSummaries().iterator();
                 iterator.hasNext(); ) {
                S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
                s3.deleteObject(folderName, summary.getKey());
            }

            // more object_listing to retrieve?
            if (object_listing.isTruncated()) {
                object_listing = s3.listNextBatchOfObjects(object_listing);
            } else {
                break;
            }
        }

        s3.deleteBucket(folderName);
    }

    public String uploadFile(String folder, MultipartFile multipartFile) {

        String fileUrl = "https://s3.us-west-2.amazonaws.com";

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withRegion(Regions.DEFAULT_REGION)
                .build();

        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = multipartFile.getName();
            fileUrl += "/" + folder + "/" + fileName;
            s3.putObject(new PutObjectRequest(folder, fileName, file));
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileUrl;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public void deleteFile(String folderName, String fileName) {

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withRegion(Regions.DEFAULT_REGION)
                .build();

        s3.deleteObject(folderName, fileName);
    }

    public void getFile(String folderName, String fileName) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withRegion(Regions.DEFAULT_REGION)
                .build();

        S3Object object = s3.getObject(new GetObjectRequest(folderName, fileName));
        InputStream objectData = object.getObjectContent();
        // Process the objectData stream.
        System.out.println(objectData.toString());
        try {
            objectData.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}