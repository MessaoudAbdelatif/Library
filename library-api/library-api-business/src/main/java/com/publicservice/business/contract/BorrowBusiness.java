package com.publicservice.business.contract;

import com.publicservice.business.exception.BorrowNotFoundException;
import com.publicservice.business.exception.ExtraTimeNotAllowed;
import com.publicservice.entities.Borrow;
import java.text.ParseException;
import java.util.List;

/**
 * use case business fonction findBorrowById. use case business fonction addExtraTime.
 */
public interface BorrowBusiness {

  /**
   * Used to find Borrow entity by his id.
   *
   * @param id is the borrowing id.
   * @return Borrow entity concerned.
   * @throws BorrowNotFoundException handle the risk if borrow not found.
   */
  Borrow findBorrowById(Long id) throws BorrowNotFoundException;

  /**
   * Used to add extra time for borrowing period.
   *
   * @param id is the borrowing id.
   */
  void addExtraTime(Long id, int extraTime)
      throws BorrowNotFoundException, ParseException, ExtraTimeNotAllowed;

  /**
   * When user make new borrow.
   *
   * @param borrow is the new borrow entity.
   * @return new Borrow instance.
   */
  Borrow createBorrow(Borrow borrow, int initialTime);

  /**
   * When user bring back a borrowed book.
   *
   * @param id is the borrow entity id.
   */
  void closeBorrow(Long id);

  List<Borrow> borrowsOverTimeLimite();

}
