package org.tuiasi.cc.dataobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class BookResponse {
    private String ISBN;
    private String title;
    private Set<String> authors;
    private String presignedUrl;
}
