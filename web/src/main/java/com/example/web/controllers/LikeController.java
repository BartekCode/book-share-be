package com.example.web.controllers;

import com.example.web.service.like.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/like")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/add/{bookId}")
    public void addLike(
            @PathVariable("bookId") Long bookId,
            @RequestParam("userId") String userId
    ) {
        likeService.addLike(userId, bookId);
    }


    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<Void> removeBook(
            @PathVariable("bookId") Long bookId,
            @RequestParam("userId") String userId
    ) {
        likeService.removeLike(userId, bookId);
        return ResponseEntity.noContent().build();
    }
}
