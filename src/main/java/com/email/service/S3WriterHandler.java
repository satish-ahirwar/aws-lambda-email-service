package com.email.service;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class S3WriterHandler implements RequestHandler<Object, String> {

    private static final String BUCKET_NAME = "lambda-logs-store";

    private final S3Client s3 = S3Client.create();

    @Override
    public String handleRequest(Object input, Context context) {
        try {
            // Create a timestamp
            String timestamp = DateTimeFormatter
                    .ofPattern("yyyy-MM-dd'T'HH-mm-ss'Z'")
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now());

            String fileName = "lambda-run-" + timestamp + ".txt";
            String content = "Lambda executed successfully at " + timestamp + " UTC";

            // Prepare upload
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(fileName)
                    .contentType("text/plain")
                    .build();

            // Upload to S3
            s3.putObject(request,
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                            new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)),
                            content.length()
                    )
            );

            String successMessage = "✅ Uploaded file: " + fileName + " to bucket: " + BUCKET_NAME;
            context.getLogger().log(successMessage);
            return successMessage;

        } catch (Exception e) {
            String errorMessage = "❌ Error uploading to S3: " + e.getMessage();
            context.getLogger().log(errorMessage);
            e.printStackTrace();
            return errorMessage;
        }
    }
}
