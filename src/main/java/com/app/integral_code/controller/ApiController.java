package com.app.integral_code.controller;

import com.app.integral_code.api.ApiService;
import com.app.integral_code.dto.RequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class ApiController {
    private final ApiService apiService;

    @RequestMapping(value = {"/","/index"})
    public ModelAndView api(ModelAndView mv) {
        RequestDTO req = new RequestDTO(3, true, false);
        var response = apiService.run(req);
        mv.addObject("textFormat", response.getTextFormat());
        mv.addObject("latexFormat", response.getLatexFormat());
        mv.setViewName("index");
        return mv;
    }
}
