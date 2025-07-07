package com.example.apptive_3team.exception.Item;

import com.example.apptive_3team.exception.ErrorCode;
import com.example.apptive_3team.exception.WOTDException;
import org.springframework.http.HttpStatus;

public class UserAlreadyHas20ItemsException extends WOTDException {

    public UserAlreadyHas20ItemsException() {
      super(ErrorCode.USER_ALREADY_HAS_20_ITEMS_ERROR, HttpStatus.BAD_REQUEST);
    }
}
