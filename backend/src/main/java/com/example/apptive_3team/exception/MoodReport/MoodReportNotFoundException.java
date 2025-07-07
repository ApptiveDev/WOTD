package com.example.apptive_3team.exception.MoodReport;

import com.example.apptive_3team.exception.ErrorCode;
import com.example.apptive_3team.exception.WOTDException;
import org.springframework.http.HttpStatus;

public class MoodReportNotFoundException extends WOTDException {

  public MoodReportNotFoundException() {
    super(ErrorCode.ITEM_NOT_FOUND_ERROR, HttpStatus.NOT_FOUND);
  }
}
