package com.publicservice.v1.controller;

import com.publicservice.business.contract.BorrowBusiness;
import com.publicservice.business.exception.BorrowNotFoundException;
import com.publicservice.business.exception.ExtraTimeNotAllowed;
import com.publicservice.entities.Borrow;
import com.publicservice.v1.configuration.ApplicationPropertiesConfiguration;
import com.publicservice.v1.dto.mapper.BorrowMapper;
import com.publicservice.v1.dto.model.BorrowDto;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("borrows")
public class BorrowController {

  private final BorrowBusiness borrowBusiness;
  private final BorrowMapper borrowMapper;
  private final ApplicationPropertiesConfiguration appProperties;


  public BorrowController(BorrowBusiness borrowBusiness,
      BorrowMapper borrowMapper,
      ApplicationPropertiesConfiguration appProperties) {
    this.borrowBusiness = borrowBusiness;
    this.borrowMapper = borrowMapper;
    this.appProperties = appProperties;
  }

  @GetMapping(value = "/{id}")
  public BorrowDto findBorrowById(@PathVariable Long id) throws BorrowNotFoundException {
    return borrowMapper.toBorrowDto(borrowBusiness.findBorrowById(id));
  }

  @PostMapping(value = "/{id}/addExtraTime")
  @ResponseStatus(HttpStatus.OK)
  public void addExtraTime(@PathVariable Long id)
      throws ParseException, BorrowNotFoundException, ExtraTimeNotAllowed {
    borrowBusiness.addExtraTime(id, appProperties.getExtraTime());
  }

  @PostMapping(value = "/ADMIN/newBorrow")
  @ResponseStatus(HttpStatus.CREATED)
  public Borrow createBorrow(BorrowDto newBorrowDto) {
    Borrow borrow = borrowMapper.toBorrow(newBorrowDto);
    return borrowBusiness.createBorrow(borrow, appProperties.getInitialTime());
  }

  @GetMapping(value = "/delay")
  public List<BorrowDto> borrowsOverTimeLimite() {

    return borrowBusiness.borrowsOverTimeLimite()
        .stream()
        .map(borrowMapper::toBorrowDto)
        .collect(Collectors.toList());
  }

}
