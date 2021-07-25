package com.example.demo.controller;

import com.example.demo.repository.FixCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.Map;

@Controller
public class MainController {
    private final FixCaseRepository fixCaseRepository;

    @GetMapping("/")
    public String home() {
        return "controlPage";
    }

    @GetMapping("/delete")
    public String delete(Model model) {
        model.addAttribute("rowsDeleted", fixCaseRepository.deleteAdded());
        return "controlPage";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, Model model) {
        if (!file.isEmpty()) {
            try {
                int rowsAmount = fixCaseRepository.putFileToDB(file);
                model.addAttribute("rowsAmount", rowsAmount);
                return "controlPage";
            } catch (Exception e) {
                e.printStackTrace();
                return "errorPage";
            }
        } else {
            return "errorPage";
        }
    }

    @Autowired
    public MainController(FixCaseRepository fixCaseRepository) {
        this.fixCaseRepository = fixCaseRepository;
    }
}