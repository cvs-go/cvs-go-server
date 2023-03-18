package com.cvsgo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileUploadService {

    String upload(MultipartFile file, String dirName) throws IOException;

    List<String> upload(List<MultipartFile> files, String dirName) throws IOException;
}
