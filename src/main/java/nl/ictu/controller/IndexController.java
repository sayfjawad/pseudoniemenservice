package nl.ictu.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Controller
@RequiredArgsConstructor
public class IndexController {

    public static final String GITPROPERTIES = "gitProperties";

    private final GitProperties gitProperties;

    @ModelAttribute(GITPROPERTIES)
    public GitProperties getGitProperties() {
        return gitProperties;
    }

    @GetMapping("/")
    public String redirectToSwaggerUi(final HttpServletRequest httpServletRequest) {
        return "redirect:webjars/swagger-ui/3.38.0/index.html?url=/v1/openapi.yaml";
    }

}
