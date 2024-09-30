package BookRidgeUtility;

import BookRidgeDAO.UtilitySQL.LoanManageSQL;
import BookRidgeDTO.BookDTO;
import BookRidgeDTO.LoanDTO;
import BookRidgeDTO.MemberDTO;
import BookRidgeDTO.ReservationDTO;

import java.sql.Date;
import java.util.List;

import static BookRidgeUtility.ColorUtility.*;
import static java.awt.Color.MAGENTA;

public class LoanManageUtility extends ResourcesUtility {

    // 도서 대출 메서드
    public void loanBook(MemberDTO loggedInUser) {

        // 연체 여부 확인
        if (!canMemberLoan(loggedInUser.getmNo())) {
            System.out.println("대출이 제한되어 있습니다.");
            return;
        }


        // 연체 상태 업데이트
        checkAndUpdateOverdueStatus();

        // 대출 중인 도서 권수 확인
        int currentLoanCount = loanManageSQL.getLoanCountByMember(loggedInUser.getmNo());

        // 대출권수 제한 확인 (3권 초과 시 대출 불가)
        if (currentLoanCount >= 3) {
            System.out.println("현재 대출 중인 도서가 3권 이상입니다. 더 이상 대출할 수 없습니다.");
            return;
        }

        System.out.print("대출할 도서의 도서번호를 입력하세요 (BA00(번호)) >> ");
        String plus = "BA00";
        String Number = sc.nextLine();
        String bookNo = plus + Number;

        // 도서번호가 유효한지 확인
        if (!loanManageSQL.isBookAvailable(bookNo)) {
            System.out.println("해당 도서는 대출이 불가능합니다.");
            return;
        }

        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setmNo(loggedInUser.getmNo()); // 회원 번호 설정
        loanDTO.setBookNo(bookNo); // 도서 번호 설정

        // 대출일 설정 (현재 날짜)
        loanDTO.setLoanDate(new Date(System.currentTimeMillis()));

        // 반납 기한일 설정 (대출일로부터 14일 후)
        long twoWeeksInMillis = 7L * 24 * 60 * 60 * 1000; // 14일을 밀리초로 변환
        loanDTO.setDueDate(new Date(System.currentTimeMillis() + twoWeeksInMillis));

        // 대출 상태 처리
        loanDTO.setIsOverdue("N"); // 대출 시 연체 여부는 "N"

        // SQL을 통해 대출 정보 저장
        if (loanManageSQL.loanBook(loanDTO)) {
            System.out.println("도서 대출이 완료되었습니다. 반납 기한일은 " + loanDTO.getDueDate() + "입니다.");

            // 알림 추가 (대출 완료 알림)

            alertDTO.setMemberNo(loggedInUser.getmNo());
            alertDTO.setAlertType("대출");
            alertDTO.setAlertMessage("도서 " + loanDTO.getBookNo() + " 대출이 완료되었습니다.");

            alertSQL.createAlert(alertDTO);

            // 로그인 성공 시 활동 로그 저장
            log.setMemberNo(loggedInUser.getmNo());
            adminSQL.insertActivityLog(loggedInUser.getmNo(), "도서 대출", bookNo + " 대출 성공");


            // 도서 상태를 '대출중'으로 업데이트
            if (loanManageSQL.updateBookStatusToLoan(bookNo)) {
                System.out.println("도서 상태가 '대출중'으로 업데이트되었습니다.");
            } else {
                System.out.println("도서 상태 업데이트에 실패했습니다.");
            }
        } else {
            System.out.println("도서 대출에 실패했습니다.");
        }
    }

    // 연체 상태 업데이트(날짜)
    public void checkAndUpdateOverdueStatus() {
        // 연체 여부를 업데이트
        if (loanManageSQL.updateOverdueStatus()) {
            System.out.println("연체 상태가 업데이트되었습니다.");
        } else {
            System.out.println("연체 상태 업데이트 중 오류가 발생했습니다.");
        }
    }

