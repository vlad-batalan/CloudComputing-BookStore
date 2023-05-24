package org.tuiasi.cc.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.Map;

@Data
@AllArgsConstructor
public class SnsService {
    private static final String TOPIC_ARN = "arn:aws:sns:us-east-1:124355366438:BookStoreTopicGood";
    private final SnsClient snsClient;

    public String subscribeTopic(String email, Boolean useEmailPolicy) {
        SubscribeRequest request = SubscribeRequest.builder()
                .protocol("email")
                .endpoint(email)
                .returnSubscriptionArn(true)
                .topicArn(TOPIC_ARN)
                .build();

        SubscribeResponse result = snsClient.subscribe(request);
        String subscriptionArn = result.subscriptionArn();

        if (useEmailPolicy) {
            SetSubscriptionAttributesRequest filterMessageRequest = SetSubscriptionAttributesRequest.builder()
                    .subscriptionArn(subscriptionArn)
                    .attributeName("FilterPolicy")
                    .attributeValue("{\"destinationEmail\":[\"" + email + "\"]}")
                    .build();
            SetSubscriptionAttributesResponse filterMessageResponse =
                    snsClient.setSubscriptionAttributes(filterMessageRequest);
        }
        return subscriptionArn;
    }

    public PublishResponse publishMessageToAll(String message) {
        PublishRequest request = PublishRequest.builder()
                .message(message)
                .topicArn(TOPIC_ARN)
                .build();

        return snsClient.publish(request);
    }

    public PublishResponse publishMessageToEmail(String message, String emailDestination) {
        Map<String, MessageAttributeValue> messageAttributes = Map.of("destinationEmail",
                MessageAttributeValue.builder()
                        .stringValue(emailDestination)
                        .dataType("String")
                        .build());

        PublishRequest request = PublishRequest.builder()
                .message(message)
                .topicArn(TOPIC_ARN)
                .messageAttributes(messageAttributes)
                .build();
        

        return snsClient.publish(request);
    }

    public boolean isEmailSubscribed(String email) {
        ListSubscriptionsByTopicRequest request = ListSubscriptionsByTopicRequest.builder()
                .topicArn(TOPIC_ARN)
                .build();
        ListSubscriptionsByTopicResponse response = snsClient.listSubscriptionsByTopic(request);

        for (Subscription subscription : response.subscriptions()) {
            if (subscription.endpoint().equals(email)) {
                return true;
            }
        }
        return false;
    }
}
