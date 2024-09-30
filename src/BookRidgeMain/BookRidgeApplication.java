package BookRidgeMain;

import BookRidgeDAO.UtilitySQL.AlertSQL;
import BookRidgeDAO.UtilitySQL.ResourcesSQL;
import BookRidgeDAO.UtilitySQL.WishBookSQL;
import BookRidgeDTO.AdminDTO;
import BookRidgeDTO.MemberDTO;
import BookRidgeDTO.WishBookDTO;
import BookRidgeUtility.*;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static BookRidgeUtility.ColorUtility.*;

public class BookRidgeApplication {

    Scanner sc = new Scanner(System.in);

    // 내 정보 시작점
    public void startMain() {

        try {
            ResourcesSQL resourcesSQL = new ResourcesSQL();
            MemberUtility memberUtility = new MemberUtility();
            AdminUtility adminUtility = new AdminUtility(); // AdminUtility 추가
            MemberDTO loggedInUser = null;
            AdminDTO loggedInAdmin = null; // 관리자 로그인 상태를 위한 변수 추가
            boolean status = true;

            // 데이터베이스 연결
            resourcesSQL.connect();

            while (status) {
                try {
                    if (loggedInUser == null) { // 로그인되지 않은 상태
                        System.out.println("\n << Team Six의 도서관리 프로그램 >> ");
                        System.out.println(GREEN_BRIGHT + BOLD + "   ___                     _                _        _     __ _          " + RESET +  "\n"+
                                GREEN + BOLD + "  | _ )    ___     ___    | |__     _ _    (_)    __| |   / _` |   ___   " + RESET +  "\n" +
                                CYAN + BOLD + "  | _ \\   / _ \\   / _ \\   | / /    | '_|   | |   / _` |   \\__, |  / -_)  " + RESET +  "\n" +
                                BLUE + BOLD + "  |___/   \\___/   \\___/   |_\\_\\   _|_|_   _|_|_  \\__,_|   |___/   \\___|  " + RESET +  "\n" +
                                BLUE_BRIGHT + BOLD + "_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"| " + RESET + "\n" +
                                BLUE_BRIGHT + BOLD + "\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-' " + RESET + "\n");
                        slowPrint(CYAN + "╔════════════════════ ⊱⋆⊰ ════════════════════╗" + RESET, 15);
                        slowPrint(YELLOW + "  🔑 [1] 로그인   " + ColorUtility.GREEN + "📝 [2] 회원가입   " + WHITE + "🚪 [0] 종료  " + RESET, 0);
                        slowPrint(CYAN + "╚═════════════════════════════════════════════╝" + RESET, 15);
                        System.out.print("선택 >> ");
                        int menu = sc.nextInt();

                        switch (menu) {
                            case 1:
                                loggedInUser = memberUtility.MemberLogin(); // 로그인 메서드
                                if (loggedInUser == null) {
                                    System.out.println("다시 시도해주세요.");
                                }
                                break;

                            case 2:
                                memberUtility.MemberJoin(); // 회원가입 메서드
                                break;

                            case 3:
                                status = false;
                                System.out.println("프로그램을 종료합니다.");
                                break;

                            case 9:
                                loggedInAdmin = adminUtility.adminLogin(); // 관리자 로그인 메서드
                                if (loggedInAdmin == null) {
                                    System.out.println("관리자 로그인에 실패했습니다. 다시 시도해주세요.");
                                } else {
                                    System.out.println("관리자 로그인에 성공했습니다. 관리자 페이지로 이동합니다.");

                                    AdminMemberMenu adminMemberMenu = new AdminMemberMenu();
                                    adminMemberMenu.adminMenu(loggedInAdmin);
                                }
                                break;

                            default:
                                System.out.println("다시 입력하세요");
                                break;
                        }

                    } else { // 로그인된 상태
                        slowPrint(
                                CYAN + "\n╔═════════════════════════════════ ⊱⋆⊰ ══════════════════════════════════╗" + RESET,
                                15);
                        slowPrint(
                                CYAN + "  🔍 [1] 도서 검색\t\t" + RESET + "📚 [2] 도서 대출 및 예약\t\t" + CYAN + "🏛️ [3] 도서관 안내" + RESET,
                                0);
                        slowPrint(
                                CYAN + RESET + "  📋 [4] 희망 도서 신청\t" + CYAN + "🔥 [5] 이달의 인기 도서\t\t" + RESET + "🛋️ [6] 그룹 스터디룸  " + CYAN,
                                0);
                        slowPrint(
                                CYAN + "  📢 [7] 공지사항\t\t" + RESET + "👤 [8] 내정보\t\t\t\t" + CYAN + "🚪 [0] 로그아웃" + RESET,
                                0);
                        slowPrint(
                                CYAN + "╚════════════════════════════════════════════════════════════════════════╝" + RESET,
                                15);
                        System.out.print(" 선택 >> ");

                        int menu = sc.nextInt();

                        switch (menu) {
                            case 1:
                                // 필요한 기능 구현
                                BookManageUtility bookManageUtility = new BookManageUtility();
                                bookManageUtility.searchBooks();
                                break;

                            case 2:
                                // 필요한 기능 구현
                                loanAndReservationMenu(loggedInUser);
                                break;

                            case 3:
                                LibraryInfo(); // 도서관 설명
                                break;

                            case 4:
                                // 필요한 기능 구현
                                wishBookMenu(loggedInUser);
                                break;

                            case 5:
                                // 이달의 인기 도서
                                popularityMenu();
                                break;

                            case 6:
                                studyRoomMenu(loggedInUser);
                                break;

                            case 7:
                                noticeMenu();
                                break;

                            case 8:
                                myInfoMenu(loggedInUser);
                                break;

                            case 0:
                                memberUtility.MemberLogout(loggedInUser); // 로그아웃 메서드 호출
                                loggedInUser = null; // 로그인된 사용자 정보 초기화
                                break;

                            default:
                                System.out.println("다시 입력하세요");
                                break;
                        }
                    }
                } catch (InputMismatchException e) {
                    System.out.println("잘못된 입력입니다. 숫자를 입력해주세요.");
                    sc.nextLine(); // 잘못된 입력 버퍼 비우기
                } catch (Exception e) {
                    System.out.println("프로그램 실행 중 오류가 발생했습니다: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("프로그램 초기화 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 내 정보 메뉴
    private void myInfoMenu(MemberDTO loggedInUser) {

        MemberUtility memberUtility = new MemberUtility();

        while (true) {
            try {
                // 읽지 않은 알림 개수 가져오기'
                AlertSQL alertSQL = new AlertSQL();
                int unreadAlertCount = alertSQL.getAlertCount(loggedInUser.getmNo());

                System.out.println(CYAN + "\n╔══════════════════════════════════════ ⊱⋆⊰ ═══════════════════════════════════════╗" + RESET);
                System.out.printf("  [1] 내 정보 조회 \uD83D\uDCCB   [2] 정보 수정 ✏\uFE0F   [3] 탈퇴 ❌   [4] 알림 \uD83D\uDCEC(%d)   [0] 뒤로 \uD83D\uDD19  \n", unreadAlertCount);
                System.out.println(CYAN + "╚══════════════════════════════════════════════════════════════════════════════════╝" + RESET);
                System.out.print("선택 >> ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        memberUtility.viewMyInfo(loggedInUser); // 내 정보 조회
                        break;

                    case 2:
                        memberUtility.updateMyInfo(loggedInUser); // 정보 수정
                        break;

                    case 3:
                        if (memberUtility.deleteMyAccount(loggedInUser)) {
                            System.out.println("회원 탈퇴가 완료되었습니다. \n프로그램을 종료합니다.");
                            System.exit(0); // 프로그램 종료
                        }
                        break;
                    case 4:
                        System.out.println("회원님의 알림 목록을 조회합니다...");
                        alertMenu(loggedInUser); // 회원의 알림 조회
                        break;
                    case 0:
                        return; // 뒤로 가기

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

    // 도서 대출 및 예약 메뉴
    private void loanAndReservationMenu(MemberDTO loggedInUser) {

        LoanManageUtility loanManageUtility = new LoanManageUtility(); // 대출 관리 유틸리티

        while (true) {
            try {
                // 메인 메뉴 출력
                System.out.println(CYAN + "╔═══════════════════════════════ ⊱⋆⊰ ═══════════════════════════════╗" + RESET);
                System.out.println(YELLOW + "  📚 [1] 도서 대출   " + GREEN + "🔄 [2] 도서 반납   " + PURPLE + "🔍 [3] 대출 도서 조회    " + RESET);
                System.out.println(RED + "  🔖 [4] 도서 예약   " + BLUE + "❌ [5] 예약 취소   " + CYAN + "📑 [6] 예약 도서 조회  " + RESET + "\uD83D\uDD19 [0] 뒤로");
                System.out.println(CYAN + "╚═══════════════════════════════════════════════════════════════════╝" + RESET);
                System.out.print("선택 >> ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        // 도서 대출 처리
                        loanManageUtility.loanBook(loggedInUser); // 도서 대출 메서드
                        break;

                    case 2:
                        // 도서 반납 처리
                        loanManageUtility.returnBook(loggedInUser); // 도서 반납 메서드
                        break;

                    case 3:
                        loanManageUtility.viewLoanedBooks(loggedInUser); // 대출 도서 조회
                        break;

                    case 4:
                        loanManageUtility.reserveBook(loggedInUser); // 예약하기
                        break;

                    case 5:
                        loanManageUtility.cancelReservationBook(loggedInUser); // 예약하기
                        break;
                    case 6:
                        loanManageUtility.displayReservedBooks(loggedInUser);
                        break;
                    case 0:
                        return; // 뒤로가기

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

    // 알림 메뉴
    private void alertMenu(MemberDTO loggedInUser) {
        AlertUtility alertUtility = new AlertUtility();

        while (true) {
            try {
                slowPrint(CYAN + "┌─────────────────────────────────────────────────────────────┐" + RESET, 10);
                slowPrint(CYAN + "  " + GREEN + "[1] ✉️  전체 메일함   " + YELLOW + "[2] 📥 안 읽은 메일함   " + WHITE + "[0] 🔙 뒤로 ", 10);
                slowPrint(CYAN + "└─────────────────────────────────────────────────────────────┘" + RESET, 10);
                System.out.print("선택 >> ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        // 전체 메일함 조회
                        System.out.println("전체 알림 목록을 조회합니다...");
                        alertUtility.displayAllAlerts(loggedInUser.getmNo());
                        break;

                    case 2:
                        // 안 읽은 메일함 조회
                        System.out.println("안 읽은 알림 목록을 조회합니다...");
                        alertUtility.displayUnreadAlerts(loggedInUser.getmNo());
                        break;

                    case 0:
                        return;  // 뒤로 가기

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

    // 공지사항 메뉴
    private void noticeMenu() {
        NoticeUtility noticeUtility = new NoticeUtility();
        while (true) {
            System.out.println(CYAN + "\n╔═══════════════════════════════ ⊱⋆⊰ ═══════════════════════════════╗" + RESET);
            System.out.println("  📋 " + YELLOW + "[1] 전체 공지사항 조회" + RESET + "   📰 " + GREEN + "[2] 세부 공지사항 조회" + RESET + "   🔙 " + WHITE + "[0] 뒤로" + RESET);
            System.out.println(CYAN + "╚═══════════════════════════════════════════════════════════════════╝" + RESET);
            System.out.print("선택 >> ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    noticeUtility.viewActiveNotices();
                    break;
                case 2:
                    noticeUtility.viewNoticeById();
                    break;
                case 0:
                    return;  // 메뉴 종료
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }


    }

    // 인기도서 조회 메뉴
    private void popularityMenu() {
        LoanManageUtility loanManageUtility = new LoanManageUtility(); // 인기도서 유틸리티 클래스 예시

        while (true) {
            try {
                slowPrint(CYAN + "\n╔════════════════════════════════════════ ⊱⋆⊰ ════════════════════════════════════════╗" + RESET, 10);
                slowPrint(CYAN + "  " + GREEN + "[1] 📚 전체 인기 도서   " + YELLOW + "[2] 👨‍ 남성 인기 도서   " + BLUE + "[3] 👩‍ 여성 인기 도서   " + WHITE + "[0] 🔙 뒤로 ", 10);
                slowPrint(CYAN + "╚═════════════════════════════════════════════════════════════════════════════════════╝" + RESET, 10);
                System.out.print("선택 >> ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        // 전체 인기 도서 조회
                        System.out.println("전체 인기 도서 목록을 조회합니다...");
                        loanManageUtility.allPopularBooks(); // 전체 도서 인기 순서대로 조회
                        break;

                    case 2:
                        // 남성 인기 도서 조회
                        System.out.println("남성 회원 기준 인기 도서 목록을 조회합니다...");
                        loanManageUtility.malePopularBooks(); // 남성 회원이 대출한 도서 중 인기 도서
                        break;

                    case 3:
                        // 여성 인기 도서 조회
                        System.out.println("여성 회원 기준 인기 도서 목록을 조회합니다...");
                        loanManageUtility.femalePopularBooks(); // 여성 회원이 대출한 도서 중 인기 도서
                        break;

                    case 0:
                        return; // 뒤로 가기

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

    // 희망 도서 신청 메뉴
    private void wishBookMenu(MemberDTO loggedInUser) {
        WishBookUtility wishBookUtility = new WishBookUtility(); // 유틸리티 클래스 생성
        WishBookSQL wishBookSQL = new WishBookSQL();

        while (true) {
            try {
                slowPrint(CYAN + "\n╔════════════════════════════ ⊱⋆⊰ ════════════════════════════╗" + RESET, 10);
                slowPrint(CYAN + "  " + GREEN + "[1] 📚 희망 도서 신청   " + YELLOW + "[2] 📖 신청 내역 조회   " + WHITE + "[0] 🔙 뒤로 ", 10);
                slowPrint(CYAN + "╚═════════════════════════════════════════════════════════════╝" + RESET, 10);
                System.out.print("선택 >> ");
                int choice = sc.nextInt();
                sc.nextLine(); // 개행 문자 처리

                switch (choice) {
                    case 1:
                        wishBookUtility.addWishBook(loggedInUser);
                        break;

                    case 2:
                        // 신청 내역 조회
                        List<WishBookDTO> wishBookHistory = wishBookSQL.getWishBookHistory(loggedInUser.getmNo());
                        wishBookUtility.displayWishBookHistory(wishBookHistory);
                        break;

                    case 0:
                        return;  // 뒤로 가기

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

    // 스터디룸 예약
    private void studyRoomMenu(MemberDTO loggedInUser) {
        StudyRoomUtility studyRoomUtility = new StudyRoomUtility();
        while (true) {
            try {
                System.out.println(CYAN + "\n╔════════════════════════════════════ ⊱⋆⊰════════════════════════════════════╗" + RESET);
                System.out.println("  🏢 " + GREEN + "[1] 스터디룸 예약" + RESET + "   📋 " + YELLOW + "[2] 예약 조회" + RESET + "   ❌ " + RED + "[3] 예약 취소" + RESET + "   🔙 " + BLUE + "[0] 뒤로 가기" + RESET);
                System.out.println(CYAN + "╚════════════════════════════════════════════════════════════════════════════╝" + RESET);

                System.out.print("선택 >> ");
                int choice = sc.nextInt();
                sc.nextLine(); // 버퍼 비우기

                switch (choice) {
                    case 1:
                        studyRoomUtility.studyRoomReservationMenu(loggedInUser);
                        break;
                    case 2:
                        studyRoomUtility.viewReservations(loggedInUser);
                        break;
                    case 3:
                        studyRoomUtility.cancelReservation(loggedInUser);
                        break;
                    case 0:
                        return; // 메뉴로 복귀
                    default:
                        System.out.println("잘못된 선택입니다. 다시 선택해주세요.");
                }
            } catch (InputMismatchException e) {
                System.out.println("잘못된 입력입니다. 숫자를 입력해주세요.");
                sc.nextLine(); // 잘못된 입력 버퍼 비우기
            }
        }

    }

    // 도서관 안내
    private void LibraryInfo() {
        System.out.println(CYAN + "\n┌───────────────────── ◈ 온라인 도서관 안내 ◈ ─────────────────────┐" + RESET);

        // 웹사이트 안내
        System.out.println("\n   💻 " + RED + "웹사이트 이용 방법" + RESET + ":");
        System.out.println("       - 공식 웹사이트: " + GREEN + "www.bookridge.com" + RESET);
        System.out.println("       - 로그인 후 원하는 도서를 검색하고 대출 신청 가능");
        System.out.println("       - 전자책 및 오디오북 제공 (24시간 이용 가능)");

        // 대출 절차
        System.out.println("   📚 " + YELLOW + "온라인 도서 대출 절차" + RESET + ":");
        System.out.println("       - 1. 로그인 후 도서 검색");
        System.out.println("       - 2. 도서 선택 후 '대출 신청' 버튼 클릭");
        System.out.println("       - 3. 전자책 또는 오디오북 형식 선택 후 바로 열람 가능");
        System.out.println("       - 4. 반납 기한 내 자동 반납 처리 (별도 사용자 반납 필요함)");

        // 계정 관리
        System.out.println("   🔐 " + GREEN + "계정 관리" + RESET + ":");
        System.out.println("       - 회원가입: 프로그램에서 휴대전화 이메일 입력 후 회원 가입 가능");
        System.out.println("       - 비밀번호 재설정: '내정보' 기능에서 수정 가능");

        // 고객 서비스 안내
        System.out.println("   🛠️ " + CYAN + "고객 서비스" + RESET + ":");
        System.out.println("       - 실시간 채팅 상담: 웹사이트에서 '고객 지원' 메뉴 클릭");
        System.out.println("       - 이메일 문의: support@bookridge.com");
        System.out.println("       - FAQ: 웹사이트 하단 '자주 묻는 질문' 참조");

        // 문의처 안내
        System.out.println("   📞 " + BLUE + "문의처" + RESET + ":");
        System.out.println("       - 전화번호: " + GREEN + "02-1234-5678" + RESET);
        System.out.println("       - 이메일: help@bookridge.com");
        System.out.println(CYAN + "\n└───────────────────────────────────────────────────────────────┘" + RESET);
    }
}
