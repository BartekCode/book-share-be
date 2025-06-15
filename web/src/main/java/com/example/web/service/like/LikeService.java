package com.example.web.service.like;

import com.example.core.services.log.LogExecutionTime;
import com.example.db.dao.like.LikeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    private  LikeDao likeDao;

    public LikeService() {
    }

    @Autowired
    public LikeService(LikeDao likeDao) {
        this.likeDao = likeDao;
    }

    @LogExecutionTime
    public void addLike(String userId, Long bookId){
        likeDao.saveBookLike(userId, bookId);
    }

    @LogExecutionTime
    public void removeLike(String userId, Long bookId){
        likeDao.deleteBookLike(userId, bookId);
    }


}
