package org.tuiasi.cc;

import org.tuiasi.cc.handlers.BorrowBookHandler;
import org.tuiasi.cc.handlers.GetAllBooksHandler;

public class AppMain {
    public static void main(String[] args) {
        GetAllBooksHandler getAllBooksHandler = new GetAllBooksHandler();
        BorrowBookHandler borrowBookHandler = new BorrowBookHandler();
    }
}
