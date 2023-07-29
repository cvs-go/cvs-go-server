package com.cvsgo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.cvsgo.dto.notice.ReadNoticeResponseDto;
import com.cvsgo.entity.Notice;
import com.cvsgo.repository.NoticeRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @InjectMocks
    NoticeService noticeService;

    @Test
    @DisplayName("공지사항 목록을 정상적으로 조회한다")
    void succeed_to_read_notice_list() {
        given(noticeRepository.findAll(any(Sort.class))).willReturn(getNoticeList());

        List<ReadNoticeResponseDto> result = noticeService.readNoticeList();

        assertEquals(getNoticeList().size(), result.size());
        then(noticeRepository).should(times(1)).findAll(any(Sort.class));
    }

    private List<Notice> getNoticeList() {
        Notice notice1 = Notice.builder()
            .id(1L)
            .title("긴급 공지")
            .content("긴급 상황입니다")
            .build();
        Notice notice2 = Notice.builder()
            .id(2L)
            .title("이벤트 공지")
            .content("이벤트 배너를 확인하세요")
            .build();

        return List.of(notice1, notice2);
    }

}
