package com.example.webcvtemplate.controller;

import com.example.webcvtemplate.entity.Lesson;
import com.example.webcvtemplate.entity.Video;
import com.example.webcvtemplate.service.LessonService;
import com.example.webcvtemplate.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/lesson")
@RequiredArgsConstructor
public class LessonController {
    @Autowired
    private LessonService lessonService;

    @Autowired
    private VideoService videoService;

    @GetMapping
    public String getHomePage(Model model) {
        // Lấy tất cả bài viết sắp xếp theo createdAt giảm dần
        List<Lesson> lessonList = lessonService.getAllLessons();
        model.addAttribute("lessonList", lessonList);
        return "admin/lesson/index";
    }
    @GetMapping("/create")
    public String getCreatePage(Model model) {
        return "admin/lesson/createlesson";
    }

    @GetMapping("/{lessonCode}/detail")
    public String getLessonDetailPage(@PathVariable String lessonCode, Model model) {
        Lesson lesson= lessonService.getLessonByLessonCode(lessonCode);
        if(lesson != null) {
            List<Video> videos = videoService.getVideosByLessonCode(lessonCode);
            model.addAttribute("lesson", lesson);
            model.addAttribute("lessonCode", lessonCode);
            model.addAttribute("videos", videos);
            return "admin/lesson/detaillesson";
        }else{
            return "admin/error";
        }
    }

}
