package org.tuiasi.cc.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Data
@AllArgsConstructor
public class S3PresignService {
    private static final String S3_IMAGE_BUCKET_NAME = "bookstore-images-vlad-batalan";
    private final S3Presigner s3Presigner;

    public String getPresignedUrl(String keyName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(S3_IMAGE_BUCKET_NAME)
                .key(keyName)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

}
