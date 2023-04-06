package com.cvsgo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.cvsgo.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3FileUploadService implements FileUploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public String upload(MultipartFile file, String dirName) throws IOException {
        String filePath = FileUtils.createFilePath(dirName, file.getOriginalFilename());

        amazonS3.putObject(new PutObjectRequest(bucketName, filePath, file.getInputStream(), null));

        return amazonS3.getUrl(bucketName, filePath).toString();
    }

    @Override
    public List<String> upload(List<MultipartFile> files, String dirName) throws IOException {
        List<String> urls = new ArrayList<>();

        if (files != null) {
            for (MultipartFile file : files) {
                String url = upload(file, dirName);
                urls.add(url);
            }
        }
        return urls;
    }
}
