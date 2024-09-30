package BookRidgeUtility;

import BookRidgeDAO.UtilitySQL.BookManageSQL;
import BookRidgeDTO.BookDTO;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

import static BookRidgeUtility.ColorUtility.*;

public class BookManageUtility extends ResourcesUtility {

    private final BookManageSQL bookManageSQL = new BookManageSQL(); // BookManageSQL 객체 선언

    private final BookDTO bookDTO = new BookDTO();  // bookDTO 객체 명확히 초기화

    // 카테고리 선택 메서드
    public String selectCategory() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("카테고리를 선택하세요:");
        System.out.println("┌──────────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("   [1] 전체          [2] 문학         [3] 역사        [4] 일반         [5] 지리관광      ");
        System.out.println("   [6] 사회과학      [7] 종교         [8] 철학        [9] 어학         [10] 예술           ");
        System.out.println("   [11] 기술과학    [12] 순수과학     [13] 총류       [14] 분류 없음                      ");
        System.out.println("└──────────────────────────────────────────────────────────────────────────────────────┘");
        System.out.print("번호를 입력하세요: ");

        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                return "전체";
            case 2:
                return "문학";
            case 3:
                return "역사";
            case 4:
                return "일반";
            case 5:
                return "지리관광";
            case 6:
                return "사회과학";
            case 7:
                return "종교";
            case 8:
                return "철학";
            case 9:
                return "어학";
            case 10:
                return "예술";
            case 11:
                return "기술과학";
            case 12:
                return "순수과학";
            case 13:
                return "총류";
            case 14:
                return "null";
            default:
                System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
                return null;
        }
    }

    // 페이징 처리를 포함한 도서 목록 출력 메서드
    public void displayBooksByCategoryWithPaging(String category) {

        final int pageSize = 10; // 한 페이지에 보여줄 데이터 수
        int totalBooks;
        List<BookDTO> books;

        // 전체 도서 목록 출력 시
        if ("전체".equals(category)) {
            totalBooks = bookManageSQL.getTotalBooksCount(); // 전체 도서 수
            books = bookManageSQL.getBooksByPage(1, pageSize); // 전체 목록 페이징
        } else {
            totalBooks = bookManageSQL.getTotalBooksCountByCategory(category); // 카테고리별 도서 수
            books = bookManageSQL.getBooksByCategoryWithPaging(category, 1, pageSize); // 카테고리 목록 페이징
        }

        int totalPages = (int) Math.ceil((double) totalBooks / pageSize); // 총 페이지 수
        int currentPage = 1;

        while (true) {
            System.out.println("총 도서 수: " + totalBooks + ", 총 페이지 수: " + totalPages);
            System.out.println("현재 페이지: " + currentPage);

            // 현재 페이지에 해당하는 도서 목록 출력
            System.out.println("┌───────────────────────────── 도서 목록 ─────────────────────────────┐");
            for (BookDTO book : books) {
                System.out.printf("도서번호: %s | 도서명: %s | 저자: %s | 출판사: %s | 출판일: %s | 분류: %s | 대출 상태: %s\n",
                        book.getBookNo(), book.getTitle(), book.getAuthor(), book.getPublisher(),
                        book.getPublishDate(), book.getCategoryCode(), book.getStatus());  // STATUS 필드 추가
            }
            System.out.println("└──────────────────────────────────────────────────────────────────────┘");

            // 다음 페이지로 넘어가거나 종료 선택
            System.out.print("[이전: P | 다음: N | 종료: 0] 페이지 이동: ");
            String input = sc.next();

            if (input.equalsIgnoreCase("P") && currentPage > 1) {
                currentPage--; // 이전 페이지로
            } else if (input.equalsIgnoreCase("N") && currentPage < totalPages) {
                currentPage++; // 다음 페이지로
            } else if (input.equals("0")) {
                System.out.println("프로그램을 종료합니다.");
                break; // 종료
            } else {
                System.out.println("잘못된 입력입니다.");
            }

            // 다음 페이지의 도서 목록을 로드
            if ("전체".equals(category)) {
                books = bookManageSQL.getBooksByPage(currentPage, pageSize);
            } else {
                books = bookManageSQL.getBooksByCategoryWithPaging(category, currentPage, pageSize);
            }
        }
    }

    // 전체 도서 목록을 페이징 처리하여 출력하는 메서드
    public void displayAllBooksWithPaging() {
        displayBooksByCategoryWithPaging("전체"); // "전체" 카테고리로 전체 도서 목록 출력
    }

    // 도서 검색 및 페이징 출력 메서드
    public void searchBooks() {

        sc.nextLine();
        System.out.print("검색할 키워드를 입력하세요 (제목, 저자, 출판사, 카테고리) >> ");
        String keyword = sc.nextLine();

        final int pageSize = 10; // 한 페이지에 보여줄 데이터 수
        int totalBooks = bookManageSQL.getTotalSearchResults(keyword); // 검색된 총 도서 수
        int totalPages = (int) Math.ceil((double) totalBooks / pageSize); // 총 페이지 수
        int currentPage = 1;

        while (true) {
            List<BookDTO> books = bookManageSQL.searchBooks(keyword, currentPage, pageSize);

            System.out.println("총 검색 결과 수: " + totalBooks + ", 총 페이지 수: " + totalPages);
            System.out.println("현재 페이지: " + currentPage);

            // 검색 결과 출력
            if (books.isEmpty()) {
                System.out.println(RED + "검색 결과가 없습니다." + RESET);
            } else {
                System.out.println(CYAN + "📚  검색 결과  📚" + RESET);
                System.out.println(CYAN + "┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐" + RESET);
                for (BookDTO book : books) {
                    // 한 줄로 출력
                    System.out.printf(YELLOW + "  도서번호: %s | " + GREEN + "도서명: %s | " + YELLOW + "저자: %s | " + BLUE + "출판사: %s | " + GREEN + "출판일: %s | " + RED + "대출 상태: %s\n" + RESET,
                            book.getBookNo(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getPublishDate(), book.getStatus());
                    System.out.println(CYAN + "  ────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────" + RESET);
                }
            }
            // 다음 페이지로 넘어가거나 종료 선택
            System.out.print("[이전: P | 다음: N | 종료: 0] 페이지 이동: ");
            String input = sc.next();

            if (input.equalsIgnoreCase("P") && currentPage > 1) {
                currentPage--; // 이전 페이지로 이동
            } else if (input.equalsIgnoreCase("N") && currentPage < totalPages) {
                currentPage++; // 다음 페이지로 이동
            } else if (input.equals("0")) {
                System.out.println("검색을 종료합니다.");
                break; // 종료
            } else {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // 도서 추가 메서드
    public void addBook() {

        System.out.print("도서명을 입력하세요 >> ");
        bookDTO.setTitle(sc.nextLine());

        System.out.println();
        System.out.print("저자를 입력하세요 >> ");
        bookDTO.setAuthor(sc.nextLine());


        System.out.print("출판사를 입력하세요 >> ");
        bookDTO.setPublisher(sc.nextLine());

        // 날짜 입력 및 검증
        // 날짜 입력 및 검증
        while (true) {
            System.out.print("출판일을 입력하세요 (YYYY-MM-DD) >> ");
            String publishDateInput = sc.nextLine();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);  // 날짜 형식 엄격하게 검증
                java.util.Date utilDate = sdf.parse(publishDateInput);  // java.util.Date로 파싱
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());  // java.sql.Date로 변환
                bookDTO.setPublishDate(sqlDate);  // 변환된 java.sql.Date를 설정
                break;  // 유효한 날짜일 경우 반복문 종료
            } catch (ParseException e) {
                System.out.println("날짜 형식이 잘못되었습니다. YYYY-MM-DD 형식으로 입력해주세요.");
            }
        }

        System.out.print("카테고리 코드를 입력하세요 >> ");
        bookDTO.setCategoryCode(sc.next());

        bookDTO.setStatus("대출가능");

        // SQL 메서드 호출
        bookManageSQL.addBook(bookDTO);
    }

    // 도서 수정 메서드
    public void updateBook() {

        System.out.print("수정할 도서의 도서번호를 입력하세요");
        System.out.println("예시 >> BA00(번호)");
        System.out.print("번호 >> ");
        String plus = sc.nextLine();  // 도서번호 입력 받기
        String bookNo = "BA00" + plus;

        // 도서번호로 해당 도서가 존재하는지 확인
        if (bookManageSQL.isBookExist(bookNo)) {
            System.out.println("도서번호 " + bookNo + "에 해당하는 도서가 존재합니다.");
        } else {
            System.out.println("도서번호 " + bookNo + "에 해당하는 도서를 찾을 수 없습니다.");
            return;
        }

        bookDTO.setBookNo(bookNo);  // 도서번호 설정

        System.out.println("수정할 항목을 선택하세요:");
        System.out.println("[1] 도서명 [2] 저자 [3] 대출 상태 [4] 출판사 [5] 출판일 [6] 카테고리");
        System.out.print("선택 >> ");
        int select = sc.nextInt();

        sc.nextLine();  // 버퍼 비우기 (nextInt() 후에 남아있는 개행 문자 제거)

        switch (select) {
            case 1:
                System.out.print("새로운 도서명을 입력하세요 >> ");
                String newTitle = sc.nextLine();
                System.out.println(newTitle);
                bookDTO.setTitle(newTitle);  // 도서명 업데이트
                break;
            case 2:
                System.out.print("새로운 저자를 입력하세요 >> ");
                String newAuthor = sc.nextLine();
                bookDTO.setAuthor(newAuthor);  // 저자 업데이트
                break;
            case 3:
                System.out.print("새로운 대출 상태를 입력하세요 (대출가능/대출중) >> ");
                String newStatus = sc.nextLine();
                bookDTO.setStatus(newStatus);  // 대출 상태 업데이트
                break;
            case 4:
                System.out.print("새로운 출판사를 입력하세요 >> ");
                String newPublisher = sc.nextLine();
                bookDTO.setPublisher(newPublisher);  // 출판사 업데이트
                break;
            case 5:
                System.out.print("새로운 출판일을 입력하세요 (YYYY-MM-DD) >> ");
                String publishDateInput = sc.nextLine();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.setLenient(false);  // 날짜 형식 엄격하게 검증
                    java.util.Date utilDate = sdf.parse(publishDateInput);  // java.util.Date로 파싱
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());  // java.sql.Date로 변환
                    bookDTO.setPublishDate(sqlDate);  // 변환된 java.sql.Date 설정
                } catch (Exception e) {
                    System.out.println("날짜 형식이 잘못되었습니다. YYYY-MM-DD 형식으로 입력해주세요.");
                    return;
                }
                break;
            case 6:
                System.out.print("새로운 카테고리 코드를 입력하세요 >> ");
                String newCategory = sc.nextLine();
                bookDTO.setCategoryCode(newCategory);  // 카테고리 업데이트
                break;
            default:
                System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
                return;
        }

        // 수정된 정보를 데이터베이스에 업데이트
        bookManageSQL.updateBook(bookDTO);
    }

    // 도서 삭제 메서드
    public void deleteBook() {

        System.out.print("수정할 도서의 도서번호를 입력하세요");
        System.out.println("예시 >> BA00(번호)");
        System.out.print("번호 >> ");
        String plus = sc.nextLine();  // 도서번호 입력 받기
        String bookNo = "BA00" + plus;

        // 도서번호로 해당 도서가 존재하는지 확인
        if (bookManageSQL.isBookExist(bookNo)) {
            System.out.println("도서번호 " + bookNo + "에 해당하는 도서가 존재합니다.");
        } else {
            System.out.println("도서번호 " + bookNo + "에 해당하는 도서를 찾을 수 없습니다.");
            return;
        }

        // SQL을 통해 도서 삭제
        BookManageSQL bookManageSQL = new BookManageSQL();
        bookManageSQL.deleteBook(bookNo);
    }



}