    // 도서 반납 메서드
    public void returnBook(MemberDTO loggedInUser) {
        System.out.print("반납할 도서의 도서번호를 입력하세요 (BA00(번호)) >> ");
        String plus = "BA00";
        String Number = sc.nextLine();
        String bookNo = plus + Number;

        // 대출 중인 도서인지 확인
        if (!loanManageSQL.isBookLoanedToMember(bookNo, loggedInUser.getmNo())) {
            System.out.println("해당 도서는 회원님이 대출한 도서가 아닙니다.");
            return;
        }

        // 반납 처리
        LoanDTO loanDTO = loanManageSQL.getLoanInfo(bookNo, loggedInUser.getmNo());
        loanDTO.setReturnDate(new Date(System.currentTimeMillis())); // 반납일 설정

        // 연체 여부 확인
        if (loanDTO.getDueDate().before(loanDTO.getReturnDate())) {
            loanDTO.setIsOverdue("Y"); // 연체일 경우
            System.out.println("해당 도서는 연체되었습니다. 연체일: " +
                    (loanDTO.getReturnDate().getTime() - loanDTO.getDueDate().getTime()) / (1000 * 60 * 60 * 24) + "일");

        } else {
            loanDTO.setIsOverdue("N"); // 연체가 아닌 경우
        }

        // SQL을 통해 반납 정보 업데이트
        if (loanManageSQL.returnBook(loanDTO)) {
            System.out.println("도서 반납이 완료되었습니다.");

            // 알림 추가 (반납 완료 알림)
            alertDTO.setMemberNo(loggedInUser.getmNo());
            alertDTO.setAlertType("반납");
            alertDTO.setAlertMessage("도서 " + bookNo + " 반납이 완료되었습니다.");
            alertSQL.createAlert(alertDTO); // 알림 생성

            // 예약자가 있는 경우 대출로 전환
            if (loanManageSQL.checkAndConvertReservation(bookNo)) {
                System.out.println("예약된 도서가 자동으로 대출되었습니다.");

                // 예약된 회원에게 알림 추가
                ReservationDTO reservationDTO = loanManageSQL.getFirstReservation(bookNo);
                if (reservationDTO != null) {
                    alertDTO.setMemberNo(reservationDTO.getMemberNo());
                    alertDTO.setAlertType("대출");
                    alertDTO.setAlertMessage("도서 " + bookNo + " 예약된 도서가 대출로 전환되었습니다.");
                    alertSQL.createAlert(alertDTO); // 알림 생성
                }
            } else {
                // 도서 상태를 '대출가능'으로 업데이트
                if (loanManageSQL.updateBookStatusToAvailable(bookNo)) {
                    System.out.println("도서 상태가 '대출가능'으로 업데이트되었습니다.");
                } else {
                    System.out.println("도서 상태 업데이트에 실패했습니다.");
                }
            }
        } else {
            System.out.println("도서 반납에 실패했습니다.");
        }
    }

