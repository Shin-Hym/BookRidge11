package BookRidgeUtility;


import BookRidgeDAO.UtilitySQL.NoticeSQL;
import BookRidgeDTO.AdminDTO;
import BookRidgeDTO.NoticeDTO;

import java.text.SimpleDateFormat;
import java.util.List;

import static BookRidgeUtility.ColorUtility.*;

public class NoticeUtility extends ResourcesUtility {

    NoticeSQL noticeSQL = new NoticeSQL();

    // 공지사항 추가 메서드
    public void addNotice(NoticeDTO noticeDTO) {
        if (noticeSQL.insertNotice(noticeDTO)) {
            System.out.println("공지사항이 성공적으로 추가되었습니다.");
        } else {
            System.out.println("공지사항 추가에 실패했습니다.");
        }
    }

    // 공지사항 수정 메서드
    public void updateNotice(NoticeDTO noticeDTO) {
        if (noticeSQL.updateNotice(noticeDTO)) {
            System.out.println("공지사항이 성공적으로 수정되었습니다.");
        } else {
            System.out.println("공지사항 수정에 실패했습니다.");
        }
    }

    // 공지사항 삭제 메서드 (활성화 여부만 변경)
    public void deactivateNotice(int noticeId) {
        if (noticeSQL.deactivateNotice(noticeId)) {
            System.out.println("공지사항이 비활성화되었습니다.");
        } else {
            System.out.println("공지사항 비활성화에 실패했습니다.");
        }
    }

    // 공지사항 조회 메서드
    public void viewAllNotices() {
        List<NoticeDTO> notices = noticeSQL.getAllNotices();
        if (notices.isEmpty()) {
            System.out.println("공지사항이 없습니다.");
        } else {
            for (NoticeDTO notice : notices) {
                System.out.printf("ID: %d, 제목: %s, 생성일: %s, 활성화 여부: %s\n",
                        notice.getNoticeId(), notice.getTitle(), notice.getCreatedAt(), notice.getIsActive());
            }
        }
    }

    // 활성화된 공지사항만 조회하는 메서드
    public void viewActiveNotices() {
        List<NoticeDTO> notices = noticeSQL.getAllNotices();
        if (notices.isEmpty()) {
            System.out.println(RED + "📢 공지사항이 없습니다." + RESET);
        } else {
            boolean foundActiveNotices = false;
            System.out.println(CYAN + "┌─────────────────────────────── 📋 활성 공지사항 목록 ───────────────────────────────┐" + RESET);
            for (NoticeDTO notice : notices) {
                if ("Y".equals(notice.getIsActive())) { // 활성화 여부가 'Y'인 공지사항만 출력
                    foundActiveNotices = true;
                    System.out.printf(YELLOW + "📌 ID: %d  " + GREEN + "| 제목: %s  " + BLUE + "| 생성일: %s\n" + RESET,
                            notice.getNoticeId(), notice.getTitle(), notice.getCreatedAt());
                }
            }
            System.out.println(CYAN + "└───────────────────────────────────────────────────────────────────────────────────┘" + RESET);

            if (!foundActiveNotices) {
                System.out.println(RED + "🔇 활성화된 공지사항이 없습니다." + RESET);
            }
        }
    }


    public void viewNoticeById() {
        System.out.print(YELLOW + "조회할 공지사항 ID를 입력하세요: " + RESET);
        int noticeId = sc.nextInt(); // 사용자로부터 공지사항 ID 입력 받기
        sc.nextLine(); // 개행 문자 처리

        NoticeDTO notice = noticeSQL.getNoticeById(noticeId); // NoticeSQL 클래스를 통해 공지사항 조회

        if (notice != null) {
            // 관리자 정보 조회 (관리자 번호로 이름과 직위 가져오기)
            AdminDTO adminDTO = adminSQL.getAdminById(notice.getAdminNo());

            // 날짜 포맷 지정 (소수 초 제외)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String createdAt = dateFormat.format(notice.getCreatedAt());
            String updatedAt = notice.getUpdatedAt() != null ? dateFormat.format(notice.getUpdatedAt()) : "수정일 없음";

            // 조회된 공지사항 출력
            System.out.println(CYAN + "┌──────────────────────────────📋 공지사항 정보───────────────────────────────┐" + RESET);
            System.out.printf(YELLOW + "  제목: " + GREEN + "%-40s\n" + RESET, notice.getTitle());
            System.out.println(CYAN + "├───────────────────────────────────────────────────────────────────────────┤" + RESET);
            System.out.printf("  " + BLUE + "작성자: " + WHITE + "%-20s" + BLUE + "   직위: " + WHITE + "%-15s\n" + RESET, adminDTO.getaNm(), adminDTO.getaRole());
            System.out.println(CYAN + "─────────────────────────────────────────────────────────────────────────────" + RESET);
            System.out.printf("%-40s\n", notice.getContent());
            System.out.println(CYAN + "─────────────────────────────────────────────────────────────────────────────" + RESET);
            System.out.printf(GREEN + "  생성일: %-35s\n" + RESET, createdAt);
            System.out.printf(YELLOW + "  수정일: %-35s\n" + RESET, updatedAt);
            System.out.println(CYAN + "└───────────────────────────────────────────────────────────────────────────┘" + RESET);
        } else {
            // 해당 ID의 공지사항이 없을 경우
            System.out.println(RED + "해당 ID의 공지사항이 존재하지 않습니다." + RESET);
        }


    }
}
