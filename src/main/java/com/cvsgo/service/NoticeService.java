package com.cvsgo.service;

import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_NOTICE;

import com.cvsgo.dto.notice.ReadNoticeDetailResponseDto;
import com.cvsgo.dto.notice.ReadNoticeResponseDto;
import com.cvsgo.entity.Notice;
import com.cvsgo.entity.NoticeImage;
import com.cvsgo.exception.NotFoundException;
import com.cvsgo.repository.NoticeImageRepository;
import com.cvsgo.repository.NoticeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;

    /**
     * 공지사항 목록을 조회한다.
     *
     * @return 공지사항 목록
     */
    @Transactional(readOnly = true)
    public Page<ReadNoticeResponseDto> readNoticeList(Pageable pageable) {
        return noticeRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map(ReadNoticeResponseDto::from);
    }

    /**
     * 공지사항을 상세 조회한다.
     *
     * @param noticeId 공지사항 ID
     * @return 공지사항 상세 정보
     * @throws NotFoundException 해당하는 아이디를 가진 공지사항이 없는 경우
     */
    @Transactional(readOnly = true)
    public ReadNoticeDetailResponseDto readNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> NOT_FOUND_NOTICE);
        List<String> noticeImages = noticeImageRepository.findByNotice(notice).stream()
            .map(NoticeImage::getImageUrl).toList();

        return ReadNoticeDetailResponseDto.of(notice, noticeImages);
    }

}
