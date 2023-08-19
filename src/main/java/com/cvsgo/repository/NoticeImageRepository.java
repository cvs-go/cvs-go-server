package com.cvsgo.repository;

import com.cvsgo.entity.Notice;
import com.cvsgo.entity.NoticeImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeImageRepository extends JpaRepository<NoticeImage, Long> {

    List<NoticeImage> findByNotice(Notice notice);
}
