package BookRidgeUtility;

import BookRidgeDAO.UtilitySQL.*;
import BookRidgeDTO.*;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ResourcesUtility {

    Scanner sc = new Scanner(System.in);
    // 멤버
    MemberDTO memberDTO = new MemberDTO();
    MemberSQL memberSQL = new MemberSQL();

    LoanManageSQL loanManageSQL = new LoanManageSQL(); // 대출 관련 SQL 객체

    BookManageSQL bookManageSQL = new BookManageSQL(); // 도서 관련 SQL 객체

    // 관리자
    AdminDTO adminDTO = new AdminDTO();
    AdminSQL adminSQL = new AdminSQL();

    // 알림
    AlertDTO alertDTO = new AlertDTO();
    AlertSQL alertSQL = new AlertSQL();

    // 로그
    ActivityLogDTO log = new ActivityLogDTO();

    // 스캐너 닫기
    public void closeScanner() {
        if (sc != null) {
            sc.close();
        }
    }
}
