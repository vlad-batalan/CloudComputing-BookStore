package org.tuiasi.cc.providers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.tuiasi.cc.services.DynamoDBService;
import org.tuiasi.cc.services.S3PresignService;
import org.tuiasi.cc.services.SnsService;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sns.SnsClient;

public class ServiceProvider {
    public static DynamoDBService provideDynamoDbService() {
        return new DynamoDBService(provideDynamoDbClient());
    }

    public static S3PresignService provideS3PresignService() {
        return new S3PresignService(provideS3Presigner());
    }

    public static SnsService provideSNSService() {
        return new SnsService(provideSNSClient());
    }

    private static AmazonDynamoDB provideDynamoDbClient() {
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-east-1")
                .build();
    }

    private static S3Presigner provideS3Presigner() {
        return S3Presigner.create();
    }

    private static SnsClient provideSNSClient() {
        return SnsClient.create();
    }
}
