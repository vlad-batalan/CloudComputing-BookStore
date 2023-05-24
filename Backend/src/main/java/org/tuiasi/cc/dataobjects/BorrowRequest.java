package org.tuiasi.cc.dataobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class BorrowRequest {
    private String isbn;
    private String email;
    private String phoneNumber;
}
