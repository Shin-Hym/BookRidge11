package BookRidgeMain;

import BookRidgeDAO.UtilitySQL.LoanManageSQL;
import BookRidgeDAO.UtilitySQL.WishBookSQL;
import BookRidgeDTO.*;
import BookRidgeUtility.*;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static BookRidgeUtility.ColorUtility.*;

public class AdminMemberMenu {

    Scanner sc = new Scanner(System.in);
    BookManageMenu bookManageMenu = new BookManageMenu();

    // 관리자 페이지 메서드
    protected void adminMenu(AdminDTO loggedInAdmin) {

        while (true) {
            try {
                System.out.println(PURPLE + "\n╔═══════════════════════════ ⊱⋆⊰ ═══════════════════════════╗" + RESET);
                System.out.println("  👥 " +  "[1] 회원 관리" +  "   📚 " +  "[2] 도서 관리" +  "     📝 " +  "[3] 공지사항 관리");
                System.out.println("  🔓 " +   "[4] 문의 관리" + "   📊 " +  "[5] 보고서 생성" +  "    🚪 " +  "[0] 종료");
                System.out.println(PURPLE + "╚═══════════════════════════════════════════════════════════╝" + RESET);
                System.out.print("선택 >> ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        memberManagement(); // 회원 관리
                        break;

                    case 2:
                        bookManageMenu.bookManageMenu(); // 도서 관리
                        break;

                    case 3:
                        noticeManagement(loggedInAdmin); // 공지사항 관리로 이동
                        break;

                    case 4:
                        manageInquiriesMenu(loggedInAdmin);
                        break;

                    case 5:
                        generateReportMenu(); // 보고서 생성 메뉴 호출
                        break;

                    case 0:
                        return; // 로그 아웃

                    default:
                        System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("잘못된 입력입니다. 숫자를 입력해주세요.");
                sc.nextLine(); // 잘못된 입력 버퍼 비우기
            } catch (Exception e) {
                System.out.println("예상치 못한 오류가 발생했습니다: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 회원 관리
    private void memberManagement() {

        AdminUtility adminUtility = new AdminUtility();

        while (true) {
            System.out.println(CYAN + "╔══════════════════════════════════════ ⊱⋆⊰ ══════════════════════════════════════╗" + RESET);
            System.out.println("  👥 " + BLUE + "[1] 전체 회원 조회" + RESET + "       🔍 " + YELLOW + "[2] 회원 검색" + RESET + "       📊 " + GREEN + "[3] 회원 활동 로그 조회" + RESET);
            System.out.println("  ✏️ " + YELLOW + "[4] 회원 정보 수정" + RESET + "       ⚙️ " + GREEN + "[5] 회원 상태 변경" + RESET + "   ❌ " + BLUE + "[6] 회원 탈퇴 처리" + RESET);
            System.out.println("  ➕ " + GREEN + "[7] 신규 회원 등록" + RESET + "       🔙 " + WHITE + "[0] 뒤로" + RESET);
            System.out.println(CYAN + "╚═════════════════════════════════════════════════════════════════════════════════╝" + RESET);

            System.out.print("선택 >> ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    adminUtility.viewAllMembers(); // 전체 회원 조회
                    adminUtility.displayUserCount();
                    break;

                case 2:
                    adminUtility.viewMemberById(); // 특정 회원 조회
                    break;

                case 3:
                    adminUtility.viewMemberActivityLog(); // 회원 활동 로그 조회
                    break;

                case 4:
                    adminUtility.updateMemberInfo(); // 회원 정보 수정 메서드 호출
                    break;

                case 5:
                    adminUtility.changeMemberStatus(); // 회원 상태 변경 메서드 호출
                    break;

                case 6:
                    adminUtility.deleteMemberStatus(); // 회원 탈퇴 처리
                    break;

                case 7:
                    MemberUtility memberUtility = new MemberUtility();
                    memberUtility.MemberJoin(); // 신규 회원 등록
                    break;

                case 0:
                    return;

                default:
                    System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
                    break;
            }
        }
    }

    // 공지사항 관리
    private void noticeManagement(AdminDTO loggedInAdmin) {
        NoticeUtility noticeUtility = new NoticeUtility();
        StringBuilder contentBuilder;

        while (true) {

            System.out.println(PURPLE + "\n╔════════════════════════════════════ ⊱⋆⊰ ════════════════════════════════════╗" + RESET);
            System.out.println(RED + "  📰 [1] 전체 공지사항 조회    " + RESET + BLUE + " 📖 [2] 공지사항 세부 조회   " + RESET + GREEN + " 📢 [3] 공지사항 추가  " + RESET);
            System.out.println(YELLOW + "  ✏️ [4] 공지사항 수정        " + RESET + PURPLE + " 🚫 [5] 공지사항 비활성화    " + RESET + WHITE + " 🔙 [0] 뒤로가기 " + RESET);
            System.out.println(PURPLE + "╚═══════════════════════════════════════════════════════════════════════════════╝" + RESET);
            System.out.print("선택 >> ");
            int choice = sc.nextInt();
            sc.nextLine(); // 선택 후 버퍼 비우기

            switch (choice) {
                case 1:
                    // 전체 공지사항 조회
                    noticeUtility.viewAllNotices();

                    break;
                case 2:
                    noticeUtility.viewNoticeById();
                    break;

                case 3:
                    // 공지사항 추가 부분
                    System.out.println("┌───────────────────────────────────────────────────────────┐");
                    System.out.println("│                      공지사항 입력 폼                       │");
                    System.out.println("└───────────────────────────────────────────────────────────┘");
                    System.out.print("공지사항 제목을 입력하세요: ");
                    String title = sc.nextLine();

                    System.out.println("───────────────────────────────────────────────────────────");
                    System.out.println("공지사항 내용을 입력하세요 (입력 종료: 빈 줄 입력 후 Enter):");
                    System.out.println("───────────────────────────────────────────────────────────");

                    // 공지사항 내용을 입력받기 위한 폼, 빈 줄이 입력되면 입력 종료
                    contentBuilder = new StringBuilder();
                    while (true) {
                        String line = sc.nextLine();
                        if (line.isEmpty()) {
                            break; // 빈 줄을 입력하면 내용 입력 종료
                        }
                        contentBuilder.append(line).append("\n"); // 줄바꿈을 포함해 내용을 추가
                    }
                    String content = contentBuilder.toString();

                    NoticeDTO newNotice = new NoticeDTO();
                    newNotice.setTitle(title);
                    newNotice.setContent(content);
                    newNotice.setAdminNo(loggedInAdmin.getaNo()); // 관리자의 번호를 넣음

                    noticeUtility.addNotice(newNotice); // 공지사항 추가
                    break;

                case 4:
                    // 공지사항 수정
                    System.out.print("수정할 공지사항 ID를 입력하세요: ");
                    int noticeId = sc.nextInt();
                    sc.nextLine(); // 버퍼 비우기

                    // 공지사항 제목 수정
                    System.out.print("새로운 제목을 입력하세요 (수정하지 않으려면 Enter): ");
                    String newTitle = sc.nextLine();

                    // 공지사항 내용 수정
                    System.out.println("───────────────────────────────────────────────────────────");
                    System.out.println("새로운 내용을 입력하세요 (빈 줄 입력 후 Enter로 종료):");
                    System.out.println("───────────────────────────────────────────────────────────");

                    // 내용 입력을 위한 폼, 줄바꿈이 있을 때 공백 2개 추가
                    contentBuilder = new StringBuilder();
                    while (true) {
                        String line = sc.nextLine();
                        if (line.isEmpty()) {
                            break; // 빈 줄을 입력하면 내용 입력 종료
                        }
                        contentBuilder.append(line).append("\n"); // 줄바꿈
                    }
                    String newContent = contentBuilder.toString().trim(); // 불필요한 공백 제거

                    // DTO에 수정할 제목과 내용 설정
                    NoticeDTO updateNotice = new NoticeDTO();
                    updateNotice.setNoticeId(noticeId);

                    // 제목이 비어있지 않으면 수정, 비어있으면 기존 제목 유지
                    if (!newTitle.trim().isEmpty()) {
                        updateNotice.setTitle(newTitle);
                    } else {
                        System.out.println("제목이 수정되지 않았습니다.");
                    }

                    // 내용이 비어있지 않으면 수정
                    if (!newContent.isEmpty()) {
                        updateNotice.setContent(newContent);
                    } else {
                        System.out.println("내용이 수정되지 않았습니다.");
                    }

                    // 공지사항 수정 메서드 호출
                    noticeUtility.updateNotice(updateNotice); // 공지사항 수정
                    System.out.println("공지사항이 성공적으로 수정되었습니다.");
                    break;

                case 5:
                    // 공지사항 비활성화
                    System.out.print("비활성화할 공지사항 ID를 입력하세요: ");
                    int deactivateId = sc.nextInt();

                    noticeUtility.deactivateNotice(deactivateId); // 공지사항 비활성화
                    break;

                case 0:
                    return; // 뒤로가기

                default:
                    System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
                    break;
            }
        }
    }

    // 보고서 생성 메뉴
    private void generateReportMenu() {

        while (true) {
            try {
                LoanManageUtility loanManageUtility = new LoanManageUtility();
                LoanManageSQL loanManageSQL = new LoanManageSQL();

                // 보고서 생성 메뉴 출력
                System.out.println(PURPLE + "\n╔═════════════════════════════ 📊 보고서 생성 메뉴 ═════════════════════════════╗" + RESET);
                System.out.println("  [1] 대출 현황 보고서");
                System.out.println("  [2] 인기 도서 보고서");
                System.out.println("  [3] 연체자 목록 보고서");
                System.out.println("  [0] 뒤로가기");
                System.out.println(PURPLE + "╚═════════════════════════════════════════════════════════════════════════════╝" + RESET);
                System.out.print("선택 >> ");

                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        List<LoanDTO> loanList = loanManageSQL.getLoanStatus(); // SQL 연결
                        loanManageUtility.displayLoanReport(loanList); // 보고서 출력 유틸리티
                        break;

                    case 2:
                        List<BookDTO> popularBooks = loanManageSQL.getAllPopularBooks(); // SQL 연결
                        loanManageUtility.displayBooks("인기 도서 목록", popularBooks); // 보고서 출력 유틸리티
                        break;

                    case 3:
                        List<MemberDTO> overdueMembers = loanManageSQL.getOverdueMembers(); // SQL 연결
                        loanManageUtility.displayOverdueReport(overdueMembers); // 보고서 출력 유틸리티
                        break;

                    case 0:
                        return; // 뒤로가기

                    default:
                        System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("숫자를 입력해주세요.");
                sc.nextLine(); // 입력 버퍼 비우기
            }
        }
    }

    // 문의 관리 메뉴
    private void manageInquiriesMenu(AdminDTO loggedInAdmin) {
        while (true) {
            try {
                WishBookUtility wishBookUtility = new WishBookUtility();
                WishBookSQL wishBookSQL = new WishBookSQL();

                // 문의 관리 메뉴 출력
                System.out.println(PURPLE + "\n╔═════════════════════════════ 📋 문의 관리 메뉴 ═════════════════════════════╗" + RESET);
                System.out.println("  [1] 대기 중인 희망 도서 조회");
                System.out.println("  [2] 희망 도서 상태 변경");
                System.out.println("  [0] 뒤로가기");
                System.out.println(PURPLE + "╚═══════════════════════════════════════════════════════════════════════════╝" + RESET);
                System.out.print("선택 >> ");

                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        // 대기 중인 희망 도서 목록 조회 및 출력
                        List<WishBookDTO> wishBookList = wishBookSQL.getPendingWishBooks(); // 대기 중인 희망 도서 SQL 호출
                        wishBookUtility.displayWishBooks(wishBookList); // 희망 도서 목록 출력 유틸리티
                        break;

                    case 2:
                        // 희망 도서 상태 변경
                        System.out.print("상태를 변경할 희망 도서 ID를 입력하세요 >> ");
                        int wishBookId = sc.nextInt();
                        sc.nextLine(); // 버퍼 비우기

                        System.out.print("변경할 상태를 입력하세요 [대기, 승인, 반려] >> ");
                        String status = sc.nextLine();

                        System.out.print("관리자 코멘트를 입력하세요 >> ");
                        String adminComment = sc.nextLine();

                        // 상태 및 코멘트 업데이트
                        wishBookSQL.updateWishBookStatus(wishBookId, status, adminComment, loggedInAdmin.getaNo());
                        System.out.println("희망 도서 신청 상태가 업데이트되었습니다.");
                        break;

                    case 0:
                        return; // 뒤로가기

                    default:
                        System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("숫자를 입력해주세요.");
                sc.nextLine(); // 입력 버퍼 비우기
            }
        }
    }


}
