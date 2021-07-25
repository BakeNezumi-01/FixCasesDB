package com.example.demo.controller;

import com.example.demo.domain.FixCase;
import com.example.demo.repository.FixCaseRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DataController {
    private final FixCaseRepository fixCaseRepository;

    @GetMapping("/data")
    public String getData(Model model) {
        model.addAttribute("cases", fixCaseRepository.findAll());
        return "dataDisplayPage";
    }

    @GetMapping("/data/top3Reasons")
    public String getTop3Reasons(Model model) {
        model.addAttribute("cases", fixCaseRepository.top3Reasons());
        return "dataDisplayPage";
    }

    @GetMapping("/data/top3Times")
    public String getTop3Times(Model model) {
        model.addAttribute("cases", fixCaseRepository.top3Times());
        return "dataDisplayPage";
    }

    @GetMapping("/data/repeatedReasons")
    public String getRepeatedReasons(Model model) {
        model.addAttribute("cases", fixCaseRepository.repeatedReasons());
        return "dataDisplayPage";
    }

    public DataController(FixCaseRepository fixCaseRepository) {
        this.fixCaseRepository = fixCaseRepository;
    }
}
