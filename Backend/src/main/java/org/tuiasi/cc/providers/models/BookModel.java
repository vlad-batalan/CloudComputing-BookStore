package org.tuiasi.cc.providers.models;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Builder(setterPrefix = "with")
@DynamoDBTable(tableName = "Books")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookModel implements Serializable {
    private String isbn;
    private String title;
    private Set<String> authors;
    private String imageKey;

    @DynamoDBHashKey(attributeName = "ISBN")
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @DynamoDBAttribute(attributeName = "Authors")
    public Set<String> getAuthors() {
        return authors;
    }
    public void setAuthors(Set<String> authors) {
        this.authors = authors;
    }

    @DynamoDBAttribute(attributeName = "Title")
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDBAttribute(attributeName = "ImageKey")
    public String getImageKey() {
        return imageKey;
    }
    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }
}
