package org.tuiasi.cc.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import org.tuiasi.cc.dataobjects.BorrowRequest;
import org.tuiasi.cc.providers.models.BookModel;
import org.tuiasi.cc.providers.models.BorrowModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DynamoDBService {
    private static final String BOOK_TABLE_NAME = "Books";
    private static final String BORROW_TABLE_NAME = "Books";
    private final AmazonDynamoDB client;
    private final DynamoDBMapper dbMapper;

    public DynamoDBService(AmazonDynamoDB client) {
        this.client = client;
        this.dbMapper = new DynamoDBMapper(client);
    }

    public List<BookModel> getAllBooks() {
        List<BookModel> bookModelList = new ArrayList<>();
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(BOOK_TABLE_NAME);

        ScanResult result = client.scan(scanRequest);
        for(Map<String, AttributeValue> bookMap : result.getItems()) {
            BookModel model = BookModel.builder()
                    .withIsbn(bookMap.get("ISBN").getS())
                    .withTitle(bookMap.get("Title").getS())
                    .withAuthors(new HashSet<>(bookMap.get("Authors").getSS()))
                    .withImageKey(bookMap.get("ImageKey").getS())
                    .build();
            bookModelList.add(model);
        }
        return bookModelList;
    }

    public BookModel getBookByISBN(String isbn) {
        return dbMapper.load(BookModel.class, isbn);
    }

    public BorrowModel createBorrow(BorrowRequest borrowRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-LL-yyyy");

        BorrowModel model = BorrowModel.builder()
                .withEmail(borrowRequest.getEmail())
                .withPhoneNumber(borrowRequest.getPhoneNumber())
                .withIsbn(borrowRequest.getIsbn())
                .withBorrowDate(LocalDate.now().format(formatter))
                .build();

        // Todo: Handle DynamoDB exceptions.
        dbMapper.save(model);
        return model;
    }
}
