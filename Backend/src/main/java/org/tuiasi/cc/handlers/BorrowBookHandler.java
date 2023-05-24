package org.tuiasi.cc.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tuiasi.cc.dataobjects.BorrowRequest;
import org.tuiasi.cc.dataobjects.BorrowResponse;
import org.tuiasi.cc.exceptions.BookNotFoundException;
import org.tuiasi.cc.providers.models.BorrowModel;
import org.tuiasi.cc.providers.ServiceProvider;
import org.tuiasi.cc.services.DynamoDBService;
import org.tuiasi.cc.services.SnsService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BorrowBookHandler implements RequestStreamHandler {
    private static final Logger logger = LogManager.getLogger(GetAllBooksHandler.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    public final DynamoDBService dbService;
    public final SnsService snsService;

    public BorrowBookHandler() {
        dbService = ServiceProvider.provideDynamoDbService();
        snsService = ServiceProvider.provideSNSService();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        String requestString = IOUtils.toString(input, StandardCharsets.UTF_8.name());
        logger.info("BorrowBookHandler: handling request: {}", requestString);

        try {
            BorrowRequest request = mapper.readValue(requestString, BorrowRequest.class);

            // Call DynamoDB to check if the Book exists.
            if (!bookExists(request.getIsbn())) {
                // It does not - not found response.
                logger.info("BorrowBookHandler: book with ISBN {} could not be found.", request.getIsbn());
                throw new BookNotFoundException("404: The provided ISBN could not be found in the store.");
            }

            // It does - save in DynamoDB.
            BorrowResponse response = saveRequestToDynamoDb(request);
            String responseAsString = mapper.writeValueAsString(response);

            // Send email to user through SNS.
            sendEmailToUser(responseAsString, response.getEmail());

            // Provide response.
            output.write(responseAsString.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.throwing(e);
            throw new IllegalArgumentException("Cannot deserialize object.", e);
        }
    }

    private boolean bookExists(String ISBN) {
        return dbService.getBookByISBN(ISBN) != null;
    }

    private BorrowResponse saveRequestToDynamoDb(BorrowRequest request) {
        BorrowModel savedModel = dbService.createBorrow(request);

        // Return response from database.
        return BorrowResponse.builder()
                .withId(savedModel.getId())
                .withISBN(savedModel.getIsbn())
                .withEmail(savedModel.getEmail())
                .withPhoneNumber(savedModel.getPhoneNumber())
                .withBorrowDate(savedModel.getBorrowDate())
                .build();
    }

    private void sendEmailToUser(String message, String emailDestination) {
        // Check if email is subscribed to topic.
        if(!snsService.isEmailSubscribed(emailDestination)) {
            // If not, subscribe with policy.
            snsService.subscribeTopic(emailDestination, true);
        }

        // Publish the message to the email.
        snsService.publishMessageToEmail(message, emailDestination);
    }
}