    // 본인이 대출한 도서 목록 조회 메서드
    public void viewLoanedBooks(MemberDTO loggedInUser) {
        List<LoanDTO> loanedBooks = loanManageSQL.getLoanedBooksByMember(loggedInUser.getmNo());

        if (loanedBooks.isEmpty()) {
            System.out.println("현재 대출 중인 도서가 없습니다.");
            return;
        }

        if (loanedBooks.isEmpty()) {
            System.out.println(RED + "대출 중인 도서가 없습니다." + RESET);
        } else {
            System.out.println(CYAN + "┌───────────────────────────────────────────────────────────────────── 📚 대출 중인 도서 목록 📚 ────────────────────────────────────────────────────────────────────────┐" + RESET);
            for (LoanDTO loan : loanedBooks) {
                // 도서 정보 가져오기
                BookDTO book = bookManageSQL.getBookByBookNo(loan.getBookNo());

                // 도서 정보 출력
                if (book != null) {
                    System.out.printf(YELLOW + "  도서번호: %s | " + GREEN + "도서명: %s | " + BLUE + "저자: %s | " + PURPLE + "출판사: %s | " + CYAN + "대출일: %s | " + GREEN + "반납 기한일: %s | " + RED + "연체 여부: %s\n" + RESET,
                            loan.getBookNo(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getPublisher(),
                            loan.getLoanDate(),
                            loan.getDueDate(),
                            loan.getIsOverdue().equals("Y") ? RED + "연체 중" + RESET : GREEN + "정상" + RESET);
                } else {
                    System.out.printf(RED + "도서번호: %s | 도서 정보를 찾을 수 없습니다. | 대출일: %s | 반납 기한일: %s | 연체 여부: %s\n" + RESET,
                            loan.getBookNo(),
                            loan.getLoanDate(),
                            loan.getDueDate(),
                            loan.getIsOverdue().equals("Y") ? RED + "연체 중" + RESET : GREEN + "정상" + RESET);
                }
                System.out.println(CYAN + "────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────" + RESET);
            }
        }
    }

    // 도서 예약하기
    public void reserveBook(MemberDTO loggedInUser) {
        System.out.print("예약할 도서의 도서번호를 입력하세요 (BA00(번호)) >> ");
        String plus = "BA00";
        String Number = sc.nextLine();
        String bookNo = plus + Number;

        // 사용자가 이미 해당 도서를 대출 중인 경우 예약 불가
        if (loanManageSQL.isBookAlreadyLoanedByMember(bookNo, loggedInUser.getmNo())) {
            System.out.println("이미 대출 중인 도서는 예약할 수 없습니다.");
            return;
        }

        // 도서 상태 확인 (대출 중인 경우만 예약 가능)
        String bookStatus = bookManageSQL.getBookStatus(bookNo);

        if ("대출중".equals(bookStatus)) {
            // 예약 처리
            if (!loanManageSQL.isBookAlreadyReserved(bookNo)) {
                ReservationDTO reservationDTO = new ReservationDTO();
                reservationDTO.setMemberNo(loggedInUser.getmNo());
                reservationDTO.setBookNo(bookNo);
                reservationDTO.setReservationDate(new Date(System.currentTimeMillis()));

                // 예약 만료일은 대출 반납 후 일정 기간으로 설정 (예: 7일 후)
                long expirationPeriodInMillis = 7L * 24 * 60 * 60 * 1000;
                reservationDTO.setExpirationDate(new Date(System.currentTimeMillis() + expirationPeriodInMillis));

                alertDTO.setMemberNo(loggedInUser.getmNo());
                alertDTO.setAlertType("예약");
                alertDTO.setAlertMessage("도서 " + bookNo + " 예약이 완료되었습니다.");
                alertSQL.createAlert(alertDTO); // 알림 생성

                if (loanManageSQL.reserveBook(reservationDTO)) {
                    System.out.println("도서 예약이 완료되었습니다.");
                } else {
                    System.out.println("도서 예약에 실패했습니다.");
                }
            } else {
                System.out.println("이미 해당 도서는 예약된 상태입니다.");
            }
        } else if ("대출가능".equals(bookStatus)) {
            System.out.println("해당 도서는 현재 대출이 가능하여 예약할 수 없습니다. 직접 대출을 진행하세요.");
        } else {
            System.out.println("해당 도서는 예약이 불가능한 상태입니다.");
        }
    }

    public void cancelReservationBook(MemberDTO loggedInUser) {
        System.out.print("취소할 예약의 예약번호를 입력하세요: ");
        int reservationNo = sc.nextInt();

        // 예약 취소 메서드 호출
        if (loanManageSQL.cancelReservation(reservationNo, loggedInUser.getmNo())) {
            System.out.println("예약이 성공적으로 취소되었습니다.");
        } else {
            System.out.println("예약 취소에 실패했습니다. 예약번호를 확인해주세요.");
        }

    }

    // 예약 조회 메서드
    public void displayReservedBooks(MemberDTO loggedInUser) {
        LoanManageSQL loanManageSQL = new LoanManageSQL();
        List<ReservationDTO> reservedBooks = loanManageSQL.getReservationsByMember(loggedInUser.getmNo());

        if (reservedBooks.isEmpty()) {
            System.out.println("현재 예약된 도서가 없습니다.");
            return;
        }

        // 예약된 도서 목록 출력
        System.out.println(CYAN + "┌──────────────────────────────── 예약된 도서 목록 ────────────────────────────────┐" + RESET);
        for (ReservationDTO reservation : reservedBooks) {
            System.out.printf(CYAN + "  " + YELLOW + "예약번호: " + RESET + "%s | " +
                            YELLOW + "도서번호: " + RESET + "%s | " +
                            YELLOW + "예약일: " + RESET + "%s | " +
                            YELLOW + "만료일: " + RESET + "%s | " +
                            YELLOW + "상태: " + RESET + "%s " + CYAN + "\n",
                    reservation.getReservationNo(),
                    reservation.getBookNo(),
                    reservation.getReservationDate(),
                    reservation.getExpirationDate(),
                    reservation.getStatus());
        }
        System.out.println(CYAN + "└────────────────────────────────────────────────────────────────────────────────┘" + RESET);

    }

    // 회원의 연체 여부 확인
    public boolean canMemberLoan(int memberNo) {
        int overdueDays = loanManageSQL.calculateOverdueDays(memberNo);

        // 연체일이 남아있는 경우 대출 불가
        if (overdueDays > 0) {
            System.out.println("연체일이 " + overdueDays + "일 남아있습니다. 연체 기간 동안 대출이 제한됩니다.");
            return false;
        }

        return true;
    }

    // 전체 인기 도서 조회
    public void allPopularBooks() {
        List<BookDTO> books = loanManageSQL.getAllPopularBooks();
        displayBooks("전체 인기 도서", books);
    }

    // 남성 인기 도서 조회
    public void malePopularBooks() {
        List<BookDTO> books = loanManageSQL.getMalePopularBooks();
        displayBooks("남성 인기 도서", books);
    }

    // 여성 인기 도서 조회
    public void femalePopularBooks() {
        List<BookDTO> books = loanManageSQL.getFemalePopularBooks();
        displayBooks("여성 인기 도서", books);
    }

    // 공통 메서드: 도서 목록 출력
    public void displayBooks(String title, List<BookDTO> books) {
        System.out.println("\n" + CYAN + "┌───────────────────────────────────────────────────────────┐" + RESET);
        System.out.println(CYAN + "        📚 " + title + " 📚" + RESET);

        if (books.isEmpty()) {
            System.out.println(CYAN + "├───────────────────────────────────────────────────────────┤" + RESET);
            System.out.println(YELLOW + "        📖  인기 도서가 없습니다." + RESET);
        } else {
            System.out.println(CYAN + "├───────────────────────────────────────────────────────────┤" + RESET);
            int rank = 1; // 순위 매기기
            for (BookDTO book : books) {
                System.out.printf("   %d등 %s  |  도서번호: %s\n",
                        rank++, WHITE + book.getTitle() + RESET, GREEN + book.getBookNo() + RESET);
            }
        }
        System.out.println(CYAN + "└───────────────────────────────────────────────────────────┘" + RESET);
    }

    // 대출 현황 보고서 출력
    public void displayLoanReport(List<LoanDTO> loans) {
        System.out.println(CYAN + "대출 현황 보고서" + RESET);
        if (loans.isEmpty()) {
            System.out.println(YELLOW + "대출 현황이 없습니다." + RESET);
        } else {
            System.out.println(CYAN + "──────────────────────────────────────────────────────────────────────────────" + RESET);
            for (LoanDTO loan : loans) {
                System.out.printf("도서번호: %s, 회원번호: %d, 대출일: %s, 반납일: %s\n",
                        loan.getBookNo(), loan.getMemberNo(), loan.getLoanDate(),
                        loan.getReturnDate() != null ? loan.getReturnDate() : "미반납");
            }
            System.out.println(CYAN + "──────────────────────────────────────────────────────────────────────────────" + RESET);
        }
    }

    // 연체자 목록 보고서 출력
    public void displayOverdueReport(List<MemberDTO> overdueMembers) {
        System.out.println(CYAN + "연체자 목록 보고서" + RESET);
        if (overdueMembers.isEmpty()) {
            System.out.println(YELLOW + "연체된 회원이 없습니다." + RESET);
        } else {
            System.out.println(CYAN + "──────────────────────────────────────────────────────────────────────────────" + RESET);
            for (MemberDTO member : overdueMembers) {
                System.out.printf("회원번호: %d, 이름: %s, 전화번호: %s\n",
                        member.getmNo(), member.getmName(), member.getmPhone());
            }
            System.out.println(CYAN + "──────────────────────────────────────────────────────────────────────────────" + RESET);
        }
    }


}