package com.cvsgo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.cvsgo.dto.notice.ReadNoticeDetailResponseDto;
import com.cvsgo.dto.notice.ReadNoticeResponseDto;
import com.cvsgo.entity.Notice;
import com.cvsgo.entity.NoticeImage;
import com.cvsgo.entity.Review;
import com.cvsgo.exception.NotFoundException;
import com.cvsgo.repository.NoticeImageRepository;
import com.cvsgo.repository.NoticeRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private NoticeImageRepository noticeImageRepository;

    @InjectMocks
    NoticeService noticeService;

    @Test
    @DisplayName("공지사항 목록을 정상적으로 조회한다")
    void succeed_to_read_notice_list() {
        Pageable pageable = PageRequest.of(0, 20);
        given(noticeRepository.findAllByOrderByCreatedAtDesc(any())).willReturn(getNoticeList());

        Page<ReadNoticeResponseDto> result = noticeService.readNoticeList(pageable);

        assertEquals(getNoticeList().getTotalElements(), result.getTotalElements());
        then(noticeRepository).should(times(1)).findAllByOrderByCreatedAtDesc(any());
    }

    @Test
    @DisplayName("공지사항을 정상적으로 조회한다")
    void succeed_to_read_notice() {
        given(noticeRepository.findById(any())).willReturn(Optional.of(notice1));
        given(noticeImageRepository.findByNotice(any())).willReturn(List.of(noticeImage));

        ReadNoticeDetailResponseDto result = noticeService.readNotice(1L);

        then(noticeRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("공지사항 조회 시 존재하지 않는 상품이면 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_read_notice_but_notice_does_not_exist() {
        given(noticeRepository.findById(any())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> noticeService.readNotice(100L));

        then(noticeRepository).should(times(1)).findById(any());
    }

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

    NoticeImage noticeImage = NoticeImage.builder()
        .notice(Notice.builder().id(1L).imageUrls(new ArrayList<>()).build())
        .imageUrl("https://어쩌구저쩌구/notice/공지이미지.png")
        .build();

    private Page<Notice> getNoticeList() {
        return new PageImpl<>(List.of(notice1, notice2));
    }

}
