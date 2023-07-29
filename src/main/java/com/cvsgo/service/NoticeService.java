package com.cvsgo.service;

import com.cvsgo.dto.notice.ReadNoticeResponseDto;
import com.cvsgo.entity.Notice;
import com.cvsgo.repository.NoticeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 목록을 조회한다.
     *
     * @return 공지사항 목록
     */
    @Transactional(readOnly = true)
    public List<ReadNoticeResponseDto> readNoticeList() {
        List<Notice> notices = noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return notices.stream().map(ReadNoticeResponseDto::from).toList();
    }

}
