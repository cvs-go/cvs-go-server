package com.cvsgo.controller;

import com.cvsgo.dto.SuccessResponse;
import com.cvsgo.service.FileUploadService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final FileUploadService fileUploadService;

    @PostMapping("/{folder}")
    public SuccessResponse<List<String>> uploadImages(@PathVariable String folder,
        @RequestPart List<MultipartFile> images) throws IOException {
        return SuccessResponse.from(fileUploadService.upload(images, folder));
    }

}
