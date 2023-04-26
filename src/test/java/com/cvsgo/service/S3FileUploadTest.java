package com.cvsgo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.amazonaws.services.s3.AmazonS3;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class S3FileUploadTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3FileUploadService fileUploadService;

    @Test
    @DisplayName("이미지 1개 업로드에 성공한다")
    void success_to_upload_one_image() throws Exception {
        MockMultipartFile image1 = new MockMultipartFile("images", "sample_image1.png",
            MediaType.IMAGE_PNG_VALUE, "image 1".getBytes());
        URL url = new URL("https://cvsgo.com");
        given(amazonS3.getUrl(any(), any())).willReturn(url);

        fileUploadService.upload(image1, "test");

        then(amazonS3).should(times(1)).putObject(any());
    }

    @Test
    @DisplayName("이미지 여러 개 업로드에 성공한다")
    void success_to_upload_images() throws Exception {
        MockMultipartFile image1 = new MockMultipartFile("images", "sample_image1.png",
            MediaType.IMAGE_PNG_VALUE, "image 1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "sample_image2.png",
            MediaType.IMAGE_PNG_VALUE, "image 2".getBytes());
        List<MultipartFile> multipartFiles = List.of(image1, image2);
        URL url = new URL("https://cvsgo.com");
        given(amazonS3.getUrl(any(), any())).willReturn(url);

        fileUploadService.upload(multipartFiles, "test");

        then(amazonS3).should(times(multipartFiles.size())).putObject(any());
    }

}
