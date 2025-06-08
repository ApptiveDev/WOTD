package com.example.apptive_3team.exception.Item;

import com.example.apptive_3team.exception.ErrorCode;
import com.example.apptive_3team.exception.WOTDException;
import org.springframework.http.HttpStatus;

public class ItemNotFoundException extends WOTDException {

    public ItemNotFoundException() {
        super(ErrorCode.ITEM_NOT_FOUND_ERROR, HttpStatus.NOT_FOUND);
    }
}
