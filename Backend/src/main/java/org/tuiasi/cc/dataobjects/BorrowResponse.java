package org.tuiasi.cc.dataobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class BorrowResponse {
    private String id;
    private String ISBN;
    private String email;
    private String phoneNumber;
    private String borrowDate;
}
