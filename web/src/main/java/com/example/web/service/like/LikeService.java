package com.example.web.service.like;

import com.example.db.dao.like.LikeDao;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    private final LikeDao likeDao;

    public LikeService(LikeDao likeDao) {
        this.likeDao = likeDao;
    }


    public void addLike(String userId, Long bookId){
        likeDao.saveBookLike(userId, bookId);
    }

    public void removeLike(String userId, Long bookId){
        likeDao.deleteBookLike(userId, bookId);
    }


}
