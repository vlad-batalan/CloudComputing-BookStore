package org.tuiasi.cc.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tuiasi.cc.dataobjects.BookResponse;
import org.tuiasi.cc.providers.models.BookModel;
import org.tuiasi.cc.providers.ServiceProvider;
import org.tuiasi.cc.services.DynamoDBService;
import org.tuiasi.cc.services.S3PresignService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class GetAllBooksHandler implements RequestStreamHandler {
    private static final Logger logger = LogManager.getLogger(GetAllBooksHandler.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private final DynamoDBService dbService;
    private final S3PresignService s3PresignService;

    public GetAllBooksHandler() {
        dbService = ServiceProvider.provideDynamoDbService();
        s3PresignService = ServiceProvider.provideS3PresignService();
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
        logger.info("GetAllBookHandler: handling request.");
        try {
            List<BookResponse> allBooks = dbService.getAllBooks().stream().map(this::mapBookModelToResponse)
                    .collect(Collectors.toList());
            String responseAsString = mapper.writeValueAsString(allBooks);
            logger.info("GetAllBookHandler: Providing all books: " + responseAsString);
            outputStream.write(responseAsString.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            logger.throwing(e);
            throw new RuntimeException("Invalid response mapping.", e);
        } catch (IOException e) {
            logger.throwing(e);
            throw new RuntimeException("Could not write response to output stream.", e);
        } catch (Exception e) {
            throw new InternalError("UNKNOWN exception: " + e.getMessage(), e);
        }
    }

    private BookResponse mapBookModelToResponse(BookModel model) {
        String imagePresignedURL = s3PresignService.getPresignedUrl(model.getImageKey());

        return BookResponse.builder()
                .withISBN(model.getIsbn())
                .withTitle(model.getTitle())
                .withAuthors(model.getAuthors())
                .withPresignedUrl(imagePresignedURL)
                .build();
    }
}
